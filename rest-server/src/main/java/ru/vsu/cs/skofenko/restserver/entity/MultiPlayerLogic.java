package ru.vsu.cs.skofenko.restserver.entity;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.*;

@RequiredArgsConstructor
@Setter
public class MultiPlayerLogic implements IGameLogic {
    private final ChessColor color;
    private GameLogic logic;

    @Override
    public GameState getGameState() {
        if (logic.getNowTurn() == color) {
            return logic.getGameState();
        } else {
            if (logic.getGameState() == GameState.STALEMATE) {
                return GameState.STALEMATE;
            }
            if (logic.getGameState() == GameState.CHECKMATE) {
                return GameState.VICTORY;
            }
            return GameState.PLAYING;
        }
    }

    @Override
    public ChessColor getNowTurn() {
        return color;
    }

    @Override
    public boolean selectPiece(Coordinate cord) {
        return logic.getNowTurn() == color && logic.selectPiece(cord);
    }

    @Override
    public boolean promotePawn(ChessPiece piece) {
        return logic.getNowTurn() == color && logic.promotePawn(piece);
    }

    @Override
    public Coordinate getSelectedCord() {
        return logic.getNowTurn() == color ? logic.getSelectedCord() : Coordinate.EMPTY_CORD;
    }

    @Override
    public BoardCell[][] getBoard() {
        BoardCell[][] board = logic.getBoard();
        if (logic.getNowTurn() == color) {
            return board;
        } else {
            BoardCell[][] boardRepresent = new BoardCell[IGameLogic.N][IGameLogic.N];
            for (int i = 0; i < IGameLogic.N; i++) {
                for (int j = 0; j < IGameLogic.N; j++) {
                    boardRepresent[i][j] = new BoardCell(board[i][j].getPiece());
                }
            }
            return boardRepresent;
        }
    }

    @Nullable
    public LogicState getLogicState() {
        if (logic == null) {
            return null;
        } else {
            return new LogicState(getNowTurn(), getGameState(), getSelectedCord(), getBoard());
        }
    }
}
