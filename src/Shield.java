import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Shield {

    /*
        This class creates a shield. The shield is created with a 4x3 array of blocks.
        Depending on which block collides with a bullet, the health of that block will decrease.
    */


    private static Image [][]imgs;                               //images for each block (imgs[blockType][health])

    //indicates the type for each block
    //-1 = empty, 0 - full block, 1 - left outer corner, 2 - right outer corner, 3 - left inner corner, 4 - right inner corner
    private static final int [][]blockType = {
            {1, 0, 0, 2},
            {0, 3, 4, 0},
            {0, -1, -1, 0},
    };

    //health for each block (max health = 4)
    private int [][]health = {
            {4, 4, 4, 4},
            {4, 4, 4, 4},
            {4, 0, 0, 4}
    };


    private int x;                                                           //x position of the shield
    private static final int y = 510, blockW = 24;                           //y position of the shield, width of a block


    /*
        Constructor - This method initializes all the required variables and imports media (images and sounds)
        Parameters : keys - array of keys that indicates which keys are being pressed
    */
    public Shield(int x) {

        this.x = x;

        //if the images are not imported, import them
        if(imgs == null) {
            imgs = new Image[][] {
                    {
                            new ImageIcon("images/shield/A1 (-).png").getImage(),
                            new ImageIcon("images/shield/A2 (-).png").getImage(),
                            new ImageIcon("images/shield/A3 (-).png").getImage(),
                            new ImageIcon("images/shield/A4 (-).png").getImage()
                    },
                    {
                            new ImageIcon("images/shield/B1 (l).png").getImage(),
                            new ImageIcon("images/shield/B2 (l).png").getImage(),
                            new ImageIcon("images/shield/B3 (l).png").getImage(),
                            new ImageIcon("images/shield/B4 (l).png").getImage()
                    },
                    {
                            new ImageIcon("images/shield/B1 (r).png").getImage(),
                            new ImageIcon("images/shield/B2 (r).png").getImage(),
                            new ImageIcon("images/shield/B3 (r).png").getImage(),
                            new ImageIcon("images/shield/B4 (r).png").getImage()
                    },
                    {
                            new ImageIcon("images/shield/C1 (l).png").getImage(),
                            new ImageIcon("images/shield/C2 (l).png").getImage(),
                            new ImageIcon("images/shield/C3 (l).png").getImage(),
                            new ImageIcon("images/shield/C4 (l).png").getImage()
                    },
                    {
                            new ImageIcon("images/shield/C1 (r).png").getImage(),
                            new ImageIcon("images/shield/C2 (r).png").getImage(),
                            new ImageIcon("images/shield/C3 (r).png").getImage(),
                            new ImageIcon("images/shield/C4 (r).png").getImage()
                    },
            };
        }

    }

    /* This method draws the graphics of the shield */
    public void draw(Graphics g) {

        //draws each block given the correct block type and health of the block
        //if the current health of the block is 0, then don't draw it
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {

                if(this.health[i][j] > 0) g.drawImage(imgs[blockType[i][j]][4-this.health[i][j]], this.x + 24*j, y + 24*i, null);

            }
        }

    }

    /* checks if the bullets hit the shield */
    public void checkCollision(ArrayList<Bullet> bullets) {

        //loop through each bullet to check
        for(Bullet bullet : bullets) {

            if(!bullet.getTerminated()) {

                //if the current bullet is not terminated, then loop through each block of the shield to check for collision
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 4; j++) {

                        if(this.health[i][j] > 0 && bullet.getRect().intersects(new Rectangle(this.x + 24*j, y + 24*i, blockW, blockW))) {

                            //if a non-dead block collides with a bullet, decrease the health of the block and terminate the bullet
                            this.health[i][j]--;
                            bullet.terminate();

                        }

                    }
                }
            }

        }

    }

}
