package testmidi;

import java.util.Vector;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

public class TestMIDI implements ControllerEventListener {

    private static volatile TestMIDI instance;

    public static TestMIDI getInstance() {
        if (instance == null) {
            synchronized (TestMIDI.class) {
                if (instance == null) {
                    instance = new TestMIDI();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) throws InvalidMidiDataException {
        Vector synthInfos = new Vector();
        MidiDevice device = null;
        MidiDevice casio = null;
        MidiDevice casioExt = null;
        MidiDevice oracle = null;

        Sequencer sequencer;
        Transmitter trans = null;
        Transmitter trans1 = null;

        String casioName = "CASIO USB-MIDI";
        String casioDescription = "External MIDI Port";
        String oracleRTS = "Real Time Sequencer";

        TestMIDI tm = getInstance();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
                System.out.println(i + ":\tname=" + infos[i].getName() + "\t\t\tvendor=" + infos[i].getVendor() + "\tversion=" + infos[i].getVersion() + "\tdescription=" + infos[i].getDescription());
                if (infos[i].getName().equals(casioName)) {
                    if (infos[i].getDescription().equals(casioDescription)) {
                        casioExt = MidiSystem.getMidiDevice(infos[i]);
                        System.out.println("casioExt");

                    } else {
                        casio = MidiSystem.getMidiDevice(infos[i]);
                        System.out.println("casio");
                    }
                }
                if (infos[i].getName().equals(oracleRTS)) {
                    oracle = MidiSystem.getMidiDevice(infos[i]);
                }
            } catch (MidiUnavailableException e) {
                e.printStackTrace(System.out);
            }
            if (device instanceof Synthesizer) {
                synthInfos.add(infos[i]);
            }
        }

        if (!(casioExt.isOpen())) {
            try {
                casioExt.open();
                System.out.println("casioExt.open()");
            } catch (MidiUnavailableException e) {
                e.printStackTrace(System.out);
            }
        }

        if (!(casio.isOpen())) {
            try {
                casio.open();
                System.out.println("casio.open()");
            } catch (MidiUnavailableException e) {
                e.printStackTrace(System.out);
            }
        }

        System.out.println("Send message");

        ShortMessage myMsg = new ShortMessage();
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
        long timeStamp = -1;
        try {
            Receiver rcvr = casioExt.getReceiver();
            rcvr.send(myMsg, timeStamp);
            trans = casio.getTransmitter();
            trans1 = casio.getTransmitter();

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        try {
            Receiver rcvr = MidiSystem.getReceiver();
            trans1.setReceiver(rcvr);
            
            DumpReceiver dr = new DumpReceiver(System.out);
            trans.setReceiver(dr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void controlChange(ShortMessage sm) {
        System.out.println(sm.toString());
    }

}
