package ru.vsu.cs.skofenko.logic.chesspieces;

import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.geometry.Directions;
import ru.vsu.cs.skofenko.logic.model.GameLogic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class King extends ChessPiece {
    public King(ChessColor color) {
        super(color);
    }

    @Override
    protected Collection<Coordinate> getPossibleCells(GameLogic logic, boolean toGo) {
        Set<Coordinate> set = new HashSet<>();
        for (Directions d : Directions.values()) {
            Coordinate cord = nowCord.copy();
            cord.plus(d);
            if (cord.isInBounds()) {
                if (logic.getPiece(cord) == null && toGo) {
                    set.add(cord);
                } else if (logic.getPiece(cord) != null && logic.getPiece(cord).color != color && !toGo) {
                    set.add(cord);
                }
            }
        }
        return set;
    }

    private King(){
    }
}
