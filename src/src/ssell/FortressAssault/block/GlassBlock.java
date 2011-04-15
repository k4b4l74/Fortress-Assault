package ssell.FortressAssault.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockDamageEvent;

public class GlassBlock {

	private static GlassBlock instance = null;
	
	public GlassBlock() {	
	}
	
	public static GlassBlock getInstance() {
		if (instance == null) {
			instance = new GlassBlock();
		}
		return instance;
	}
	
	public void onBlockDamage(BlockDamageEvent event, Block block) {
		if (block.getType() == Material.GLASS) {
			// Cant destroy the GLASS
			event.setCancelled(true);
		}
	}
}
