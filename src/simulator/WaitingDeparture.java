package simulator;

import agent.NetworkAgent;
import agent.fsm.transition.AgentPropertyTransition;
import agent.fsm.transition.Transition;
import environment.physical.Location;
import pbc.Constant;
import pbc.Description;
import pbc.Property;
import sharedInformation.Properties;
import taxi.behaviorState.TaxiBehaviorStateNameEnum;
import taxiSimulation.Taxi;
import vehicle.behaviorState.VehicleBehaviorStateNameEnum;
import vehicle.behaviorState.Waiting;

public class WaitingDeparture extends Waiting{

	public WaitingDeparture(NetworkAgent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}
	
	public WaitingDeparture(NetworkAgent agent, VehicleBehaviorStateNameEnum waiting) {
		super(agent, waiting);
	}

	@Override
	public void initState(Description parameters) {
		RegulatedVehicle vehicle = (RegulatedVehicle) this.getAgent();
		
		// waiting transition update
		Description conditions = new Description();
		conditions.put(Property.propertyFactory(Properties.Cycle, "Double"),
				new Constant<Double>(vehicle.getDepartureTime()));
		Transition tr = vehicle.getBehavior().getTransition(VehicleBehaviorStateNameEnum.WAITING, "WaitingToGoTo");
		((AgentPropertyTransition) tr).modify(conditions);

	}

}
