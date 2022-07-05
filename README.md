# 坦克大战游戏开发设计模式总结

github地址：

+ [https://github.com/2017403603/TankGame_Simple1.0.git](https://github.com/2017403603/TankGame_Simple1.0.git)
+ [https://github.com/2017403603/TankGame.git](https://github.com/2017403603/TankGame.git)

## 1. 遇到的问题

1. 如何定义主战坦克的方向？**使用Enum枚举类定义上下左右四个方向，（枚举类本身也是一个饿汉式单例模式）**
2. 根据按键改变主战坦克方向：没按下一个键设置主坦克的方向属性，坦克拥有速度属性，坦克根据方向和速度前进。
3. 如何创建更多坦克和子弹？：将坦克和子弹都封装成对象类。
4. 画面坦克和子弹闪烁问题：使用双缓冲解决，这是游戏开发中的概念，当计算速度跟不上显示速度时，画面会一条条快速显示出来，这时就会发生闪烁现象；双缓冲解决：**在显示之前，先计算并将计算结果放到内存中，在一次性给程序进行显示，就能解决闪烁问题**。
5. 如何打出一串子弹？：将子弹对象存在一个list之中，设置一个是否存活属性，当子弹飞出或者击中敌人，视为子弹死亡，从list之中remove出去。
6. 如何分辨敌我？：定义一个枚举类，枚举类里面有good和bad两个类，把这两个类作为属性，每次碰撞检测进行判断。
7. 如何产生爆炸？：先加载一组图片，当要产生爆炸时，快速遍历这组图片，以此来达到爆炸的动画特效。
8. 音乐与音效：定义一个音频类，并将此类的播放方法开启一个线程，每次爆炸或者移动、射击时加载对应的音频并播放
9. 如何使敌人更智能？：给敌方坦克一个移动速度，每次随机给敌方坦克一个移动方向，并设置一个随机开火的概率，这样就可以展现出敌方坦克在移动并射击的效果。
10. **边界检测**：给子弹和坦克都定义一个内部矩阵类，每个矩阵类代表每个类的形状和大小，自带的矩阵类有判断是否重合的方法，若重合则不能继续移动；也可以计算出每个矩阵的外接圆，每次判断矩阵外接圆圆心的距离，来判断是否重叠。将矩阵类作为内部类的一个原因是防止内存泄漏。
11. 在Property中事先写好游戏及角色的一些属性，在开启游戏前先加载信息。

## 2. 策略模式的使用

1. 坦克的开火使用策略模式，定义两个策略：

   + **发射一颗子弹**

   + **四面发射四颗子弹**

   + FireStrategy接口：

     ```java
     public interface FireStrategy extends Serializable {
     	void fire(Tank t);
     }
     ```

   + DefaultFireStrategy类：

     ```java
     public class DefaultFireStrategy implements FireStrategy {
     
      @Override
      public void fire(Tank t) {
       int bX = t.x + Tank.WIDTH/2 - Bullet.WIDTH/2;
       int bY = t.y + Tank.HEIGHT/2 - Bullet.HEIGHT/2;
       new Bullet(bX, bY, t.dir, t.group);
       if(t.group == Group.GOOD) new Thread(()->new Audio("audio/tank_fire.wav").play()).start();
      }
     
     }
     ```

   + FourDirFireStrategy类：

     ```java
     public class FourDirFireStrategy implements FireStrategy {
     
      @Override
      public void fire(Tank t) {
       int bX = t.x + Tank.WIDTH/2 - Bullet.WIDTH/2;
       int bY = t.y + Tank.HEIGHT/2 - Bullet.HEIGHT/2;
       Dir[] dirs = Dir.values();
       for(Dir dir : dirs) {
        new Bullet(bX, bY, dir, t.group);
       }
       if(t.group == Group.GOOD) new Thread(()->new Audio("audio/tank_fire.wav").play()).start();
      }
     }
     ```

   + 将策略名作为字符串存储在property中，在需要使用某种策略时通过反射加载。

## 3. 工厂设计模式

1. **将爆炸类和子弹类各自抽象出一个基类，可以定制不同的爆炸类产生不同的爆炸效果，也可以定制不同的子弹类型** ，在需要对应的类型时，直接使用工厂类生成，也可以将坦克抽象出一个基类，可以使用工厂类生产不同类型的坦克，在生产过程中不用考虑创建过程，只需看到结果。

## 4. 中介者模式

1. **加门面思想**：之前每个坦克子弹类都持有界面类的引用，现在将所有坦克子弹直接放到gamemodel类之中，不在直接与界面打交道，gamemodel起一个门面的作用(**Model和View分离**)，让GameModel持有所有对象的引用，当其他类之间需要打交道时，都通过GameModel来沟通。

2. ```java
   public abstract class GameObject implements Serializable {
   	public int x, y;
   	
   	public abstract void paint(Graphics g);
   	public abstract int getWidth();
   	public abstract int getHeight();
   }
   ```

3. **中介者调停**：将所有游戏里面的画面类如墙、子弹、坦克、爆炸类都抽象出一个基类gameobject类，在中介者类中定义一个list存储基类gameobject类，在每次添加墙、子弹、坦克、爆炸类时直接丢进list，在使用时循环对gameobject类的子类进行类型判断，便可知道是哪一个具体的类。

```java
public class GameModel {

 private static final GameModel INSTANCE = new GameModel();
 
 static {
  INSTANCE.init();
 }

 Tank myTank;

 // List<Bullet> bullets = new ArrayList<>();
 // List<Tank> tanks = new ArrayList<>();
 // List<Explode> explodes = new ArrayList<>();
 ColliderChain chain = new ColliderChain();

 private List<GameObject> objects = new ArrayList<>();

 public static GameModel getInstance() {
  return INSTANCE;
 }

 private GameModel() {}

 private void init() {
  // 初始化主战坦克
  myTank = new Tank(200, 400, Dir.DOWN, Group.GOOD);

  int initTankCount = Integer.parseInt((String) PropertyMgr.get("initTankCount"));

  // 初始化敌方坦克
  for (int i = 0; i < initTankCount; i++) {
   new Tank(50 + i * 80, 200, Dir.DOWN, Group.BAD);
  }

  // 初始化墙
  add(new Wall(150, 150, 200, 50));
  add(new Wall(550, 150, 200, 50));
  add(new Wall(300, 300, 50, 200));
  add(new Wall(550, 300, 50, 200));
 }

 public void add(GameObject go) {
  this.objects.add(go);
 }

 public void remove(GameObject go) {
  this.objects.remove(go);
 }

 public void paint(Graphics g) {
  Color c = g.getColor();
  g.setColor(Color.WHITE);
  // g.drawString("子弹的数量:" + bullets.size(), 10, 60);
  // g.drawString("敌人的数量:" + tanks.size(), 10, 80);
  // g.drawString("爆炸的数量:" + explodes.size(), 10, 100);
  g.setColor(c);

  myTank.paint(g);
  for (int i = 0; i < objects.size(); i++) {
   objects.get(i).paint(g);
  }

  // 互相碰撞
  for (int i = 0; i < objects.size(); i++) {
   for (int j = i + 1; j < objects.size(); j++) { // Comparator.compare(o1,o2)
    GameObject o1 = objects.get(i);
    GameObject o2 = objects.get(j);
    // for
    chain.collide(o1, o2);
   }
  }

  // for (int i = 0; i < bullets.size(); i++) {
  // for (int j = 0; j < tanks.size(); j++)
  // bullets.get(i).collideWith(tanks.get(j));
  // }

 }

 public Tank getMainTank() {
  return myTank;
 }
 
 public void save() {
  File f = new File("c:/mashibing/tank.data");
  ObjectOutputStream oos = null;
  try {
   oos = new ObjectOutputStream(new FileOutputStream(f));
   oos.writeObject(myTank);
   oos.writeObject(objects);
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } finally {
   if(oos != null) {
    try {
     oos.close();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
  }
 }

 public void load() {
  File f = new File("c:/mashibing/tank.data");
  try {
   ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
   myTank = (Tank)ois.readObject();
   objects = (List)ois.readObject();
   
   
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } catch (ClassNotFoundException e) {
   e.printStackTrace();
  }
 }

}
```

## 5. 责任链模式

1. 在判断类与类之间碰撞检测时，此时我们不知道类是子弹还是坦克还是墙，定义一个Collider接口，分别定义不同的类实现判断碰撞的方法，如BulletTankCollider类负责子弹和坦克的碰撞检测，TankTankCollider类负责坦克和坦克的碰撞检测。

2. Collider接口：

   ```java
   public interface Collider {
    boolean collide(GameObject o1, GameObject o2);
   }
   ```

3. BulletTankCollider类:

   ```java
   public class BulletTankCollider implements Collider {
   
    @Override
    public boolean collide(GameObject o1, GameObject o2) {
     if(o1 instanceof Bullet && o2 instanceof Tank) {
      Bullet b = (Bullet)o1;
      Tank t = (Tank)o2;
      //TODO copy code from method collideWith
      if(b.group == t.getGroup()) return true;
      
      if(b.rect.intersects(t.rect)) {
       t.die();
       b.die();
       int eX = t.getX() + Tank.WIDTH/2 - Explode.WIDTH/2;
       int eY = t.getY() + Tank.HEIGHT/2 - Explode.HEIGHT/2;
       new Explode(eX, eY);
       return false;
      }
      
     } else if (o1 instanceof Tank && o2 instanceof Bullet) {
      return collide(o2, o1);
     } 
     
     return true;
     
    }
   
   }
   ```

4. TankTankCollider类：

   ```java
   public class TankTankCollider implements Collider {
   
    @Override
    public boolean collide(GameObject o1, GameObject o2) {
     if(o1 instanceof Tank && o2 instanceof Tank) {
      Tank t1 = (Tank)o1;
      Tank t2 = (Tank)o2;
      if(t1.getRect().intersects(t2.getRect())) {
       t1.back();
       t2.back();
      }
      
     } 
     
     return true;
     
    }
   
   }
   ```

5. 定义一个责任链ColliderChain类也实现Collider接口，里面集成所有的责任类，定义一个list放BulletTankCollider类、TankTankCollider类等，在初始化的时候写好责任链的顺序，**在需要判断碰撞检测责任的时候，循环遍历所有责任类，并调用对应类的碰撞检测方法**。

   ```java
   public class ColliderChain implements Collider {
    private List<Collider> colliders = new LinkedList<>();
    
    public ColliderChain() {
     add(new BulletTankCollider());
     add(new TankTankCollider());
     add(new BulletWallCollider());
     add(new TankWallCollider());
    }
    
    
    public void add(Collider c) {
     colliders.add(c);
    }
   
   
    public boolean collide(GameObject o1, GameObject o2) {
     for(int i=0; i<colliders.size(); i++) {
      if(!colliders.get(i).collide(o1, o2)) {
       return false;
      }
     }
     
     return true;
    }
    
    
   }
   ```

6. 当需要添加坦克和墙碰撞或者子弹与墙(木墙和钢墙)碰撞检测时，只需要实现Collider接口重新定义一个类，并完善类中的碰撞检测方法，最后定义不同物体之间的碰撞检测责任链顺序，**即可完成任意物体之间的碰撞检测，也不需要修改原有代码，只需添加新的类**。

## 6. 装饰器模式

1. 如果我们想在坦克吃了道具后能够发射带尾巴特效的子弹，或者生成带防护罩的坦克，或者说我们希望坦克带一个血条，这时我们可以用到装饰器模式。

   <img src=".\1.png" alt="image-20220705163059518" style="zoom: 50%;" />

2. 实际上就是抽象出一个抽象类，然后继承抽象类实例出一个具体的类，类里面有实现特效子弹和防护罩坦克的方法，而这些方法也是其他已经实现的方法组合而成，许多个方法组和成一个方法，不再需要考虑内部实现细节，只需最终结果生成，在外界看到的就是一个具体生成方法，起到装饰的作用。

## 7. 观察者模式

将我方坦克开火视为一个事件源，其他坦克作为观察者

## 8. 备忘录模式

坦克存盘，并且继续游戏使用到备忘录模式
