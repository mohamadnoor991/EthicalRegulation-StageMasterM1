package demand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * specific generator for demand
 * @author flavien.balbo
 *
 */
public class RequestGeneratorDemand extends RequestGenerator{
	
	public JSONArray edges;
	public int minimalTW;
	public int maximalTW;
	public int nbResourceLocations;

	/**
	 * init attributes (call generateResourceLocation)  
	 * @param jo scenario parameters
	 */
	protected RequestGeneratorDemand(JSONObject jo) {
		// debug
		json.TransformJSON.initInstanceFromJSONO(this, jo);
		// json.TransformJSON.initInstanceFromJSONO(this, jo);
		random = new Random(seed);
		resources = new HashMap<String, JSONObject>();
		generateResourceLocation();	
	}	
	
	/**
	 * creates resources following the value of processCreation
	 * random : the location is randomly chosen.
	 */
	private void generateResourceLocation() {		
		if (processCreation.compareToIgnoreCase("random")==0) {
			for (int i = 0; i < nbResourceLocations; i++) {
				
				// random location
				int pos = random.nextInt(edges.size());
				
				JSONObject jo = (JSONObject) edges.get(pos);
				ArrayList<JSONObject> ajo = new ArrayList<JSONObject>();
				ajo.add(((ArrayList<JSONObject>)jo.get("jsongeoms")).get(0)); 
				jo.put("jsongeoms", ajo);
				resources.put("SLoc" + i, jo);
			}
		}
	}
}
