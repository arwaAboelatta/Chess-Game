package ChessCore;
import ChessCore.Pieces.*;


public final class ClassicBoardInitializer implements BoardInitializer {
    private static final BoardInitializer instance = new ClassicBoardInitializer();
    private PiecesFactory factory=new PiecesFactory();
    private ClassicBoardInitializer() {
    }

    public static BoardInitializer getInstance() {
        return instance;
    }

    @Override
    public Piece[][] initialize() {
        Piece[][] initialState = {
            {factory.create("rook", Player.WHITE), factory.create("knight", Player.WHITE), factory.create("bishop", Player.WHITE), factory.create("queen", Player.WHITE), factory.create("king", Player.WHITE),factory.create("bishop", Player.WHITE),factory.create("knight", Player.WHITE),factory.create("rook", Player.WHITE)},
            {factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE), factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE),factory.create("pawn", Player.WHITE)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK), factory.create("pawn", Player.BLACK)},
            {factory.create("rook", Player.BLACK),factory.create("knight", Player.BLACK), factory.create("bishop", Player.BLACK), factory.create("queen", Player.BLACK), factory.create("king", Player.BLACK), factory.create("bishop", Player.BLACK), factory.create("knight", Player.BLACK),factory.create("rook", Player.BLACK)}
        };
        return initialState;
    }
}