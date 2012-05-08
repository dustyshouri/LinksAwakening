package linksawakening;

import org.newdawn.slick.*;

public class HUD {
  static Image invimg,weapimgs,selecticon,nums,hearts;
  static int sy,sclosedy;
  private Input input;
  private Sounds sound;
  Link plyr;
  
  static int y;
  static int closedy;
  int selection;
  float ticker;
  static boolean open;
  boolean transitioning;
  
  public HUD(GameContainer c,Input i,Sounds s) throws SlickException {
    input = i;
    sound = s;

    invimg     = new Image("res/images/la_hud-inv.png",false,0x2); 
    weapimgs   = new Image("res/images/la_weapons.png",false,0x2); 
    selecticon = new Image("res/images/la_invselection.png",false,0x2); 
    nums       = new Image("res/images/la_hud-nums.png",false,0x2);
    hearts     = new Image("res/images/la_hud-hearts.png",false,0x2); 

    closedy = (c.getHeight()/2)-16;
    open = false;
    y = closedy;
    this.selection = 0;
  }
  
  public void tick(int dt) {
    if (plyr.transitioning > -1) return;
    
    ticker += dt/1000f;
    if (ticker > 1) ticker = 0;
    
    if (open == true) {
      if (y <= 0) {
        y = 0;
        return;
      } else {
        y -= (500/1000f)*dt;
        float f = 1-((((float)closedy - (float)y)/(float)closedy)*.5f);
        sound.changeVolume(f);
      }
    } else {
      if (y >= closedy) {
        y = closedy;
        return;
      }
      else {
        y += (1000/1000f)*dt;
        float f = 1-((((float)closedy - (float)y)/(float)closedy)*.5f);
        sound.changeVolume(f);
      }
    }
    if (y <= 0) y = 0;
    if (y >= closedy) y = closedy;
    
    this.transitioning = (open == true && y > 0) || (open == false && y < closedy) || open;
  }
  
  public static boolean isFocused() {
    return open || y != closedy;
  }
  
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_Q) {
      if (!(y == 0 || y == closedy)) return;
      open = !open;
      if (open == true) {
        ticker = 0;
        sound.playSound("la_inv-open.wav");
        //sound.changeVolume(.5f);
      } else {
        sound.playSound("la_inv-close.wav");
        input.clearKeyPressedRecord();
        //sound.changeVolume(1f);
      }
    }
    
    if (open == true) {
      if (y <= 0) {
        
        for (int i=0;i<4;i++) {
          if (key == Main.keys[i]) {
            this.selection += Main.vectors[0][i];
            this.selection += Main.vectors[1][i]*2;
            
            sound.playSound("la_invmove.wav");
            ticker = 0;
            
            break;
          }
        }
        if (this.selection > 9) this.selection = 0;
        else if (this.selection < 0) this.selection = 9;
        
        if (key == Input.KEY_S) {
          Weapon tempweap = plyr.weapons[0];
          plyr.weapons[0] = plyr.inventory[this.selection];
          plyr.inventory[this.selection] = tempweap;
          sound.playSound("la_invselect.wav");
        }
        if (key == Input.KEY_D) {
          Weapon tempweap = plyr.weapons[1];
          plyr.weapons[1] = plyr.inventory[this.selection];
          plyr.inventory[this.selection] = tempweap;
          sound.playSound("la_invselect.wav");
        }
      }
    }
  }
  
  public void draw(GameContainer c) {
    invimg.draw(0,y);

    if (!plyr.weapons[0].getIDname().equals("none")) {
      weapimgs.draw(8,y,8 + 24,y + 16,
          (plyr.weapons[0].getID()%4)*24,(int)(plyr.weapons[0].getID()/4)*16,
          (plyr.weapons[0].getID()%4)*24+24,(int)(plyr.weapons[0].getID()/4)*16+16);
    }
    if (!plyr.weapons[1].getIDname().equals("none")) {
      weapimgs.draw(48,y,48 + 24,y + 16,
          (plyr.weapons[1].getID()%4)*24,(int)(plyr.weapons[1].getID()/4)*16,
          (plyr.weapons[1].getID()%4)*24+24,(int)(plyr.weapons[1].getID()/4)*16+16);
    }
    
    if (open) drawWeapons();
    int r = (int)(plyr.grupees/100);
    nums.draw(82-2,y+10-2,82-2+8,y+10-2+8,r*8,0,r*8+8,8);
    r = (int)(plyr.grupees/10)%10;
    nums.draw(82-2+8,y+10-2,82-2+8+8,y+10-2+8,r*8,0,r*8+8,8);
    r = plyr.grupees - (int)(plyr.grupees/10)*10;
    nums.draw(82-2+16,y+10-2,82-2+8+16,y+10-2+8,r*8,0,r*8+8,8);

    for (int i=0;i<plyr.maxhearts;i++) {
      int hx = i < (int)plyr.hearts ? 0 : 14;
      if (plyr.maxhearts - i == (plyr.maxhearts - (int)plyr.hearts) && (int)plyr.hearts != plyr.hearts) hx = 7;
      hearts.draw(104+(i%7)*8,y+(int)((i/7)*8)+1,104+(i%7)*8+7,y+(int)((i/7)*8)+1+7,hx,0,hx+7,7);
    }
  }
  
  public void drawWeapons() {
    if (ticker % 1 < .5f) selecticon.draw(8+(this.selection%2)*32 - 2,y+24+(int)(this.selection/2)*24 + 1);
 
    for (int i=0;i<10;i++) {
      if (plyr.inventory[i].getIDname() == "none") continue;
      int dx = 8+(i%2)*32;
      int dy = y+24+(int)(i/2)*24;
      weapimgs.draw(dx,dy,dx+24,dy+16,
                    (plyr.inventory[i].getID()%4)*24,(int)(plyr.inventory[i].getID()/4)*16,
                    (plyr.inventory[i].getID()%4)*24+24,(int)(plyr.inventory[i].getID()/4)*16+16);
    }
  }
  
  public void playerListen(Link p) {
    plyr = p;
  }
}
