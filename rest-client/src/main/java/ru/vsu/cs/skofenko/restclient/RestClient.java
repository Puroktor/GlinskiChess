package ru.vsu.cs.skofenko.restclient;

import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.BoardCell;
import ru.vsu.cs.skofenko.logic.model.GameState;
import ru.vsu.cs.skofenko.logic.model.IGameLogic;
import ru.vsu.cs.skofenko.logic.model.LogicState;

import javax.swing.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RestClient implements IGameLogic {
    public static final String SERVER_URL = "http://localhost:8080/api";

    private static final RestTemplate TEMPLATE = new RestTemplate();

    private final Long SESSION_ID;
    private final Timer timer = new Timer();
    private final Consumer<String> returnToSingleFunc;
    private LogicState logicState;

    public RestClient(Consumer<String> returnToSingleFunc, Runnable repaintFunc) {
        this.returnToSingleFunc = returnToSingleFunc;
        try {
            SESSION_ID = TEMPLATE.postForObject(String.format("%s/logic", SERVER_URL), null, Long.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            returnToSingleFunc.accept("no-connection");
            throw e;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogicState previousState = logicState;
                updateLogicState();
                if (!Objects.equals(previousState, logicState)) {
                    SwingUtilities.invokeLater(repaintFunc);
                }
            }
        }, 1000, 1000);
        updateLogicState();
    }

    private void updateLogicState() {
        try {
            logicState = TEMPLATE.getForObject(String.format("%s/logic/{0}", SERVER_URL), LogicState.class, SESSION_ID);
        } catch (HttpStatusCodeException e) {
            returnToSingleFunc.accept("player-left");
            timer.cancel();
        } catch (ResourceAccessException e) {
            returnToSingleFunc.accept("no-connection");
            timer.cancel();
            throw e;
        }
    }

    @Override
    public ChessColor getNowTurn() {
        return logicState.getNowTurn();
    }

    @Override
    public GameState getGameState() {
        return logicState.getGameState();
    }

    @Override
    public Coordinate getSelectedCord() {
        return logicState.getSelectedCoordinate();
    }

    @Override
    public BoardCell[][] getBoard() {
        return logicState.getBoard();
    }

    @Override
    public boolean selectPiece(Coordinate coordinate) {
        try {
            ResponseEntity<Boolean> response = TEMPLATE.exchange(
                    String.format("%s/select/{0}", SERVER_URL),
                    HttpMethod.PUT,
                    getHttpEntity(coordinate),
                    Boolean.class,
                    SESSION_ID);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new HttpServerErrorException(response.getStatusCode());
            }
            updateLogicState();
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpStatusCodeException e) {
            returnToSingleFunc.accept("player-left");
            timer.cancel();
        } catch (ResourceAccessException e) {
            returnToSingleFunc.accept("no-connection");
            timer.cancel();
            throw e;
        }
        return false;
    }

    @Override
    public boolean promotePawn(ChessPiece piece) {
        try {
            ResponseEntity<Boolean> response = TEMPLATE.exchange(
                    String.format("%s/promote/{0}", SERVER_URL),
                    HttpMethod.PUT,
                    getHttpEntity(piece),
                    Boolean.class,
                    SESSION_ID);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new HttpServerErrorException(response.getStatusCode());
            }
            updateLogicState();
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpStatusCodeException e) {
            returnToSingleFunc.accept("player-left");
            timer.cancel();
        } catch (ResourceAccessException e) {
            returnToSingleFunc.accept("no-connection");
            timer.cancel();
            throw e;
        }
        return false;
    }

    public void terminate() {
        try {
            TEMPLATE.delete(String.format("%s/logic/{0}", SERVER_URL), SESSION_ID);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            returnToSingleFunc.accept("no-connection");
            throw e;
        } finally {
            timer.cancel();
        }
    }

    private <T> HttpEntity<T> getHttpEntity(T value) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(value, headers);
    }
}
