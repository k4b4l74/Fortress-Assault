package ssell.FortressAssault.block;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import ssell.FortressAssault.FARespawnHandler;
import ssell.FortressAssault.FortressAssault.FAPlayer;

public class SpawnBlock {

	private static SpawnBlock instance = null;
	private static FARespawnHandler respawnHandler = null;
	
	public SpawnBlock(FARespawnHandler aRespawnHandler) {
		respawnHandler = aRespawnHandler;
	}
	
	public static SpawnBlock getInstance(FARespawnHandler respawnHandler) {
		if (instance == null) {
			instance = new SpawnBlock(respawnHandler);
		}
		return instance;
	}
	
	public void onBlockDamage(BlockDamageEvent event, Block block) {
		if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.LAPIS_ORE) {
			if (respawnHandler.isSpawnBlock(block)) {
				// Can't destroy a spawn point
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event, FAPlayer thisPlayer) {
		if (event.getBlock().getType() == Material.REDSTONE_ORE || event.getBlock().getType() == Material.LAPIS_ORE) {
			
			respawnHandler.addSpawnBlock(thisPlayer, event.getBlock());
		}
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.getEntity() instanceof TNTPrimed) {
			List<Block> blocks = event.blockList();
			for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext();) {
				Block blockToBeExplosed = (Block) iterator.next();
				
				for (Iterator<Block> iterator2 = respawnHandler.getRespawnListBlock().iterator(); iterator2.hasNext();) {
					Block blockSpawn = (Block) iterator2.next();
					
					if (blockToBeExplosed == blockSpawn) {
						iterator.remove();
						break;
					}
				}
			}
		}
	}
}
