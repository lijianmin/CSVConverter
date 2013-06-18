
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author li0562e
 */
public class ReportProcessor {
                   
        //String reportCreator_MCR = parts[4].split("\\^")[0];
        //String createdDateTime = parts[5];
        //String radiologist1_MCR = parts[8].split("\\^")[0];
        //String verifiedDateTime1 = parts[9];
        //String finalizedDateTime = parts[15];

        //E.g. 1/17/05 (MM/DD/YY)
        private void verifyDate(String dateFromColumn, String reportDate){
            
            Date oldColumnDate = new Date();
            SimpleDateFormat oldFormat = new SimpleDateFormat("MM/dd/yy");
            
            Date newColumnDate = new Date();
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM-yyyy");
            
            Date actualReportDate = new Date();

            try{
                //convert dateFromColumn string to new format and set as Date object
                oldColumnDate = oldFormat.parse(dateFromColumn);
                newColumnDate = newFormat.parse(newFormat.format(oldColumnDate));
                
                //set reportDate string to new format and set as Date object
                actualReportDate = newFormat.parse(reportDate);
                
                //compare dates
                if(actualReportDate.before(newColumnDate) || 
                        actualReportDate.after(newColumnDate)){
                    System.out.print("WARNING: Difference in both dates found");
                }
                
            }catch(Exception e){
                System.out.print("ERROR: Unable to convert or compare");
            }
            
            
        }
      
        public void extractAccessionNumber(String[] report){
            //Only for reports dated before year 2010
            //Cerner Accession Number: Get the index of the report which has it
            String regex = "(SGH|SIC|NHC|NCC|NDC)\\u00A0";
            String previous = "";
            String current = "";
            
            for(int i=0; i<report.length; i++){
                Matcher m = Pattern.compile(regex).matcher(report[i]);
                
                while(m.find()){
                    
                    if(m.start()+16 < report[i].length()){
                        current = report[i].substring(m.start(), m.start()+16);
                    
                        if(!previous.equals(current)){
                            System.out.print(current + "\t");
                        }
                    
                        previous = current;
                    }
                    
                }//while
                
            }//for
                
        }
        
        public int getReportText(String p, String[] report){
            int index = -1;
            Matcher m = null;
            Pattern e = Pattern.compile(p);
            
            for(int r=0; r<report.length; r++){
                m = e.matcher(report[r]);
                if(m.find()){
                    index = r;
                    break;
                } 
            }
            
            return index;
        }
        
        public String[] removeWhiteSpaces(String[] report){
            LinkedList<String> t = new LinkedList();
            
            for(int r=0; r<report.length; r++){
                if(report[r].equals("") || report[r].equals(" ")) 
                    continue;
                else
                    t.add(report[r]);
            }
            
            String[] temp = t.toArray(new String[t.size()]);
            //for(int i=0; i<temp.length; i++) System.out.println(temp[i]);
            
            return temp;
        }
        
        public void validate(   String reportCreator, 
                                String radiologist1, 
                                String verifiedDateTime1, 
                                String finalizedDateTime, 
                                String[] report             ){
            
            Pattern caretDelimited      
                    = Pattern.compile("\\^");
            
            Pattern reportCreator_MCR   
                    = Pattern.compile( ( caretDelimited.split(reportCreator)[0] ).toUpperCase() );
            
            Pattern radiologist1_MCR    
                    = Pattern.compile( ( caretDelimited.split(radiologist1)[0] ).toUpperCase() );
            
            Matcher m = null;

            for(int r=0; r<report.length; r++){
                
                if(report[r].contains("Resident")){
                    //System.out.print("WARNING: Resident is found in the report \t");
                    //System.out.print("#\t");
                }
                if(report[r].contains("Reported\\u00A0by")){
                    
                    System.out.println("FOUND");
                    m = reportCreator_MCR.matcher(report[r]);
                    if(!m.find()) 
                        System.out.print("WARNING: " + reportCreator_MCR.toString() 
                                + " not found \t");
                }
                if(report[r].contains("Verified by")){
                    
                    m = radiologist1_MCR.matcher(report[r]);
                    if(!m.find()) 
                        System.out.print("WARNING: " + radiologist1_MCR.toString() 
                                + " not found \t");                    
                }
                if(report[r].contains("Verified Date")){
                    //take finalized date time and compare
                    /*System.out.println( "VERIFIED DATE FOUND IN REPORT TEXT: " 
                            + report[r].split("  ")[1] );
                    
                    System.out.println( "COLUMN VERIFIED_DATE_TIME_1: " 
                            + verifiedDateTime1.substring(0, verifiedDateTime1.indexOf(" ")) );
                    
                    System.out.println( "COLUMN FINALIZED_DATE_TIME: " 
                            + finalizedDateTime.substring(0, finalizedDateTime.indexOf(" ")) );*/
                    
                    this.verifyDate(verifiedDateTime1.substring(0, verifiedDateTime1.indexOf(" ")), report[r].split("  ")[1]);
                }
               
            }

        }
        
        private LinkedList<String> src_ISR 
                = new LinkedList<String>();
        
        private LinkedList<String> target_ISR 
                = new LinkedList<String>();
        
        public void storeISR(String[] p){
            
            //ISR on index 1
            src_ISR.add(p[1]);
            target_ISR.add(p[1]);
            
        }
        
        public void runGroupReporting(){
            String[] src = new String[src_ISR.size()];
            src_ISR.toArray(src);
            
            String[] target = new String[target_ISR.size()];
            target_ISR.toArray(target);
            
            int repeatedCount = 0;
            
            for(int x=0; x<src.length; x++){
                String ISR = src[x];
                
                for(int y=0; y<target.length; y++){
                    String tgtISR = target[y];
                    if(ISR.equalsIgnoreCase(tgtISR)) repeatedCount++;
                }
                
                if(repeatedCount > 1){
                    System.out.println(src_ISR + ": There are "+repeatedCount+" same ISR.");
                }
                
                repeatedCount = 0;
            }
        }
        
        private boolean isReportFinalized(String report){
            boolean isFinalized = true;
            
            Matcher m = Pattern.compile("FINAL\\u00A0REPORT").matcher(report);
            
            if(!m.find()) isFinalized = false;
            
            return isFinalized;
        }
        
        public void processReport(String source){
            
            String[] parts = 
                    Pattern.compile("\\|").split(source); 
            
            String reportText = 
                    parts[3];
            
            String[] splittedReport = 
                    Pattern.compile("\\\\.br\\\\").split(reportText); 

            String reportCreator = parts[4];
            String radiologist1 = parts[8];
            
            //Remove redundant empty indexes.
            //removeWhiteSpaces(splittedReport);
            
            //Validate report content
            //System.out.print(parts[2] + "\t");
            //this.validate( reportCreator, radiologist1, parts[9], parts[15], removeWhiteSpaces(splittedReport) );
            
            //Additional: Extraction of legacy accession number
            //System.out.print(this.number + ".\t" + parts[2] +"\t");
            //this.number++;
            this.extractAccessionNumber(splittedReport);
            
            
            //this.storeISR(parts);
            System.out.print("\n");

        }    
}
