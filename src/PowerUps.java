import java.util.Arrays;
import java.awt.*;
import javax.swing.*;

class PowerUps {

    private static final int RAPIDFIRE = 0, EXTRALIFE = 1, SHIELD = 2;                          //magic numbers to specify each powerup
    private static boolean isTerminated, collected;                                             //if the powerup is terminated, if the powerup is collected
    private static boolean[] powerups = new boolean[3];                                         //stores which powerup is engaged (only one powerup can be engaged at a time)

    private static long powerUpT, powerUpCoolDownT, powerUpCoolDownLimit;                       //powerup time, cooldown before generating a new powerup
    private static final long powerUpLimit = 80;                                                //how long the effects of a powerup lasts

    private static final int vy = 10, w = 31, h = 31;

    private static Image[] powerUpImgs = new Image[3];                                          //powerup images
    private static Sound powerUpSound;                                                          //powerup collection sound

    private int x, y, curType;                                                                  //curType = current powerup type


    /* This method initializes all the required variables and imports media (images and sounds) */
    public PowerUps() {

        //none of the powerups are engaged at the start
        Arrays.fill(powerups, false);

        isTerminated = true;
        collected = false;

        //if the images and/or sounds are null, import the required media
        if (powerUpImgs[RAPIDFIRE] == null) powerUpImgs[RAPIDFIRE] = new ImageIcon("images/rapidFirePowerUp.png").getImage();
        if (powerUpImgs[EXTRALIFE] == null) powerUpImgs[EXTRALIFE] = new ImageIcon("images/extraLifePowerUp.png").getImage();
        if(powerUpImgs[SHIELD] == null) powerUpImgs[SHIELD] = new ImageIcon("images/shieldPowerUp.png").getImage();
        if(powerUpSound == null) powerUpSound = new Sound("sounds/powerUp.wav");

        //reset cooldowns
        powerUpCoolDownT = GamePanel.curFrame;
        powerUpCoolDownLimit = GamePanel.random.nextInt(100) + 200;                       //cooldown to generate new powerup is randomized

    }


    /* This method kills the powerup (called when powerup is used up or leaves the game screen uncollected) */
    private void kill() {

        //reset powerup variables
        powerups[this.curType] = false;
        collected = false;
        isTerminated = true;

        //reset cooldown to generate new powerup
        powerUpCoolDownT = GamePanel.curFrame;
        powerUpCoolDownLimit = GamePanel.random.nextInt(100) + 200;

    }

    /*
        This method checks for powerup collection. If the powerup is collected and it's an extra life, return true. Otherwise, return false
        Parameters : ship - ship rectangle (used for collision detection)
    */
    public boolean collectPowerUp(Rectangle ship) {
        if (!isTerminated && !collected) {                                                      //if the powerup is not terminated and not collected, check for intersection with the ship

            if (ship.intersects(new Rectangle(this.x, this.y, w, h))) {                         //if the ship collides with the powerup

                powerUpSound.playSound();                                                       //play powerup collection sound

                if (this.curType == EXTRALIFE) {

                    //if the powerup is an extraLife, kill immediately and return true
                    kill();
                    return true;

                } else {

                    //indicate powerup collected, engage powerup and start powerup timer
                    collected = true;
                    powerUpT = GamePanel.curFrame;
                    powerups[this.curType] = true;

                }

            }

        }

        return false;
    }

    /* This method generates a new powerup */
    private void newPowerUp() {

        this.x = GamePanel.random.nextInt(GamePanel.WIDTH - 40) + 20;              //random horizontal position to generate the powerup
        this.y = -20;

        //powerup is not terminated and not collected
        isTerminated = false;
        collected = false;

        this.curType = GamePanel.random.nextInt(3);                                //randomly generate the type of powerup (RAPIDFIRE, EXTRALIFE or SHIELD)

    }

    /* This method updates powerup cooldowns and positions */
    public void update() {

        if (isTerminated) {
            if (GamePanel.curFrame - powerUpCoolDownT >= powerUpCoolDownLimit) { newPowerUp(); }      //if powerup is terminated and the cooldown is finished, generate a new powerup

        } else if (!collected && this.y >= GamePanel.HEIGHT + 50) kill();                             //if powerup is off the screen, kill it

        if (!isTerminated && collected && GamePanel.curFrame - powerUpT >= powerUpLimit) kill();      //if the time runs out for a powerup that's been engaged, kill it

        this.y += vy;

    }

    /* This method draws the graphics for the powerup */
    public void draw(Graphics g) {

        //if the powerup is not terminated and not collected, draw the current powerup
        if (!collected && !isTerminated) {
            g.drawImage(powerUpImgs[this.curType], this.x, this.y, null);
        }

    }

    /* Returns if the rapidFire powerup is engaged */
    public boolean getRapidFire() { return powerups[RAPIDFIRE]; }

    /* Returns if the shield powerup is engaged */
    public boolean getShield() { return powerups[SHIELD]; }

}