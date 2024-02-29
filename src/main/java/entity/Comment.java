package entity;

import java.text.SimpleDateFormat;

public class Comment implements Comparable<String>{
    private String username;
    private long sendTime;
    private boolean isAdmin;

    public Comment(){};

    public Comment(String username, long sendTime, boolean isAdmin){
        this.username = username;
        this.sendTime = sendTime;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public int compareTo(String user) {
        if(!this.username.equals(user)) return -1;
        return 0;
    }

    @Override
    public String toString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!isAdmin) return getUsername() + " (" + df.format(getSendTime()) + ")";
        return getUsername() + "\uD83D\uDC51 (" + df.format(getSendTime()) + ")";
    }
}

