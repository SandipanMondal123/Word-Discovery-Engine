package lse;
//AUTHOR: SANDIPAN MONDAL 
import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> table = new HashMap<String,Occurrence>();
        Scanner reader = new Scanner(new File(docFile));
        String wrd;

        while(reader.hasNext())
        {
            wrd = getKeyword(reader.next());

            if(wrd!=null)
                if(!(table.containsKey(wrd)))
                    table.put(wrd, new Occurrence(docFile, 1));
                else
                    table.get(wrd).frequency = table.get(wrd).frequency + 1;    
        }
       
        return table;
    }
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
        for(String x: kws.keySet())
        {

            ArrayList<Occurrence> newList = new ArrayList<Occurrence>();
            boolean holder = keywordsIndex.containsKey(x);
            if(!holder)
            {
                newList.add(kws.get(x));
                keywordsIndex.put(x, newList);
            }
            else if(holder)
            {
                keywordsIndex.get(x).add(kws.get(x));
                insertLastOccurrence(keywordsIndex.get(x));
            }

        }


    }

	
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		for(int x = word.length()-1; x>=0; x--)
        {
            if(!condition(word.charAt(x)))
                word = word.substring(0,x);
            else 
                x=-1;
        }
         word = word.toLowerCase();
        int x = 0;
        while(x<word.length())
        {
            int ascii_value = (int)word.charAt(x);
            if(!(ascii_value >= 97  && ascii_value <= 122))
                return null;
            x++;
        }

        if(noiseWords.contains(word)) 
            return null;
        
        return word;
    }

    private static boolean condition(char indentifier)
    {
        if(indentifier == '.')
            return false;
        if(indentifier == ',')
            return false;
        if(indentifier == '?')
            return false;
        if(indentifier == ':')
            return false;
        if(indentifier == ';')
            return false;
        if(indentifier == '!')
            return false;
        return true;
    }

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size()==1)
        return null;
    ArrayList<Integer> indexes = new ArrayList<Integer>();
    int low_index = 0;
    int high_index = occs.size()-2;
    int mid_index = (int) (low_index + high_index)/2;
    int value =-1;

    while(low_index<=high_index)
    {
        indexes.add(mid_index);
        if(occs.get(mid_index).frequency < occs.get(occs.size()-1).frequency)
        {
              high_index = mid_index - 1;
              value = 0;
        }
          
        else if(occs.get(mid_index).frequency > occs.get(occs.size()-1).frequency)
        {
            low_index = mid_index + 1;
            value = 1;
        }   
        else if(occs.get(mid_index).frequency == occs.get(occs.size()-1).frequency)
        {
            value = 2;
            break;
        }
          
        mid_index = (int) (low_index + high_index)/2;       
    }
    
        int random=(high_index+(low_index+1))/2;
        int sub = random;
        if(sub < occs.size()-1)
            for(int x = sub; x <occs.size()-1; x++)
            {
                if(occs.get(1+random).frequency == occs.get(random).frequency )
                    random++;
                else
                    break;
            }
           
        occs.add(random, occs.get(occs.size()-1));
        occs.remove(occs.size()-1);
        return indexes;
        
    }
    


	
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) 
	{
		ArrayList<String> docs = new ArrayList<String>();
        ArrayList<Occurrence> holder = new ArrayList<Occurrence>();
        ArrayList<Occurrence> kw1Array = keywordsIndex.get(kw1.toLowerCase());
        ArrayList<Occurrence> kw2Array = keywordsIndex.get(kw2.toLowerCase());
        
        boolean kw1cond = kw1Array == null;
        boolean kw2cond = kw2Array == null;

        int current = 0;

        if(kw1cond && kw2cond)
            return null;

		if(!kw1cond && !kw2cond)
		{ 
			int max=0;String documentName="";
			holder.addAll(kw1Array);
			holder.addAll(kw2Array);
				 
			int min=0;
		 	while(current<5)
			{
			   int x = 0;
			   while(x < holder.size())
			   {
					if(holder.get(x).frequency>max)
					{
						documentName=holder.get(x).document;
						max=holder.get(x).frequency;
						min=x;
					}
						 
				x++;
				}
				
				if(!(docs.contains(documentName)))
				{
					docs.add(documentName);
					current++;
				}   
					holder.remove(min);
					max = 0;
					if(holder.size()==0)
						break;
			}
		 
		}else if(!kw1cond)
			 {
				for(int x = 0; x < kw1Array.size(); x++)
				{
					docs.add(kw1Array.get(x).document);
					current++;
					if(current == 5)
						x = kw1Array.size();
	 
				}
			 }else
			 {
				for(int x = 0; x < kw2Array.size(); x++)
				{
					docs.add(kw2Array.get(x).document);
					current++;
					if(current == 5)
						x = kw2Array.size();
	 
				}
			 }
			 return docs;
	}
}
