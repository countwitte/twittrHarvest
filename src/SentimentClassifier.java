import com.aliasi.classify.*;
import com.aliasi.util.*;
import java.io.*;

public class SentimentClassifier {
	String[] categories; 
	LMClassifier clas; 
	public SentimentClassifier(String classifier) { 
		try { 
			clas= (LMClassifier) AbstractExternalizable.readObject(new File(classifier));
			categories = clas.categories();
			} catch (ClassNotFoundException e) { 
				e.printStackTrace();
					} catch (IOException e) { e.printStackTrace(); 
						} 
		} 
	
	public String classify(String text) { 
		ConditionalClassification classification = clas.classify(text);
		return classification.bestCategory();
		}
	}
