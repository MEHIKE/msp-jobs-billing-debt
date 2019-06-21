package ee.telia.msp.billing.jobs;


import ee.telia.msp.billing.jobs.common.health.RunningIndicator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.actuate.system.EmbeddedServerPortFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
@SpringBootApplication
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableTransactionManagement
public class SpringBootMain {

  @Autowired
  Environment environment;

  //@Autowired
  //RunningIndicator shutdownIndicator;

  private static final Logger log = LoggerFactory.getLogger(SpringBootMain.class);

  public static void main(String[] args) {
    SpringApplication billingJobsApp = new SpringApplication(SpringBootMain.class);
    addApplicationInfoListeners(billingJobsApp);
    billingJobsApp.run(args);

    // Job main method calls
    //EBillReplyJob._main(args);
  }

  private static void addApplicationInfoListeners(SpringApplication app) {
    app.addListeners(new ApplicationPidFileWriter());
    app.addListeners(new EmbeddedServerPortFileWriter("application.port"));
  }


/*  @PostConstruct
  public void init(){
    log.info( "======> Active profile = {}", Arrays.toString( environment.getActiveProfiles() ) );
    try{
      Signal.handle( new Signal( "HUP" ), new HupSignalHandler( shutdownIndicator ) );
    }
    catch( Exception e ){
      log.warn( "HUP signal failure!" );
    }
  }

  @Slf4j
  private static class HupSignalHandler implements SignalHandler{
    RunningIndicator runningIndicator;

    HupSignalHandler( RunningIndicator runningIndicator ){
      this.runningIndicator = runningIndicator;
    }

    @Override
    public void handle( Signal signal ){
      log.info( "============ PRE-KILL received ... prepare for shutdown =================" );
      runningIndicator.setAppRunning( false );
    }
  }
*/
}
