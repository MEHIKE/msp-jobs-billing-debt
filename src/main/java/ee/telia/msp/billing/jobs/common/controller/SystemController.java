package ee.telia.msp.billing.jobs.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public String heartbeat() {
        return healthEndpoint.invoke().getStatus() == Status.UP ? "OK" : "DOWN";
    }

}
