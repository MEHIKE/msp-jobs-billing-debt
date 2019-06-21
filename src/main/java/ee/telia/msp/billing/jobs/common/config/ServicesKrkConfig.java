package ee.telia.msp.billing.jobs.common.config;

import com.caucho.hessian.client.HessianProxyFactory;
import ee.emt.xgate.component.incasso.IncassoRemote;
import ee.emt.xgate.component.reminder.ReminderRemote;
//import org.apache.log4j.Logger;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import java.net.MalformedURLException;

@Configuration
public class ServicesKrkConfig {

  static transient Logger log = LoggerFactory.getLogger(ServicesKrkConfig.class.getName());

  //@Value("${lindorff.serviceUrl}")
  @Value("${remote.services_krk_process_provider}")
  private String servicesKrkProcessUrl;

  @Value("${hessian.read.timeout}")
  private Long hessianReadTimeout;

  @SuppressWarnings("unchecked")
  private <T> T getHessianProxy( Class<T> cl, String url ) throws MalformedURLException {
    HessianProxyFactory hessianFactory = new HessianProxyFactory();
    hessianFactory.setOverloadEnabled( true );
    //hessianFactory.setChunkedPost( false );
    //hessianFactory.setHessian2Reply( true );
    //hessianFactory.setHessian2Request( true );
    //hessianFactory.setReadTimeout(hessianReadTimeout);

    return (T) hessianFactory.create( cl, url );
  }

  @Bean(name = "bus.incassoRemote")
  public IncassoRemote getIncassoRemote() throws MalformedURLException{
    log.info("incasso service conf");
    return getHessianProxy( IncassoRemote.class, servicesKrkProcessUrl+"/IncassoService" );
  }

  @Bean(name = "bus.incassoRemote2")
  public IncassoRemote getIncassoRemoteBean() throws MalformedURLException{
    log.info("incasso service remote conf");
    HessianProxyFactory factory = new HessianProxyFactory();
    factory.setOverloadEnabled(true);
    final IncassoRemote basic = (IncassoRemote) factory.create(IncassoRemote.class, servicesKrkProcessUrl+"/IncassoService");
    return basic;
  }

  @Bean(name = "bus.reminderRemote")
  public ReminderRemote getReminderRemote() throws MalformedURLException{
    log.info("reminder remote conf");
    return getHessianProxy( ReminderRemote.class, servicesKrkProcessUrl + "/ReminderService" );
  }

  @Bean(name = "bus.reminderRemote2")
  public ReminderRemote getReminderRemoteBean() throws MalformedURLException{
    log.info("reminder remote conf");
    HessianProxyFactory factory = new HessianProxyFactory();
    final ReminderRemote basic = (ReminderRemote) factory.create(ReminderRemote.class, servicesKrkProcessUrl + "/ReminderService");
    return basic;
  }

}
