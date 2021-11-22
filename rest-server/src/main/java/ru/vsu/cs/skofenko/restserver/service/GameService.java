package ru.vsu.cs.skofenko.restserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.GameLogic;
import ru.vsu.cs.skofenko.logic.model.LogicState;
import ru.vsu.cs.skofenko.restserver.entity.MultiPlayerLogic;
import ru.vsu.cs.skofenko.restserver.repository.LogicRepository;

@Service
@RequiredArgsConstructor
public class GameService {
    private final LogicRepository repository;
    private long clientID = Long.MIN_VALUE;

    public long connect() {
        long id = clientID++;
        if (id % 2 == 0) {
            repository.saveNewLogic(id, new MultiPlayerLogic(ChessColor.WHITE));
        } else {
            GameLogic logic = new GameLogic();
            repository.getLogicByClientID(id - 1).setLogic(logic);
            MultiPlayerLogic multiLogic = new MultiPlayerLogic(ChessColor.BLACK);
            multiLogic.setLogic(logic);
            repository.saveNewLogic(id, multiLogic);
        }
        return id;
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
        if (key % 2 == 0) {
            repository.removeLogicByID(key);
            repository.removeLogicByID(key + 1);
        } else {
            repository.removeLogicByID(key - 1);
            repository.removeLogicByID(key);
        }
    }
}
