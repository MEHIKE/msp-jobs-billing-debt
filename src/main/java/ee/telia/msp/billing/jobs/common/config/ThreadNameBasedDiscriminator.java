package ee.telia.msp.billing.jobs.common.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

/**
 * Created by Rynno.Ruul on 05.11.2017.
 */
public class ThreadNameBasedDiscriminator implements Discriminator<ILoggingEvent> {

  private static final String KEY = "loggerName";

  private boolean started;

  @Override
  public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
    //return Thread.currentThread().getName();
    //return Thread.currentThread().getContextClassLoader().toString();
    if (iLoggingEvent.getLoggerName().startsWith("ee.telia.")) {
      //return iLoggingEvent.getMarker().getName();
      return iLoggingEvent.getLoggerName().substring(iLoggingEvent.getLoggerName().lastIndexOf(".")+1,iLoggingEvent.getLoggerName().length());
      //return iLoggingEvent.getLoggerContextVO().getName();
    }
    else
      return "general";
  }

  @Override
  public String getKey() {
    return KEY;
  }

  public void start() {
    started = true;
  }

  public void stop() {
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
