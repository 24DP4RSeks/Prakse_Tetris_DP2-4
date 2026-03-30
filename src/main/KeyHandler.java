package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public static boolean spacePressed, downPressed, leftPressed, rightPressed, pausePressed;
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_SPACE){
            spacePressed = true;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = true;
        }
        if(code == KeyEvent.VK_S){
            downPressed = true;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
        if(code == KeyEvent.VK_1){
            if(pausePressed) {
                pausePressed = false;

            }
            else {
                pausePressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}
