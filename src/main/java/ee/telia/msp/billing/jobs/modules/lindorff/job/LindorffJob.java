package ee.telia.msp.billing.jobs.modules.lindorff.job;

import ee.telekom.msp.annotation.Job;
import ee.telia.msp.billing.jobs.modules.lindorff.beans.Loader;
import ee.telia.msp.billing.jobs.modules.lindorff.beans.Sender;
import ee.telia.msp.billing.jobs.modules.remindercloseaccounts.ReminderCloseAccounts;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

//import ee.emt.job_lindorff.beans.Loader;
//import ee.emt.job_lindorff.beans.Sender;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

@Component
public class LindorffJob {//extends BaseJob{
    //static transient Logger log = Logger.getLogger(LindorffJob.class.getName());
    private static final Logger log = LoggerFactory.getLogger(LindorffJob.class);
    final static Marker LOG = MarkerFactory.getMarker("Lindorff");

/*    public static void main( String[] args ){
        System.out.println("Started");
        new LindorffJob().localTest();
    }
    public void initLocalParams(){}


    public void localTest(){
        setContext(new ClassPathXmlApplicationContext( new String[]{"applicationContext.xml"} ));
        LindorffJob job = (LindorffJob)getContext().getBean("job");
        JobVO jobVO     = new JobVO();
        jobVO.jobRefNum = "1";
        jobVO.job_name  = "JobLindorff";
        //jobVO.cron      = "0/10 * * * * ?";
        jobVO.cron      = "10 * * * * ?";
        jobVO.is_active = "Y";
        job.initLocalParams();
        job.initJob(jobVO);
        System.out.println("loacal test Started");
    }*/

    @Value("${alert.notification.targets}")
    private String alertNotificationTargets;

    @Value("${lindorff.job.cronstring}")
    private String cronString;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage templateMessage;

    // doJob()
    private CronTrigger trigger;
    private SimpleTriggerContext triggerContext = new SimpleTriggerContext();

    @Autowired
    private Loader loader;

    @Autowired
    private Sender sender;

    @Value("${spring_profile: }")
    private String spring_profile;

    //@PostConstruct
    public void init() {
        MDC.put("JobName","Lindorff");
        log.info(LOG, "Application started. First job execution at " + getNextExecutionTime());
        MDC.remove("JobName");
        doJob();
    }

    // doJob()
    @Job(cronProperty = "lindorff.job.cronstring", clustered = false, concurrent = false,
            name = "JobLindorff", startProperty="lindorff.startProperty")
    public void doJob(){
        MDC.put("JobName","Lindorff");
        //log.info( "================= Lindorff doJob() called ====================");
        log.info(LOG, "===============Lindorff started at " + new Date()+" =====================");

        try{
            // kas existeerib vana fail mis n paeva jooksul saadetud
            File lastFile = loader.getLastFile();

            // otsi current fail
            File currentFile = loader.getCurrentFile();
            log.info(LOG, "Last file:"    + lastFile);
            log.info(LOG, "Today's file:" + currentFile);

            // current fail olemas
            if(currentFile != null){
                // laadi current fail malusse
                Map data = loader.load(currentFile);

                // kui vana fail eksisteerib
                if(lastFile != null)
                    // siis viska current datast need entrid valja, mis on tapselt samad vanas failiga olevad
                    data = loader.removeSimilars(data, lastFile);
                dumpMap(data);
                sender.sendDataToKreedo(data);
            }
//            setFinishMessage("JobLindorff job finished SUCCESSFULLY");
        }
        catch(Exception e){
//            setFinishMessage("JobLindorff job finished FAILURE");
            log.error(LOG, e.getLocalizedMessage(), e);
            handleFailure(e);
        }

        //log.info( "JobLindorff doJob() finished");
        log.info(LOG, "JobLindorff ended at " + new Date());
        log.info(LOG, "JobLindorff next run at " + getNextExecutionTime());
        MDC.remove("JobName");

    }

    private Date getNextExecutionTime() {
        if (trigger == null) {
            trigger = new CronTrigger(cronString);
        }
        triggerContext.update(null, null, new Date());
        return trigger.nextExecutionTime(triggerContext);
    }

    private void handleFailure(Exception e) {
        SimpleMailMessage errorMessage = new SimpleMailMessage();
        //errorMessage.setFrom("job_manager@telia.ee");
        errorMessage.setFrom(from);
        errorMessage.setTo(alertNotificationTargets.split(",\\s?"));
        errorMessage.setSubject("Job Scheduler Error Lindorff -> "+spring_profile);
        errorMessage.setText("Lindorff job failed with exception: " + e.getMessage());
        MDC.put("JobName","Lindorff");
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
        errorMessage.setSubject("Job Scheduler Error Lindorff -> "+spring_profile);
        errorMessage.setText(str);
        MDC.put("JobName","Lindorff");
        log.info(LOG, "Sending error message: " + str);
        MDC.remove("JobName");
        mailSender.send(errorMessage);
    }


    public void dumpMap(Map mp) {
        Set keyValuePairs = mp.entrySet();
        Iterator it = keyValuePairs.iterator();
        MDC.put("JobName","Lindorff");
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            log.info("key=" + me.getKey() + ",value=" + me.getValue());
        }
        MDC.remove("JobName");
    }


}
