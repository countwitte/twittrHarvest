import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class twitterHarvest {

	/**
	 * this top level class pulls relvant parameters for twitter api querying and then updates storage
	 */

	/** Last Known tweet ID found in google drive */
	private static long lastID = 0;
	private static String LastIDfileID = "xxxxxxxxx"; //google drive file id that contains the tweet id of last known tweet
	public static void main(String[] args) {

		// create Drive service object 
		Drive service = null;
		String content = null;
		try {
			service = twitterHarvest.getDriveService();
			twitterHarvest.printFile(service, LastIDfileID);
			File file = service.files().get(LastIDfileID).execute();

			// pull contents of last known tweet id to a string
			content = twitterHarvest.convertStreamToString(twitterHarvest.downloadFile(service, file));
			System.out.println(content);

		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// deal with format of string
		content = content.substring(1);
		lastID = Long.valueOf(content);
		System.out.println(lastID);
		
		// add query for twitter api to an array to pass to api querying code
		String query = "#indyref+since_id:"+lastID;
		String[] arg = {"-t",query};

		// run twitter api querying class
		sentiAnalysis.main(arg);
		java.io.File trainingset = new java.io.File("trainingset.txt");
		if (trainingset.length()==0) {
			System.out.println("No new tweets - exiting...");
			System.exit(0);
		}
		
		//Insert a file to googledrive storage 
	    File body = new File();
	    Date myDate = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
	    String myDateString = sdf.format(myDate);
	    body.setTitle("indyref-fulldetails-"+myDateString);
	    body.setDescription("tweets harvested on "+myDateString);
	    body.setMimeType("text/plain");
	    body.setParents(Arrays.asList(new ParentReference().setId("0B5GB1O5VghifSmwyQUY1aTdmWU0")));
	    java.io.File fileContent = new java.io.File("trainingset.txt");
		// add the extracted tweets and metadata as content for new file
	    FileContent mediaContent = new FileContent("text/plain", fileContent);

	    File file = null;
		try {
			file = service.files().insert(body, mediaContent).execute();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    System.out.println("File ID: " + file.getId());
		
		// extract last known tweet id from file just created and update file	
		int c;
		StringBuilder response= new StringBuilder();
		try {
			BufferedReader ts = new BufferedReader(new FileReader("trainingset.txt"));
			while((c = ts.read())!= -1) {
				if (response.lastIndexOf(",")==-1) {
				response.append((char) c);
				} else {
					ts.close();
					break;
				}
			}
			System.out.println(response.toString());
			String result = response.toString().substring(0, (response.toString().length()-1));
			System.out.println(result);
		
		PrintWriter lid = new PrintWriter("newlid.txt");
		lid.write(result);
		lid.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		twitterHarvest.updateFile(service, LastIDfileID, "newlid.txt", "text/plain");
	}

	/** Email of the Service Account */
	private static final String SERVICE_ACCOUNT_EMAIL = "xxxxxxxxxxxxxxxxxx";

	/** Path to the Service Account's Private Key file */
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "xxxxxxxxxxxxxxxxxxxxx";

	/**
	 * Build and returns a Drive service object authorized with the service accounts.
	 *
	 * @return Drive service object that is ready to make requests.
	 */
	public static Drive getDriveService() throws GeneralSecurityException,
	IOException, URISyntaxException {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		ArrayList<String> scopes = new ArrayList<String>();
		scopes.add(DriveScopes.DRIVE);
		GoogleCredential credential = new GoogleCredential.Builder()
		.setTransport(httpTransport)
		.setJsonFactory(jsonFactory)
		.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		.setServiceAccountScopes(scopes)
		.setServiceAccountPrivateKeyFromP12File(
				new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH))
				.build();
		Drive service = new Drive.Builder(httpTransport, jsonFactory, null)
		.setApplicationName("secretplan4b-twittrharvest/0.1")
		.setHttpRequestInitializer(credential)
		.build();
		return service;
	}

	/**
	 * Print a file's metadata from google drive api
	 *
	 * @param service Drive API service instance.
	 * @param fileId ID of the file to print metadata for.
	 */
	private static void printFile(Drive service, String fileId) {

		try {
			File file = service.files().get(fileId).execute();

			System.out.println("Title: " + file.getTitle());
			System.out.println("Description: " + file.getDescription());
			System.out.println("MIME type: " + file.getMimeType());
			System.out.println("downloadURL: " + file.getDownloadUrl());
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
		}
	}

	/**
	 * Download a file's content using google drive api
	 * 
	 * @param service Drive API service instance.
	 * @param file Drive File instance.
	 * @return InputStream containing the file's content if successful,
	 *         {@code null} otherwise.
	 */
	private static InputStream downloadFile(Drive service, File file) {
		String downloadUrl = file.getExportLinks().get("text/plain");
		if (downloadUrl != null) {
			try {
				HttpResponse resp =
						service.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl))
						.execute();
				return resp.getContent();
			} catch (IOException e) {
				// An error occurred.
				e.printStackTrace();
				return null;
			}
		} else {
			// The file doesn't have any content stored on Drive.
			return null;
		}
	}

	// ...


	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}


	/**
	 * Update an existing file's metadata and content.
	 *
	 * @param service Drive API service instance.
	 * @param fileId ID of the file to update.
	 * @param newTitle New title for the file.
	 * @param newDescription New description for the file.
	 * @param newMimeType New MIME type for the file.
	 * @param newFilename Filename of the new content to upload.
	 * @param newRevision Whether or not to create a new revision for this
	 *        file.
	 * @return Updated file metadata if successful, {@code null} otherwise.
	 */
	private static File updateFile(Drive service, String fileId,String newFilename,String newMimeType) {
		try {
			// First retrieve the file from the API.
			File file = service.files().get(fileId).execute();

			// File's new content.
			java.io.File fileContent = new java.io.File(newFilename);
			FileContent mediaContent = new FileContent(newMimeType, fileContent);

			// Send the request to the API.
			File updatedFile = service.files().update(fileId, file, mediaContent).execute();

			return updatedFile;
		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
			return null;
		}
	}
}
