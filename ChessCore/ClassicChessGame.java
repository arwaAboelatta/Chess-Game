package ChessCore;

public final class ClassicChessGame extends ChessGame {
    
    private static ClassicChessGame obj;

    private ClassicChessGame() {
        super(ClassicBoardInitializer.getInstance());    
    }
    public static ClassicChessGame getInstance()
    {
        if (obj==null)
        {
            obj=new ClassicChessGame();
        }
        return obj;
    }

    @Override
    protected boolean isValidMoveCore(Move move) {
        return !Utilities.willOwnKingBeAttacked(this.getWhoseTurn(), move, this.getBoard());
    }
}
