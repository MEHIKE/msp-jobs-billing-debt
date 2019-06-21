package ee.telia.msp.billing.jobs.common.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
@Component("appStatus")
public class RunningIndicator extends AbstractHealthIndicator {

    private final AtomicBoolean appRunning = new AtomicBoolean( true );

    @Override protected void doHealthCheck( Health.Builder builder ) throws Exception {
        if( appRunning.get() ) {
            builder.up();
        }
        else {
            builder.down();
        }
    }

    public void setAppRunning( boolean appRunning ) {
        this.appRunning.set( appRunning );
    }
}
