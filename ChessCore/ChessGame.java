package ChessCore;

import ChessCore.Pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ChessGame {

    private final ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;
    private Move lastMove;
    private PiecesFactory factory = new PiecesFactory();
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;

    private Stack<ChessGame.Memento> s = new Stack<>();

    public void saveToMomento() {
        s.push(new Memento(new ChessBoard(board), whoseTurn, gameStatus, lastMove,
                canWhiteCastleKingSide, canWhiteCastleQueenSide, canBlackCastleKingSide, canBlackCastleQueenSide));
    }

    public void restoreFromMemento() {
        if (!s.empty()) {
            Memento mem = s.pop();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    this.board.setPieceAtSquare(new Square(BoardFile.values()[i],
                            BoardRank.values()[j]), mem.getBoard().getPieceAtSquare(new Square(BoardFile.values()[i], BoardRank.values()[j])));

                }
            }
            this.whoseTurn = mem.getWhoseTurn();
            this.gameStatus = mem.getGameStatus();
            this.lastMove = mem.lastMove;
            this.canBlackCastleKingSide = mem.canBlackCastleKingSide;
            this.canBlackCastleQueenSide = mem.canBlackCastleQueenSide;
            this.canWhiteCastleKingSide = mem.canWhiteCastleKingSide;
            this.canWhiteCastleQueenSide = mem.canWhiteCastleQueenSide;

        }

    }

    protected ChessGame(BoardInitializer boardInitializer) {
        this.board = new ChessBoard(boardInitializer.initialize());
    }

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    protected boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        // A player can't capture his own piece.
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }

    public Square getKingSquare() {
        Square s = Utilities.getKingSquare(whoseTurn, board);
        return s;
    }

    public boolean KingInCheck() {
        return Utilities.isInCheck(whoseTurn, board);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    ChessBoard getBoard() {
        return board;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public boolean isPawnPromotion(Move move) {
        Square fromSquare = move.getFromSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {

                List<Square> walk = getAllValidMovesFromSquare(fromSquare);
                for (Square s : walk) {
                    if (s.getRank() == move.getToSquare().getRank() && s.getFile() == move.getToSquare().getFile()) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public boolean makeMove(Move move) {

        if (!isValidMove(move)) {
            return false;
        }
        saveToMomento();
        Square fromSquare = move.getFromSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);

        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {
            if (fromPiece.getOwner() == Player.WHITE) {
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
            } else {
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleKingSide = false;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleKingSide = false;
                }
            }
        }
        SpecialMoves specialMoves = new SpecialMoves(board, lastMove, move);
        EnPassantCommand enPassant = new EnPassantCommand(specialMoves);
        // En-passant.
        if (fromPiece instanceof Pawn
                && move.getAbsDeltaX() == 1
                && !hasPieceIn(move.getToSquare())) {
            enPassant.execute();

        }

        // Promotion
        // Promotion
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {

                Player playerPromoting = toSquareRank == BoardRank.EIGHTH ? Player.WHITE : Player.BLACK;
                PawnPromotion promotion = move.getPawnPromotion();

                switch (promotion) {
                    case Queen:
                        fromPiece=factory.create("queen", playerPromoting);
                        break;
                    case Rook:
                        fromPiece=factory.create("rook", playerPromoting);
                        break;
                    case Knight:
                        fromPiece=factory.create("knight", playerPromoting);
                        break;
                    case Bishop:
                        fromPiece=factory.create("bishop", playerPromoting);
                        break;
                    case None:
                        throw new RuntimeException("Pawn moving to last rank without promotion being set. This should NEVER happen!");
                }
               
            }
        }

        // Castle
        if (fromPiece instanceof King
                && move.getAbsDeltaX() == 2) {
             CanCastleCommand cancastle=new CanCastleCommand(specialMoves);
             cancastle.execute();
        }

        board.setPieceAtSquare(fromSquare, null);
        board.setPieceAtSquare(move.getToSquare(), fromPiece);

        whoseTurn = Utilities.revertPlayer(whoseTurn);
        lastMove = move;
        updateGameStatus();
        return true;
    }

    private void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }

    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON
                || gameStatus == GameStatus.BLACK_WON
                || gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
         */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(new Square(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(new Square(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = new Square(i, j);
                if (isValidMove(new Move(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }

    public class Memento {

        private ChessBoard board;
        private Player whoseTurn;
        private GameStatus gameStatus;
        private Move lastMove;
        private Boolean canWhiteCastleKingSide;
        private Boolean canWhiteCastleQueenSide;
        private Boolean canBlackCastleKingSide;
        private Boolean canBlackCastleQueenSide;

        public Memento(ChessBoard board, Player whoseTurn, GameStatus gameStatus, Move move,
                Boolean canWhiteCastleKingSide, Boolean canWhiteCastleQueenSide,
                Boolean canBlackCastleKingSide, Boolean canBlackCastleQueenSide) {
            this.board = board;
            this.whoseTurn = whoseTurn;
            this.gameStatus = gameStatus;
            this.lastMove = move;
            this.canBlackCastleKingSide = canBlackCastleKingSide;
            this.canBlackCastleQueenSide = canBlackCastleQueenSide;
            this.canWhiteCastleKingSide = canWhiteCastleKingSide;
            this.canWhiteCastleQueenSide = canWhiteCastleQueenSide;

        }

        public ChessBoard getBoard() {
            return board;
        }
        
        public void setBoard(ChessBoard board) {
            this.board = board;
        }

        public Player getWhoseTurn() {
            return whoseTurn;
        }

        public void setWhoseTurn(Player whoseTurn) {
            this.whoseTurn = whoseTurn;
        }

        public GameStatus getGameStatus() {
            return gameStatus;
        }

        public void setGameStatus(GameStatus gameStatus) {
            this.gameStatus = gameStatus;
        }

    }

}
