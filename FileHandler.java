package assignment1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.jsoup.Jsoup;


public class FileHandler {
	
	//this function returns the file names found in the given directory.
	public static ArrayList<String> getFileNamesFromDirectory(String directory)
	{
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames=new ArrayList<String>();
        
		for (int i = 0; i < listOfFiles.length; i++) 
		  if (listOfFiles[i].isFile()) 
		  {
		   fileNames.add(listOfFiles[i].getName());
		  } 
		  
		  return fileNames;
		
	}
	
	public static String readFile(String file)
	{
		try {
			String text = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
			return text;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	  
	public static String extractStringFromHTTP(String filePath)
	{

		
		String http= readFile(filePath);
	
		
	
	   String html=null;
	   
	   try
	   {
		   
	   html= http.substring(http.toLowerCase().indexOf("<!doctype"));
	
	   }
	   catch (StringIndexOutOfBoundsException e)
	   {
		   try
		   {
		   html= http.substring(http.toLowerCase().indexOf("<html>"));
		   
		
	       }
		   
		   catch (StringIndexOutOfBoundsException s)
		   {
			 
				   html=null;
		   }
	   
	 
	    
	    //return  Jsoup.parse(html).body().text();
	}
	   

	   if (html==null)
		   return null;

	   
    return Jsoup.parse(html).text();
	}

	
}
