package org.yoharnu.IPGet;

import java.io.*;
import java.util.GregorianCalendar;
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

	@SuppressWarnings("deprecation")
	@Override
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String playerName = player.getName();
		logsFolder = new File(plugin.getDataFolder(), "logs");
		if (!logsFolder.exists()) {
			logsFolder.mkdirs();
		}
		configFile = new File(plugin.getDataFolder(), "logs/" + playerName + ".log");
			try {
				configFile.createNewFile();
			} catch (IOException e) {}
		FileOutputStream out;
		PrintStream p;// = new PrintStream(out);
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(configFile);
		} catch (FileNotFoundException e1) {}
		DataInputStream in = new DataInputStream(fstream);
		String tempFile = "";
		String newline = System.getProperty("line.separator");
		try {
			while (in.available() !=0)
			{
				tempFile += in.readLine() + newline;
			}
		} catch (IOException e1) {}
		try {
			out = new FileOutputStream(configFile);
			p = new PrintStream(out);
			p.println(tempFile + getDateString() + " - " + player.getAddress());
			p.close();
		} catch (FileNotFoundException e) {}
		return;
	}

	public static String getDateString() {
		GregorianCalendar now = new GregorianCalendar();
		String date = "";
		date += now.get(GregorianCalendar.MONTH) + 1;
		date += " ";
		date += now.get(GregorianCalendar.DAY_OF_MONTH);
		date += " ";
		date += now.get(GregorianCalendar.YEAR);
		date += " : ";
		date += now.get(GregorianCalendar.HOUR);
		date += ":";
		date += now.get(GregorianCalendar.MINUTE);
		date += ":";
		date += now.get(GregorianCalendar.SECOND);
		return date;
	}
}
