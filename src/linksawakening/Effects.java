package linksawakening;

import java.util.ArrayList;
import javax.sound.midi.MidiUnavailableException;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Effects {
  static Sounds sound;
  private static SpriteSheet effectsheet;
  static Animation leafani;
  static ArrayList<Animation> effects = new ArrayList<Animation>();
  static ArrayList<float[]> effectsdata = new ArrayList<float[]>();
  
  public Effects() throws SlickException, MidiUnavailableException {
    effectsheet = new SpriteSheet("res/images/la_effects.png",32,32);
    sound = new Sounds();
  }
  
  public static void checkClearance() {
    for (int i = effects.size() - 1;i > -1;i--) {
      if (effects.get(i).isStopped()) {
        effects.remove(i);
        effectsdata.remove(i);
      }
    }
  }
  
  public static void addLeaf(float x,float y,float alpha) {
    sound.playUniqueSound("la_bushcut.wav",.5f);
    
    float[] p = {x,y,alpha};
    Animation temp = new Animation(effectsheet,0,0,7,0,true,40,true);
    temp.stopAt(8);
    temp.setLooping(false);
    effects.add(temp);
    effects.get(effects.size()-1).start();
    effectsdata.add(p);
  }
  
  public static void addDing(float x,float y) {
    sound.playSound("la_swordtap.wav",.5f);
    
    float[] p = {x,y,0};
    Animation temp = new Animation(effectsheet,0,2,1,2,true,100,true);
    temp.stopAt(2);
    temp.setLooping(false);
    effects.add(temp);
    effects.get(effects.size()-1).start();
    effectsdata.add(p);
  }
  
  public static void addBoom(float x,float y) {
    sound.playSound("la_boom.wav",.5f);
    
    float[] p = {x,y,0};
    Animation temp = new Animation(effectsheet,2,1,6,1,true,100,true);
    temp.stopAt(7);
    temp.setLooping(false);
    effects.add(temp);
    effects.get(effects.size()-1).start();
    effectsdata.add(p);
  }
  
  public static void addPoof(float x,float y) {
    sound.playUniqueSound("la_ignite.wav",.5f);
    
    float[] p = {x,y,0};
    Animation temp = new Animation(effectsheet,0,1,1,1,true,100,true);
    temp.stopAt(2);
    temp.setLooping(false);
    effects.add(temp);
    effects.get(effects.size()-1).start();
    effectsdata.add(p);
  }
  
  public static void addMagic(float x,float y) {
    sound.playUniqueSound("la_magicpowder.wav",.5f);
    
    float[] p = {x,y,0};
    Animation temp = new Animation(effectsheet,2,2,7,2,true,60,true);
    temp.stopAt(8);
    temp.setLooping(false);
    effects.add(temp);
    effects.get(effects.size()-1).start();
    effectsdata.add(p);
  }
  
  public static void render() {
    for (int i=0;i<effects.size();i++) {
      if (effectsdata.get(i)[2] == 1) effects.get(i).draw(effectsdata.get(i)[0]*16,effectsdata.get(i)[1]*16,new Color(255,255,255,255/2));
      else effects.get(i).draw(effectsdata.get(i)[0]*16,effectsdata.get(i)[1]*16);
      //e.draw();
      //e.draw(0,0);
    }
  }
}
