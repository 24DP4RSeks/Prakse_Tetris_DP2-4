package main.java.main;

import java.util.ArrayList;
import main.java.mino.*;
import java.awt.Color;

public class LineClearManager {
    public static void checkDelete(PlayManager pm) {
        int x = pm.left_x;
        int y = pm.top_y;
        int blockCount = 0;
        int lineCount = 0;

        // Check the entire play area for full lines
        while(x < pm.right_x && y < pm.bottom_y) {
            for(int i = 0; i < pm.staticBlocks.size(); i++) {
                if(pm.staticBlocks.get(i).x == x && pm.staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == pm.right_x) {
                if(blockCount == 12) { // Line is full
                    pm.effectCounterOn = true;
                    // Ensure effectY is a list in PlayManager to handle multiple red flashes
                    pm.effectY.add(y);

                    // Remove blocks in the cleared line
                    for(int i = pm.staticBlocks.size() - 1; i > -1; i--) {
                        if(pm.staticBlocks.get(i).y == y) {
                            pm.staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    pm.lines++;

                    // Shift blocks above down
                    for(int i = 0; i < pm.staticBlocks.size(); i++) {
                        if(pm.staticBlocks.get(i).y < y) {
                            pm.staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = pm.left_x;
                y += Block.SIZE;
            }
        }

        // If lines were cleared in this single move
        if(lineCount > 0) {
            GamePanel.se.play(1, false); // Play clear sound
            
            // Set the combo to the number of lines cleared in this one move
            pm.combo = lineCount;

            int lineScoreBase;
            // High base scores for multi-clears
            switch(lineCount) {
                case 1: lineScoreBase = 100; break;
                case 2: lineScoreBase = 400; break; // DOUBLE
                case 3: lineScoreBase = 800; break; // TRIPLE
                case 4: lineScoreBase = 1600; break; // TETRIS
                default: lineScoreBase = 200 * lineCount; break;
            }

            // Apply Level Multiplier
            float levelMultiplier = 1.0f + (pm.level - 1) * 0.2f;
            
            // Apply aggressive Combo Multiplier for multi-line clears
            // Clearing 4 lines (Tetris) gives a massive 4x multiplier on top of the base
            float comboMultiplier = (float)Math.pow(pm.combo, 1.5); 
            
            int finalScore = (int)(lineScoreBase * levelMultiplier * comboMultiplier * 0.4);
            pm.score += finalScore;

            // Trigger the "Showup Message" if 2 or more lines were cleared at once
            if(pm.combo >= 2) {
                pm.comboEffectOn = true;
                pm.comboEffectCounter = 0;
                
                // Play a special sound effect for multi-clears
                GamePanel.se.play(2, false); 
            }

            updateDifficulty(pm);
        }
    }

    private static void updateDifficulty(PlayManager pm) {
        if(pm.lines >= pm.level * 10 && pm.level < 10) {
            pm.level++;
            if(PlayManager.dropInterval > 5) {
                PlayManager.dropInterval -= 5;
            }
        }
    }
}