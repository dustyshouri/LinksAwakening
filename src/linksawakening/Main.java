package linksawakening;

import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.midi.MidiUnavailableException;
import linksawakening.HUD;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.TiledMap;

public class Main extends BasicGame {
  public HUD hud;
  public static TiledMap tilemap;
  public Map map;
  public Input input;
  public Link plyr;
  public Sounds sound;
  public Effects effects;
  
  int test;
  
  static public int[][] vectors = {{0,-1,0,1},{-1,0,1,0}};
  static public int[]   keys    = {Input.KEY_UP,Input.KEY_LEFT,Input.KEY_DOWN,Input.KEY_RIGHT};
  
  float anitimer = 0;
  int tileid = 0,saturation = 50;
  static int scale = 2;
  
  boolean mousedown = false;
  public static boolean debug = false;
  ArrayList<Mob> mobs = new ArrayList<Mob>();
  
  public Main() {
    super("Legend of Zelda - Link's Awakening"); 
  }
  
  @Override
  public void init(GameContainer container) throws SlickException {
    int testscore = 0;
    for (int i=0;i<24;i++) {
      testscore += 80 + (i*20);
    }
    System.out.println(testscore);
    
    
    container.setIcon("res/images/la_icon.png");
    input = container.getInput();
    
    try {
      sound = new Sounds();
      effects  = new Effects();
    } catch (MidiUnavailableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    map      = new Map(input,sound);
    plyr     = new Link(input,sound);
    hud      = new HUD(container,input,sound);
    tilemap  = new TiledMap("res/maps/koholit.tmx","res");
    
    Mob.initiate(plyr);
    hud.playerListen(plyr);
    plyr.getData(hud,tilemap,mobs);
    
    Level.getData(tilemap,plyr);

    sound.playSong("la_mabe.mid");
  } 

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    if (container.hasFocus()) {
      if (container.isPaused()) {
        sound.resumeSong();
        container.resume();
      }
    } else if (!container.isPaused()) {
      sound.pauseSong();
      container.pause();
    }

    Effects.checkClearance();
    saturation += Mouse.getDWheel()/10;
    if (saturation < 0) saturation = 0;
    else if (saturation > 255) saturation = 255;
    
    sound.applyVolume();
    Animate.add(delta);
    test = delta;
    if (input.isKeyDown(Input.KEY_ESCAPE)) container.exit();
    if (input.isKeyPressed(Input.KEY_F1)) {
      if (scale == 4) scale = 1;
      else if (scale == 3) scale = 4;
      else if (scale == 1) scale = 2;
      else scale = 3;
      
      AppGameContainer con = (AppGameContainer) container;
      con.setDisplayMode(160*scale,144*scale, false);
    }
    if (input.isKeyPressed(Input.KEY_F2)) debug = !debug;
    
    if (!Map.isFocused()) {
      hud.tick(delta);
      if (!HUD.isFocused()) plyr.tick(delta);
    } else map.tick(delta);
    
    if (!Map.isFocused() && !HUD.isFocused() && plyr.transitioning == -1) {
      animateTiles(delta);
    
      for (int i = mobs.size() - 1; i > -1; i--) {
        Mob m = mobs.get(i);
        if (plyr.transitioning >= 0) continue;
        if (plyr.coord[0] != m.coord[0] || plyr.coord[1] != m.coord[1]) {
          mobs.remove(i);
          m = null;
          continue;
        }
        m.tick(delta);
      }
    }

  } 
  
  public void keyPressed(int key, char c) {
    if (!Map.isFocused() && plyr.transitioning == -1) hud.keyPressed(key,c);
    if (!HUD.isFocused() && plyr.transitioning == -1) map.keyPressed(key,c);
    if (!HUD.isFocused()&& !Map.isFocused()) plyr.keyPressed(key,c);
  }
  
  public void keyReleased(int key, char c) {
    if (!HUD.isFocused()&& !Map.isFocused()) plyr.keyReleased(key,c);
  }
  
