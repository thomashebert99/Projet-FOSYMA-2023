package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalStats;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;

public class TankerAgent extends AbstractDedaleAgent{
	
	private static final long serialVersionUID = 5987018471096798774L;
	private Map<String, PersonalStats> agentsInfos = new HashMap<String, PersonalStats>();
	
	public Set<Treasure> getTreasures(){
		return this.getTreasures();
	}
	
	public void addTreasures(Set<Treasure> t) {
		this.addTreasures(t);
	}

	public Map<String, PersonalStats> getAgentsInfos() {
		return agentsInfos;
	}

	public void setAgentsInfos(Map<String, PersonalStats> agentsInfos) {
		this.agentsInfos = agentsInfos;
	}
	
	public void addPersonalInfos(String agent, PersonalStats infos) {
		if (agentsInfos.containsKey(agent)) {
			agentsInfos.replace(agent, infos);
		}
		else {
			agentsInfos.put(agent, infos);
		}
	}
	
	public List<String> getList_agentNames() {
		return this.getList_agentNames();
	}
	
	public MapRepresentation getMyMap() {
		return this.getMyMap();
	}

}
