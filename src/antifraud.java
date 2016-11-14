import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;

public class antifraud{

    // Constants
    private static final String DELIMITER = ", ";

    public static void main(String args[]){


        // File paths from command line args.
        String file_input_batch = args[0];
        String file_input_stream = args[1];
        String directory_output = args[2];


        // Construct Graph 
        Graph userGraph = new Graph();


        // Processes Batch File
        // Returns Current State Of Graph
        userGraph = buildCurrentState(userGraph, file_input_batch);


        // Processes Stream File
        // Writes to Output Files
        // Returns final state of graph.
        userGraph = streamValidateTransactions(userGraph, file_input_stream, directory_output);


        // Command Line Notification for Finished Process
        System.out.println("Stream Processing Finished. Output in: " + directory_output);

    }

    /**
     * Processes batch transaction file to build graph.
     *
     * @param input_file - the file to be processed to establish network
     * @return userGraph object - the "current state" of user network
     */

    public static Graph buildCurrentState(Graph userGraph, String input_file){

        // Open text Files
        try (BufferedReader br = new BufferedReader(new FileReader(input_file))) {
            String line;

            // Skip first line
            br.readLine();

            

            // Iterate through, repeat until no more lines to read
            while ((line = br.readLine()) != null) {

                // Add unweighted edges to graph based on user ID
                // See addEdge method for how various cases are handled
                userGraph.addEdge(parseLine(line));
            }
        } catch (FileNotFoundException y){
            System.out.println(y);
        } catch (IOException x){
            System.out.println(x);
        } 

        return userGraph;
    }

    /**
     * Processes stream transaction file, writes 
     *
     * @param file_input_stream - file path for 
     * @param file_output - file path for where we are writing.
     * @return userGraph object - the "final state" of user network
     */

    public static Graph streamValidateTransactions(Graph userGraph, String file_input_stream, String output_directory){

            try (BufferedReader br = new BufferedReader(new FileReader(file_input_stream))) {
            String line;
            // Skip first line
            br.readLine();

            // Set up output files for writing.
            // First File
            File output1 = new File(output_directory +  "output1.txt");
            output1.createNewFile();
            FileWriter fw1 = new FileWriter(output1.getAbsoluteFile());
            BufferedWriter bw1 = new BufferedWriter(fw1);
            // Second File
            File output2 = new File(output_directory + "output2.txt");
            output2.createNewFile();
            FileWriter fw2 = new FileWriter(output2.getAbsoluteFile());
            BufferedWriter bw2 = new BufferedWriter(fw2);
            // Third File
            File output3 = new File(output_directory + "output3.txt");
            output3.createNewFile();
            FileWriter fw3 = new FileWriter(output3.getAbsoluteFile());
            BufferedWriter bw3 = new BufferedWriter(fw3);


            while ((line = br.readLine()) != null) {
                String[] edge = parseLine(line);

                // If Edge exists on Graph
                if (userGraph.edgeExists(edge)) {
                    // Users are friends - edge distance is 1
                    reportResults(1, bw1, bw2, bw3);

                // If Edge does not exist on Graph
                } else {
                    // Report distance between nodes.
                    reportResults(userGraph.getDistance(edge), bw1, bw2, bw3);
                    // Create Edge
                    userGraph.addEdge(edge);
                }

            }
        } catch (FileNotFoundException y){
            System.out.println(y);
        } catch (IOException x){
            System.out.println(x);
        }   

        return userGraph;
    }

    public static String[] parseLine(String line){
        return Arrays.copyOfRange(line.split(DELIMITER), 1, 2);

    }

    public static void reportResults(Integer distance, BufferedWriter bw1, BufferedWriter bw2, BufferedWriter bw3) throws IOException{
        if (distance == 1 || distance == 0){ 
        // user paying friend or herself
            bw1.write("verified");
            bw2.write("verified");
            bw3.write("verified");
        } else if (distance == 2){ 
        // user paying friend of friend
            bw1.write("unverified");
            bw2.write("verified");
            bw3.write("verified");
        } else if (distance == 3 || distance == 4){ 
        // user paying 3rd or 4th degree connection
            bw1.write("unverified");
            bw2.write("unverified");
            bw3.write("verified");
        } else { 
        // user paying 5th+ degree connection
            bw1.write("unverified");
            bw2.write("unverified");
            bw3.write("unverified");
        }

    }
    
}