package main.java.main;


import javax.swing.JFrame;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class Main{
        public static JFrame window;
        public static boolean isFullscreen = false;
        private static GraphicsDevice device;

        /**
         * funkcija main pieņem String[] tipa vērtību args un atgriež void tipa vērtību null.
         * Šī funkcija inicializē logu, pievieno GamePanel un palaiž spēli.
         */
        public static void main(String[] args){
            
                window = new JFrame("Simple Tetris");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(true);
                window.setUndecorated(false);

                // Get graphics device for fullscreen
                device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

                // Add GamePanel to the window
                GamePanel gp = new GamePanel();
                window.add(gp);
                window.pack();

                window.setLocationRelativeTo(null);
                window.setVisible(true);

                gp.launchGame();

                DatabaseHandler db = new DatabaseHandler();
                db.connect();
                
                
        }
        
        /**
         * funkcija toggleFullscreen pieņem void tipa vērtību null un atgriež void tipa vērtību null.
         * Šī funkcija pārslēdz starp pilnekrāna un loga režīmu.
         */
        public static void toggleFullscreen() {
            if(isFullscreen) {
                // Exit fullscreen
                window.dispose();
                window.setUndecorated(false);
                device.setFullScreenWindow(null);
                window.setVisible(true);
                isFullscreen = false;
            } else { 
                // Enter fullscreen
                window.dispose();
                window.setUndecorated(true);
                device.setFullScreenWindow(window);
                isFullscreen = true;
            }
        }
}