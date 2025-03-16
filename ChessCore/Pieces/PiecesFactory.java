/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore.Pieces;

import ChessCore.Player;

/**
 *
 * @author Esraa
 */
public class PiecesFactory {

    public Piece create(String piece, Player player) {
        Piece p;
        if (piece.equals("pawn")) {
            p= new Pawn(player);
            //p.setOwner(player);
            return p;
        }
        if (piece.equals("rook")) {
            p= new Rook(player);
           // p.setOwner(player);
            return p;
        }
        if (piece.equals("queen")) {
            p= new Queen(player);
           // p.setOwner(player);
            return p;
        }
        if (piece.equals("king")) {
            p= new King(player);
           // p.setOwner(player);
            return p;
        }
        if (piece.equals("bishop")) {
            p= new Bishop(player);
            //p.setOwner(player);
            return p;
        }
        if (piece.equals("knight")) {
            p= new Knight(player);
            //p.setOwner(player);
            return p;
        }

        return null;
    }
}