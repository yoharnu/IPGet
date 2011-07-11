package org.yoharnu.IPGet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Hretsam
 */
public class FileHandler {

    /** Filename of the playerlog */
    private static final String filename = "players.yml";
    /** Loaded userfilelog */
    private Configuration userlog;

    public FileHandler(File datafolder) throws IOException {
        // Checks if directory already exists
        if (!datafolder.exists()) {
            // makes directory
            datafolder.mkdirs();
        }
        // Loads the location of the file
        File userLogFile = new File(datafolder.getAbsolutePath() + File.separator + filename);
        // check if exists
        if (!userLogFile.exists()) {
            // If not exists create new file
            userLogFile.createNewFile();
        }
        // Load file into configuration (yml reader)
        userlog = new Configuration(userLogFile);
        // Loads the file 
        userlog.load();
    }

    /**
     * Adds the ip to the user's IP list
     * @param username
     * @param ip
     * @param dateString 
     */
    public void addIp(String username, String ip, long dateString) {
        ip = formatIP(ip);

        // Makes sure it wont mistake the ip for nodes in the yml file
        ip = ip.replaceAll("\\.", "_");

        // Sets the ip (use the ip node for future adding of other nodes
        userlog.setProperty("users." + username + ".ip." + ip, dateString);
        // Saves file
        userlog.save();
    }

    /**
     * Returns a list with all of the ip logged to this user
     * @param username
     * @param forceCaseCheck (use this when your not sure the casing is right)
     * @return 
     */
    public ArrayList<IIP> getUserIplist(String username, boolean forceCaseCheck) {
        if (forceCaseCheck){
            username = checkCaseIndependant(username);
        }
        // Get all ip's
        List<String> keys = userlog.getKeys("users." + username + ".ip");
        // Check if there are ip's
        if (keys == null) {
            return null;
        }
        // Create the return list
        ArrayList<IIP> iplist = new ArrayList<IIP>(keys.size());
        // Go past every ip to get the data, and put it in the list
        for (String ip : keys) {
            iplist.add(new IIP(ip, Long.parseLong(userlog.getString("users." + username + ".ip." + ip, "0"))));

        }
        // return the list
        return iplist;
    }

    /**
     * Returns a list with all of the usernames used by the given ip
     * @param username
     * @return 
     */
    public ArrayList<String> getIpUserList(String ip) {
        // Extra IP cleaning check
        ip = formatIP(ip);
        // Makes sure it wont mistake the ip for nodes in the yml file
        ip = ip.replaceAll("\\.", "_");

        // Get all ip's
        List<String> keys = userlog.getKeys("users");
        // Check if there are any keys
        if (keys == null) {
            return null;
        }
        // Create the return list
        ArrayList<String> usernamelist = new ArrayList<String>();

        // Loops trough all logged users
        for (String username : keys) {
            // Gets the ips logged for the current user
            List<String> ipList = userlog.getKeys("users." + username + ".ip");
            // Check if there are ip's
            if (ipList == null) {
                continue;
            }
            // Check if the ip is in the list
            if (ipList.contains(ip)) {
                // Add to the list
                usernamelist.add(username);
            }
        }
        // Return the list
        return usernamelist;
    }

    /**
     * Formats the ip address to remove the slash and port
     * @param ip
     * @return ip itself
     */
    public static String formatIP(String ip) {
        // Remove the slash at the start
        if (ip.contains("/")) {
            // Remove the slash
            ip = ip.substring(1);
        }
        // Check if the ip still contains the port
        if (ip.contains(":")) {
            // Remove the port
            ip = ip.split(":")[0];
        }
        return ip;
    }

    /**
     * Returns the username that is equal to the given username
     * but where the casing is not the same
     * @param username
     * @return 
     */
    private String checkCaseIndependant(String username) {
        // Get all ip's
        List<String> keys = userlog.getKeys("users");
        // Check if there are ip's
        if (keys == null) {
            return null;
        }
        // loop trough all names
        for (String name : keys) {
            if (name.equalsIgnoreCase(username)) {
                return name;
            }
        }
        return username;
    }
}
