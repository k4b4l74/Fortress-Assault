package ssell.FortressAssault;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FASpecHandler {

	private Location observersSpawnLocation;
	private List<Player> observers;
	
	public FASpecHandler() {
		observersSpawnLocation = null;
		observers = new ArrayList<Player>();
	}
	
	public Location getLocation() {
		return observersSpawnLocation;
	}
	
	public void setLocation(Location location) {
		observersSpawnLocation = location;
	}
	
	public void removeObserver(Player player) {
		for (Iterator<Player> iterator = observers.iterator(); iterator.hasNext();) {
			Player observer = iterator.next();
			if (observer.getName().equalsIgnoreCase(player.getName())) {
				iterator.remove();
			}
		}
	}
	
	public List<Player> getObservers() {
		return observers;
	}
	
	public void addObserver(Player player) {
		observers.add(player);
	}
	
	public boolean isAnObserver(Player player) {
		for (Iterator<Player> iterator = observers.iterator(); iterator.hasNext();) {
			Player observer = iterator.next();
			if (observer.getName().equalsIgnoreCase(player.getName())) {
				return true;
			}
			
		}
		return false;
	}
	
	public void clearObservers() {
		observers = new ArrayList<Player>();
	}
}
