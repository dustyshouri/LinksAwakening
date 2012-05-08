package linksawakening;

import javax.sound.midi.MidiUnavailableException;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Bomb {
  Image img,environmentimg;
  Link plyr;
  Sounds sound;
  float x,y,z,offsety,fuse,flicker;
  
  public Bomb(Link p) {
    plyr = p;
    try {
      img = new Image("res/images/la_weap-bomb.png",false,0x2);
      environmentimg = new Image("res/images/la_sprite-environment.png",false,0x2);
      sound = new Sounds();
    } catch (SlickException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    this.x       = plyr.x + Main.vectors[0][plyr.dir]*.5f;
    this.y       = plyr.y + Main.vectors[1][plyr.dir]*.25f + 0.05f;
    this.fuse    = 2;
    this.flicker = 0;
    
    if (this.x < -.25f) this.x = -.25f;
    else if (this.x > 9.25) this.x = 9.25f;
    if (this.y < -.25f) this.y = -.25f;
    else if (this.y > 7) this.y = 7;
  }
  
  public void tick() {
    if (Level.tileType(this.x + .5f,this.y + .75f) == 4) this.offsety = -.1f;
    else this.offsety = 0;
    
    this.fuse -= Animate.delta/1000f;
    if (this.fuse < .5f) this.flicker += (15/1000f)*Animate.delta;
  }
  
  public void render() {
    float dx = this.x*16 ;
    float dy = this.y*16-this.z*16-this.offsety*16;
    
    if (this.fuse < .5f && (int)(this.flicker%2) == 0) {
      img.draw(dx,dy,dx+16,dy+16,16,0,32,16);
    } else img.draw(dx,dy,dx+16,dy+16,0,0,16,16);
    
    if (Level.tileType(this.x + .5f,this.y + .75f) == 3) environmentimg.draw(dx,dy+8,dx+16,dy+8+8,0,0,16,8);
    else if (Level.tileType(this.x + .5f,this.y + .75f) == 4) {
      environmentimg.draw(dx,dy+10,dx+16,dy+10+8,
                          (int)Animate.fastframe*16,8,(int)Animate.fastframe*16+16,8+8);
    }
  }
}
