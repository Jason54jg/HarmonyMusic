package com.jason.harmony.gui;

import com.jason.harmony.Bot;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class GUI extends JFrame {
    private final ConsolePanel console;
    private final Bot bot;

    public GUI(Bot bot) {
        super();
        this.bot = bot;
        console = new ConsolePanel();
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Harmony");
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("console", console);
        getContentPane().add(tabs);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { /* unused */ }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    bot.shutdown();
                } catch (Exception ex) {
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) { /* unused */ }

            @Override
            public void windowIconified(WindowEvent e) { /* unused */ }

            @Override
            public void windowDeiconified(WindowEvent e) { /* unused */ }

            @Override
            public void windowActivated(WindowEvent e) { /* unused */ }

            @Override
            public void windowDeactivated(WindowEvent e) { /* unused */ }
        });
    }
}
