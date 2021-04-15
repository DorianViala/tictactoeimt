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
public class AlgoMiniMax9x9 extends AlgoRecherche {

    private Joueur joueurMax;
    private Joueur joueurMin;

    public AlgoMiniMax9x9(Joueur _joueur1, Joueur _joueur2) {
        this.joueurMax = _joueur1;
        this.joueurMin = _joueur2;

    }

    // Fonction d'évaluation du plateau
    public int eval(Plateau _plateau) {

        // Partie gagnée par le joueur qui appelle la fonction Minmax → 10
        if (_plateau.vainqueur() == joueurMax) {
            return 100;
        }

        // Partie nulle → 0
        if (_plateau.partieNulle()) {
            return 0;
        }

        // Calcul nombre grille3x3 gagnées
        int nbCase3x3Max = 0;
        int nbCase3x3Min = 0;
        for (int c = 0; c < 3; c++) {
            for (int l = 0; l < 3; l++) {

                Jeton[][] grille3x3 = _plateau.getGrille3x3(); // Retourne l'état des grilles3x3

                if (grille3x3[c][l] != null) {
                    if (grille3x3[c][l].getJoueur().getIdJoueur() == joueurMax.getIdJoueur()) {
                        nbCase3x3Max++;
                    } else {
                        nbCase3x3Min++;
                    }
                }

            }
        }

        // Joueur Max a 1,2,3,4,5,6,7,8 grille3x3 gagnée → 53,54,55,56,57,58,59
        if (nbCase3x3Max != 0) {
            if (nbCase3x3Max == 1) {
                return 53;
            }

            else if (nbCase3x3Max == 2) {
                return 54;
            }

            else if (nbCase3x3Max == 4) {
                return 55;
            }

            else if (nbCase3x3Max == 5) {
                return 56;
            }

            else if (nbCase3x3Max == 6) {
                return 57;
            }

            else if (nbCase3x3Max == 7) {
                return 58;
            }

            else if (nbCase3x3Max == 8) {
                return 59;
            }
        }

        // Joueur Min a 1,2,3,4,5,6,7,8 grille3x3 gagnée → -53,-54,-55,-56,-57,-58,-59
        if (nbCase3x3Min != 0) {
            if (nbCase3x3Min == 1) {
                return -53;
            }

            else if (nbCase3x3Min == 2) {
                return -54;
            }

            else if (nbCase3x3Min == 4) {
                return -55;
            }

            else if (nbCase3x3Min == 5) {
                return -56;
            }

            else if (nbCase3x3Min == 6) {
                return -57;
            }

            else if (nbCase3x3Min == 7) {
                return -58;
            }

            else if (nbCase3x3Min == 8) {
                return -59;
            }
        }

        // Calcul nombre case milieu des joueurs
        int nbCaseMilieuMax = 0;
        int nbCaseMilieuMin = 0;
        for (int c = 1; c < 9; c = c + 3) {
            for (int l = 1; l < 9; l = l + 3) {

                Jeton[][] grille9x9 = _plateau.getGrille9x9(); // Retourne la grille à l'état actuel

                if (grille9x9[c][l] != null) {
                    if (grille9x9[c][l].getJoueur().getIdJoueur() == joueurMax.getIdJoueur()) {
                        nbCaseMilieuMax++;
                    } else {
                        nbCaseMilieuMin++;
                    }
                }
            }
        }

        // Joueur Max a 1,2,3,4,5,6,7,8,9 case du milieu → 3,4,5,6,7,8,9,10,11
        if (nbCaseMilieuMax != 0) {
            if (nbCaseMilieuMax == 1) {
                return 3;
            }

            else if (nbCaseMilieuMax == 2) {
                return 4;
            }

            else if (nbCaseMilieuMax == 3) {
                return 5;
            }

            else if (nbCaseMilieuMax == 4) {
                return 6;
            }

            else if (nbCaseMilieuMax == 5) {
                return 7;
            }

            else if (nbCaseMilieuMax == 6) {
                return 8;
            }

            else if (nbCaseMilieuMax == 7) {
                return 9;
            }

            else if (nbCaseMilieuMax == 8) {
                return 10;
            }

            else if (nbCaseMilieuMax == 9) {
                return 11;
            }
        }

        // Joueur Min a 1,2,3,4,5,6,7,8,9 case du milieu → -3,-4,-5,-6,-7,-8,-9,-10,-11
        if (nbCaseMilieuMin != 0) {
            if (nbCaseMilieuMin == 1) {
                return -3;
            }

            else if (nbCaseMilieuMin == 2) {
                return -4;
            }

            else if (nbCaseMilieuMin == 3) {
                return -5;
            }

            else if (nbCaseMilieuMin == 4) {
                return -6;
            }

            else if (nbCaseMilieuMin == 5) {
                return -7;
            }

            else if (nbCaseMilieuMin == 6) {
                return -8;
            }

            else if (nbCaseMilieuMin == 7) {
                return -9;
            }

            else if (nbCaseMilieuMin == 8) {
                return -10;
            }

            else if (nbCaseMilieuMin == 9) {
                return -11;
            }
        }

        // Partie gagnée par le joueur qui cherche à minimiser la fonction Minmax → -100
        return -100;
    }

    public int minimax(Plateau _plateau, boolean isMaximizing, int depth, int alpha, int beta) {

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
                eval = minimax(_plateau, false, depth - 1, alpha, beta);
                _plateau.annuleDernierCoup();

                // On garde le plus grand score
                maxEval = max(eval, maxEval);
                alpha = max(alpha, eval);

                if (beta <= alpha) {
                    break;
                }
            }

            // On retourne le plus grand score possible pour chaque coup
            return maxEval;
        }

        // Si c'est au tout du joueur qui cherche à minimiser le score
        else {
            int minEval = 100;
            ArrayList<Coup> coupsMin = _plateau.getListeCoups(joueurMin);

            // Pour chacuns de ses coups
            for (int i = 0; i < coupsMin.size(); i++) {
                int eval;

                _plateau.joueCoup(coupsMin.get(i));
                // On attribue un score
                eval = minimax(_plateau, true, depth - 1, alpha, beta);
                _plateau.annuleDernierCoup();

                // On garde le plus petit score
                minEval = min(eval, minEval);
                beta = min(beta, eval);

                if (beta <= alpha) {
                    break;
                }
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

            // On joue ce coup
            _plateau.joueCoup(coups.get(i));
            // On évalue ce coup
            score = minimax(_plateau, false, 5, -20, 20);
            // On reset le coup
            _plateau.annuleDernierCoup();

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