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
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fi.aalto.hta.interfaces.FinishCallBackGeoCoding;
import fi.aalto.hta.vos.VOAddress;

public class HttpAddressToGeo extends HttpTransaction{
	
	private final String REQUEST_TYPE = "geocode"; 
	private final String ADDRESS_PREFIX = "&key=";
	private String address;
	
	// Dialog Title
	final static String dialogTitle = "Get Geo Info";
	// Dialog message
	final static String dialogMessage = "Progressing, Please wait...";
	// file content
	private String fileContents;
	// HSLAddress
	private VOAddress hslAddress = null;
	// Activity with call back
	FinishCallBackGeoCoding activity;

	public HttpAddressToGeo(Context mContext, String address,FinishCallBackGeoCoding activity) {
		super(mContext, dialogTitle, dialogMessage);
		this.address = address;
		this.activity = activity;
	}

	@Override
	public String buildURIContent(String additionalField) {
		return PREFIX+REQUEST_TYPE_PREFIX+REQUEST_TYPE+ADDRESS_PREFIX+Uri.encode(this.address)+GEO_CODING_TYPE_OUT+AUTHENTICATION;
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
				hslAddress = result.get(0);
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
			String coords = this.hslAddress.getCoords();
			int commaIndex = coords.indexOf(",");
			String lonString = coords.substring(0, commaIndex);
			String latString = coords.substring(commaIndex+1);
			this.activity.doneGeoCoding(Double.valueOf(latString), Double.valueOf(lonString));
		}
		super.onPostExecute(result);
	}

}
