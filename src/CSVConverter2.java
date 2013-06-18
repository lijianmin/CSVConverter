
import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author li0562e
 */
public class CSVConverter2 {
    
    public static void main (String[] args){
        //readCSV2("test", "test", '|');
    }
    
    public static void writeToXLSX2 (Map<String, String[]> data, String outputXLSX){
        
        try{
            
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(outputXLSX);
            Row r;
        
            Set<String> keyset = data.keySet();
        
            int rownum = 0;    
            
            System.out.print("Converting to "+outputXLSX+".xlsx...");
            
            //monkey donkeys here
            for(String key : keyset){
                r = sheet.createRow(rownum++);
                
                String[] values = data.get(key);
                
                int cellnum = 0;
                
                for(String v : values){
                    Cell c = r.createCell(cellnum++);
                    c.setCellValue(v.replaceAll("�", " "));
                }
                
            }
            
            FileOutputStream out = new FileOutputStream(new File(outputXLSX+".xlsx"));
            workbook.write(out);
            
            out.close();
            
            System.out.print("COMPLETE\n");
        
        }catch(Exception e){
            
        }

    }
    
    public static void readCSV2(String inputCSV, String outputXLSX, char delimiter){
        
        int rowNo = 0;

        try{
            
            String[] values = null;
            
            Map<String, String[]> excel_data = new HashMap<String, String[]>();
            
            InputStream csvFile = new FileInputStream(inputCSV + ".csv");
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFile), delimiter);
            
            System.out.print("Reading and storing "+inputCSV+".csv...");
            
            while((values = csvReader.readNext()) != null) {
                
                excel_data.put(Integer.toString(rowNo), values);
                rowNo++;
                
            }
            
            System.out.print("COMPLETE\n");
            
            csvReader.close();
            
            writeToXLSX2(excel_data, outputXLSX);
            
        }catch(Exception e){
            
        
        } 
    }   
}
