package edu.cmu.smartphone.telemedicine.entities;

import java.sql.Date;

public class RecentChat {
    private String userID;
    private Date updateTime;
    
    private String time;
    
    
    public RecentChat(String userID, Date updateTime) {
        this.userID = userID;
        this.updateTime = updateTime;
    }
    
    public RecentChat(String userID) {
        super();
        this.userID = userID;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
    
    public Date getDate() {
        return updateTime;
    }
    
    public void setDate(Date updateTime) {
        this.updateTime = updateTime;
    }
}
