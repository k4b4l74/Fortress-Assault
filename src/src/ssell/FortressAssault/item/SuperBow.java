package ssell.FortressAssault.item;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SuperBow {

	private static SuperBow instance = null;
	private int dmgMultiplier = 2;

	public SuperBow() {
	}

	public static SuperBow getInstance() {
		if (instance == null) {
			instance = new SuperBow();
		}
		return instance;
	}

	public void onEntityDamage(EntityDamageEvent event, Player player) {
		if (event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent edpe = (EntityDamageByProjectileEvent) event;
			if (edpe.getProjectile() instanceof Arrow) {
				event.setDamage(event.getDamage() * dmgMultiplier);

				// if damager is a player AND projectile is an arrow
				if (edpe.getDamager() instanceof Player && edpe.getProjectile() instanceof Arrow) {
					Player attacker = (Player) edpe.getDamager();

					// FIRE
					if (attacker.getInventory().contains(Material.LAVA_BUCKET)
							|| attacker.getInventory().contains(Material.LAVA)) {

						player.setFireTicks(60);

						if (attacker.getInventory().contains(
								Material.LAVA_BUCKET))
							attacker.getInventory()
									.remove(Material.LAVA_BUCKET);
						else
							attacker.getInventory().remove(Material.LAVA);
					}
					// GRAB
					else if (attacker.getInventory().contains(
							Material.YELLOW_FLOWER)) {
						attacker.getInventory().remove(Material.YELLOW_FLOWER);
						Location attackerLoc = attacker.getLocation();
						// prevent fusion :)
						attackerLoc.setX(attackerLoc.getX() + 2);
						player.teleport(attackerLoc);
					}
					// explode
					else if (attacker.getInventory().contains(Material.TNT)) {
						World world = (World) attacker.getWorld();
						Location loc = attacker.getLocation();
						EntityTNTPrimed tnt = new EntityTNTPrimed(
								(net.minecraft.server.World) world, loc.getX(),
								loc.getY(), loc.getZ());
						// world.a(tnt);
						float realYield = (float) 2.0;
						world.a(tnt, loc.getX(), loc.getY(), loc.getZ(),
								realYield);

					}
				}

			}
		}
	}
}
