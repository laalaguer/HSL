package fi.aalto.hta.interfaces;

import android.location.Location;

public interface LocationUpdateCallBack {
	public void updateLocation(Location location);
	public void startLocationUpdates();
	public void stopLocationUpdates();
	
}
