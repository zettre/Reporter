package com.tcs.reporter.test;
import com.tcs.reporter.main.ReportCreator;
import com.tcs.reporter.bean.Config;
import com.tcs.reporter.exception.ReporterException;
import com.tcs.reporter.util.Logger;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Reporter {

	public static void main(String[] args) {
		String projectRootFolder=System.getProperty("user.dir");
		String configFilePath=projectRootFolder+"\\conf\\config.xml";
		
		
		//reading config.xml and getting configuration parameters
		File xmlFile=new File(configFilePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        org.w3c.dom.Document doc;
        NodeList nodeList;
		Config con=null;
        try 
        {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("config");
            if(nodeList.getLength()==0) throw new ReporterException("Config tag missing in config.xml");
            con=getConfig(nodeList.item(0));
        }catch (SAXException | ParserConfigurationException | IOException e) 
        {
        	System.out.println("Cannot load configuration file!");
        }catch(ReporterException re)
        {
        	System.out.println(re.getMessage());
        }
		con.setProjectRootFolder(projectRootFolder+"\\");
		ReportCreator rc=new ReportCreator(con);
		try
		{
			rc.generateReport(args[0]);
		}catch(ReporterException re)
		{
			Logger.log(re.getMessage(),con.getLogFilePath());
		}
	}
	
	private static Config getConfig(Node node) {
        Config con = new Config();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            con.setPdfSavePath(getTagValue("pdfSavePath", element));
            con.setReportsFileName(getTagValue("reportsFileName", element));
            con.setReportsFilePath(getTagValue("reportsFilePath", element));
            con.setLogFilePath(getTagValue("logFilePath",element));
            con.setDbUrl(getTagValue("dbUrl",element));
            con.setDriver(getTagValue("dbDriver",element));
            con.setUsername(getTagValue("dbUsername",element));
            con.setPassword(getTagValue("dbPassword",element));
        }
        return con;
    }

	
	private static String getTagValue(String tag, Element element) {
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
        return value; //node.getNodeValue();
    }
}
