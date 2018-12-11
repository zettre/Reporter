package com.tcs.reporter.main;
import com.tcs.reporter.exception.ReporterException;
import com.tcs.reporter.bean.Report;
import com.tcs.reporter.bean.Column;
import com.tcs.reporter.bean.Config;
import com.tcs.reporter.bean.Argument;
import com.tcs.reporter.bean.PdfProperty;
import com.tcs.reporter.dao.ReporterDAO;


import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;

public class ReportCreator {
	private Config config;
	public ReportCreator(Config config)
	{
		this.config=config;
	}
	public void generateReport(String reportNumber) throws ReporterException
	{
		
		
		//reading reports.xml and finding the report to print
		String xmlFilePath=config.getProjectRootFolder()+config.getReportsFilePath()+config.getReportsFileName();
		File xmlFile=new File(xmlFilePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        org.w3c.dom.Document doc;
        NodeList nodeList;
        ArrayList<Report> rptList;
        Report report=null;
        try 
        {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("report");
            if(nodeList.getLength()==0) throw new ReporterException("Report tag missing in reprts.xml"); 
            rptList = new ArrayList<Report>();
            Report r;
            for (int i = 0; i < nodeList.getLength(); i++) {
            	r=getReport(nodeList.item(i));
                rptList.add(r);
                if(r.getSerialNumber().equals(reportNumber)) report=r;
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new ReporterException(e.getMessage());
        }
		
        
		//reading column mapping xml file
        float [] columnWidths;
        ArrayList<Column> colList;
        xmlFile=new File(config.getProjectRootFolder()+config.getReportsFilePath()+report.getColumnMappingFile());
        try {
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("column");
            if(nodeList.getLength()==0) throw new ReporterException("Column tag missing in column mapping file");
            colList = new ArrayList<Column>();
            Column c;
            Float total=0.0f;
            float width=0.0f;
            int zeros=0;
            columnWidths=new float[nodeList.getLength()];
            for (int i = 0; i < nodeList.getLength(); i++) 
            {
            	c=getColumn(nodeList.item(i));
            	columnWidths[i]=c.getWidth();
            	if(c.getWidth()==0) zeros++;
            	total+=columnWidths[i];
                colList.add(c);
            }
            if(total!=100 && zeros!=0)
            {
            	width=(100-total)/zeros;
            	int i=0;
            	for(Column cm:colList)
            	{
            		if(cm.getWidth()==0.0f)
            		{
            			cm.setWidth(width);
            			columnWidths[i]=width;
            		}
            		i++;
            	}
            }
            else if(total==100 && zeros!=0)
            {
            	throw new ReporterException("Some column in column mapping xml have undefined widths");
            }else if(total!=100 && zeros==0)
            {
            	throw new ReporterException("Total width of all column should be equal to 100");
            }
        } catch (SAXException | IOException e) {
            throw new ReporterException(e.getMessage());
        }
        
        
		/*reading sql file and getting query, 
		 * using dao to run sql command and getting data*/
        String sqlFile=config.getProjectRootFolder()+config.getReportsFilePath()+report.getSqlFile();
        String sql=null;
        try
        {
	        BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
	        StringBuilder stringBuilder = new StringBuilder();
	        String line = null;
	        String ls = System.getProperty("line.separator");
	        while ((line = reader.readLine()) != null) {
	        	stringBuilder.append(line);
	        	stringBuilder.append(ls);
	        }
	        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
	        reader.close();
	        sql = stringBuilder.toString();
        }catch(IOException io)
        {
        	throw new ReporterException(io.getMessage());
        }
		
        /*reading arguments file to get arguments for sql query 
         * and replacing them with variables in query*/
        ArrayList<Argument> argsList;
        xmlFile=new File(config.getProjectRootFolder()+config.getReportsFilePath()+report.getArgumentsFile());
        try {
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("argument");
            argsList = new ArrayList<Argument>();
            Argument arg;
            for (int i = 0; i < nodeList.getLength(); i++) {
            	arg=getArgument(nodeList.item(i));
                argsList.add(arg);
            }
        } catch (SAXException | IOException e) {
            throw new ReporterException(e.getMessage());
        }

        if(argsList.size()!=0) sql=setSqlVariables(sql, argsList);
        ReporterDAO rptDAO=new ReporterDAO(config);
        ArrayList<ArrayList<String>> records=rptDAO.getRecords(sql, colList);
		
        
		/*reading pdf xml file to get properties and creating pdf 
		 * and saving it to specified location*/
        PdfProperty pdf;        
        xmlFile=new File(config.getProjectRootFolder()+config.getReportsFilePath()+report.getPdfPropertiesFile());
        try {
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("property");
            if(nodeList.getLength()==0)
            {
            	pdf=new PdfProperty(36,36,36,36,12,15,10);
            }
            else
            	pdf=getPdfProperty(nodeList.item(0));
        } catch (SAXException | IOException e) {
            throw new ReporterException(e.getMessage());
        }

        
        
        File pdfFile=new File(config.getPdfSavePath()+report.getName()+"_"+new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())+".pdf");
        pdfFile.getParentFile().mkdirs();
        Rectangle rect=new Rectangle(pdf.getPageWidth()*72,pdf.getPageHeight()*72);
        Document document=new Document(rect);
        document.setMargins(pdf.getLeftMargin(), pdf.getRightMargin(), pdf.getTopMargin(), pdf.getBottomMargin());
        try
        {
	        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        document.open();
	        PdfPTable table=new PdfPTable(colList.size());
	        table.setWidthPercentage(100);
	        table.setWidths(columnWidths);
	        PdfPCell pdfCell;
	    	for(Column col:colList)
	    	{
	    		pdfCell=new PdfPCell(new Phrase(col.getDisplayName(),new Font(Font.FontFamily.TIMES_ROMAN, pdf.getFontSize()+2, Font.BOLD)));
	            table.addCell(pdfCell);        		
	    	}        
	    	table.setHeaderRows(1);
	        for(ArrayList<String> record:records)
	        {
	        	for(String val:record)
	        	{
		    		pdfCell=new PdfPCell(new Phrase(val,new Font(Font.FontFamily.TIMES_ROMAN, pdf.getFontSize(), Font.NORMAL)));
	                table.addCell(val);        		
	        	}
	        }
	        document.add(table);
	        document.close();
        }catch(DocumentException | IOException e)
        {
        	throw new ReporterException(e.getMessage());
        }
        
	}
	
	
	//Utility methods	
	private static String setSqlVariables(String sql, ArrayList<Argument> argsList)
	{
		String newSql=sql;
		for(Argument arg:argsList)
		{
			if(arg.getType().equalsIgnoreCase("string"))
			{
				newSql=newSql.replace("$"+arg.getName(), "'"+arg.getValue()+"'");
			}
			else if(arg.getType().equalsIgnoreCase("integer"))
			{
				newSql=newSql.replace("$"+arg.getName(), arg.getValue());
			}
		}
		return newSql;
	}
	
	private static Report getReport(Node node) 
	{
        Report rpt = new Report();
        if (node.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) node;
            rpt.setName(getTagValue("name", element));
            rpt.setSerialNumber(getTagValue("serialNumber", element));
            rpt.setSqlFile(getTagValue("sqlFileName", element));
            rpt.setColumnMappingFile(getTagValue("columnMappingFileName", element));
            rpt.setPdfPropertiesFile(getTagValue("pdfPropertiesFileName", element));
            rpt.setArgumentsFile(getTagValue("argumentsFileName", element));
        }

        return rpt;
    }
	
