package Tscenario;


import org.json.simple.JSONObject;

public class Coordinate {
////The coordinates of all map
	public static double MINLAT_COMPLET =  45.4325;
    public static  double MINLONG_COMPLET = 4.3782;
    public  static  double MAXLAT_COMPLET = 45.444;
    public static  double MAXLONG_COMPLET = 4.4001;
    
    
    
  //The coordinates of simulated area of map
    public  static  double MINLAT_SIMULATED = 45.43632;
     public   static  double MINLONG_SIMULATED = 4.380861;
     public   static  double MAXLAT_SIMULATED = 45.442024;
     public   static  double MAXLONG_SIMULATED = 4.396207;
     //The coordinates of regulated area of map
    public   static  double MINLAT_REGULATED = 45.4352;
    public   static  double MINLONG_REGULATED = 4.386362;
    public   static  double MAXLAT_REGULATED = 45.439544;
    public   static  double MAXLONG_REGULATED = 4.391236 ;
    
	public Coordinate() {}
	public Coordinate(double min_lat ,double min_long ,double max_lat ,double max_long) {
		
	}
	public  JSONObject mapCoordinate(double min_lat ,double min_long ,double max_lat ,double max_long ){
		JSONObject v= new JSONObject();
		v.put("min-latitude",min_lat);
		v.put("min-longitude",min_long);
		v.put("max-latitude",max_lat);
		v.put("max-longitude",max_long);
		
		return v;
	}
	JSONObject complete_map =  mapCoordinate(MINLAT_COMPLET,MINLONG_COMPLET,MAXLAT_COMPLET,MAXLONG_COMPLET);
	JSONObject simulated_map =  mapCoordinate(MINLAT_SIMULATED,MINLONG_SIMULATED,MAXLAT_SIMULATED,MAXLONG_SIMULATED);
	JSONObject regulated_map =  mapCoordinate(MINLAT_REGULATED,MINLONG_REGULATED,MAXLAT_REGULATED,MAXLONG_REGULATED);
	
	public void displayMap(String mapName, JSONObject jo) {
		double min_lat = (double) jo.get("min-latitude");
		double min_long = (double) jo.get("min-longitude");
		double max_lat = (double) jo.get("max-latitude");
		double max_long = (double) jo.get("max-longitude");
		System.out.print(mapName + ":");
		System.out.println("("+ min_lat + " , " + min_long + ") -> (" + max_lat + " , " + max_long + ")" ); 
	}
	
	public static void main(String[] args) {
		Coordinate co = new Coordinate();
		co.displayMap("complete",co.complete_map);
		co.displayMap("simulated",co.simulated_map);
		co.displayMap("regulated",co.regulated_map);
	}
}

