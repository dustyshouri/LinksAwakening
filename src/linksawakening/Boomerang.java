package linksawakening;

import javax.sound.midi.MidiUnavailableException;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Boomerang {
  Image img;
  Link plyr;
  Sounds sound;
  public float x,y,origx,origy,spinframe,speed;
  public int rotation,soundtick;
  boolean flying;
  int[] coord = {0,0};
  
  Vector2f velocity;
  
  public Boomerang(Link p,int cx,int cy) {
    plyr = p;
    try {
      img = new Image("res/images/la_weap-boomerang.png",false,0x2);
      this.rotation = 180 - ((plyr.dir+1)%4)*90;
      sound = new Sounds();
    } catch (SlickException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    this.x         = this.origx = plyr.x;
    this.y         = this.origy = plyr.y;
    this.coord[0]  = cx;
    this.coord[1]  = cy;
    this.speed     = 8/1000f;
    this.flying    = true;
    this.soundtick = 0;
    
    if (plyr.delta.length() > 0) this.velocity = plyr.delta;
    else this.velocity = new Vector2f(Main.vectors[0][plyr.dir],Main.vectors[1][plyr.dir]);
    
    //sound.playSoundLoop("la_boomerang.wav");
  }
  
  public void tick() {
    if (this.soundtick%25 == 0) sound.playSound("la_boomerang.wav");
    this.soundtick++;
    this.spinframe += (15/1000f)*Animate.delta;
    
    if (this.flying == true) {
      float dx   = this.x - this.origx;
      float dy   = this.y - this.origy;
      float dist = (float)Math.sqrt(dx*dx + dy*dy);
      
      this.x += velocity.getX()*this.speed*Animate.delta;
      this.y += velocity.getY()*this.speed*Animate.delta;
      
      if (dist > 5) this.flying = false;
      else {
        if (Level.checkCollision(this.x+.5f,this.y+.5f)) {
          if (!checkTiles()) {
            //sound.playSound("la_swordtap.wav");
            Effects.addDing(this.x - .5f,this.y - .5f);
            this.flying = false;
          }
        }
        if (this.x < -.5f) this.x = -.5f;
        else if (this.x > 9.5f) this.x = 9.5f;
        if (this.y < -.5f) this.y = -.5f;
        else if (this.y > 7.5f) this.y = 7.5f;
        if (this.x <= -.5f || this.x >= 9.5f || this.y <= -.5f || this.y >= 7.5f) {
          Effects.addDing(this.x - .5f,this.y - .5f);
          this.flying = false;
        }
      }
    } else {
      checkTiles();
      float dx   = plyr.x - this.x;
      float dy   = plyr.y - this.y;
      float dist = (float)Math.sqrt(dx*dx + dy*dy);
      
      this.x += (dx/dist)*this.speed*Animate.delta;
      this.y += (dy/dist)*this.speed*Animate.delta;
    }
  }
  
  private boolean checkTiles() {
    int[][] bushes = {
      {253,220},
      {254,370},
      {191,190},
    };
    
    int hitbush = -1;
    for (int i=0;i<bushes.length;i++) {
      if (Level.tileID(this.x+.5f,this.y+.5f) == bushes[i][0]) {
        hitbush = i;
        break; 
      }
    }
    if (hitbush >= 0) {
      Level.setTileID(this.x+.5f,this.y+.5f,bushes[hitbush][1]);
      Effects.addPoof((int)(this.x+.5f) - .5f,(int)(this.y+.5f) - .5f);
      return true;
    }
    return false;
  }
  
  public void render() {
    img.setRotation(this.rotation - (int)this.spinframe*90);
    img.draw(this.x*16-(int)(plyr.roomtransition[0]*16),this.y*16-(int)(plyr.roomtransition[1]*16));
  }
}
