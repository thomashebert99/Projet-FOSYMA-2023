package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.TankerAgent;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.PersonalStats;
import eu.su.mas.dedaleEtu.mas.knowledge.Treasure;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class GiveMissionsBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = -6592117807161525821L;
	private int step = 0;
	private int repliesCnt = 0;
	private Boolean statsOK = false;
	private TankerAgent myagent;
	private List<Treasure> listTreasures = new ArrayList<Treasure>();
	
	public GiveMissionsBehaviour(final TankerAgent myagent) {
		this.myagent = myagent;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		switch(step) {
		case 0:
			Boolean treasureNull = false;
			Boolean infosNull = false;
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("SHARE-TREASURES"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage received = this.myAgent.receive(mt);
	        if(received != null){
	        	Set<Treasure> sreceived = null;
	        	try {
					sreceived = (Set<Treasure>) received.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	this.myagent.addTreasures(sreceived);
	        	repliesCnt++;
	        	
	        } else {
	        	treasureNull = true;
	        }
	        if (statsOK==false) {
		        MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchProtocol("SHARE-INFOS"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage received2 = this.myAgent.receive(mt2);
		        if(received2 != null){
		        	PersonalStats sreceived2 = null;
		        	try {
						sreceived2 = (PersonalStats) received2.getContentObject();
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	this.myagent.addPersonalInfos(received2.getSender().getLocalName(), sreceived2);
		        	if (this.myagent.getList_agentNames().size()==this.myagent.getAgentsInfos().size()) {
		        		statsOK = true;
		        	}
		        } else {
		        	infosNull = true;
		        }
	        }
	        
	        if (repliesCnt >= this.myagent.getList_agentNames().size()) {
	        	step = 1;
	        }
	        else if ((treasureNull && infosNull) || (treasureNull && statsOK)) {
	        	block();
	        }
		
		case 1:
			listTreasures.addAll(this.myagent.getTreasures());
			Boolean empty = false;
			if (listTreasures.isEmpty()) {
				empty = true;
			}
			Map<String, PersonalStats> agentsName = new HashMap<String, PersonalStats>(this.myagent.getAgentsInfos());
			
			while(agentsName.isEmpty()==false) {
				Date oldest = null;
				Treasure obj = null;
				if (empty==false) {
					for (Treasure t : listTreasures) {
						if (t.getTime().compareTo(oldest) < 0) {
							oldest = t.getTime();
							obj = t;
						}
					}
				}
				Couple<String, List<Couple<String, String>>> mission = null;
				if (empty==false) {
					mission = this.findBestConfig(obj, agentsName);
				}
				ACLMessage msgMission = new ACLMessage(ACLMessage.REQUEST);
				msgMission.setSender(this.myagent.getAID());
				msgMission.setProtocol("MISSION");
				if (empty==false) {
					msgMission.addReceiver(new AID(mission.getLeft(),AID.ISLOCALNAME));
				} else {
					for (String name : agentsName.keySet()) {
						msgMission.addReceiver(new AID(name,AID.ISLOCALNAME));
					}
				}
				try {
					List<String> helpers = new ArrayList<String>();
					if (empty==false) {
						for (Couple<String, String> helper : mission.getRight()) {
							helpers.add(helper.getLeft());
						}
						msgMission.setContentObject(new Couple<Couple<Location, String>, List<String>>(new Couple<Location, String>(obj.getLocation(), "Collect"), helpers));
					} else {
						msgMission.setContentObject(new Couple<Couple<Location, String>, List<String>>(new Couple<Location, String>(null, "Explore"), null));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				((AbstractDedaleAgent)this.myAgent).sendMessage(msgMission);
				
				if (empty==false) {
					if (mission.getRight().isEmpty()==false) {
						List<String> neighbors = this.myagent.getMyMap().getNeighborNodes(obj.getLocation().getLocationId());
						String previousTarget = null;
						List<String> previousNeighbors = new ArrayList<String>();
						for (Couple<String, String> helper : mission.getRight()) {
							String target = null;
							for (String n : neighbors) {
								if ((n.equals(previousTarget) || previousNeighbors.contains(n))==false){
									target = n;
								}
							}
							ACLMessage msgMission2 = new ACLMessage(ACLMessage.REQUEST);
							msgMission2.setSender(this.myagent.getAID());
							msgMission2.setProtocol("MISSION");
							msgMission2.addReceiver(new AID(helper.getLeft(),AID.ISLOCALNAME));
							try {
								List<String> helpers = new ArrayList<String>();
								for (Couple<String, String> helper2 : mission.getRight()) {
									if (helper2.getLeft().equals(helper.getLeft())==false) {
										helpers.add(helper2.getLeft());
									}
								}
								helpers.add(mission.getLeft());
								msgMission2.setContentObject(new Couple<Couple<Location, String>, List<String>>(new Couple<Location, String>(new gsLocation(target), helper.getRight()), helpers));
							} catch (IOException e) {
								e.printStackTrace();
							}
							((AbstractDedaleAgent)this.myAgent).sendMessage(msgMission2);
							previousTarget = target;
							previousNeighbors = neighbors;
							neighbors = this.myagent.getMyMap().getNeighborNodes(target);
						}
					}
					agentsName.remove(mission.getLeft());
					for (Couple<String, String> helper : mission.getRight()) {
						agentsName.remove(helper.getLeft());
					}
					listTreasures.remove(obj);
				} else {
					agentsName.clear();
				}
			}
			break;
			
		}

	}
	
	public Couple<String, List<Couple<String, String>>> findBestConfig(Treasure t, Map<String, PersonalStats> agentsList) {
		
		String collector = null;
		List<Couple<String, String>> helpers = new ArrayList<Couple<String, String>>();
		int nessCp = t.getQuantity();
		int nessLp = t.getLockpicking();
		int nessSt = t.getStrength();
		
		Map<String, PersonalStats> potentialCollectors = agentsList.entrySet()
		        .stream().filter(x->x.getValue().getType().equals(t.getType()) ||
		        					x.getValue().getType().equals(Observation.ANY_TREASURE))
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		Map<String, Integer> capacityMap = new HashMap<String, Integer>();
		Map<String, Integer> lockpickingMap = new HashMap<String, Integer>();
		Map<String, Integer> strengthMap = new HashMap<String, Integer>();
		for (String agent : potentialCollectors.keySet()) {
			capacityMap.put(agent, potentialCollectors.get(agent).getCapacity());
			lockpickingMap.put(agent, potentialCollectors.get(agent).getLockpicking());
			strengthMap.put(agent, potentialCollectors.get(agent).getStrength());
		}
		
		capacityMap = this.triAvecValeur(capacityMap);
		lockpickingMap = this.triAvecValeur(lockpickingMap);
		strengthMap = this.triAvecValeur(strengthMap);
		
		Map<String, Integer> capacityMapCopy = this.deepCopy(capacityMap);
		Map<String, Integer> lockpickingMapCopy = this.deepCopy(lockpickingMap);
		Map<String, Integer> strengthMapCopy = this.deepCopy(strengthMap);
		
		
		List<Integer> temp = (List<Integer>) capacityMapCopy.values();
		int temp2 = temp.get(0);
		
		if (temp2 >= t.getQuantity()) {
			for (String agent : capacityMap.keySet()) {
				if (capacityMapCopy.get(agent) < t.getQuantity()) {
					capacityMapCopy.remove(agent);
					lockpickingMapCopy.remove(agent);
					strengthMapCopy.remove(agent);
				}
			}
			
			temp = (List<Integer>) lockpickingMapCopy.values();
			temp2 = temp.get(0);
			
			if (temp2 >= t.getLockpicking()) {
				for (Iterator<Map.Entry<String, Integer>> agent = lockpickingMapCopy.entrySet().iterator(); agent.hasNext();) {
					Map.Entry<String, Integer> entry = agent.next();
					if (entry.getValue() < t.getLockpicking()) {
						agent.remove();
						strengthMapCopy.remove(entry.getKey());
					}
				}
				
				temp = (List<Integer>) strengthMapCopy.values();
				temp2 = temp.get(0);
				
				if (temp2 >= t.getStrength()) {
					for (Iterator<Map.Entry<String, Integer>> agent = strengthMapCopy.entrySet().iterator(); agent.hasNext();) {
						Map.Entry<String, Integer> entry = agent.next();
						if (entry.getValue() < t.getStrength()) {
							agent.remove();
						}
					}
					
					for (String agent : strengthMapCopy.keySet()) {
						collector = agent;
					}
					
				} else {
					for (String agent : strengthMapCopy.keySet()) {
						collector = agent;
						break;
					}
				}
				
			} else {
				for (String agent : lockpickingMapCopy.keySet()) {
					collector = agent;
					break;
				}
			}
			
		} else {
			for (String agent : capacityMap.keySet()) {
				collector = agent;
				break;
			}
		}
		
		nessCp -= agentsList.get(collector).getCapacity();
		nessLp -= agentsList.get(collector).getLockpicking();
		nessSt -= agentsList.get(collector).getStrength();
		
		capacityMapCopy = this.deepCopy(capacityMap);
		lockpickingMapCopy = this.deepCopy(lockpickingMap);
		strengthMapCopy = this.deepCopy(strengthMap);
		
		while (nessCp > 0) {
			temp = (List<Integer>) capacityMapCopy.values();
			temp2 = temp.get(0);
			
			if (temp2 >= t.getQuantity()) {
				for (Iterator<Map.Entry<String, Integer>> agent = capacityMapCopy.entrySet().iterator(); agent.hasNext();) {
					Map.Entry<String, Integer> entry = agent.next();
					if (entry.getValue() < t.getQuantity()) {
						agent.remove();
					}
				}
				String helper = null;
				for (String agent : capacityMapCopy.keySet()) {
					helper = agent;
				}
				helpers.add(new Couple<String, String>(helper, "Collect-help"));
				nessCp -= agentsList.get(helper).getCapacity();
				
			} else {
				for (String agent : capacityMapCopy.keySet()) {
					helpers.add(new Couple<String, String>(agent, "Collect-help"));
					nessCp -= agentsList.get(agent).getCapacity();
					break;
				}
			}
		}
		
		for (Couple<String, String> helper : helpers) {
			lockpickingMapCopy.remove(helper.getLeft());
		}
		
		while (nessLp > 0) {
			temp = (List<Integer>) lockpickingMapCopy.values();
			temp2 = temp.get(0);
			
			if (temp2 >= t.getLockpicking()) {
				for (Iterator<Map.Entry<String, Integer>> agent = lockpickingMapCopy.entrySet().iterator(); agent.hasNext();) {
					Map.Entry<String, Integer> entry = agent.next();
					if (entry.getValue() < t.getLockpicking()) {
						agent.remove();
					}
				}
				String helper = null;
				for (String agent : lockpickingMapCopy.keySet()) {
					helper = agent;
				}
				helpers.add(new Couple<String, String>(helper, "Boost"));
				nessLp -= agentsList.get(helper).getLockpicking();
				
			} else {
				for (String agent : lockpickingMapCopy.keySet()) {
					helpers.add(new Couple<String, String>(agent, "Boost"));
					nessLp -= agentsList.get(agent).getLockpicking();
					break;
				}
			}
		}
		
		for (Couple<String, String> helper : helpers) {
			strengthMapCopy.remove(helper.getLeft());
		}
		
		while (nessSt > 0) {
			temp = (List<Integer>) strengthMapCopy.values();
			temp2 = temp.get(0);
			
			if (temp2 >= t.getStrength()) {
				for (Iterator<Map.Entry<String, Integer>> agent = strengthMapCopy.entrySet().iterator(); agent.hasNext();) {
					Map.Entry<String, Integer> entry = agent.next();
					if (entry.getValue() < t.getStrength()) {
						agent.remove();
					}
				}
				String helper = null;
				for (String agent : strengthMapCopy.keySet()) {
					helper = agent;
				}
				helpers.add(new Couple<String, String>(helper, "Boost"));
				nessSt -= agentsList.get(helper).getStrength();
				
			} else {
				for (String agent : strengthMapCopy.keySet()) {
					helpers.add(new Couple<String, String>(agent, "Boost"));
					nessSt -= agentsList.get(agent).getLockpicking();
					break;
				}
			}
		}
		
		return new Couple<String, List<Couple<String, String>>>(collector, helpers);
		
	}
	
	public Map deepCopy(Map map){
		Map result = new HashMap();
		for (Object o : map.keySet()) {
			result.put(o, map.get(o));
		}
		result = Collections.unmodifiableMap(result);
		return result;
	}
	
	public Map<String, Integer> triAvecValeur( Map<String, Integer> map ){
		 List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>( map.entrySet() );
		 Collections.sort( list, new Comparator<Map.Entry<String, Integer>>(){
			 public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ){
				 return (o2.getValue()).compareTo( o1.getValue());
			 }
		 });

		 Map<String, Integer> map_apres = new LinkedHashMap<String, Integer>();
		 for(Map.Entry<String, Integer> entry : list)
		 map_apres.put( entry.getKey(), entry.getValue() );
		 return map_apres;
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
