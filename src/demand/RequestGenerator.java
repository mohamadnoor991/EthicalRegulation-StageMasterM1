package demand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import territoireConnection.TerritoireHttpConnection;

/**
 * 
 * @author flavien.balbo object for generating request for every type of
 *         resourceTypes
 */
public abstract class RequestGenerator {

	// input parameters
	public String scenarioName;
	public long seed;
	public String resourceType;
	public String scenarioModel;
	public int nbCycles;
	public String processCreation;
	public int nbMaxByCycles;

	// local parameters;
	protected Map<String, JSONObject> resources = new HashMap<>();
	protected Random random;
	protected static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");
	protected static String puturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/put-data?");

	/**
	 * create the random and resources objects
	 */
	protected RequestGenerator() {
		random = new Random();
		resources = new HashMap<String, JSONObject>();
	}

	/**
	 * Create the RequestGenerator object according to the name of the scenario
	 * 
	 * @param scenarioName name of the scenario. It is used to request the scenario
	 *                     to the scenario service.
	 * @return a RequestGenerator object corresponding to the scenarioType value in
	 *         scenario
	 */
	private static RequestGenerator factory(JSONObject scenarioParameters) {

		if (scenarioParameters.containsKey("scenarioType")) {
			String scenarioType = scenarioParameters.get("scenarioType").toString(); //
			if (scenarioType.substring(scenarioType.length() - "Demand".length()).compareToIgnoreCase("Demand") == 0)
				return new RequestGeneratorDemand(scenarioParameters);
		}
		return null;
	}

	/**
	 * 
	 * @param scenarioName name of the scenario for which requests are generated
	 * @return the request list ordered by cycles. A cell is the JSONObject with the
	 *         requests of the corresponding cycle
	 */
	public static JSONArray createRequests(JSONObject scenarioParameters, String login, String password,
			boolean pushData) {
		RequestGenerator rg = factory(scenarioParameters);
		String simulationName = (String) scenarioParameters.get("scenarioName");
		JSONArray result = new JSONArray();
		if (rg != null) {
			result = rg.generate(rg.nbCycles);
			if (pushData)
				try {
					// System.out.println("post:"+ TerritoireHttpConnection.postURL(puturl, result,
					// login, password, "request", simulationName));
					TerritoireHttpConnection.postURL(puturl, result, login, password, "request", simulationName);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return result;
	}

	/**
	 * generate nbCycles of requests. A cycle may contain between 0 and nbMaxByCycle
	 * requests
	 * 
	 * @param nbCycles number of cycles of the simulation
	 * @return the request list ordered by cycles. A cell is the JSONObject with the
	 *         requests of the corresponding cycle
	 */
	private JSONArray generate(int nbCycles) {
		JSONArray result = new JSONArray();
		for (int i = 0; i < nbCycles; i++) {
			JSONArray jo = new JSONArray();
			for (int j = 0; j < random.nextInt(nbMaxByCycles); j++)
				jo.add(Request.getJSONObject(this, i));
			result.add(jo); // jo size may be 0
		}
		return result;
	}

	/**
	 * random process to access to a source location
	 * 
	 * @return a resource location with is name (key) and its description (value)
	 */
	public Map.Entry<String, JSONObject> getRandomResourceLocation() {
		int i = random.nextInt(resources.size());
		for (Map.Entry<String, JSONObject> entry : resources.entrySet()) {
			if (i-- == 0)
				return entry;
		}
		return null;
	}

	/**
	 * random process to access to a source location with a different name that the
	 * one in parameter
	 * 
	 * @param key name of the source
	 * @return a resource location with is name (key) and its description (value)
	 */
	public Map.Entry<String, JSONObject> getRandomResourceLocation(String key) {
		Map.Entry<String, JSONObject> entry = getRandomResourceLocation();
		while (entry.getKey().compareTo(key) == 0)
			entry = getRandomResourceLocation();
		return entry;
	}

	/**
	 * 
	 * @return resourceType value
	 */

	public String getResourceType() {
		return resourceType;
	}

	/**
	 * 
	 * @return random value
	 */
	public Random getRandomGenerator() {
		return this.random;
	}

	/**
	 * print resource JSON definition
	 */
	public void displayResources() {
		for (Map.Entry<String, JSONObject> r : resources.entrySet())
			System.out.println(r.getKey() + " : " + r.getValue());
	}

	public static JSONArray getRequests(String simulationName, String login, String password) {
		return TerritoireHttpConnection.getSimulationData(geturl, login, password, simulationName, "request");
	}
}
