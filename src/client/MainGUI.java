package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.net.SocketAddress;

public class MainGUI extends JFrame {
    private JPanel root;
    private JTextArea consoleOut;
    private JButton requestSoundButton;
    private JButton connectButton;

    private void createUIComponents() {
        
    }

    public MainGUI() {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                add(root);
                setTitle("Java VOIP Client");
                setSize(400, 500);

                connectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {


                    }
                });
            }
        });
    }
}
