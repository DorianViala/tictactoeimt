package tictactoecodingame;

import java.util.ArrayList;
import java.util.Random;

public class AlgoRechercheMonteCarlo extends AlgoRecherche {
    Random rnd;

    private int max_iteration;
    private Joueur ennemi;

    public AlgoRechercheMonteCarlo(Joueur ennemi) {
        rnd = new Random();
        this.ennemi = ennemi;
        this.max_iteration = 1000;
    }

    public AlgoRechercheMonteCarlo(int max_iteration, Joueur ennemi) {
        rnd = new Random();
        this.ennemi = ennemi;
        this.max_iteration = max_iteration;
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        Arbre arbre = new Arbre();
        Node root = arbre.getRoot();
        root.getState().setJoueur(ennemi);
        root.getState().setPlateau(_plateau);

        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);

        return coups.get(rnd.nextInt(coups.size()));
    }

}
