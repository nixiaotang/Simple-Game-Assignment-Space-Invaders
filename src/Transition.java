import java.awt.*;

public class Transition {

    /*
        This class creates a transition by drawing a black rectangle over the entire scene that increases in opacity.
        At full opacity, the scene is changed and the opacity is decreased to reveal the new scene.
    */


    private static final boolean IN = false, OUT = true;                 //magic numbers to specify if the transition is going in or out (increase or decrease opacity)
    private static boolean state, transition;                            //state of transition (IN or OUT), whether or not a a transition is taking place
    private static float opacity;                                        //opacity of the black rectangle overlay
    private static String scene;                                         //scene to transition to

    public Transition() {

    }


    /*
        This method starts a new transition and resets the transition variables
        Parameters : nextScene - scene to transition into
    */
    public void start(String nextScene) {
        opacity = 20;                                                    //starting opacity
        scene = nextScene;
        GamePanel.changeGameScene("transition");                         //transition scene - don't update other game scenes (will freeze game if transitioning out of gameScene)
        state = IN;                                                      //transition going IN
        transition = true;                                               //specifies a transition is taking place
    }

    /* This method draws the transition overlay and updates the transition settings */
    public void draw(Graphics g) {

        opacity = Math.max(Math.min(opacity, 255), 0);                   //limits opacity to be within range (0-255)

        //creates a new black colour with the current opacity and draw a rectangle over the entire screen
        g.setColor(new Color(0, 0, 0, (int)opacity));
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        //if a current transition is taking place
        if(transition) {

            if(state == IN) {
                opacity *= 1.4;                                          //if the transition is going in, increase the opacity

                if(opacity >= 255) {

                    //if the opacity reaches the limit, change the scene and transition out
                    GamePanel.changeGameScene(scene);
                    state = OUT;

                }

            } else {
                opacity /= 1.4;                                          //if the transition is going out, decrease the opacity

                if(opacity <= 0) transition = false;                     //if the opacity reaches the limit, transition is finished

            }


        }

    }


}
