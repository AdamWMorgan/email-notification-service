package notificationEmail;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class buildMessage extends messagingService{
	
	public static String subject(String Sub) {
		
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		LocalTime currentTime = LocalTime.now();
		
		currentTime.format(sdf);

		if(currentTime.isAfter( LocalTime.parse( "00:00:01" )) && currentTime.isBefore(LocalTime.parse("11:59:59"))) {
			Sub = "Your Morning Update";
		}
		if(currentTime.isAfter( LocalTime.parse( "12:00:00" )) && currentTime.isBefore(LocalTime.parse("17:59:59"))) {
			Sub = "Your Afternoon Update";
		}
		if(currentTime.isAfter( LocalTime.parse( "18:00:00" )) && currentTime.isBefore(LocalTime.parse("23:59:59"))){
			Sub = "Your Evening Update";
		}
		
		return Sub;
	}
	
	public static String factData(String fact) {

		String year = "Unknown";
		String battle = "Unknown";
		String details = "Unknown";
		String fileLoc = loadProperties("FACT_FILE");
		
		try	{ 
			File csvFile = new File(fileLoc); 

			BufferedReader br = new BufferedReader(new FileReader(csvFile));

			String line = ""; 
			
			Random r = new Random();
			int randomNum = r.nextInt(56-1) + 1;
		    String numString = String.valueOf(randomNum);
			while ((line = br.readLine()) != null) { 

				String[] arr = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
				if(arr[0].contains(numString)) {
					year = arr[1];
					battle = arr[2];
					details = arr[6];
				}

					fact = "History Fact:"
							+ "\n\n " + year + "	" + battle + "	" + details;				
			}
		}

		catch(IOException ex) {	

		ex.printStackTrace();

		}
		return fact;
	}
	
	@SuppressWarnings("deprecation")
	public static String newsData(String news) throws Exception {
		
		String output;
		String newsAPI = loadProperties("NEWS_API");
		news = "Latest Headlines:" + "\n\n";
		
		  try {
				@SuppressWarnings({ "resource" })
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet getRequest = new HttpGet(newsAPI);
				getRequest.addHeader("accept", "application/json");

				HttpResponse response = httpClient.execute(getRequest);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
					   + response.getStatusLine().getStatusCode());
				}

				BufferedReader br = new BufferedReader(
		                         new InputStreamReader((response.getEntity().getContent())));

				while ((output = br.readLine()) != null) {
					//System.out.println(output);
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				              new FileOutputStream("newsData.txt"), "utf-8"))) {
				   writer.write(output);
				}
				}

				httpClient.getConnectionManager().shutdown();
				
				JSONParser parser = new JSONParser();
				Object json = parser.parse(new FileReader("newsData.txt"));
				
				JSONObject jsonObject = (JSONObject) json;
				
				
				JSONArray articles = (JSONArray) jsonObject.get("articles");
				
				Iterator iter = articles.iterator();
				
				while (iter.hasNext()) {
					JSONObject slide = (JSONObject) iter.next();
					news += (String)slide.get("title") + "\n\n";
				    }
				
			
			  } catch (ClientProtocolException e) {
			
				e.printStackTrace();

			  } catch (IOException e) {
			
				e.printStackTrace();
			  }

		return news;
	}

}
