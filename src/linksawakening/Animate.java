package linksawakening;

public class Animate {
  static float timer = 0,gtimer = 0,fasttimer = 0;
  static int frame = 0,fastframe = 0;
  static int delta;
  public static void add(int d) {
    delta = d;
    timer += (6/1000f) * d;
    fasttimer += (12/1000f) * d;
    gtimer += (1/1000f) * d;
    frame = (int)timer % 2;
    fastframe = (int)fasttimer % 2;
  }
}
