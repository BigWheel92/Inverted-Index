package assignment1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.tartarus.snowball.ext.EnglishStemmer;

//part 3 of the assignment
public class IndexReader {
	
	private static class TermMetaData
	{
		long offset;
		int frequency;
		int noOfDocsInWhichTermAppeared;
		
		public TermMetaData(long offset, int frequency, int noOfDocsInWhichTermAppeared)
		{
			this.offset=offset;
			this.frequency=frequency;
			this.noOfDocsInWhichTermAppeared=noOfDocsInWhichTermAppeared;
		}
	}
	
	private static class DocMetaData
	{
		int distinctTerms=0;
		int totalTerms=0;
	}
	private static class TermDocMetaData
	{
		int termFrequencyInDoc=0;
		ArrayList<Integer> positions=new ArrayList<Integer>();
	}
	private static EnglishStemmer stemmer=new EnglishStemmer();
	
	public static void main(String []args)
	{
	
		if (args.length<2)
		{
			return;
		}
		
		//--term TERM  --doc DOCNAME
		if (args[0].equals("--term") && args.length==4)
		{
			int termId= getTermId(args[1]);
			int docId= getDocId(args[3]);
			
			if (termId==-1 )
			{
				System.out.println("The given term does not exist in the corpus!");
				return;
			}
			
			if (docId==-1)
			{
				System.out.println("The given document name does not exist in the corpus!");
			    return;
			}
			
			System.out.println("Inverted list for term: "+args[1]);
			System.out.println("In Document: "+args[3]);
			System.out.println("Term Id: "+termId);
			System.out.println("Doc Id: "+docId);
			
			TermDocMetaData m=getTermDocMetaData(termId, docId);
			
			if (m==null)
			{
				System.out.println("The given term does not exist in the given document!");
				return;
			}
			System.out.println("Term Frequency In Doc: "+m.termFrequencyInDoc);
			
			System.out.print("Positions: ");
			for (int i=0; i<m.positions.size(); i++)
			{
				System.out.print(m.positions.get(i));
			    
				if (i<m.positions.size()-1)
				{
					System.out.print(", ");
				}
			}
		}
		
		//--doc DOCNAME
		else if (args[0].equals("--doc"))
		{
			System.out.println("Listing for Document: "+args[1]);
			int docId= getDocId(args[1]);
			if (docId==-1)
			{
				System.out.println("The given document name does not exist in the corpus!");
				return;
			}
			
			System.out.println("DOCID: "+docId);
			DocMetaData m=getDocMetaData(docId);
			System.out.println("Distinct Terms: "+m.distinctTerms);
			System.out.println("Total Terms: "+m.totalTerms);
		}
		// --term TERM
		else if (args[0].equals("--term") && args.length==2)
		{
			System.out.println("Inverted List for term: "+args[1]);
			
			int termId=getTermId(args[1]);
		    
			if (termId==-1)
			{
				System.out.println("The given term does not exist in the corpus!");
			    return;
			}
			else
			{
				System.out.println("Term Id: "+termId);
				
			
				TermMetaData m= getTermMetaDataUsingTermIndex(termId);
				if (m==null)
				{
					System.out.println("Invalid Term Id.");
				}
				else
				{
					System.out.println("No. of Documents Containing Term: "+m.noOfDocsInWhichTermAppeared);
					System.out.println("Term Frequency in Corpus: "+m.frequency);
					System.out.println("Inverted List Offset: "+m.offset);
				}
			}
		}
	}
	
	
	
	
	//function to get term offset in term_index.txt
	private static long getTermOffset(int termId)
	{
		long offset=-1;
		BufferedReader termInfoReader;
		try {
			
			termInfoReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\term_info.txt"));
			
				String line = termInfoReader.readLine();
				
			    while (line != null) {
			    	
			        String data[]=line.split("\t");
			      
			        if (Integer.parseInt(data[0])==termId)
			        {
			        	offset= Long.parseLong(data[1]);
			        	
			        }
			        line = termInfoReader.readLine();
					
			      }
			    
			    termInfoReader.close();
			    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return offset;
	
	}
	//function to find Meta Data of a term using term_index.txt (offset is found using term_info.txt)
		private static TermMetaData getTermMetaDataUsingTermIndex(int termId)
		{
			TermMetaData m=null;
		   long offset= 0;
   		   BufferedReader termIndexReader=null;
   		
		 	try
	        {
		 		   termIndexReader=new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\term_index.txt"));
		 	       	
                    offset=getTermOffset(termId); 
	    			        	
	    			termIndexReader.skip(offset);
	    			        	
	    			String []requiredLine=termIndexReader.readLine().split("\t");
	    			        	
	    			int totalDocs=0;
	    			int frequency=0;
	    			    
	    	
    			    frequency+=requiredLine.length-1;
    			        		
    	  //finding total number of documents in which the term appeared
	    for (int i=1; i<requiredLine.length; i++)
	     {
	    			                
	    	int nextDocID= Integer.parseInt(requiredLine[i].split(":")[0]);
	    			        		
	    	if (nextDocID!=0)
	          totalDocs++;
	     }
	    			        	
	    	m=new TermMetaData(offset, frequency, totalDocs);
	    			       
	    			        
		    termIndexReader.close();
	    			        
	   }
	    			     
	catch (IOException e) {
	    			
	     System.out.println("AN IO Exception occurred in getTermMetaDataUsingTermIndex function!");
	    			    
	   }
		 	
		 	return m;
			
		}
		
	//function to Read Meta Data of a term using TermInfo.txt file
	private static TermMetaData getTermMetaDataUsingTermInfo(int termId)
	{
		TermMetaData m=null;
	
	 	try
        {
    		BufferedReader termInfoReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\term_info.txt"));
    	
  				String line = termInfoReader.readLine();
  				
    			    while (line != null) {
    			    	
    			        String data[]=line.split("\t");
    			       
    			        if (Integer.parseInt(data[0])==termId)
    			        {
    			        	m=new TermMetaData(Long.parseLong(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
    			    
    			        	break;
    			        }
    			        
    			        line = termInfoReader.readLine();    			        
    			    }
    			    termInfoReader.close();
    			    
    	 }
	 	 catch (IOException e) {
    			
    			    System.out.println("AN IO Exception occurred in getTermMetaDataUsingTermInfo function while reading term_info.txt!");
    			    
    			   }
	 	
	 	return m;
		
	}
	
	
	private static TermDocMetaData getTermDocMetaData(int termId, int docId)
	{
		TermDocMetaData m=new TermDocMetaData();
	
		
		long termOffset= getTermOffset(termId);
		
		try {
			BufferedReader termIndexReader= new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\term_index.txt"));
		    
			termIndexReader.skip(termOffset);
		
			String line= termIndexReader.readLine();
			termIndexReader.close();
			
		     String data[]= line.split("\t");
		     
			for (int i=1; i<data.length; i++)
			{
				int docIdInIndex= Integer.parseInt(data[i].split(":")[0]);
			
			//	System.out.println("termid= "+data[0]+" DOC ID In Index: "+docIdInIndex);
				while (docIdInIndex!=docId)
				{
					if (docIdInIndex>docId)
						return null;
					docIdInIndex+=Integer.parseInt(data[i].split(":")[0]);
					i++;
				}
				
				int nextOffset=Integer.parseInt(data[i].split(":")[1]);
				m.termFrequencyInDoc++;
				m.positions.add(nextOffset);
				
				for (int j=i+1; Integer.parseInt(data[j].split(":")[0])==0; j++)
				{
					nextOffset= nextOffset+Integer.parseInt(data[j].split(":")[1]);
					m.termFrequencyInDoc++;
					m.positions.add(nextOffset);
					
					
				}
				return m;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	//function to find doc id of a document using docids.txt
	private static int getDocId(String doc)
	{
		int retValue=-1;
	 	try
        {
	 		
    		BufferedReader docIdsReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\docids.txt"));
    	
  				String line = docIdsReader.readLine();
  				
    			    while (line != null) {
    			     
    			        String data[]=line.split("\t");
    			        
    			        if (doc.equals(data[1]))
    			        {
    			        	retValue=Integer.parseInt(data[0]);
    			        	break;
    			        }
    			        
    			        line = docIdsReader.readLine();     
    			        
    			    }
    			    docIdsReader.close();
    			   
    			    
    	 }
	 	 catch (IOException e) {
    			
    			    System.out.println("AN IO Exception occurred in getDocId function while reading docids.txt!");
    			    
    			   }
	 	
	        return retValue; 	        
	}
	
	
	//function to find the term id of a term using termIds.txt
	private static int getTermId(String term)
	{
		int retValue=-1;
	 	try
        {
	 		
    		BufferedReader termIdDocReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\termids.txt"));
    	
  				String line = termIdDocReader.readLine();
  				
    			    while (line != null) {
    			     
    			        String data[]=line.split("\t");
    			        stemmer.setCurrent(term.toLowerCase());
    			        stemmer.stem();
    			        
    			        if (stemmer.getCurrent().equals(data[1]))
    			        {
    			        	retValue=Integer.parseInt(data[0]);
    			        	break;
    			        }
    			        
    			        line = termIdDocReader.readLine();     
    			        
    			    }
    			    termIdDocReader.close();
    			   
    			    
    	 }
	 	 catch (IOException e) {
    			
    			    System.out.println("AN IO Exception occurred in getTermId function while reading termids.txt!");
    			    
    			   }
	 	
	        return retValue; 	        
	}
	
	
	//get document information (total terms and total unique terms) using doc_index.txt
	public static DocMetaData getDocMetaData(int docId)
	{
		DocMetaData m=new DocMetaData();
		try {
			BufferedReader docIndexReader=new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\doc_index.txt"));
	        String line=null;
	        
			while ((line=docIndexReader.readLine())!=null)
			{
			        String data[]=line.split("\t");
			        if (Integer.parseInt(data[0])==docId)
			        {
			               while (Integer.parseInt(data[0])==docId)
			               {
			            	   m.distinctTerms++;
			            	   
			            	   m.totalTerms+=data.length-2;
			            	   
			            	   data=docIndexReader.readLine().split("\t");
			               }
			               break;
			        }
			}
			
			docIndexReader.close();
			return m;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return m;
	}
}
