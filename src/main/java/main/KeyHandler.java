package main.java.main;



import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public static boolean spacePressed, leftPressed, rightPressed, pausePressed;

    public static boolean upPressed;
    public static boolean downPressed;
    public static boolean menuPressed;
    public static boolean fullscreenPressed;
    public static boolean restartPressed;
    public static boolean ePressed;
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode();
        
        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W){
            upPressed = true;
        }
        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S){
            downPressed = true;
        }

        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER){
            spacePressed = true;
        }
        if(code == KeyEvent.VK_E){
            ePressed = true;
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
        if(code == KeyEvent.VK_ESCAPE){
            if(pausePressed) {
                pausePressed = false;
            }
            else {
                pausePressed = true;
            }
        }
        
        if(code == KeyEvent.VK_F){
            fullscreenPressed = true;
        }
        if(code == KeyEvent.VK_R){
            restartPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER){
            spacePressed = false;
        }
        if(code == KeyEvent.VK_E){
            ePressed = false;
        }
    }

}
