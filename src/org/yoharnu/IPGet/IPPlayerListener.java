package org.yoharnu.IPGet;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class IPPlayerListener extends PlayerListener {
	public static IPGet plugin;
	public String callingPlayerName;
	private File configFile;
	private File logsFolder;
	
	public IPPlayerListener(IPGet instance) {
		plugin = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String playerName = player.getName();
		logsFolder = new File(plugin.getDataFolder(), "logs");
		if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
		configFile = new File(plugin.getDataFolder(), "logs/" + playerName);
		
		return;
	}
}
