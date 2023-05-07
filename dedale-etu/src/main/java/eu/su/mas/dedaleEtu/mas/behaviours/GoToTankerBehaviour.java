package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class GoToTankerBehaviour extends SimpleBehaviour {
	
	private CollectorAgent myagent;
	private boolean finished;
	private int endValue;

	private static final long serialVersionUID = 638863085310028432L;
	
	public GoToTankerBehaviour(final BaseAgent myagent) {
		super(myagent);
		this.myagent = (CollectorAgent) myagent;
		this.finished = false;
		this.endValue = 1;
	}
	@Override
	public void action() {

		Location myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
		int cpt=0;
		String tankerPos = null;
		
		if (this.myagent.getTankerPos()==null) {
			tankerPos = this.myagent.getMyMap().centralize();
		} else {
			tankerPos = this.myagent.getTankerPos();
		}
		List<String> pathtoTanker = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), tankerPos);
		if (pathtoTanker.size()>0) {
			for(int k=0;k<pathtoTanker.size()-1;k++) {
				myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
				pathtoTanker=this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(),tankerPos);
				if (pathtoTanker.size() < 2) {
					Boolean empty = this.myagent.emptyMyBackPack("Tanker");
					if (empty) {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setSender(this.myagent.getAID());
						msg.setProtocol("SHARE-TREASURES");
						msg.addReceiver(new AID("Tanker",AID.ISLOCALNAME));
						try {					
							msg.setContentObject((Serializable) this.myagent.getTreasures());
						} catch (IOException e) {
							e.printStackTrace();
						}
						((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
						this.myagent.eraseTreasures();
						
						if (this.myagent.getInfos().getRight()==false) {
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.setSender(this.myagent.getAID());
							msg2.setProtocol("SHARE-INFOS");
							msg2.addReceiver(new AID("Tanker",AID.ISLOCALNAME));
							try {					
								msg2.setContentObject((Serializable) this.myagent.getInfos().getLeft());
							} catch (IOException e) {
								e.printStackTrace();
							}
							((AbstractDedaleAgent)this.myAgent).sendMessage(msg2);
							this.myagent.setInfosSent();
						}
						break;
					}
				}
				k=0;
				boolean succ=((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(pathtoTanker.get(k)));
				if(succ==false){
					cpt+=1;
					k=k-1;
				}
				if(cpt>1){
					Random r= new Random();
					List<Couple<Location, List<Couple<Observation, Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
					
					int moveId=1+r.nextInt(lobs.size()-1);
					((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
					myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					pathtoTanker = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), tankerPos	);
					k=0;
					cpt=0;
				}
			}
		}
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public int onEnd()
	{
		return this.endValue;
	}

}
