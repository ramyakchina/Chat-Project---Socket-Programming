/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/**
 *
 * @author Ramya
 */
public class ChatClient extends JFrame implements Runnable, ChatConstants {

    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private String host = "localhost";
    JLabel jlblTitle = new JLabel();
    JLabel jlblStatus = new JLabel();
    JPanel panel = new JPanel();
    JTextArea up = new JTextArea(3, 10);
    JTextArea down = new JTextArea(5, 10);
    Color color;
    public ChatClient() {
      
        JScrollPane scrollP1 = new JScrollPane(up);
        up.setLineWrap(true);
        up.setBackground(Color.PINK);
        panel.add(scrollP1);
        JScrollPane scrollP2 = new JScrollPane(down);
        down.setLineWrap(true);
        down.setEditable(false);
        down.setBackground(Color.ORANGE);
        panel.add(scrollP2);
        up.addKeyListener(new KeyListener() {
            public void keyPress(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
               
                    sendMessage(up.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        jlblTitle.setHorizontalAlignment(JLabel.CENTER);
        jlblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        jlblTitle.setBorder(new LineBorder(Color.BLACK, 1));
        jlblStatus.setBorder(new LineBorder(Color.BLACK, 1));
        add(jlblTitle, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(jlblStatus, BorderLayout.SOUTH);
        try {
            Socket socket = new Socket(host, 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            System.err.println(e);
        }
        setSize(320, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void run() {
        try {
            int person = fromServer.readInt();
            if (person == PERSON1) {
                jlblTitle.setText("Person 1");
                jlblStatus.setText("Waiting for Person2 to Join!");
                fromServer.readInt();
                jlblStatus.setText("Person 2 has joined, I can start first!");
            } else if (person == PERSON2) {
                jlblTitle.setText("Person 2");
                jlblStatus.setText("Waiting for Person 1 to send message!");
            }
            while (true) {
                if (person == PERSON1) {
                    jlblStatus.setText("Chatting!");
                    waitForPersonMessage();
                } else if (person == PERSON2) {
                    jlblStatus.setText("Chatting!");
                    receiveFromServer();
                    waitForPersonMessage();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void waitForPersonMessage() throws InterruptedException {    
        while (true) {
            Thread.sleep(100);
        }
    }

    public void sendMessage(String msg) {
        try {
            up.setText("");
            toServer.writeUTF(msg);
            receiveFromServer();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void receiveFromServer() {
        try {
            String msg = fromServer.readUTF();
            down.append(msg + "\n");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
    }
}
