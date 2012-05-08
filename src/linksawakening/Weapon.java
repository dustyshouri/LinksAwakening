package linksawakening;

public enum Weapon {
  SHIELD1(0,"shield","LVL-1 Shield")         ,SWORD1(1,"sword","LVL-1 Sword"),
  MAGICPOWDER(2,"magicpowder","Magic Powder"),BOMB(3,"bomb","Bombs"),
  SHIELD2(4,"shield","LVL-2 Shield")         ,SWORD2(5,"sword","LVL-2 Sword"),
  HOOKSHOT(6,"hookshot","Hookshot")          ,OCARINA(7,"ocarina","Ocarina"),
  BRACELET1(8,"bracelet","LVL-1 Bracelet")   ,BOOTS(9,"boots","Pegusus Boots"),
  BOW(10,"bow","Bow")                        ,SHOVEL(11,"shovel","Shovel"),
  BRACELET2(12,"bracelet","LVL-2 Bracelet")  ,BOOMERANG(13,"boomerang","Boomerang"),
  FEATHER(14,"feather","Roc's Feather")      ,MAGICROD(15,"magicrod","Magic Rod"),
  MUSHROOM(16,"mushroom","Mushroom")         ,NONE(-1,"none","Nothing");
  
  private int id;
  private String idname,name;
  
  Weapon(int id,String idname,String name) {
    this.id     = id;
    this.idname = idname;
    this.name   = name;
  }
  int getID() {
    return id;
  }
  String getIDname() {
    return idname;
  }
  String getName() {
    return name;
  }
}
