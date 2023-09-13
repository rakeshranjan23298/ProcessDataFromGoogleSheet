package whp;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONObject;
import setting.DatabaseSetting;
import setting.WriteLogs;
public class Whp 
{
    static String sheet = "LEAD";
    public static void main(String[] args) throws IOException 
    {
        HttpClient client = new HttpClient();
        String sheetURL = "";
        sheetURL = "https://script.google.com/macros/s/AKfycbxTbH2LC_NPpOS735PX3uzCs3Ssnec8hnzgd_ZhSxqsgvVhXk_sxso9eE6QDUCBel2YSg/exec";	//----url1 (to fetch DAta)		
        HttpMethod method = new GetMethod(sheetURL);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) 
            {
                System.err.println("Method failed: " + method.getStatusLine());
                WriteLogs.CreateAppLogs("Exception||" + method.getStatusLine());
            }
            byte[] responseBody = method.getResponseBody();
            ArrayList list = new ArrayList();
            list.add(new String(responseBody));
            JSONArray jsonParsed = new JSONArray(list.toString());
            JSONObject jsonResponse = jsonParsed.getJSONObject(0);
            uploadData(jsonResponse);
        }catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            WriteLogs.CreateAppLogs("Exception||" + e.toString());
            e.printStackTrace();
        }catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            WriteLogs.CreateAppLogs("Exception||" + e.toString());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
            WriteLogs.CreateAppLogs("Data Read From Google Sheet Sucess ");            
            System.out.println("SucessFully Data Read From Google Sheet[if new data found then uploading start ondatabase.");            
        }
    }
    public static void uploadData(JSONObject jsonObj) 
    {
        try {
            DatabaseSetting db = new DatabaseSetting();
            db.readConfiguration();
            Connection conn = db.getConnection();
            WriteLogs.CreateAppLogs("Start Uploading Data into Database");                
            if (conn == null) {
                System.out.println("Error : Could not connect to database");
                WriteLogs.CreateAppLogs("Error : Could not connect to database");                  
                return;
            }
            String sql0 = "";
            PreparedStatement prest = null;
            ResultSet rs = null;
//-----------------   ---     JSON Manipulation   -------------------------------------------------------------------------------------------------------------------
            JSONArray contentArray = jsonObj.getJSONArray("content");
            String c_name = "";
            String c_email = "";
            String contact_number = "";
            String looking_for = "";
            String looking_for2 = "";
            String Source = "";
            String when_required = "";
            String c_whatsapp = "";
            String c_address = "";
            String c_message = "";
            String product = "";
            String leadQuality = "";
            String processed = "";

            int count = 0;
//              ----------------------------------------------------
            for (int row = 0; row < contentArray.length(); row++) 
            {
                JSONArray rowData = contentArray.getJSONArray(row);
                if (row >= 2) 
                {
                    int data = row;
                }
                c_name = cleanXSS(String.valueOf(rowData.get(6)));
                c_email = cleanXSS(String.valueOf(rowData.get(5)));
                contact_number = cleanXSS(String.valueOf(rowData.get(7)));
                looking_for = cleanXSS(String.valueOf(rowData.get(14)));
                looking_for2 = cleanXSS(String.valueOf(rowData.get(13)));
                Source = cleanXSS(String.valueOf(rowData.get(9)));
                when_required = cleanXSS(String.valueOf(rowData.get(15)));
                processed = String.valueOf(rowData.get(17));
				
                Long random11 = (long) (Math.random() * 9999999999999L);
                String refID = random11.toString();
                if (row > 1) 
                {
                    if (!processed.equalsIgnoreCase("PROCESSED")) {
                        try {
                            sql0 = "CALL sp_LeadPush_google(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                            prest = conn.prepareStatement(sql0);
                            prest.setString(1, refID);
                            prest.setString(2, contact_number);
                            prest.setString(3, c_email);
                            prest.setString(4, c_name);
                            prest.setString(5, looking_for);
                            prest.setString(6, looking_for2);
                            prest.setString(7, Source);
                            prest.setString(8, when_required);
                            prest.setString(9, "");
                            prest.setString(10, "");
                            prest.setString(11, "");
                            prest.setString(12, sheet);
                            prest.setString(13, "");
                            prest.setString(14, "");
                            prest.setString(15, sheet);
                            contact_number = contact_number.trim();
                            if (contact_number.length() > 9) 
                            {
                                contact_number = contact_number.substring(contact_number.length() - 9);
                                prest.executeUpdate();
                                count++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            WriteLogs.CreateAppLogs("Exception generate for ref ID="+refID+" which contact(msisdn) n0="+contact_number+" Exception name ="+e.toString());
                            System.out.println("Exception generate for ref ID="+refID+" which contact(msisdn) n0="+contact_number+" Exception name ="+e.toString());
                        }
                    }
                }
            }
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------                
            String result = "No New Data Found to Upload. all data of This Google Sheet is already Processed";
            if (count > 0) {
                System.out.println("count = " + count);
                result="Success \n total number of new Uploaded data is ="+count;
                WriteLogs.CreateAppLogs(result);
                callURL();
            }
            WriteLogs.CreateAppLogs(result);
            System.out.println(result);
            if (rs != null) {try {rs.close();} catch (Exception e) {}}
            if (prest != null) {try {prest.close();} catch (Exception e) {}}
            if (conn != null) {try {conn.close();} catch (Exception e) {}}
        } catch (Exception e) {
            System.out.println("Exception||" + e);
            WriteLogs.CreateAppLogs("Exception||" + e.toString());
        }
    }
    public static void callURL()
    {
        HttpClient client = new HttpClient();
        String sheetURL = "";
        sheetURL = "https://script.google.com/macros/s/AKfycbx3GUGuXgcAc75XUk8mG_Ww6EHmhsiBkXDjSlDdAp0Fh9lMuaWqkZLsJXHZ-jySf6ddyA/exec";	//----url 2-- to update Google Sheet With Processed
        HttpMethod method = new GetMethod(sheetURL);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);
            WriteLogs.CreateAppLogs("URL Hitt to Update GoogleSheet Sucess");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            WriteLogs.CreateAppLogs("Exception||" + e.toString());
        }
    }
    private static String cleanXSS(String value) {
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }
}
