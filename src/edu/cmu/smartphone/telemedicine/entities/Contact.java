package edu.cmu.smartphone.telemedicine.entities;

public class Contact {
    private String type;
    private String email;
    private String phone;
    
    private String nation; // don't store nation id local.
    private String province;
    private String city;
    
    private String name;
    private String userID;
    
    private String intro;
    private String headPortrait; // store the picture of the user.
    
    private int age;
    private String passWord;
    
    /** 
     * The sorted character.
     */  
    private String sortKey;
    
    private static String currentUserID;
    private static String currentUserFullName;
    
    // this constract a contact which only has name and loginID to show it 
    // on the screen. detail profile can be fatched from the database.
    public Contact (String name, String loginID) {
        this.name = name;
        this.userID = loginID;
        
        // set the sort key
        this.sortKey = name;
    }
    
    public Contact() {
        // TODO Auto-generated constructor stub
    }
    
    public static String getCurrentUserID() {
        return currentUserID;
    }
    
    public static void setCurrentUserID(String currentUserID1) {
        currentUserID = currentUserID1;
    }
    
    public static String getCurrentUserFullName() {
        //return currentUserFullName;
        return currentUserID;
    }
    
    public static void setCurrentUserFullName(String currentUserFullName1) {
        currentUserFullName = currentUserFullName1;
    }

    public String getUserID() {
        return userID;
    }
    
    public String getType() {
        return type;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getNation() {
        return nation;
    }
    
    public String getProvince() {
        return province;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getName() {
        return name;
    }
    
    public String getIntro() {
        return intro;
    }
    
    public void setIntro(String intro) {
        this.intro = intro;
    }
    
    public String getHeadPortrait() {
        return headPortrait;
    }
    
    public int getAge() {
        return age;
    }
    
    public String getPassword() {
        return passWord;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSortKey(String sortKey2) {
        sortKey = sortKey2;
    }
    
    public void deleteFriend(Contact fiend) {
        
    }
}
