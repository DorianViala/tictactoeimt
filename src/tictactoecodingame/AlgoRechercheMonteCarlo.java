package tictactoecodingame;

import java.util.ArrayList;
import java.util.Collections;
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
        this.max_iteration = 5000;
    }

    public AlgoRechercheMonteCarlo(int max_iteration, Joueur ennemi) {
        rnd = new Random();
        this.ennemi = ennemi;
        this.max_iteration = max_iteration;
    }

    private Joueur getJoueurEnnemi(Joueur joueurEnCours) {
        if (joueurEnCours == this.ennemi) {
            return this.bot;
        }
        return this.ennemi;
    }

    private Node selection(Node root, Plateau _plateau) {
        ArrayList<Node> childArray = new ArrayList<Node>();

        // on selection a chaque étage de l'arbre
        while (!root.getChildArray().isEmpty() && _plateau.partieTerminee() == false) {
            childArray = root.getChildArray();
            // update all uct scores
            childArray.forEach(node -> {
                node.updateUctScore(Math.sqrt(2));

            });

            // get node with max uct score
            root = Collections.max(childArray);
            _plateau.joueCoup(root.getCoup());
        }
        return root;
    }

    private Node expension(Node node, Plateau _plateau) {
        // on expend que si on a deja visite la node
        // sinon on rollout sur cette node
        if (_plateau.partieTerminee() == false) {
            Joueur joueurEnCours = getJoueurEnnemi(node.getJoueur());
            ArrayList<Coup> listeCoups = _plateau.getListeCoups(joueurEnCours);
            ArrayList<Node> newArrayChild = new ArrayList<Node>();

            for (int i = 0; i < listeCoups.size(); i++) {
                Node tmpNode = new Node(listeCoups.get(i), node, joueurEnCours);
                newArrayChild.add(tmpNode);

            }
            node.setChildArray(newArrayChild);
            return newArrayChild.get(0);
        }

        return node;

    }

    private Joueur simulation(Node node, Plateau _plateau) {
        // System.out.println(_plateau);

        // permet de simuler une partie à partir de cette node
        Joueur joueurEnCours = node.getJoueur();
        _plateau.joueCoup(node.getCoup());

        while (!_plateau.partieTerminee()) {
            // changer le jouer en cours
            // System.out.println(nbCoup++);
            joueurEnCours = getJoueurEnnemi(joueurEnCours);
            ArrayList<Coup> coupPossible = _plateau.getListeCoups(joueurEnCours);
            // joue un coup aléatoire
            Coup coupjoue = coupPossible.get(rnd.nextInt(coupPossible.size()));
            // System.out.println(joueurEnCours);
            // System.out.println(coup.nextInt(coupPossible.size()));
            _plateau.joueCoup(coupjoue);
            // System.out.println(_plateau);

        }
        // System.out.println(_plateau.vainqueur());
        return _plateau.vainqueur();
    }

    private void backPropagation(Node node, Joueur joueurGagnant, Plateau _plateau) {
        // permet de remonter tout l'arbre et d'y affecter
        // les scores correspondant a chaque node parcourut
        Node nodeAux = node;
        while (nodeAux != null) {
            nodeAux.incrementNbVisite();
            // si la node est l'opposant, on augmente son score
            // on vérifie que il n'y a pas eu égalité et si le joueur de node n'est pas le
            // meme que le joueur gagnant
            if (joueurGagnant == this.bot) {
                if (nodeAux.getJoueur() == this.bot) {
                    nodeAux.incrementScore();
                }
            }

            nodeAux = nodeAux.getParent();
        }
    }

    public Node findBestChild(Node root) {
        ArrayList<Node> children = root.getChildArray();
        int max = 0;
        Node bestNode = new Node();
        for (int i = 0; i < children.size(); i++) {

            if (children.get(i).getNbVisite() > max) {
                max = children.get(i).getNbVisite();
                bestNode = children.get(i);
            }
        }

        return bestNode;
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
        Arbre arbre = new Arbre(this.ennemi);
        Node root = arbre.getRoot();
        // root.incrementNbVisite();
        Node newNode;
        Joueur gagnant;
        _plateau.sauvegardePosition(0);
        for (int iter = 0; iter < this.max_iteration; iter++) {

            // phase 1 : selection
            Node nodeSelectionne = this.selection(root, _plateau);

            // phase 2 : expension
            newNode = this.expension(nodeSelectionne, _plateau);

            // phase 3 : simulation
            gagnant = this.simulation(newNode, _plateau);

            // phase 4 : backpropagation
            this.backPropagation(nodeSelectionne, gagnant, _plateau);
            // retore le plateau

            _plateau.restaurePosition(0);

        }
        // fonction qui return
        return findBestChild(root).getCoup();
    }

}
