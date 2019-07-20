package assignment1;



public class Main {
	
	public static void main(String args[]) 
	{
	
		if (args.length==0)
		{
			System.out.println("Please pass the directory path");
		}
		//running part 1 of the assignment
		Tokenizer t=new Tokenizer(args[0]);
		t.TokenizeAndWriteToFiles();
	
		System.gc();
		
		//running part 2 of the assignment
		Indexer indexer=new Indexer();
		indexer.createInMemoryInvertedIndex();
		indexer.writeInvertedIndexToFile();
	
					
    }
}