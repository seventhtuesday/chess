package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard game;

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    
    public ChessGame() {
        turn = TeamColor.WHITE;
        game = new ChessBoard();
        game.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //check if there is a piece at startPosition
        ChessPiece piece = game.getPiece(startPosition);
        Collection<ChessMove> moves = new HashSet<> (0);
        if(piece == null) {
            return moves;
        }

        //if piece at start check which moves are valid
        else {
            moves = piece.pieceMoves(game, startPosition);
            Collection<ChessMove> goods = new HashSet<> ();
            for(ChessMove move: moves) {
                ChessPosition end = move.getEndPosition();
                ChessPiece endP = game.getPiece(end);
                
                //make the move temporarily
                game.addPiece(end, piece);
                game.remPiece(startPosition);

                //check if in check; remove move if so
                if(!isInCheck(piece.getTeamColor())) {
                    goods.add(move);
                }

                //undo move
                game.addPiece(startPosition, piece);
                game.addPiece(end, endP);
            }
            return goods;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        //check for easy errors
        if(validMoves(start).size() == 0) {
            throw new InvalidMoveException("No piece at start position.");
        }
        ChessPiece piece = game.getPiece(start);
        if(piece.getTeamColor() != turn) {
            throw new InvalidMoveException("It is the other teams turn.");
        }        

        //check if move is valid
        Collection<ChessMove> moves = validMoves(start);
        if(!moves.contains(move)) {
            throw new InvalidMoveException("That is not a valid move.");
        }
        else {
            //check if need to promote
            if(move.getPromotionPiece() != null) {
                piece = new ChessPiece(turn, move.getPromotionPiece());
            }

            //make the move
            game.addPiece(end, piece);
            game.remPiece(start);

            //advance turn
            if(turn == TeamColor.BLACK) {
                turn = TeamColor.WHITE;
            }
            else {
                turn = TeamColor.BLACK;
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition pos;
        ChessPiece piece;
        ChessPosition king = null;
        
        //find king
        kingloop:
        for(int n = 1; n <= 8; n++) {
            for(int m = 1; m <= 8; m++) {
                pos = new ChessPosition(n, m);
                piece = game.getPiece(pos);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == PieceType.KING) {
                    king = pos;
                    break kingloop;
                }
            }
        }

        //find if other piece can capture king
        for(int n = 1; n <= 8; n++) {
            for(int m = 1; m <= 8; m++) {
                pos = new ChessPosition(n, m);
                piece = game.getPiece(pos);
                if(subCheck(pos, piece, teamColor, king)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean subCheck(ChessPosition pos, ChessPiece piece, TeamColor teamColor, ChessPosition king) {
        if(piece != null && piece.getTeamColor() != teamColor) {
            Collection<ChessMove> moves = piece.pieceMoves(game, pos);
            PieceType[] types = {null, PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
            for(PieceType type: types) {
                if(moves.contains(new ChessMove(pos, king, type))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if not in check false
        if(!isInCheck(teamColor)) {
            return false;
        }
        //same as stalemate below; check stalemate for comments
        else {
            return stalemate(teamColor);
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if in check not stalemante
        if(isInCheck(teamColor)) {
            return false;
        }
        else {
            return stalemate(teamColor);
        }      
    }

    private boolean stalemate(TeamColor teamColor) {
        ChessPosition pos;
        ChessPiece piece;

        //check each board space for pieces on team and if they can move
        for(int n = 1; n <= 8; n++) {
            for(int m = 1; m <= 8; m++) {
                pos = new ChessPosition(n, m);
                piece = game.getPiece(pos);
                if(piece != null && piece.getTeamColor() == teamColor && !validMoves(pos).isEmpty()) {
                    return false;
                }
            }
        }
        //if make it through then none can move
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(game, chessGame.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, game);
    }
}
