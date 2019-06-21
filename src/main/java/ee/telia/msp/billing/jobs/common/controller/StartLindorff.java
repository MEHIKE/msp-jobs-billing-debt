package ee.telia.msp.billing.jobs.common.controller;

import ee.telia.msp.billing.jobs.modules.lindorff.job.LindorffJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rynno.Ruul on 01.02.2018.
 */
@Component
public class StartLindorff implements Endpoint<List<String>> {

    @Override
    public String getId() {
        return "startLindorff";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Autowired
    private LindorffJob job;

    @Override
    public List<String> invoke() {
        // Custom logic to build the output
        List<String> messages = new ArrayList<String>();
        //JobCloseIniAccounts job = new JobCloseIniAccounts();
        try {
            job.init();
            messages.add("STARTED");
        } catch (Exception e) {
            e.printStackTrace();
            messages.add("NOTOK");}
        return messages;
    }
}
