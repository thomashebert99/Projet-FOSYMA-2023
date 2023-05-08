package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.CalculBCBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.CheckMailBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GiveMissionsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GoToObjectiveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GoToTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.InObjectiveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMissionBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RequestConnectionBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalStats;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class TankerAgent extends BaseAgent{
	
	private static final long serialVersionUID = 5987018471096798774L;
	private Map<String, PersonalStats> agentsInfos = new HashMap<String, PersonalStats>();
	
	protected void setup() {
		
		super.setup();
		
		
		FSMBehaviour fsmBehaviour = new FSMBehaviour();
        fsmBehaviour.registerFirstState(new ExplorationBehaviour(this),"explo");
        fsmBehaviour.registerState(new CheckMailBehaviour(this),"mail");
        fsmBehaviour.registerState(new RequestConnectionBehaviour(this),"connection");
        fsmBehaviour.registerState(new SendMapBehaviour(this),"send");
        fsmBehaviour.registerState(new ReceiveMapBehaviour(this),"receive");
        fsmBehaviour.registerState(new CalculBCBehaviour(this),"BC");
        fsmBehaviour.registerState(new GiveMissionsBehaviour(this),"giveM");


        fsmBehaviour.registerTransition("explo","mail",1); //explore to check mail
        fsmBehaviour.registerTransition("mail","connection",2); //check mail to start com
        fsmBehaviour.registerTransition("mail","send",1); //check mail to send map
        fsmBehaviour.registerTransition("connection","receive",1); //com to receive
        fsmBehaviour.registerTransition("send","receive",1); // send to receive
        fsmBehaviour.registerTransition("send","explo",2); // send to explo
        fsmBehaviour.registerTransition("receive","explo",1); // receive to explolore
        fsmBehaviour.registerTransition("receive","send",2); // receive to send
        
        fsmBehaviour.registerTransition("explo", "BC", 2);
        fsmBehaviour.registerTransition("BC", "giveM", 0);
        fsmBehaviour.registerTransition("giveM", "giveM", 0);

        List<Behaviour> lb=new ArrayList<Behaviour>();
        
        lb.add(fsmBehaviour);
        
        addBehaviour(new startMyBehaviours(this,lb));
        
        System.out.println("the  agent "+this.getLocalName()+ " is started");

		
	}
	
//	public Set<Treasure> getTreasures(){
//		return this.getTreasures();
//	}
	
	public void addTreasures(Set<Treasure> t) {
		this.addTreasures(t);
	}

	public Map<String, PersonalStats> getAgentsInfos() {
		return agentsInfos;
	}

	public void setAgentsInfos(Map<String, PersonalStats> agentsInfos) {
		this.agentsInfos = agentsInfos;
	}
	
	public void addPersonalInfos(String agent, PersonalStats infos) {
		if (agentsInfos.containsKey(agent)) {
			agentsInfos.replace(agent, infos);
		}
		else {
			agentsInfos.put(agent, infos);
		}
	}
	
//	public List<String> getList_agentNames() {
//		return this.getList_agentNames();
//	}
	
//	public MapRepresentation getMyMap() {
//		return this.getMyMap();
//	}

}
