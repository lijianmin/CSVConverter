/*
 * http://thinktibits.blogspot.sg/2012/12/OpenCSV-POI-CSV-XLS-Java-Servlet-Example.html
 */

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import au.com.bytecode.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class CSVConverter {

    public static void main(String[] args) {
        
        //String inputCSV = args[0];
        //String outputXLSX = args[1];
        //char delimiter = args[2];
        //readCSV("Report_2005_01_01-t", "Report_2005_01_01-t", '|');
        
    }
    
    public static void writeToXLSX (String outputXLSX, String[] values, int rownum){
        
        try{
            
            XSSFWorkbook w = new XSSFWorkbook( 
                        new FileInputStream( new File(outputXLSX + ".xlsx") ) 
                    );
            
            XSSFSheet s = w.getSheetAt(0);
            
            Row r = s.createRow(rownum);
            int cellnum = 0;
        
            for(String v : values){
                Cell c = r.createCell(cellnum++);
                c.setCellValue(v.replaceAll("�", " "));
            }
            
            FileOutputStream output = new FileOutputStream( new File(outputXLSX + ".xlsx") );
            w.write(output);
            output.close();
            
        }catch(Exception e){
            
        }
    }
    
    public static void createXLSXFile(String outputXLSX){
        
        try{
            
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet s = workbook.createSheet(outputXLSX);
            FileOutputStream out = new FileOutputStream(new File(outputXLSX + ".xlsx"));
            workbook.write(out);

            out.close();
            
        }catch(Exception e){
            
        }
        
    }
        
    public static void readCSV(String inputCSV, String outputXLSX, char delimiter){
        
        int rowNo = 0;

        try{
            
            String[] values = null;
            
            createXLSXFile(outputXLSX);
            
            InputStream csvFile = new FileInputStream(inputCSV + ".csv");
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFile), delimiter);
            
            System.out.print("Reading and converting " + inputCSV + ".csv...");

            while((values = csvReader.readNext()) != null) {
                
                for(int i=0; i<values.length; i++) System.out.print(values[i]+"\t");
                //writeToXLSX(outputXLSX, values, rowNo++);
                System.out.println();
                
            }
            
            System.out.print("COMPLETE\n");
            
            csvReader.close();
            
        }catch(Exception e){

        }finally{
            
        }
    }
    
}
