package assignment1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//part 2 of the assignment
public class Indexer {
	
	private class InvertedIndexItem
	{  
		
	    ArrayList<DocIdOffsetIdPair> docIdOffsetIdpairs;
	    int totalNumberOfDocumentsInWhichTheTermAppeared=0;
	    
	    public InvertedIndexItem()
	    {
	    	this.docIdOffsetIdpairs=new ArrayList<DocIdOffsetIdPair>();
	    }
	    
	   public  class DocIdOffsetIdPair
	   {
		 int docId;
		 int offset;
		 
		 public DocIdOffsetIdPair(int docId, int offset)
		 {
			 this.docId=docId;
			 this.offset=offset;
		 }
	   }
	 
    }
	
	private InvertedIndexItem []invertedIndex; 
	
	
	private int totalDocuments=0;
	private int totalTerms=0;
	
	Indexer()
	{
		try {
			BufferedReader countReader=new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\counter.txt"));
	        String counterData;
	        counterData=countReader.readLine();
	        
	        this.totalTerms=Integer.parseInt(counterData.split("\t")[0]);
	        this.totalDocuments=Integer.parseInt(counterData.split("\t")[1]);
	  
	        
	        countReader.close();
	        
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
	
	
	}//end of constructor
	
	
	public void createInMemoryInvertedIndex()
	{
		invertedIndex=new InvertedIndexItem[totalTerms+1];
		for (int i=1; i< totalTerms+1; i++)
		{
			this.invertedIndex[i]=new InvertedIndexItem();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\doc_index.txt"))) {
		    String line;
		    int lineNo=1;
		    while ((line = br.readLine()) != null) {
		       
		    	String data[]= line.split("\t");
		    	int termID= Integer.parseInt(data[1]);
		    	int docId=Integer.parseInt(data[0]);
		    	System.out.println("Indexer is now processing line no: "+ (lineNo++) +" of doc_index.txt");
		    	this.invertedIndex[termID].totalNumberOfDocumentsInWhichTheTermAppeared++;
		    	ArrayList<InvertedIndexItem.DocIdOffsetIdPair> list= this.invertedIndex[termID].docIdOffsetIdpairs;
		    	int prevDocId=0;
		    	for (int i=0; i< list.size(); i++)
		    	{
		    		prevDocId+= list.get(i).docId;
		    		
		    	}
		    	
		    	int currentDocIdDelta=docId-prevDocId; //document delta
		      
		    	
		    	//adding doc delta: offset delta pairs
		    	list.add(invertedIndex[termID].new DocIdOffsetIdPair(currentDocIdDelta, Integer.parseInt(data[2])));
		    	int termOffsetAggregate= Integer.parseInt(data[2]);
		    	
		    	for (int i=3; i<data.length; i++)
		    	{
		    	   	int newOffsetDelta= Integer.parseInt(data[i])-termOffsetAggregate;
		    	   	list.add(invertedIndex[termID].new DocIdOffsetIdPair(0, newOffsetDelta));
		    	   	
		    	   	termOffsetAggregate+=newOffsetDelta;
		    	}
		    	
		  
	             	    	
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void writeInvertedIndexToFile()
	{
		try
		{

	  	BufferedWriter termIndexwriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\term_index.txt"));
        BufferedWriter termInfoWriter=new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\term_info.txt"));

	  	long offset=0;
    	for (int i=1; i<totalTerms+1; i++)
    	{
    		
    		termInfoWriter.write(i+"\t"+offset+"\t"+this.invertedIndex[i].docIdOffsetIdpairs.size()+"\t"+this.invertedIndex[i].totalNumberOfDocumentsInWhichTheTermAppeared);
    		
    		termIndexwriter.write(i+"");
    		offset+= String.valueOf(i).length();
    		
    		for (int j=0; j<this.invertedIndex[i].docIdOffsetIdpairs.size(); j++)
    		{
    			
    			offset+=1+String.valueOf(this.invertedIndex[i].docIdOffsetIdpairs.get(j).docId).length()+ 1+String.valueOf(this.invertedIndex[i].docIdOffsetIdpairs.get(j).offset).length();
              termIndexwriter.write("\t"+this.invertedIndex[i].docIdOffsetIdpairs.get(j).docId+":"+this.invertedIndex[i].docIdOffsetIdpairs.get(j).offset);
    		
              
    		}
    	
    	
    		if (i<totalTerms)
    		{   offset++;
    		   termInfoWriter.write("\n");
    			termIndexwriter.write("\n");
    		}
    	//System.out.println(i);
    	}
		
    	termInfoWriter.close();
    	termIndexwriter.close();
		}
		catch (IOException  e)
		{
			System.out.println("Error while writing to term_index.txt!");
		}
	
	}

}
