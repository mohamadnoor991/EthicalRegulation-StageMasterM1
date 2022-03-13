package demand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Global;
import scenario.Scenario;
import territoireConnection.TerritoireHttpConnection;

public class TripTraffic extends Resource{
	public  JSONObject origin;
	public  JSONObject destination;
	public double DepartureTime;
	public String VehicleType;
	public  int sex;
	public  int age;
	public  int educL;
	public  int annuI;
	public  int timeR;
	
	
	
	//Constructor for testing steps during my work
	public TripTraffic() {
		
	}
	
	private JSONObject selection (JSONObject point) {
        JSONObject selectedPoint = new JSONObject();
        selectedPoint=(JSONObject) point;
       String location=(String) point.get("osm_id"); 
        selectedPoint.put("osm_id", location);
        return selectedPoint;
    }
	
	public TripTraffic(JSONObject origin,JSONObject destination,double DepartureTime, String VehicleType) {
		
			this.origin=selection(origin);
			this.destination=selection(destination);
			this.DepartureTime=DepartureTime;
			this.VehicleType=VehicleType;
	}
	


	@Override
	public JSONObject generate(RequestGeneratorDemand rgd) {
		// TODO Auto-generated method stub
		return null;
	}
//getters and settes 
	public  int getSex() {
		return sex;
	}

	public  void setSex(int sex) {
		this.sex = sex;
	}
	public  int getAge() {
		return age;
	}

	public  void setAge(int age) {
		this.age = age;
	}

	public  int getEducL() {
		return educL;
	}

	public  void setEducL(int educL) {
		this.educL = educL;
	}

	public  int getAnnuI() {
		return annuI;
	}

	public  void setAnnuI(int annuI) {
		this.annuI = annuI;
	}

	public  int getTimeR() {
		return timeR;
	}

	public  void setTimeR(int timeR) {
		this.timeR = timeR;
	}

} //end of method 
