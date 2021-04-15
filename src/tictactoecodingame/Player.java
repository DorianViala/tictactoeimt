package tictactoecodingame;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 *
 * @author franck
 */

/*--------------------------------------------------------*/
/* Version jeu en local */
/*--------------------------------------------------------*/
public class Player {

    public static void main(String args[]) {
        JoueurHumain humain = new JoueurHumain("codinggame");
        JoueurOrdi monteCarloJoueur = new JoueurOrdi("MonteCarlo");

        AlgoRechercheMonteCarlo monte = new AlgoRechercheMonteCarlo(humain, monteCarloJoueur);
        Jeton jetonMonte = new Jeton(monteCarloJoueur);
        Jeton jetonHumain = new Jeton(humain);
        GrilleTicTacToe3x3 grille3 = new GrilleTicTacToe3x3();

        // Remplacer ici l'algorithme aléatoire par votre algorithme.
        // Créer une nouvelle classe qui hérite de la class AlgoRecherche

        monteCarloJoueur.setAlgoRecherche(monte);

        Arbitre a = new Arbitre(grille3, monteCarloJoueur, humain);

        a.startNewGame(false); // Demarre une partie en affichant la grille du jeu

        // Pour lancer un tournooi de 100 parties en affichant la grille du jeu
        //
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            grille3.joueCoup(new CoupTicTacToe(opponentCol, opponentRow, jetonHumain));
            System.err.println(opponentCol + " " + opponentRow);
            int validActionCount = in.nextInt();
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
            }
            Coup coupMonteCarlo = monte.meilleurCoup(grille3, monteCarloJoueur, true);
            grille3.joueCoup(coupMonteCarlo);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(coupMonteCarlo);
        }

    }
}

/*--------------------------------------------------------*/
/* Version Codin game */
/*--------------------------------------------------------*/

/*
 * import java.util.Scanner;
 * 
 * 
 * 
 * class Player {
 * 
 * public static void main(String args[]) {
 * 
 * Scanner in = new Scanner(System.in);
 * 
 * CoupTicTacToe3x3 coup; JoueurHumain adversaire = new
 * JoueurHumain("Adversaire"); JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
 * 
 * AlgoRechercheAleatoire alea = new AlgoRechercheAleatoire( ); // L'ordinateur
 * joue au hasard joueurOrdi.setAlgoRecherche(alea);
 * 
 * GrilleTicTacToe3x3 grille = new GrilleTicTacToe3x3(); grille.init();
 * 
 * 
 * while (true) { int opponentRow = in.nextInt(); int opponentCol =
 * in.nextInt(); int validActionCount = in.nextInt(); for (int i = 0; i <
 * validActionCount; i++) { int row = in.nextInt(); int col = in.nextInt(); } if
 * ( opponentCol != -1 ) { coup = new CoupTicTacToe3x3(opponentCol, opponentRow,
 * new Jeton(adversaire)); grille.joueCoup(coup); }
 * 
 * coup = (CoupTicTacToe3x3) joueurOrdi.joue(grille); grille.joueCoup(coup);
 * System.out.println(coup.getLigne() + " " + coup.getColonne() );
 * System.out.flush(); } }
 * 
 * }
 */
