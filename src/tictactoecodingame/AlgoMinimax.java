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

    private Joueur joueurMax;
    private Joueur joueurMin;

    public AlgoMinimax(Joueur _joueur1, Joueur _joueur2) {
        this.joueurMax = _joueur1;
        this.joueurMin = _joueur2;

    }

    public int eval(Plateau _plateau) {
        if (_plateau.partieNulle()) {
            return 0;
        }

        if (_plateau.vainqueur() == joueurMax) {
            return 10;
        }

        else {
            return -10;
        }
    }

    public int minimax(Plateau _plateau, boolean isMaximizing, int depth) {

        if (depth == 0 || _plateau.partieTerminee()) {
            return eval(_plateau);
        }

        if (isMaximizing) {

            int maxEval = -100;
            ArrayList<Coup> coupsMax = _plateau.getListeCoups(joueurMax);

            for (int i = 0; i < coupsMax.size(); i++) {
                int eval;

                _plateau.joueCoup(coupsMax.get(i));
                eval = minimax(_plateau, false, depth - 1);
                _plateau.annuleDernierCoup();

                maxEval = max(eval, maxEval);
            }

            return maxEval;
        }

        else {
            int minEval = 100;
            ArrayList<Coup> coupsMin = _plateau.getListeCoups(joueurMin);

            for (int i = 0; i < coupsMin.size(); i++) {
                int eval;

                _plateau.joueCoup(coupsMin.get(i));
                eval = minimax(_plateau, true, depth - 1);
                _plateau.annuleDernierCoup();

                minEval = min(eval, minEval);
            }

            return minEval;
        }
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        int bestScore = -1000;
        int move = 0;
        int score;

        for (int i = 0; i < coups.size(); i++) {

            _plateau.joueCoup(coups.get(i));
            score = minimax(_plateau, false, 9);
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
