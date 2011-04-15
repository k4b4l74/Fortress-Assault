package ssell.FortressAssault.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import ssell.FortressAssault.FAGizmoHandler;
import ssell.FortressAssault.FortressAssault.FAPlayer;

public class GizmoBlock {

	private static GizmoBlock instance = null;
	private static FAGizmoHandler gizmoHandler = null;
	
	public GizmoBlock(FAGizmoHandler aGizmoHandler) {
		gizmoHandler = aGizmoHandler;
	}
	
	public static GizmoBlock getInstance(FAGizmoHandler gizmoHandler) {
		if (instance == null) {
			instance = new GizmoBlock(gizmoHandler);
		}
		return instance;
	}
	
	public void onBlockDamage(BlockDamageEvent event, Block block, FAPlayer thisPlayer, int phase) {
		
		if (phase == 1) {
			if (block.getType() == Material.OBSIDIAN) {
				if (gizmoHandler.isGizmo(block)) {
					// Cant destroy the Gizmo
					event.setCancelled(true);
				}
			}
		} else if (phase == 2) {
			if (block.getType() == Material.OBSIDIAN) {
				if (gizmoHandler.isGizmo(block)) {
					// The sponge that was right-clicked is a Gizmo
					gizmoHandler.gizmoHit(thisPlayer, block);

					// Can't destroy the Gizmo
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event, FAPlayer thisPlayer) {
		if (event.getBlock().getType() == Material.OBSIDIAN) {
			if (!gizmoHandler.addGizmo(thisPlayer, event.getBlock())) {
				event.getBlock().setType(Material.AIR);
			}
		}
	}
}
