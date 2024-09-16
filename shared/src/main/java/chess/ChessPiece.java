package chess;

import java.util.Collection;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor team;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.team = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves;
        ChessPosition testPosition;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece piece = board.getPiece(myPosition);

        switch(piece.getPieceType()) {
            case PAWN:
                //is black or white
                if(piece.team == TeamColor.WHITE) {
                    testPosition = new ChessPosition(row+1, col);
                    if(board.getPiece(testPosition) != null) {
                        validMoves.add(new ChessMove(myPosition, testPosition, type));
                }
                }
                else if(piece.team == TeamColor.BLACK) {

                }
                else {
                    //error no piece
                }
        }

        return validMoves;
    }

    @Override
    public String toString() {
        return "[" + this.team + "_" + this.type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessPiece other = (ChessPiece) obj;
        if (team != other.team)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
