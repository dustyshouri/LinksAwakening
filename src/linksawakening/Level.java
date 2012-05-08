package linksawakening;

import org.newdawn.slick.tiled.TiledMap;

public class Level {
  private static TiledMap map;
  private static Link plyr;
  
  private static int[] blockmap = {
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 7, 7, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 7, 1, 7, 1, 1, 7, 1, 1, 7, 1, 1, 7, 1, 7, 1, 7, 1, 1, 7, 1, 1, 1, 1,
    0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1,
    0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
    0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 0, 1,
    0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 3,
    0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 3, 3, 0, 6, 1, 1, 1, 1, 7, 6, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1,10,10,10, 1, 1, 0, 4, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 2,11, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1,
    1, 1, 1, 1, 6, 0, 1, 1, 1, 7, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 4, 4, 4, 4,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 9, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 5, 5, 5, 5,
    1, 1, 1, 1, 1,11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 5, 5, 5, 5,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 5, 5, 5, 5,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 5, 5, 5, 5,
    1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4,
    1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4,
    1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4,
    1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
  };
  
  public Level(String lvl) {
  }
  
  public static void getData(TiledMap m,Link p) {
    plyr = p;
    map = m;
  }
  
  public static boolean checkCollision(float cx,float cy) {
    if (Main.debug == true) return false;
    if (tileType(cx,cy) == 1) return true;
    else if ((tileType(cx,cy) == 7 || tileType(cx,cy) == 2) && ((cx % 1) < .375 || (cx % 1) > .625)) return true;
    else if (tileType(cx,cy) == 8 && (cy % 1) < .5) return true;
    else if (tileType(cx,cy) == 9 && (cx % 1) < .5) return true;
    else if (tileType(cx,cy) == 10 && (cy % 1) > .5) return true;
    else if (tileType(cx,cy) == 11 && (cx % 1) > .5) return true;
    return false;
  }
  
  public static int tileType(float x,float y) {
    return blockmap[map.getTileId(plyr.coord[0]*10 + (int)x,plyr.coord[1]*8 + (int)y,0)-1];
  }
  
  public static int tileID(float x,float y) {
    return map.getTileId(plyr.coord[0]*10 + (int)x,plyr.coord[1]*8 + (int)y,0)-1;
  }
  
  public static void setTileID(float tx,float ty, int id) {
    map.setTileId((int)(plyr.coord[0]*10+tx),(int)(plyr.coord[1]*8+ty),0,id+1);
  }
}
