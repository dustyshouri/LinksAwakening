package linksawakening;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class Link {
  ArrayList<Mob> mobs;
  ArrayList<Object[]> delays = new ArrayList<Object[]>();
  
  Mode mode;
  
  //INITIALIZE WEAPONS
  public Boomerang boomerang;
  public Hookshot hookshot;
  public Bomb bomb;
  
  //private ArrayList<Mob> mobs = new ArrayList<Mob>();
  static Input input;
  private Image linkimg,environmentimg,swordimg;
  private Sounds sound;
  private TiledMap map;
  private HUD hud;
  private int[][] tilecache = new int[10][8];

  int dir,transitioning,rupees,grupees,sprite,maxhearts,grabkey;
  int choosedir = -1;
  float x,y,z,offsety,speed,dt,aniframe,pushtimer,mspeed,hearts;
  float pausetimer,anipause;
  float[] roomtransition = {0,0};
  int[] coord = {0,0};
  Vector2f delta;
  
  Weapon[] defaultinv = {Weapon.SHIELD1,Weapon.FEATHER,Weapon.MAGICPOWDER,Weapon.HOOKSHOT,Weapon.MAGICROD,
                         Weapon.BRACELET1,Weapon.BOOMERANG,Weapon.BOW,Weapon.BOMB};
  Weapon[] inventory  = new Weapon[10];
  Weapon[] weapons    = new Weapon[2];
  
  
  public Link(Input inp,Sounds s) {
    sound = s;
    try {
      linkimg        = new Image("res/images/la_sprite-link.png",false,0x2);
      environmentimg = new Image("res/images/la_sprite-environment.png",false,0x2);
      swordimg       = new Image("res/images/la_weap-sword.png",false,0x2);
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    input         = inp;
    this.dir      = 2;
    this.x        = 5;
    this.y        = 5;
    this.z        = 0;
    this.offsety  = 0;
    this.coord[0] = 2;
    this.coord[1] = 10;
    //this.coord[0] = this.coord[1] = 1;
    this.rupees   = 0;
    this.speed    = 4/1000f;
    this.hearts = 3;
    this.maxhearts = 9;
    //this.hearts = this.maxhearts = 3*4;
    this.pushtimer = this.aniframe = this.pausetimer = this.anipause = 0;
    this.transitioning = -1;
    this.mode = Mode.IDLE;
    
    this.weapons[0] = Weapon.SWORD1;
    this.weapons[1] = Weapon.SHOVEL;
    Arrays.fill(this.inventory,Weapon.NONE);
    for (int i=0;i<this.defaultinv.length;i++) {
      this.inventory[i] = this.defaultinv[i];
    }
    Map.addVisit(this.coord[0],this.coord[1]);
  }
  
  public void getData(HUD h,TiledMap m,ArrayList<Mob> mb) {
    this.hud = h;
    this.map = m;
    mobs = mb;
    for (int i=0;i<10;i++) {
      for (int j=0;j<8;j++) {
        int tx = this.coord[0]*10 + i;
        int ty = this.coord[1]*8 + j;
        tilecache[i][j] = map.getTileId(tx,ty,0);
      }
    }
  }
  
  public void tick(int d) {
    if (Main.debug == true) this.speed    = 10/1000f;
    else this.speed = 4/1000f;
    if (input.isKeyPressed(Input.KEY_MINUS)) this.hearts -= .5f;
    else if (input.isKeyPressed(Input.KEY_EQUALS)) this.hearts += .5f;
    if (input.isKeyPressed(Input.KEY_LBRACKET)) this.rupees -= 50;
    else if (input.isKeyPressed(Input.KEY_RBRACKET)) this.rupees += 50;
    if (this.hearts < 0) this.hearts = 0;
    else if (this.hearts > this.maxhearts) this.hearts = this.maxhearts;
    
    if (this.rupees < 0) this.rupees = 0;
    else if (this.rupees > 999) this.rupees = 999;
    if (this.grupees != this.rupees) {
      sound.playSound("la_rupee.wav",.25f);
      if (this.grupees < this.rupees) this.grupees++;
      else if (this.grupees > this.rupees) this.grupees--;
    }
    
    dt = d;
    
    handleDelays();
    
    if (this.transitioning >= 0) {
      transitionRoom();
      return;
    }
    if (hud.transitioning) return;
    
    if (this.pausetimer <= 0) move();
    
    if (this.aniframe < this.mode.getStartFrame()) this.aniframe = this.mode.getStartFrame();
    this.aniframe += this.mode.getAniSpeed()*dt;
    if (this.aniframe > this.mode.getStartFrame() + this.mode.getFrameCount()) {
      //if (mode == Mode.SWORD) mode = Mode.IDLE;
      if (this.mode.getAniLoop() || this.mode.getFrameCount() == 0) this.aniframe = this.mode.getStartFrame();
      else this.aniframe = this.mode.getStartFrame() + this.mode.getFrameCount() - .5f;
    }
    
    if (this.pausetimer > 0) this.pausetimer -= dt/1000f;
    if (this.anipause > 0) this.anipause -= dt/1000f;
    else this.anipause = 0;

    if (bomb != null) {
      bomb.tick();
      if (bomb.fuse <= 0) {
        Effects.addBoom(bomb.x - .5f,bomb.y - .5f);
        explosion(bomb.x - .5f,bomb.y -.5f);
        bomb = null;
      }
    }
    
    if (boomerang != null) {
      boomerang.tick();
      if (boomerang.flying == false) {
        float dx   = this.x - boomerang.x;
        float dy   = this.y - boomerang.y;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        if (dist < .5f) boomerang = null;
      }
    }
    
    if (hookshot != null) {
      hookshot.tick();
      if (hookshot.mode > 0) {
        float dx   = this.x - hookshot.x;
        float dy   = this.y - hookshot.y;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        if ((hookshot.mode == 1 && dist < .25f) || (hookshot.mode == 2 && dist < .75f)) {
          hookshot.stopSound();
          hookshot = null;
        }
      }
    }
  }
  
  public void keyPressed(int key, char c) {
    if (this.transitioning >= 0 || hud.transitioning) {
      input.clearKeyPressedRecord();
      return;
    }
    
    if (this.mode == Mode.GRAB) {
      if (input.isKeyDown(Main.keys[(this.dir + 2)%4])) {
        this.mode = Mode.PULL;
      }
    }
    
    if (this.mode == Mode.GRAB || this.mode == Mode.PULL) return;
    
    if (this.mode != Mode.SWIM) {
      if (key == Input.KEY_S) {
        if (this.weapons[0].getIDname() == "none") return;
        Method method;
        try {
          method = this.getClass().getDeclaredMethod("weapon_" + this.weapons[0].getIDname(),new Class[] {Integer.TYPE});
          method.invoke(this,new Object[] {Input.KEY_S});
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    
      if (key == Input.KEY_D) {
        if (this.weapons[1].getIDname() == "none") return;
        Method method;
        try {
          method = this.getClass().getDeclaredMethod("weapon_" + this.weapons[1].getIDname(),new Class[] {Integer.TYPE});
          method.invoke(this,new Object[] {Input.KEY_D});
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void keyReleased(int key, char c) {
    if (this.mode == Mode.PULL && key == Main.keys[(this.dir + 2)%4]) this.mode = Mode.GRAB;
    if (key == this.grabkey && (this.mode == Mode.GRAB || this.mode == Mode.PULL)) this.mode = Mode.IDLE;
  }
  
  public void move() {
    if (this.mode == Mode.GRAB || this.mode == Mode.PULL) return;
    mspeed = speed;

    if (Level.tileType(this.x + .5f,this.y + .75f) == 3 || Level.tileType(this.x + .5f,this.y + .75f) == 4) mspeed = this.speed*.75f;
    if (Level.tileType(this.x + .5f,this.y + .75f) == 4 || Level.tileID(this.x + .5f,this.y + .75f) == 326) this.offsety = -.1f;
    else this.offsety = 0;
    if (Level.tileType(this.x + .5f,this.y + .75f) == 2) mspeed = this.speed*.5f;
    delta = new Vector2f(
      input.isKeyDown(Input.KEY_LEFT) ? -1 : (input.isKeyDown(Input.KEY_RIGHT) ? 1 : 0),
      input.isKeyDown(Input.KEY_UP) ? -1 : (input.isKeyDown(Input.KEY_DOWN) ? 1 : 0)
    );
    
    boolean collideXone = Level.checkCollision(this.x + .49f + delta.getX()*.225f + delta.getX()*mspeed*dt,this.y + .55f + delta.getY()*mspeed*dt);
    boolean collideXtwo = Level.checkCollision(this.x + .49f + delta.getX()*.225f + delta.getX()*mspeed*dt,this.y + .8f + delta.getY()*mspeed*dt);
    
    boolean collideYone = Level.checkCollision(this.x + .4f + delta.getX()*mspeed*dt,this.y + .7f + delta.getY()*.25f + delta.getY()*mspeed*dt);
    boolean collideYtwo = Level.checkCollision(this.x + .6f + delta.getX()*mspeed*dt,this.y + .7f + delta.getY()*.25f + delta.getY()*mspeed*dt);
    
    //if (!collideXone && !collideXtwo && !collideYone && !collideYtwo) {
      if (delta.length() > 1) delta.normalise();
    //}
    
    if (!collideXone && !collideXtwo) this.x += delta.getX()*mspeed*dt;
    else {
      if (Level.tileType(this.x + .5f,this.y + .85f) != 7 && delta.getX() != 0) {
        if (collideXone && !collideXtwo) {
          if (!Level.checkCollision(this.x + .5f,this.y + .7f + .25f + 2/1000f*dt)) this.y += 2/1000f*dt;
        }
        else if (!collideXone && collideXtwo) {
          if (!Level.checkCollision(this.x + .5f,this.y + .7f - .25f - 2/1000f*dt)) this.y -= 2/1000f*dt;
        }
      }
    }
    //else this.x = delta.getX() == 1 ? (int)this.x + .2f : (int)(this.x + .5f) - .2f;
    
    if (!collideYone && !collideYtwo) {
      this.y += delta.getY()*mspeed*dt;
      if (Level.tileType(this.x + .5f,this.y + .85f) == 7) this.x = (int)(this.x + .5f); 
    } else {
      if (Level.tileType(this.x + .5f,this.y + .85f) != 7 && delta.getY() != 0) {
        if (collideYone && !collideYtwo) {
          if (!Level.checkCollision(this.x + .5f + .25f + 2/1000f*dt,this.y + .75f)) this.x += 2/1000f*dt;
        } else if (!collideYone && collideYtwo) {
          if (!Level.checkCollision(this.x + .5f - .25f - 2/1000f*dt,this.y + .75f)) this.x -= 2/1000f*dt;
        }
      }
    }
    
    if (delta.length() > 0) {
      if (collideXone || collideXtwo || collideYone || collideYtwo) {
        if (this.mode == Mode.WALK || this.mode == Mode.SHIELDWALK) {
          if (this.pushtimer > 20/1000f) {
            int[] jumptiles = {271,272,273};
            if (this.dir == 2 && Arrays.binarySearch(jumptiles,Level.tileID(this.x + .5f,this.y + 1)) >= 0) {
              this.x = (int)(this.x + .5f);
              this.y = (int)this.y + .5f;
              this.mode = Mode.CLIFFJUMP;
              this.pausetimer = 1000/1000f;
            } else this.mode = Mode.PUSH;
          }
          this.pushtimer += dt/1000;
        } else {
          if (this.mode != Mode.PUSH) {
            if (this.mode == Mode.SHIELDHOLDWALK && !input.isKeyDown(Input.KEY_D)) this.mode = Mode.WALK;
            this.mode = this.weapons[1].getIDname().startsWith("shield") ? (input.isKeyDown(Input.KEY_D) ? Mode.SHIELDHOLDWALK : Mode.SHIELDWALK): Mode.WALK;
          }
        }
      } else {
        if (this.anipause == 0) {
          if (this.mode == Mode.SHIELDHOLDWALK && !input.isKeyDown(Input.KEY_D)) this.mode = Mode.WALK;
          this.mode = this.weapons[1].getIDname().startsWith("shield") ? (input.isKeyDown(Input.KEY_D) ? Mode.SHIELDHOLDWALK : Mode.SHIELDWALK): Mode.WALK;
        }
        this.pushtimer = 0;
      }
    } else {
      //weapons
      setIdle();
      this.pushtimer = 0;
    }
    
    if (input.isKeyPressed(Input.KEY_UP) || input.isKeyPressed(Input.KEY_LEFT) ||
        input.isKeyPressed(Input.KEY_DOWN) || input.isKeyPressed(Input.KEY_RIGHT)) {
      aniframe = mode.getStartFrame() + mode.getFrameCount()/2;
    }
    //else this.y = delta.getY() == 1 ? (int)(this.y + .5f) : (int)(this.y)+.5f;
    
    /*
    if (delta.getY() < 0) this.dir = 0;
    else if (delta.getY() > 0) this.dir = 2;
    if (delta.getX() < 0) this.dir = 1;
    else if (delta.getX() > 0) this.dir = 3;
    */
    
    if (choosedir == -1) {
      for (int i=0;i<4;i++) {
        if (input.isKeyDown(Main.keys[i])) {
          this.dir = choosedir = i;
          break;
        }
      }
    } else {
      if (!(input.isKeyDown(Main.keys[0]) || input.isKeyDown(Main.keys[1]) ||
          input.isKeyDown(Main.keys[2]) || input.isKeyDown(Main.keys[3]))) choosedir = -1;
      else if (!input.isKeyDown(Main.keys[choosedir]) || input.isKeyDown(Main.keys[(choosedir+2)%4])) choosedir = -1;
    }
    
    if (this.x < -.25) changeRoom(1);
    else if (this.x > 9.25) changeRoom(3);
    if (this.y < -.25) changeRoom(0);
    else if (this.y > 7.25) changeRoom(2);
    
    if (Level.tileType(this.x + .5f,this.y + .65f) == 5) this.mode = Mode.SWIM;
    //if (delta.length() == 0) this.aniframe = 0;
  }
  
  public void setIdle() {
    if (this.anipause > 0) return;
    this.mode = this.weapons[1].getIDname().startsWith("shield") ? (input.isKeyDown(Input.KEY_D) ? Mode.SHIELDHOLD : Mode.SHIELDIDLE) : Mode.IDLE;
  }
  
  public void transitionRoom() {
    if (this.transitioning == 0) {
      this.roomtransition[1] -= 16/1000f*dt;
      this.y += 14/1000f*dt;
    } else if (this.transitioning == 1) {
      this.roomtransition[0] -= 20/1000f*dt;
      this.x += 18/1000f*dt;
    } else if (this.transitioning == 2) {
      this.roomtransition[1] += 16/1000f*dt;
      this.y -= 14/1000f*dt;
    } else if (this.transitioning == 3) {
      this.roomtransition[0] += 20/1000f*dt;
      this.x -= 18/1000f*dt;
    }
    String songid = findSong(this.coord[0]+Main.vectors[0][this.transitioning],this.coord[1]+Main.vectors[1][this.transitioning]);
 
    if (!songid.equals(sound.currentSong)) {
      if (this.transitioning > -1) {
        float v = this.transitioning == 1 || this.transitioning == 3 ? 1 - Math.abs(this.roomtransition[0]/10) : 1 - Math.abs(this.roomtransition[1]/8);
        if (v > 1) v = 1;
        else if (v < 0) v = 0;
        sound.changeVolume(v);
      }
    }
    
    if (this.roomtransition[1] < -8 || this.roomtransition[0] < -10 || 
        this.roomtransition[1] > 8 || this.roomtransition[0] > 10) {
      
      for (int i=0;i<10;i++) {
        for (int j=0;j<8;j++) {
          int tx = this.coord[0]*10 + i;
          int ty = this.coord[1]*8 + j;
          if (tilecache[i][j] != 0) map.setTileId(tx,ty,0,tilecache[i][j]);
        }
      }
      
      this.roomtransition[0] = this.roomtransition[1] = 0;
      this.coord[0] += Main.vectors[0][this.transitioning];
      this.coord[1] += Main.vectors[1][this.transitioning];

      for (int i=0;i<10;i++) {
        for (int j=0;j<8;j++) {
          int tx = this.coord[0]*10 + i;
          int ty = this.coord[1]*8 + j;
          tilecache[i][j] = map.getTileId(tx,ty,0);
        }
      }
      
      String song = findSong(this.coord[0],this.coord[1]);
      sound.playSong(song);
      
      input.clearKeyPressedRecord();
      this.transitioning = -1;
      
      if (this.boomerang != null) boomerang = null;
      
      Map.addVisit(this.coord[0],this.coord[1]);
    }
  }
  
  public String findSong(int cx,int cy) {
    String song = "la_overworld.mid";
    for (int i=0;i<map.getObjectCount(0);i++) {
      if (cx*10 + 5 < map.getObjectX(0,i)/16) continue;
      if (cx*10 + 5 > map.getObjectX(0,i)/16+map.getObjectWidth(0,i)/16) continue;
      if (cy*8 + 4 < map.getObjectY(0,i)/16) continue;
      if (cy*8 + 4 > map.getObjectY(0,i)/16+map.getObjectHeight(0,i)/16) continue;
      
      song = map.getObjectProperty(0,i,"song","la_mabe.mid");
      break;
    }
    return song;
  }
  
  public void changeRoom(int d) {
    int tx = this.coord[0] + Main.vectors[0][d];
    int ty = this.coord[1] + Main.vectors[1][d];
    if (map.getLayerIndex("Mobs") > -1) {
      for (int i=0;i<10;i++) {
        for (int j=0;j<8;j++) {
          int id = map.getTileId(tx*10 + i,ty*8 + j,map.getLayerIndex("Mobs"));

          if (id <= 0) continue;
          mobs.add(new Mob(id,tx,ty,i,j,map.getMapProperty("Name","Hell")));
        }
      }
    }
    bomb = null;
    
    if (d >= 0) this.transitioning = d;
  }
  
  public void render() {
    if (bomb != null) bomb.render();
    if (boomerang != null) boomerang.render();
    if (hookshot != null) hookshot.render();
    
    float dx = this.x*16 ;
    float dy = this.y*16-this.z*16-this.offsety*16;
    if (this.mode == Mode.SWORD) {
      int swordframe = 2-(12-(int)this.aniframe);
      swordimg.draw(dx-16,dy-16,dx-16+48,dy-16+48,
                    this.dir*48,swordframe*48,this.dir*48+48,swordframe*48+48);
      if (swordframe == 2) {
        dx += Main.vectors[0][this.dir]*3;
        dy += Main.vectors[1][this.dir]*3;
      }
    }
    linkimg.draw(dx,dy,dx+16,dy+16,
                 this.dir*16,(int)this.aniframe*16,this.dir*16+16,(int)this.aniframe*16+16);
    
    float ex = this.x + .5f;
    float ey = this.y + .75f;
    
    if (this.transitioning == -1) {
      if (Level.tileType(ex,ey) == 3) {
        environmentimg.draw(dx,dy+8,dx+16,dy+8+8,
                            (int)this.aniframe*16,0,(int)this.aniframe*16+16,8);
      } else if (Level.tileType(ex,ey) == 4) {
        environmentimg.draw(dx,dy+10,dx+16,dy+10+8,
                            (int)Animate.fastframe*16,8,(int)Animate.fastframe*16+16,8+8);
      }
    }
  }
  
  public void explosion(float ex,float ey) {
    int[][] slashable = {
      {229,220,1},
      {253,220,0},
      {191,190,0},
      {215,190,1},
      {254,370,0},
    };
    
    for (int j=0;j<9;j++) {
      int hittile = -1;
      float cx = ex - .25f + j%3;
      float cy = ey - .25f + (int)(j/3);
      
      if (cx < 0 || cx > 10 || cy < 0 || cy > 8) continue;
      
      if (Level.tileID(cx,cy) == 386) {
        sound.playSound("la_secret.wav",.5f);
        Level.setTileID(cx,cy,297);
      }
                                              
      for (int i=0;i<slashable.length;i++) {
        if (Level.tileID(cx,cy) == slashable[i][0]) {
          hittile = i;
          break;
        }
      }
      
      if (hittile >= 0) {
        int[] i = {(int)cx,(int)cy,slashable[hittile][1],slashable[hittile][2]};
        float delay = (float)(Math.random())*250;
        delayInvoke("cutBush",delay/1000f,i);
      }
    }
  }
  
  // WEAPONS
  
  public void weapon_sword(int key) {
    if (this.mode != Mode.SWORD && this.pausetimer > 0) return;
    int[][] slashable = {
      {229,220,1},
      {253,220,0},
      {191,190,0},
      {215,190,1},
      {254,370,0},
    };
    sound.playSound("la_sword" + (int)(Math.random()*4+1) + ".wav",.5f);
    int cx = (int)(this.x + .5f + Main.vectors[0][this.dir]);
    int cy = (int)(this.y + .5f + Main.vectors[1][this.dir]);
    
    this.pausetimer = 180/1000f;
    this.mode = Mode.SWORD;
    this.aniframe = this.mode.getStartFrame();
    
    if (cx < 0 || cx > 9 || cy < 0 || cy > 7) return;
    
    int hittile = -1;
    for (int i=0;i<slashable.length;i++) {
      if (Level.tileID(cx,cy) == slashable[i][0]) {
        hittile = i;
        break;
      }
    }
    
    if (hittile >= 0) {
      /*
      int[] i = {cx,cy,slashable[hittile][1],slashable[hittile][2]};
      float delay = (float)(Math.random())*500;
      delayInvoke("cutBush",delay/1000f,i);
      */
      Level.setTileID(cx,cy,slashable[hittile][1]);
      Effects.addLeaf(cx - .5f,cy - .5f,slashable[hittile][2]); 
    }
  }
  
  public void weapon_boomerang(int key) {
    if (boomerang == null) {
      this.mode = Mode.THROW;
      this.anipause = 200/1000f;
      boomerang = new Boomerang(this,this.coord[0],this.coord[1]);
    }
  }
  
  public void weapon_bracelet(int key) {
    float cx = this.x + .5f + Main.vectors[0][this.dir]*.5f;
    float cy = this.y + .75f + Main.vectors[1][this.dir]*.5f;
    if (Level.checkCollision(cx,cy)) {
      this.grabkey = key;
      this.mode = Mode.GRAB;
    }
  }
  
  public void weapon_hookshot(int key) {
    if (hookshot == null) {
      this.mode = Mode.THROW;
      this.pausetimer = 200/1000f;
      hookshot = new Hookshot(this);
    }
  }
  
  public void weapon_feather(int key) {
    if (this.z == 0) {
      this.pausetimer = 1000/1000f;
      this.mode = Mode.JUMP;
    }
  }
  
  public void weapon_magicpowder(int key) {
    if (this.mode == Mode.THROW) return;
    this.mode = Mode.THROW;
    this.anipause = 400/1000f;
    float px = this.x + Main.vectors[0][this.dir]*.85f - .5f;
    float py = this.y + Main.vectors[1][this.dir]*.85f - .2f;
    Effects.addMagic(px,py);
  }
  
  public void weapon_bomb(int key) {
    if (bomb == null) {
      sound.playSound("la_placebomb.wav",.5f);
      this.mode = Mode.THROW;
      this.anipause = 200/1000f;
      bomb = new Bomb(this);
    }
  }
  
  public void weapon_shovel(int key) {
    if (this.pausetimer > 0) return;
    int[] idigit = {
        120,121,122,123,124,125,126,127,144,145,146,147,
        148,149,150,151,168,169,170,171,172,173,174,175,
        190,192,193,194,195,196,197,198,199,206,207,214,
        216,217,218,219,220,221,222,239,240,241,242,243,
        244,245,246,247,276,306,307,308,327,328,329,330,
        331,332,351,354,355,356,370,375,376,377,378,379,
        400,401,402,403,424,425,426,427,448,449,450,451,
        472,473,474,475,496,497,498,499,
    };
    int cx = (int)(this.x + .5f + Main.vectors[0][this.dir]*.5f);
    int cy = (int)(this.y + .5f + Main.vectors[1][this.dir]*.5f);
    
    if (cx < 0 || cx > 9 || cy < 0 || cy > 7) return;
    
    if (Arrays.binarySearch(idigit,Level.tileID(cx,cy)) >= 0) {
      int[] i = {cx,cy};
      sound.playSound("la_shovel.wav",.5f);
      delayInvoke("digHole",250/1000f,i);
    } else sound.playSound("la_swordtap.wav",.5f);

    this.pausetimer = 500/1000f;
    this.mode = Mode.SHOVEL;
  }
  
  public void delayInvoke(String s,float t,int[] i) {
    Object[] str = {s,0,t,i};
    delays.add(str);
  }
  
  public void delayInvoke(String s,float t) {
    Object[] str = {s,0,t,null};
    delays.add(str);
  }
  
  public void handleDelays() {
    for (int i=delays.size()-1;i>-1;i--) {
      String methodname = (String)delays.get(i)[0];
      int[] args = (int[])delays.get(i)[3];
      float fuse = Float.valueOf(delays.get(i)[2].toString());
      float ticker = Float.valueOf(delays.get(i)[1].toString());
        
      if (ticker > fuse) {
        Method method;
        try {
          method = this.getClass().getDeclaredMethod(methodname,new Class[] {int[].class});
          method.invoke(this,new Object[] {args});
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
        
        delays.remove(i);
      } else {
        ticker += dt/1000f;
        delays.get(i)[1] = ticker;
      }
    }
  }
  
  public void digHole(int[] i) {
    Level.setTileID(i[0],i[1],326);
  }
  
  public void cutBush(int[] i) {
    Level.setTileID(i[0],i[1],i[2]);
    Effects.addLeaf(i[0] - .5f,i[1] - .5f,i[3]); 
  }
  
}
