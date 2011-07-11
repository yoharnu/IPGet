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

    public IIP(String ip, Long date) {
        if (ip.contains("_")) {
            ip = ip.replaceAll("_", "\\.");
        }
        this.ip = ip;

        this.date = date;
    }

    public Long getDateLong() {
        return date;
    }

    public Date getDate() {
        return new Date(date);
    }
    
    public String getDateString(){
        String pattern = "dd-MMM-yyyy hh:ss";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return ip + " '" + getDateString() + "'";
    }
}