  public void mousePressed(int btn, int mx, int my) {
    if (btn != 1) return;
    mobs.add(new Mob(0,plyr.coord[0],plyr.coord[1],mx/(16*scale),my/(16*scale),tilemap.getMapProperty("Name","Hell")));
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.setBackground(new Color(Color.black));
    
    g.pushTransform();
    g.scale(scale,scale);
    
    tilemap.render(-(10*16) - (int)(plyr.roomtransition[0]*16),-(8*16) - (int)(plyr.roomtransition[1]*16),plyr.coord[0]*10 - 10,plyr.coord[1]*8 - 8,10*3,8*3,0,false);
 
    for (int i=0;i<mobs.size();i++) {
      mobs.get(i).render();
    }
    
    Effects.render();
    plyr.render();
    hud.draw(container);
    if (Map.isFocused()) map.render(g,container);
    
    g.popTransform();
    
    g.scale(scale*.5f,scale*.5f);
    
    g.setColor(new Color(127,127,127,saturation));
    g.fillRect(0,0,container.getWidth(),container.getHeight());
    
    if (debug) {
      g.setColor(Color.black);
      g.drawRect((int)(Mouse.getX()/32*32)+1,(int)((container.getHeight() - Mouse.getY())/32*32)+1,32,32);
      g.setColor(Color.white);
      g.drawRect((int)(Mouse.getX()/32*32),(int)((container.getHeight() - Mouse.getY())/32*32),32,32);
      int tileid = Level.tileID(Mouse.getX()/32,(container.getHeight() - Mouse.getY())/32);
      g.setColor(Color.black);
      g.drawString("TileID : " + tileid,container.getWidth() - 125 + 1,10 + 1);
      g.setColor(Color.white);
      g.drawString("TileID : " + tileid,container.getWidth() - 125,10);
      
      String t = "FPS       : " + container.getFPS() + "\n" +
                 "Position  : " + plyr.coord[0] + "/" + plyr.coord[1] + "\n" +
                 "Mob Count : " + mobs.size() + "\n" +
                 "Choosedir : " + plyr.choosedir;
      g.setColor(Color.black);
      g.drawString(t,10 + 1,10 + 1);
      g.setColor(Color.white);
      g.drawString(t,10,10);
    }
  }

  public static void main(String[] args) {
    try {
      AppGameContainer app = new AppGameContainer(new Main());
      app.setShowFPS(false);
      app.setAlwaysRender(false);

      app.setSmoothDeltas(true);
      app.setDisplayMode(160*scale,144*scale,false);
      app.setTargetFrameRate(60);
      //app.setVSync(true);
 
      app.start();
    } catch (SlickException e) {
      e.printStackTrace(); 
    }
  }
  
  public void animateTiles(float dt) {
    float anispeed = (3/1000f)*dt;
    int[][] anitiles = {
        {377,378,379,380},
        {401,402,403,404},
        {425,426,427,428},
        {449,450,451,452},
        {473,474,475,476},
        {497,498,499,500},
        {381,382,383,384},
        {405,406,407,408},
        {429,430,431,432},
        {453,454,455,456},
        {477,478,479,480},
        {501,502,503,504},
        {521,522,523,524},
        {545,546,547,548},
        {569,570,571,572},
        {593,594,595,596},
        {525,526,527,528},
        {549,550,551,552},
        {573,574,575,576},
        {597,598,599,600},
        {301,302,303,304},
    };
    
    int[][] speedrooms = {
        new int[]{1,9},new int[]{1,10},new int[]{1,11},
        new int[]{2,9},new int[]{2,10},new int[]{2,11},
        new int[]{3,9},new int[]{12,3},new int[]{13,3},
        new int[]{12,4},new int[]{13,4},new int[]{14,4},new int[]{15,4},
        new int[]{12,5},new int[]{13,5},new int[]{14,5},new int[]{15,5},
        new int[]{12,6},new int[]{13,6},new int[]{14,6},new int[]{15,6},
        new int[]{12,7},new int[]{13,7},new int[]{14,7},new int[]{15,7},
        new int[]{15,8},new int[]{14,8},new int[]{15,9},new int[]{15,10}
    };

    for (int[] i : speedrooms) {
      if (Arrays.equals(plyr.coord,i)) anispeed += (6/1000f)*dt;
    }
    
    for (int i=0;i<10*3;i++) {
      for (int j=0;j<8*3;j++) {
        for (int[] k : anitiles) {
          int tx = plyr.coord[0]*10 - 10 + i;
          int ty = plyr.coord[1]*8 - 8 + j;
          if (tx < 0 || ty < 0 || tx > tilemap.getWidth()-1 || ty > tilemap.getHeight()-1) continue;
          if (Arrays.binarySearch(k,tilemap.getTileId(tx,ty,0)) >= 0) tilemap.setTileId(tx,ty,0,k[(int)anitimer]);
        }
      }
    }
    
    anitimer += anispeed;
    if (anitimer > 4) anitimer = 0;
  }
  
}