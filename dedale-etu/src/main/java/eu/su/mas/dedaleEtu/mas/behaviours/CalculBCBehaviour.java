package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Date;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import jade.core.behaviours.SimpleBehaviour;

public class CalculBCBehaviour extends SimpleBehaviour {
	
	private String BC;
	private BaseAgent myagent;

	private static final long serialVersionUID = 638863085310028432L;
	
	public CalculBCBehaviour(final BaseAgent myagent) {
		super(myagent);
		this.BC = null;
		this.myagent = myagent;
	}
	
	@Override
	public void action() {

		Location myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
		
		if (this.BC == null) {
			int cpt=0;
			this.BC = this.myagent.getMyMap().centralize();
			System.out.println(this.myagent.getLocalName() + " : OBJECTIF BC " + this.BC);
			List<String> pathtoBC = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), BC);
			
			
			if (pathtoBC.size()>0) {
				for(int k=0;k<pathtoBC.size()-1;k++) {
					myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
					pathtoBC=this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(),BC);
					
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
					
					k=0;
					boolean succ=((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(pathtoBC.get(k)));
					if(succ==false){
						cpt+=1;
						k=k-1;
					}
					
					if(cpt>1){
						Random r= new Random();
						int moveId=1+r.nextInt(lobs.size()-1);
						((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
						myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
						pathtoBC = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), BC);
						k=0;
						cpt=0;
					}
				}
				
				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(this.BC));
			}
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
