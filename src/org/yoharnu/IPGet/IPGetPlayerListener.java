// Package
package org.yoharnu.IPGet;

// Imports

import org.bukkit.event.player.PlayerListener;

public class IPGetPlayerListener extends PlayerListener {
	
	public static IPGet plugin;
	public String callingPlayerName;

	public IPGetPlayerListener(IPGet instance) {
		plugin = instance;
	}
}