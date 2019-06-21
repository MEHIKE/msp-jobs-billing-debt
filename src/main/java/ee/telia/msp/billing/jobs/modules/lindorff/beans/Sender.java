package ee.telia.msp.billing.jobs.modules.lindorff.beans;

import ee.emt.xgate.component.incasso.IncassoRemote;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Date;

import ee.emt.xgate.component.incasso.model.DebtStatus;
import ee.emt.xgate.component.incasso.model.Decision;
import ee.emt.xgate.component.incasso.model.Penalty;
import ee.emt.xgate.component.incasso.model.DebtComponents;

import ee.telia.msp.billing.jobs.modules.lindorff.job.LindorffJob;
import ee.telia.msp.billing.jobs.modules.lindorff.model.LindorffVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

@Component
public class Sender{

    @Resource(name = "bus.incassoRemote2")
    private IncassoRemote service;

    @Autowired
    private Mailer mailer;


    //static transient Logger log = Logger.getLogger(Sender.class.getName());
    private static final Logger log = LoggerFactory.getLogger(Sender.class);
    final static Marker LOG = MarkerFactory.getMarker("Lindorff");

    public void sendDataToKreedo(Map map){
        String id        = "";
        int successCount = 0;
        int failureCount = 0;
        String errorString = "";
        String successString = "";
        MDC.put("JobName","Lindorff");

        Set keyValuePairs = map.entrySet();
        Iterator it = keyValuePairs.iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            LindorffVO vo = (LindorffVO)(me.getValue());

            log.info(LOG, "SEND:" + vo);
            id = vo.requestId;
            String status = "";

            try{
                DebtStatus debtStatus = new DebtStatus();

                debtStatus.setRequestId(Integer.parseInt(vo.requestId));                  //requestId;
                debtStatus.setSenderCode(vo.senderCode);                                  //senderCode
                debtStatus.setQueryMode(vo.queryMode);                                    //queryMode
               
                Decision decision = new Decision();
                decision.setDecisionDate(D(vo.decisionDate));                             //decisionDate  (Date)
                decision.setDecisionCode(vo.decisionCode);                                //decisionCode  (String)
                decision.setReasonCode(vo.reasonCode);                                    //reasonCode    (String)
                decision.setReasonComment(vo.reasonComment);                              //reasonComment (Date)

                decision.setDeadline(D(vo.deadline));                                     //deadline      (Date)
                debtStatus.setDecision(decision);
                
                DebtComponents debtComponents = new DebtComponents();
                debtComponents.setCurrency(vo.currency);                                  //currency (String)

                debtComponents.setPrincipalClaim(B(vo.principalClaim));                   //principalClaim (BigDecimal)
                debtComponents.setPetitionerCosts(B(vo.petitionerCosts));                 //petitionerCosts (BigDecimal)
                debtComponents.setRepresentativeCosts(B(vo.representativeCosts));         //representativeCosts (BigDecimal)
                debtComponents.setPetitionerStateFee(B(vo.petitionerStateFee));           //petitionerStateFee (BigDecimal)
                debtComponents.setRepresentativeStateFee(B(vo.representativeStateFee));   //representativeStateFee (BigDecimal)
                
                Penalty penalty = new Penalty();

                penalty.setAmount     (B(vo.amount));                                     //amount (BigDecimal)
                penalty.setDescription(vo.description);                                   //description (String)
                penalty.setBeginning  (D(vo.beginning));                                  //beginning (Date)
                penalty.setEnd        (D(vo.end));                                        //end (Date)
                penalty.setPeriod     (vo.period);                                        //period (String)
                penalty.setRate       (B(vo.rate));                                       //rate (BigDecimal)
                debtComponents.setPenalty(penalty);

                debtStatus.setDebtComponents(debtComponents);

                status = service.updateDebtStatusThroughHessian(debtStatus);
                log.info(LOG, vo.requestId + "=" + status);

                if("SUCCESS".equals(status)){
                    successCount++;
                    successString+= id + ":" + status + "\n";

                }
                else
                    throw new Exception(status);
            }
            catch(Exception e){
                failureCount++;
                errorString+= id + ":" + status + "\n";
                log.error(LOG, e.getLocalizedMessage(), e);
            }
        }
        log.info(LOG, "successCount=" + successCount + ";failureCount=" + failureCount);

        String mailText = "";
        mailText += "\nEdukate kirjete arv:" + successCount;
        mailText += "\nVigaste kirjete arv:" + failureCount;

        if(errorString.length() > 0){
            mailText += "\nVigased ID-d koos pohjustega:\n";
            mailText += errorString;
        }

        mailText += "\nEdukad ID-d:\n";
        mailText += successString;
        mailer.send(mailText);
        MDC.remove("JobName");

    }

    public Date D(String s){
        try{
          //return (Date)(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(s.substring(0, 10) + " " + s.substring(11, 19)));
            return (Date)(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.substring(0, 10) + " " + s.substring(11, 19)));
        }
        catch(Exception e){
            //log.warn("Warning:date michmatch for:" + s);
        }
        return null;
    }
    public BigDecimal B(String s){
        try{
            return new BigDecimal(s);
        }
        catch(Exception e){
            //log.warn("Warning:BigDecimal michmatch for:" + s);
        }
        return null;
    }



    //spring stuff
    public void setKreedoService(IncassoRemote service){
        MDC.put("JobName","Lindorff");
        log.info(LOG, "Injecting service:" + service);
        MDC.remove("JobName");
        this.service = service;
    }
    public void setMailer(Mailer mailer){
        MDC.put("JobName","Lindorff");
        log.info(LOG, "Injecting mailer:" + mailer);
        MDC.remove("JobName");
        this.mailer = mailer;
    }

}
