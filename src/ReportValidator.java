import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ReportValidator {

    private OPCPackage xlsxPackage;
    private int minColumns;
    private PrintStream output;

    /**
     * Creates a new XLSX -> CSV converter
     *
     * @param pkg        The XLSX package to process
     * @param output     The PrintStream to output the CSV to
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     */
    public ReportValidator(OPCPackage pkg, PrintStream output, int minColumns) {
        this.xlsxPackage = pkg;
        this.output = output;
        this.minColumns = minColumns;
    }

    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *
     * @param styles
     * @param strings
     * @param sheetInputStream
     */
    public void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream)
            throws IOException, ParserConfigurationException, SAXException {

        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        
        MyXSSFSheetHandler xssfsheethandler = new MyXSSFSheetHandler(styles, strings, this.minColumns, this.output);
        ContentHandler handler = xssfsheethandler;
        
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
        
        //run last
        xssfsheethandler.groupReports();
        
    }

    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws IOException
     * @throws OpenXML4JException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void process() 
            throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            String sheetName = iter.getSheetName();
            this.output.println();
            this.output.println(sheetName + " [index=" + index + "]:");
            processSheet(styles, strings, stream);
            stream.close();
            ++index;
        }
    }   

    public static void main(String[] args) throws Exception {
        
        String dir = "D:\\DOCUMENTS\\OMG LATEST REPORTS OMG\\XLSX\\";
        //String file = "REP_2010_01_01.xlsx";
        //String file = "REP_2010_04_01.xlsx";
        //String file = "REP_2010_07_01.xlsx";
        String file = "REP_2010_10_01.xlsx";
        
        File xlsxFile = new File(dir+file);
        int minColumns = -1;
        OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
	ReportValidator v = new ReportValidator(p, System.out, minColumns);
	System.out.println("### PROCESSING "+xlsxFile.toString()+" ###");
        v.process();  
        
    }

}


