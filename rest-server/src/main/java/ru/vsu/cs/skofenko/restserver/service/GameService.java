package ru.vsu.cs.skofenko.restserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.GameLogic;
import ru.vsu.cs.skofenko.logic.model.LogicState;
import ru.vsu.cs.skofenko.restserver.entity.GamePlayerLogic;
import ru.vsu.cs.skofenko.restserver.repository.LogicRepository;

@Service
@RequiredArgsConstructor
public class GameService {
    private final LogicRepository repository;
    private long clientID = Long.MIN_VALUE;
    private int playersWaiting;

    public synchronized long connect() {
        try {
            if (playersWaiting == 0) {
                playersWaiting++;
                wait();
                return clientID - 2;
            } else {
                GameLogic logic = new GameLogic();
                playersWaiting--;
                repository.saveNewLogic(clientID, new GamePlayerLogic(logic, ChessColor.WHITE));
                repository.saveNewLogic(clientID + 1, new GamePlayerLogic(logic, ChessColor.BLACK));
                clientID += 2;
                notify();
                return clientID - 1;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Exception during being in waiting queue", e);
        }
    }

    public LogicState getLogicState(long key) {
        return repository.getLogicByClientID(key).getLogicState();

    }

    public boolean selectPiece(long key, Coordinate cord) {
        return repository.getLogicByClientID(key).selectPiece(cord);
    }

    public boolean promotePawn(long key, ChessPiece piece) {
        return repository.getLogicByClientID(key).promotePawn(piece);
    }

    public void terminate(long key) {
        if (repository.getLogicByClientID(key).getNowTurn() == ChessColor.WHITE) {
            repository.removeLogicByID(key);
            repository.removeLogicByID(key + 1);
        } else {
            repository.removeLogicByID(key - 1);
            repository.removeLogicByID(key);
        }
    }
}
