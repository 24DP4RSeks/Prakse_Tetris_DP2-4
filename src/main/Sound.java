package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public class Sound {

    Clip musicClip;
    URL url[] = new URL[10];

    public Sound() {

        url[0] = getClass().getResource("/res/gameOver_sound.wav");
        url[1] = getClass().getResource("/res/lines_sound.wav");
        url[2] = getClass().getResource("/res/gameOver_sound.wav");
        url[3] = getClass().getResource("/res/rotation_sound.wav");
        url[4] = getClass().getResource("/res/blockHit_sound.wav");
        
        // Music themes (indices 5, 6, 7)
        // Reusing gameOver_sound.wav as placeholder - replace with your actual theme files
        url[5] = getClass().getResource("/res/tetris.wav");  // Theme 1
        url[6] = getClass().getResource("/res/tetris1.wav");  // Theme 2
        url[7] = getClass().getResource("/res/tetris2.wav");  // Theme 3
        url[8] = getClass().getResource("/res/tetrisDub.wav");  // Theme 3
        
    }
    public void play(int i, boolean music) {

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();

            if(music) {
                musicClip = clip;
            }

            clip.open(ais);
            clip.addLineListener(new LineListener(){
                @Override
                public void update(LineEvent event) {
                    if(event.getType() == Type.STOP) {
                        clip.close();
                    }
                }
                
            });
            ais.close();
            clip.start();
            
        }catch(Exception e) {

        }
    }
    public void loop(){
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        musicClip.stop();
        musicClip.close();
    }
}
