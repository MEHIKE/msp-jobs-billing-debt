package ee.telia.msp.billing.jobs.common.config;

import com.caucho.hessian.client.HessianProxyFactory;
import ee.emt.xgate.component.sync.SyncMessageRemote;
//import ee.emt.xgate.service.ebillreply.EBillReplyServiceRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
public class HessianConfig {

  private static final Logger log = LoggerFactory.getLogger(HessianConfig.class);
  //static transient Logger log = LoggerFactory.getLogger("hessian" );

  @Value("${remote.services_bus_provider}")
  private String servicesBusUrl;

  @Value("${hessian.read.timeout}")
  private Long hessianReadTimeout;

  @Value("${remote.services_bus_username}")
  private String servicesBusUser;

  @Value("${remote.services_bus_password}")
  private String servicesBusPassword;

  @SuppressWarnings("unchecked")
  private <T> T getHessianProxy( Class<T> cl, String url ) throws MalformedURLException {
    HessianProxyFactory hessianFactory = new HessianProxyFactory();
    //hessianFactory.setOverloadEnabled( true );
    //hessianFactory.setChunkedPost( false );
    //hessianFactory.setHessian2Reply( true );
    //hessianFactory.setHessian2Request( true );
    hessianFactory.setReadTimeout(hessianReadTimeout);
    hessianFactory.setUser(servicesBusUser);
    hessianFactory.setPassword(servicesBusPassword);
    return (T) hessianFactory.create( cl, url );
  }

  @Bean(name = "bus.syncMessageRemote")
  public SyncMessageRemote getReminderRemote() throws MalformedURLException{
    log.info("sync Message Remote conf");
    return getHessianProxy( SyncMessageRemote.class, servicesBusUrl + "/SyncMessageService" );
  }

  @Bean(name = "bus.syncMessageRemote2")
  public SyncMessageRemote getReminderRemoteBean() throws MalformedURLException{
    log.info("reminder remote conf");
    HessianProxyFactory factory = new HessianProxyFactory();
    factory.setReadTimeout(hessianReadTimeout);
    factory.setUser(servicesBusUser);
    factory.setPassword(servicesBusPassword);
    final SyncMessageRemote basic = (SyncMessageRemote) factory.create(SyncMessageRemote.class, servicesBusUrl + "/SyncMessageService");
    return basic;
  }

}
