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
    
    public static char lastTypedChar = Character.MIN_VALUE;

    @Override
    public void keyTyped(KeyEvent e) {
        lastTypedChar = e.getKeyChar();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP) upPressed = true;
        if(code == KeyEvent.VK_DOWN) downPressed = true;
        if(code == KeyEvent.VK_LEFT) leftPressed = true;
        if(code == KeyEvent.VK_RIGHT) rightPressed = true;
        if(code == KeyEvent.VK_ENTER) ePressed = true;
        if(code == KeyEvent.VK_F) fullscreenPressed = true;
        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) spacePressed = true;
        if(code == KeyEvent.VK_ESCAPE) pausePressed = !pausePressed;
        if(code == KeyEvent.VK_R) restartPressed = true;
        if(code == KeyEvent.VK_BACK_SLASH) menuPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP ) upPressed = false;
        if(code == KeyEvent.VK_DOWN ) downPressed = false;
        if(code == KeyEvent.VK_LEFT ) leftPressed = false;
        if(code == KeyEvent.VK_RIGHT ) rightPressed = false;
        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) spacePressed = false;
        if(code == KeyEvent.VK_ENTER) ePressed = false;
        if(code == KeyEvent.VK_ESCAPE) menuPressed = false;
        if(code == KeyEvent.VK_P) pausePressed = false;
        if(code == KeyEvent.VK_F) fullscreenPressed = false;
        if(code == KeyEvent.VK_R) restartPressed = false;
    }

}