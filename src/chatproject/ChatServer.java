/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatproject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

/**
 *
 * @author Ramya
 */
public class ChatServer extends JFrame implements ChatConstants {

    public ChatServer() {
        DefaultListModel model = new DefaultListModel();
        JList list = new JList(model);
        add(list);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("ChatServer");
        setVisible(true);
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            int sessionNo = 1;
            while (true) {
                model.addElement("Wait for players to join session " + sessionNo);
                Socket person1 = serverSocket.accept();
                model.addElement("Person 1 joined session " + sessionNo);
                model.addElement("Person 1's IP address" + person1.getInetAddress().getHostAddress());
                new DataOutputStream(person1.getOutputStream()).writeInt(PERSON1);
                Socket person2 = serverSocket.accept();
                model.addElement("Person 2 joined session " + sessionNo);
                model.addElement("Person 2's IP address" + person2.getInetAddress().getHostAddress());
                new DataOutputStream(person2.getOutputStream()).writeInt(PERSON2);
                model.addElement("Start a thread for session " + sessionNo++);
                HandleASession task = new HandleASession(person1, person2);
                new Thread(task).start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

class HandleASession implements Runnable {

    Socket person1;
    Socket person2;

    public HandleASession(Socket person1, Socket person2) {
        this.person1 = person1;
        this.person2 = person2;
    }

    public void run() {
        try {
            DataInputStream fromPerson1 = new DataInputStream(person1.getInputStream());
            DataOutputStream toPerson1 = new DataOutputStream(person1.getOutputStream());
            DataInputStream fromPerson2 = new DataInputStream(person2.getInputStream());
            DataOutputStream toPerson2 = new DataOutputStream(person2.getOutputStream());
            toPerson1.writeInt(1);
            while (true) {
                String msg = fromPerson1.readUTF();
                toPerson2.writeUTF(msg);
                msg = fromPerson2.readUTF();
                toPerson1.writeUTF(msg);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        ChatServer frame = new ChatServer();
    }
}
