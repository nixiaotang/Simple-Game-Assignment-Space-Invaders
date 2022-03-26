import java.applet.*;
import java.io.*;

class Sound {

    File wavFile;                      //sound file
    AudioClip sound;                   //audio

    /*
        Constructor - This method loads the correct sound file
        Parameters : path - file path to the sound file
    */
    public Sound(String path) {
        wavFile = new File(path);                                 //initialize the file with the correct sound file

        //convert file to audio clip
        try{
            sound = Applet.newAudioClip(wavFile.toURL());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /* This method plays the sound effect */
    public void playSound() { sound.play(); }

}
