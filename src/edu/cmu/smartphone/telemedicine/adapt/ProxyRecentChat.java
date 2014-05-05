package edu.cmu.smartphone.telemedicine.adapt;

import java.sql.Date;
import java.util.LinkedHashMap;

import edu.cmu.smartphone.telemedicine.entities.RecentChat;

public class ProxyRecentChat {
    private static LinkedHashMap<String, RecentChat> recentChatHash = 
            new LinkedHashMap<String, RecentChat>();
    
    public void addRecentChat(String userID, Date updateTime) {
        RecentChat recentChat = new RecentChat(userID, updateTime);
        recentChatHash.put(userID, recentChat);
    }
    
    // delete a recent chat.
    public void deleteRecentChat(String userID) {
        recentChatHash.remove(userID);
    }
    
    // change the recent chat update time.
    public void updateRecentChat(String userID, Date date) {
        RecentChat recentChat = recentChatHash.get(userID);
        recentChat.setDate(date);
        //deleteRecentChat(userID);
        recentChatHash.put(userID, recentChat);
    }
}
