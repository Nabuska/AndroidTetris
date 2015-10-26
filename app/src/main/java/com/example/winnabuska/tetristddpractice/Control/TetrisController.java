package com.example.winnabuska.tetristddpractice.Control;

import android.graphics.Point;
import android.os.Vibrator;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Block;
import com.example.winnabuska.tetristddpractice.TetrisLogic.GridSquareManipulator;
import com.example.winnabuska.tetristddpractice.TetrisLogic.GridSpaceEvaluator;
import com.example.winnabuska.tetristddpractice.TetrisLogic.Square;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joona Enbuska on 19.10.2015.
 *
 * Before TetrisController is used its initialization must be called
 * TetrisController is a Class that that performs changes on the user interfaces 'grid' array
 * TetrisControllers methods 'onTick', 'performSoftDrop' and 'performHardDrop' should not be called from the UI Thread,
 * instead use UIActionExecutor for all basic GameAction method calls
 * */
public class TetrisController {

    public static final int ROWS = 22, NUMBER_OF_VISIBLEROWS = 20, COLUMNS = 10, FIRST_VISIBLE_ROW = 2, LAST_VISIBLE_ROW = 21;
    public static Optional<Square> [][] grid;

    private static Block playersBlock;
    private static GridSquareManipulator manipulator;
    private static GridSpaceEvaluator evaluator;
    private static Vibrator vibrator;

    private TetrisController(){}

    public static void initialize(Vibrator vibrator){
        TetrisController.vibrator = vibrator;
        grid = (Optional<Square>[][]) new Optional[22][10];
        clearGrid();
        manipulator = new GridSquareManipulator();
        evaluator = new GridSpaceEvaluator();
        playersBlock = Block.randomBlock();
        manipulator.addSquaresToGrid(playersBlock.squares);
        manipulator.addSquaresToGrid(evaluator.getBlockShadow(playersBlock));
    }

    protected static boolean onTick(){
        synchronized (grid) {
            return onDrop();
        }
    }

    protected static void performHardDrop() {
        vibrate(new long[]{50, 10, 50});
        synchronized (grid) {
            hardDropFloatingSquares();
            onDrop();
            grid.notifyAll();
        }
    }

    protected static void performSoftDrop() {
        vibrate(new long[]{50});
        synchronized (grid) {
            onDrop();
        }
    }

    protected static void rotateBlock(final int ROTATE_DIRECTION){
        vibrate(new long[]{50});
        synchronized (grid) {
            Map<Square, Point> offsetSquares = playersBlock.squareOffSetsOnRotate(ROTATE_DIRECTION);
            if (evaluator.isSafeOffset(offsetSquares)) {
                manipulator.removeGridValueAtSquarePoints(evaluator.getBlockShadow(playersBlock));
                playersBlock.updateOrientation(ROTATE_DIRECTION);
                manipulator.offsetSquares(offsetSquares);
                manipulator.addSquaresToGrid(evaluator.getBlockShadow(playersBlock));
            }
            grid.notifyAll();
        }
    }

    protected static void moveBlockHorizontally(final int MOVE_DIRECTION){
        vibrate(new long[]{50});
        synchronized (grid) {
            if (evaluator.blockHasRoomAtHorizontal(playersBlock, MOVE_DIRECTION)) {
                manipulator.removeGridValueAtSquarePoints(evaluator.getBlockShadow(playersBlock));
                manipulator.moveBlockHorizontally(playersBlock, MOVE_DIRECTION);
                manipulator.addSquaresToGrid(evaluator.getBlockShadow(playersBlock));
            }
            grid.notifyAll();
        }
    }

    private static boolean onDrop(){
        if (!evaluator.squaresHaveRoomBelow(playersBlock.squares)) {
            return onBlockLanding();
        } else {
            manipulator.dropSquaresByOne(playersBlock.squares);
            return true;
        }
    }

