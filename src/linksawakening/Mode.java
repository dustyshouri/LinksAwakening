package linksawakening;

public enum Mode {
  IDLE(0,0,0,false)            ,WALK(0,2,8/1000f,true)          ,PUSH(13,2,6/1000f,true),
  SHIELDIDLE(2,0,6/1000f,false),SHIELDWALK(2,2,6/1000f,true)    ,
  SHIELDHOLD(4,0,6/1000f,false),SHIELDHOLDWALK(4,2,6/1000f,true),
  SWORD(10,3,16/1000f,false)   ,SWIM(6,2,6/1000f,true)          ,
  GRAB(15,0,0,false)           ,PULL(16,0,0,false)              ,THROW(12,0,0,false),
  SHOVEL(19,2,4/1000f,false)   ,CLIFFJUMP(21,4,10/1000f,true)   ,JUMP(21,4,10/1000f,true);
  
  private final int startframe,framecount;
  private final float anispeed;
  private final boolean loop;
  Mode(int startframe,int framecount,float anispeed,boolean loop) {
    this.startframe = startframe;
    this.framecount = framecount;
    this.anispeed = anispeed;
    this.loop = loop;
  }
  int getStartFrame() {
    return startframe;
  }
  int getFrameCount() {
    return framecount;
  }
  float getAniSpeed() {
    return anispeed;
  }
  boolean getAniLoop() {
    return loop;
  }
}
