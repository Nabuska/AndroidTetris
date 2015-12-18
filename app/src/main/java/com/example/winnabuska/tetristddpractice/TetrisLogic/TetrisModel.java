package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;
import android.os.Vibrator;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Joona Enbuska on 19.10.2015.
 *
 * TetrisModel makes changes on the 'grid' array. After the constructor has been called grid should be given to UI view.
 * Tetris model acts as a 'Observable' and TetrisUI is its observer. Everytime the grid array is changes, TetrisModel calls 'setChanged()'
 * and 'notifyObservers()'
 * All TetrisModels methods except constructor should not be called outside of UI Thread
 * The game will continue and 'onTick' will be called as long as there is space for a new block to appear after the previous new block has landed in the grid
 * */
public class TetrisModel extends Observable {

    public static final int ROWS = 22, NUMBER_OF_VISIBLEROWS = 20, COLUMNS = 10, FIRST_VISIBLE_ROW = 2, LAST_VISIBLE_ROW = 21;
    private Optional<Square> [][] grid;

    private Block playersBlock;
    private GridSquareManipulator manipulator;
    private GridSpaceEvaluator evaluator;
    private Vibrator vibrator;

    public TetrisModel(Vibrator vibrator, Observer observer){
        addObserver(observer);
        this.vibrator = vibrator;
        grid = (Optional<Square>[][]) new Optional[22][10];
        clearGrid();
        manipulator = new GridSquareManipulator(grid);
        evaluator = new GridSpaceEvaluator(grid);
        playersBlock = Block.randomBlock();
        manipulator.addSquaresToGrid(playersBlock.squares);
        manipulator.addSquaresToGrid(evaluator.getBlockShadowSquares(playersBlock));
    }

    public void clearGrid(){
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                grid[y][x] = Optional.empty();
            }
        }
    }

    public Optional<Square> [][] getGrid(){
        return grid;
    }

    public void onTick(){
        onDrop();
    }

    public void performHardDrop() {
        vibrate(new long[]{50, 10, 50});
        hardDropFloatingSquares();
        onDrop();
    }

    public void performSoftDrop() {
        vibrate(new long[]{50});
        onDrop();
    }

    public void rotateBlock(final int ROTATE_DIRECTION){
        vibrate(new long[]{50});
        Map<Square, Point> offsetSquares = playersBlock.squareOffSetsOnRotate(ROTATE_DIRECTION);
        if (evaluator.isSafeOffset(offsetSquares)) {
            manipulator.removeGridValueAtSquarePoints(evaluator.getBlockShadowSquares(playersBlock));
            playersBlock.updateOrientation(ROTATE_DIRECTION);
            manipulator.offsetSquares(offsetSquares);
            manipulator.addSquaresToGrid(evaluator.getBlockShadowSquares(playersBlock));
            setChanged();
            notifyObservers();
        }
    }

    public void moveBlockHorizontally(final int MOVE_DIRECTION){
        vibrate(new long[]{50});
        if (evaluator.blockHasRoomAtHorizontal(playersBlock, MOVE_DIRECTION)) {
            manipulator.removeGridValueAtSquarePoints(evaluator.getBlockShadowSquares(playersBlock));
            manipulator.moveBlockHorizontally(playersBlock, MOVE_DIRECTION);
            manipulator.addSquaresToGrid(evaluator.getBlockShadowSquares(playersBlock));
            setChanged();
            notifyObservers();
        }
    }

    /**Once onDrop sends Boolean true to observer the game ends*/
    private void onDrop(){
        boolean gameContinues;
        if (!evaluator.squaresHaveRoomBelow(playersBlock.squares)) {
            gameContinues = onBlockLanding();
        } else {
            manipulator.dropSquaresByOne(playersBlock.squares);
            gameContinues = true;
        }
        setChanged();
        notifyObservers(gameContinues?null:true);
    }

    private boolean onBlockLanding(){
        boolean gameContinues = true;

        sleepMS(100);
        vibrate(new long[]{100});

        playersBlock = Block.randomBlock();

        Set<Integer> filledRows;
        while(!(filledRows = evaluator.getFilledRows()).isEmpty())
            onFullRowsPresent(filledRows);

        if (evaluator.squareLocationsAreEmpty(playersBlock.squares)) {
            insertPlayerBlockToGrid();
            manipulator.addSquaresToGrid(evaluator.getBlockShadowSquares(playersBlock));
        } else
            gameContinues = false;
        return gameContinues;
    }

    private void onFullRowsPresent(Set<Integer> fullRows){
        Stream.of(fullRows).forEach(rowNumber -> manipulator.destroyRow(rowNumber));
        vibrate(new long[]{50, 25, 50});
        setChanged();
        notifyObservers();
        sleepMS(250);
        hardDropFloatingSquares();
        vibrate(new long[]{50, 25, 50});
        setChanged();
        notifyObservers();
        sleepMS(250);
    }

    private void hardDropFloatingSquares(){
        List<Square> floatingSquares = evaluator.getAllFloatingSquares();
        while (!floatingSquares.isEmpty()) {
            manipulator.dropSquaresByOne(floatingSquares);
            floatingSquares = evaluator.getAllFloatingSquares();
        }
    }

    private void vibrate(long []pattern){
        for (int i = 0; i < pattern.length; i++) {
            vibrator.vibrate(pattern[i]);
        }
    }

    private void insertPlayerBlockToGrid(){
        manipulator.addSquaresToGrid(playersBlock.squares);
        if (evaluator.squaresHaveRoomBelow(playersBlock.squares))
            manipulator.dropSquaresByOne(playersBlock.squares);
        if (evaluator.squaresHaveRoomBelow(playersBlock.squares))
            manipulator.dropSquaresByOne(playersBlock.squares);
    }

    private void sleepMS(long ms){
        try{Thread.sleep(ms);}catch (InterruptedException e){}
    }

    public String gridToString(){
        String str = "\n";
        for (int y = 0; y < ROWS; y++) {
            str += "#";
            for (int x = 0; x < COLUMNS; x++) {
                if (grid[y][x].isPresent())
                    str += grid[y][x].get().COLOR;
                else
                    str += "*";
            }
            str += "#\n";
        }
        str+=("############\n");
        return str;
    }
}
