package chess;

import java.util.Collection;
import java.util.HashSet;

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
        Collection<ChessMove> validMoves = new HashSet<> ();
        ChessPosition testPosition;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece piece = board.getPiece(myPosition);

        switch(piece.getPieceType()) {
            case PAWN:
                if(piece.team == TeamColor.WHITE) {
                    testPosition = new ChessPosition(row+1, col);
                    if(board.getPiece(testPosition) == null) {
                        validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        testPosition = new ChessPosition(row+2, col);
                        if(row == 2 && board.getPiece(testPosition) == null) {
                            validMoves.add(new ChessMove(myPosition, testPosition));
                        }
                    }
                    if(col != 8) {   
                        testPosition = new ChessPosition(row+1, col+1);
                        if (board.getPiece(testPosition) != null && board.getPiece(testPosition).team == TeamColor.BLACK) {
                            validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        }
                    }
                    if(col != 1) {
                        testPosition = new ChessPosition(row+1, col-1);
                        if ( board.getPiece(testPosition) != null && board.getPiece(testPosition).team == TeamColor.BLACK) {
                            validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        }
                    }
                }
                else if(piece.team == TeamColor.BLACK) {
                    testPosition = new ChessPosition(row-1, col);
                    if(board.getPiece(testPosition) == null) {
                        validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        testPosition = new ChessPosition(row-2, col);
                        if(row == 7 && board.getPiece(testPosition) == null) {
                            validMoves.add(new ChessMove(myPosition, testPosition));
                        }
                    }
                    if(col != 8) {   
                        testPosition = new ChessPosition(row-1, col+1);
                        if (board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() == TeamColor.WHITE) {
                            validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        }
                    }
                    if(col != 1) {
                        testPosition = new ChessPosition(row-1, col-1);
                        if ( board.getPiece(testPosition) != null && board.getPiece(testPosition).getTeamColor() == TeamColor.WHITE) {
                            validMoves.addAll(checkPromote(new ChessMove(myPosition, testPosition)));
                        }
                    }
                }
                break;
            case QUEEN:
                //Queen will fall through Rook and Bishop movement
            case ROOK:
                //up
                for(int n = row+1; n <= 8; n++) {
                    testPosition = new ChessPosition(n, col);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //down
                for(int n = row-1; n >= 1; n--) {
                    testPosition = new ChessPosition(n, col);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //right
                for(int n = col+1; n <= 8; n++) {
                    testPosition = new ChessPosition(row, n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //left
                for(int n = col-1; n >= 1; n--) {
                    testPosition = new ChessPosition(row, n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                if(piece.getPieceType() == PieceType.ROOK) {
                    break;
                }
            case BISHOP:
                //NE
                for(int n = 1; row+n<=8 && col+n<=8; n++) {
                    testPosition = new ChessPosition(row+n, col+n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //NW
                for(int n = 1; row-n>=1 && col+n<=8; n++) {
                    testPosition = new ChessPosition(row-n, col+n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //SE
                for(int n = 1; row+n<=8 && col-n>=1; n++) {
                    testPosition = new ChessPosition(row+n, col-n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                //SW
                for(int n = 1; row-n>=1 && col-n>=1; n++) {
                    testPosition = new ChessPosition(row-n, col-n);
                    if(board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, testPosition));
                    }
                    if(board.getPiece(testPosition) != null) {
                        break;
                    }
                }
                break;
            case KNIGHT:
                //up
                testPosition = new ChessPosition(row+2, col-1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                testPosition = new ChessPosition(row+2, col+1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }  
                //down
                testPosition = new ChessPosition(row-2, col-1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                testPosition = new ChessPosition(row-2, col+1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }  
                //right
                testPosition = new ChessPosition(row-1, col+2);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                testPosition = new ChessPosition(row+1, col+2);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }  
                //left
                testPosition = new ChessPosition(row-1, col-2);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                testPosition = new ChessPosition(row+1, col-2);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }  
                break;
            case KING:
                //up
                testPosition = new ChessPosition(row+1, col);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //NE
                testPosition = new ChessPosition(row+1, col+1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //right
                testPosition = new ChessPosition(row, col+1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //SE
                testPosition = new ChessPosition(row-1, col+1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //down
                testPosition = new ChessPosition(row-1, col);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //SW
                testPosition = new ChessPosition(row-1, col-1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //left
                testPosition = new ChessPosition(row, col-1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                //NW
                testPosition = new ChessPosition(row+1, col-1);
                if((testPosition.getRow() <= 8 && testPosition.getRow() >= 1 && testPosition.getColumn() <= 8 && testPosition.getColumn() >= 1) && (board.getPiece(testPosition) == null || board.getPiece(testPosition).getTeamColor() != piece.getTeamColor())) {
                    validMoves.add(new ChessMove(myPosition, testPosition));
                }
                break;

        }

        return validMoves;
    }

    //determines if a piece is on the other team and therefore can be captured
    public boolean canCapture(ChessBoard map, ChessPosition them, TeamColor us) {
        return map.getPiece(them).getTeamColor() != us;
    }

    //checks if a pawn is moving to the end of the board and makes promtoes possible if it is
    public Collection<ChessMove> checkPromote(ChessMove move) {
        Collection<ChessMove> moves = new HashSet<> ();
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();
        if(end.getRow() == 1 || end.getRow() == 8) {
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            return moves;
        }
        else {
            moves.add(move);
            return moves;
        }
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
