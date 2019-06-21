package ee.telia.msp.billing.jobs.common.config;

/**
 * Created by Rynno.Ruul on 02.02.2018.
 */
import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.stereotype.Component;

/**
 * @author ashraf_sarhan
 *
 */
@Component
public class ApplicationProperties {

    private PropertiesConfiguration configuration;

    @PostConstruct
    private void init() {
        try {
            String filePath = PropertiesConstants.PROPERTIES_FILE_PATH;
            System.out.println("Loading the properties file: " + filePath);
            configuration = new PropertiesConfiguration(filePath);
            //Create new FileChangedReloadingStrategy to reload the properties file based on the given time interval
            FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
            fileChangedReloadingStrategy.setRefreshDelay(PropertiesConstants.REFRESH_DELAY);
            configuration.setReloadingStrategy(fileChangedReloadingStrategy);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return (String) configuration.getProperty(key);
    }

    public void setProperty(String key, Object value) {
        configuration.setProperty(key, value);
    }

    public void save() {
        try {
            configuration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

}
