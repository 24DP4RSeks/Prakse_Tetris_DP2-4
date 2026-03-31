package main;

import java.util.Random;
import mino.*;

public class MinoFactory {
    public static Mino pickMino() {
        int i = new Random().nextInt(7);
        switch(i) {
            case 0: return new Mino_L1();
            case 1: return new Mino_L2();
            case 2: return new Mino_Square();
            case 3: return new Mino_Bar();
            case 4: return new Mino_T();
            case 5: return new Mino_Z1();
            case 6: return new Mino_Z2();
            default: return new Mino_Square();
        }
    }
}
