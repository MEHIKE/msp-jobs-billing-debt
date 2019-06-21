package ee.telia.msp.billing.jobs.common.config;

/**
 * Created by Rynno.Ruul on 02.02.2018.
 */
public class PropertiesConstants {

    public static final String PROPERTIES_FILE_PATH = System
            .getProperty("user.dir") + "/conf/application.properties";

    public static final int REFRESH_DELAY = 1000;

    public static final String REMINDER_CRON = "reminder_closeaccounts.job.cronstring";
    public static final String LINDORFF_CRON = "lindorff.job.cronstring";
    public static final String REMINDER_START = "reminder_closeaccounts";
    public static final String LINDORFF_START = "lindorff.startProperty";

}
