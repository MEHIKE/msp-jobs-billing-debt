package ee.telia.msp.billing.jobs.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ProcessStatusHealthIndicator implements HealthIndicator{

    @Override
    public Health health(){
        int errorCode = 0;
        if( errorCode != 0 ){
            return Health.down().withDetail( "Error Code", errorCode ).build();
        }
        return Health.up().build();
    }

}
