package com.example.sw221103;

public class User1 {
    private String id;
    private String uid;
    private String user_key;
    private String user_name;
    //private String user_content;
    private String date;
    private String order_date;
    private String password;

    User1() {
    }

    public User1(String uid, String user_key, String user_name,
                 String date, String order_date, String password) {
        this.user_key = user_key;
        this.user_name = user_name;
        //this.user_content = user_content;
        this.date = date;
        this.order_date = order_date;
        this.uid = uid;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    //public String getUser_content(){return user_content;}
    //public void setUser_content(String user_content){this.user_content = user_content;}
    public String getUser_date() {
        return date;
    }

    public void setUser_date(String date) {
        this.date = date;
    }

    public String getUser_order_date() {
        return order_date;
    }

    public void setUser_order_date(String order_date) {
        this.order_date = order_date;
    }

    public String getUser_password() {
        return password;
    }

    public void setUser_password(String password) {
        this.password = password;
    }

}