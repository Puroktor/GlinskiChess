package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;

public interface IGameLogic{
    int N = 11;

    ChessColor getNowTurn();

    GameState getGameState();

    Coordinate getSelectedCord();

    BoardCell[][] getBoard();

    boolean selectPiece(Coordinate cord);

    boolean promotePawn(ChessPiece piece);
}
