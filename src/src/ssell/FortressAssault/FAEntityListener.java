package ssell.FortressAssault;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import ssell.FortressAssault.FortressAssault;
import ssell.FortressAssault.FAPvPWatcher;
import ssell.FortressAssault.FortressAssault.ClassType;
import ssell.FortressAssault.FortressAssault.FAPlayer;

//------------------------------------------------------------------------------------------

public class FAEntityListener 
	extends EntityListener
{
	private final FortressAssault plugin;
	private final FAPvPWatcher pvpWatcher;
	private final FASpecHandler specHandler;
		
	//--------------------------------------------------------------------------------------
	
	public FAEntityListener( FortressAssault instance, FASpecHandler aSpecHandler)
	{
		plugin = instance;
		pvpWatcher = instance.getWatcher( );
		specHandler = aSpecHandler;
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
		super.onEntityDamage(event);
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
					
					if (event instanceof EntityDamageByBlockEvent) {
						EntityDamageByBlockEvent damageEvent = (EntityDamageByBlockEvent) event;
						if (damageEvent.getDamager().getType() == Material.TNT) {
							event.setDamage(10);
						}
					}
					
					if(event instanceof EntityDamageByProjectileEvent && event.getDamage() >= 1){
		        		event.setDamage(event.getDamage() * 2);
		        	}
					
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
					
					//LLY
					//trigger on damage from projectile (as arrow)
					if( event instanceof EntityDamageByProjectileEvent )
					{
						EntityDamageByProjectileEvent damageEvent = ( EntityDamageByProjectileEvent  )event;
						
						//if damager is a player AND projectile is an arrow
						if(damageEvent.getDamager( ) instanceof Player && 
								damageEvent.getProjectile() instanceof Arrow)
						{
							Player attacker = ( Player )damageEvent.getDamager( );
					
							//FIRE
							if(attacker.getInventory().contains(Material.LAVA_BUCKET ) || 
									attacker.getInventory().contains(Material.LAVA))
							{
								
								player.setFireTicks(60);
								
								if(attacker.getInventory().contains(Material.LAVA_BUCKET ))
									attacker.getInventory().remove(Material.LAVA_BUCKET);
								else
									attacker.getInventory().remove(Material.LAVA);
							}
							//SNARE
							else if(attacker.getInventory().contains(Material.ICE))
							{
								attacker.getInventory().remove(Material.ICE);
								Vector speed = player.getVelocity();
								if(speed == plugin.getPlayersSpeed())
								{
									Vector newspeed = speed.multiply(0.5);
									player.setVelocity(newspeed);
								}
							}
							//GRAB
							else if(attacker.getInventory().contains(Material.YELLOW_FLOWER))
							{
								attacker.getInventory().remove(Material.YELLOW_FLOWER);
								Location attackerLoc = attacker.getLocation();
								//prevent fusion :)
								attackerLoc.setX(attackerLoc.getX()+2);
								player.teleport(attackerLoc);
								FortressAssault.lastSnareEvent = System.currentTimeMillis();
							}
							//explode
							else if(attacker.getInventory().contains(Material.TNT))
							{
								World world	=	(World) attacker.getWorld();
								Location loc = attacker.getLocation();
								EntityTNTPrimed tnt = new EntityTNTPrimed((net.minecraft.server.World) world, loc.getX(), loc.getY(), loc.getZ());
						    	//world.a(tnt);
								float realYield = (float) 2.0;
								world.a(tnt, loc.getX(), loc.getY(), loc.getZ(), realYield);
						   
							}
						}
						
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
