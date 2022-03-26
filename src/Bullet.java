import java.awt.*;

public class Bullet {

    public static final int w = 2, h = 15, vy = 20;
    private int x, y, direc;
    private boolean isTerminated = false, type;                   //if bullet is terminated, if bullet is player or enemy bullet
    private Color col;                                            //colour of the bullet


    /*
        Constructor - This method initializes all the required variables
        Parameters : x, y = position, type = player or enemy bullet
    */
    public Bullet(int x, int y, boolean type) {
        this.x = x;
        this.y = y;

        this.type = type;

        if(this.type == GamePanel.ENEMY) {            //if the bullet is an enemy bullet, direction should move down, colour should be white
            this.direc = 1;
            this.col = Color.white;

        } else {                                      //if the bullet is a player bullet, should move up

            this.direc = -1;

            //If the bullet is fired during rapid fire, generate a random colour for it. Otherwise, make the colour red
            if (GamePanel.getPowerUp().getRapidFire()) {
                this.col = new Color(GamePanel.random.nextInt(155)+100, GamePanel.random.nextInt(155)+100, GamePanel.random.nextInt(155)+100);

            } else this.col = Color.red;

        }

    }

    /* This method updates the position of the bullet */
    public void move() {
        this.y += vy * this.direc;
    }

    /* This method draws the bullet if it's not terminated */
    public void draw(Graphics g) {

        if(!this.isTerminated) {
            g.setColor(this.col);
            g.fillRect(this.x, this.y, w, h);
        }
    }

    /* Returns a new Rectangle with the position and dimensions of the bullet */
    public Rectangle getRect() { return new Rectangle(this.x, this.y, w, h); }

    /* Returns the y position of the bullet */
    public int getY() { return this.y; }

    /* Terminates the bullet */
    public void terminate() { this.isTerminated = true; }

    /* Returns whether or not the bullet is terminated */
    public boolean getTerminated() { return this.isTerminated; }

}
