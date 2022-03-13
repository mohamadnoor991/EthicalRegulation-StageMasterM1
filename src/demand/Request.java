
package demand;

import org.json.simple.JSONObject;

public class Request {

	private int twi, twj; // temporal validity window
	private Resource requestedResource;

// constructors

	private Request(String subject, int twi, int twj, Resource requestedResource) {
		this.twi = twi;
		this.twj = twj;
		this.requestedResource = requestedResource;
	}

	private Request(RequestGeneratorDemand requestGenerator, int cycle) {
		try {
			String typeOfResource = "unknown";
			switch (requestGenerator.getResourceType()) {
			case "trip":
				typeOfResource = "demand.Trip";
			}
			@SuppressWarnings("rawtypes")
			Class resourceClass = Class.forName(typeOfResource);
			requestedResource = (Resource) resourceClass.newInstance();
			this.generateTW(requestGenerator, cycle);
			requestedResource.completeName("[" + twi + "," + twj + "]");

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generateTW(RequestGeneratorDemand requestGenerator, int cycle) {
		twi = cycle;
		twj = requestGenerator.getRandomGenerator().nextInt(requestGenerator.maximalTW) + cycle
				+ requestGenerator.minimalTW;
	}

	/*
	 * Getter and Setter
	 */

	public int getTwi() {
		return twi;
	}

	public void setTwi(int twi) {
		this.twi = twi;
	}

	public int getTwj() {
		return twj;
	}

	public void setTwj(int twj) {
		this.twj = twj;
	}

	public Resource getRequestedResource() {
		return requestedResource;
	}

	public void setRequestedResource(Resource requestedResource) {
		this.requestedResource = requestedResource;
	}

	/*
	 * JSON transformation method
	 */
	/**
	 * 
	 * @param requestGenerator for generating request parameters
	 * @param cycle            of the simulation when request is created
	 * @return the JSON definition of the request
	 */
	public static JSONObject getJSONObject(RequestGenerator requestGenerator, int cycle) {
		Request r = new Request((RequestGeneratorDemand) requestGenerator, cycle);
		return r.getJSONObject((RequestGeneratorDemand) requestGenerator);
	}

	/**
	 * create the JSON value of the object
	 * 
	 * @param requestGenerator for resource initialization
	 * @return JSON verson of the object
	 */
	private JSONObject getJSONObject(RequestGeneratorDemand requestGenerator) {
		JSONObject jo = new JSONObject();
		jo.put("twi", twi);
		jo.put("twj", twj);
		jo.put("resource", requestedResource.generate(requestGenerator));
		return jo;
	}

	/**
	 * create a Request object from its JSON declaration
	 * 
	 * @param jo JSON object declaration
	 * @return created object
	 */

	public static Request factory(JSONObject jo) {
		int twi = -1, twj = -1;
		String name = jo.get("twi").getClass().getTypeName();
		if (name.compareTo("java.lang.Integer") == 0) {
			twi = (Integer) jo.get("twi");
			twj = (Integer) jo.get("twj");

		} else {
			twi = Integer.parseInt(((Long) jo.get("twi")).toString());
			twj = Integer.parseInt(((Long) jo.get("twj")).toString());
		}
		Resource requestedResource = Resource.factory((JSONObject) jo.get("resource"));
		return new Request("request", twi, twj, requestedResource);
	}

//useful methods
	public String toString() {
		return "temporal window [" + twi + "," + twj + "] " + requestedResource;
	}

	/**
	 * 
	 * @return true if the request is valid
	 */
	public boolean valid(int simulationCycle) {
		return (simulationCycle >= this.getTwi() && simulationCycle <= this.getTwj());
	}

}
