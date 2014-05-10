package ui;


import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sound.NoiseMachine;
import calculate.Creator;

//XXX: should/can the Thread bit be separated from the UI bit?
//XXX: what needs to be synchronized?
public class Shower extends Thread {

    private final NoiseMachine noiseMachine;
    private final Creator creator;
    private final JLabel theBigPicture;
    private final JLabel instrumentLabel;
    private final NumberShower numberShower;
    private final JFrame masterFrame;
    
    private int FRAME_MILLIS = 200;
    private boolean play = false;
    private boolean showImage = true;
    private int i = 1;
        
    public Shower() throws IOException, MidiUnavailableException {
        noiseMachine = new NoiseMachine();
        noiseMachine.toggleMute();
        creator = new Creator();
        
        masterFrame = getMeAFrame(1024,830);
        masterFrame.setLayout(new FlowLayout(3,0,0));
        //BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS); 
        //frame.setLayout(boxLayout);
        addKeyListeners(masterFrame);
        
        //JFrame instrumentFrame = getMeAFrame(100, 100);
        instrumentLabel = new JLabel();
        //instrumentFrame.add(instrumentLabel);
        instrumentLabel.setText(noiseMachine.getCurrentInstrument());
        instrumentLabel.setFont(new Font("Britannic Bold", Font.BOLD, 16));
        masterFrame.add(instrumentLabel);
        
        numberShower = new NumberShower(this, masterFrame);
        theBigPicture = new JLabel();
        
        masterFrame.add(theBigPicture);
        
    }
    
    private static JFrame getMeAFrame(int height, int width) {
        JFrame frame = new JFrame();
        frame.setSize(height,width);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }
    
    private void addKeyListeners(JFrame frame) {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_ENTER) {
                    togglePlay();
                }
            }
        });
        
        //TODO: refactor this down a little:
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'd') {
                    showNextImage(++i);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'a') {
                    showNextImage(--i);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'm') {
                    noiseMachine.toggleMute();
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'c') {
                    ImageIcon icon = new ImageIcon(creator.cycleColor());
                    theBigPicture.setIcon(icon);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'p') {
                    showImage = !showImage;
                    theBigPicture.setVisible(showImage);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'l') {
                    noiseMachine.changeInstrumentLeft();
                    instrumentLabel.setText(""+noiseMachine.getCurrentInstrument());
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'j') {
                    noiseMachine.changeInstrumentRight();
                    instrumentLabel.setText(""+noiseMachine.getCurrentInstrument());
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'i') {
                    FRAME_MILLIS/=1.1;  
                    p.p(FRAME_MILLIS);
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'k') {
                    if (FRAME_MILLIS == 0) {
                        FRAME_MILLIS = 1;
                        p.p(FRAME_MILLIS);
                    } else if (FRAME_MILLIS < (Integer.MAX_VALUE/1.1)) {
                        FRAME_MILLIS*=1.1;
                        p.p(FRAME_MILLIS);
                    }
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'u') {
                    theBigPicture.setIcon(new ImageIcon(creator.toggleUnsuprising()));
                }
            }
            
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 'o') {
                    theBigPicture.setIcon(new ImageIcon(creator.toggleSuprising()));
                }
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == 't') {
                    theBigPicture.setIcon(new ImageIcon(creator.cycleThickness()));
                }
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    try {
                        creator.writeToFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    private void togglePlay() {
        play = !play;
    }
    
    //synchronized so button mashing doesn't cause CMEs
    public synchronized void showNextImage(int N) {
        i = N;//this line is optional in most cases, which is bad
        HashSet<Integer> primeDivisors = noiseMachine.makeSomeNoise(N);
        numberShower.showNumber(N, primeDivisors);
        if (showImage) {
            ImageIcon icon = new ImageIcon(creator.setN(N));
            theBigPicture.setIcon(icon);        
        }
    }
    
    
    @Override
    public void run() {
        super.run();
        showNextImage(++i);
        while (true) {
            if  (!play) {
                justFuckOffAndSleep(100);
            } else {
                long then = System.currentTimeMillis();

                try {
                    showNextImage(++i);
                } catch (Exception e) {
                    e.printStackTrace();
                    play = false;
                }
                justFuckOffAndSleep(Math.max(FRAME_MILLIS + (int)(System.currentTimeMillis() - then), 0));
                System.out.println(System.currentTimeMillis() - then + " " + i);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, MidiUnavailableException {
        Shower shower = new Shower();
        shower.start();
        //shower.togglePlay();
    }
    
    private void justFuckOffAndSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getN() {
        return i;
    }
    
    public static final class p {
        public static void p(Object out) {
            System.out.println(out);
        }
    }
}