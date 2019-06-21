package ee.telia.msp.billing.jobs.modules.remindercloseaccounts;

import ee.telekom.msp.annotation.Job;
import ee.telia.msp.billing.jobs.common.config.ApplicationProperties;
import ee.telia.msp.billing.jobs.common.config.PropertiesConstants;
import org.quartz.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ee.emt.xgate.component.reminder.model.StatusChangeResultBo;
import ee.emt.xgate.component.sync.SyncMessageRemote;
import ee.emt.xgate.component.sync.model.SyncVO;
import ee.emt.xgate.component.sync.model.SynchronizeStatusVO;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Reminder close accounts, rewritten from app
 *
 */
@Component
public class ReminderCloseAccounts  { //extends BaseJob{

    //static transient Logger log = Logger.getLogger(ReminderCloseAccounts.class.getName());
    private static final Logger log = LoggerFactory.getLogger(ReminderCloseAccounts.class);
    final static Marker LOG = MarkerFactory.getMarker("ReminderCloseAccounts");

    @Resource(name = "bus.syncMessageRemote2")
    private SyncMessageRemote syncMessage;

    @Value("${alert.notification.targets}")
    private String alertNotificationTargets;

    @Value("${reminder_closeaccounts.job.cronstring}")
    private String cronString;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage templateMessage;

    @Autowired
    private ReminderCloseAccountsImpl reminderImpl;

    @Value("${spring_profile: }")
    private String spring_profile;

    // doJob()
    //private CronTrigger trigger;
    private SimpleTriggerContext triggerContext = new SimpleTriggerContext();

    //@PostConstruct
    public void init() {
        MDC.put("JobName","ReminderCloseAccounts");
        log.info(LOG, "Application started. First job execution at " + getNextExecutionTime());
        log.info("***************"+applicationProperties.getProperty(PropertiesConstants.REMINDER_CRON));
        MDC.remove("JobName");
        doJob();
    }

    private boolean isActiveOnNode = false;

    @Autowired
    private ApplicationProperties applicationProperties;

    //@Autowired
    protected CronTrigger trigger;

    private String lastError = "";
    private Date lastFinishedDate = null;
    private long startTime = 0;
    private long lastTimeTaken = 0;

    @Job(cronProperty = "reminder_closeaccounts.job.cronstring", clustered = false, concurrent = false,
    //@Job(cronProperty = PropertiesConstants.REMINDER_CRON, clustered = false, concurrent = false,
            name = "ReminderCloseAccountsJob", startProperty="reminder_closeaccounts.startProperty")
            //name = "ReminderCloseAccountsJob", startProperty=applicationProperties.REMINDER_START)
    //@Scheduled(cron=applicationProperties.getProperty(PropertiesConstants.REMINDER_CRON))
    public void doJob(){
        MDC.put("JobName","ReminderCloseAccounts");
        log.info(LOG, "Reminder CloseAccounts started at " + new Date());
        log.info("***************"+applicationProperties.getProperty(PropertiesConstants.REMINDER_CRON));
        try{
            //log.info("Reminder CloseAccounts started at " + new Date());

            executeCloseAccountsMode();
            //--setFinishMessage( "Finished successfully" );
        }
        catch( Exception e ){
            log.error(LOG,  "An exception occured in JobReminderCloseAccounts doJob(): ", e );
            handleFailure(e);
            //--setFailure( e );
        }
        //m_log.info( "JobReminderCloseAccounts End" );
        log.info(LOG, "Reminder ClouseAccounts ended at " + new Date());
        log.info(LOG, "Reminder Closeaccounts next run at " + getNextExecutionTime());
        MDC.remove("JobName");

    }

/*    public static void _main( String[] args ){
        System.out.println( "Job Initiated from main()" );
        //ApplicationContext ctx = getApplicationContext();
        //ReminderCloseAccounts job = (ReminderCloseAccounts)ctx.getBean( "job" );
        JobVO jobVO = new JobVO();
        jobVO.job_name = "JobReminderCloseAccounts";
        jobVO.cron = "10/20 * * * * ?";
        jobVO.is_active = "Y";
        //job.setContext( ctx );
        //job.initJob( jobVO );
    }*/

