package fi.aalto.hta;

public class RouteTypes {
	/*
    1 = Helsinki internal bus lines
    2 = trams
    3 = Espoo internal bus lines
    4 = Vantaa internal bus lines
    5 = regional bus lines
    6 = metro
    7 = ferry
    8 = U-lines
    12 = commuter trains
    21 = Helsinki service lines
    22 = Helsinki night buses
    23 = Espoo service lines
    24 = Vantaa service lines
    25 = region night buses
    36 = Kirkkonummi internal bus lines
    39 = Kerava internal bus lines

	 */
	public static final String TYPE_WALK = ",walk,";
	public static final String TYPE_BUS = ",1,3,4,5,8,22,25,36,39,";
	public static final String TYPE_TRAIN = ",12,";
	public static final String TYPE_METRO = ",6,";
	public static final String TYPE_TRAM = ",2,";
	public static final String TYPE_FERRY = ",7,";
	
	public static int getRouteTypeResource(String type){
		String modType = "," + type + ",";
		if(TYPE_WALK.indexOf(modType) > -1){
//			return R.drawable.walk;
			return R.drawable.man_white;
		}
		if(TYPE_BUS.indexOf(modType) > -1){
			return R.drawable.bus_white;
//			return R.drawable.bus;
		}
		if(TYPE_TRAIN.indexOf(modType) > -1){
			return R.drawable.train_white;
//			return R.drawable.train;
		}
		if(TYPE_METRO.indexOf(modType) > -1){
			return R.drawable.metro_white;
//			return R.drawable.metro;
		}
		if(TYPE_TRAM.indexOf(modType) > -1){
			return R.drawable.tram_white;
//			return R.drawable.tram;
		}
		if(TYPE_FERRY.indexOf(modType) > -1){
			return R.drawable.boat_white;
//			return R.drawable.ferry;
		}
		return 0;
	}
}
