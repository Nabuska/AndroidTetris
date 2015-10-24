package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.example.winnabuska.tetristddpractice.Control.TetrisController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joona Enbuska on 19.10.2015.
 */
public class GridSpaceEvaluator {

    private Predicate<Point> isWithinGridRange = p -> p.x>=0 && p.y>=0 && p.y< TetrisController.ROWS && p.x< TetrisController.COLUMNS;
    private Predicate<Point> isEmptyGridPoint = p -> !gridValue(p).isPresent() ||gridValue(p).get().isShadowSquare();

    /* Takes a Map an checks if all Square can be safely offset by the Point x y values*/
    public boolean isSafeOffset(Map<Square, Point> offset){
        Predicate<Point> isPartOfSameBlock = p -> Stream.of(offset.keySet()).anyMatch(s -> new Point(s.location.x, s.location.y).equals(p));
        List<Point> positionsAfterRotate = Stream.of(offset).map(e -> new Point(e.getKey().location.x + e.getValue().x, e.getKey().location.y + e.getValue().y)).collect(Collectors.toList());
        return Stream.of(positionsAfterRotate).allMatch(p -> isWithinGridRange.test(p) && (isEmptyGridPoint.test(p) || isPartOfSameBlock.test(p)));
    }

    /* Returns a set os 'shadow' squares that represent the location where the Block will fall*/
    public Set<Square> getBlockShadow(Block block){
        Set<Point> pointsUnderSquares;
        if(!squaresHaveRoomBelow(block.squares))
            pointsUnderSquares = new HashSet<>();
        else {
            pointsUnderSquares = Stream.of(block.squares).map(s -> new Point(s.location.x, s.location.y)).collect(Collectors.toSet());
            Predicate<Point> gridIsOutOfRows = p -> p.y > TetrisController.grid.length - 1;
            Predicate<Point> partOfSameGroup = p -> Stream.of(block.squares).anyMatch(s -> s.location.equals(p.x, p.y));
            while (Stream.of(pointsUnderSquares).noneMatch(p -> gridIsOutOfRows.test(p)) &&
                    Stream.of(pointsUnderSquares).noneMatch(p -> !isEmptyGridPoint.test(p) && !partOfSameGroup.test(p))) {
                Stream.of(pointsUnderSquares).forEach(p -> p.offset(0, 1));
            }
            Stream.of(pointsUnderSquares).forEach(p -> p.offset(0, -1));
        }
        return Stream.of(pointsUnderSquares).map(p -> new Square(p, Square.SHADOW)).collect(Collectors.toSet());
    }

    /* Returns true when in every rows column there is a block that is NOT floating*/
    public boolean isFilledRow(int row) {
        Set<Square> stableSquares = getAllStableSquares();
        return Stream.of(TetrisController.grid[row]).allMatch(optS -> optS.isPresent() && !optS.get().isShadowSquare() && stableSquares.contains(optS.get()));
    }

    /*Floating squares are squars that have not landed yet*/
    public List<Square> getAllFloatingSquares() {
        Set<Square> stableSquares = getAllStableSquares();
        return Stream.of(TetrisController.grid).flatMap(row ->
                Stream.of(row).filter(s -> s.isPresent() && !stableSquares.contains(s.get())))
                .map(optSquare -> ((Optional<Square>) optSquare).get()).collect(Collectors.toList());
    }


    /*Returns a set of squares 'stable' squares. That means that the squares are on the ground or
    on top of squares that are on the ground or connected to squares fill one of the requirements before.*/
    public Set<Square> getAllStableSquares() {
        Set<Square> prevTopSquares = getBottomBlocks();
        Set<Square> stableBlocks = new HashSet<>(prevTopSquares);
        Set<Square> nextTopSquares = new HashSet<>();
        do {
            nextTopSquares.clear();
            for (Square square : prevTopSquares) {
                Optional<Square> onTopOffBlock = getBlockOnTopOff(square);
                if (onTopOffBlock.isPresent() && !onTopOffBlock.get().isShadowSquare() && !stableBlocks.contains(onTopOffBlock.get())) {
                    HashSet<Square> newSquares = new HashSet<>(onTopOffBlock.get().getAllSquaresInTheBlock());
                    nextTopSquares.addAll(newSquares);
                }
            }
            prevTopSquares.clear();
            prevTopSquares.addAll(nextTopSquares);
            stableBlocks.addAll(nextTopSquares);
        }while(!nextTopSquares.isEmpty());
        return stableBlocks;
    }


    /* Checks if the locations of the squares are not occupied*/
    public boolean squareLocationsEmpty(List<Square> squares) {
        return Stream.of(squares).allMatch(s -> isEmptyGridPoint.test(s.location));
    }

    public boolean squaresHaveRoomBelow(List<Square> squares) {
        for(int i = 0; i<squares.size(); i++){
            Point p = squares.get(i).location;
            Point below = new Point(p.x, p.y+1);
            if(!isWithinGridRange.test(below))
                return false;
            Optional<Square> belowSquare = gridValue(below);
            if(belowSquare.isPresent() && !belowSquare.get().isShadowSquare() && !squares.contains(belowSquare.get())){
                return false;
            }
        }
        return true;
    }

    public boolean blockHasRoomAtHorizontal(Block block, final int MOVE_DIRECTION) {
        for(int i = 0; i<block.squares.size(); i++){
            Point p = block.squares.get(i).location;
            Point side = new Point(p.x+MOVE_DIRECTION, p.y);
            if(!isWithinGridRange.test(side))
                return false;
            Optional<Square> toRightSquare = gridValue(side);
            if(toRightSquare.isPresent() && !toRightSquare.get().isShadowSquare() && !block.squares.contains(toRightSquare.get())){
                return false;
            }
        }
        return true;
    }

    private Optional<Square> getBlockOnTopOff(Square square){
        if(square.location.y==0)
            return Optional.empty();
        else
            return TetrisController.grid[square.location.y-1][square.location.x];
    }

    private Set<Square> getBottomBlocks(){
        Set<Square> blockSquares = new HashSet<>();
        for (int x = 0; x < TetrisController.COLUMNS; x++) {
            int a = x;
            Optional<Square> optSquare = TetrisController.grid[TetrisController.ROWS - 1][x];
            if (optSquare.isPresent()) {
                Square b = optSquare.get();
                if(!blockSquares.contains(b))
                    blockSquares.addAll(b.getAllSquaresInTheBlock());
            }
        }
        return blockSquares;
    }

    private Optional<Square> gridValue(Point p){
        return TetrisController.grid[p.y][p.x];
    }

}