    private static boolean onBlockLanding(){
        boolean gameContinues = true;

        try {Thread.sleep(100);} catch (InterruptedException e) {}
        vibrate(new long[]{100});

        playersBlock = Block.randomBlock();

        Set<Integer> filledRows;
        while(!(filledRows = evaluator.getFilledRows()).isEmpty())
            onFullRowsPresent(filledRows);

        if (evaluator.squareLocationsEmpty(playersBlock.squares)) {
            insertPlayerBlockToGrid();
            manipulator.addSquaresToGrid(evaluator.getBlockShadow(playersBlock));
        } else
            gameContinues = false;
        return gameContinues;
    }

    private static void onFullRowsPresent(Set<Integer> fullRows){
        deleteRows(fullRows);
        vibrate(new long[]{50, 25, 50});
        enableUIViewUpdate();
        sleepMS(250);
        hardDropFloatingSquares();
        vibrate(new long[]{50, 25, 50});
        enableUIViewUpdate();
        sleepMS(250);
    }

    private static void deleteRows(Set<Integer> fullRows){
        Stream.of(fullRows).forEach(i -> manipulator.destroyRow(i));
    }

    private static void hardDropFloatingSquares(){
        List<Square> floatingSquares = evaluator.getAllFloatingSquares();
        while (!floatingSquares.isEmpty()) {
            manipulator.dropSquaresByOne(floatingSquares);
            floatingSquares = evaluator.getAllFloatingSquares();
        }
    }

    public static void clearGrid(){
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                grid[y][x] = Optional.empty();
            }
        }
    }

    private static void insertPlayerBlockToGrid(){
        manipulator.addSquaresToGrid(playersBlock.squares);
        if (evaluator.squaresHaveRoomBelow(playersBlock.squares))
            manipulator.dropSquaresByOne(playersBlock.squares);
        if (evaluator.squaresHaveRoomBelow(playersBlock.squares))
            manipulator.dropSquaresByOne(playersBlock.squares);
        grid.notifyAll();
    }

    private static void vibrate(long []pattern){
        for (int i = 0; i < pattern.length; i++) {
            vibrator.vibrate(pattern[i]);
        }
    }

    private static void enableUIViewUpdate(){
        grid.notifyAll();
        try{grid.wait();//UI thread update starts
        }catch (InterruptedException e){}
        //UI Thread has updated and called notifyAll
    }

    private static void sleepMS(long ms){
        try{Thread.sleep(ms);}catch (InterruptedException e){}
    }

    public static String gridToString(){
        String str = "\n";
        for (int y = 0; y < ROWS; y++) {
            str += "#";
            for (int x = 0; x < COLUMNS; x++) {
                if (TetrisController.grid[y][x].isPresent())
                    str += TetrisController.grid[y][x].get().COLOR;
                else
                    str += "*";
            }
            str += "#\n";
        }
        str+=("############\n");
        return str;
    }



    /*private static void deleteAndDropFullRows(){
        Set<Integer> fullRows = Stream.ofRange(0, ROWS).filter(i -> evaluator.isFilledRow(i)).collect(Collectors.toSet());
        while(!fullRows.isEmpty()){
            Stream.of(fullRows).forEach(i -> manipulator.destroyRow(i));
            List<Square> floatingSquares = evaluator.getAllFloatingSquares();
            while (!floatingSquares.isEmpty()) {
                manipulator.dropSquaresByOne(floatingSquares);
                floatingSquares = evaluator.getAllFloatingSquares();
                Log.i("deleteAndDrop", "2 while end");
            }
            vibrate(new long[]{50, 25, 50});
            grid.notifyAll();
            try{grid.wait();}catch (InterruptedException e){}
            try{Thread.sleep(400);}catch (InterruptedException e){}
            fullRows = Stream.ofRange(0, ROWS).filter(i -> evaluator.isFilledRow(i)).collect(Collectors.toSet());
        }
    }*/
}
