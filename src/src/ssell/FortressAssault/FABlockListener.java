package ssell.FortressAssault;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import ssell.FortressAssault.FAGizmoHandler;
import ssell.FortressAssault.FortressAssault.FAPlayer;

//------------------------------------------------------------------------------------------

public class FABlockListener extends BlockListener {
	public final class FASpecialBlock {
		public Block block;
		public int power;

		public FASpecialBlock(Block p_block) {
			block = p_block;
		}
	}

	public static FortressAssault plugin;
	public final FAGizmoHandler gizmoHandler;
	public final FARespawnHandler respawnHandler;

	public FABlockListener(FortressAssault instance, FAGizmoHandler gizmo,
			FARespawnHandler respawn) {
		plugin = instance;
		gizmoHandler = gizmo;
		respawnHandler = respawn;
	}
	

	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {
			return;
		}
		
		if (block.getType() == Material.REDSTONE_ORE
				|| block.getType() == Material.LAPIS_ORE) {
			if (plugin.phase == 2 || plugin.phase == 1) {
				if (respawnHandler.isSpawnBlock(block)) {
					// Can't destroy a spawn point
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {
			return;
		}
		
		if (block.getType() == Material.GLASS) {
			if (plugin.phase == 2) {
				// Cant destroy the GLASS
				event.setCancelled(true);
			}
		}

		if (block.getType() == Material.OBSIDIAN) {
			if (plugin.phase == 2) {
				if (gizmoHandler.isGizmo(block)) {
					// The sponge that was right-clicked is a Gizmo
					gizmoHandler.gizmoHit(thisPlayer, block);

					// Can't destroy the Gizmo
					event.setCancelled(true);
				}
			} else if (plugin.phase == 1) {
				if (gizmoHandler.isGizmo(block)) {
					// Cant destroy the Gizmo
					event.setCancelled(true);
				}
			}
		} else if (block.getType() == Material.REDSTONE_ORE
				|| block.getType() == Material.LAPIS_ORE) {
			if (plugin.phase == 2 || plugin.phase == 1) {
				if (respawnHandler.isSpawnBlock(block)) {
					// Can't destroy a spawn point
					event.setCancelled(true);
				}
			}
		} else if (block.getType() != Material.BRICK) {
			if (plugin.phase == 1) {
				// Can't destroy another than stone
				event.setCancelled(true);
			}
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {
			return;
		}

		if (event.getBlock().getType() != Material.TNT
				&& event.getBlock().getType() != Material.BRICK && event.getBlock().getType() != Material.LADDER) {
			if (plugin.phase == 2) {
				// Can not place block during this phase except TNT and BRICK
				event.setCancelled(true);
			}
		}

		if (event.getBlock().getType() == Material.OBSIDIAN) {
			if (plugin.phase == 1) {
				if (!gizmoHandler.addGizmo(thisPlayer, event.getBlock())) {
					event.getBlock().setType(Material.AIR);
				}
			}
		}
		if (event.getBlock().getType() == Material.REDSTONE_ORE || event.getBlock().getType() == Material.LAPIS_ORE) {
			if (plugin.phase == 1) {
				respawnHandler.addSpawnBlock(thisPlayer, event.getBlock());
			}
		}
	}
}
