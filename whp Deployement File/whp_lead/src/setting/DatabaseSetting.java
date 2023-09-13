package  setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseSetting 
{
    private String databaseip;
    private String databaseusername;
    private String databasepassword;
    private String databasename;
    private String databaseport;
    private String logEnable;
    private String logPath;
    final static String FILE_NAME = "db.properties";

    public String getDatabaseip() {
        return databaseip;
    }

    public void setDatabaseip(String databaseip) {
        this.databaseip = databaseip;
    }

    public String getDatabaseusername() {
        return databaseusername;
    }

    public void setDatabaseusername(String databaseusername) {
        this.databaseusername = databaseusername;
    }

    public String getDatabasepassword() {
        return databasepassword;
    }

    public void setDatabasepassword(String databasepassword) {
        this.databasepassword = databasepassword;
    }

    public String getDatabasename() {
        return databasename;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    public String getDatabaseport() {
        return databaseport;
    }

    public void setDatabaseport(String databaseport) {
        this.databaseport = databaseport;
    }

    public String getLogEnable() {
        return logEnable;
    }

    public void setLogEnable(String logEnable) {
        this.logEnable = logEnable;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

//    public void readConfiguration() {
//
//        try {
//
//            ResourceBundle rbConfig = ResourceBundle.getBundle("./db");
//
//            databaseip = rbConfig.getString("dbip");
//            databaseusername = rbConfig.getString("dbusername");
//            databasepassword = rbConfig.getString("dbpassword");
//            databasename = rbConfig.getString("dbname");
//            databaseport = rbConfig.getString("dbport");
//            logEnable = rbConfig.getString("logenable");
//            logPath = rbConfig.getString("logpath");
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//
//        }
//
//    }
    
    
   

    public void readConfiguration() {

        try {

            File file = new File(FILE_NAME);    //creates a new file instance  
            FileReader fr = new FileReader(file);   //reads the file  
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("dbip")) {
                    databaseip = (line.split("=")[1]);
                }
                if (line.contains("dbusername")) {
                    databaseusername = (line.split("=")[1]);
                }
                if (line.contains("dbpassword")) {
                    databasepassword = (line.split("=")[1]);
                }
                if (line.contains("dbport")) {
                    databaseport = (line.split("=")[1]);
                }
                if (line.contains("dbname")) {
                    databasename = (line.split("=")[1]);
                }
                if (line.contains("logenable")) {
                    logEnable = (line.split("=")[1]);
                }
                if (line.contains("logpath")) {
                    logPath = (line.split("=")[1]);
                }

            }
            fr.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Connection getConnection(){
        Connection conn = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://"+databaseip+":"+databaseport+"/"+databasename+"?characterEncoding=UTF-8",databaseusername,databasepassword);
//            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.223:3306/asterisk","root","s@mp@rk@123");
        }
        catch(Exception ee)
        {
           // System.out.println(ee);
            WriteLogs.CreateAppLogs("getConnection=>"+ee.toString());
        }
        return conn;
    }
}
