/*

    SpaceInvaders.java
    Lily Ni

    This program creates a Space Invaders game.

*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SpaceInvaders extends JFrame {

    GamePanel game;

    public SpaceInvaders() {
        super("Swing eg.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new GamePanel();
        add(game);
        pack();
        setVisible(true);
        setResizable(false);
    }


    public static void main(String []args) {
        SpaceInvaders frame = new SpaceInvaders();

    }

}

class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

    public static final int WIDTH = 1040, HEIGHT = 650;
    public static final boolean PLAYER = false, ENEMY = true;
    public static long curFrame;                                                //frame count (used for cooldowns)
    public static Random random = new Random();

    private static boolean []keys;
    private static boolean click = false;                                       //if mouse is clicked
    private static String gameScene = "menu";                                   //current game scene

    private static int level;                                                   //level number
    private static Point []stars = new Point[100];                              //star positions for background

    //Images
    private static Image logo, heartImg;
    private static Image []nums = new Image[10];
    private static Image playText, scoreText, levelText, winText, loseText, beatGameText, playAgainText, nextLevelText;
    private static Image eImg1, eImg2, eImg3, ufoImg, questionMarkImg;

    //Sound
    private static Sound clickSound;

    //Game timer
    private static Timer timer;

    //Game Objcts
    private Ship ship;
    private EnemyGroup eGroup;
    private Shield []shields = new Shield[4];
    private UFO ufo;
    private static PowerUps powerup = new PowerUps();
    private static Transition transition = new Transition();

    //Mouse position
    private static int mouseX, mouseY;

    //button colours (hover and non-hover)
    private static final Color butCol = new Color(150, 150, 150), butHovCol = new Color(100, 100, 100);

    //UFO cooldown
    private long lastUfoT;
    private final long UFOCoolDown = 500;


    //constructor, start function (do all loading stuff here)
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        //mouse and key interaction
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        keys = new boolean[2000];


        //load all the required images
        logo = new ImageIcon("images/logo.png").getImage().getScaledInstance(427, 179, Image.SCALE_SMOOTH);
        heartImg = new ImageIcon("images/heart.png").getImage();
        playText = new ImageIcon("images/text/playText.png").getImage().getScaledInstance(80, 20, Image.SCALE_SMOOTH);
        scoreText = new ImageIcon("images/text/scoreText.png").getImage().getScaledInstance(108, 20, Image.SCALE_SMOOTH);
        levelText = new ImageIcon("images/text/levelText.png").getImage().getScaledInstance(100, 20, Image.SCALE_SMOOTH);
        winText = new ImageIcon("images/text/winText.png").getImage().getScaledInstance(144, 20, Image.SCALE_SMOOTH);
        loseText = new ImageIcon("images/text/loseText.png").getImage().getScaledInstance(164, 20, Image.SCALE_SMOOTH);
        beatGameText = new ImageIcon("images/text/beatGameText.png").getImage().getScaledInstance(152, 56, Image.SCALE_SMOOTH);
        playAgainText = new ImageIcon("images/text/playAgainText.png").getImage().getScaledInstance(184, 20, Image.SCALE_SMOOTH);
        nextLevelText = new ImageIcon("images/text/nextLevelText.png").getImage().getScaledInstance(192, 20, Image.SCALE_SMOOTH);
        questionMarkImg = new ImageIcon("images/text/questionMark.png").getImage().getScaledInstance(16, 20, Image.SCALE_SMOOTH);

        eImg1 = new ImageIcon("images/A1.png").getImage();
        eImg2 = new ImageIcon("images/B1.png").getImage();
        eImg3 = new ImageIcon("images/C1.png").getImage();
        ufoImg = new ImageIcon("images/ufo.png").getImage();

        for(int i = 0; i < 10; i++) {
            nums[i] = new ImageIcon("images/text/"+i+".png").getImage().getScaledInstance(16, 20, Image.SCALE_SMOOTH);
        }

        //load the required sound
        clickSound = new Sound("sounds/click.wav");

        //generate the star positions for the background
        for(int i = 0; i < stars.length; i++) {
            stars[i] = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        //start new timer
        timer = new Timer(20, this);
        timer.start();

        setFocusable(true);
        requestFocus();

        //set to first level and reset the game
        level = 1;
        resetGame();
    }

    /* This method resets the level */
    private void resetGame() {

        //creates new game objects
        ship = new Ship(keys);
        eGroup = new EnemyGroup(level*20);

        for(int i = 0; i < 4; i ++) shields[i] = new Shield(95+i*238);

        powerup = new PowerUps();

        //resets UFO cooldown
        this.lastUfoT = curFrame;
        ufo = null;

    }

    public static void changeGameScene(String scene) { gameScene = scene; }
    public static void transitionGameScene(String scene) { transition.start(scene); }
    public static PowerUps getPowerUp() { return powerup; }

    /* This method takes in a number and draws the numbers */
    private void drawNum(int num, int x, int y, Graphics g) {
        String s = ""+num; //convert to string

        for(int i = 0; i < s.length(); i++) {
            g.drawImage(nums[s.charAt(i)-'0'], x+i*19, y, null);
        }
    }

    /* This method creates a new button given the position, dimensions and next scene to transition to */
    private void button(int x, int y, int w, int h, String nextScene, Graphics g) {

        Rectangle rect = new Rectangle(x, y, w, h);

        if(rect.contains(mouseX, mouseY)) {

            //change button colour if mouse is hovering over it (and change cursor)
            g.setColor(butHovCol);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            if(click) {                           //if click button

                clickSound.playSound();

                //scene transitions
                if(gameScene.equals("win")) {

                    if(level == 4)  transition.start("beatGame");
                    else {
                        level++;
                        resetGame();
                        transition.start("game");
                    }

                } else if (gameScene.equals("lose")) {
                    resetGame();
                    transition.start("game");

                } else {
                    transition.start(nextScene);
                }

            }

        } else {

            //default cursor and button colour
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            g.setColor(butCol);
        }

        //draw button
        g.fillRect(x, y, w, h);

    }

    //main game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("HELLO");
        //update game
        if(gameScene.equals("game")) gameUpdate();

        //updates graphics of the game
        repaint();

    }

    private void gameUpdate() {

        curFrame++;                                                            //update current frame

        //move the ship and enemies and powerups
        ship.move();
        eGroup.move();
        powerup.update();
        if(powerup.collectPowerUp(ship.getRect())) {
            ship.addLife();
        }

        ship.addScore(eGroup.checkBulletCollision(ship.getBullets()));                                //check if the ship bullets collide with the enemies and add score
        if(eGroup.checkEnemyBullets(ship.getRect()) && !powerup.getShield()) { ship.loseLife(); }     //check if the enemy bullets collide with the ship. If collision and shield powerup is not engaged, decrease life

        //for each shield, check bullet collisions
        for(Shield s : shields) {
            s.checkCollision(eGroup.getEnemyBullets());
            s.checkCollision(ship.getBullets());
        }


        //generate new ufo if cooldown is done, reset cooldown
        if(curFrame - this.lastUfoT >= this.UFOCoolDown) {
            this.lastUfoT = curFrame;
            ufo = new UFO(random.nextBoolean());
        }

        //move and check ufo collisions if it exists
        if(ufo != null) {
            ufo.move();
            ship.addScore(ufo.checkCollision(ship.getBullets()));                              //check if the ship bullets collide with the ufo and add score
        }

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }


    /* This method draws all the graphics of the game */
    @Override
    public void paint(Graphics g) {

        //if the game is not transitioning, draw the background (black rectangle with white dots)
        if(!gameScene.equals("transition")) {

            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            //stars
            g.setColor(Color.white);
            for (Point i : stars) {
                g.fillOval(i.x, i.y, 2, 2);
            }
        }

        //draw graphics depending on which scene it is
        if(gameScene.equals("game")) gameScreen(g);
        else if (gameScene.equals("menu")) menuScreen(g);
        else if (gameScene.equals("lose")) loseScreen(g);
        else if (gameScene.equals("win")) winScreen(g);
        else if (gameScene.equals("beatGame")) beatScreen(g);

        //draw transitions over top
        transition.draw(g);


        //reset click
        click = false;

    }

    /* GRAPHICS FOR EACH SCENE */
    private void gameScreen(Graphics g) {

        //score text
        g.drawImage(scoreText, 20, 20, null);
        drawNum(ship.getScore(), 150, 20, g);

        //level text
        g.drawImage(levelText, 300, 20, null);
        drawNum(level, 420, 20, g);

        //lives
        for(int i = 0; i < ship.getLives(); i++) {
            g.drawImage(heartImg, 890 + i*45, 20, null);
        }

        eGroup.draw(g);                                                   //draw enemies
        ship.draw(g);                                                     //draw ship

        for(int i = 0; i < 4; i++) { shields[i].draw(g); }                //draw shields
        if(ufo != null) ufo.draw(g);                                      //draw ufo

        //draw powerup
        powerup.draw(g);

    }

    private void menuScreen(Graphics g) {

        //draw logo
        g.drawImage(logo, (WIDTH-427)/2, 120, null);

        //draw play button
        button(WIDTH/2-40-10, 550-10, 100, 40, "game", g);
        g.drawImage(playText, WIDTH/2-40, 550, null);

        //draw enemies
        g.drawImage(eImg1, WIDTH/2-16-30, 340, null);
        g.drawImage(eImg2, WIDTH/2-20-30, 385, null);
        g.drawImage(eImg3, WIDTH/2-24-30, 430, null);
        g.drawImage(ufoImg, WIDTH/2-32-30, 475, null);

        //draw points
        drawNum(10, WIDTH/2+30, 345, g);
        drawNum(20, WIDTH/2+30, 390, g);
        drawNum(30, WIDTH/2+30, 435, g);
        g.drawImage(questionMarkImg, WIDTH/2+30, 480, null);
    }

    private void winScreen(Graphics g) {

        if(level == 3) {

            //if all levels are beat, change to beatGame scene
            gameScene = "beatGame";
            beatScreen(g);

        } else {

            //draw message and nextLevel button
            g.drawImage(winText, WIDTH/2-72, HEIGHT/2-30, null);
            button(WIDTH/2-96-10, HEIGHT/2-10+30, 212, 40, "game", g);
            g.drawImage(nextLevelText, WIDTH/2-96, HEIGHT/2+30, null);
        }

    }

    private void loseScreen(Graphics g) {

        //draw messgae and retry button
        g.drawImage(loseText, WIDTH/2-82, HEIGHT/2-30, null);
        button(WIDTH/2-92-10, HEIGHT/2-10+30, 204, 40, "game", g);
        g.drawImage(playAgainText, WIDTH/2-92, HEIGHT/2+30, null);
    }

    private void beatScreen(Graphics g) {

        //draw message
        g.drawImage(beatGameText, WIDTH/2-76, HEIGHT/2-28, null);

    }



    /* MOUSE AND KEYBOARD INPUT */
    @Override
    public void mousePressed(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){ click = true; }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    public void mouseMoved(MouseEvent e) {

        //update mouse position whenever it moves
        mouseX = e.getPoint().x;
        mouseY = e.getPoint().y;
    }
    public void mouseDragged(MouseEvent e) {}

    public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
    public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
    public void keyTyped(KeyEvent e) {}

}
