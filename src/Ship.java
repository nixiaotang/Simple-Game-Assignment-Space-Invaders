import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;

public class Ship {

    private static final int w = 60, h = 40, vx = 10;
    private int x, y, lives, score;
    private boolean []keys;                                                                    //array to keep track of pressed keys
    private static Image ship, rapidFireShip, shieldShip, deadShip;                            //images of different ship states (default, rapidfire powerup, shield powerup, dead ship)

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();                                    //ArrayList to keep track of all bullets shot from the ship
    private long lastShootT, shootTime;                                                        //frame of the last bullet shot, the cool down time
    private static final long shootCoolDown = 20, rapidFireCoolDown = 3;                       //cool downs for normal shooting and rapidfire powerup shooting

    private static Sound shootSound, explosionSound;                                           //shooting and explosion (when ship gets hit) sounds


    /*
        Constructor - This method initializes all the required variables and imports media (images and sounds)
        Parameters : keys - array of keys that indicates which keys are being pressed
    */
    public Ship(boolean []keys) {

        //set the starting x and y positions of the ship
        this.x = GamePanel.WIDTH/2 - w/2;
        this.y = GamePanel.HEIGHT-h-20;

        //if the images are null, import the correct images
        if(ship == null) ship = new ImageIcon("images/ship.png").getImage();
        if(deadShip == null) deadShip = new ImageIcon("images/deadShip.png").getImage();
        if(rapidFireShip == null) rapidFireShip = new ImageIcon("images/rapidFireShip.png").getImage();
        if(shieldShip == null) shieldShip = new ImageIcon("images/shieldShip.png").getImage();
        if(shootSound == null) shootSound = new Sound("sounds/shootSound.wav");
        if(explosionSound == null) explosionSound = new Sound("sounds/explosion.wav");

        this.lives = 3;
        this.score = 0;
        this.keys = keys;

        //sets the frame of the last bullet shot to the current frame (resets the shooting cool down)
        this.lastShootT = GamePanel.curFrame;

    }


    /* This method draws all the graphics (ship and bullets) and handles the scene change if there are no lives left */
    public void draw(Graphics g) {

        //draw the ship
        if(this.lives != 0) {

            //if the ship is not dead, draw the ship image depending on which powerup is engaged (if any)
            if (GamePanel.getPowerUp().getRapidFire()) g.drawImage(rapidFireShip, this.x, this.y, null);
            else if (GamePanel.getPowerUp().getShield()) g.drawImage(shieldShip, this.x, this.y, null);
            else g.drawImage(ship, this.x, this.y, null);

        } else {

            //if the ships is dead, draw the deadShip image and transition to the lose scene
            g.drawImage(deadShip, this.x, this.y, null);
            GamePanel.transitionGameScene("lose");

        }

        //draw the bullets
        for (Bullet i : bullets) i.draw(g);
    }

    /* This method performs controls all the updates for the ship and its bullets (movement, shooting cooldown, bullet termination) */
    public void move() {

        //ship movement left/right with arrow keys or A and D
        if(this.keys[KeyEvent.VK_RIGHT] || this.keys[KeyEvent.VK_D]) this.x = Math.min(this.x + vx, GamePanel.WIDTH - w - 20);
        else if(this.keys[KeyEvent.VK_LEFT] || this.keys[KeyEvent.VK_A]) this.x = Math.max(this.x - vx, 20);

        if(GamePanel.getPowerUp().getRapidFire()) this.shootTime = rapidFireCoolDown;                  //if the rapidFire powerup is engaged, change the shooting cooldown time to the faster cool down
        else this.shootTime = shootCoolDown;                                                           //otherwise, use the normal cooldown time

        //check if SPACE keys is pressed and cool down is done for shooting
        if(this.keys[KeyEvent.VK_SPACE] && GamePanel.curFrame - this.lastShootT >= shootTime) {
            this.shoot();
            this.lastShootT = GamePanel.curFrame;                                                      //reset shooting cooldown
        }

        //updating the bullets
        for(int i = bullets.size()-1; i >= 0; i--) {

            //if the bullet is off the screen or is terminated, remove the bullets from the list
            if(bullets.get(i).getY() < -200 || bullets.get(i).getTerminated()) {
                bullets.remove(bullets.get(i));
            }
            else bullets.get(i).move();                                                                //otherwise, update the position of the bullet
        }

    }

    /* This method creates a new bullet */
    private void shoot() {
        bullets.add(new Bullet(this.x + this.w/2 - Bullet.w/2, this.y-10, GamePanel.PLAYER));        //make a new bullet and add it to the bullet ArrayList
        shootSound.playSound();                                                                            //play shooting sound effect
    }

    /* Returns a Rectangle with the position and dimensions of the ship (for collisions) */
    public Rectangle getRect() { return new Rectangle(this.x, this.y, w, h); }

    /* Returns the ArrayList of non-terminated bullets (for collisions) */
    public ArrayList<Bullet> getBullets() { return bullets; }

    /* Adds to the score (called when a bullet kills an enemy) */
    public void addScore(int score) { this.score += score; }

    /* Returns the score (used for graphic display) */
    public int getScore() { return this.score; }

    /* Returns the number of lives (used for graphic display) */
    public int getLives() { return this.lives; }

    /* Decreases the lives by one (called when an enemy bullet hits the ship) */
    public void loseLife() {
        this.lives--;
        explosionSound.playSound();                                         //play explosion sound effect
    }

    /* Increases the lives by one (called when the ship collects an extraLife powerup) */
    public void addLife() { this.lives = Math.min(this.lives+1, 3); }

}
