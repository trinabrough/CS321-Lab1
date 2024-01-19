import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**This is the driver class for the Cache class. It will create a one-level or two-level cache depending on the user's input
 * at the command line. If the user wants a one-level cache, these are the accepted parameters:
 * @param
 *  1 or 2: 1 to create a single-level cache (if the user enters 1, the following parameters must be included)
 *  size of cache: the number of objects that the cache can store at one time
 *  input textfile name: the textfile that Test will parse and store in the cache. Test will parse using space as a delimiter.
 * If the user wants a two-level cache, these are the accepted parameters (from the command line):
 * @param
 *  1 or 2: 2 to create a two-level cache (use the user enters 2, the following parameters must be included)
 *  size of cache 1: the number of objects that cache 1 can store at one time
 *  size of cache 2: the number of objects that cache 2 can store at one time (cache 2 must be bigger than cache 1)
 *  input textfile name: the textfile that Test will parse and store in the cache. Test will parse using space as a delimiter.
 *  
 * This class parses the textfile and stores the data in the cache(s). It then reports to the console the following statistics:
 *  
 * number of global references: total number of items searched for in the cache. This will be the same as the total number of
 *  objects in the textfile
 * number of global cache hits: total number of times the object was found stored in the cache(s).
 * global hit ratio: global hits / global references
 * 
 * number of 1st-level references: total number of items searched for in cache 1. (Will equal global ref because will always search in cache 1).
 * number of 1st-level cache hits: number of times the object was found stored in cache 1.
 * 1st-level cache hit ratio: 1st hits / 1st references
 * 
 * number of 2nd-level references: total number of items searched for in cache 2.
 * number of 2nd-level cache hits: number of times the object was found stored in cache 2.
 * 2nd-level cache hit ratio: 2nd hits / 2nd references
 * 
 * @author Trina Brough Spring 2024
 */

public class Test {
    //declare global vars
    static long numCache1References = 0;
    static long numCache1Hits = 0;
    static long numCache2References = 0;
    static long numCache2Hits = 0;
    

