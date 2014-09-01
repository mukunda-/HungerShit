// oh SHIT LOOK AT THIS PLUGIN

package com.mukunda.hungershit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit; 
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

//-------------------------------------------------------------------------------------------------
public final class HungerShit extends JavaPlugin implements Listener {

	//-------------------------------------------------------------------------------------------------
	private final HashMap<UUID,Integer> savedFoodLevel = new HashMap<>();
	
	//-------------------------------------------------------------------------------------------------
	@Override 
    public void onEnable() {

		getConfig().options().copyDefaults( true );
		getConfig().addDefault( "minimum-food", 1 );
		
		saveConfig();

		getServer().getPluginManager().registerEvents( this, this );
    }
	
	//-------------------------------------------------------------------------------------------------
	@EventHandler( priority = EventPriority.MONITOR )
	public void onPlayerDie( PlayerDeathEvent event ) {
		
		savedFoodLevel.put( 
				event.getEntity().getUniqueId(), 
				event.getEntity().getFoodLevel() );
	}
	
	//-------------------------------------------------------------------------------------------------
	@EventHandler( priority = EventPriority.MONITOR )
	public void onPlayerRespawn( PlayerRespawnEvent event ) {
		
		final UUID id = event.getPlayer().getUniqueId();
		final Integer food = savedFoodLevel.get(id);
		if( food == null ) {
			// this may happen if the server shuts down after a player
			// dies, and then he respawns in the next server session...
			
			// then again when did a little null pointer exception hurt anyone
			return;
		}
		new RestoreFoodTask( id, food ).runTaskLater( this, 5L );
	
	}
	
	//-------------------------------------------------------------------------------------------------
	private class RestoreFoodTask extends BukkitRunnable {
		private final UUID playerId;
		private final int food;
		
		public RestoreFoodTask( UUID player, int food ) {
			this.playerId = player;
			this.food = food;
		}
		
		public void run() {
			Player player = Bukkit.getPlayer( playerId );
			if( player == null ) {
				// this *might* happen if the player logs out right
				//  when he respawns to avoid his food getting taken away... 
				return; // they escaped us...
			}  
			player.setFoodLevel( food );
		}
	}
}
