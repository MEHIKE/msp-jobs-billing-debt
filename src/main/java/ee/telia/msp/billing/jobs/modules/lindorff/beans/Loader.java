package ee.telia.msp.billing.jobs.modules.lindorff.beans;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

import ee.telia.msp.billing.jobs.modules.lindorff.model.LindorffVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Loader{
    //static transient Logger log = Logger.getLogger(Loader.class.getName());
    private static final Logger log = LoggerFactory.getLogger(Loader.class);
    final static Marker LOG = MarkerFactory.getMarker("Lindorff");

    @Value("${lindorff.path}")
    private String path;

    @Value("${lindorff.prefix}")
    private String prefix;

    @Value("${lindorff.maxDays}")
    private int maxDays;

    SimpleDateFormat fmt = new SimpleDateFormat("ddMMyyyy");


    public File getCurrentFile(){
        MDC.put("JobName","Lindorff");

        log.info(LOG, "getCurrentFile called");

        final String pre = prefix;
        File directory = new File(path);

        File files[] = directory.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name){
                if(name.startsWith(pre) ){
                    MDC.remove("JobName");
                    return true;
                }
                MDC.remove("JobName");
                return false;
            }
        } );
        if( files.length == 0 ){ 
            log.info(LOG, "No matching files found... returning immiadetely");
            MDC.remove("JobName");
            return null;
        }
        File f = findFile(files, fmt.format(new Date()));
        if(f != null)
            log.info(LOG, "Today's file was found:" + f.getName());
        else
            log.error(LOG, "Today's file NOT found from " + path);
        MDC.remove("JobName");

        return f;
    }


    public File getLastFile(){
        MDC.put("JobName","Lindorff");

        log.info(LOG, "getLastFile called for " + path);

        final String pre = prefix;
        File directory = new File(path);

        File files[] = directory.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name){
                if(name.startsWith(pre) ){
                    MDC.remove("JobName");
                    return true;
                }
                MDC.remove("JobName");
                return false;
            }
        } );
        if (files==null) {
            log.info(LOG, "No directory found... returning immiadetely");
            MDC.remove("JobName");
            return null;
        }
        if( files.length == 0 ){ 
            log.info(LOG, "No matching files found... returning immiadetely");
            MDC.remove("JobName");
            return null;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("ddMMyyyy");

        for(int i = 1; i <= maxDays; i++){
            String pattern = fmt.format(computeDays(new Date(), -i));
            String logTxt = "Checking back day " + i + " with pattern " + pattern;
            File file = findFile(files, pattern);
            if(file != null){
                logTxt+="... FOUND!";
                return file;
            }
            else{
                logTxt+="... NOT FOUND!";
            }
            log.info(LOG, logTxt);
        }
        MDC.remove("JobName");
        return null;
    }


    // liidab n peava kuupaevale, lahutamiseks pane negative days 
    Date computeDays(Date d, int days){
        d.setTime(d.getTime() + days*1000*60*60*24);
        return d;
    }

    File findFile(File[] files, String pattern){
        MDC.put("JobName","Lindorff");
        for(int a = 0; a < files.length; a++){
            File f = files[a];
            String realName = getFileNameWithoutExtension(f);
            //log.info("Testing " + realName);
            if(realName.endsWith(pattern)){
                log.info(LOG, "  Match found!");
                MDC.remove("JobName");
                return f;
            }
        }
        //log.info("  Match not found");
        MDC.remove("JobName");
        return null;
    }

    String getFileNameWithoutExtension(File tmpFile) {        
        tmpFile.getName();        
        int whereDot = tmpFile.getName().lastIndexOf('.');        
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2 ) {
            return tmpFile.getName().substring(0, whereDot);            
        }            
        return "";    
    }

    public Map load(File f) throws Exception{
        MDC.put("JobName","Lindorff");
        log.info(LOG, "load called for " + f);
        Map data = new HashMap();

        String line = null;
        int row = 1;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
        while ((line = bufferedReader.readLine()) != null) {
            if(row == 1){    // esimene rida on header
                row++;
                continue;
            }

            LindorffVO lf = new LindorffVO();
            try{
                lf = lf.mapData(line);
                data.put(lf.requestId, lf);
            }
            catch(Exception e){
                log.error(LOG, "Error on line " + row + ":" + line);
                log.error(LOG, lf.toString());
                log.error(LOG, e.getLocalizedMessage(), e);
            }
            row++;
        }
        log.info(LOG, "load done with " + data.size() + " entries");
        MDC.remove("JobName");
        return data;
    }

    public Map removeSimilars(Map data, File oldFile) throws Exception{
        MDC.put("JobName","Lindorff");
        log.info(LOG, "removeSimilars called for old file " + oldFile);

        String line = null;
        int row = 1;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(oldFile));
        while ((line = bufferedReader.readLine()) != null) {
            if(row == 1){    // esimene rida on header
                row++;
                continue;
            }

            LindorffVO oldLF = new LindorffVO();
            LindorffVO newLF = null;
            try{

                oldLF = oldLF.mapData(line);
                newLF = (LindorffVO)data.get(oldLF.requestId);
                if(newLF == null)
                    continue;

                if(oldLF.compare(newLF)){
                    data.remove(oldLF.requestId);
                }
            }
            catch(Exception e){
                log.error(LOG, "Error on old line " + row + ":" + line);
                log.error(LOG, oldLF.toString());
                log.error(LOG, e.getLocalizedMessage(), e);
            }
            row++;
        }
        log.info(LOG, "removeSimilars done! Still remained " + data.size() + " entries");
        MDC.remove("JobName");
        return data;
    }

    //spring stuff
    public void setPath(String path){
        this.path = path;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public void setMaxDays(String maxDays){
        this.maxDays = Integer.parseInt(maxDays);
    }

}
