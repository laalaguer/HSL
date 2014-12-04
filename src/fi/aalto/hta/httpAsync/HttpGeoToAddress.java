package fi.aalto.hta.httpAsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.HttpResponse;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fi.aalto.hta.interfaces.FinishCallBackReverseGeo;
import fi.aalto.hta.vos.VOAddress;

public class HttpGeoToAddress extends HttpTransaction{

	private final String REQUEST_TYPE = "reverse_geocode"; 
	private final String COORDINATE_PREFIX = "&coordinate=";
	private double latitude;
	private double longitude;
	
	// Dialog Title
	final static String dialogTitle = "Get Address";
	// Dialog message
	final static String dialogMessage = "Progressing, Please wait";
	// file content
	private String fileContents;
	// HSLAddress
	private VOAddress address = null;
	// Call back activity
	FinishCallBackReverseGeo activity;
	
	
	public HttpGeoToAddress(Context mContext,double latitude,double longitude,FinishCallBackReverseGeo activity) {
		super(mContext, dialogTitle, dialogMessage);
		this.latitude = latitude;
		this.longitude = longitude;
		this.activity = activity;
	}

	@Override
	public String buildURIContent(String additionalField) {
		return PREFIX+REQUEST_TYPE_PREFIX+REQUEST_TYPE+COORDINATE_PREFIX+this.longitude+","+this.latitude+GEO_CODING_TYPE_IN+AUTHENTICATION;
	}

	@Override
	protected boolean parseResponse(HttpResponse response) {
		boolean success = false;
		
		InputStream stream = null;
		try {
			stream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			fileContents = builder.toString();
			Gson gson = new Gson();
			Type collectionType = new TypeToken<Collection<VOAddress>>(){}.getType();
			ArrayList<VOAddress> result = gson.fromJson(fileContents, collectionType);
			if(result != null){
				address = result.get(0);
				success = true;
			}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try{
					stream.close();
				}catch (Exception e){
				// Nothing
				}
			}
		
		return success;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result == true){
			// set the address name onto the activity
			this.activity.doneReverseGeo(this.address.getName());
		}
		super.onPostExecute(result);
	}

}
