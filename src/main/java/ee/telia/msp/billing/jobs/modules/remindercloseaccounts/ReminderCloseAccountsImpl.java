package ee.telia.msp.billing.jobs.modules.remindercloseaccounts;

import java.sql.*;
import java.util.*;

import ee.emt.xgate.businessclientdata.dao.util.DbStringCoder;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ee.emt.xgate.component.paymentgraph.model.Tuple;
import ee.emt.xgate.component.reminder.ReminderRemote;
import ee.emt.xgate.component.reminder.model.StatusChangeResultBo;
//import ee.emt.xgate.core.common.log4j.XGLogger;
//import ee.emt.xgate.core.common.util.DbStringCoder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Contains functionality from ee.emt.xgate.component.reminder.impl.ReminderImpl
 *
 */
@Component
public class ReminderCloseAccountsImpl{

    //static transient Logger log = Logger.getLogger( ReminderCloseAccountsImpl.class.getName() );
    private static final Logger log = LoggerFactory.getLogger(ReminderCloseAccountsImpl.class);
    final static Marker LOG = MarkerFactory.getMarker("ReminderCloseAccounts");


    private static String stringConvertMode = "MODE_PLAIN";

    @Resource(name = "bus.reminderRemote2")
    private ReminderRemote reminder;


    @Resource(name = "tbcisTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * Retrieves the string convert mode.
     */
    public static String getStringConvertMode(){
        return stringConvertMode;
    }


    /**
     * Looks for contracts to close, and closes them
     * 
     * @return
     */
    public StatusChangeResultBo closeAccountsWithResult(){
        try{
            MDC.put("JobName","ReminderCloseAccounts");
            log.debug(LOG,  "Collecting accounts to close." );
            Collection maacs = getAccountsToClose();
            log.debug(LOG, "Found " + maacs.size() + " accounts." );
            //MDC.remove("JobName");
            return reminder.changeAccountStatuses( maacs );
            //return ((ReminderRemote)hessian.getServiceInterface()).changeAccountStatuses(maacs);
        }
        catch( Exception e ){
            e.printStackTrace();
            log.error(LOG, e.getLocalizedMessage(), e);
            throw new RuntimeException( e );
        }
        finally {
            MDC.remove("JobName");
        }
    }

    /**
     * Returns a <code>Collection</code> of <code>ee.emt.xgate.component.paymentgraph.model.Tuple</code>
     * with the first component - the maac_ref_num (<code>Integer</code>) and the second -
     * overdue amount for current maac (<code>Double</code>).
     * 
     * The query called is TBCIS.xg_reminder.get_accounts_to_close()
     * 
     * @return
     */
    public Collection getAccountsToClose() {//} throws Exception  {
        Vector result = new Vector();
        Connection connection = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        MDC.put("JobName","ReminderCloseAccounts");
        try{
            String mode = getStringConvertMode();
            DataSource ds = jdbcTemplate.getDataSource();
            connection = ds.getConnection();
            String query = "BEGIN ? := TBCIS.XG_REMINDER.get_accounts_to_close(); END;";
            stmt = connection.prepareCall( query );
            stmt.registerOutParameter( 1, oracle.jdbc.OracleTypes.CURSOR );
            stmt.execute();
            rs = (ResultSet)stmt.getObject( 1 );
            while( rs.next() ){
                result.add( new Tuple( rs.getString( "MAAC_REF_NUM" ), new String( DbStringCoder.decode( rs.getString( "CLN_REASON" ), mode ) ) ) );
                log.debug(LOG, "maac: "+rs.getString( "MAAC_REF_NUM" )+" | reason: "+new String( DbStringCoder.decode( rs.getString( "CLN_REASON" ), mode ) ));
            }

        }
        catch( Exception e ){
            e.printStackTrace();
            throw new RuntimeException( e );
        }
        finally{
            try{
                rs.close();
            }
            catch( Exception e ){
                e.printStackTrace();
            }
            try{
                stmt.close();
            }
            catch( Exception e ){
                e.printStackTrace();
            }
            try{
                connection.close();
            }
            catch( Exception e ){
                e.printStackTrace();
            }
            MDC.remove("JobName");
        }
        return result;
    }

}
