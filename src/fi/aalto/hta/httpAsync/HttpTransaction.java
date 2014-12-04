package fi.aalto.hta.httpAsync;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import fi.aalto.hta.db.asyn.ShowDialogProgress;

/**
 * abstract class. Common Methods for online status, dialog setting. 
 * @author Xiqing Chu
 *
 */
public abstract class HttpTransaction extends AsyncTask<Void,Integer,Boolean>{
	private static final String TAG = HttpTransaction.class.getName();
	// HSL 
	// Authentication with username and password
	protected final static String AUTHENTICATION = "&user=XXX&pass=XXX"; // Please modify here your HSL api access username and password.
	// Geo coding send to HSL with wgs84 coordinats
	protected final static String GEO_CODING_TYPE_IN = "&epsg_in=wgs84";
	// Geo coding get from HSL with wgs84 coordinats
	protected final static String GEO_CODING_TYPE_OUT = "&epsg_out=wgs84";
	// prefix of URL
	protected final static String PREFIX = "http://api.reittiopas.fi/hsl/prod/";
	// HSL request type
	protected final static String REQUEST_TYPE_PREFIX = "?request=";
	
	
	// Uri for request;
	URI requestUri;
	// Environment need to initialize!
	Context mContext;
	// Http Client
	HttpClient client;
	// Http Get (for parsing URL)
	HttpGet httpGet;
	// Http Response (raw result)
	HttpResponse response;
	// Http response Status
	StatusLine statusLine;
	
	// Dialog while processing
	ShowDialogProgress dialogMgr;
	// Dialog Title
	String dialogTitle;
	// Dialog message
	String dialogMessage;
	
	
	public HttpTransaction(Context mContext, String title, String msg){
		this.mContext = mContext;
		this.dialogTitle = title;
		this.dialogMessage = msg;
		dialogMgr = new ShowDialogProgress(dialogTitle, dialogMessage, mContext);
	}
	
	
	@Override
    protected void onPreExecute() {
		super.onPreExecute();
		dialogMgr.showDialog();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
        dialogMgr.setProgress(progress[0]);
    }
	
	
	@Override
	protected Boolean doInBackground(Void... arg0){
		boolean success = false;
		if (this.requestUri!=null){
			HttpResponse rsp = getHttpResponse(this.requestUri);
			if (rsp!=null){
				success = parseResponse(rsp);
			}
		}
		return success;
	}
	
	// Parse the response 
	protected abstract boolean parseResponse(HttpResponse response);
	
	@Override
	protected void onPostExecute(Boolean result) {
		dialogMgr.dismissDialog();
		if (result==false){
			this.showFailAlert();
		}
		super.onPostExecute(result);
	}
	
	// Return context of enviroment
	public Context getContext(){
		return this.mContext;
	}
	
	// This class shall be inherited and implemented
	public abstract String buildURIContent(String additionalField);
	
	// Build uri from string, if error then return null
	public URI buildURI(String uriContent){
		requestUri = null;
		try {
			requestUri = new URI(uriContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestUri;
	}
	
	
	
	// Get Http Response , if error then return null;
	public HttpResponse getHttpResponse(URI uri){
		Log.i(TAG, "Connecting to: " + uri.toASCIIString());
		
		//Http client parameters:
		HttpParams httpParameters = new BasicHttpParams();
		// Timeount until a connection is established:
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Socket timeout for waiting for data:
		int timeoutSocket = 8000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		client = new DefaultHttpClient(httpParameters);
		
		httpGet = new HttpGet(uri);
		try {
			response = client.execute(httpGet);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode == 200){
				return response;
			}else if(statusCode == 404){
				return null;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check if device is online or not.
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	/**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
 
        // Setting Dialog Title
        alertDialog.setTitle("Internet Settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("Internet is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                mContext.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
    
    public void showFailAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
 
        // Setting Dialog Title
        alertDialog.setTitle("Request Fail");
 
        // Setting Dialog Message
        alertDialog.setMessage("An Error Occured, Fail in Query.");
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
}
