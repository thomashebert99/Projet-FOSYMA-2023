package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;

public class BaseAgent extends AbstractDedaleAgent {
	
	private static final long serialVersionUID = -3419842586282508535L;
	private String previousBehaviour;
	private MapRepresentation myMap;
	private Set<Treasure> treasures = new HashSet<Treasure>();
	private List<String> list_agentNames=new ArrayList<String>();
	
	protected void setup() {
		super.setup();
		
		final Object[] args = getArguments();
		
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames.add((String)args[i]);
				i++;
			}
		}
		
	}
	
	public String getPreviousBehaviour() {
        return previousBehaviour;
    }

    public void setPreviousBehaviour(String previousBehaviour) {
        this.previousBehaviour = previousBehaviour;
    }
    
    public void mergeMap(SerializableSimpleGraph<String, MapAttribute> map) {
    	this.myMap.mergeMap(map);
    }
    
    public void addTreasure(Treasure t) {
    	treasures.add(t);
    }

	public MapRepresentation getMyMap() {
		return myMap;
	}

	public void setMyMap(MapRepresentation myMap) {
		this.myMap = myMap;
	}

	public Set<Treasure> getTreasures() {
		return treasures;
	}

	public void setTreasures(Set<Treasure> treasures) {
		this.treasures = treasures;
	}
	
	public void addTreasures(Set<Treasure> t) {
		this.treasures.addAll(t);
	}
	
	public void eraseTreasures() {
		this.treasures = new HashSet<Treasure>();
	}
	
	public List<String> getList_agentNames() {
		return list_agentNames;
	}
    
    


}