    public static void main(String[] args) {

        //INPUT VERIFICATION
        //Check number of parameters
        if (args.length != 3 && args.length != 4){ //we must have 3 or 4 parameters from the command line
            printUsage(); //print usage and quit
        }
        //Verify first param is 1 or 2
        int cacheLevel = 0;
        try {
            cacheLevel = Integer.parseInt(args[0]); //convert first parameter to int
        } catch (Exception e) {
            printUsage(); //print usage and quit if can't convert first param to an integer
        }
        if (cacheLevel != 1 && cacheLevel !=2){ //can only create 1 or 2 level cache
            printUsage(); //print usage and quit
        }
        //Verify for a 1 cache system: 3 params passed, second param is an int, third param is a file
        FileInputStream fileInputStr = null;
        int cache1Capacity = 0;
        int cache2Capacity = 0;
        if (cacheLevel == 1){ //Level-one cache, verify the 3 parameters and have a valid capacity
            if (args.length != 3){ 
                printUsage(); //print usage and quit
            } else {
                try {
                    cache1Capacity = Integer.parseInt(args[1]);
                    fileInputStr = new FileInputStream(args[2]);
                } catch (Exception e){
                    printUsage(); //print usage and quit if can't turn capacity into an int or can't connect to file given
                }
            }
        //Verify for a 1 cached system: 4 params passed, second and third params are int, fourth param is a file, cache1 capacity < cache2 capacity
        } else { //Level-two cache, verify have 4 parameter, valid cache capacities and that cache1 capacity is < cache2 capacity
            if (args.length != 4){
                printUsage(); //print usage and quit  
            } else {
                try {
                    cache1Capacity = Integer.parseInt(args[1]);
                    cache2Capacity = Integer.parseInt(args[2]);
                    fileInputStr = new FileInputStream(args[3]);
                } catch (Exception e){
                    printUsage(); //print usage and quit if can't turn capacities into integers or can't connect to file given
                }
                if (cache1Capacity >= cache2Capacity){
                    printUsage(); //print usage and quit if given incorrect capacity inputs 
                }
            }
        }

        long startTime = System.currentTimeMillis();
           
        //CREATE CACHE(S)
        Cache<String> cache1;
        Cache<String> cache2 = null;
        cache1 = new Cache<String>(cache1Capacity);
        System.out.println("Created cache 1 with capacity: " + cache1Capacity);
        if(cacheLevel == 2){
            cache2 = new Cache<String>(cache2Capacity);
            System.out.println("Created cache 2 with capacity: " + cache2Capacity);
        }
        


        //PARSE TEXTFILE
        StringTokenizer strToken;
        String DELIMITER = " ";
        String line, word;
        long wordCount = 0;
        
        try{
            InputStreamReader inputStrmRdr = new InputStreamReader(fileInputStr);
            BufferedReader bufferRdr = new BufferedReader(inputStrmRdr);
            while ((line = bufferRdr.readLine()) != null){
                strToken = new StringTokenizer(line, DELIMITER, false); //splits line by DELIMITER, false means don't grab the DELIMITER
                while (strToken.hasMoreTokens()){
                    wordCount++;
                    if((wordCount % 5000) == 0){
                        System.out.print("."); //create a visual indicator that this is doing something
                    }
                    word = strToken.nextToken();
                    numCache1References++; //increment cache 1 reference counter
                    //search for item in cache1
                    if(cache1.findAndRemove(word)){ //found the word in cache1 - move hit data to top of both caches
                        numCache1Hits++; //increment cache 1 hit counter
                        cache1.addToTop(word);
                        if (cacheLevel == 2){ //there is a second-level cache - move this hit data to top of cache2 as well
                            if (cache2.findAndRemove(word)){ //found in cache 2
                                cache2.addToTop(word);
                            }
                        }
                    } else { //did not find word in cache1 - search in cache 2 (if there is one)
                        if (cacheLevel == 2){ //two caches so search in cache 2
                            numCache2References++; //increment cache 2 reference counter
                            if(cache2.findAndRemove(word)){ //found the word in cache2
                                numCache2Hits++; //increment cache 2 hit counter
                                cache2.addToTop(word); //move hit data to top of cache 2
                                cache1.addToTop(word);//add item to top of cache 1

                            } else { //not found in cache 2 - add to top of both cache 1 and cache 2
                                cache1.addToTop(word);
                                cache2.addToTop(word);
                            }
                        } else { //only 1 cache and didn't find it -- add to top
                            cache1.addToTop(word);
                        }
                    }
                }
            }
            inputStrmRdr.close();
            bufferRdr.close();
            fileInputStr.close();
        } catch (Exception e){
            //parsing error - how to handle this?
            System.out.println("Parsing error.");
            System.exit(1);
        }


        //PRINT OUTPUT
        double hitRatio;
        if (cacheLevel == 2) { //2 cache levels
            System.out.println();
            System.out.println("The number of global references: " + numCache1References);
            System.out.println("The number of global hits: " + (numCache1Hits + numCache2Hits));
            hitRatio = (double)(numCache1Hits + numCache2Hits) / numCache1References;
            System.out.println("The global hit ratio: " + hitRatio);
            System.out.println();

            System.out.println("The number of 1st-level references: " + numCache1References);
            System.out.println("The number of 1st-level cache hits: " + numCache1Hits);
            hitRatio = (double)numCache1Hits / numCache1References;
            System.out.println("The 1st-level cache hit ratio: " + hitRatio);
            System.out.println();

            System.out.println("The number of 2nd-level references: " + numCache2References);
            System.out.println("The number of 2nd-level cache hits: " + numCache2Hits);
            hitRatio = (double)numCache2Hits / numCache2References;
            System.out.println("The 2nd-level cache hit ratio: " + hitRatio);
            System.out.println();
            
        
        } else { //only one cache
            System.out.println("The number of cache references: " + numCache1References);
            System.out.println("The number of cache hits: " + numCache1Hits);
            hitRatio = (double)numCache1Hits / numCache1References;
            System.out.println("The cache hit ratio: " + hitRatio);
            System.out.println();

        }




        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time: " + totalTime);



    }
        
        

        
    
       

    /**Utility method: printUsage to screen */
    private static void printUsage(){
        System.out.println("Usage: Test 1 <cache capacity> <textfile to parse> OR");
        System.out.println("Usage: Test 2 <cache 1 size> <cache 2 capacity> <textfile to parse>");
        System.exit(1);
    }


}


    
