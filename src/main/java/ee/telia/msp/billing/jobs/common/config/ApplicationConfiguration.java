package ee.telia.msp.billing.jobs.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
//@ConfigurationProperties(prefix = "mailSender")
public class ApplicationConfiguration {

    //static transient Logger log = Logger.getLogger(ApplicationConfiguration.class.getName());
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Value("${mailSender.host: }")
    private String host;

    @Value("${mailSender.port: }")
    private String port;

    @Value("${mailSender.username: }")
    private String username;

    @Value("${mailSender.password: }")
    private String password;

    @Value("${mailSender.from: }")
    private String from;

    @Value("${spring_profile: }")
    private String spring_profile;

    @Bean
    //@ConfigurationProperties(prefix = "mailSender")
    public MailSender mailSender() throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        //mailSender.setHost("telekom-smtp.estpak.ee");
        log.info("username = " + username+"   port="+port+"   url="+host+"  password="+password);
        if (host!=null && !" ".equals(host))
            mailSender.setHost(host);
        if (port!=null && !" ".equals(port))
            mailSender.setPort(Integer.valueOf(port));
        if (username!=null && !" ".equals(username))
            mailSender.setUsername(username);
        if (password!=null && !" ".equals(password))
            mailSender.setPassword(password);
        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom("job_manager@telia.ee");
        if (from!=null && !"".equals(from))
            message.setFrom(from);
        message.setSubject("JobsBillingDebt -> "+spring_profile);
        return message;
    }

}
