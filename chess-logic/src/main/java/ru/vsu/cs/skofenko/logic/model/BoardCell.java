package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;

public class BoardCell {
    private ChessPiece piece;
    private boolean reachable = false;
    private boolean capturable = false;

    public ChessPiece getPiece() {
        return piece;
    }

    void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public boolean isReachable() {
        return reachable;
    }

    public boolean isCapturable() {
        return capturable;
    }

    void setCapturable(boolean capturable) {
        this.capturable = capturable;
    }

    void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    BoardCell(){}

    public BoardCell(ChessPiece piece) {
        this.piece = piece;
    }
}
