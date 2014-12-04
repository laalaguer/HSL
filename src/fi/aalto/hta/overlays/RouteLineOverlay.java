package fi.aalto.hta.overlays;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import fi.aalto.hta.managers.HSLManager;
import fi.aalto.hta.util.DistancePointSegmentCalculator;
import fi.aalto.hta.vos.VOCoordinates;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;

public class RouteLineOverlay extends Overlay {
	private List<VOLeg> legs = null;
	private int flags = 0;
	private final int maxZoomLevel = 18;

	public static final String TYPE_WALK = ",walk,";
	public static final String TYPE_BUS = ",1,3,4,5,";
	public static final String TYPE_TRAIN = ",12,";
	public static final String TYPE_METRO = ",6,";
	public static final String TYPE_TRAM = ",2,";

	public static final int PART_FIRST = 1 << 0;
	public static final int PART_LAST = 1 << 1;

	public int detectedNextStopInt = -1;
	public int detectedNextStopLegInt = -1;

	int northLat = Integer.MIN_VALUE,
			southLat = Integer.MAX_VALUE,
			eastLon = Integer.MAX_VALUE,
			westLon = Integer.MIN_VALUE;


	private String type;
	private boolean firstUse = true;

	public RouteLineOverlay(List<VOLeg> legs){
		super();
		this.legs = legs;
	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		super.draw(canvas, mapv, shadow);
		if(shadow)
			return;
		Paint mPaint = null; 

		Paint circlePaint = new Paint();
		circlePaint.setDither(true);
		circlePaint.setColor(Color.YELLOW);
		circlePaint.setStyle(Style.FILL);

		Paint circlePaintStart = new Paint(circlePaint);
		circlePaintStart.setColor(Color.GREEN);

		Paint circlePaintStop = new Paint(circlePaint);
		circlePaintStop.setColor(Color.GRAY);

		Paint circlePaintFollowedStop = new Paint(circlePaint);
		circlePaintFollowedStop.setColor(Color.MAGENTA);

		Paint circlePaintEnd = new Paint(circlePaint);
		circlePaintEnd.setColor(Color.RED);

		Paint circleEdgePaint = new Paint();
		circleEdgePaint.setDither(true);
		circleEdgePaint.setColor(Color.BLACK);
		circleEdgePaint.setStyle(Style.FILL); 

		Point p1 = new Point();
		Point p2 = new Point();

		Projection projection = mapv.getProjection();

		Path path = null;
		GeoPoint geoPoint = null;
		int i = 0;
		for(VOLeg leg: this.legs){
			path = new Path();
			mPaint = getPaint(leg.getType());
			List<VOCoordinates> locations = leg.getShape();
			int j = 0;
			for(VOCoordinates tmpLoc: locations){
				geoPoint = toGeoPoint(tmpLoc);
				if(j == 0){
					projection.toPixels(geoPoint, p1);
					path.moveTo(p1.x, p1.y);
				}else{
					projection.toPixels(geoPoint, p2);          
					path.lineTo(p2.x,p2.y);
				}

				j++;
			}      
			canvas.drawPath(path, mPaint);
			if(!leg.getType().equals(HSLManager.VEH_TYPE_WALK)){
				List<VOLocation> stops = leg.getLocs();
				int k = 0;
				for(VOLocation tmpLoc: stops){
					geoPoint = toGeoPoint(tmpLoc.getCoord());
					Point p = new Point();
					projection.toPixels(geoPoint, p);
					canvas.drawCircle(p.x, p.y, 9, circleEdgePaint);
					if(detectedNextStopInt == k && detectedNextStopLegInt == i){
						canvas.drawCircle(p.x, p.y, 7, circlePaintFollowedStop);
					}else{
						canvas.drawCircle(p.x, p.y, 7, circlePaintStop);
					}
					k++;
				} 
			}
			if(locations.size() > 0){
				canvas.drawCircle(p1.x, p1.y, 9, circleEdgePaint);
				if(i == 0){
					canvas.drawCircle(p1.x, p1.y, 7, circlePaintStart);
				}else{
					canvas.drawCircle(p1.x, p1.y, 7, circlePaint);
				}

				if(j > 0){
					canvas.drawCircle(p2.x, p2.y, 9, circleEdgePaint);
					if(legs.get(legs.size()-1) == leg){  // it is the last one
						canvas.drawCircle(p2.x, p2.y, 7, circlePaintEnd);
					}else{
						canvas.drawCircle(p2.x, p2.y, 7, circlePaint);
					}
				}
			}
			i++;
		}
		if(firstUse){
			for(VOLeg leg: legs){
				for(VOCoordinates coord: leg.getShape()){
					GeoPoint p = toGeoPoint(coord);
					eastLon = Math.min(eastLon, p.getLongitudeE6());
					westLon = Math.max(westLon, p.getLongitudeE6());
					northLat = Math.max(northLat, p.getLatitudeE6());
					southLat = Math.min(southLat, p.getLatitudeE6());
				}
			}
			MapController controller = mapv.getController();
			controller.zoomToSpan(Math.abs(northLat - southLat), Math.abs(westLon - eastLon));
			if(mapv.getZoomLevel() > maxZoomLevel){
				controller.setZoom(maxZoomLevel);
			}
			controller.animateTo(new GeoPoint( (northLat + southLat)/2, (westLon + eastLon)/2 ));
			firstUse = false;
		}
	}
	private static GeoPoint toGeoPoint(VOCoordinates location) {
		int latE6 = toDegreesE6(location.getY());
		int lonE6 = toDegreesE6(location.getX());
		return new GeoPoint(latE6, lonE6);
	}
	private static int toDegreesE6(double degrees) {
		return (int) Math.round(degrees * 1000000);
	}

	private Paint getPaint(String type)
	{
		String tmpType = "," + type + ","; 
		Paint mPaint = new Paint();
		mPaint.setDither(true);
		if(TYPE_WALK.indexOf(tmpType) > -1){
			mPaint.setColor(Color.BLUE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setPathEffect(new DashPathEffect(new float[] {20,10}, 0));
		}else if(TYPE_BUS.indexOf(tmpType) > -1){
			mPaint.setColor(Color.BLUE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
		}else if(TYPE_TRAIN.indexOf(tmpType) > -1){
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
		}else if(TYPE_METRO.indexOf(tmpType) > -1){
			mPaint.setColor(Color.CYAN);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
		}else if(TYPE_TRAM.indexOf(tmpType) > -1){
			mPaint.setColor(Color.GREEN);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
		}
		mPaint.setStrokeWidth(8);
		return mPaint;
	}

	public void updateNextPosition(Location userLocation){
		int[] result = DistancePointSegmentCalculator.updateNextPosition(legs, userLocation);
		detectedNextStopLegInt = result[0];
		detectedNextStopInt = result[1];
	}
	public void updateNextPosition(GeoPoint userLocation){
		Location loc = new Location("");
		loc.setLatitude(userLocation.getLatitudeE6() / 1e6);
		loc.setLongitude(userLocation.getLongitudeE6() / 1e6);
		int[] result = DistancePointSegmentCalculator.updateNextPosition(legs, loc);
		detectedNextStopLegInt = result[0];
		detectedNextStopInt = result[1];
	}
}
