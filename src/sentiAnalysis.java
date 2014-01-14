import java.io.IOException;


public class sentiAnalysis {

	/**
	 * executes query on twitter API using twittermanager from twitter4j libs
	 * if training -t isn't given then also carries out sentiment analysis using LingPipe libs
	 */
	public static int main(String[] args) {
		String query = null;
		int t = 0;
		String classifier = null;
		String usage = "USAGE: sentiAnalysis [-t] [-c FILE] QUERY\nSupply a query formatted for the twitter search api. e.g. #tag+exclude:retweets" +
				"\n(Optional) -t		Create trainingset.txt from query\n-c FILE		specify classifier file";		
		
		if (args.length == 0) {
			System.out.println(usage);
			System.exit(0);
		} else for (int i=0;i<args.length;i++) {
			if (args[0].contains("-t")) {
				t=1;
			} else if (args[i].contains("-c")) {
				classifier = args[i+1];
			}
		}
		query = args[args.length-1];
		TwitterManager twitterManager = new TwitterManager(classifier);
		if (t==1) {
			twitterManager.setT(1);
		}
		try {
			
			twitterManager.performQuery(query);
			System.out.printf("Results:\nNo. pos: %d\nNo. neg: %d\nNo. neu: %d\n", twitterManager.getPosCount() 
					,twitterManager.getNegCount(), twitterManager.getNeuCount());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
