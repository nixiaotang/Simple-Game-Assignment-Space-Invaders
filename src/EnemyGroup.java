import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EnemyGroup {

    private static final boolean LEFT = false, RIGHT = true;                    //magic numbers to indicate the LEFT and RIGHT directions
    private static final int ALIVE = 0;                                         //magic number for if the enemy is alive
    public static final int A = 0, B = 1, C = 2;                                //magic numbers to indicate the type of enemy
    private static final int []enemyPts = {30, 20, 10};                         //points that each type of enemy will give if killed
    private static Image[][]enemyImgs;                                          //images for each enemy
    private static Image deathEnemyImg;                                         //dying enemy image

    private int []enemyCountCol = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};            //number of alive enemies at each column
    private int []enemyCountRow = {11, 11, 11, 11, 11};                         //number of alive enemies at each row
    private int imgNum;                                                         //keeps track of which enemy image is currently being used (for animation)

    private int x, y, vx, vy;                                                   //x,y position to offset the enemies to simulate movement, vx and vy are the speeds
    private int minCol, maxCol;                                                 //min and max columns with alive enemies
    private int minX, maxX;                                                     //min and max x values for enemy offset
                                                                                //  - will change when the leftmost or rightmost column of enemies all die
                                                                                //  - if the leftmost column of enemies die, the enemy group will move more to the left before it turns, vice versa for the right
    private int lowestRow;                                                      //lowest row with alive enemies
    private boolean direc;                                                      //direction that enemies are moving

    private Enemy []enemies = new Enemy[55];                                    //enemies array
    private ArrayList<Bullet> enemyBullets = new ArrayList<Bullet>();           //ArrayList of all the enemy bullets

    private static long lastMoveT, lastShootT;                                  //frame where the enemies last moved, frame where the enemies last shot a bullet
    private static final long moveCoolDown = 10, shootCoolDown = 10;            //cool down times for moving and shooting


    /*
        Constructor - This method initializes all the required variables and imports images
        Parameters : vx - speed of horizontal movement, dictated by the current game level
    */
    public EnemyGroup(int vx) {

        //how much to offset the group of enemies by (simulate moving)
        this.x = 0;
        this.y = 0;

        //marks how far left and right the group of enemies move
        //(this may change if the column of enemies closest to the edge all dies)
        this.minX = 0;
        this.maxX = 340;
        this.minCol = 0;
        this.maxCol = 10;

        //velocity
        this.vx = vx;                                 //horizontal speed is controlled by the level number (higher level = faster = more difficult)
        this.vy = 20;

        this.lowestRow = 4;                           //all enemies are alive, lowest row = 4 (rows are from 0-4)

        this.direc = RIGHT;                           //direction that the enemies are currently moving towards

        this.imgNum = 0;                              //image number for enemy graphics (either 0 or 1)

        //if the images are null, ipmort them
        if(enemyImgs == null) {
            enemyImgs = new Image[][] {
                    { new ImageIcon("images/A1.png").getImage(), new ImageIcon("images/A2.png").getImage() },
                    { new ImageIcon("images/B1.png").getImage(), new ImageIcon("images/B2.png").getImage() },
                    { new ImageIcon("images/C1.png").getImage(), new ImageIcon("images/C2.png").getImage() },
            };
        }
        if(deathEnemyImg == null) deathEnemyImg = new ImageIcon("images/deadEnemy.png").getImage();

        //reset moving and shooting cooldowns
        lastMoveT = GamePanel.curFrame;
        lastShootT = GamePanel.curFrame;


        int[] enemyTypes = {A, B, B, C, C};
        int []enemyWidths = {32, 40, 48};

        //generates all the enemies
        for(int i = 0; i < 11; i++) {
            for(int j = 0; j < 5; j++) {

                enemies[j*11+i] = new Enemy(
                        50-enemyWidths[enemyTypes[j]]/2+i*60,
                        110+j*45, enemyWidths[enemyTypes[j]],
                        enemyTypes[j],
                        i,
                        j,
                        enemyImgs[enemyTypes[j]],
                        deathEnemyImg
                );

            }
        }


    }

    /* This method returns whether or not the enemies have hit a wall and should move down */
    private boolean checkMoveDown() {

        //return true if moving right and enemies hit right wall or moving left and enemies hit left wall
        return (this.direc == RIGHT && this.x >= this.maxX) || (this.direc == LEFT && this.x <= this.minX);

    }

    /* This method updates the cooldowns and positions of the enemies and enemy bullets */
    public void move() {

        //update each enemy bullet
        for(int i = enemyBullets.size()-1; i >= 0; i--) {

            //remove the bullets that are either off the screen or are already terminated
            if(enemyBullets.get(i).getY() > GamePanel.HEIGHT+200 || enemyBullets.get(i).getTerminated()) {
                enemyBullets.remove(enemyBullets.get(i));
            }
            else enemyBullets.get(i).move();                                                          //otherwise, update the position of the bullet

        }

        //if the shooting cooldown is finished, make a new shot and reset the cooldown
        if(GamePanel.curFrame - lastShootT >= shootCoolDown) {
            enemyShoot();
            lastShootT = GamePanel.curFrame;
        }

        //if the movement cooldown is finished
        if(GamePanel.curFrame - lastMoveT >= moveCoolDown) {

            lastMoveT = GamePanel.curFrame;                    //reset the movement cooldown

            if (checkMoveDown()) {                             //check if the enemies hit a wall and should move down

                //move down and change direction
                this.y += this.vy;
                this.direc = !direc;
                this.vx *= -1;

                //if the enemies are too far down the screen (almost overlapping with the ship), transition to the lose scene
                if(this.y >= 420-this.lowestRow*40) GamePanel.transitionGameScene("lose");

            } else this.x += this.vx;                          //update horizontal position

            //update and move the positions of the individual enemies
            for (Enemy i : enemies) {
                i.move(this.x, this.y, this.imgNum);
            }

            this.imgNum = (this.imgNum + 1) % 2;               //change enemy image

        }

    }


    /* This method generates a new enemy bullet */
    private void enemyShoot() {

        int shootEnemy = GamePanel.random.nextInt(55);       //randomly picks an enemy to shoot from

        //continuously picks a new enemy to shoot from until the selected enemy is alive
        while(enemies[shootEnemy].getState() != ALIVE) {
            shootEnemy = GamePanel.random.nextInt(55);
        }

        //add a new enemy bullet and shoot from the selected enemy
        enemyBullets.add(enemies[shootEnemy].shoot());
    }

    /* This method draws the enemy and bullets */
    public void draw(Graphics g) {

        //draw the individual enemies
        for(Enemy i : enemies) { i.draw(g); }

        //draw the enemy bullets
        for(Bullet i : enemyBullets) { i.draw(g); }

    }

    /* This method checks for ship bullet collisions with the enemies and returns the score the player recieves if an enemy is killed */
    public int checkBulletCollision(ArrayList<Bullet> bullets) {

        int totScoreIncrease = 0;                                                 //score increase if ship kills an enemy

        //loop through each enemy and bullet to see if they collide
        for(Enemy e : enemies) {

            if(e.getState() != ALIVE) continue;                                   //if the current enemy is already dead, ignore it

            //loop through each ship bullet
            for(Bullet b : bullets) {

                //if the bullet hasn't been terminated and it collides with the enemy
                if(!b.getTerminated() && e.getRect().intersects(b.getRect())) {

                    totScoreIncrease = enemyPts[e.getType()];                      //get the number of points received from killing the enemy

                    //decrease the row and column count of the killed enemy
                    int eCol = e.getColNum(), eRow = e.getRowNum();
                    this.enemyCountCol[eCol]--;
                    this.enemyCountRow[eRow]--;

                    if(this.enemyCountRow[eRow] == 0) this.lowestRow--;            //update the lowest row with alive enemies


                    //if the entire column of enemies are dead, update how far right and left the groups of enemies should move
                    if(this.enemyCountCol[eCol] == 0) {

                        if(eCol == this.minCol) {               //shifts this.minCol up until it finds a column with enemies still alive

                            while(this.minCol <= this.maxCol) {
                                if(this.enemyCountCol[eCol] == 0) {
                                    this.minCol++;
                                    this.minX -= 60;
                                    eCol++;

                                } else break;
                            }

                        } else if (eCol == this.maxCol) {      //shifts this.maxCol down until it finds a column with enemies still alive

                            while(this.minCol <= this.maxCol) {
                                if(this.enemyCountCol[eCol] == 0) {
                                    this.maxCol--;
                                    this.maxX += 60;
                                    eCol--;

                                } else break;
                            }
                        }

                    }

                    //terminate the bullet and kill the enemy
                    e.kill();
                    b.terminate();

                    //if there are no more enemies alive, transition to the win screen
                    if(this.minCol > this.maxCol) {
                        GamePanel.transitionGameScene("win");
                        return totScoreIncrease;
                    }

                    break;

                }

            }
        }


        return totScoreIncrease;       //score increase if an enemy is killed

    }

    /* Returns whether or not an enemy bullet collides with the ship */
    public boolean checkEnemyBullets(Rectangle player) {

        //loop through all the enemy bullets
        for(Bullet b : enemyBullets) {
            if (!b.getTerminated() && player.intersects(b.getRect())) {

                //if the bullet is not terminated and collides with the player, terminate the bullet and return true
                b.terminate();
                return true;
            }
        }

        return false;            //not collided
    }

    /* Returns the ArrayList of enemy bullets */
    public ArrayList<Bullet> getEnemyBullets() { return enemyBullets; }

}
