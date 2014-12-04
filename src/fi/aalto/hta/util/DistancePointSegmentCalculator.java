package fi.aalto.hta.util;

import java.util.Iterator;
import java.util.List;

import android.location.Location;
import android.util.Log;
import fi.aalto.hta.vos.VOCoordinates;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;

public class DistancePointSegmentCalculator {
	@SuppressWarnings("unused")
	private static final String TAG = DistancePointSegmentCalculator.class.getSimpleName();
    
	
	public static int[] updateNextPosition(List<VOLeg> legs, Location userLocation){
		double distanceSquare = 0;
		double minimumDistSquare = Double.MAX_VALUE;
		int newNextStopLegIndex = -1;
		int newNextStopIndex = -1;
		Iterator<VOLeg> iterLegs = legs.iterator();
		int i = 0;
		while(iterLegs.hasNext()){
			VOLeg leg = iterLegs.next();
			Iterator<VOLocation> iterLocs = leg.getLocs().iterator();
			int j = 0;
			VOLocation firstLoc = null;
			VOLocation secondLoc = null;
			while(iterLocs.hasNext()){
				if(firstLoc == null){
					firstLoc = iterLocs.next();
				}else{
					secondLoc = iterLocs.next();
					distanceSquare = distanceSquareToSegment(firstLoc.getCoord().toLocation(),
							secondLoc.getCoord().toLocation(), userLocation);
					
//					Log.d("segments", String.format("%d %d Calc dist square: %f", i, j, distanceSquare));
					if (distanceSquare < minimumDistSquare) {
						minimumDistSquare = distanceSquare;
						newNextStopLegIndex = i;
						newNextStopIndex = j;
					}
					firstLoc = secondLoc;
				}
				j++;
			}
			i++;
		}
//		if(minimumDistSquare > 100000){
//			return new int[]{-1,-1};
//		}else{
			return new int[]{newNextStopLegIndex, newNextStopIndex}; 
//		}
	}
	
    public static float distanceSquareToSegment(Location p1, Location p2, Location p3){
    	float dist12 = p1.distanceTo(p2);            // c
    	float dist23 = p2.distanceTo(p3);            // b
    	float dist13 = p1.distanceTo(p3);    	     // a
//    	Log.d("Segm", String.format("dist12 %f dist23 %f dist13 %f", dist12, dist23, dist13));
    	if(dist12 < 0.001){
    		return dist13 * dist13;
    	}
    	float part2 = ((dist13 * dist13) - (dist23 * dist23) + (dist12 * dist12))/(2 * dist12);     // y
    	if (part2 > dist12){
    		return dist23 * dist23;
    	}
    	if(part2 < 0){
    		return dist13 * dist13;
    	}
    	// Projection on segment. Calculating triangle height
    	float dist = dist13 * dist13 - part2 * part2;
    	return dist;
    }
}