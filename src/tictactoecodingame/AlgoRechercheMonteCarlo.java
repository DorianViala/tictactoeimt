package tictactoecodingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AlgoRechercheMonteCarlo extends AlgoRecherche {
    Random rnd;

    private int max_iteration;
    private Joueur ennemi;
    private Joueur bot;

    public AlgoRechercheMonteCarlo(Joueur ennemi, Joueur bot) {
        rnd = new Random();
        this.ennemi = ennemi;
        this.bot = bot;
        this.max_iteration = 1000;
    }

    public AlgoRechercheMonteCarlo(int max_iteration, Joueur ennemi) {
        rnd = new Random();
        this.ennemi = ennemi;
        this.max_iteration = max_iteration;
    }

    private Node selection(Node n) {
        Node root = n;
        ArrayList<Node> childArray = new ArrayList<Node>();
        // on selection a chaque depth
        while (!root.getChildArray().isEmpty()) {
            childArray = root.getChildArray();

            // update all uct scores
            childArray.forEach(node -> {
                node.updateUctScore(Math.sqrt(2));
            });

            // get node with max uct score
            root = Collections.max(childArray);
        }
        return root;
    }

    private void expension(Node node, Plateau _plateau, Joueur _joueur) {
        // on expend que si on a deja visite la node
        // sinon on rollout sur cette node
        if (node.getNbVisite() != 0) {
            ArrayList<Coup> listeCoups = _plateau.getListeCoups(_joueur);
            ArrayList<Node> newArrayChild = new ArrayList<Node>();
            listeCoups.forEach(coup -> {
                Node tmpNode = new Node(coup, node.getParent());
                newArrayChild.add(tmpNode);
            });
            node.setChildArray(newArrayChild);
        }
    }

    private Joueur simulation(Node node, Plateau _plateau) {
        // permet de simuler une partie à partir de cette node
        Joueur joueurEnCours = node.getJoueur();
        Random coup = new Random();
        if (_plateau.partieGagnee()) {
            return _plateau.vainqueur();
        }
        while (!_plateau.partieTerminee()) {
            // changer le jouer en cours
            if (joueurEnCours == this.ennemi) {
                joueurEnCours = this.bot;
            } else {
                joueurEnCours = this.ennemi;
            }
            ArrayList<Coup> coupPossible = _plateau.getListeCoups(joueurEnCours);

            // joue un coup aléatoire
            _plateau.joueCoup(coupPossible.get(coup.nextInt(coupPossible.size())));
        }
        return _plateau.vainqueur();
    }

    private void backPropagation(Node node, Joueur joueurGagnant) {
        // permet de remonter tout l'arbre et d'y affecter
        // les scores correspondant a chaque node parcourut
        Node nodeAux = node;
        while (nodeAux != null) {
            nodeAux.incrementScore();
            // si la node est l'opposant, on augmente son score
            if (nodeAux.getJoueur() != joueurGagnant) {
                nodeAux.incrementScore();
            }
            nodeAux = nodeAux.getParent();
        }
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        Arbre arbre = new Arbre();
        Node root = arbre.getRoot();
        _plateau.sauvegardePosition(0);
        for (int iter = 0; iter < this.max_iteration; iter++) {
            // phase 1 : selection
            Node nodeSelectionne = this.selection(root);

            // phase 2 : expension
            this.expension(nodeSelectionne, _plateau, _joueur);

            // retore le plateau
            _plateau.restaurePosition(0);
        }

        return coups.get(rnd.nextInt(coups.size()));
    }

}
