import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

class Oblig4{

    //Read input file
    static void readfilesAndCombine(Model model, Model model2, String filename, String queryfile, String combinedFile) throws IOException{
        
        /*
        Read models and add url, for some reason using the url path for simpsons.ttl makes the program stall and not work...
        Therefore i have used java nio for reading the file at the desired url, saved it as local file and then read the model
        */
        
        URL website = new URL("https://www.uio.no/studier/emner/matnat/ifi/IN3060/v21/obliger/simpsons.ttl");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("simpsons.ttl");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        
        model.read(filename);
        model2.read("simpsons.ttl");
        fos.close();

        //Combine models and read SPARQL query
        Model combinationModel = ModelFactory.createRDFSModel(model, model2);
        Query query = QueryFactory.read(queryfile);
        QueryExecution execution = QueryExecutionFactory.create(query, combinationModel);
        Model newModel = execution.execConstruct();
        
        //Add the new information to the model
        combinationModel.add(newModel);
        execution.close();

        makeOutput(combinationModel, combinedFile);

    }

    //Make output file
    static void makeOutput(Model model, String filename){
        try{
            FileOutputStream w = new FileOutputStream(filename);
            String placeholder = filename.toString();
            int index = placeholder.lastIndexOf('.');
            String extension = filename.substring(index + 1);
            model.write(w, extension);
            
        } catch (Exception e){
            System.out.println("Something went wrong! No file was created\n");
        }
    }
    //MAIN
    /*
        First argument, args[0] = RDFS Model (family.ttl)
        Second argument, args[1] = SPARQL query (Oblig4.rq)
        Third argument, args[2] = Result file to write results from SPARQL query (results.ttl)
    */
    public static void main (String[] args){
        try{
            Model model = ModelFactory.createDefaultModel();
            Model urlModel = ModelFactory.createDefaultModel();

            System.out.println("Reading file.....\n");
            readfilesAndCombine(model, urlModel, args[0], args[1], args[2]);
            System.out.println("Making model.....\n");
            System.out.println("Creating output.....\n");
            System.out.println("Finished!\n");   
        
        } catch (Exception e) {
            System.out.println("ERROR, try again!\n");
            System.exit(0);
        }
    }
}