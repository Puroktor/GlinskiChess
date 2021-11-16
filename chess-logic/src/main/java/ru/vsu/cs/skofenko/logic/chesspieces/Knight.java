package ru.vsu.cs.skofenko.logic.chesspieces;

import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.GameLogic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Knight extends ChessPiece {
    public Knight(ChessColor color) {
        super(color);
    }

    @Override
    protected Collection<Coordinate> getPossibleCells(GameLogic logic, boolean toGo) {
        Set<Coordinate> set = new HashSet<>();
        int r0 = nowCord.getR(), q0 = nowCord.getQ();
        int y0 = -r0 - q0;
        for (int i = Math.max(r0 - 3, -GameLogic.N / 2); i <= Math.min(r0 + 3, GameLogic.N / 2); i++) {
            for (int k = Math.max(q0 - 3, -GameLogic.N / 2); k <= Math.min(q0 + 3, GameLogic.N / 2); k++) {
                int j = -k - i;
                Coordinate to = Coordinate.createFromAxial(i, k);
                if (Math.abs(i + k) <= GameLogic.N / 2 && i != r0 && j != y0 && k != q0 && nowCord.distanceFrom(to) == 3) {
                    if (logic.getPiece(to) == null && toGo) {
                        set.add(to);
                    } else if (logic.getPiece(to) != null && logic.getPiece(to).getColor() != color && !toGo) {
                        set.add(to);
                    }
                }
            }
        }
        return set;
    }
    private Knight(){
    }
}
