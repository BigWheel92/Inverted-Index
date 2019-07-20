package assignment1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.tartarus.snowball.ext.EnglishStemmer;


//Part 1 of the assignment
public class Tokenizer {

	int totalTerms=0;
	public class KeyPair implements Comparable{

	    private final int docId;
	    private final int termId;

	    public KeyPair( int docId, int termId) {
	        this.docId = docId;
	        this.termId = termId;
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof KeyPair)) return false;
	        KeyPair other= (KeyPair) o;
	        return this.docId == other.docId && this.termId == other.termId;
	    }
	    

	    @Override
	    public int hashCode() {
	        int result = (int)docId;
	        result = 31 * result + (int)termId;
	        return result;
	    }

		@Override
		public int compareTo(Object arg0) {
			
			if (arg0==null)
				return -1;
			
			KeyPair other=(KeyPair)arg0;
			if (this.docId== other.docId && this.termId==other.termId )
				return 0;
			
			else if (this.docId>other.docId)
			{
				return 1;
			}
			
			else if (this.docId<other.docId)
			{
				return -1;
			}
			else if (this.termId> other.termId)
			{
				return 1;
			}
			else return -1;
			
		}

	}
	
	private HashMap<String, String> stopWords;
    private ArrayList<String> fileNames;
    private HashMap<String, Integer> termsDictionary; //also contains mapping to termIds
    private TreeMap<KeyPair,ArrayList<Integer>> forwardIndex;
	private String directoryPath;
	
	//Constructor needs absolute path of directory
    Tokenizer(String directory)
    {
    	
    	this.directoryPath=directory;
    	 fileNames=FileHandler.getFileNamesFromDirectory(directory);
    	 stopWords=new HashMap<String, String>();
    	 termsDictionary=new HashMap<String, Integer>();
    	 forwardIndex=new TreeMap<KeyPair, ArrayList<Integer>>();
    	 readStopWords();
    }
    

    public void readStopWords()
    {
    	//reading stop list and creating its hash map
    	try
        {
    		BufferedReader stopListReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\src\\assignment1\\stoplist.txt"));
    	
  				String word = stopListReader.readLine();
  				
    			    while (word != null) {
    			    	
    			    	stopWords.put(word, null); 
    			        word = stopListReader.readLine();
    			        
    			    }
    			
    			    stopListReader.close();
    			    
    			}  catch (IOException e) {
    			
    			    System.out.println("AN IO Exception occurred in readStopWords Function in Tokenizer class!");
    			    
    			}
    	
              

    }
    
	public void TokenizeAndWriteToFiles()
	{
		BufferedWriter docIdWriter=null;
		BufferedWriter termIdWriter=null;
		BufferedWriter forwardIndexWriter=null;
		EnglishStemmer stemmer=new EnglishStemmer();
		int termId=1;
		
		//creating new file docids.txt
		try {
			docIdWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\docids.txt"));
		    termIdWriter=new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\termids.txt"));
	        forwardIndexWriter=new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\doc_index.txt"));
		} catch (IOException e) {
			System.out.println("An exception occurred while creating output files!");
		}
		

		int currentDocumentId=1;
		for (int i=0; i<fileNames.size(); i++)
		{
			System.out.println("Tokenizer is now processing Document No: "+(i+1)+". ("+fileNames.get(i)+")");
	   
			
			
			
		 //reading the document
         String extract=FileHandler.extractStringFromHTTP( directoryPath+"\\"+fileNames.get(i));
      
         if (extract==null)
        	 continue;
         String[] tokens = extract.split("[\\p{Punct}\\s]+"); //tokenizing //////////////////////[0-9] added afterwards//////////////////
         //doc.body().text().split("\\s*[^a-zA-Z]+\\s*");
         //"[\\p{Punct}\\s[0-9]]+"
       
         
         int currentPosition=1;
         boolean flag=false;
         for (int j=0; j< tokens.length; j++)
         {
        	   //removing non ASCII characters & changing to lower case
        	  tokens[j]=Normalizer.normalize(tokens[j], Normalizer.Form.NFD).replaceAll("[^\\x00-\\x7F]", "").toLowerCase();
        	  

        	  if (tokens[j].equals("") || tokens[j].length()<2)
       		   continue;
        	
        	    //Applying the Stemmer
            	stemmer.setCurrent(tokens[j]);
            	stemmer.stem();
            	
         	   
            	//removing stop words
          	  if (stopWords.containsKey(tokens[j]) || stopWords.containsKey(stemmer.getCurrent()))
          	  {
          		//  System.out.println("Stop Word Found: "+ tokens[j]+" "+i);
          		  continue; //ignore the word
          		 
          	  }
          	  
          
               //adding to dictionary
            	if (termsDictionary.containsKey(stemmer.getCurrent()))
            	{
            		//the word is already in the dictionary, so do not end it again 
            		//(neither in dictionary nor in termIds.txt).
            	}
            	else
            	{
            		//add to termsDictionary as well as termids.txt 
            		try {
						termIdWriter.write((termId++) + "\t" +stemmer.getCurrent()+"\r\n");
						termsDictionary.put(stemmer.getCurrent(), (termId-1));
						totalTerms++;
					} catch (IOException e) {
					
						System.out.println("An error Occurred while writing to termids.txt!");
					}
            		
            	}
            		
            	//docid, term id pair as key
            
            	KeyPair key=new KeyPair(currentDocumentId,  termsDictionary.get(stemmer.getCurrent()));
            if( forwardIndex.containsKey(key))
            {
            	
            }
            else
            {
            	flag=true;
            	forwardIndex.put(key, new ArrayList<Integer>());
            }
            
            forwardIndex.get(key).add(currentPosition++);
            	
           
           
         } //end of token reading of one file  loop
       
         //writing document id to docids.txt
         if (flag==true)
			 try {
				docIdWriter.write((currentDocumentId++)+"\t"+fileNames.get(i)+ (i==fileNames.size()-1 ? "" : "\r\n"));
				flag=false;
				//System.out.println(fileNames.get(i));
			} catch (IOException e) {
				System.out.println("An exception occurred while writing to docids.txt!");
			}
     
         
         //writing forward index to disk
         Iterator it = forwardIndex.entrySet().iterator();
		 while (it.hasNext()) {
        	 Map.Entry pair = (Map.Entry)it.next();
        	 KeyPair keyPair= (KeyPair)pair.getKey();
        	 ArrayList<Integer> value= (ArrayList<Integer>) pair.getValue();
             try {
				forwardIndexWriter.write(keyPair.docId+"\t"+keyPair.termId);
				
				
				 for (int k=0; k<value.size(); k++)
	             {
	            	 forwardIndexWriter.write("\t"+value.get(k));
	            	 
	             }
				
				
				 it.remove(); // avoids a ConcurrentModificationException
			    if (it.hasNext()==false && i==fileNames.size()-1)//preventing the new line at the end of file
			    	break;
				 forwardIndexWriter.write("\r\n");
			} catch (IOException e) {
				System.out.println("An exception occurred while writing to doc_index.txt");
			}
	
        
	
	  }
		  
             
            	 
   }//end of fileNames.size() loop (all files reading one by one)
   try {
			
	     BufferedWriter countWriter=new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"\\src\\assignment1\\counter.txt"));
		 countWriter.write(totalTerms+"\t"+(currentDocumentId-1));
	     docIdWriter.close();
		 termIdWriter.close();
		 forwardIndexWriter.close();
		 countWriter.close();
	   }
       catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} //end of Tokenize function
		
	
}
