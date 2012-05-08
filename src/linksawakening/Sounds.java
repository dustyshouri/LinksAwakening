package linksawakening;

import java.io.IOException;
import javax.sound.midi.*;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Sounds {
  public Receiver receiver;
  public Synthesizer synthesizer;
  int oldvolume,volume;
  Sequencer sequencer;
  Sequence sequence;
  String currentSong;
  public Sound globalsfx;
  
  public Sounds() throws MidiUnavailableException {
    sequencer = MidiSystem.getSequencer();
  }
  
  public void playSong(String s) {
    try {
      //InputStream is = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("res/music/" + s));
      sequence = MidiSystem.getSequence(getClass().getClassLoader().getResourceAsStream("res/music/" + s));
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (currentSong != null && currentSong.equals(s)) return;
    if (sequencer.getTickPosition() > 0) sequencer.stop();
    
    try {
      receiver = MidiSystem.getReceiver();
      synthesizer = MidiSystem.getSynthesizer();
      sequencer.open();
      sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
      sequencer.setSequence(sequence);
    } catch (MidiUnavailableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidMidiDataException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    currentSong = s;
    sequencer.start();
    
    //vol = MasterVolume.valueOf("LA_OVERWORLD");
    changeVolume(1);
  }
  
  public void resumeSong() {
    sequencer.start();
  }
  
  public void pauseSong() {
    sequencer.stop();
  }
  
  public void changeVolume(float v) {
    //if ((int)(v*vol.getVolume()*127) == oldvolume) return;
    volume = (int)(v*.5f*127);
    applyVolume();
  }
  
  public void applyVolume() {
    ShortMessage volumeMessage= new ShortMessage();
    for (int c=0;c<16;c++) {
      try {
        volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE,c,7,volume);
        receiver.send(volumeMessage, -1);
      } catch (InvalidMidiDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public void playSound(String s) {
    try {
      Sound sfx = new Sound("res/sounds/" + s);
      sfx.play();
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void playUniqueSound(String s,float v) {
    try {
      stopSound();
      globalsfx = new Sound("res/sounds/" + s);
      globalsfx.play(1,v);
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void playSoundLoop(String s) {
    try {
      globalsfx = new Sound("res/sounds/" + s);
      globalsfx.loop();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }
  
  public void stopSound() {
    if (globalsfx != null) globalsfx.stop();
  }
  
  public void playSound(String s,float v) {
    try {
      Sound sfx = new Sound("res/sounds/" + s);
      sfx.play(1,v);
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
