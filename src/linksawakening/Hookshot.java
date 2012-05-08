package linksawakening;

import java.util.Arrays;
import javax.sound.midi.MidiUnavailableException;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Hookshot {
  Image img;
  Link plyr;
  Sounds sound;
  public float x,y,origx,origy,speed;
  public int mode,dir,soundtick,flydir;
  float[][] chains = new float[3][2];
  int[] hooktiles = {162,208,209,211,325,347,368};
  
  Vector2f velocity;
  
  public Hookshot(Link p) {
    plyr = p;
    try {
      img = new Image("res/images/la_weap-hookshot.png",false,0x2);
      sound = new Sounds();
    } catch (SlickException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    this.x         = this.origx = plyr.x;
    this.y         = this.origy = plyr.y;
    this.dir       = plyr.dir;
    this.speed     = 12/1000f;
    this.mode      = 0;
    this.soundtick = 0;
    this.flydir    = -1;
    this.chains[0][0] = this.chains[1][0] = this.chains[2][0] = this.x;
    this.chains[0][1] = this.chains[1][1] = this.chains[2][1] = this.x;
    
    this.velocity = new Vector2f(Main.vectors[0][this.dir],Main.vectors[1][this.dir]);
    
    sound.playSoundLoop("la_hookshot.wav");
  }
  
  public void tick() {
    plyr.pausetimer = 20/1000f;
    plyr.anipause = 250/1000f;
    //if (this.soundtick%5 == 0) sound.playSound("la_hookshot.wav");
    this.soundtick++;
    
    if (this.mode == 0) {
      this.x += velocity.getX()*this.speed*Animate.delta;
      this.y += velocity.getY()*this.speed*Animate.delta;
      
      float dx   = this.x - plyr.x;
      float dy   = this.y - plyr.y;
      float dist = (float)Math.sqrt(dx*dx + dy*dy);
      
      if (dist > 8) {
        this.mode = 1;
        return;
      }
      if (Level.checkCollision(this.x+.5f,this.y+.5f) || Level.tileType(this.x+.5f,this.y+.5f) == 7) {
        int checktile = Level.tileID(this.x+.5f,this.y+.5f);

        if (Arrays.binarySearch(hooktiles,checktile) >= 0) {
          Effects.addDing(this.x - .5f,this.y - .5f);
          this.mode = 2;
        } else {

          if (this.flydir == -1) {
            if (checktile == 437 && this.dir == 3) this.flydir = 3;
            else if (checktile == 413 && this.dir == 1) this.flydir = 1;
          }
          if (this.flydir == -1 && checktile != 363) {
            Effects.addDing(this.x - .5f,this.y - .5f);
            this.mode = 1;
          }
        }
      }
      if (this.x < -.5f) this.x = -.5f;
      else if (this.x > 9.5f) this.x = 9.5f;
      if (this.y < -.5f) this.y = -.5f;
      else if (this.y > 7.5f) this.y = 7.5f;
      if (this.x <= -.5f || this.x >= 9.5f || this.y <= -.5f || this.y >= 7.5f) {
        Effects.addDing(this.x - .5f,this.y - .5f);
        this.mode = 1;
      }
    } else if (this.mode == 1) {
      this.x -= velocity.getX()*this.speed*Animate.delta;
      this.y -= velocity.getY()*this.speed*Animate.delta;
    } else if (this.mode == 2) {
      plyr.x += velocity.getX()*this.speed*Animate.delta;
      plyr.y += velocity.getY()*this.speed*Animate.delta;
    }
    
    for (int i=0;i<3;i++) {
      float[] increments = {.25f,.5f,.75f};
      float dx   = this.x - plyr.x;
      float dy   = this.y - plyr.y;
      this.chains[i][0] = this.x - dx*increments[i];
      this.chains[i][1] = this.y - dy*increments[i];
    }
  }
  
  public void stopSound() {
    sound.stopSound(); 
  }
  
  public void render() {
    for (int i=0;i<3;i++) {
      float drawx = this.chains[i][0]*16;
      float drawy = this.chains[i][1]*16;
      img.draw(drawx,drawy,drawx+16,drawy+16,16,0,16+16,16);
    }
    img.draw(this.x*16,this.y*16,this.x*16+16,this.y*16+16,0,0,16,16);
  }
}
