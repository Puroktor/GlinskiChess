package ru.vsu.cs.skofenko.logic.geometry;

import ru.vsu.cs.skofenko.logic.model.IGameLogic;

import java.util.Objects;

public class Coordinate {

    public static final Coordinate EMPTY_CORD = createFromInner(-1, -1);

    private int i;
    private int j;

    private int r;
    private int q;

    private Coordinate() {
    }

    public static Coordinate createFromInner(int i, int j) {
        Coordinate cord = new Coordinate();
        cord.i = i;
        cord.j = j;
        cord.updateRQ();
        return cord;
    }

    public static Coordinate createFromAxial(int r, int q) {
        Coordinate cord = new Coordinate();
        cord.r = r;
        cord.q = q;
        cord.updateIJ();
        return cord;
    }

    public void plus(Directions d) {
        r += d.getR();
        q += d.getQ();
        updateIJ();
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getR() {
        return r;
    }

    public int getQ() {
        return q;
    }

    private static int GetRowFromAxial(int r) {
        return r + IGameLogic.N / 2;
    }

    private static int GetColumnFromAxial(int r, int q) {
        return q + IGameLogic.N / 2 + Math.min(0, r);
    }

    private static int GetAxialR(int i) {
        return i - IGameLogic.N / 2;
    }

    private static int GetAxialQ(int i, int j) {
        return j - IGameLogic.N / 2 - Math.min(0, GetAxialR(i));
    }

    private void updateIJ() {
        i = GetRowFromAxial(r);
        j = GetColumnFromAxial(r, q);
    }

    private void updateRQ() {
        r = GetAxialR(i);
        q = GetAxialQ(i, j);
    }

    public Coordinate copy() {
        return createFromInner(i, j);
    }

    public boolean isInBounds() {
        return Math.abs(r) <= IGameLogic.N / 2 && Math.abs(q) <= IGameLogic.N / 2 && Math.abs(r + q) <= IGameLogic.N / 2;
    }

    public int distanceFrom(Coordinate from) {
        return Math.max(Math.max(Math.abs(from.r - r), Math.abs(from.q - q)), Math.abs(from.r + from.q - r - q));
    }

    public Coordinate middleWith(Coordinate other) {
        return createFromAxial((r + other.r) / 2, (q + other.q) / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate coordinate = (Coordinate) o;
        return i == coordinate.i && j == coordinate.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
