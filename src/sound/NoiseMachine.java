package sound;

import java.util.HashSet;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import calculate.SmallPrimes;



public class NoiseMachine {

    @SuppressWarnings("restriction")//naughty naughty
    private final com.sun.media.sound.SoftSynthesizer synthesizer;
    static final boolean mute = false;
    private final Instrument[] instruments;
    
    private int currentInstrument = 0;

    public NoiseMachine() {
        synthesizer = new com.sun.media.sound.SoftSynthesizer();
        if (!mute) {
            toggleMute();
        }
        instruments = synthesizer.getAvailableInstruments();
    }
    
    public HashSet<Integer> makeSomeNoise(int useThis) {
        HashSet<Integer> iWillMakeTheseNoises = makeTheseNoises(useThis);
        //System.out.println(useThis + " " + makeTheseNoises(useThis));
        for (Integer i : iWillMakeTheseNoises) {
            synthesizer.getChannels()[0].noteOn(convert(i,false), 64);
        }
        return iWillMakeTheseNoises;
    }
    
    public void toggleMute() {
        if (synthesizer.isOpen()) {
            synthesizer.close();
        } else {
            try {
                synthesizer.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private final int[] heptatonic = new int[]{0,2,3,5,7,8,10};
    private final int[] pentatonic =  new int[]{1,4,6,9,11};
    private final int[] scale = pentatonic;
    
    private HashSet<Integer> makeTheseNoises(int i) {
        HashSet<Integer> noises = new HashSet<Integer>();
        for (int smallPrime : SmallPrimes.someSmallPrimes)  {
            if (smallPrime*smallPrime > i ) {
                break;
            } else if (i % smallPrime == 0) {
                noises.add(smallPrime);
            } 
        }
        return noises;
    }
    
    private int convert(int in, boolean lowIsLow) {
        in += 10;
        int r = in%scale.length;
        int m = in/scale.length;
        
        if (lowIsLow) {
            return 12*m + scale[r];
        } else {
            return 128-12*m-scale[r];
        }
    }
    
    private void cycleInstrument(int amount) {
        currentInstrument = (amount+currentInstrument)%instruments.length;
        if (currentInstrument < 0) {
            currentInstrument += instruments.length;//ffs
        }
        for (MidiChannel mc : synthesizer.getChannels()){
            mc.programChange(instruments[currentInstrument].getPatch().getProgram());
        }
        System.out.println(instruments[currentInstrument]);
        toggleMute();toggleMute();//shut them up once changed
        for (MidiChannel mc : synthesizer.getChannels()){//buggy shit
            mc.programChange(instruments[currentInstrument].getPatch().getProgram());
        }
    }
    
    public void changeInstrumentLeft() {
        cycleInstrument(1);    
    }

    public void changeInstrumentRight() {
        cycleInstrument(-1);
    }
    
    public String getCurrentInstrument() {
        return currentInstrument + " " + instruments[currentInstrument].getName();
    }

    //from stackoverflow
    private static 
    class Instruments {
        public static void main(String[] args) throws MidiUnavailableException {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            Instrument[] orchestra = synthesizer.getAvailableInstruments();

            StringBuilder sb = new StringBuilder();
            String eol = System.getProperty("line.separator");
            sb.append(
                "The orchestra has " + 
                orchestra.length + 
                " instruments." + 
                eol);
            for (Instrument instrument : orchestra) {
                sb.append(instrument.toString());
                sb.append(eol);
            }
            synthesizer.close();
            JOptionPane.showMessageDialog(null,
                new JScrollPane(new JTextArea(sb.toString(),20,30)));
        }
    }
}
