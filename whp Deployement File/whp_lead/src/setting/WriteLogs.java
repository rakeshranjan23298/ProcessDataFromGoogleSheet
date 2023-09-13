/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setting;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WriteLogs {

    static public void CreateAppLogs(String message) {
        try {
            DatabaseSetting dbSetting = new DatabaseSetting();
            dbSetting.readConfiguration();
            if (dbSetting.getLogEnable().equalsIgnoreCase("true")) {

                Calendar currentDate = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy_HH");
                String dateNow = formatter.format(currentDate.getTime());
                File file = new File(dbSetting.getLogPath());
                if(!file.exists()){
                  file.mkdir();
                }
                String separator = System.getProperty("file.separator");
                String filename = file.getAbsolutePath()+separator+ "WHP-Logs_" + dateNow + ".txt";
                FileWriter fstream = new FileWriter(filename, true);
                BufferedWriter out = new BufferedWriter(fstream);
                formatter = new SimpleDateFormat("dd-MMM-yyyy/HH:mm:ss");
                dateNow = formatter.format(currentDate.getTime());
                out.write(dateNow + "\t: " + message + "\n");
                out.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }
    }

}
