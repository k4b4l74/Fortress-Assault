package ssell.FortressAssault.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import ssell.FortressAssault.FortressAssault;

public class SnowBallSnare {

	private static SnowBallSnare instance = null;
	private Map<String, Long> playersSnare = null;
	private float defaultSnaring = (float) 0.5;
	private int snareDelaySeconds = 2;
	
	public SnowBallSnare() {
		playersSnare = new HashMap<String, Long>();
	}
	
	public static SnowBallSnare getInstance(FortressAssault plugin) {
		if (instance == null) {
			instance = new SnowBallSnare();
		}
		return instance;
	}
	
	/**
	 * UnSnare the player if necessary
	 * @param event
	 */
	public void onPlayerMove(PlayerMoveEvent event) {
		checkForRemovingSnare(event.getPlayer());
	}
	
	/**
	 * Snare the player on entity damage
	 * @param event
	 */
	public void onEntityDamage( EntityDamageEvent event, Player player) {
		if( event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent edpe = (EntityDamageByProjectileEvent) event;
	        if (edpe.getProjectile() instanceof Snowball) {
	        	//Snare
	        	snarePlayer((Player) event.getEntity());
	        }
		}
	}
	
	
	private void snarePlayer(Player player) {
		Long initialSnare = new Long(System.currentTimeMillis());
		playersSnare.put(player.getName(), initialSnare);
		Vector slowVelocity = player.getVelocity().multiply(defaultSnaring);
		player.setVelocity(slowVelocity);
	}
	
	private void checkForRemovingSnare(Player player) {
		if (playersSnare.containsKey(player.getName())) {
			long now = System.currentTimeMillis();
			long initialSnare = playersSnare.get(player.getName()).longValue();
			long maxSnare = initialSnare + (1000 * snareDelaySeconds);
			
			if (now > maxSnare) {
				playersSnare.remove(player.getName());
				Vector normalVelocity = player.getVelocity().multiply(1 / defaultSnaring);
				player.setVelocity(normalVelocity);
			}
		}
		
		
	}
}
