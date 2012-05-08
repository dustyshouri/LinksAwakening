package linksawakening;

import java.util.ArrayList;

import org.newdawn.slick.*;

public class Map {
  Input input;
  Sounds sound;
  Image mapimg,mapblock;
  
  static float whiteout = 0;
  static boolean open = false;
  
  static ArrayList<String> visited = new ArrayList<String>();
  
  public Map(Input i,Sounds s) throws SlickException {
    this.input = i;
    this.sound = s;
    this.mapimg = new Image("res/images/la_map.png",false,0x2);
    this.mapblock = new Image("res/images/la_map-blackout.png",false,0x2);
  }
  
  public static void addVisit(int mx,int my) {
    visited.add(mx + "." + my);
  }
  
  public void tick(int dt) {
    if (open) {
      if (whiteout < 2) whiteout += ((whiteout < 1 ? 5 : 10)/1000f)*dt;
      else if (whiteout > 2) whiteout = 2;
    } else {
      if (whiteout > 0) {
        whiteout -= ((whiteout > 1 ? 5 : 10)/1000f)*dt;
        if (whiteout < 0) whiteout = 0;
      }
    }
  }
  
  public static boolean isFocused() {
    return open || whiteout > 0;
  }
  
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_M) {
      open = !open;
      input.clearKeyPressedRecord();
    }
  }
  
  public void render(Graphics g,GameContainer container) {
    if (open) {
      if (whiteout > 1) drawMap();
      if (whiteout < 2) {
        int w = (int)((whiteout < 1 ? whiteout%1 : 1 - (whiteout - 1))*255);
        g.setColor(new Color(255,255,255,w));
        g.fillRect(0,0,container.getWidth(),container.getHeight()); 
      }
    } else {
      if (whiteout > 1) drawMap();
      if (whiteout > 0) {
        int w = (int)((whiteout < 1 ? whiteout%1 : 1 - (whiteout - 1))*255);
        g.setColor(new Color(255,255,255,w));
        g.fillRect(0,0,container.getWidth(),container.getHeight()); 
      }     
    }
  }
  
  public void drawMap() {
    this.mapimg.draw(0,0);
    
    for (int i=0;i<16;i++) {
      for (int j=0;j<16;j++) {
        String b = i + "." + j;
        if (visited.indexOf(b) == -1) this.mapblock.draw(16 + i*8,8 + j*8);
      }
    }
  }
  
}
