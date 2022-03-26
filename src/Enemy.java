import java.awt.*;

public class Enemy {

    private static final int ALIVE = 0, DYING = 1, DEAD = 2;                      //magic numbers for the state of the enemy

    private int x, y, w, h, type, colNum, rowNum, relX, relY, imgNum;             //enemy info (positions, types, current animation image...etc)
    private int state;                                                            //state of enemy (ALIVE, DYING or DEAD)
    private Image []imgs;                                                         //alive images of the enemy (animation)
    private static Image deathImg;                                                //dying image of enemy

    private long deathTime;                                                       //dying cooldown
    private static final long dyingCoolDown = 4;                                  //dying cooldown time limit

    /*
        Constructor - This method initializes all the required variables and imports images
        Parameters : x, y = position, w = width, type = type of enemy, colNum, rowNum = column and row of the enemy, imgs = animation images, deadImg = dying enemy image
    */
    public Enemy(int x, int y, int w, int type, int colNum, int rowNum, Image []imgs, Image deadImg) {
        this.relX = x;
        this.relY = y;
        this.type = type;
        this.colNum = colNum;
        this.rowNum = rowNum;

        this.w = w;
        this.h = 30;

        this.x = this.relX;
        this.y = this.relY;
        this.state = ALIVE;

        this.imgs = imgs;
        deathImg = deadImg;
    }

    /* This method updates the movement of the enemy */
    public void move(int sx, int sy, int imgNum) {

        this.x = sx + this.relX;
        this.y = sy + this.relY;
        this.imgNum = imgNum;         //updates the animation image of the enemy

    }

    /* This method draws the enemy */
    public void draw(Graphics g) {

        if(this.state == ALIVE) {                                                    //draw the enemy if it's alive
            g.drawImage(this.imgs[this.imgNum], this.x, this.y, null);

        } else if (this.state == DYING) {                                           //if the enemy is dying, draw the dying image

            g.drawImage(deathImg, this.x, this.y, null);

            //if the dying cooldown ended, the enemy is fully dead
            if(GamePanel.curFrame - this.deathTime >= dyingCoolDown) { this.state = DEAD; }

        }

    }

    /* This method generates a new enemy bullet */
    public Bullet shoot() {
        return new Bullet(this.x + this.w/2, this.y + this.h/2, GamePanel.ENEMY);
    }

    /* Returns a rectangle with the position and dimensions of the enemy (for collisions) */
    public Rectangle getRect() {
        return new Rectangle(this.x, this.y, this.w, this.h);
    }

    /* These methods return the state, type, column number and row number of the enemy */
    public int getState() { return this.state; }
    public int getType() { return this.type; }
    public int getColNum() { return this.colNum; }
    public int getRowNum() { return this.rowNum; }

    /* This method kills the enemy (start dying cool down) */
    public void kill() {
        this.state = DYING;
        this.deathTime = GamePanel.curFrame;
    }
}