	private static Column getColumn(Node node) throws ReporterException 
	{
        Column col = new Column();
        if (node.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) node;
            col.setSqlName(getTagValue("sqlName", element));
            col.setDisplayName(getTagValue("displayName", element));
            try
            {
            	col.setWidth(Float.parseFloat(getTagValue("width",element)));
            }catch(NumberFormatException nfe)
            {
            	col.setWidth(0.0f);
            }
        }
        return col;
    }

	private static PdfProperty getPdfProperty(Node node) throws ReporterException 
	{
        PdfProperty pdf = new PdfProperty();
        if (node.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) node;
            try
            {
	            pdf.setLeftMargin(Float.parseFloat(getTagValue("leftMargin", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setLeftMargin(36);
            }
            try
            {
	            pdf.setRightMargin(Float.parseFloat(getTagValue("rightMargin", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setRightMargin(36);
            }
            try
            {
	            pdf.setTopMargin(Float.parseFloat(getTagValue("topMargin", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setTopMargin(36);
            }
	        try
	        {
	            pdf.setBottomMargin(Float.parseFloat(getTagValue("bottomMargin", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setBottomMargin(36);
            }

	        try
	        {
	            pdf.setPageHeight(Integer.parseInt(getTagValue("pageHeight", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setPageHeight(15);
            }
	        try
	        {
	            pdf.setPageWidth(Integer.parseInt(getTagValue("pageWidth", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setPageWidth(10);
            }
	        try
	        {
	            pdf.setFontSize(Integer.parseInt(getTagValue("fontSize", element)));
            }catch(NumberFormatException nfe)
            {
            	pdf.setFontSize(12);
            }

        }
        return pdf;
    }

	private static Argument getArgument(Node node) 
	{
        Argument arg = new Argument();
        if (node.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) node;
            arg.setName(getTagValue("name", element));
            arg.setValue(getTagValue("value", element));
            arg.setType(getTagValue("type", element));
        }

        return arg;
    }

	private static String getTagValue(String tag, Element element) 
	{
		Node node;
		String value="";
        NodeList nodeList = element.getElementsByTagName(tag);//.item(0).getChildNodes();
        if(nodeList.getLength()==1)
        {
        	node=nodeList.item(0);
            if(node.getNodeType()==Node.ELEMENT_NODE)
            {
            	nodeList=node.getChildNodes();
            	if(nodeList.getLength()==1)
            	{
	            	node=nodeList.item(0);
	            	if(node.getNodeType()==Node.TEXT_NODE) value=node.getNodeValue();
            	}
            }
        }
        //node = (Node) nodeList.item(0);
        return value.trim(); //node.getNodeValue();
    }
}