package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Joona Enbuska on 18.10.2015.
 * A Block is a tetris block that the players controls by rotating, moving horizontally and vertically down.
 * Block is made of a set of squares that are attached to each other. Block is a concept that is used for creating
 * different shaped of square clusters, and it provides info how should the Block Square locations change if the
 * Block would be rotated.
 * After the Block hits the bottom of the grid or other Squares in the grid, the player
 * loses control over the block, and the concept of the particular Block becomes obsolete, but the Squares and
 * they attachments remain unchanged, unless the attachments are implicitly detached.
 * When a block is created, its default horizontal positions if in the middle of the row. If there is no absolute
 * horizontal center for the Block the position is rounded down (to left).
 * Blocks vertical default position is in the two first rows of the grid.
 * In the case of I Block its vertical default position is on the first row of the grid.
 *
 */
public class Block {


    public static final int COUNTERCLOCKWISE = -1, CLOCKWISE = 1, MOVE_DIRECTION_RIGHT = 1, MOVE_DIRECTION_LEFT = -1;
    public final int COLOR;
    public final int BLOCK_TYPE;
    protected static final int I = 1, S = 2, Z = 3,  O = 4, J = 5, T = 6, L = 7;
    public final List<Square> squares;
    private final int NUMBER_OF_ORIENTATIONS;
    private int currentOrientation = 0;

    public static Block randomBlock(){
        int type = (int) (7*Math.random())+1;
        return new Block(type);
    }
    protected Block(int blockType){
        BLOCK_TYPE = blockType;
        switch (blockType){
            case(I):
                COLOR = Square.COLOR_CYAN;
                NUMBER_OF_ORIENTATIONS = 2;
                squares = createIBlock();
                break;
            case(J):
                COLOR = Square.COLOR_BLUE;
                NUMBER_OF_ORIENTATIONS = 4;
                squares = createJBlock();
                break;
            case(L):
                COLOR = Square.COLOR_ORANGE;
                NUMBER_OF_ORIENTATIONS = 4;
                squares = createLBlock();
                break;
            case(O):
                COLOR = Square.COLOR_YELLOR;
                NUMBER_OF_ORIENTATIONS = 1;
                squares = createOBlock();
                break;
            case(S):
                COLOR = Square.COLOR_GREEN;
                NUMBER_OF_ORIENTATIONS = 2;
                squares = createSBlock();
                break;
            case(T):
                COLOR = Square.COLOR_PURPLE;
                NUMBER_OF_ORIENTATIONS = 4;
                squares = createTBlock();
                break;
            case(Z):
                COLOR = Square.COLOR_RED;
                NUMBER_OF_ORIENTATIONS = 2;
                squares = createZBlock();
                break;
            default:
                squares = null;
                NUMBER_OF_ORIENTATIONS = 0;
                COLOR = -1;
                Log.i("error", "!Block constructor switch default!");
        }
    }

    /**squareOffSetsOnRotate
     * Return a map that tells how much every Block Squares location would change if the
     * the Block was rotated in the given direction. Note that if the offsets would be used and the
     * Block rotated the BLOCK MUST BE INFORMED of this by calling 'update orientation (TURN_DIRECTION)'.*/
    public Map<Square, Point> squareOffSetsOnRotate(final int TURN_DIRECTION){
        List<Point> offsets;
        switch (BLOCK_TYPE){
            case(O):
                offsets = new ArrayList<>();
                Stream.ofRange(0,4).forEach(i -> offsets.add(new Point(0,0)));
                break;
            case(I):
                offsets = rotationChanges_IBlock();
                break;
            case(S):
                offsets = rotationChanges_SBlock();
                break;
            case(Z):
                offsets = rotationChanges_ZBlock();
                break;
            case(J):
                offsets = rotationChanges_JBlock(TURN_DIRECTION);
                break;
            case(L):
                offsets = rotationChanges_LBlock(TURN_DIRECTION);
                break;
            case(T):
                offsets =  rotationChanges_TBlock(TURN_DIRECTION);
                break;
            default:
                offsets = null;
                break;
        }
        Map<Square, Point> squareOffsets = new HashMap<>();
        for(int i = 0; i<4; i++) {
            squareOffsets.put(squares.get(i), offsets.get(i));
        }
        return squareOffsets;
    }

    /**
     * This method has to be called every time the block is rotated. Otherwise next time the 'squareOffSetsOnRotate'
     * method is called the Point offset info will be inaccurate*/
    public void updateOrientation(final int TURN_DIRECTION){
        currentOrientation = (NUMBER_OF_ORIENTATIONS + TURN_DIRECTION + currentOrientation)%NUMBER_OF_ORIENTATIONS;
    }


    private List<Point> rotationChanges_TBlock(final int TURN_DIRECTION){
        List<List<Point>> turns = new ArrayList<>();
        turns.add(Arrays.asList(new Point(1, -2), new Point(0, -1), new Point(1, 0), new Point(-1, 0)));
        turns.add(Arrays.asList(new Point(1,1), new Point(0,0), new Point(-1,1), new Point(-1, -1)));
        turns.add(Arrays.asList(new Point(-1, 1), new Point(0,0), new Point(-1,-1), new Point(1, -1)));
        turns.add(Arrays.asList(new Point(-1, 0), new Point(0, 1), new Point(1, 0), new Point(1, 2)));

        return pickFitTurn(turns, TURN_DIRECTION);
    }

