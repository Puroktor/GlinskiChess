package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;

public class BoardCell {
    public enum CellType {
        DEFAULT,
        REACHABLE,
        CAPTURABLE,
        SELECTED
    }

    private ChessPiece piece;

    private CellType cellType = CellType.DEFAULT;

    public ChessPiece getPiece() {
        return piece;
    }

    void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public CellType getCellType() {
        return cellType;
    }

    void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    BoardCell() {
    }

    public BoardCell(ChessPiece piece) {
        this.piece = piece;
    }
}
