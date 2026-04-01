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

        while(x < pm.right_x && y < pm.bottom_y) {
            for(int i = 0; i < pm.staticBlocks.size(); i++) {
                if(pm.staticBlocks.get(i).x == x && pm.staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == pm.right_x) {
                if(blockCount == 12) {
                    pm.effectCounterOn = true;
                    pm.effectY.add(y);

                    for(int i = pm.staticBlocks.size() - 1; i > -1; i--) {
                        if(pm.staticBlocks.get(i).y == y) {
                            pm.staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    pm.lines++;

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

        if(lineCount > 0) {
            GamePanel.se.play(1, false);
            long currentTime = System.currentTimeMillis();
            if(currentTime - pm.lastLinesClearedTime > pm.COMBO_TIMEOUT) {
                pm.combo = 1;
            } else {
                pm.combo++;
            }
            pm.lastLinesClearedTime = currentTime;

            int lineScoreBase;
            switch(lineCount) {
                case 1: lineScoreBase = 100; break;
                case 2: lineScoreBase = 300; break;
                case 3: lineScoreBase = 500; break;
                case 4: lineScoreBase = 800; break;
                default: lineScoreBase = 50 * lineCount; break;
            }

            float levelMultiplier = 1.0f + (pm.level - 1) * 0.1f;
            int baseScore = (int)(lineScoreBase * levelMultiplier);
            float comboMultiplier = 1.0f + (pm.combo - 1) * 0.5f;
            int comboScore = (int)(baseScore * comboMultiplier);
            pm.score += comboScore;

            if(pm.combo >= 2) {
                pm.comboEffectOn = true;
                pm.comboEffectCounter = 0;
                GamePanel.se.play(2, false);
            }

            updateDifficulty(pm);
        }
    }

    public static void updateDifficulty(PlayManager pm) {
        pm.level = 1 + (pm.lines / 5);
        int speedIncrease = (pm.lines / 5) + (pm.score / 1000);
        pm.dropInterval = Math.max(10, 60 - (speedIncrease * 3));
    }
}
