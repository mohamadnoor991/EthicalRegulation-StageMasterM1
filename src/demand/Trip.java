package demand;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import territoireData.Coordinates;

public class Trip extends Resource {

	public JSONObject origin;
	public JSONObject destination;
	public static int sex;//not useful for now
	




	@Override
	protected void setName(String ID) {
		super.setName(getOriginName()+ "_"+ getDestinationName() + "_" + ID); 
	}
	
	
	
	
	@Override
	public JSONObject generate(RequestGeneratorDemand rgd) {

		if (rgd.processCreation.compareTo("random") == 0)
			randomGeneration(rgd);
		JSONObject value = json.TransformJSON.createJSONFromInstance(this);
		value.put("resourceType", "Trip");
		this.setIdResource(getIdResource());
		return value;
	}
	/**
	 * 
	 * @param rgd generate the origin and destination randomly
	 */
	private void randomGeneration(RequestGeneratorDemand rgd) {
		Map.Entry<String, JSONObject> originEntry = rgd.getRandomResourceLocation();
		originEntry.getValue().put("locationName", originEntry.getKey());
		origin = originEntry.getValue();

		Map.Entry<String, JSONObject> destinationEntry = rgd.getRandomResourceLocation(originEntry.getKey());
		destinationEntry.getValue().put("locationName", destinationEntry.getKey());
		destination = destinationEntry.getValue();

	}
	
	/**
	 * create an object Trip with its JSONDeclaration
	 * @param jo Trip object declaration
	 * @return Trip object
	 */
	public static Trip factory(JSONObject jo) {
		Trip r = new Trip();
		r.origin = (JSONObject) jo.get("origin");
		r.destination = (JSONObject) jo.get("destination");
		return r;
	}
	
	public Coordinates getOriginCoordinates() {
		double latitude = (double) ((JSONObject)((ArrayList<JSONObject>)origin.get("jsongeoms")).get(0)).get("latitude");
		double longitude = (double) ((JSONObject)((ArrayList<JSONObject>)origin.get("jsongeoms")).get(0)).get("longitude");
		return new Coordinates(latitude, longitude);
	}
	
	public Coordinates getDestinationCoordinates() {
		double latitude = (double) ((JSONObject)((ArrayList<JSONObject>)destination.get("jsongeoms")).get(0)).get("latitude");
		double longitude = (double) ((JSONObject)((ArrayList<JSONObject>)destination.get("jsongeoms")).get(0)).get("longitude");
		return new Coordinates(latitude, longitude);
	}
	public Long getDestinationEdgeId() {
		Long osm_id = Long.parseLong((String) destination.get("osm_id"));
		return osm_id;
	}
	public Long getOriginEdgeId() {
		Long osm_id = Long.parseLong((String) origin.get("osm_id"));
		return osm_id;
	}
	public String getOriginName() {
		return (String) origin.get("locationName");
	}
	public String getDestinationName() {
		return (String) destination.get("locationName");
	}
	
	// useful methods
		public String toString() {
			return "request:" + this.getIdResource() + " from " + origin.get("locationName") + " to " + destination.get("locationName");
		}

		
		//getters and setters
		public static int getSex() {//not useful for now
			return sex;//not useful for now
		}

		//not useful for now


		public static void setSex(int sexx) {//not useful for now
			sex = sexx;//not useful for now
		}
		//not useful for now
}
