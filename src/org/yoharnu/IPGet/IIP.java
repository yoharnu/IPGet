package org.yoharnu.IPGet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Hretsam
 */
public class IIP {

    private String ip;
    private Long date;

    /**
     * Constructor of IIP
     * ip underscores will be set to dots, if any
     * @param ip
     * @param date 
     */
    public IIP(String ip, Long date) {
        if (ip.contains("_")) {
            ip = ip.replaceAll("_", "\\.");
        }
        this.ip = ip;

        this.date = date;
    }

    /**
     * Returns the data as a long
     * @return 
     */
    public Long getDateLong() {
        return date;
    }

    /**
     * Returns the data as a Date object
     * @return 
     * @see Date
     */
    public Date getDate() {
        return new Date(date);
    }

    /**
     * Returns the data as a string
     * @return 
     */
    public String getDateString() {
        String pattern = IPGet.getPlugin().getConfig().getDateSyntax();
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * Returns the IP
     * @return 
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the ip with the date
     * @return 
     */
    @Override
    public String toString() {
        return ip + " '" + getDateString() + "'";
    }
    
}
