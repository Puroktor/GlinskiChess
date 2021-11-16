package ru.vsu.cs.skofenko.restclient.visualization;

import ru.vsu.cs.skofenko.logic.chesspieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

class ChessPieceIcons {
    private static final Map<Class<? extends ChessPiece>, Map<ChessColor, Image>> map = new HashMap<>();

    static {
        @SuppressWarnings("unchecked")
        Class<? extends ChessPiece>[] pieces = new Class[]{King.class, Pawn.class, Queen.class, Knight.class, Bishop.class, Rook.class};
        String[] paths = new String[]{"/static/WhiteKing.png", "/static/BlackKing.png", "/static/WhitePawn.png", "/static/BlackPawn.png",
                "/static/WhiteQueen.png", "/static/BlackQueen.png", "/static/WhiteKnight.png", "/static/BlackKnight.png",
                "/static/WhiteBishop.png", "/static/BlackBishop.png", "/static/WhiteRook.png", "/static/BlackRook.png"};
        int i = 0;
        for (Class<? extends ChessPiece> type : pieces) {
            Map<ChessColor, Image> innerMap = new HashMap<>();
            for (ChessColor color : ChessColor.values()) {
                innerMap.put(color, new ImageIcon(ChessPieceIcons.class.getResource(paths[i++])).getImage());
            }
            map.put(type, innerMap);
        }
    }

    public static Image getIcon(ChessPiece piece) {
        return map.get(piece.getClass()).get(piece.getColor());
    }
}
