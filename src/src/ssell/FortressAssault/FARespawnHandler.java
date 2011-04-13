package ssell.FortressAssault;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import ssell.FortressAssault.FortressAssault.FAPlayer;

public class FARespawnHandler {

	private final FortressAssault plugin;
	
	public class FARespawn {

		public Block block;
		public FAPlayer spawner;
		
		public FARespawn(FAPlayer p_spawner, Block p_Block) {
			block = p_Block;
			spawner = p_spawner;
		}
	}
	
	private List< FARespawn > respawnList = new ArrayList< FARespawn >( );
	
	public FARespawnHandler(FortressAssault instance) {
		plugin = instance;
	}
	
	public void addSpawnBlock( FAPlayer thisPlayer, Block block )
	{		
		FARespawn respawn = new FARespawn(thisPlayer, block );
		plugin.getServer( ).broadcastMessage( plugin.getTeamColor(thisPlayer.team) + thisPlayer.name+"  has placed his Respawn Point !" );
		respawnList.add(respawn);
	}
	
	public boolean isSpawnBlock(Block block )
	{
		for( int i = 0; i < respawnList.size( ); i++ )
		{
			if( respawnList.get( i ).block == block )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Block getRespawnBlockFromPlayer(FAPlayer thisPlayer) {
		Block block = null;
		for( int i = 0; i < respawnList.size( ); i++ )
		{
			if (respawnList.get(i).spawner.name.equalsIgnoreCase(thisPlayer.name)) {
				block =  respawnList.get(i).block;
				break;
			}
		}
		return block;
	}

	public void clearList() {
		for( int i = 0; i < respawnList.size( ); i++ )
		{
			respawnList.get( i ).block.setType( Material.AIR );
			respawnList.get( i ).spawner = null;
		}
		
		respawnList.clear( );
	}
}
