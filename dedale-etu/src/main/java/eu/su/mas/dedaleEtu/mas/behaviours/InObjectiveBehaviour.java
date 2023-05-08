package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class InObjectiveBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = -9051659611785488702L;
	private CollectorAgent myagent;
	private int endValue;
	private Boolean opened = false;
	
	public InObjectiveBehaviour(final CollectorAgent myagent) {
		super(myagent);
		this.myagent = myagent;
		this.endValue = 1;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		String missionOrder = this.myagent.getMission().getLeft().getRight();
		if (missionOrder.equals("Collect")) {
			Boolean success = false;
			if (opened==false) {
				while(success==false) {
					success = this.myagent.openLock(this.myagent.getMyTreasureType());
				}
			}
			this.myagent.pick();
			
			if (this.myagent.getMission().getRight().isEmpty()==false) {
				ACLMessage ok = new ACLMessage(ACLMessage.INFORM);
				ok.setSender(this.myagent.getAID());
				ok.setProtocol("COLLECT-OK");
				for (String helper : this.myagent.getMission().getRight()) {
					ok.addReceiver(new AID(helper,AID.ISLOCALNAME));
				}
				try {
					ok.setContentObject(((BaseAgent)this.myAgent).getCurrentPosition());
				} catch (IOException e) {
					e.printStackTrace();
				}
				((AbstractDedaleAgent)this.myAgent).sendMessage(ok);
			}
			this.opened = false;
			this.endValue = 1;
		} else {
			Location target = null;
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("COLLECT-OK"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage received = this.myAgent.blockingReceive(mt);
	        if(received != null){
	        	Location sreceived=null;
	        	try {
					sreceived = (Location)received.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	target = sreceived;
	        } else {
	        	block();
	        }
	        
	        ACLMessage ok2 = new ACLMessage(ACLMessage.INFORM);
			ok2.setSender(this.myagent.getAID());
			ok2.setProtocol("COLLECT-OK");
			for (String helper : this.myagent.getMission().getRight()) {
				if (helper.equals(this.myagent.getLocalName())==false) {
					ok2.addReceiver(new AID(helper,AID.ISLOCALNAME));
				}
			}
			try {
				ok2.setContentObject(target);
			} catch (IOException e) {
				e.printStackTrace();
			}
			((AbstractDedaleAgent)this.myAgent).sendMessage(ok2);
			
			if (missionOrder.equals("Collect-help")) {
				Couple<Couple<Location, String>, List<String>> newMission = 
						new Couple<Couple<Location, String>, List<String>>(new Couple<Location, String>
						(target, "Collect"), new ArrayList<String>());
				this.opened = true;
				this.endValue = 2;
				
			}
		}
		

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public int onEnd() {
		return endValue;
	}

}
