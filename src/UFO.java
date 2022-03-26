import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class UFO {

    private static final boolean LEFT = false, RIGHT = true;                                   //magic numbers indicating the direction of movement
    private static Image img;                                                                  //ufo image

    private int x;
    private static final int y = 60, vx = 10, w = 64, h = 28;
    private boolean direc, isDead;                                                             //direction that it's moving in, whether or not it's dead


    /*
        Constructor - This method initializes all the required variables and imports media (images and sounds)
        Parameters : direc - specifies if the ufo is moving right or left
    */
    public UFO(boolean direc) {
        this.direc = direc;

        //if the ufo image has not been imported, import it
        if(img == null) img = new ImageIcon("images/ufo.png").getImage();

        //if moving left, set the x position to the right side of the screen and vice versa
        if(this.direc == LEFT) this.x = GamePanel.WIDTH;
        else this.x = -w;

        this.isDead = false;
    }

    /* This method updates the position of the ufo */
    public void move() {
        if(this.direc == LEFT) this.x -= vx;
        else this.x += vx;
    }

    /* This method draws the ufo graphics if it's not dead */
    public void draw(Graphics g) {

        if(!this.isDead) g.drawImage(img, this.x, y, null);

    }

    /* This method check's if a ship bullet has hit the ufo. If it has, returns the score the player receives from killing the ufo */
    public int checkCollision(ArrayList<Bullet> bullets) {

        //loop through all the ship bullets to check for collisions
        for(Bullet b : bullets) {

            if(!b.getTerminated() && (new Rectangle(this.x, y, w, h)).intersects(b.getRect())) {

                //if a non-terminated bullet hits the ufo, terminate the bullet and kill the ufo
                b.terminate();
                this.isDead = true;

                return GamePanel.random.nextInt(3)*50 + 50;                             //return a score of 50, 100, or 150

            }
        }

        return 0;
    }

}
