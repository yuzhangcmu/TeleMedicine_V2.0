package edu.cmu.smartphone.telemedicine.adapt;

import java.sql.Date;

public interface CreateRecentChat {
    public void addRecentChat(String userID, Date updateTime);

}
