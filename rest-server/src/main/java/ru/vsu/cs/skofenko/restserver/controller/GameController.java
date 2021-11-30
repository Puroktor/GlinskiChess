package ru.vsu.cs.skofenko.restserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.LogicState;
import ru.vsu.cs.skofenko.restserver.service.GameService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/logic")
    public long connect() {
        return gameService.connect();
    }

    @GetMapping("/logic/{key}")
    public LogicState getLogicState(@PathVariable long key) {
        return gameService.getLogicState(key);

    }

    @PutMapping("/select/{key}")
    public boolean selectPiece(@PathVariable long key, @RequestBody Coordinate cord) {
        return gameService.selectPiece(key, cord);
    }

    @PutMapping("/promote/{key}")
    public boolean promotePawn(@PathVariable long key, @RequestBody ChessPiece piece) {
        return gameService.promotePawn(key, piece);
    }

    @PostMapping("/logic/{key}")
    public void terminate(@PathVariable long key) {
        gameService.terminate(key);
    }
}
