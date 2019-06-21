package ee.telia.msp.billing.jobs.common.health;

import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MailServerHealthIndicator implements HealthIndicator {

  @Value("${mailSender.host}")
  private String host;

  @Value("${mailSender.port}")
  private int port;

  @Override
    public Health health() {
        //int port = 25;
        try (Socket pingSocket = new Socket(host, port)) {
        } catch (Exception e) {
            return Health.down().withDetail("Reason", "Host (" + host + ":" + port + ") " + e.getMessage()).build();
        }
        return Health.up().build();
    }

}
