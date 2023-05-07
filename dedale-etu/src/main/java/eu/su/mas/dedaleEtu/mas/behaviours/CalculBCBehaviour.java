package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import jade.core.behaviours.SimpleBehaviour;

public class CalculBCBehaviour extends SimpleBehaviour {
	
	private String BC;
	private BaseAgent myagent;
	private boolean finished;
	private int endValue;

	private static final long serialVersionUID = 638863085310028432L;
	
	public CalculBCBehaviour(final BaseAgent myagent) {
		super(myagent);
		this.BC = null;
		this.myagent = myagent;
		this.finished = false;
		this.endValue = 1;
	}
	@Override
	public void action() {

		Location myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
		
		if (this.BC == null) {
			int cpt=0;
			this.BC = this.myagent.getMyMap().centralize();
			List<String> pathtoBC = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), BC);
			if (pathtoBC.size()>0) {
				for(int k=0;k<pathtoBC.size()-1;k++) {
					myPosition=((BaseAgent)this.myAgent).getCurrentPosition();
					pathtoBC=this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(),BC);
					k=0;
					boolean succ=((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(pathtoBC.get(k)));
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
						pathtoBC = this.myagent.getMyMap().getShortestPath(myPosition.getLocationId(), BC);
						k=0;
						cpt=0;
					}
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
