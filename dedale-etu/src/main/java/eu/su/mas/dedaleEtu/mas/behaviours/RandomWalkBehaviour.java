package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
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
import jade.core.behaviours.TickerBehaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR RandomWalk : Illustrates how an agent can interact with, and move in, the environment
 * 
 * 
 **************************************/


public class RandomWalkBehaviour extends SimpleBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private int finished;

	public RandomWalkBehaviour (final AbstractDedaleAgent myagent) {
		super(myagent);
		this.finished = 0;
	}

	@Override
	public void action() {
		//Example to retrieve the current position
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		//System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			
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
				this.finished = 1;
			}

//			//Little pause to allow you to follow what is going on
//			try {
//				System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
//				System.in.read();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			//list of observations associated to the currentPosition
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();

//			//example related to the use of the backpack for the treasure hunt
//			Boolean b=false;
//			for(Couple<Observation,Integer> o:lObservations){
//				switch (o.getLeft()) {
//				case DIAMOND:case GOLD:
//					System.out.println(this.myAgent.getLocalName()+" - My treasure type is : "+((AbstractDedaleAgent) this.myAgent).getMyTreasureType());
//					System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
//					System.out.println(this.myAgent.getLocalName()+" - Value of the treasure on the current position: "+o.getLeft() +": "+ o.getRight());
//					System.out.println(this.myAgent.getLocalName()+" - The agent grabbed :"+((AbstractDedaleAgent) this.myAgent).pick());
//					System.out.println(this.myAgent.getLocalName()+" - the remaining backpack capacity is: "+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
//					b=true;
//					break;
//				default:
//					break;
//				}
//			}

//			//If the agent picked (part of) the treasure
//			if (b){
//				List<Couple<Location,List<Couple<Observation,Integer>>>> lobs2=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
//				System.out.println(this.myAgent.getLocalName()+" - State of the observations after trying to pick something "+lobs2);
//			}

			//Random move from the current position
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);//removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move

			//The move action (if any) should be the last action of your behaviour
			Boolean success = ((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
			while (success==false) {
				Random r2= new Random();
				int moveId2=1+r2.nextInt(lobs.size()-1);
				success = ((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
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
		return finished;
	}

}