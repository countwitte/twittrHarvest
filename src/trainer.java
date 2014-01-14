import java.io.File;
import java.io.IOException;
import com.aliasi.classify.*;
import com.aliasi.util.*;
import com.aliasi.corpus.*;


public class trainer {

	static String usage = "USAGE:trainer -d DIRECTORY [-c FILENAME]\n\n-d DIRECTORY		Parent Directory of Categories for Classification" +
			"(default .\\trainDirectory)\n-c FILENAME		specify filename of classifier output (.\\classifier.txt)";
	static File trainDir = null;
	static String Classifier  = null;
	public static void main(String[] args) {
		
		
		// process cmd line args
		if (args.length==0) {
			System.out.println(usage);
			System.exit(0);
		} else for (int i=0;i<args.length;i++) {
			if (args[i].contains("-d"))
				trainDir = new File(args[i+1]);
		 else if (args[i].contains("-c"))
			Classifier = new String(args[i+1]);

		try {
			train();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	}
static void train() throws IOException, ClassNotFoundException  { 
		String[] categories; 
		DynamicLMClassifier mClassifier;  
		categories = trainDir.list(); 
		int nGram = 8; //the nGram level, any value between 7 and 12 works
		
		// check for null input/output args
		if (trainDir==null) {
			trainDir = new File("trainDirectory");
		}
		if (Classifier==null) {
			Classifier = "classifier";
		}
		mClassifier = DynamicLMClassifier.createNGramProcess(categories, nGram);
		for (int i = 0; i < categories.length; ++i) { 
			String category = categories[i];
			Classification classification = new Classification(category);
			File file = new File(trainDir, categories[i]); 
			File[] trainFiles = file.listFiles(); 
			for (int j = 0; j < trainFiles.length; ++j) { 
				File trainFile = trainFiles[j]; 
				String review = Files.readFromFile(trainFile, "ISO-8859-1"); 
				Classified classified = new Classified(review, classification); 
				((ObjectHandler) mClassifier).handle(classified);
				} 
			} 
		AbstractExternalizable.compileTo((Compilable) mClassifier, new File(Classifier + ".txt"));
		}
		
	}