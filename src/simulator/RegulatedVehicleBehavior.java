package simulator;

import java.util.HashMap;

import agent.NetworkAgent;
import agent.fsm.state.BehaviorAutomata;
import agent.fsm.transition.AgentPropertyTransition;
import agent.fsm.transition.DurationTransition;
import agent.fsm.transition.Transition;
import environment.physical.Location;
import pbc.Constant;
import pbc.Description;
import pbc.Property;
import sharedInformation.ParameterEnum;
import sharedInformation.Properties;
import vehicle.behaviorState.GoingTo;
import vehicle.behaviorState.Moving;
import vehicle.behaviorState.VehicleBehaviorStateNameEnum;
import vehicle.behaviorState.Waiting;


public class RegulatedVehicleBehavior extends BehaviorAutomata{

	
	protected int waitingTime = 2;
	
	public RegulatedVehicleBehavior(NetworkAgent a, boolean trace) {
		super(a, "regulatedVehicle", trace);
	}

    @Override
    public void initBehavior(HashMap<ParameterEnum, Object> additionalData) {
        /*
         * State initialization
		 */    
        Waiting waiting = new  Waiting(agent,VehicleBehaviorStateNameEnum.WAITING);
        		//WaitingDeparture(agent,VehicleBehaviorStateNameEnum.WAITING);
        Moving goTo = new RegulatedGoingTo(agent,VehicleBehaviorStateNameEnum.GOINGTO);
        
        /*
         * Initiate First State and Transition
         */
        setFirstState(waiting);

        Description conditions = new Description();
        conditions.put(Property.propertyFactory(Properties.Cycle, "Double"), new Constant<Double>(null));

        
        Transition waitingToGoTo = new DurationTransition("WaitingToGoTo", 1, waitingTime);
        addStateAndTransition(waitingToGoTo, waiting.getName(), goTo);
        
        conditions = new Description();
        conditions.put(Property.propertyFactory(Properties.Location, "Location"), new Constant<Location>(null));
        
        Transition goToToWaiting = new AgentPropertyTransition("goToToWaiting", 1, conditions);
        addStateAndTransition(goToToWaiting, goTo.getName(), waiting);
    }
    
    

}
