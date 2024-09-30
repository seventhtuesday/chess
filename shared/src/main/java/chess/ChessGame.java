package chess;

import java.util.Collection;

import chess.ChessGame.TeamColor;

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
        if(piece == null) {
            return null;
        }

        //if piece at start check which moves are valid
        else {
            Collection<ChessMove> moves = piece.pieceMoves(game, startPosition);
            for(ChessMove move: moves) {
                ChessPosition end = move.getEndPosition();
                ChessPiece endP = game.getPiece(end);
                
                //make the move temporarily
                game.addPiece(end, piece);
                game.remPiece(startPosition);

                //check if in check; remove move if so
                if(isInCheck(piece.getTeamColor())) {
                    moves.remove(move);
                }

                //undo move
                game.addPiece(startPosition, piece);
                game.addPiece(end, endP);
            }
            return moves;
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
        ChessPiece piece = game.getPiece(start);

        //check for easy errors
        if(piece.getTeamColor() != turn) {
            throw new InvalidMoveException("It is the other teams turn.");
        }
        else if(validMoves(start) == null) {
            throw new InvalidMoveException("No piece at start position.");
        }

        //check if move is valid
        Collection<ChessMove> moves = validMoves(start);
        if(!moves.contains(move)) {
            throw new InvalidMoveException("That is not a valid move. It may leave the king in check, capture one of the same team pieces, or move illegally");
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
}
