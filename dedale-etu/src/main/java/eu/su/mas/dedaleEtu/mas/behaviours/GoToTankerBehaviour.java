package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class GoToTankerBehaviour extends SimpleBehaviour {
	
	private CollectorAgent myagent;



	private static final long serialVersionUID = 638863085310028432L;
	
	public GoToTankerBehaviour(final BaseAgent myagent) {
		super(myagent);
		this.myagent = (CollectorAgent) myagent;
	}
	
	@Override
	public void action() {
		
		if (this.myagent.getTankerPos()==null) {
			Date avant = null;		
			try {
				System.out.println(this.myagent.getLocalName() + " : J'ATTENDS LONGTEMPS ");
				avant = new Date();
				Thread.sleep(100000);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
			Date apres = new Date();
			long temps = apres.getTime()-avant.getTime();
			System.out.println(this.myagent.getLocalName() + " : JE REPRENDS, TEMPS : " + temps);
		}

		Location myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
		int cpt=0;
		String tankerPos = null;
		
		if (this.myagent.getTankerPos()==null) {
			tankerPos = this.myagent.getMyMap().centralize();
			this.myagent.setTankerPos(tankerPos);
		} else {
			tankerPos = this.myagent.getTankerPos();
		}
		List<String> pathtoTanker = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), tankerPos);
		
		
		if (pathtoTanker.size()>0) {
			for(int k=0;k<pathtoTanker.size()-1;k++) {
				//System.out.println(this.myagent.getLocalName() + " : vers le tanker " + myPosition.getLocationId() + " vers " + pathtoTanker);
				myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
				pathtoTanker=this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(),tankerPos);
				
				List<Couple<Location, List<Couple<Observation, Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				
				try {
					this.myAgent.doWait(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(lobs.get(0).getRight().isEmpty()==false) {
					for (Couple<Observation, Integer> o : lobs.get(0).getRight()) {
						
						Observation type = null;
						Integer quantity = null;
						Integer lockpicking = null;
						Integer strength = null;
						
						switch(o.getLeft()) {
						
						case GOLD:
							type = o.getLeft();
							quantity = o.getRight();
						case DIAMOND:
							type = o.getLeft();
							quantity = o.getRight();
						case LOCKPICKING:
							lockpicking = o.getRight();
						case STRENGH:
							strength = o.getRight();
							
						}
						
						Treasure t = new Treasure(myPosition, type, quantity, lockpicking, strength, new Date());
						((BaseAgent) myAgent).addTreasure(t);
						
					}
				}
				
				if (pathtoTanker.size() <= 2) {
					//System.out.println(this.myagent.getLocalName() + " : à portée");
					Boolean empty = this.myagent.emptyMyBackPack("Tank");
					if (empty) {
						//System.out.println(this.myagent.getLocalName() + " : sac à dos vidé");
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setSender(this.myagent.getAID());
						msg.setProtocol("SHARE-TREASURES");
						msg.addReceiver(new AID("Tank",AID.ISLOCALNAME));
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
							msg2.addReceiver(new AID("Tank",AID.ISLOCALNAME));
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
				//System.out.println(this.myagent.getLocalName() + " : --goToTanker " + pathtoTanker);
				boolean succ=((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(pathtoTanker.get(k)));
				if(succ==false){
					cpt+=1;
					k=k-1;
				}
				if(cpt>1){
					Random r= new Random();
					
					int moveId=1+r.nextInt(lobs.size()-1);
					//System.out.println(this.myagent.getLocalName() + " : --test " + lobs.get(moveId).getLeft());
					((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
					myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					pathtoTanker = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), tankerPos);
					k=0;
					cpt=0;
				}
			}
			
			//System.out.println(this.myagent.getLocalName() + " : JE SORS DE LA BOUCLE " + ((BaseAgent)this.myAgent).getCurrentPosition() + " vers " + this.myagent.getMyMap().getShortestPath(((BaseAgent)this.myAgent).getCurrentPosition().getLocationId(),tankerPos));
		}
	}
	

	@Override
	public boolean done() {
		return true;
	}
	
	@Override
	public int onEnd() {
		return 0;
	}

}
