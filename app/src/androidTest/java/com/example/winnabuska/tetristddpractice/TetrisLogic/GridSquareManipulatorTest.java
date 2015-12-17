package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.content.Context;
import android.os.Vibrator;
import android.test.AndroidTestCase;

import com.annimon.stream.Optional;

/**
 * Created by WinNabuska on 19.10.2015.
 */
public class GridSquareManipulatorTest extends AndroidTestCase {

    GridSquareManipulator manipulator;
    Optional<Square>[][] grid;
    String [] emptyGridRows;
    String emptyGridStr;
    TetrisModel tetris;

    @Override
    public void setUp() throws Exception {
        tetris = new TetrisModel((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE), (a1, a2) -> System.out.print(""));
        tetris.clearGrid();
        grid = tetris.getGrid();
        manipulator = new GridSquareManipulator(grid);
        emptyGridStr = "#**********#\n"+
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
                    "############";
        emptyGridRows = emptyGridStr.split("\n");
    }

    /*Creating and adding new Tetries pieces on the grid*/

    public void testAddTetrisPiece_TBlock() throws Exception{
        Block t = new Block(Block.T);
        manipulator.addSquaresToGrid(t.squares);
        String assertString =
                "\n#****"+6+"*****#\n"+
                 "#***"+6+6+6+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
    }

