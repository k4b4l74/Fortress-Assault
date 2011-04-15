package ssell.FortressAssault;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import ssell.FortressAssault.FAGizmoHandler;
import ssell.FortressAssault.FortressAssault.FAPlayer;
import ssell.FortressAssault.block.GizmoBlock;
import ssell.FortressAssault.block.GlassBlock;
import ssell.FortressAssault.block.SpawnBlock;

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
		
	public void onBlockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {
			return;
		}
		
		if (plugin.phase == 1) {
			GizmoBlock.getInstance(gizmoHandler).onBlockDamage(event, block, thisPlayer, plugin.phase);
			SpawnBlock.getInstance(respawnHandler).onBlockDamage(event, block);
			GlassBlock.getInstance().onBlockDamage(event, block);
			
			//In fortify we can not destroy something except brick
			if (block.getType() != Material.BRICK) {
				event.setCancelled(true);
			}
		} else if (plugin.phase == 2) {
			GizmoBlock.getInstance(gizmoHandler).onBlockDamage(event, block, thisPlayer, plugin.phase);
			SpawnBlock.getInstance(respawnHandler).onBlockDamage(event, block);
			GlassBlock.getInstance().onBlockDamage(event, block);
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		FAPlayer thisPlayer = plugin.getFAPlayer(player);
		if (thisPlayer == null) {
			return;
		}

		if (plugin.phase == 1) {
			GizmoBlock.getInstance(gizmoHandler).onBlockPlace(event, thisPlayer);
			SpawnBlock.getInstance(respawnHandler).onBlockPlace(event, thisPlayer);
		} else if (plugin.phase == 2) {
			// FIGHT: We can not place a block except TNT, BRICK & LADDER.
			if (event.getBlock().getType() != Material.TNT
					&& event.getBlock().getType() != Material.BRICK 
					&& event.getBlock().getType() != Material.LADDER) {
				event.setCancelled(true);
			}
		}
		
		
	}
}
