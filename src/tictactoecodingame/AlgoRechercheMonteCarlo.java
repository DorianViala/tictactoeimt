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
        this.max_iteration = 1000;
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

    private Node selection(Node n, Plateau _plateau) {
        Node root = n;
        ArrayList<Node> childArray = new ArrayList<Node>();

        // au début, la node root n'a pas de fils
        if (root.getChildArray().isEmpty()) {
            return root;
        }

        // on selection a chaque depth
        while (!root.getChildArray().isEmpty()) {
            childArray = root.getChildArray();
            if (root.getCoup() != null) {
                _plateau.joueCoup(root.getCoup());
            }
            // update all uct scores
            childArray.forEach(node -> {
                node.updateUctScore(Math.sqrt(2));

                /*
                 * System.out.println("node victoire : " + node.getScoreVictoire() +
                 * " node visite : " + node.getNbVisite() + " node parent : " +
                 * node.getParent().getNbVisite() + " node utc : " + node.getUCT());
                 */

            });

            // 00System.out.println("\n----------\n");

            // get node with max uct score
            // root = Collections.max(childArray);
            double max = -1;
            Node finalNode = new Node();
            for (int i = 0; i < childArray.size(); i++) {
                if (childArray.get(i).getUCT() > max) {
                    max = childArray.get(i).getUCT();
                    finalNode = childArray.get(i);
                }
            }
            root = finalNode;
            // System.out.println(root);
        }
        // System.out.println("selected : " + root);
        _plateau.joueCoup(root.getCoup());
        return root;
    }

    private void expension(Node node, Plateau _plateau, Joueur _joueur) {
        // on expend que si on a deja visite la node
        // sinon on rollout sur cette node
        if (node.getNbVisite() != 0 && !_plateau.partieTerminee()) {

            ArrayList<Coup> listeCoups = _plateau.getListeCoups(_joueur);
            ArrayList<Node> newArrayChild = new ArrayList<Node>();
            Joueur joueurEnCours = getJoueurEnnemi(node.getJoueur());
            // System.out.println("nouvelle expension");
            // System.out.println("nbre coup possible" +
            // _plateau.getListeCoups(_joueur).size());
            listeCoups.forEach(coup -> {

                Node tmpNode = new Node(coup, node, joueurEnCours);
                // System.out.println("joueur en, cours : " +
                // getJoueurEnnemi(node.getJoueur()));
                newArrayChild.add(tmpNode);
            });
            node.setChildArray(newArrayChild);
            // System.out.println("taille enfant node " + node.getChildArray().size());

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
            joueurEnCours = getJoueurEnnemi(joueurEnCours);
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
            nodeAux.incrementNbVisite();
            // si la node est l'opposant, on augmente son score
            // passage++;
            // System.out.println("passage" + passage);
            // System.out.println("joueur en cours: " + nodeAux.getJoueur());
            // System.out.println("joueur gagant: " + joueurGagnant);

            // on vérifie que il n'y a pas eu égalité et si le joueur de node n'est pas le
            // meme que le joueur gagnant
            if (nodeAux.getJoueur() != joueurGagnant && joueurGagnant != null) {
                nodeAux.incrementScore();
            }
            nodeAux = nodeAux.getParent();
        }
    }

    public Node findBestChild(Node root) {
        ArrayList<Node> children = root.getChildArray();
        int max = 0;
        Node bestNode = new Node();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getScoreVictoire() > max) {
                max = children.get(i).getScoreVictoire();
                bestNode = children.get(i);
            }
        }
        return bestNode;
    }

    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

        Arbre arbre = new Arbre();
        Node root = arbre.getRoot();
        root.incrementNbVisite();
        Joueur gagnant;
        _plateau.sauvegardePosition(0);
        for (int iter = 0; iter < this.max_iteration; iter++) {
            // phase 1 : selection
            Node nodeSelectionne = this.selection(root, _plateau);

            // phase 2 : expension
            this.expension(nodeSelectionne, _plateau, _joueur);

            // phase 3 : simmulation
            gagnant = this.simulation(nodeSelectionne, _plateau);

            // phase 4 : backpropagation

            this.backPropagation(nodeSelectionne, gagnant);
            // retore le plateau
            _plateau.restaurePosition(0);

            // System.out.println("ITER : " + iter);
        }
        // fonction qui return
        return findBestChild(root).getCoup();
    }

}
