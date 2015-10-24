package com.example.winnabuska.tetristddpractice.TetrisLogic;

import android.graphics.Point;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by WinNabuska on 18.10.2015.
 * In game all squares are part of a block. A square can have non or many squares that it is attached to.
 * Square has a color that is assigned by the Block. Squares are in a 22x10 grid. Squares always know they location in the grid
 */
public class Square {
    public final static int COLOR_RED = 0, COLOR_CYAN = 1, COLOR_BLUE = 2, COLOR_ORANGE = 3, COLOR_YELLOR = 4, COLOR_GREEN = 5, COLOR_PURPLE = 6, SHADOW = 7;

    public final int COLOR;
    private Set<Square> attachedTo;
    Point location;

    public Square(Point location, final int COLOR) {
        this.COLOR = COLOR;
        this.location = location;
        attachedTo = new HashSet<>();
    }

    public boolean isShadowSquare(){
        return COLOR == 7;
    }

    public Square attachTo(Square... squares){
        Stream.of(squares).forEach(block -> {
            attachedTo.add(block);
            block.attachedTo.add(Square.this);
        });
        return squares[squares.length-1];
    }

    public Set<Square> getAttachedTo(){
        return attachedTo;
    }

    public Optional<Square> detachOneSelf(){
        Stream.of(attachedTo).forEach(block -> block.attachedTo.remove(this));
        attachedTo.clear();
        return Optional.empty();
    }

    public Set<Square> getAllSquaresInTheBlock(){
        Set<Square> squareGroup = Stream.of(attachedTo)
                .flatMap(block -> Stream.of(block.collectIndirectlyAttachedBlocks(new HashSet<>(Arrays.asList(this, block)))))
                .collect(Collectors.toSet());
        squareGroup.add(this);
        return squareGroup;
    }

    public Set<Square> getAllSquaresInTheBlockExcludingSelf(){
        Set<Square> squareGroup = getAllSquaresInTheBlock();
        squareGroup.remove(this);
        return squareGroup;
    }

    private Set<Square> collectIndirectlyAttachedBlocks(Set<Square> squares){
        if(attachedTo.size()==0 || squares.containsAll(attachedTo)){
            return squares;
        }
        else{
            Set<Square> upgrades = Stream.of(attachedTo).filter(block -> !squares.contains(block)).collect(Collectors.toSet());
            squares.addAll(upgrades);
            return Stream.of(upgrades).flatMap(block -> Stream.of(block.collectIndirectlyAttachedBlocks(squares))).collect(Collectors.toSet());
        }
    }
}
