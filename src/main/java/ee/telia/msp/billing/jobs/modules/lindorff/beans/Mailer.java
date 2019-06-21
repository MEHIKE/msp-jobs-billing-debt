package ee.telia.msp.billing.jobs.modules.lindorff.beans;

import ee.telia.msp.billing.jobs.modules.lindorff.job.LindorffJob;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class Mailer{
    //static transient Logger log = Logger.getLogger(Mailer.class.getName());
    private static final Logger log = LoggerFactory.getLogger(Mailer.class);
    final static Marker LOG = MarkerFactory.getMarker("Lindorff");

    @Value("${lindorff.mailFrom}")
    private String mailFrom;

    @Value("${lindorff.mailTo}")
    private String mailTo;

    @Value("${lindorff.mailServer}")
    private String mailServer;

    /*
    public void send(String text){
        try{
            String[] array = mailTo.split(";");
            for(int a = 0;  a < array.length; a++){
                SmtpClient smtp = new SmtpClient(mailServer);
                smtp.from(mailFrom);
                smtp.to  (array[a]);
                PrintStream mailMsg = smtp.startMessage();
                mailMsg.println("Subject:Lindorff batch andmed tulemused");
                mailMsg.println();
                mailMsg.println(text);
                smtp.closeServer();
                log.info("Email successfully sended to:" + array[a]);
            }
        }
        catch(Exception e){
            log.error(e,e);
        }
     }
*/

    void send(String message)
    {
        try {
             Properties props = new Properties();
             props.put("mail.smtp.host", mailServer);
    
            Session session = Session.getDefaultInstance(props, null);
            
            Message msg = new MimeMessage(session);
    
            InternetAddress addressFrom = new InternetAddress(mailFrom);
            msg.setFrom(addressFrom);
    
            String[] recipients=mailTo.split(";");
            InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
            for (int i = 0; i < recipients.length; i++)
            {
                addressTo[i] = new InternetAddress(recipients[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);
            
            // Setting the Subject and Content Type
            msg.setSubject("Lindorff batch andmed tulemused");
            msg.setContent(message, "text/plain");
            //log.info("email message="+message);
            Transport.send(msg);
        }
        catch(Exception e){
            MDC.put("JobName","Lindorff");
            log.error(LOG, e.getLocalizedMessage(), e);
            MDC.remove("JobName");
        }
    }

    //spring stuff
    public void setMailFrom(String mailFrom){
        this.mailFrom = mailFrom;
    }
    public void setMailTo(String mailTo){
        this.mailTo = mailTo;
    }
    public void setMailServer(String mailServer){
        this.mailServer = mailServer;
    }

}
