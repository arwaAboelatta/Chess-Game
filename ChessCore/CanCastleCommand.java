/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore;

/**
 *
 * @author Esraa
 */
public class CanCastleCommand implements ActionListenerCommand {

    private SpecialMoves specialMoves;

    public CanCastleCommand(SpecialMoves specialMoves) {
        this.specialMoves = specialMoves;
    }
    
    @Override
    public void execute() {
        specialMoves.canCastle();
    }
    
}