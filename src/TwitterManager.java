import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import twitter4j.*;
import twitter4j.conf.*;
import java.io.*;

	
	/*
	 * creates a TwitterManager object using twitter4j libraries 
	 *
	 */


public class TwitterManager { 
	SentimentClassifier sentClassifier; 
	int LIMIT= 10000; //the max number of retrieved tweets 
	ConfigurationBuilder cb; 
	Twitter twitter; 
	PrintWriter p;
	int posCount=0;
	int negCount=0;
	int neuCount=0;
	int train = 0;
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public TwitterManager(String classifier) { 
		cb = new ConfigurationBuilder(); 
		cb.setOAuthConsumerKey("xxxxxx"); 
		cb.setOAuthConsumerSecret("xxxxxxx"); 
		cb.setOAuthAccessToken("xxxxxxx"); 
		cb.setOAuthAccessTokenSecret("xxxxxxxx"); 
		twitter = new TwitterFactory(cb.build()).getInstance();
		if (classifier!=null) {
			sentClassifier = new SentimentClassifier(classifier);
			}
		} 
	
	public void performQuery(String inQuery) throws InterruptedException, IOException { 
		Query query = new Query(inQuery); 
		query.setCount(100); 
		if (train==1) { 
		p = new PrintWriter("trainingset.txt");
		}
		try { 
			int count=0; 
			QueryResult r; 
			do { 
				r = twitter.search(query);
				ArrayList ts= (ArrayList) r.getTweets();
				for (int i = 0; i < ts.size() && count < LIMIT; i++) {
					count++;
					Status t = (Status) ts.get(i);
					String text = t.getText();
					text = text.replace("\n", "").replace("\r", "").replace(",", " ");
					System.out.println("Text: " + text);
					if (train==1) {
						p.write(t.getId()+",");
						double lat = 0;
						double longit = 0;
						if (t.getGeoLocation()!=null) {
						lat = t.getGeoLocation().getLatitude();
						longit = t.getGeoLocation().getLongitude();
						}
						p.write(lat+"-"+longit+",");
						p.write(df.format(t.getCreatedAt())+",");
						p.write(t.getUser().getScreenName()+",");
						p.write(text + "\n");
					} else {
					String name = t.getUser().getScreenName();
					System.out.println("User: " + name);
					String sent = sentClassifier.classify(text);
					System.out.println("Sentiment: " + sent);
					if (sent.contains("pos")) {
						posCount++;
					} else if (sent.contains("neg")) {
						negCount++;
					} else if (sent.contains("neu")) {
						neuCount++;
						}
					}
				}
			} 
			
			while ((query = r.nextQuery()) != null && count < LIMIT);
			} catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te); 
				}
		p.close();
		} 
	public int getPosCount() {
		return this.posCount;
	}
	public int getNegCount() {
		return this.negCount;
	}
	public int getNeuCount() {
		return this.neuCount;
	}
	public void setT(Integer t) {
		this.train = t;
	}
}
