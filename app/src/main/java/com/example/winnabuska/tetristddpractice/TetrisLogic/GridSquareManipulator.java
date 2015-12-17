package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Joona Enbuska on 19.10.2015.
 */
public class GridSquareManipulator {

    Optional<Square>[][] grid;

    public GridSquareManipulator(Optional<Square>[][] grid){
        this.grid = grid;
    }

    public void destroyRow(int row){
        for(int x = 0; x< TetrisModel.COLUMNS; x++){
            grid[row][x].get().detachOneSelf();
            grid[row][x] = Optional.empty();
        }
    }

    public void removeGridValueAtSquarePoints(Collection<Square> squares){
        Stream.of(squares).forEach(s -> removeGridValueAtPoint(s.location));
    }

    public void dropSquaresByOne(List<Square> squares) {
        Comparator<Square> fromDownToUp = (b1, b2) -> b2.location.y-b1.location.y;
        List<Square> copy = Stream.of(squares).sorted(fromDownToUp).collect(Collectors.toList());
        for(Square s : copy){
            removeSquareFromGrid(s);
            s.location.offset(0,1);
            addSquareToGrid(s);
        }
    }

    public void offsetSquares(Map<Square, Point> squareOffsets){
        Stream.of(squareOffsets.keySet()).forEach(s -> removeSquareFromGrid(s));
        Stream.of(squareOffsets).forEach(e -> e.getKey().location.offset(e.getValue().x, e.getValue().y));
        Stream.of(squareOffsets.keySet()).forEach(s -> addSquareToGrid(s));
    }

    public void moveBlockHorizontally(Block block, final int MOVE_DIRECTION){
        Comparator<Square> horizontalIterationOrder;
        if(MOVE_DIRECTION == Block.MOVE_DIRECTION_RIGHT)
            horizontalIterationOrder = (b1, b2) -> b2.location.x-b1.location.x;
        else //MOVE_DIRECTION_LEFT
            horizontalIterationOrder = (b1, b2) -> b1.location.x-b2.location.x;
        List<Square> blockSquareCopy = Stream.of(block.squares).sorted(horizontalIterationOrder).collect(Collectors.toList());
        for(Square s : blockSquareCopy){
            removeSquareFromGrid(s);
            s.location.offset(MOVE_DIRECTION,0);
            addSquareToGrid(s);
        }
    }

    public void addSquaresToGrid(Collection<Square> squares) {
        Stream.of(squares).forEach(s -> addSquareToGrid(s));
    }

    private void addSquareToGrid(Square square){
        grid[square.location.y][square.location.x] = Optional.of(square);
    }

    private void removeSquareFromGrid(Square square){
        Optional<Square> gridContent = grid[square.location.y][square.location.x];
        if(gridContent.isPresent() && gridContent.get()==square)
            grid[square.location.y][square.location.x] = Optional.empty();
    }

    private void removeGridValueAtPoint(Point p){
        grid[p.y][p.x] = Optional.empty();
    }
}