    private List<Point> rotationChanges_LBlock(final int TURN_DIRECTION){
        List<List<Point>> turns = new ArrayList<>();
        turns.add(Arrays.asList(new Point(1,-2), new Point(0,-1), new Point(-1,0), new Point(0,1)));
        turns.add(Arrays.asList(new Point(1,1), new Point(0,0), new Point(-1,-1), new Point(-2,0)));
        turns.add(Arrays.asList(new Point(-1,1), new Point(0,0), new Point(1,-1), new Point(0,-2)));
        turns.add(Arrays.asList(new Point(-1, 0), new Point(0, 1), new Point(1, 2), new Point(2, 1)));

        return pickFitTurn(turns, TURN_DIRECTION);
    }

    private List<Point> rotationChanges_JBlock(final int TURN_DIRECTION){
        List<List<Point>> turns = new ArrayList<>();
        turns.add(Arrays.asList(new Point(2, -1), new Point(1,-2), new Point(0, -1), new Point(-1, 0)));
        turns.add(Arrays.asList(new Point(0, 2), new Point(1,1), new Point(0,0), new Point(-1,-1)));
        turns.add(Arrays.asList(new Point(-2, 0), new Point(-1,1), new Point(0,0), new Point(1,-1)));
        turns.add(Arrays.asList(new Point(0, -1), new Point(-1, 0), new Point(0, 1), new Point(1, 2)));

        return pickFitTurn(turns, TURN_DIRECTION);
    }

    private List<Point> pickFitTurn(List<List<Point>> turns, final int TURN_DIRECTION){
        if(TURN_DIRECTION== CLOCKWISE)
            return turns.get(currentOrientation % NUMBER_OF_ORIENTATIONS);
        else{
            List<Point> dLoc = turns.get(Math.abs((NUMBER_OF_ORIENTATIONS+ currentOrientation -1)%NUMBER_OF_ORIENTATIONS));
            Stream.of(dLoc).forEach(l -> l.negate());
            return dLoc;
        }
    }

    private List<Point> rotationChanges_IBlock(){
        List<Point> turn = Arrays.asList(new Point(2,-1), new Point(1,0), new Point(0,1), new Point(-1,2));
        if(currentOrientation %NUMBER_OF_ORIENTATIONS==0) return turn;
        else{
            Stream.of(turn).forEach(l -> l.negate());
            return turn;
        }
    }

    private List<Point> rotationChanges_SBlock(){
        List<Point> turn = Arrays.asList(new Point(1,0), new Point(0,-1), new Point(-1,0), new Point(-2,-1));
        if(currentOrientation %NUMBER_OF_ORIENTATIONS==0) return turn;
        else return Stream.of(turn).map(p -> new Point(-p.x, -p.y)).collect(Collectors.toList());
    }

    private List<Point> rotationChanges_ZBlock(){
        List<Point> turn = Arrays.asList(new Point(2,-1), new Point(1,0), new Point(0,-1), new Point(-1,0));
        if(currentOrientation %NUMBER_OF_ORIENTATIONS==0) return turn;
        else return Stream.of(turn).map(p -> new Point(-p.x, -p.y)).collect(Collectors.toList());
    }

    private List<Square> createIBlock() {
        Square b1 = new Square(new Point(3,0), COLOR);
        Square b2 = new Square(new Point(4,0), COLOR);
        Square b3 = new Square(new Point(5,0), COLOR);
        Square b4 = new Square(new Point(6,0), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4);
        return Arrays.asList(b1, b2, b3, b4);
    }

    private List<Square> createJBlock() {
        Square b1 = new Square(new Point(3,0), COLOR);
        Square b2 = new Square(new Point(3,1), COLOR);
        Square b3 = new Square(new Point(4,1), COLOR);
        Square b4 = new Square(new Point(5,1), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4);
        return Arrays.asList(b1,b2,b3,b4);
    }

    private List<Square> createLBlock() {
        Square b1 = new Square(new Point(3,1), COLOR);
        Square b2 = new Square(new Point(4,1), COLOR);
        Square b3 = new Square(new Point(5,1), COLOR);
        Square b4 = new Square(new Point(5,0), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4);
        return Arrays.asList(b1,b2,b3,b4);
    }

    private List<Square> createOBlock() {
        Square b1 = new Square(new Point(4,1), COLOR);
        Square b2 = new Square(new Point(4,0), COLOR);
        Square b3 = new Square(new Point(5,0), COLOR);
        Square b4 = new Square(new Point(5,1), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4).attachTo(b1);
        return Arrays.asList(b1,b2,b3,b4);
    }

    private List<Square> createSBlock() {
        Square b1 = new Square(new Point(3,1), COLOR);
        Square b2 = new Square(new Point(4,1), COLOR);
        Square b3 = new Square(new Point(4,0), COLOR);
        Square b4 = new Square(new Point(5,0), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4);
        return Arrays.asList(b1,b2,b3,b4);
    }

    private List<Square> createTBlock() {
        Square b1 = new Square(new Point(3,1), COLOR);
        Square b2 = new Square(new Point(4,1), COLOR);
        Square b3 = new Square(new Point(4,0), COLOR);
        Square b4 = new Square(new Point(5,1), COLOR);
        b1.attachTo(b2).attachTo(b3,b4);
        return Arrays.asList(b1,b2,b3,b4);
    }

    private List<Square> createZBlock() {
        Square b1 = new Square(new Point(3,0), COLOR);
        Square b2 = new Square(new Point(4,0), COLOR);
        Square b3 = new Square(new Point(4,1), COLOR);
        Square b4 = new Square(new Point(5,1), COLOR);
        b1.attachTo(b2).attachTo(b3).attachTo(b4);
        return Arrays.asList(b1,b2,b3,b4);
    }
}
