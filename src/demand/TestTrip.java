package demand;


import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class TestTrip {
	
	public static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");

	public static String login  = "bader-sim";
	public static String password = "MDPbader1352";
	public static String scenarioName = "Traffic2020";//my scenario name

	public static void main(String[] args) throws IOException  {	
		JSONObject scenarioparameter=new JSONObject();
		TrafficGenerator traffic = new TrafficGenerator();
		JSONObject name;
		name=new JSONObject();
		//TripTraffic d;
		JSONArray v;
		JSONArray vv;
		JSONArray ajo;
		JSONArray ajo1;
		
		name.put("scenarioName",TestTrip.scenarioName);
		
		 // check the result of generateJson().
		  v=traffic.generateJson();
		// System.out.println(v);
		 
	ajo1=TrafficGenerator.createRequests(name, login, password,false);//should be true.
//System.out.println("createRequest"+ajo1);
	
	vv=traffic.getRequests();
	System.out.println(vv);
	ajo=TrafficGenerator.getRequests();
//	System.out.println("get request"+ajo);
	
	FileWriter file = new FileWriter("src/Jsonfiles/requests.json");//the changes will save directly to the json file
	file.write(((JSONObject) ajo.get(0)).toJSONString());
	file.close();	
	}
	}
	


