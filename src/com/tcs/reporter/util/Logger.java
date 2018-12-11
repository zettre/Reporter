package com.tcs.reporter.util;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class Logger {
	private static final String dev_mode="development";// development/production
	private static final String logFile="logger.lg";
	public static void log(String log,String logFilePath)
	{
		if(dev_mode.equals("development"))
		{
			System.out.println(log);
		}else if(dev_mode.equals("production"))
		{
			try
			{
				new File(logFilePath).mkdirs();
				FileWriter fw=new FileWriter(logFilePath+logFile,true);
				fw.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+" : "+log+"\r\n");
				fw.close();
			}catch(IOException io)
			{
				System.out.println(io.getMessage());
			}
		}else
		{
			System.out.println("Check Logger!");
		}
	}
}
