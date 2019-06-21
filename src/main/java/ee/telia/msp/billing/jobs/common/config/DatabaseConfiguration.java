package ee.telia.msp.billing.jobs.common.config;

import ee.telekom.msp.jdbc.DefaultDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DatabaseConfiguration{

    @Autowired
    private ApplicationContext applicationContext;

    @Primary
    @Bean(name = "tbcisDataSource")
    @ConfigurationProperties(prefix = "tbcis.datasource")
    public DataSource tbcisDataSource(){
        return DefaultDataSourceBuilder.create().build( DefaultDataSourceBuilder.Type.ORACLE );
    }

    @Bean(name = "tbcisTemplate")
    public JdbcTemplate tbcisTemplate(){
        return new JdbcTemplate( tbcisDataSource() );
    }



    private Resource getResourceByEnvProperty( String environmentProperty ) throws IOException{
        return applicationContext.getResource( "file:" + applicationContext.getEnvironment().getProperty( environmentProperty ) );
    }

}
