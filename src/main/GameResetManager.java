package main;

public class GameResetManager {
    public static void resetGame(PlayManager pm) {
        pm.staticBlocks.clear();
        pm.level = 1;
        pm.lines = 0;
        pm.score = 0;
        pm.combo = 0;
        pm.comboEffectOn = false;
        pm.comboEffectCounter = 0;
        pm.lastLinesClearedTime = 0;
        pm.isPaused = false;
        pm.pauseMenuSelection = 0;
        pm.gameOver = false;
        pm.dropInterval = 60;

        pm.currentMino = MinoFactory.pickMino();
        pm.currentMino.setXY(pm.MINO_START_X, pm.MINO_START_Y);

        pm.nextMino = MinoFactory.pickMino();
        pm.nextMino.setXY(pm.NEXTMINO_X, pm.NEXTMINO_Y);
    }
}
