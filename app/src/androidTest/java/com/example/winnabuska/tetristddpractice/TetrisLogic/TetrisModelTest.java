package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.test.AndroidTestCase;

import com.annimon.stream.Optional;

/**
 * Created by WinNabuska on 19.10.2015.
 */
public class TetrisModelTest extends AndroidTestCase {

    TetrisModel tetris;
    String emptyGridStr;
    Optional<Square>[][]grid;

    @Override
    public void setUp() throws Exception {
        tetris = new TetrisModel((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE), (a1, a2) -> System.out.print(""));
        grid = tetris.getGrid();
        tetris.clearGrid();
        emptyGridStr = "\n#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "#**********#\n"+
                        "############\n";
    }


    public void testClearGrid() throws Exception {
        String str = tetris.gridToString();
        assertEquals(emptyGridStr, str);
    }

    public void testAddBlockToGrid() throws Exception {
        Optional<Square> b1 = Optional.of(new Square(new Point(1, 1), 1));
        grid[b1.get().location.y][b1.get().location.x] = b1;

        String [] rows = emptyGridStr.split("\n");
        rows[b1.get().location.y] = "#*"+b1.get().COLOR+"********#";
        String assertStr = "\n";
        for(int y = 0; y<rows.length; y++){
            assertStr+=rows[y]+"\n";
        }
        String actualStr = tetris.gridToString();
    }
}
