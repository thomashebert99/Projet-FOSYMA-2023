package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;

import java.util.List;

import org.glassfish.pfl.dynamic.codegen.impl.ExpressionFactory.ThisExpression;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.EntityCharacteristics;
import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.CheckMailBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GoToObjectiveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GoToTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.InObjectiveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMissionBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RequestConnectionBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalStats;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class CollectorAgent extends BaseAgent {
	
	private static final long serialVersionUID = -2015901968838395743L;
	private String tankerPos;
	private Couple<PersonalStats, Boolean> infos;
	private Couple<Couple<Location, String>, List<String>> mission;
	

	protected void setup() {
		
		super.setup();
		
		this.infos = new Couple<PersonalStats, Boolean>(new PersonalStats(this), false);
		
		FSMBehaviour fsmBehaviour = new FSMBehaviour();
        fsmBehaviour.registerFirstState(new ExplorationBehaviour(this),"explo");
        fsmBehaviour.registerState(new CheckMailBehaviour(this),"mail");
        fsmBehaviour.registerState(new RequestConnectionBehaviour(this),"connection");
        fsmBehaviour.registerState(new SendMapBehaviour(this),"send");
        fsmBehaviour.registerState(new ReceiveMapBehaviour(this),"receive");
        fsmBehaviour.registerState(new GoToTankerBehaviour(this), "goToTanker");
        fsmBehaviour.registerState(new ReceiveMissionBehaviour(this), "receiveM");
        fsmBehaviour.registerState(new GoToObjectiveBehaviour(this), "goToObj");
        fsmBehaviour.registerState(new InObjectiveBehaviour(this), "inObj");
        fsmBehaviour.registerState(new RandomWalkBehaviour(this), "random");


        fsmBehaviour.registerTransition("explo","mail",1); //explore to check mail
        fsmBehaviour.registerTransition("mail","connection",2); //check mail to start com
        fsmBehaviour.registerTransition("mail","send",1); //check mail to send map
        fsmBehaviour.registerTransition("connection","receive",1); //com to receive
        fsmBehaviour.registerTransition("send","receive",1); // send to receive
        fsmBehaviour.registerTransition("send","explo",2); // send to explo
        fsmBehaviour.registerTransition("receive","explo",1); // receive to explolore
        fsmBehaviour.registerTransition("receive","send",2); // receive to send
        
        fsmBehaviour.registerTransition("explo", "goToTanker", 2);
        fsmBehaviour.registerTransition("goToTanker", "receiveM", 0);
        fsmBehaviour.registerTransition("receiveM", "goToObj", 1);
        fsmBehaviour.registerTransition("receiveM", "random", 2);
        fsmBehaviour.registerTransition("goToObj", "inObj", 0);
        fsmBehaviour.registerTransition("inObj", "goToTanker", 1);
        fsmBehaviour.registerTransition("inObj", "goToObj", 2);
        fsmBehaviour.registerTransition("random", "random", 0);
        fsmBehaviour.registerTransition("random", "goToTanker", 1);

        List<Behaviour> lb=new ArrayList<Behaviour>();
        
        lb.add(fsmBehaviour);
        
        addBehaviour(new startMyBehaviours(this,lb));
        
        System.out.println("the  agent "+this.getLocalName()+ " is started");

		
	}

	public String getTankerPos() {
		return tankerPos;
	}

	public void setTankerPos(String tankerPos) {
		this.tankerPos = tankerPos;
	}

	public Couple<PersonalStats, Boolean> getInfos() {
		return infos;
	}

	public void setInfosSent() {
		this.infos = new Couple<PersonalStats, Boolean>(new PersonalStats(this), true);
	}

	public Couple<Couple<Location, String>, List<String>> getMission() {
		return mission;
	}

	public void setMission(Couple<Couple<Location, String>, List<String>> mission) {
		this.mission = mission;
	}
	
	
	
	
}
