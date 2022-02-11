import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

class Simpsons{

    //Read input file
    static void readfile(Model model, String filename){
        //Read file submitted
        model.read(filename);
        
        //Create prefixes
        String sim = model.getNsPrefixURI("sim"); //Get from model
        String rdf = model.getNsPrefixURI("rdf");
        String fam = model.getNsPrefixURI("fam"); //Get from model
        String foaf = model.getNsPrefixURI("foaf");

        //Create names
        Resource Maggie = model.createResource(sim+"Maggie");
        Resource Mona = model.createResource(sim+"Mona");
        Resource Abraham = model.createResource(sim+"Abraham");
        Resource Herb = model.createResource(sim+"Herb");

        //Create class & predicates
        Resource Person = model.createResource(foaf+"Person");
        Property type = model.createProperty(rdf+"type");
        Property age = model.createProperty(foaf+"age");
        Property name = model.createProperty(foaf+"name");
        Property hasSpouse = model.createProperty(fam+"hasSpouse");
        Property hasFather = model.createProperty(fam+"hasFather");
        
        //Add properties and literals to the model
        Maggie.addProperty(type, Person);
        Maggie.addLiteral(age, (Integer) 1);
        Maggie.addProperty(name, "Maggie Simpson");

        Mona.addProperty(type, Person);
        Mona.addLiteral(age, (Integer) 70);
        Mona.addProperty(name, "Mona Simpson");
        Mona.addProperty(hasSpouse, Abraham);

        Abraham.addProperty(type, Person);
        Abraham.addLiteral(age, (Integer) 78);
        Abraham.addProperty(name, "Abraham Simpson");
        Abraham.addProperty(hasSpouse, Mona);

        Herb.addProperty(type, Person);
        Herb.addProperty(hasFather, model.createResource());

        //Set age property with corresponding values; infant, minor and old
        ResIterator r = model.listSubjectsWithProperty(type, Person);
        while(r.hasNext()){
            Resource subprop = r.next();

            if(subprop.hasProperty(age)){
                int AGE = subprop.getProperty(age).getInt();
                
                if(AGE < 2){
                    subprop.addProperty(type, model.createResource(fam+"Infant"));
                }

                if(AGE < 18){
                    subprop.addProperty(type, model.createResource(fam+"Minor"));
                }
                
                if(AGE > 70){
                    subprop.addProperty(type, model.createResource(fam+"Old"));
                }
            }
        }
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
    public static void main (String[] args){
        try{
            Model model = ModelFactory.createDefaultModel();
            System.out.println("Reading file.....\n");
            readfile(model, args[0]);
            System.out.println("Making model.....\n");
            System.out.println("Creating output.....\n");
            makeOutput(model, args[1]);
            System.out.println("Finished!\n");
        
        } catch (Exception e) {
            System.out.println("ERROR, try again!\n");
            System.exit(0);
        }
    }
}