package ee.telia.msp.billing.jobs.common.health;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.DataSourceHealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TbcisDbHealthIndicator extends DataSourceHealthIndicator {

    @Autowired
    public TbcisDbHealthIndicator(DataSource tbcisDataSource) {
        super(tbcisDataSource);
    }

}
