package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;

import java.util.Arrays;
import java.util.Objects;

public class LogicState {
    private ChessColor nowTurn;
    private GameState gameState;
    private Coordinate selectedCoordinate;
    private BoardCell[][] board;

    public ChessColor getNowTurn() {
        return nowTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Coordinate getSelectedCoordinate() {
        return selectedCoordinate;
    }

    public BoardCell[][] getBoard() {
        return board;
    }

    public LogicState(ChessColor nowTurn, GameState gameState, Coordinate selectedCoordinate, BoardCell[][] board) {
        this.nowTurn = nowTurn;
        this.gameState = gameState;
        this.selectedCoordinate = selectedCoordinate;
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicState that = (LogicState) o;
        return nowTurn == that.nowTurn && gameState == that.gameState && Objects.equals(selectedCoordinate, that.selectedCoordinate)
                && Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nowTurn, gameState, selectedCoordinate);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    private LogicState() {

    }
}
