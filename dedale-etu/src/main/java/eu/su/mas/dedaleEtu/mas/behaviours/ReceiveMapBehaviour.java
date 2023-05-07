package eu.su.mas.dedaleEtu.mas.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.HashMap;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class ReceiveMapBehaviour extends SimpleBehaviour{

    private static final long serialVersionUID = 4873083083120096534L;
	private final BaseAgent myagent;
    private MapRepresentation myMap;
    private int nextBehaviourSelect; // 1 = no map received return to explore 2 = map ok sending map

    public ReceiveMapBehaviour(final BaseAgent myagent) {
        super(myagent);
        this.myagent = myagent;
    }

    public void action() {
        //System.out.println(this.myAgent.getLocalName() +" Is waiting for a Map");
        nextBehaviourSelect = 1;
        ACLMessage received = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM),50);
        if(received != null){
        	SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
        	try {
				sgreceived = (SerializableSimpleGraph<String, MapAttribute>)received.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	myagent.mergeMap(sgreceived);
            nextBehaviourSelect = 2;
            System.out.println(this.myagent.getLocalName() + " : --received map from " + received.getSender().getLocalName());
        }	
        
    }

    @Override
    public boolean done() {
        if (nextBehaviourSelect != 1)
            if(myagent.getPreviousBehaviour().equals("RequestConnectionBehaviour")){
                nextBehaviourSelect = 2 ; //pass to send map
            }else{ //ca vient de sendmap
                nextBehaviourSelect = 1; //pass to exp
            }
        myagent.setPreviousBehaviour("ReceiveMapBehaviour");
        return true;
    }

    public int onEnd() {
        return nextBehaviourSelect;
    }
}
