package JDBC;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.*;

public class JDBC {

    public static void main(String[] args) {
        String csvFile = "C:/Users/shinwoohwan/Desktop/Han/2017-Summer/Fintech-Groundup/3аж/7.18/oxford/OXFORD.csv";
       BufferedReader br = null;
       String line = "";
       String cvsSplitBy = ",";

       try {
           
           br = new BufferedReader(new FileReader(csvFile));
           while ((line = br.readLine()) != null) {
               
               // use comma as separator
               String[] field = line.split(cvsSplitBy);
               Connection con;
               Class.forName("com.ibm.db2.jcc.DB2Driver");
               con = DriverManager.getConnection("jdbc:db2://dashdb-entry-yp-dal09-10.services.dal.bluemix.net:50000/BLUDB", "dash7609", "_zms4M0m_OCA");
               PreparedStatement pre;
               File picfile = new File("C:/Users/shinwoohwan/Desktop/Han/2017-Summer/Fintech-Groundup/3аж/7.18/oxford/" + field[1]);
               FileInputStream fis = new FileInputStream(picfile);
               pre = con
                       .prepareStatement("insert into PRODUCT values (?,?,?,?,?)");
               pre.setString(1, field[0]);
               pre.setString(2, field[1]);
               pre.setString(3, field[2]);
               pre.setString(4, field[3]);
               pre.setBinaryStream(5, fis, (int) picfile.length());
               int count = pre.executeUpdate();
               System.out.println("field [name= " + field[0] + " , jpg=" + field[1] + "]");
               System.out.println("isUpdated? " + count);
               pre.close();
               con.close();
           }
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (br != null) {
               try {
                   br.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
        
    }
}