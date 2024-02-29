package client;

import adt.StackInterface;
import db.DBCommentConnection;
import entity.Chat;
import entity.Comment;
import entity.Donation;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Broadcast extends JFrame{
    private JPanel panelComment;
    private JTextArea taComment;
    private JTextField tfComment;
    private JButton btnSend;
    private JButton btnUnsend;
    private JLabel labelLive;
    private JScrollPane scroll;
    private JButton btnLeave;
    private JLabel JATLTV;
    private JButton btnDonateOrEnd;
    private JLabel labelScreen;
    private JLabel labelSupport;
    private JTextField tfUsername;
    private JSeparator sep1;
    private JSeparator sep2;
    private JSeparator sep3;
    private JSeparator sep4;
    private JSeparator sep5;
    private JSeparator sep6;
    private JSeparator sep7;
    private JSeparator sep8;
    private JSeparator sep9;
    //timer
    private Timer unsendTimer;
    private long startTime = -1;
    private long duration = 20000;
    private Timer labelLiveTimer;
    private int labelLiveCounter;
    private Timer labelSupportTimer;
    private int labelSupportCounter;
    //BGM
    private Clip bgmClip;
    //comment
    private StackInterface<Comment>commentStack;
    private DBCommentConnection conn;
    //username, isAdmin
    private String username;
    private boolean isAdmin;

    public Broadcast(String username, boolean isAdmin){
        this.username = username;
        this.isAdmin = isAdmin;

        setBroadcastGUI();

        //unsend button countdown
        unsendTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = commentStack.peek().getSendTime();
                }
                long now = System.currentTimeMillis();
                long clockTime = now - startTime;
                if (clockTime >= duration) {
                    clockTime = duration;
                    unsendTimer.stop();
                    setUnsendBtnEnabled(false);
                }
                SimpleDateFormat df = new SimpleDateFormat("s");
                btnUnsend.setText("Unsend(" + df.format(duration - clockTime) + ")");
            }
        });
        unsendTimer.setInitialDelay(0);

        //unsend btn settings
        setUnsendBtnTimer();

        //set label live
        labelLiveTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(labelLiveCounter % 2 == 0) {
                    labelLive.setBackground(Color.white);
                    labelLive.setForeground(Color.red);
                }
                else {
                    labelLive.setBackground(Color.red);
                    labelLive.setForeground(Color.white);
                }
                labelLiveCounter++;
            }
        });
        labelLiveTimer.start();

        //set label support
        labelSupportTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(labelSupportCounter % 2 == 0) {
                    labelSupport.setForeground(Color.cyan);
                }
                else {
                    labelSupport.setForeground(Color.red);
                }
                labelSupportCounter++;
            }
        });
        labelSupportTimer.start();

        //send button
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //text field should not be empty
                //censorship -> use list
                if(!tfComment.getText().equals("")) {
                    //create new comment element
                    setSoundEffect("click.wav");
                    commentStack.push(new Chat(username, System.currentTimeMillis(),isAdmin,tfComment.getText()));
                    taComment.setText(taComment.getText() + commentStack.peek()); //or use newComment
                    tfComment.setText("");
                    setUnsendBtnTimer();
                }
            }
        });

        //unsend button
        btnUnsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(isUnsendable()){
                    setSoundEffect("pop.wav");
                    //pop the comment
                    commentStack.pop();
                    //clear text area
                    taComment.setText("");
                    loadTextAreaComment();
                }
                setUnsendBtnTimer();
            }
        });

        //donate button
        btnDonateOrEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isAdmin){
                    commentStack.clear();
                    conn.clearCommentInDB(commentStack);
                    setSoundEffect("click.wav");
                    bgmClip.stop();
                    dispose();
                }else {
                    Random random = new Random();
                    int donation = random.nextInt(1, 1000);
                    setSoundEffect("coin.wav");
                    commentStack.push(new Donation(username, System.currentTimeMillis(), false, donation));
                    taComment.setText(taComment.getText() + commentStack.peek()); //or use newComment
                    setUnsendBtnTimer();
                }
            }
        });

        //leave button
        btnLeave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conn.saveCommentToDB(commentStack);
                setSoundEffect("click.wav");
                bgmClip.stop();
                dispose();
                //setVisible(false);
            }
        });
    }

    private void setBroadcastGUI(){
        //remove error from command lines
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);
        //frame settings
        setTitle("JATL GROCER LIVE BROADCAST");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelComment);
        setPreferredSize(new Dimension(1040,560));
        pack();
        setResizable(false);
        //border settings
        taComment.setBorder(BorderFactory.createLineBorder(Color.lightGray,1));
        tfComment.setBorder(BorderFactory.createLineBorder(Color.lightGray,1));
        labelScreen.setBorder(BorderFactory.createLineBorder(Color.lightGray,1));
        //font settings
        taComment.setFont(new Font("Serif", Font.PLAIN, 18));
        tfUsername.setFont(new Font("Serif", Font.PLAIN, 18));
        tfComment.setFont(new Font("Serif", Font.PLAIN, 18));
        btnSend.setFont(new Font("Arial", Font.BOLD, 16));
        btnUnsend.setFont(new Font("Arial", Font.BOLD, 16));
        btnDonateOrEnd.setFont(new Font("Arial", Font.BOLD, 16));
        btnLeave.setFont(new Font("Arial", Font.BOLD, 16));
        labelLive.setOpaque(true);
        //caret color settings
        tfComment.setCaretColor(Color.white);
        //data settings
        //initialize comment, load the comments into text area
        tfUsername.setText(username);
        conn = new DBCommentConnection();
        commentStack = conn.getCommentFromDB();
        loadTextAreaComment();

        //admin cannot donate
        if(isAdmin){
            //btnDonateOrEnd.setEnabled(false);
            btnDonateOrEnd.setText("End Broadcast");
            btnDonateOrEnd.setBackground(Color.BLUE);
            btnDonateOrEnd.setForeground(Color.WHITE);
            tfUsername.setForeground(Color.cyan);
            tfUsername.setText(username + "\uD83D\uDC51");

        }

        //show window
        setVisible(true);

        //set background music
        try {
            String directory = "src/main/resources/music/BGM.wav";
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(directory).getAbsoluteFile());
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioInputStream);
            //loop continuously
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void loadTextAreaComment(){
        if(!commentStack.isEmpty()){
            Iterator<Comment>cursor = commentStack.getIterator();
            while(cursor.hasNext()){
                Comment comment = cursor.next();
                taComment.setText(taComment.getText() + comment.toString());
            }
        }
    }

    private void setUnsendBtnEnabled(boolean enabled){
        if(enabled){
            btnUnsend.setEnabled(true);
            btnUnsend.setBackground(new Color(66,103,178));
        }else{
            btnUnsend.setEnabled(false);
            btnUnsend.setBackground(Color.lightGray);
        }
    }

    private boolean isUnsendable(){
        Comment comment = commentStack.peek();
        if(comment == null) return false;
        return (comment.compareTo(username)) >= 0 && (System.currentTimeMillis() - comment.getSendTime() <= 20000);
    }

    private void setUnsendBtnTimer(){
        try {
            if (isUnsendable()) {
                setUnsendBtnEnabled(true);
                startTime = -1;
                unsendTimer.start();
            } else {
                setUnsendBtnEnabled(false);
                if (unsendTimer != null) {
                    unsendTimer.stop();
                }
                btnUnsend.setText("Unsend(0)");
            }
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    private void setSoundEffect(String filename){
        try {
            String directory = "src/main/resources/sound_effect/" + filename;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(directory).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
