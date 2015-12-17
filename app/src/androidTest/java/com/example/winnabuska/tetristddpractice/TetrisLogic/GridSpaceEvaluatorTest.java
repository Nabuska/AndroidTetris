package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.test.AndroidTestCase;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by WinNabuska on 19.10.2015.
 */
public class GridSpaceEvaluatorTest extends AndroidTestCase {

    final String TAG = "GridSpaceEvaluatorTest";
    GridSpaceEvaluator evaluator;
    Optional<Square> [][] grid;
    int bottomY = 21;
    GridSquareManipulator manipulator;
    TetrisModel tetris;

    HashMap<Point, Square> squares;

    @Override
    public void setUp() throws Exception {
        tetris = new TetrisModel((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE), (a1, a2) -> System.out.print(""));
        tetris.clearGrid();
        grid = tetris.getGrid();
        squares = new HashMap<>();
        manipulator = new GridSquareManipulator(grid);

        evaluator = new GridSpaceEvaluator(grid);
        squares.put(new Point(1,bottomY), new Square(new Point(1, bottomY),         1));
        squares.put(new Point(2,bottomY), new Square(new Point(2,bottomY),          1));
        squares.put(new Point(3,bottomY), new Square(new Point(3,bottomY),          1));
        squares.put(new Point(4, bottomY), new Square(new Point(4, bottomY), 1));
        squares.put(new Point(2, bottomY - 1), new Square(new Point(2, bottomY - 1), 1));
        squares.put(new Point(3, bottomY - 2), new Square(new Point(3, bottomY - 2), 1));
        squares.put(new Point(4, bottomY - 2), new Square(new Point(4, bottomY - 2), 1));
        squares.put(new Point(5, bottomY - 2), new Square(new Point(5, bottomY - 2), 1));
        squares.put(new Point(6, bottomY), new Square(new Point(6, bottomY), 1));
        squares.put(new Point(6, bottomY - 1), new Square(new Point(6, bottomY - 1), 1));
        squares.put(new Point(6, bottomY - 2), new Square(new Point(6, bottomY - 2), 1));
        //
        //***####***
        //**#***#***
        //*####*#***

        Stream.of(squares).forEach(e -> grid[e.getKey().y][e.getKey().x] = Optional.of(e.getValue()));
    }

    public void testIsFilledRow() throws Exception{
        boolean isFull = evaluator.isFilledRow(bottomY);
        assertFalse(isFull);
        grid[bottomY][5] = Optional.of(new Square(new Point(5,bottomY), 1));
        grid[bottomY][0] = Optional.of(new Square(new Point(0,bottomY), 1));
        grid[bottomY][7] = Optional.of(new Square(new Point(7,bottomY), 1));
        grid[bottomY][8] = Optional.of(new Square(new Point(8, bottomY), 1));
        isFull = evaluator.isFilledRow(bottomY);
        assertFalse(isFull);
        grid[bottomY][9] = Optional.of(new Square(new Point(9,bottomY), 1));
        isFull = evaluator.isFilledRow(bottomY);
        assertTrue(isFull);
    }

    public void testGetAllStableBlocks_SingleNonAttachedSquares()throws Exception {
        Set<Square> assertStableSquares = new HashSet<>(Arrays.asList(
                squares.get(new Point(1, bottomY)), squares.get(new Point(2, bottomY)), squares.get(new Point(3, bottomY)), squares.get(new Point(4, bottomY)),
                squares.get(new Point(2, bottomY - 1)),
                squares.get(new Point(6, bottomY)), squares.get(new Point(6, bottomY - 1)), squares.get(new Point(6, bottomY - 2))));
        Set<Square> actualStableSquares = evaluator.getAllStableSquares();

        assertEquals(assertStableSquares.size(), actualStableSquares.size());
        assertEquals(assertStableSquares, actualStableSquares);
    }

    public void testGetAllStableBlocks_AttachedHangingSquares()throws Exception {

        squares.get(new Point(6,bottomY-2)).attachTo(squares.get(new Point(5,bottomY-2)),squares.get(new Point(4,bottomY-2)),squares.get(new Point(3,bottomY-2)));

        Set<Square> assertStableSquares = new HashSet<>(Arrays.asList(
                squares.get(new Point(1, bottomY)), squares.get(new Point(2, bottomY)), squares.get(new Point(3, bottomY)), squares.get(new Point(4, bottomY)),
                squares.get(new Point(2, bottomY - 1)),
                squares.get(new Point(6, bottomY)), squares.get(new Point(6, bottomY - 1)), squares.get(new Point(6, bottomY - 2))
                //1 added
                ,squares.get(new Point(5,bottomY-2)),squares.get(new Point(4,bottomY-2)),squares.get(new Point(3,bottomY-2))));

        //
        //***¤¤¤¤*** <-- ¤ attachedToEachOther
        //**#***#***
        //*####*#***

        Set<Square> actualStableSquares = evaluator.getAllStableSquares();
        assertEquals(assertStableSquares.size(), actualStableSquares.size());
        assertEquals(assertStableSquares, actualStableSquares);
    }

