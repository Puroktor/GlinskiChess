package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;

public class LogicState {
    private ChessColor nowTurn;
    private GameState gameState;
    private BoardCell[][] board;

    public ChessColor getNowTurn() {
        return nowTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public BoardCell[][] getBoard() {
        return board;
    }

    public LogicState(ChessColor nowTurn, GameState gameState, BoardCell[][] board) {
        this.nowTurn = nowTurn;
        this.gameState = gameState;
        this.board = board;
    }

    private LogicState() {

    }
}
