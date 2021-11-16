package ru.vsu.cs.skofenko.logic.model;

import ru.vsu.cs.skofenko.logic.chesspieces.*;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;

import java.util.*;

public class GameLogic implements IGameLogic {
    private final BoardCell[][] board = new BoardCell[N][N];
    private final Collection<ChessPiece> chessPieces = new HashSet<>();

    private ChessColor nowTurn = ChessColor.WHITE;
    private ChessPiece selectedPiece;
    private GameState gameState;
    private King whiteKing, blackKing;
    private Pawn enPassant;
    private Coordinate enPasCord, promotingCord;
    private boolean locked = false;

    public GameLogic() {
        initBoard();
        placeChess();
        fillPiecesCollections();
        fillPossibleCells();
        gameState = GameState.PLAYING;
    }

    private void fillPiecesCollections() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                ChessPiece piece = board[i][j].getPiece();
                if (piece != null) {
                    chessPieces.add(piece);
                    piece.setNowCord(Coordinate.createFromInner(i, j));
                }
            }
        }
    }

    private void fillPossibleCells() {
        fillPossibleCells(true);
        fillPossibleCells(false);
        checkMateAndRemoveCells();
    }

    private void fillPossibleCells(boolean toGo) {
        for (ChessPiece piece : chessPieces) {
            piece.setPossibleCells(this, toGo);
        }
    }

    private void initBoard() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = new BoardCell();
            }
        }
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public ChessColor getNowTurn() {
        return nowTurn;
    }

    @Override
    public boolean selectPiece(Coordinate cord) {
        if (locked || gameState == GameState.CHECKMATE || gameState == GameState.STALEMATE) {
            return false;
        }
        ChessPiece now = getPiece(cord);
        if (now != null && now.getColor() == nowTurn) {
            removeSelectedPiece();
            setSelectedPiece(cord);
        } else if (selectedPiece != null && (selectedPiece.getPossibleGoCells().contains(cord) ||
                selectedPiece.getPossibleCaptureCells().contains(cord))) {
            return goTo(cord);
        }
        return false;
    }

    private void setSelectedPiece(Coordinate cord) {
        selectedPiece = getPiece(cord);
        setCellsBoolean(true);
    }

    private void setCellsBoolean(boolean value) {
        for (Coordinate p : selectedPiece.getPossibleGoCells()) {
            board[p.getI()][p.getJ()].setReachable(value);
        }
        for (Coordinate p : selectedPiece.getPossibleCaptureCells()) {
            board[p.getI()][p.getJ()].setCapturable(value);
        }
    }

    private void removeSelectedPiece() {
        if (selectedPiece == null)
            return;
        setCellsBoolean(false);
        selectedPiece = null;
    }

    private void movePiece(Coordinate from, Coordinate to) {
        enPassant = null;
        enPasCord = null;
        BoardCell cell = board[to.getI()][to.getJ()];
        if (cell.getPiece() != null) {
            chessPieces.remove(cell.getPiece());
        }
        cell.setPiece(board[from.getI()][from.getJ()].getPiece());
        board[from.getI()][from.getJ()].getPiece().setNowCord(to);
        board[from.getI()][from.getJ()].setPiece(null);
    }

    private boolean goTo(Coordinate to) {
        if (to.equals(enPasCord)) {
            board[enPassant.getNowCord().getI()][enPassant.getNowCord().getJ()].setPiece(null);
            chessPieces.remove(enPassant);
        }
        Coordinate from = selectedPiece.getNowCord();
        movePiece(from, to);
        if (selectedPiece instanceof Pawn) {
            if(pawnMoved((Pawn) selectedPiece, from))
                return true;
        }
        startNewMove();
        return false;
    }

    private void startNewMove() {
        removeSelectedPiece();
        nowTurn = (nowTurn == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
        fillPossibleCells();
        checkState();
    }

    private void checkState() {
        gameState = GameState.PLAYING;
        if (isInCheck()) {
            gameState = checkMateAndRemoveCells() ? GameState.CHECKMATE : GameState.CHECK;
        } else if (isInStaleMate()) {
            gameState = GameState.STALEMATE;
        }
    }

    private boolean isInStaleMate() {
        for (ChessPiece piece : chessPieces) {
            if (piece.getColor() != nowTurn)
                continue;
            if (piece.getPossibleCaptureCells().size() > 0 || piece.getPossibleGoCells().size() > 0)
                return false;
        }
        return true;
    }

    private boolean isInCheck() {
        for (ChessPiece piece : chessPieces) {
            if (piece.getColor() != nowTurn && piece.getPossibleCaptureCells().contains(getNowKing().getNowCord())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMateAndRemoveCells() {
        boolean result = true;
        Map<ChessPiece, Set<Coordinate>> pieceToCaptureCellsMap = new HashMap<>();
        for (ChessPiece piece : chessPieces.toArray(new ChessPiece[0])) {
            if (piece.getColor() != nowTurn)
                continue;
            Coordinate from = piece.getNowCord();
            Iterator<Coordinate> iterator = piece.getPossibleGoCells().iterator();
            while (iterator.hasNext()) {
                if (tryToMakeMove(from, iterator.next())) {
                    iterator.remove();
                } else {
                    result = false;
                }
            }
            HashSet<Coordinate> captureSet = new HashSet<>();
            pieceToCaptureCellsMap.put(piece, captureSet);
            for (Coordinate cord : piece.getPossibleCaptureCells()) {
                if (!tryToMakeMove(from, cord)) {
                    result = false;
                    captureSet.add(cord);
                }
            }

        }
        for (ChessPiece piece : chessPieces) {
            if (piece.getColor() == nowTurn) {
                piece.setPossibleCaptureCells(pieceToCaptureCellsMap.get(piece));
            }
        }
        return result;
    }

    private boolean tryToMakeMove(Coordinate from, Coordinate to) {
        ChessPiece toPiece = board[to.getI()][to.getJ()].getPiece();
        Pawn enPassant = this.enPassant;
        Coordinate enPasCord = this.enPasCord;
        movePiece(from, to);
        fillPossibleCells(false);
        boolean result = isInCheck();
        movePiece(to, from);
        if (toPiece != null) {
            board[to.getI()][to.getJ()].setPiece(toPiece);
            chessPieces.add(toPiece);
        }
        this.enPassant = enPassant;
        this.enPasCord = enPasCord;
        fillPossibleCells(false);
        return result;
    }

    private boolean pawnMoved(Pawn pawn, Coordinate from) {
        Coordinate cord = pawn.getNowCord();
        if (pawn.hasNotMoved()) {
            if (cord.distanceFrom(from) == 2) {
                enPassant = pawn;
                enPasCord = from.middleWith(cord);
            }
            if (Math.abs(from.getQ()) <= Math.abs(cord.getQ())) {
                pawn.moved();
            }
        }
        int y = -cord.getR() - cord.getQ();
        if (((cord.getR() + cord.getQ() == -5 || cord.getQ() + y == 5) && pawn.getColor() == ChessColor.WHITE) ||
                ((cord.getR() + cord.getQ() == 5 || cord.getQ() + y == -5) && pawn.getColor() == ChessColor.BLACK)) {
            locked = true;
            promotingCord = cord;
            return true;
        }
        return false;
    }

    @Override
    public boolean promotePawn(ChessPiece piece) {
        if (!locked) {
            return false;
        }
        piece.setNowCord(promotingCord);
        board[promotingCord.getI()][promotingCord.getJ()].setPiece(piece);
        locked = false;
        startNewMove();
        return true;
    }

    @Override
    public Coordinate getSelectedCord() {
        return (selectedPiece == null) ? Coordinate.EMPTY_CORD : selectedPiece.getNowCord();
    }

    @Override
    public BoardCell[][] getBoard() {
        return board;
    }

    public ChessPiece getPiece(Coordinate cord) {
        return board[cord.getI()][cord.getJ()].getPiece();
    }

    public Coordinate getEnPasCord() {
        return enPasCord;
    }

    private King getNowKing() {
        return nowTurn == ChessColor.WHITE ? whiteKing : blackKing;
    }

    private void placeChess() {
        board[0][4].setPiece(new Pawn(ChessColor.BLACK));
        board[6][9].setPiece(new Pawn(ChessColor.WHITE));

        board[0][3].setPiece(new Rook(ChessColor.BLACK));
        board[1][4].setPiece(new Pawn(ChessColor.BLACK));
        board[6][8].setPiece(new Pawn(ChessColor.WHITE));
        board[7][8].setPiece(new Rook(ChessColor.WHITE));

        board[0][2].setPiece(new Knight(ChessColor.BLACK));
        board[2][4].setPiece(new Pawn(ChessColor.BLACK));
        board[6][7].setPiece(new Pawn(ChessColor.WHITE));
        board[8][7].setPiece(new Knight(ChessColor.WHITE));

        blackKing = new King(ChessColor.BLACK);
        board[0][1].setPiece(blackKing);

        board[3][4].setPiece(new Pawn(ChessColor.BLACK));
        board[6][6].setPiece(new Pawn(ChessColor.WHITE));

        whiteKing = new King(ChessColor.WHITE);
        board[9][6].setPiece(whiteKing);

        board[0][0].setPiece(new Bishop(ChessColor.BLACK));
        board[1][1].setPiece(new Bishop(ChessColor.BLACK));
        board[2][2].setPiece(new Bishop(ChessColor.BLACK));
        board[4][4].setPiece(new Pawn(ChessColor.BLACK));
        board[6][5].setPiece(new Pawn(ChessColor.WHITE));
        board[8][5].setPiece(new Bishop(ChessColor.WHITE));
        board[9][5].setPiece(new Bishop(ChessColor.WHITE));
        board[10][5].setPiece(new Bishop(ChessColor.WHITE));

        board[1][0].setPiece(new Queen(ChessColor.BLACK));
        board[4][3].setPiece(new Pawn(ChessColor.BLACK));
        board[7][4].setPiece(new Pawn(ChessColor.WHITE));
        board[10][4].setPiece(new Queen(ChessColor.WHITE));

        board[2][0].setPiece(new Knight(ChessColor.BLACK));
        board[4][2].setPiece(new Pawn(ChessColor.BLACK));
        board[8][3].setPiece(new Pawn(ChessColor.WHITE));
        board[10][3].setPiece(new Knight(ChessColor.WHITE));

        board[3][0].setPiece(new Rook(ChessColor.BLACK));
        board[4][1].setPiece(new Pawn(ChessColor.BLACK));
        board[9][2].setPiece(new Pawn(ChessColor.WHITE));
        board[10][2].setPiece(new Rook(ChessColor.WHITE));

        board[4][0].setPiece(new Pawn(ChessColor.BLACK));
        board[10][1].setPiece(new Pawn(ChessColor.WHITE));
    }
}