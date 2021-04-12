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
public class AlgoMinimax9x9 extends AlgoRecherche {

    private Joueur joueurMax;
    private Joueur joueurMin;

    public AlgoMinimax9x9(Joueur _joueur1, Joueur _joueur2) {
        this.joueurMax = _joueur1;
        this.joueurMin = _joueur2;

    }

    // Fonction d'évaluation du plateau
    public int eval(Plateau _plateau) {

        // Partie nulle → 0
        if (_plateau.partieNulle()) {
            return 0;
        }

        // Partie gagnée par le joueur qui appelle la fonction Minmax → 10
        if (_plateau.vainqueur() == joueurMax) {
            return 10;
        }

        // Partie perdue par le joueur qui appelle la fonction Minmax → -10
        else {
            return -10;
        }
    }

    public int minimax(Plateau _plateau, boolean isMaximizing, int depth) {

        // Si la partie est terminée, ou si on atteind la profondeur maximale de
        // l'arbre, on évalue le plateau
        if (depth == 0 || _plateau.partieTerminee()) {
            return eval(_plateau);
        }

        // Si c'est au tour du joueur qui cherche à maximiser le score
        if (isMaximizing) {

            int maxEval = -100;
            ArrayList<Coup> coupsMax = _plateau.getListeCoups(joueurMax);

            // Pour chacuns de ses coups
            for (int i = 0; i < coupsMax.size(); i++) {
                int eval;

                _plateau.joueCoup(coupsMax.get(i));

                // On attribue une valeur
                eval = minimax(_plateau, false, depth - 1);
                _plateau.annuleDernierCoup();

                // On garde le plus grand score
                maxEval = max(eval, maxEval);
            }

            // On retourne le plus grand score possible pour chaque coup
            return maxEval;
        }

        // Si c'est au tout du joueur qui cherche à minimiser le score
        else {
            int minEval = 100;
            ArrayList<Coup> coupsMin = _plateau.getListeCoups(joueurMin);

            // Pour chacuns de ses coups
            for (int i = 0; i < coupsMin.size(); i++) { // Y'a trop de reset de coups
                int eval;

                _plateau.sauvegardePosition(1);
                _plateau.joueCoup(coupsMin.get(i));
                // On attribue un score
                eval = minimax(_plateau, true, depth - 1);
                _plateau.restaurePosition(1);
                // _plateau.annuleDernierCoup();

                // On garde le plus petit score
                minEval = min(eval, minEval);
            }

            // On retourne le plus petit score possible pour chaque coup
            return minEval;
        }
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        int bestScore = -1000;
        int move = 0;
        int score;

        // On cherche à attribuer à chaque coup un score
        for (int i = 0; i < coups.size(); i++) {

            _plateau.sauvegardePosition(0);
            // On joue ce coup
            _plateau.joueCoup(coups.get(i));
            // On évalue ce coup
            score = minimax(_plateau, false, 10000);
            // On reset le coup
            _plateau.restaurePosition(0);
            // _plateau.annuleDernierCoup(); // Erreur here

            // On cherche le meilleur score, donc le meilleur coup
            if (score > bestScore) {
                bestScore = score;
                move = i;
            }
        }

        // On retourne le meilleur coup
        return coups.get(move);
    }

    // Fonction max et min
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