    public void testGetAllStableBlocks_SquaresOnTopOfHangingSquares()throws Exception{
        Square singleNonStable = new Square(new Point(7,bottomY-2),1);
        grid[singleNonStable.location.y][singleNonStable.location.x] = Optional.of(singleNonStable);
        Square onTopOfLedgeSquare = new Square(new Point(3,bottomY-3),1);
        Square onTopOfLedgeHangingSquare = new Square(new Point(2,bottomY-3),1);
        onTopOfLedgeSquare.attachTo(onTopOfLedgeHangingSquare);
        //grid[onTopOfLedgeSquare.location.y][onTopOfLedgeSquare.location.x] = Optional.of(onTopOfLedgeSquare);singleNonStable
        grid[onTopOfLedgeSquare.location.y][onTopOfLedgeSquare.location.x] = Optional.of(onTopOfLedgeSquare);
        grid[onTopOfLedgeHangingSquare.location.y][onTopOfLedgeHangingSquare.location.x] = Optional.of(onTopOfLedgeHangingSquare);
        squares.get(new Point(6,bottomY-2)).attachTo(squares.get(new Point(5,bottomY-2)),squares.get(new Point(4,bottomY-2)),squares.get(new Point(3,bottomY-2)));

        Set<Square> assertStableSquares = new HashSet<>(Arrays.asList(
                squares.get(new Point(1, bottomY)), squares.get(new Point(2, bottomY)), squares.get(new Point(3, bottomY)), squares.get(new Point(4, bottomY)),
                squares.get(new Point(2, bottomY - 1)),
                squares.get(new Point(6, bottomY)), squares.get(new Point(6, bottomY - 1)), squares.get(new Point(6, bottomY - 2))
                //1 added
                ,squares.get(new Point(5,bottomY-2)),squares.get(new Point(4,bottomY-2)),squares.get(new Point(3,bottomY-2)),
                onTopOfLedgeHangingSquare, onTopOfLedgeSquare));

        //**¤¤****** <-- ¤ 2 squares attached to eachother
        //***¤¤¤¤#** <-- ¤ 4 squares attached to eachother
        //**#***#*** <-- # 2 single squares
        //*####*#*** <-- # 5 single squares

        Set<Square> actualStableSquares = evaluator.getAllStableSquares();
        assertEquals(assertStableSquares, actualStableSquares);
        assertEquals(assertStableSquares.size(), actualStableSquares.size());
    }

    public void testRoomForBlock(){
        tetris.clearGrid();
        Block b1 = new Block(Block.I);
        assertTrue(evaluator.squareLocationsAreEmpty(b1.squares));
        grid[b1.squares.get(0).location.y][b1.squares.get(0).location.x] = Optional.of(b1.squares.get(0));
        assertFalse(evaluator.squareLocationsAreEmpty(b1.squares));
    }

    //Note this method relies on GridSquareManipulator methods addBlockToGrid and dropSquaresByOne to work correctly
    public void testBlockHasRoomBelow_OtherBlockBlocking() throws Exception{
        tetris.clearGrid();
        Block b1 = new Block(Block.I);
        manipulator.addSquaresToGrid(b1.squares);
        manipulator.dropSquaresByOne(b1.squares);
        manipulator.dropSquaresByOne(b1.squares);
        assertTrue(evaluator.squaresHaveRoomBelow(b1.squares));

        Block b2 = new Block(Block.I);
        manipulator.addSquaresToGrid(b2.squares);
        manipulator.dropSquaresByOne(b2.squares);
        assertFalse(evaluator.squaresHaveRoomBelow(b2.squares));

        manipulator.dropSquaresByOne(b1.squares);
        assertTrue(evaluator.squaresHaveRoomBelow(b2.squares));

        manipulator.dropSquaresByOne(b2.squares);
        assertFalse(evaluator.squaresHaveRoomBelow(b2.squares));
    }

    public void testBlockHasRoomBelow_FloorBlocking() throws Exception {
        Block b1 = new Block(Block.Z);
        while(Stream.of(b1.squares).noneMatch(s -> s.location.y==21)){
            manipulator.dropSquaresByOne(b1.squares);
        }
        assertFalse(evaluator.squaresHaveRoomBelow(b1.squares));
    }

    public void testBlockHasRoomToRight_HasRoom(){
        Block b1 = new Block(Block.Z);
        assertTrue(evaluator.blockHasRoomAtHorizontal(b1, Block.MOVE_DIRECTION_RIGHT));
        //assertTrue(evaluator.blockHasRoomToRight(b1));
    }

    public void testBlockHasRoomToLeft_HasRoom(){
        Block b1 = new Block(Block.Z);
        assertTrue(evaluator.blockHasRoomAtHorizontal(b1, Block.MOVE_DIRECTION_LEFT));
        //assertTrue(evaluator.blockHasRoomToLeft(b1));
    }

}
