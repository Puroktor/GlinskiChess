package ru.vsu.cs.skofenko.logic.geometry;

import java.util.EnumSet;

public enum Directions {
    UP_RIGHT(0, -1),
    DOWN_LEFT(0, 1),
    UP(-1, 0),
    UP_LEFT(-1, 1),
    DOWN_RIGHT(1, -1),
    DOWN(1, 0),

    DIAGONAL_LEFT(-2, 1),
    DIAGONAL_RIGHT(2, -1),
    DIAGONAL_UP_LEFT(-1, -1),
    DIAGONAL_UP_RIGHT(1, -2),
    DIAGONAL_DOWN_LEFT(-1, 2),
    DIAGONAL_DOWN_RIGHT(1, 1);

    private final int r;
    private final int q;

    Directions(int r, int q) {
        this.r = r;
        this.q = q;
    }

    public int getR() {
        return r;
    }

    public int getQ() {
        return q;
    }

    public static EnumSet<Directions> getOrthogonal() {
        return EnumSet.of(UP_RIGHT, DOWN_LEFT, UP, UP_LEFT, DOWN_RIGHT, DOWN);
    }

    public static EnumSet<Directions> getDiagonal(){
        return EnumSet.of( DIAGONAL_LEFT, DIAGONAL_RIGHT, DIAGONAL_UP_LEFT, DIAGONAL_UP_RIGHT, DIAGONAL_DOWN_LEFT,
                DIAGONAL_DOWN_RIGHT);
    }
}
