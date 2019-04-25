package com.trig.voip.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

public class MainGUI extends JFrame {
    private JPanel root;
    private JTextArea consoleOut;
    private JButton requestSoundButton;
    private JButton testMicButton;

    private VOIPClient client;

    private void createUIComponents() {

    }

    public MainGUI(VOIPClient client) {
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(root);

//        JScrollPane scroll = new JScrollPane(consoleOut, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        add(scroll);
        setTitle("Java VOIP Client");
        setSize(400, 500);


        client.ready(); //Signal to the VOIPClient that the GUI is ready
        requestSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage("02 hello");
            }
        });

        testMicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testMic();
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

    private void testMic() {
        client.micTest();

    }
}
