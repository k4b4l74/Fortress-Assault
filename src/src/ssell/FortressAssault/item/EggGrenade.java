package ssell.FortressAssault.item;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;

import ssell.FortressAssault.FortressAssault;

public class EggGrenade {

	private FortressAssault plugin;
	//Delai of explosion
	private int secondDelay = 1;
	//Yield
	private float yield = (float) 1.5;
	
	private static EggGrenade instance = null;
	
	public EggGrenade(FortressAssault aPlugin) {
		plugin = aPlugin;
	}
	
	public static EggGrenade getInstance(FortressAssault plugin) {
		if (instance == null) {
			instance = new EggGrenade(plugin);
		}
		return instance;
	}
	
	public void onEntityDamage( EntityDamageEvent event, Player player) {
		if (event instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent damageEvent = (EntityDamageByBlockEvent) event;
			if (damageEvent.getDamager().getType() == Material.TNT) {
				event.setDamage(1);
			}
		}
	}
	
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		Egg egg = event.getEgg();
    	Location loc = egg.getLocation();
    	World world = ((CraftWorld)loc.getWorld()).getHandle();
    	eggThrown(loc, player, world, egg, event);		
	}
	
	private void eggThrown(final Location loc, Player player, final net.minecraft.server.World world, Egg egg, Event event){
		long actualDelayTime = secondDelay * 20;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			    public void run() {
			    	EntityTNTPrimed tnt = new EntityTNTPrimed((net.minecraft.server.World) world, loc.getX(), loc.getY(), loc.getZ());
					world.a(tnt, loc.getX(), loc.getY(), loc.getZ(), yield);
			    }
			}, actualDelayTime);
	}
}
