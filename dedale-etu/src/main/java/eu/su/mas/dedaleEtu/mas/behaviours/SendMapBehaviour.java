package eu.su.mas.dedaleEtu.mas.behaviours;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class SendMapBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = 7504267628641151550L;
	private int nextBehaviourSelect; //1 = go to receive map , 2 = go to send position
    private BaseAgent myagent;


    //TODO gerer la terminaison du behaviour.
    public SendMapBehaviour(final BaseAgent myagent){
    	super(myagent);
    	this.myagent = myagent;
    	
    }

    @Override
    public void action() {
    	
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.myagent.getAID());
        msg.setProtocol("SHARE-MAP");
        for (String agentName : this.myagent.getList_agentNames()) {
			msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
		}
        
        System.out.println(this.myagent.getLocalName() + " : --map null = " + (this.myagent.getMyMap()==null));
        SerializableSimpleGraph<String, MapAttribute> sg=this.myagent.getMyMap().getSerializableGraph();
		try {					
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		System.out.println(this.myagent.getLocalName() + " : --send map to " + this.myagent.getList_agentNames());
    }

    @Override
    public boolean done() {
        if(myagent.getPreviousBehaviour().equals("CheckMailBehavior")){
            nextBehaviourSelect = 1; // pass to receive map
        }else{
            nextBehaviourSelect = 2; //pass to "exp
        }
        myagent.setPreviousBehaviour("SendMapBehaviour");
        return true;
    }

    public int onEnd() {
        return nextBehaviourSelect;
    }
}
