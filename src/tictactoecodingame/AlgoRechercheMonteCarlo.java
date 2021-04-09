package tictactoecodingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private double valeurSelection(int nbTotalVisite, double scoreVictoire, int nodeVisite) {
        if (nodeVisite == 0) {
            return Integer.MAX_VALUE;
        }
        return (scoreVictoire / (double) nodeVisite) + 1.41 * Math.sqrt(Math.log(nbTotalVisite) / (double) nodeVisite);
    }

    private Node trouverMeilleurNode(Node node) {
        int parentVisit = node.getState().getNbDeVisite();
        return Collections.max(node.getChildArray(), Comparator.comparing(
                c -> valeurSelection(parentVisit, c.getState().getScoreVictoire(), c.getState().getNbDeVisite())));
    }

    private Node selection(Node root) {
        Node node = root;
        while (node.getChildArray().size() != 0) {
            node = this.trouverMeilleurNode(root);
        }
        return new Node();
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        Arbre arbre = new Arbre();
        Node root = arbre.getRoot();
        root.getState().setJoueur(ennemi);
        root.getState().setPlateau(_plateau);

        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        for (int iter = 0; iter < this.max_iteration; iter++) {
            Node nodeSelectionne = this.selection(root);
        }

        return coups.get(rnd.nextInt(coups.size()));
    }

}
