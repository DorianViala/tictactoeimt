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

    private Node selection(Node root){
        ArrayList<Node> childArray = root.getChildArray();
        ArrayList<double> uctScores = new ArrayList<double>;
        childArray.forEach(node -> {
            double nodeScore = valeurSelection(10, node.getScoreVictoire, node.getNbVisite);
            uctScores.add(nodeScore);
        })
    }

    private void expension(Node node) {
        if(node.getState().plateauPartieEnCours()){
           ArrayList<Coup> tousLesCoups = node.getState().getPlateau().getListeCoups();
           tousLesCoups.forEach(coup ->{
               Plateau plateauCopy = new GrilleTicTacToe3x3()
           })
        }
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        Arbre arbre = new Arbre();
        Node root = arbre.getRoot();

        for (int iter = 0; iter < this.max_iteration; iter++) {
            // selection
            Node nodeSelectionne = this.selection(root);

            // expension 
            this.expension(nodeSelectionne);
        }

        return coups.get(rnd.nextInt(coups.size()));
    }

}
