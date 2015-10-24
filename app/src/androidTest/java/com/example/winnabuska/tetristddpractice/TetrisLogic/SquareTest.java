package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;
import android.test.AndroidTestCase;

import com.annimon.stream.Stream;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Square;

import java.util.Arrays;

/**
 * Created by WinNabuska on 18.10.2015.
 */
public class SquareTest extends AndroidTestCase {

    Square square1, square2, square3, square4, square5;

    final String TAG = "SquareTest";

    @Override
    public void setUp() throws Exception {
        square1 = new Square(new Point(0,0), Square.COLOR_RED);
        square2 = new Square(new Point(0,1), Square.COLOR_RED);
        square3 = new Square(new Point(1,1), Square.COLOR_RED);
        square4 = new Square(new Point(0,2), Square.COLOR_RED);
        square5 = new Square(new Point(0,3), Square.COLOR_RED);
        //5
        //4
        //2 3
        //1
    }

    public void testAttachedTo() throws Exception {
        square1.attachTo(square2);
        square1.attachTo(square3);
        assertEquals(2, square1.getAttachedTo().size());
        assertEquals(1, square2.getAttachedTo().size());
        assertTrue(square1.getAttachedTo().containsAll(Arrays.asList(square2, square3)));
        assertSame(square1, square3.getAttachedTo().iterator().next());
        assertSame(square1, square2.getAttachedTo().iterator().next());
    }

    public void testGetAttachedToRecursivelyExcludingSelf(){
        square1.attachTo(square2);
        square2.attachTo(square3);
        square2.attachTo(square4);
        square4.attachTo(square5);
        assertEquals(4, square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square2.getAllSquaresInTheBlockExcludingSelf().size(), square3.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square3.getAllSquaresInTheBlockExcludingSelf().size(), square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square1.getAllSquaresInTheBlockExcludingSelf().size(), square2.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square4.getAllSquaresInTheBlockExcludingSelf().size(), square5.getAllSquaresInTheBlockExcludingSelf().size());

        Stream.of(Arrays.asList(square1, square2, square3, square4, square5)).forEach(b -> b.detachOneSelf());
        square1.attachTo(square2).attachTo(square3, square4).attachTo(square5);
        assertEquals(4, square1.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(4, square2.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(4, square3.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(4, square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(4, square5.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square2.getAllSquaresInTheBlockExcludingSelf().size(), square3.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square3.getAllSquaresInTheBlockExcludingSelf().size(), square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square1.getAllSquaresInTheBlockExcludingSelf().size(), square2.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(square4.getAllSquaresInTheBlockExcludingSelf().size(), square5.getAllSquaresInTheBlockExcludingSelf().size());
    }

    public void testDetachFrom(){
        square1.attachTo(square2);
        square2.attachTo(square3);
        square2.attachTo(square4);
        square4.attachTo(square5);
        square2.detachOneSelf();
        assertEquals(0, square1.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(0, square3.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(1, square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(1, square5.getAllSquaresInTheBlockExcludingSelf().size());

        square1.attachTo(square2);
        square2.attachTo(square3);
        square2.attachTo(square4);
        square4.attachTo(square5);
        square3.detachOneSelf();
        assertEquals(3, square1.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(3, square2.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(3, square4.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(3, square5.getAllSquaresInTheBlockExcludingSelf().size());

        square1.attachTo(square2);
        square2.attachTo(square3);
        square2.attachTo(square4);
        square4.attachTo(square5);
        square4.detachOneSelf();
        assertEquals(2, square1.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(2, square2.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(2, square3.getAllSquaresInTheBlockExcludingSelf().size());
        assertEquals(0, square5.getAllSquaresInTheBlockExcludingSelf().size());

    }

}
