package linksawakening;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Mob {
  private static Link plyr;
  static Image octimg;
  float x,y,speed;
  String uniqueID;
  int dir;
  boolean walking,animate;
  ArrayList<String> dead = new ArrayList<String>();
  int[] coord = {0,0};
  
  public Mob(int id,int cx,int cy,int nx,int ny,String n) {
    coord[0] = cx;
    coord[1] = cy;
    this.x = nx;
    this.y = ny;
    this.dir = (int)(Math.random()*4);
    this.walking = Math.random() < .5f ? true : false;
    this.speed = 2/1000f;
    this.uniqueID = n + "." + cx + "." + cy + "." + nx + "." + ny;
    this.animate = false;
  }
  
  public void tick(int dt) {
    if (plyr.coord[0] != this.coord[0] || plyr.coord[1] != this.coord[1]) return;
    if (plyr.roomtransition[0] != 0 || plyr.roomtransition[1] != 0) return;
    if (this.walking) {
      this.x += Main.vectors[0][this.dir]*this.speed*dt;
      this.y += Main.vectors[1][this.dir]*this.speed*dt;
      this.animate = true;
    } else this.animate = false;
    if (Level.tileType(this.x + .5f + (Main.vectors[0][this.dir]*.5f) + Main.vectors[0][this.dir]*this.speed*dt,
                       this.y + .5f + (Main.vectors[1][this.dir]*.5f) + Main.vectors[1][this.dir]*this.speed*dt) != 0) {
      if (Math.random() < .5f) this.dir = (this.dir + 2)%4;
      else if (Math.random() < .5f) this.dir = (this.dir + 3)%4;
      else this.dir = (this.dir + 1)%4;
      return;
    }
    if (this.x < 0) {
      this.x = 0;
      this.dir = 3;
    } else if (this.x > 9) {
      this.x = 9;
      this.dir = 1;
    }
    if (this.y < 0) {
      this.y = 0;
      this.dir = 2;
    } else if (this.y > 7) {
      this.y = 7;
      this.dir = 0;
    }
    
    if (Math.random()*100 < 1) {
      this.walking = !this.walking;
    }
    if (Math.random()*100 < 1) {
      this.dir = (int)(Math.random()*4);
    }
  }
  
  public static void initiate(Link p) {
    plyr = p;
    try {
      octimg = new Image("res/images/la_mob-octotok1.png",false,0x2);
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void render() {
    float dx = this.x*16 - (plyr.coord[0] - this.coord[0])*10*16 - (int)(plyr.roomtransition[0]*16);
    float dy = this.y*16 - (plyr.coord[1] - this.coord[1])*8*16 - (int)(plyr.roomtransition[1]*16);
    int frame = this.animate ? Animate.frame : 0;
    octimg.draw(dx,dy,dx+16,dy+16,
        this.dir*16,frame*16,this.dir*16+16,frame*16 + 16);
  }
}
