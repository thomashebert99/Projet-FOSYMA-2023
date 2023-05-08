package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMissionBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = 373370365185964498L;
	private final CollectorAgent myagent;
	
	public ReceiveMissionBehaviour(final CollectorAgent myagent) {
        super(myagent);
        this.myagent = myagent;
    }

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		ACLMessage received = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        if(received != null){
        	Couple<Couple<Location, String>, List<String>> sreceived = null;
        	try {
				sreceived = (Couple<Couple<Location, String>, List<String>>)received.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	this.myagent.setMission(sreceived);
        } else {
        	block();
        }

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public int onEnd() {
		if (this.myagent.getMission().getLeft().getRight().equals("Explore")) {
			return 2;
		}
		return 1;
	}

}
