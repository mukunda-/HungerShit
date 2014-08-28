// oh SHIT LOOK AT THIS PLUGIN

package com.mukunda.hungershit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class HungerShit extends JavaPlugin implements Listener{
	@Override
    public void onEnable() {

		getServer().getPluginManager().registerEvents( this, this );
		getConfig().options().copyDefaults( true );
		getConfig().addDefault( "minimum-food", 1 );
		
		saveConfig();
		
    }
	
	@EventHandler( priority = EventPriority.MONITOR )
	public void onPlayerDie( PlayerDeathEvent ev ) {
		
		getConfig().set( 
			"players"+'.' + ev.getEntity().getName(), 
			ev.getEntity().getFoodLevel() );
	}
	
	@EventHandler( priority = EventPriority.MONITOR )
	public void onPlayerRespawn( PlayerRespawnEvent ev ) {
		final Player p = ev.getPlayer();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask( this, new Runnable() {
			public void run() {
				int food = getConfig().getInt( "players"+'.'+p.getName() );
				int minfood =getConfig().getInt( "minimum-food" ); 
				if( food < minfood ) food = minfood;
				p.setFoodLevel( food );
			}
		}, 5 );
	}
}
