package ssell.FortressAssault;

import net.minecraft.server.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import ssell.FortressAssault.FortressAssault.ClassType;
import ssell.FortressAssault.FortressAssault.FAPlayer;
import ssell.FortressAssault.item.EggGrenade;
import ssell.FortressAssault.item.SnowBallSnare;

@SuppressWarnings("unused")
public class FAPlayerListener 
	extends PlayerListener
{
	private final FortressAssault plugin;
	private final FARespawnHandler respawnHandler;
	private final FASpecHandler specHandler;
	
	public FAPlayerListener( FortressAssault instance, FAEntityListener entity, FARespawnHandler respawn, FASpecHandler spec)
	{
		plugin = instance;
		respawnHandler = respawn;
		specHandler = spec;
	}
	
	@Override
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {	
			return;
		}
		//Launch the grenade
    	EggGrenade.getInstance(plugin).onPlayerEggThrow(event);
	}
	
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {	
			return;
		}
		double zTo = event.getTo().toVector().getY();
		double zFrom = event.getFrom().toVector().getY();
		if (zTo>zFrom) {
			/*
			double speed = -0.2;
			Vector velocity = player.getVelocity().clone();
			velocity.multiply(speed/velocity.length());			
			player.setVelocity(velocity);
			*/
			//player.sendMessage( ChatColor.RED + "MOVE:" + Double.toString(velocity.getY()));
			/*
			double speed = .2; // whatever
			Location loc = player.getLocation();		
			Vector target = new Vector(loc.getX(), loc.getY()+2, loc.getZ());
			Vector velocity = target.clone().subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));
			velocity.multiply(speed/velocity.length());
			player.setVelocity(velocity);
			//event.setCancelled(true);
			 */
		}
		
		//Check unfreeze
		SnowBallSnare.getInstance(plugin).onPlayerMove(event);
	}
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer != null) {			
				if (plugin.phase != 0) {
					Item item = event.getItemDrop();
					ItemStack itemStack =  item.getItemStack();
					if(itemStack != null) {
						if (itemStack.getType() != Material.GRILLED_PORK &&
							itemStack.getType() != Material.PORK &&
							itemStack.getType() != Material.APPLE &&
							itemStack.getType() != Material.GOLDEN_APPLE && 
							itemStack.getType() != Material.MUSHROOM_SOUP && 
							itemStack.getType() != Material.COOKED_FISH &&
							itemStack.getType() != Material.RAW_FISH &&
							itemStack.getType() != Material.BREAD &&
							itemStack.getType() != Material.COOKIE) {
							//can't drop that
							switch(thisPlayer.classtype) {
								case NONE:
									player.sendMessage( ChatColor.RED + "Can only drop food items" );
									break;
								case SCOUT:								
									double speed = .7;
									Location loc = player.getLocation();		
									Vector target = new Vector(loc.getX(), loc.getY()+50, loc.getZ());
									Vector velocity = target.clone().subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));
									velocity.multiply(speed/velocity.length());
									player.setVelocity(velocity);
									break;
								case PYRO:
									plugin.getAbilities().shootFlamerThrower(player);
									break;
							}
							
							event.setCancelled(true);							
							return;
						}
					}
				}
		}
	}
    
	
	public void onPlayerQuit( PlayerQuitEvent event) {
		//check if player that quits is in the game and return their inventory.
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer != null) {
			thisPlayer.disconnected = true;
		}		
	}
	
	public void onPlayerJoin( PlayerJoinEvent event) {
		//check if player that join is in the game. getFAPlayer will update their entityid properly.
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer != null) {
			thisPlayer.disconnected = false;
			//player came back during the game, restore their items and give them the proper equipment.
			player = plugin.getServer().getPlayer(ChatColor.stripColor(player.getDisplayName()));
			thisPlayer.player = player;
			if (plugin.phase != 0) {
				//not sure why but if a player dies then disconnects this will cause them to die when they log back in.
				//plugin.storeInventory(thisPlayer);
				plugin.giveGameItems(player);
			}
		}		
	}
	
	@Override
	public void onPlayerRespawn( PlayerRespawnEvent event )
	{	
		final PlayerRespawnEvent finalEvent = event;
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		
		if (thisPlayer == null) {
			return;
		}
		thisPlayer.player = player;
		thisPlayer.dead = false;
		thisPlayer.itemset = 0;
		
		plugin.getServer( ).getScheduler( ).scheduleAsyncDelayedTask( plugin, new Runnable( ) 
		{
			public void run( )
			{
					delayedCheckSpec( finalEvent );
			}
		}, 10 );
		
		plugin.getServer( ).getScheduler( ).scheduleAsyncDelayedTask( plugin, new Runnable( ) 
		{
			public void run( )
			{
					delayedCheck( finalEvent );
			}
		}, 300 );
		
	}
	
	
	public void delayedCheck( PlayerRespawnEvent event )
	{	
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		
		if (thisPlayer == null) {
			return;
		}
		thisPlayer.dead = false;
		player = plugin.getServer().getPlayer(ChatColor.stripColor(player.getDisplayName()));

		//Remove player from observer list
		specHandler.removeObserver(player);
		
		if (plugin.phase != 0) {
			plugin.giveGameItems(player);
			//Find the spawn block of the player
			Block block = respawnHandler.getRespawnBlockFromPlayer(thisPlayer);
			if (block != null) {
				Location spawnLocation = new Location(thisPlayer.world, block.getX(), block.getY()+1, block.getZ());
				thisPlayer.player.teleport(spawnLocation);
			}
		}
		
	}
	
	public void delayedCheckSpec( PlayerRespawnEvent event )
	{	
		Player player = event.getPlayer( );
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		
		if (thisPlayer == null) {
			return;
		}

		player = plugin.getServer().getPlayer(ChatColor.stripColor(player.getDisplayName()));
		//Add player to observer list
		specHandler.addObserver(player);
		
		if (plugin.phase != 0) {
			//Find the spawn block of the player
			Block block = respawnHandler.getRespawnBlockFromPlayer(thisPlayer);
			if (block != null) {
				Location specLocation = specHandler.getLocation();
				thisPlayer.player.teleport(specLocation);
			}
		}
		
	}
}
