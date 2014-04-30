package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import calculate.SmallPrimes;


public class NumberShower   {
    private static final long serialVersionUID = 1L;
    private static final int FONT_SIZE = 36;
    private static final int GIVE_UP = 41;
    
    public static final Font FONT = new Font("Verdana", Font.BOLD, FONT_SIZE);

    private final HashSet<Integer> unseenPrimes = new HashSet<Integer>();
    private final JLabel integerLabel;
    private final HashMap<Integer, JLabel> primeLables = new HashMap<Integer, JLabel>();
    private final Color backgroundColor;
    
    public NumberShower(final Shower shower, JFrame masterFrame) {
        
        backgroundColor = masterFrame.getBackground();
        
        integerLabel = new JLabel();
        integerLabel.setFont(new Font("Verdana", Font.BOLD, 2*FONT_SIZE));
        integerLabel.setBackground(backgroundColor);
        integerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = Integer.parseInt(JOptionPane.showInputDialog(""));
                if (i > 0 && i != shower.getN()){
                    shower.showNextImage(i);
                }
            }
        });
        masterFrame.add(integerLabel);
        
        for (int i : SmallPrimes.someSmallPrimes) {
            if (i > GIVE_UP) {
                break; //swing is fucking annoying. 
            }
            unseenPrimes.add(i);
            
            JLabel primeLabel = new JLabel(" " + i);
            primeLables.put(i, primeLabel);
            primeLabel.setFont(FONT);
            masterFrame.add(primeLabel);
        }
        
    }
    
    
    public void showNumber(final int n, HashSet<Integer> primeDivisors) {
        integerLabel.setText(""+n);
        for (int prime : SmallPrimes.someSmallPrimes) {
            if (prime > GIVE_UP) {
                break; //swing is fucking annoying. 
            }
            Color color;
            if (primeDivisors.contains(prime)) {
                color = SmallPrimes.majorScaleColors.get(prime);
                unseenPrimes.remove(prime);
            } else if (unseenPrimes.contains(prime)) {
                color = backgroundColor;
            } else {
                color = Color.gray;
            }
            primeLables.get(prime).setForeground(color);
        }
    }
}
