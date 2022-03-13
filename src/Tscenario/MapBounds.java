package Tscenario;


import org.json.simple.JSONObject;

import json.TransformJSON;

public class MapBounds {

    
	public double min_lat;
	public double min_long;
	public double max_lat;
	public double max_long;
	public String mapName;


	public MapBounds(double min_lat ,double min_long ,double max_lat ,double max_long, String mapName) {
		this.min_lat = min_lat;
		this.min_long = min_long;
		this.max_lat = max_lat;
		this.max_long = max_long;
		this.mapName = mapName;
	}
	
	public void displayMap(String mapName, JSONObject jo) {
		System.out.println("("+ min_lat + " , " + min_long + ") -> (" + max_lat + " , " + max_long + ")" ); 
	}
	

	public JSONObject getJSONObject() {
		return TransformJSON.createJSONFromInstance(this);
	}
}

