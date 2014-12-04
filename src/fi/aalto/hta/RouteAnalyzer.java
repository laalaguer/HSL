package fi.aalto.hta;

public class RouteAnalyzer {
	public static String getRouteCode(String type, String rawCode){
		if(rawCode == null){
			return null;
		}
		String shownCode = "";
		if(type.equals("6")){
			shownCode = "Metro";
		}else if(type.equals("12")){
			shownCode = rawCode.substring(4,5);
		}else{
			shownCode = rawCode.substring(1, 5);
			shownCode = shownCode.replaceAll("^0+", "");
			shownCode = shownCode.trim();
		}
		return shownCode;
	}
}
