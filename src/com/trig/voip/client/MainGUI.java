package com.trig.voip.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends JFrame {
    private JPanel root;
    private JTextArea consoleOut;
    private JButton requestSoundButton;

    private VOIPClient client;

    private void createUIComponents() {

    }

    public MainGUI(VOIPClient client) {
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(root);
        setTitle("Java VOIP Client");
        setSize(400, 500);


        client.ready(); //Signal to the VOIPClient that the GUI is ready
        requestSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage("02 hello");
            }
        });
    }

    public void writeToConsole(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                consoleOut.append(msg + "\n");
            }
        });
    }
}
