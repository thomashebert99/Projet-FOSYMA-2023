package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.CollectorAgent;

public class PersonalStats implements Serializable {
	
	private static final long serialVersionUID = 243337794892389380L;
	private int capacity;
	private Observation type;
	private int lockpicking;
	private int strength;
	
	public PersonalStats(CollectorAgent myagent) {
		super();
		
		this.type = myagent.getMyTreasureType();
		//this.type = null;
		
		this.capacity = 0;
		
//		this.lockpicking = 0;
//		this.strength = 0;
		
		List<Couple<Observation, Integer>> freeSpace =  myagent.getBackPackFreeSpace();
		for (Couple<Observation, Integer> t : freeSpace) {
			if (t.getLeft().equals(this.type) || this.type.equals(Observation.ANY_TREASURE)){
				this.capacity = t.getRight();
			}
		}
		
		for (Couple<Observation, Integer> c : myagent.getMyExpertise()) {
			if (c.getLeft().getName().equals("Strength")) {
				this.strength = c.getRight();
			}
			if (c.getLeft().getName().equals("LockPicking")) {
				this.lockpicking = c.getRight();
			}
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Observation getType() {
		return type;
	}

	public void setType(Observation type) {
		this.type = type;
	}

	public int getLockpicking() {
		return lockpicking;
	}

	public void setLockpicking(int lockpicking) {
		this.lockpicking = lockpicking;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonalStats other = (PersonalStats) obj;
		return capacity == other.capacity && lockpicking == other.lockpicking && strength == other.strength
				&& type == other.type;
	}
	
	
	

}


