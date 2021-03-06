package ssell.FortressAssault;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import ssell.FortressAssault.FortressAssault;
import ssell.FortressAssault.FAPvPWatcher;
import ssell.FortressAssault.FortressAssault.ClassType;
import ssell.FortressAssault.FortressAssault.FAPlayer;
import ssell.FortressAssault.block.SpawnBlock;
import ssell.FortressAssault.item.EggGrenade;
import ssell.FortressAssault.item.PickAxe;
import ssell.FortressAssault.item.SnowBallSnare;
import ssell.FortressAssault.item.SuperBow;

//------------------------------------------------------------------------------------------

public class FAEntityListener 
	extends EntityListener
{
	private final FortressAssault plugin;
	private final FAPvPWatcher pvpWatcher;
	private final FASpecHandler specHandler;
	private final FARespawnHandler respawnHandler;
		
	//--------------------------------------------------------------------------------------
	
	public FAEntityListener( FortressAssault instance, FASpecHandler aSpecHandler, FARespawnHandler aRespawnHandler)
	{
		plugin = instance;
		pvpWatcher = instance.getWatcher( );
		specHandler = aSpecHandler;
		respawnHandler = aRespawnHandler;
	}

	/**
	 * When TNT Explode
	 */
	public void onEntityExplode(EntityExplodeEvent event) {
		if (plugin.phase == 2) {
			//PREVENT SPAWN BLOCK TO BE "TNTed"
			SpawnBlock.getInstance(respawnHandler).onEntityExplode(event);
		}
		
	}
	
	/**
	 * Whenever an entity is damaged, this method is called.<br><br>
	 * If godEnabled is true, it is checked if the entity is a player. If
	 * it is a player, and the player is on the playerList list, then
	 * the damage is canceled.
	 */
	@Override 
	public void onEntityDamage( EntityDamageEvent event )
	{
		Entity entity = event.getEntity( );
		if( entity instanceof Player )
		{
			Player player = ( Player )entity;
			FAPlayer thisPlayer = plugin.getFAPlayer(player);
			if (thisPlayer == null) {
				//not in the game so ignore
				return;
			} else {
				if( plugin.phase == 1 )
				{
					//phase 1 so no damage to players.
					event.setCancelled( true );
					return;
				}
				else if( plugin.phase == 2 ) {
					//GRENADE DMG MODIFIER
					EggGrenade.getInstance(plugin).onEntityDamage(event, player);
					
					//SNARE IF SNOWBALL
					SnowBallSnare.getInstance(plugin).onEntityDamage(event, player);
					
					//BOW DMG MODIFIER
					SuperBow.getInstance().onEntityDamage(event, player);
					
					//PICKAXE DMG MODIFIER
					PickAxe.getInstance().onEntityDamage(event);
					
					//NO DMG IF PLAYER IS AN OBSERVER
					if( event instanceof EntityDamageByEntityEvent ) {
						EntityDamageByEntityEvent damageEvent = ( EntityDamageByEntityEvent  )event;
						
						if( ( damageEvent.getDamager( ) instanceof Player ) &&
							  damageEvent.getEntity( ) instanceof Player )
						{
							Player attacker = ( Player )damageEvent.getDamager( );
							if (specHandler.isAnObserver(attacker)) {
								event.setDamage(0);
								return;
							}
						}
					}
					
					
		        	
					int damage = event.getDamage();
					int oldHealth = player.getHealth( );
					int newHealth = oldHealth - damage;
					
					if (thisPlayer.dead) {
						//already dead
						return;
					}							
										
					if( newHealth <= 0 )
					{						
						//health says they are dead but lets make sure.								
						event.setDamage(999);
						thisPlayer.dead = true;
						//to stop loot from dropping
						player.getInventory( ).clear( );
						player.getInventory( ).setHelmet(null);
						player.getInventory( ).setChestplate(null);
						player.getInventory( ).setLeggings(null);
						player.getInventory( ).setBoots(null);
						
						//drop a cookie
						player.getWorld().dropItem(player.getLocation(),new ItemStack( Material.COOKIE, 1 ));
						

					}
					
					if( event instanceof EntityDamageByEntityEvent )
					{
						EntityDamageByEntityEvent damageEvent = ( EntityDamageByEntityEvent  )event;
						
						if( ( damageEvent.getDamager( ) instanceof Player ) &&
							  damageEvent.getEntity( ) instanceof Player )
						{					
							//player damaged by player
							Player attacker = ( Player )damageEvent.getDamager( );
							if (thisPlayer.dead) {
								pvpWatcher.killEvent( attacker,  player );
							}
							
				
						} else if (damageEvent.getEntity( ) instanceof Player) {
							//player damaged by mob
						}
					} else {
						//player damaged by something else, maybe lava?
						//don't burn for so long.
						String cause = event.getCause().toString();
						if (cause.equals("FIRE") || cause.equals("FIRE_TICK")) {
							if (thisPlayer.classtype == ClassType.PYRO) {									
								player.setFireTicks(0);
								event.setCancelled( true );								
							} else {
								int burningTime = player.getFireTicks();
								if (burningTime>60) {
									player.setFireTicks(60);
								}
							}
						}
					}
				}
			}
		} else {
			//not a player so we don't care
			return;
		}
	}
	public void onEntityDeath( EntityDeathEvent event ) {
		Entity entity = event.getEntity();
		FAPlayer thisPlayer = plugin.getFAPlayer(entity);
		if (thisPlayer != null) {
			//player died by some other cause lets mark them as dead
			thisPlayer.dead = true;
		}
	}
}
