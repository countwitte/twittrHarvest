import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class verifier {

	/**
	 * this class offers a manual verifier for sentiments captured through our automated sentiment analysis
	 */
	public static void main(String[] args) {
	
		// variables	
		BufferedReader in;
		Console con = System.console();
		int posNo = 0;
		int negNo = 0;
		int neuNo = 0;
		String text;
		String input;
		File trainingData = new File("trainingData");
		trainingData.mkdir();
		File pos = new File("pos");
		File neg = new File("neg");
		File neu = new File("neu");
		pos.mkdir();
		neg.mkdir();
		neu.mkdir();
		PrintWriter p;
		int lineNo = 0;
		try {
			in = new BufferedReader(new FileReader("trainingset.txt"));
	
		// print one line of file and prompt for user input	
		while ((text = in.readLine()) != null) {
			lineNo++;
			input = "";
			System.out.println(text);
			input = con.readLine("Sentiment: ");
			
			if (input.contains("pos")) {
				p = new PrintWriter(pos.getAbsolutePath().concat(lineNo + ".txt"));
				posNo++;
			} else if (input.contains("neg")) {
				p = new PrintWriter(neg.getAbsolutePath().concat(lineNo + ".txt"));
				negNo++;
			} else {
				p = new PrintWriter(neu.getAbsolutePath().concat(lineNo + ".txt"));
				neuNo++;
			}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// print out total numbers	
		System.out.printf("Results:\nNo. pos: %d\nNo. neg: %d\nNo. neu: %d\n", posNo,negNo,neuNo);
	}
	

}
