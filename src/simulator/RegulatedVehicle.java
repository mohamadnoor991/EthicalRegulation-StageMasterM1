package simulator;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import agent.fsm.state.BehaviorState;
import agent.fsm.transition.Transition;
import environment.physical.Location;
import environment.physical.NetworkLocation;
import pbc.Constant;
import pbc.Property;
import sharedInformation.ParameterEnum;
import sharedInformation.Properties;
import taxiSimulation.TaxiBehavior;
import territoireData.Coordinates;
import territoireData.Network;
import vehicle.behaviorState.VehicleBehaviorStateNameEnum;
import vehicleSimulation.Vehicle;

public class RegulatedVehicle extends Vehicle {

	private NetworkLocation origin;
	private NetworkLocation destination;
	private double departureTime; // change to int

	public RegulatedVehicle(NetworkLocation origin, NetworkLocation destination, double departureTime) {
		this.origin = origin;
		this.destination = destination;
		this.departureTime  = departureTime;
		
	}

	@Override
	public NetworkLocation getDestination() {
		return destination;
	}
	
	public static RegulatedVehicle factory(JSONObject jo, boolean trace, HashMap<ParameterEnum, Object> param, Network network) {
		
		NetworkLocation origin = RegulatedVehicle.createLocation((JSONObject)jo.get("origin"), network);
		NetworkLocation destination = RegulatedVehicle.createLocation((JSONObject)jo.get("destination"), network);
		double departureTime = (double)jo.get("DepartureTime");
		String vehicleType = (String) jo.get("resourceType");
		
		// vehicleType should be used for creating different types of regulated vehicle => different behaviors
		RegulatedVehicle v = new RegulatedVehicle(origin, destination, departureTime);
		v.initDescription();
		v.initValue("taxi", origin, trace, param,network);
		HashMap<ParameterEnum, Object> parameter = new HashMap<ParameterEnum, Object>(); 
		parameter.put(ParameterEnum.Duration, (int)departureTime);
		v.getBehavior().getCurrentState().getTransition("WaitingToGoTo").modify(parameter);
		return v;
	}
	
	private static NetworkLocation createLocation(JSONObject jo, Network network) {
		int arcNumber = Integer.parseInt((String) jo.get("osm_id"));
		JSONObject coordinates = (JSONObject) jo.get("jsongeoms");
		
		double latitude = (double) coordinates.get("latitude");
		double longitude = (double)coordinates.get("longitude");
		return new NetworkLocation(new Coordinates(latitude, longitude), arcNumber, network);
	}
	
	@Override
	public void initDescription() {
		super.initDescription();
		description.setTypeDescription("Vehicle");
		description.put(Property.propertyFactory(Properties.DestinationName, "String"), new Constant<>(null));
		description.put(Property.propertyFactory(Properties.ResourceId, "Integer"), new Constant<>(-1));
	}

	@Override
	public void initBehavior(HashMap<ParameterEnum, Object> parameters, boolean trace) {
		setBehavior(new RegulatedVehicleBehavior(this, trace));
		
	}

	public double getDepartureTime() {
		return departureTime;
	}
	
	 @Override
	    public void decide() {
		 
	    	VehicleBehaviorStateNameEnum currentBehavior = (VehicleBehaviorStateNameEnum) getCurrentBehaviorState().getName(); 
	    	switch(currentBehavior){
	    		case GOINGTO:
	    			// if no destination a random destination is computed
	    			 if (getDestination().equals(getLocation())) {
	    				 BehaviorState waiting = this.getBehavior().getState(VehicleBehaviorStateNameEnum.WAITING);
	    				 Transition tr = waiting.getTransition("WaitingToGoTo");
	    				 waiting.getTransitions().remove(tr);	    				 
	    			 }
	    				
	    				break;
	    			}
	    	}
	
}
