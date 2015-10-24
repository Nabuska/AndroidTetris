package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.test.AndroidTestCase;

import com.annimon.stream.Optional;
import com.example.winnabuska.tetristddpractice.Control.TetrisController;

/**
 * Created by WinNabuska on 19.10.2015.
 */
public class TetrisControllerTest extends AndroidTestCase {

    String emptyGridStr;

    @Override
    public void setUp() throws Exception {
        TetrisController.initialize((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE));
        TetrisController.clearGrid();
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
        String str = TetrisController.gridToString();
        assertEquals(emptyGridStr, str);
    }

    public void testAddBlockToGrid() throws Exception {
        Optional<Square> b1 = Optional.of(new Square(new Point(1, 1), 1));
        TetrisController.grid[b1.get().location.y][b1.get().location.x] = b1;

        String [] rows = emptyGridStr.split("\n");
        rows[b1.get().location.y] = "#*"+b1.get().COLOR+"********#";
        String assertStr = "\n";
        for(int y = 0; y<rows.length; y++){
            assertStr+=rows[y]+"\n";
        }
        String actualStr = TetrisController.gridToString();
    }
}
