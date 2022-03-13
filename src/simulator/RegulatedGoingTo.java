package simulator;

import agent.NetworkAgent;
import agent.fsm.state.BehaviorStateName;
import agent.fsm.transition.AgentPropertyTransition;
import agent.fsm.transition.Transition;
import environment.physical.Location;
import pbc.Constant;
import pbc.Description;
import pbc.Property;
import sharedInformation.Properties;
import vehicle.behaviorState.GoingTo;
import vehicle.behaviorState.VehicleBehaviorStateNameEnum;

public class RegulatedGoingTo extends GoingTo{

	public RegulatedGoingTo(NetworkAgent agent, BehaviorStateName stateName) {
		super(agent, stateName);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initState(Description parameters) {
		RegulatedVehicle regulatedVehicle = (RegulatedVehicle) this.getAgent();
		regulatedVehicle.getLocation().clean();
		// Pickup transition update
		Description conditions = new Description();
		conditions.put(Property.propertyFactory(Properties.Location, "NetworkLocation"),
				new Constant<Location>(regulatedVehicle.getDestination()));
		Transition tr = regulatedVehicle.getBehavior().getTransition(VehicleBehaviorStateNameEnum.GOINGTO, "goToToWaiting");
		((AgentPropertyTransition) tr).modify(conditions);

		
		
	}

}
