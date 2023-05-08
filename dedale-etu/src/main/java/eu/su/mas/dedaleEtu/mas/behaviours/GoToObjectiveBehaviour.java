package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GoToObjectiveBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8191069475840319073L;
	private CollectorAgent myagent;
	private boolean finished;
	private int endValue;
	private int step = 0;
	
	public GoToObjectiveBehaviour(final BaseAgent myagent) {
		super(myagent);
		this.myagent = (CollectorAgent) myagent;
		this.finished = false;
		this.endValue = 1;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		Location myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
		int cpt=0;
		String target = this.myagent.getMission().getLeft().getLeft().getLocationId();
		
		List<String> pathtoObj = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), target);
		
		
		if (pathtoObj.size()>0) {
			for(int k=0;k<pathtoObj.size()-1;k++) {
				myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
				pathtoObj=this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(),target);
				
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
				boolean succ=((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(pathtoObj.get(k)));
				if(succ==false){
					cpt+=1;
					k=k-1;
				}
				if(cpt>1){
					Random r= new Random();
					
					int moveId=1+r.nextInt(lobs.size()-1);
					((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
					myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					pathtoObj = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), target);
					k=0;
					cpt=0;
				}
			}
			
			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(target));
		}

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public int onEnd() {
		return 0;
	}

}
