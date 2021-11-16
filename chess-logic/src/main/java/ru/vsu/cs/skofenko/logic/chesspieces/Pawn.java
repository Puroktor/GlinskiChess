package ru.vsu.cs.skofenko.logic.chesspieces;

import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.geometry.Directions;
import ru.vsu.cs.skofenko.logic.model.GameLogic;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class Pawn extends ChessPiece {
    private boolean notMoved = true;

    public boolean hasNotMoved() {
        return notMoved;
    }

    public void moved() {
        this.notMoved = false;
    }

    @Override
    protected Collection<Coordinate> getPossibleCells(GameLogic logic, boolean toGo) {
        Set<Coordinate> set = new HashSet<>();
        if (toGo) {
            Directions d = (color == ChessColor.WHITE) ? Directions.UP : Directions.DOWN;
            Coordinate cord = nowCord.copy();
            for (int k = 0; (k < 1) || (k < 2 && notMoved); k++) {
                cord.plus(d);
                if (cord.isInBounds() && logic.getPiece(cord) == null) {
                    set.add(cord.copy());
                } else {
                    break;
                }
            }
        } else {
            EnumSet<Directions> enumSet;
            if (color == ChessColor.WHITE) {
                enumSet = EnumSet.of(Directions.UP_LEFT, Directions.UP_RIGHT);
            } else {
                enumSet = EnumSet.of(Directions.DOWN_LEFT, Directions.DOWN_RIGHT);
            }
            for (Directions d : enumSet) {
                Coordinate cord = nowCord.copy();
                cord.plus(d);
                if (cord.isInBounds() && (logic.getPiece(cord) != null && logic.getPiece(cord).color != color
                        || cord.equals(logic.getEnPasCord())))
                    set.add(cord);
            }
        }
        return set;
    }

    public Pawn(ChessColor color) {
        super(color);
    }

    private Pawn(){
    }
}
