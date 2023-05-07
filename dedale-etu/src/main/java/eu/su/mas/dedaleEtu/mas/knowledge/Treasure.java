package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;

public class Treasure implements Serializable{
	
	private static final long serialVersionUID = -6189835795744610927L;
	private Location location;
	private	Observation type;
	private Integer quantity;
	private Integer lockpicking;
	private Integer strength;
	private Date time;
	
	public Treasure(Location location, Observation type, Integer quantity, Integer lockpicking, Integer strength,
			Date time) {

		this.location = location;
		this.type = type;
		this.quantity = quantity;
		this.lockpicking = lockpicking;
		this.strength = strength;
		this.time = time;
	}

	public Location getLocation() {
		return location;
	}

	public Observation getType() {
		return type;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Integer getLockpicking() {
		return lockpicking;
	}

	public Integer getStrength() {
		return strength;
	}

	public Date getTime() {
		return time;
	}
	
	public int compareTo(Object o) {
		return ((Treasure)o).getQuantity().compareTo(this.getQuantity());
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, lockpicking, quantity, strength, time, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Treasure other = (Treasure) obj;
		return Objects.equals(location, other.location) && Objects.equals(lockpicking, other.lockpicking)
				&& Objects.equals(quantity, other.quantity) && Objects.equals(strength, other.strength)
				&& Objects.equals(time, other.time) && type == other.type;
	}
	
	
	
	
}
