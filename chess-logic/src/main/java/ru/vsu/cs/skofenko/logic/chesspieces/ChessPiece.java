package ru.vsu.cs.skofenko.logic.chesspieces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.geometry.Directions;
import ru.vsu.cs.skofenko.logic.model.GameLogic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Bishop.class, name = "bishop"),
        @JsonSubTypes.Type(value = King.class, name = "king"),
        @JsonSubTypes.Type(value = Knight.class, name = "knight"),
        @JsonSubTypes.Type(value = Pawn.class, name = "pawn"),
        @JsonSubTypes.Type(value = Queen.class, name = "queen"),
        @JsonSubTypes.Type(value = Rook.class, name = "rook"),
})
public abstract class ChessPiece {
    protected ChessColor color;

    protected Coordinate nowCord;
    protected Collection<Coordinate> possibleGoCells;
    protected Collection<Coordinate> possibleCaptureCells;

    public Coordinate getNowCord() {
        return nowCord;
    }

    public void setNowCord(Coordinate nowCord) {
        this.nowCord = nowCord;
    }

    public Collection<Coordinate> getPossibleGoCells() {
        return possibleGoCells;
    }

    public void setPossibleCells(GameLogic logic, boolean toGo) {
        if (toGo) {
            possibleGoCells = getPossibleCells(logic, true);
        } else {
            possibleCaptureCells = getPossibleCells(logic, false);
        }
    }

    public Collection<Coordinate> getPossibleCaptureCells() {
        return possibleCaptureCells;
    }

    public void setPossibleCaptureCells(Collection<Coordinate> possibleCaptureCells) {
        this.possibleCaptureCells = possibleCaptureCells;
    }

    public ChessColor getColor() {
        return color;
    }

    protected abstract Collection<Coordinate> getPossibleCells(GameLogic logic, boolean toGo);

    protected static Collection<Coordinate> getCellsOfStraightPieces(GameLogic logic, ChessPiece piece, boolean isOrthogonal, boolean toGo) {
        Set<Coordinate> set = new HashSet<>();
        for (Directions d : (isOrthogonal) ? Directions.getOrthogonal() : Directions.getDiagonal()) {
            Coordinate cord = piece.getNowCord().copy();
            cord.plus(d);
            while (cord.isInBounds()) {
                if (logic.getPiece(cord) != null) {
                    if (logic.getPiece(cord).color != piece.getColor() && !toGo) {
                        set.add(cord.copy());
                    }
                    break;
                } else if (toGo) {
                    set.add(cord.copy());
                }
                cord.plus(d);
            }
        }
        return set;
    }

    //except King and Pawn
    public static ChessPiece getChessPieceFromStr(String str, ChessColor color) {
        if (str == null) {
            throw new IllegalArgumentException("String is invalid");
        }

        switch (str) {
            case "Rook":
                return new Rook(color);
            case "Bishop":
                return new Bishop(color);
            case "Knight":
                return new Knight(color);
            case "Queen":
                return new Queen(color);
            default:
                throw new IllegalArgumentException("String is invalid");
        }
    }

    public ChessPiece(ChessColor color) {
        this.color = color;
    }

    protected ChessPiece() {
    }
}
