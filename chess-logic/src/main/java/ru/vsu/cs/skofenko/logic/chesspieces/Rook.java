package ru.vsu.cs.skofenko.logic.chesspieces;

import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.GameLogic;

import java.util.Collection;

public class Rook extends ChessPiece {
    public Rook(ChessColor color) {
        super(color);
    }

    @Override
    public Collection<Coordinate> getPossibleCells(GameLogic logic, boolean toGo) {
        return getCellsOfStraightPieces(logic, this, true, toGo);
    }

    private Rook(){
    }
}
