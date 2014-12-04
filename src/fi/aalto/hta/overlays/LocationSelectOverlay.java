package fi.aalto.hta.overlays;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocationSelectOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	@SuppressWarnings("unused")
	private Context mContext;
	
	public LocationSelectOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		populate();
		mContext = context;
	}
	// removes shadow
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
        if(!shadow)
        {
            super.draw(canvas, mapView, false);
        }
    }

	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	@Override
	public int size() {
		 return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void removeAll(){
		mOverlays.clear();
		populate();
	}
	
	public Location getPosition(){
		if(mOverlays.size() > 0){
			GeoPoint point = mOverlays.get(0).getPoint();
			Location location = new Location("");
			location.setLatitude(point.getLatitudeE6() / 1000000.0);
			location.setLongitude(point.getLongitudeE6() / 1000000.0);
			return location;
		}else{
			return null;
		}
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		removeAll();
		addOverlay(new OverlayItem(p, "", ""));
		return true;
	}

}