    public void testAddTetrisPiece_OBlock() throws Exception{
        Block o = new Block(Block.O);
        manipulator.addSquaresToGrid(o.squares);
        String assertString =
                "\n#****"+4+4+"****#\n"+
                  "#****"+4+4+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0,2));
    }

    public void testAddTetriesPiece_ZBlock() throws Exception{
        Block z = new Block(Block.Z);
        manipulator.addSquaresToGrid(z.squares);
        String assertString =
                    "\n#***"+0+0+"*****#\n"+
                        "#****"+0+0+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0,2));
    }

    public void testAddTetriesPiece_IBlock() throws Exception{
        Block i = new Block(Block.I);
        manipulator.addSquaresToGrid(i.squares);
        String assertString =
                "\n#***"+1+1+1+1+"***#\n"+
                   "#**********#\n";
        assertEquals(assertString, getTetrisRowsToString(0,2));
    }

    public void testAddTetriesPiece_JBlock() throws Exception{
        Block j = new Block(Block.J);
        manipulator.addSquaresToGrid(j.squares);
        String assertString =
                      "\n#***"+2+"******#\n"+
                        "#***"+2+2+2+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0,2));
    }

    public void testAddTetriesPiece_LBlock() throws Exception{
        Block l = new Block(Block.L);
        manipulator.addSquaresToGrid(l.squares);
        String assertString =
                        "\n#*****"+3+"****#\n"+
                        "#***"+3+3+3+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
    }

    public void testAddTetriesPiece_SBlock() throws Exception{
        Block s = new Block(Block.S);
        manipulator.addSquaresToGrid(s.squares);
        String assertString =
                "\n#****"+5+5+"****#\n"+
                "#***"+5+5+"*****#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
    }

    /*dropping the tetris pieces by 1 grid block*/
    public void testDropBlocksByOne()throws Exception{
        Block t = new Block(Block.T);
        manipulator.addSquaresToGrid(t.squares);
        manipulator.dropSquaresByOne(t.squares);
        String assertString =
                         "\n#**********#\n"+
                         "#****"+6+"*****#\n"+
                        "#***"+6+6+6+"****#\n";
        String actualString = getTetrisRowsToString(0,3);
        assertEquals(assertString, actualString);
        manipulator.dropSquaresByOne(t.squares);
        assertString =
                        "\n#**********#\n"+
                          "#**********#\n"+
                          "#****"+6+"*****#\n"+
                         "#***"+6+6+6+"****#\n";
        actualString = getTetrisRowsToString(0,4);
        assertEquals(assertString, actualString);
    }





    /*Rotating Blocks*/

    //Rotate the Block to right 3 times around
    public void testRotateRight_TBlock()throws Exception{
        Block t = new Block(Block.T);
        manipulator.addSquaresToGrid(t.squares);
        manipulator.dropSquaresByOne(t.squares);
        manipulator.dropSquaresByOne(t.squares);
        for(int i = 0; i<3; i++) {
            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
            t.updateOrientation(Block.CLOCKWISE);
            String assertString =
                    "\n#**********#\n" +
                            "#****" + 6 + "*****#\n" +
                            "#****" + 6 + 6 + "****#\n" +
                            "#****" + 6 + "*****#\n";
            String actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
            t.updateOrientation(Block.CLOCKWISE);
            assertString =
                    "\n#**********#\n" +
                            "#**********#\n" +
                            "#***" + 6 + 6 + 6 + "****#\n" +
                            "#****" + 6 + "*****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
            t.updateOrientation(Block.CLOCKWISE);
            assertString =
                    "\n#**********#\n" +
                            "#****" + 6 + "*****#\n" +
                             "#***" + 6 + 6 + "*****#\n" +
                            "#****" + 6 + "*****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
            t.updateOrientation(Block.CLOCKWISE);
            assertString = "\n#**********#\n" +
                    "#**********#\n" +
                       "#****" + 6 + "*****#\n" +
                    "#***" + 6 + 6 + 6 + "****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);
        }
    }




    //Rotate the Block to left 3 times around
    public void testRotateLeft_TBlock()throws Exception{
        for(int i = 0; i<3; i++) {
            Block t = new Block(Block.T);
            manipulator.addSquaresToGrid(t.squares);
            manipulator.dropSquaresByOne(t.squares);
            manipulator.dropSquaresByOne(t.squares);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
            t.updateOrientation(Block.COUNTERCLOCKWISE);
            String assertString =
                    "\n#**********#\n" +
                            "#****" + 6 + "*****#\n" +
                            "#***" + 6 + 6 + "*****#\n" +
                            "#****" + 6 + "*****#\n";
            String actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
            t.updateOrientation(Block.COUNTERCLOCKWISE);
            assertString =
                    "\n#**********#\n" +
                            "#**********#\n" +
                            "#***" + 6 + 6 + 6 + "****#\n" +
                            "#****" + 6 + "*****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
            t.updateOrientation(Block.COUNTERCLOCKWISE);
            assertString =
                    "\n#**********#\n" +
                            "#****" + 6 + "*****#\n" +
                            "#****" + 6 + 6 + "****#\n" +
                            "#****" + 6 + "*****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);

            manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
            t.updateOrientation(Block.COUNTERCLOCKWISE);
            assertString = "\n#**********#\n" +
                    "#**********#\n" +
                    "#****" + 6 + "*****#\n" +
                    "#***" + 6 + 6 + 6 + "****#\n";
            actualString = getTetrisRowsToString(0, 4);
            assertEquals(assertString, actualString);
        }
    }


    public void testRotateRight_LBlock()throws Exception{
        Block l = new Block(Block.L);
        manipulator.addSquaresToGrid(l.squares);
        manipulator.dropSquaresByOne(l.squares);
        manipulator.dropSquaresByOne(l.squares);
        for(int i = 0; i<3; i++) {

            manipulator.offsetSquares(l.squareOffSetsOnRotate(Block.CLOCKWISE));
            l.updateOrientation(Block.CLOCKWISE);
            String shouldBe =
                    "\n#**********#\n" +
                            "#****" + 3 + "*****#\n" +
                            "#****" + 3 + "*****#\n" +
                            "#****" + 3 + 3 + "****#\n";
            String actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(l.squareOffSetsOnRotate(Block.CLOCKWISE));
            l.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                    "\n#**********#\n" +
                            "#**********#\n" +
                            "#***" + 3 + 3 + 3 + "****#\n" +
                            "#***" + 3 + "******#\n";
            actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(l.squareOffSetsOnRotate(Block.CLOCKWISE));
            l.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                    "\n#**********#\n" +
                            "#***" + 3 + 3 + "*****#\n" +
                            "#****" + 3 + "*****#\n" +
                            "#****" + 3 + "*****#\n";
            actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(l.squareOffSetsOnRotate(Block.CLOCKWISE));
            l.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                    "\n#**********#\n" +
                            "#**********#\n" +
                            "#*****" + 3 + "****#\n" +
                            "#***" + 3 + 3 + 3 + "****#\n";
            actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
        }
    }
    public void testRotateRight_JBlock()throws Exception{
        Block b = new Block(Block.J);
        manipulator.addSquaresToGrid(b.squares);
        manipulator.dropSquaresByOne(b.squares);
        manipulator.dropSquaresByOne(b.squares);
        for(int i = 0; i<3; i++) {
            manipulator.offsetSquares(b.squareOffSetsOnRotate(Block.CLOCKWISE));
            b.updateOrientation(Block.CLOCKWISE);
            String shouldBe =
                    "\n#**********#\n" +
                            "#****" + 2 + 2 + "****#\n" +
                            "#****" + 2 + "*****#\n" +
                            "#****" + 2 + "*****#\n";

            String actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(b.squareOffSetsOnRotate(Block.CLOCKWISE));
            b.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                    "\n#**********#\n" +
                            "#**********#\n" +
                            "#***"+2+2+2+"****#\n" +
                              "#*****"+2+"****#\n";
                    actualStr = getTetrisRowsToString(0, 4);

            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(b.squareOffSetsOnRotate(Block.CLOCKWISE));
            b.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                    "\n#**********#\n" +
                              "#****" + 2 + "*****#\n" +
                              "#****" + 2 + "*****#\n" +
                            "#***" +2 + 2 + "*****#\n";
            actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
            manipulator.offsetSquares(b.squareOffSetsOnRotate(Block.CLOCKWISE));
            b.updateOrientation(Block.CLOCKWISE);
            shouldBe =
                          "\n#**********#\n" +
                            "#**********#\n" +
                            "#***" + 2 + "******#\n" +
                            "#***" + 2 + 2 + 2 + "****#\n";
            actualStr = getTetrisRowsToString(0, 4);
            assertEquals(shouldBe, actualStr);
        }


    }

    public void testRotate_IBlock()throws Exception{

        Block b = new Block(Block.I);
        manipulator.addSquaresToGrid(b.squares);
        manipulator.dropSquaresByOne(b.squares);
        manipulator.dropSquaresByOne(b.squares);

        String upRight =
                "\n#**********#\n" +
                "#*****" + 1 + "****#\n" +
                "#*****" + 1 + "****#\n" +
                "#*****" + 1 + "****#\n"+
                "#*****" + 1 + "****#\n";
        String flatt =
                "\n#**********#\n" +
                    "#**********#\n" +
                    "#***"+1+1+1+1+"***#\n" +
                    "#**********#\n"+
                    "#**********#\n";
        String [] positions = {upRight, flatt};
        int [] turns = {Block.CLOCKWISE,Block.COUNTERCLOCKWISE, Block.COUNTERCLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE};
        for (int i = 0; i<turns.length; i++){
            manipulator.offsetSquares(b.squareOffSetsOnRotate(turns[i]));
            //manipulator.rearrangeBlocksSquares(b, b.positionAfterTurn(turns[i]));
            b.updateOrientation(turns[i]);
            String actualStr = getTetrisRowsToString(0, 5);
            assertEquals(positions[i%2], actualStr);
        }
    }

    public void testRotate_SBlock()throws Exception{
        Block b = new Block(Block.S);
        manipulator.addSquaresToGrid(b.squares);
        manipulator.dropSquaresByOne(b.squares);
        manipulator.dropSquaresByOne(b.squares);

        String upRight =
                "\n#**********#\n" +
                        "#***" + 5 + "******#\n" +
                        "#***" + 5+5 + "*****#\n"+
                         "#****" + 5 + "*****#\n"+
                        "#**********#\n";
        String flatt =
                "\n#**********#\n" +
                        "#**********#\n" +
                         "#****"+5+5+"****#\n"+
                        "#***"+5+5+"*****#\n"+
                        "#**********#\n";
        String [] positions = {upRight, flatt};
        int [] turns = {Block.CLOCKWISE,Block.COUNTERCLOCKWISE, Block.COUNTERCLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE};
        for (int i = 0; i<turns.length; i++){
            manipulator.offsetSquares(b.squareOffSetsOnRotate(turns[i]));
            //manipulator.rearrangeBlocksSquares(b, b.positionAfterTurn(turns[i]));
            b.updateOrientation(turns[i]);
            //String desc = b.squares.get(0).location + " : " + b.squares.get(1).location + " : " + b.squares.get(2).location + " : " + b.squares.get(3).location;
            String actualStr = getTetrisRowsToString(0, 5);
            assertEquals(positions[i%2], actualStr);
        }
    }

    public void testRotate_ZBlock()throws Exception{
        Block b = new Block(Block.Z);
        manipulator.addSquaresToGrid(b.squares);
        manipulator.dropSquaresByOne(b.squares);
        manipulator.dropSquaresByOne(b.squares);

        String upRight =
                "\n#**********#\n" +
                        "#*****" + 0+ "****#\n" +
                        "#****" +0+0 + "****#\n"+
                        "#****" +0+ "*****#\n"+
                        "#**********#\n";
        String flatt =
                "\n#**********#\n" +
                        "#**********#\n" +
                        "#***"+0+0+"*****#\n"+
                         "#****"+0+0+"****#\n"+
                        "#**********#\n";
        String [] positions = {upRight, flatt};
        int [] turns = {Block.CLOCKWISE,Block.COUNTERCLOCKWISE, Block.COUNTERCLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE,Block.CLOCKWISE};
        for (int i = 0; i<turns.length; i++){
            //manipulator.rearrangeBlocksSquares(b, b.positionAfterTurn(turns[i]));
            manipulator.offsetSquares(b.squareOffSetsOnRotate(turns[i]));
            b.updateOrientation(turns[i]);
            //String desc = b.squares.get(0).location + " : " + b.squares.get(1).location + " : " + b.squares.get(2).location + " : " + b.squares.get(3).location;
            String actualStr = getTetrisRowsToString(0, 5);
            assertEquals(positions[i%2], actualStr);
        }
    }


    //O block should not be visible affected when rotated
    public void testRotate_OBlock()throws Exception{
        Block o = new Block(Block.O);
        manipulator.addSquaresToGrid(o.squares);
        manipulator.dropSquaresByOne(o.squares);
        manipulator.dropSquaresByOne(o.squares);
        manipulator.offsetSquares(o.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
        String assertString =
                "\n#**********#\n" +
                        "#**********#\n" +
                        "#****"+4+4+"****#\n"+
                        "#****"+4+4+"****#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 4));
        for (int i = 0; i < 3; i++) {
            manipulator.offsetSquares(o.squareOffSetsOnRotate(Block.CLOCKWISE));
            o.updateOrientation(Block.CLOCKWISE);
            assertEquals(assertString, getTetrisRowsToString(0, 4));
        }
    }



    public void testRotateDifferentDirections_TBlock() throws Exception {
        Block t = new Block(Block.T);
        manipulator.addSquaresToGrid(t.squares);
        manipulator.dropSquaresByOne(t.squares);
        manipulator.dropSquaresByOne(t.squares);
        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
        t.updateOrientation(Block.CLOCKWISE);
        String assertString =
                "\n#**********#\n" +
                        "#****" + 6 + "*****#\n" +
                        "#****" + 6 + 6 + "****#\n" +
                        "#****" + 6 + "*****#\n";
        String actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);

        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
        t.updateOrientation(Block.COUNTERCLOCKWISE);
        assertString = "\n#**********#\n" +
                "#**********#\n" +
                "#****" + 6 + "*****#\n" +
                "#***" + 6 + 6 + 6 + "****#\n";

        actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);


        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
        t.updateOrientation(Block.COUNTERCLOCKWISE);
        assertString = "\n#**********#\n" +
                "#****" + 6 + "*****#\n" +
                "#***" + 6 + 6 + "*****#\n" +
                "#****" + 6 + "*****#\n";

        actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);

        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
        t.updateOrientation(Block.COUNTERCLOCKWISE);
        assertString =
                "\n#**********#\n" +
                        "#**********#\n" +
                        "#***" + 6 + 6 + 6 + "****#\n" +
                        "#****" + 6 + "*****#\n";
        actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);


        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.COUNTERCLOCKWISE));
        t.updateOrientation(Block.COUNTERCLOCKWISE);
        assertString =
                "\n#**********#\n" +
                        "#****" + 6 + "*****#\n" +
                        "#****" + 6 + 6 + "****#\n" +
                        "#****" + 6 + "*****#\n";
        actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);

        manipulator.offsetSquares(t.squareOffSetsOnRotate(Block.CLOCKWISE));
        t.updateOrientation(Block.CLOCKWISE);
        assertString =
                "\n#**********#\n" +
                        "#**********#\n" +
                        "#***" + 6 + 6 + 6 + "****#\n" +
                        "#****" + 6 + "*****#\n";
        actualString = getTetrisRowsToString(0, 4);
        assertEquals(assertString, actualString);
    }

    public void testMoveToRight_OBlock()throws Exception{
        Block b1 = new Block(Block.O);
        manipulator.addSquaresToGrid(b1.squares);
        manipulator.moveBlockHorizontally(b1, Block.MOVE_DIRECTION_RIGHT);
        String assertString =
                        "\n#*****"+4+4+"***#\n"+
                        "#*****"+4+4+"***#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));

        tetris.clearGrid();


    }
    public void testMoveToRight_ZBlockMultipleLines()throws Exception{
        Block b1 = new Block(Block.Z);
        manipulator.addSquaresToGrid(b1.squares);
        manipulator.moveBlockHorizontally(b1, Block.MOVE_DIRECTION_RIGHT);
        //manipulator.moveBlockToRight(b1);
        String assertString =
                "\n#****"+0+0+"****#\n"+
                        "#*****"+0+0+"***#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
        manipulator.moveBlockHorizontally(b1, Block.MOVE_DIRECTION_RIGHT);
        //manipulator.moveBlockToRight(b1);
        assertString =
                "\n#*****"+0+0+"***#\n"+
                        "#******"+0+0+"**#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
        manipulator.moveBlockHorizontally(b1, Block.MOVE_DIRECTION_RIGHT);
        //manipulator.moveBlockToRight(b1);
        assertString =
                "\n#******"+0+0+"**#\n"+
                        "#*******"+0+0+"*#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));

        manipulator.moveBlockHorizontally(b1, Block.MOVE_DIRECTION_RIGHT);
        //manipulator.moveBlockToRight(b1);
        assertString =
                "\n#*******"+0+0+"*#\n"+
                        "#********"+0+0+"#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
    }

    public void testMoveToLeft_JBlockMultipleLines()throws Exception{
        Block j = new Block(Block.J);
        manipulator.addSquaresToGrid(j.squares);
        manipulator.moveBlockHorizontally(j, Block.MOVE_DIRECTION_LEFT);
        String assertString =
                "\n#**"+2+"*******#\n"+
                        "#**"+2+2+2+"*****#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
        manipulator.moveBlockHorizontally(j, Block.MOVE_DIRECTION_LEFT);
        assertString =
                "\n#*"+2+"********#\n"+
                        "#*"+2+2+2+"******#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
        manipulator.moveBlockHorizontally(j, Block.MOVE_DIRECTION_LEFT);
        assertString =
                "\n#"+2+"*********#\n"+
                        "#"+2+2+2+"*******#\n";
        assertEquals(assertString, getTetrisRowsToString(0, 2));
    }

    private String getTetrisRowsToString(int from, int to){
        String [] gridStr = tetris.gridToString().trim().split("\n");
        String assertStr = "\n";
        for(int i = from; i<to; i++)
            assertStr+=gridStr[i] + "\n";
        return assertStr;
    }
}
