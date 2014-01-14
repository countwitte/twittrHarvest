import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.nio.*;


public class manualClassifier {

	/**
	 * This class allows the user to read one tweet at a time from the training selection and manually
	 * classify the sentiment - this is used to train the classifier later
	 * 
	 */
	public static void main(String[] args) {
		
		BufferedReader in; // read from training file
		Console con = System.console();
		int posNo = 0; // track number of postives
		int negNo = 0; // track number of negatives
		int neuNo = 0; // track number of neutrals
		String text; // tweet content
		String input; // console input from user
		File trainingData = null; // place to store all tweets once classified
		File pos = null;  // place to store postive tweets once classified
		File neg = null; // place to store negative tweets once classified
		File neu = null; // place to store neutral tweets once classified
		PrintWriter p; // printwrter for creating classfied tweet txt files
		File trainingSet = null;
		int lineNo = 0; // Line Number in training file
		
		// Usage text
		String usage = new String("USAGE: manualClassifier -d DIRECTORY -f FILE\n-d DIRECTORY		Parent Directory for Sentiment Categories (default .\trainingData)\n" +
				"-f FILE		training set file (default .\trainingset.txt)\n\nWhen prompted enter sentiment of tweet:\n" +
				"1 = Positive\n0 = Negative\n9 = Neutral");
		System.out.println(usage);
		System.out.println();
		
		// process command line args
		if (args.length==0) {
			System.out.println(usage);
			System.exit(0);
		} else {
			for (int i = 0; i < args.length; i++) {
				if (args[i].contains("-d")) {
					trainingData = new File(args[i+1]);
					pos = new File(trainingData.getAbsolutePath().concat(File.separator + "pos"));
					neg = new File(trainingData.getAbsolutePath().concat(File.separator + "neg"));
					neu = new File(trainingData.getAbsolutePath().concat(File.separator + "neu"));
				} else if (args[i].contains("-f")) {
					trainingSet = new File(args[i+1]);
				}
			}
		}
		// set defaults if no args specified
		if (trainingData==null) {
			trainingData = new File("trainingData");
			pos = new File(trainingData.getAbsolutePath().concat(File.separator + "pos"));
			neg = new File(trainingData.getAbsolutePath().concat(File.separator + "neg"));
			neu = new File(trainingData.getAbsolutePath().concat(File.separator + "neu"));
		}
		if (trainingSet==null) {
			trainingSet= new File("trainingset.txt");
		}
		
		// create directories if needed
		trainingData.mkdir();
		pos.mkdir();
		neg.mkdir();
		neu.mkdir();
		
		try {
			in = new BufferedReader(new FileReader(trainingSet));
		// while training file has next line process tweets
		while ((text = in.readLine()) != null) {
			lineNo++;
			input = "";
			String t = "";
			System.out.println(text); // print tweet to user
			input = con.readLine("Sentiment: "); // prompt for user sentiment classification
			
			if (input.contains("exit")) {
				File temp = File.createTempFile((new String(getDateTime())), ".tmp");
				p = new PrintWriter(temp);
				while ((text = in.readLine()) != null) {
						//System.out.println("am i here?");
						p.write(text);
						p.write("\n");
						}
				in.close();
				p.close();
				trainingSet.delete();
				boolean successful = temp.renameTo(trainingSet);
				System.out.println("new training file created: " + successful);
				// print totals for each classification
				System.out.printf("Results:\nNo. pos: %d\nNo. neg: %d\nNo. neu: %d\n", posNo,negNo,neuNo);
				System.exit(0);
			}
			else if (input.contains("1")) { // positive sentiment
				p = new PrintWriter(pos.getAbsolutePath().concat(File.separator + lineNo + (t = getDateTime()) + ".txt"));
				posNo++;
				p.write(text);
				p.close();
			} else if (input.contains("0")) { // negative sentiment
				p = new PrintWriter(neg.getAbsolutePath().concat(File.separator + lineNo + (t = getDateTime()) + ".txt"));
				negNo++;
				p.write(text);
				p.close();
			} else if (input.contains("9")) { // neutral sentiment
				p = new PrintWriter(neu.getAbsolutePath().concat(File.separator + lineNo + (t = getDateTime()) + ".txt"));
				p.write(text);
				neuNo++;
				p.close();
			} else
				System.out.println("Sentiment not recognised.");
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// print totals for each classification
		System.out.printf("Results:\nNo. pos: %d\nNo. neg: %d\nNo. neu: %d\n", posNo,negNo,neuNo);
	}
	
	// helper method getting time stamp for tweet filenames
	private final static String getDateTime()
	{
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
	    df.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return df.format(new Date());
	}
}
