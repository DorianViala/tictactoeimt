/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoecodingame;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author franck.tempet
 */
public class AlgoMinimax extends AlgoRecherche {
    Random rnd;

    public AlgoMinimax() {
    }

    public int minimax(Plateau _plateau) {
        return 1;
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        int bestScore = -10;
        int move = 0;
        int score;

        for (int i = 0; i < coups.size(); i++) {
            // jouer ce coup
            _plateau.joueCoup(coups.get(i));
            // evaluer ce coup
            score = minimax(_plateau);
            // restaure le coup
            _plateau.annuleDernierCoup();

            if (score > bestScore) {
                bestScore = score;
                move = i;
            }
        }

        return coups.get(move);
    }

    public int max(int a, int b) {
        if (a >= b) {
            return a;
        } else {
            return b;
        }
    }

}
