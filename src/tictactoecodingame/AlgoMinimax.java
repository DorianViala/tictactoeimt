/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoecodingame;

import java.util.ArrayList;

/**
 *
 * @author franck.tempet
 */
public class AlgoMinimax extends AlgoRecherche {

    public AlgoMinimax() {
    }

    public int eval(Plateau _plateau, Joueur _joueur) {
        if (_plateau.partieGagnee()) {
            return 1;
        } else if (!_plateau.partieGagnee()) {
            return -1;
        } else {
            return 0;
        }

    }

    public int minimax(Plateau _plateau, Joueur _joueur, int depth, boolean isMaximizing) {

        if (depth == 0 || _plateau.partieTerminee()) {
            return eval(_plateau, _joueur);
        }

        if (isMaximizing) {
            ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
            int maxEval = -10;
            int eval;

            for (int i = 0; i < coups.size(); i++) {
                _plateau.joueCoup(coups.get(i));
                eval = minimax(_plateau, _joueur, depth - 1, false);
                _plateau.annuleDernierCoup();
                maxEval = max(eval, maxEval);

            }

            return maxEval;
        }

        else {
            ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
            int minEval = 10;
            int eval;

            for (int i = 0; i < coups.size(); i++) {
                _plateau.joueCoup(coups.get(i));
                eval = minimax(_plateau, _joueur, depth - 1, true);
                _plateau.annuleDernierCoup();
                minEval = min(eval, minEval);
            }

            return minEval;
        }

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
            score = minimax(_plateau, _joueur, 1, true);
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

    public int min(int a, int b) {
        if (a <= b) {
            return a;
        } else {
            return b;
        }
    }
}