    public void executeCloseAccountsMode() throws Exception{
        SynchronizeStatusVO syncStatusVo = null;
        try{
            if( syncMessage.isProcessRunning( SyncVO.SYNC_PROCESS_REMINDER_CLOSE_ACCO_MODE ) ){
                syncStatusVo = syncMessage.createSynchronizeStatus( SyncVO.SYNC_PROCESS_REMINDER_CLOSE_ACCO_MODE, "AUTO" );
                syncStatusVo.endedBy = "AUTO";
                syncStatusVo.syncResult = SyncVO.SYNC_START_TERMINATED;
                syncMessage.finishSynchronizeStatus( syncStatusVo );
                MDC.put("JobName","ReminderCloseAccounts");
                log.info( LOG, "Another reminder process on close accounts mode seems to be executing, quiting" );
                MDC.remove("JobName");
                return;
            }
            else{
                syncStatusVo = syncMessage.createSynchronizeStatus( SyncVO.SYNC_PROCESS_REMINDER_CLOSE_ACCO_MODE, "AUTO" );
            }
            StatusChangeResultBo result = executeCloseAccountsModeInternal();
            if( result != null ){
                syncStatusVo.startedInstancesCount = result.getChangeCount();
                syncStatusVo.syncResult = result.getErrorCount() == 0 ? SyncVO.SYNC_SUCCESS : SyncVO.SYNC_FAILURE;
            }
            else{
                syncStatusVo.syncResult = SyncVO.SYNC_SUCCESS;
            }
            syncStatusVo.endedBy = "AUTO";
            syncMessage.finishSynchronizeStatus( syncStatusVo );

        /*    syncStatusVo = syncMessage.createSynchronizeStatus( SyncVO.SYNC_PROCESS_REMINDER_CLOSE_ACCO_MODE, "AUTO" );
            syncStatusVo.syncResult = SyncVO.SYNC_SUCCESS;
            syncStatusVo.endedBy = "AUTO";
            syncMessage.finishSynchronizeStatus( syncStatusVo );
*/
        }
        catch( Exception e ){
  /*          syncStatusVo = syncMessage.createSynchronizeStatus( SyncVO.SYNC_PROCESS_REMINDER_CLOSE_ACCO_MODE, "AUTO" );
            syncStatusVo.syncResult = SyncVO.SYNC_SUCCESS;
            syncStatusVo.endedBy = "AUTO";
            syncMessage.finishSynchronizeStatus( syncStatusVo );
*/
            try{
                if( syncStatusVo != null && syncStatusVo.syncResult == null && syncMessage != null ){
                    syncStatusVo.endedBy = "AUTO";
                    syncStatusVo.syncResult = SyncVO.SYNC_FAILURE;
                    syncMessage.finishSynchronizeStatus( syncStatusVo );
                }
            }
            catch( Exception ex ){
                MDC.put("JobName","ReminderCloseAccounts");
                log.error( LOG, "Exception when updating process status record", ex );
                MDC.remove("JobName");
                throw e;
            }
            throw e;
        }
    }

    private StatusChangeResultBo executeCloseAccountsModeInternal() throws Exception{
        MDC.put("JobName","ReminderCloseAccounts");
        log.info( LOG, "xgate_reminder started in closeaccounts mode." );
        StatusChangeResultBo result = null;
        //ReminderCloseAccounts reminder = new ReminderCloseAccounts();
        ///phaze 4 - closing accounts
        log.info( LOG, "closing accounts." );
        result = closeAccounts();
        log.info( LOG, "closed accounts: " + result );
        MDC.remove("JobName");
        return result;
    }

    public StatusChangeResultBo closeAccounts() throws Exception{
        MDC.put("JobName","ReminderCloseAccounts");
        log.info( LOG, "closing contracts" );
        //ReminderCloseAccountsImpl reminder = new ReminderCloseAccountsImpl();
        MDC.remove("JobName");
        return reminderImpl.closeAccountsWithResult();
    }

    private Date getNextExecutionTime() {
        if (trigger == null) {
            trigger = new CronTrigger(cronString);
        }
        triggerContext.update(null, null, new Date());
        //return trigger.getNextFireTime();//.nextExecutionTime(triggerContext);
        return trigger.nextExecutionTime(triggerContext);
    }

    private void handleFailure(Exception e) {
        SimpleMailMessage errorMessage = new SimpleMailMessage();
        //errorMessage.setFrom("job_manager@telia.ee");
        errorMessage.setFrom(from);
        errorMessage.setTo(alertNotificationTargets.split(",\\s?"));
        errorMessage.setSubject("Job Scheduler Error ReminderCloseAccounts -> "+spring_profile);
        errorMessage.setText("ReminderCloseAccounts job failed with exception: " + e.getMessage());
        MDC.put("JobName","ReminderCloseAccounts");
        log.info(LOG, "Sending error message: " + errorMessage);
        MDC.remove("JobName");
        mailSender.send(errorMessage);
    }

    @Value("${mailSender.from: }")
    private String from;

    private void handleFailure(String str) {
        SimpleMailMessage errorMessage = new SimpleMailMessage();
        //errorMessage.setFrom("job_manager@telia.ee");
        errorMessage.setFrom(from);
        errorMessage.setTo(alertNotificationTargets.split(",\\s?"));
        errorMessage.setSubject("Job Scheduler Error ReminderCloseAccounts -> "+spring_profile);
        errorMessage.setText(str);
        MDC.put("JobName","ReminderCloseAccounts");
        log.info(LOG, "Sending error message: " + str);
        MDC.remove("JobName");
        mailSender.send(errorMessage);
    }

}
