package client;

import javax.swing.*;

public class Driver {
    public static void main(String[] args){
        //constructor syntax: public Broadcast(String username, boolean isAdmin)
        //example customer
        JFrame frame = new Broadcast("Aiskrim", false);
        //example admin
        //JFrame frame = new Broadcast("Joshua", true);
    }
}
