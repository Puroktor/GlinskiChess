package ru.vsu.cs.skofenko.restserver.repository;

import org.springframework.stereotype.Repository;
import ru.vsu.cs.skofenko.restserver.entity.GamePlayerLogic;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class LogicRepository {
    private final Map<Long, GamePlayerLogic> logicMap = new HashMap<>();

    public GamePlayerLogic saveNewLogic(Long clientID, GamePlayerLogic logic) {
        return logicMap.put(clientID, logic);
    }

    public GamePlayerLogic getLogicByClientID(Long clientID) {
        GamePlayerLogic logic = logicMap.get(clientID);
        if (logic != null) {
            return logic;
        } else {
            throw new NoSuchElementException("No user is registered with that ID!");
        }
    }

    public GamePlayerLogic removeLogicByID(Long clientID) {
        return logicMap.remove(clientID);
    }
}
