# TicTacToe

## MonteCarlo

L'algorithme de MonteCarlo est une algorithme qui explore l'arbre des possibles. Il se déroule en 4 étapes principales qui sont répétées un grand nombre de fois. Ces 4 étapes sont les suivantes :

- la sélection
- l'expansion
- la simulation
- la rétropropagation

Dans notre code, ces 4 étapes se traduisent de la manière suivante :

```java
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
```

Dans premier temps, nous initialisons notre `arbre` avec comme root un `node`.

La classe `Node.java` stocke les informations suivantes:

```java
public class Node implements Comparable<Node> {
    private int nbVisite;
    private int scoreVictoire;
    private double uctScore;
    private Coup coup;
    private Node parent;
    private Joueur joueur;
    private ArrayList<Node> childArray;
}
```

### Fonction de sélection

```java
private Node selection(Node root, Plateau _plateau) {
        ArrayList<Node> childArray = new ArrayList<Node>();

        // on selection a chaque étage de l'arbre
        while (!root.getChildArray().isEmpty() && _plateau.partieTerminee() == false) {
            childArray = root.getChildArray();
            // update all uct scores
            childArray.forEach(node -> {
                node.updateUctScore(2);

            });

            // get node with max uct score
            root = Collections.max(childArray);
            _plateau.joueCoup(root.getCoup());
        }
        return root;
    }
```

Le but de la fonction sélection est de sélectionner succesivement les noeud de l'arbre jusqu'à arriver à une feuille. Pour sélectionner les nodes, on utilise le score UCT. Ce score permet de faire un compromis entre l'exploitation d'une node qui nous fait gagner souvent et l'exploration de node qui pourrait nous faire gagner.

Pour sélectionner le score UCT maximum, nous avons étendu la classe `Node` en `Comparable`. Cela nous permet de définir la fonction `CompareTo` dans notre Node qui va comparer le score UCT maximum.

```java
// Dans la classe Node.java
@Override
    public int compareTo(Node o) {
        if (this.getUCT() > o.getUCT()) {
            return 1;
        } else if (this.getUCT() < o.getUCT()) {
            return -1;
        }
        return 0;
    }
```

Ensuite, dans notre fonction sélection, il nous reste plus qu'à mettre à jour les scores UCT et sélectionner le node avec le score maximum.

```java
root = Collections.max(childArray);
```

Ensuite, dans notre fonction sélection, il nous reste plus qu'à mettre à jour les scores UCT et sélectionner le node avec le score maximum.

```java
root = Collections.max(childArray);
```

### Fonction de simulation

```java
  // permet de simuler une partie à partir d'une node
    private Joueur simulation(Node node, Plateau _plateau) {

        Joueur joueurEnCours = node.getJoueur();
        _plateau.joueCoup(node.getCoup());

        if (_plateau.vainqueur() == this.ennemi) {
            node.setScoreVictoire(Integer.MAX_VALUE);
            return _plateau.vainqueur();
        }

        while (!_plateau.partieTerminee()) {
            // changer le jouer en cours
            joueurEnCours = getJoueurEnnemi(joueurEnCours);
            ArrayList<Coup> coupPossible = _plateau.getListeCoups(joueurEnCours);
            // joue un coup aléatoire
            Coup coupjoue = coupPossible.get(rnd.nextInt(coupPossible.size()));
            _plateau.joueCoup(coupjoue);
        }
        return _plateau.vainqueur();
    }
```

Le but de cette fonction est de faire jouer les deux joueurs de manière aléatoire à partir d'une node particulière.
Tout d'abord, on récupère la node passée en paramètre (celle retourné par la pahse d'expension) et on **joue le coup** contenu dans cette node afin de **mettre le plateau à jour**.

Ensuite, on vérifie si, après ce coup, **la partie est terminée et si le vainqueur et notre ennemi** (ici `this.ennemi`). Si c'est le cas, on va attribuer comme score de victoire à cette node la valeur maximale `Integer.MAX_VALUE`. Cette action favorise l'algorithme à choisir cette node lorsque l'on se retrouvera dans une phase de séléction sur la node parent lors d'une autre itération, et donc de **toujours incrémenter le score de visite mais pas celui de victoire** sur toutes les nodes de cette branche lors de la phase de Backpropagation, ce qui entrainera l'algorithme à **choisir un autre coup que celui-la** lors de la phase de séléction au niveau de la root de l'arbre.

Sinon, tant que la partie terminée, la fonction change le joueur en cours avec `getJoueurEnnemi` :

```java
 private Joueur getJoueurEnnemi(Joueur joueurEnCours) {
        if (joueurEnCours == this.ennemi) {
            return this.bot;
        }
        return this.ennemi;
    }
```

,lui attribue ensuite un coup choisi aléatoirement dans sa liste de coups disponible :

```java
Coup coupjoue = coupPossible.get(rnd.nextInt(coupPossible.size()));
```

et enfin joue ce coup sur le plateau.

Lorsque la partie est terminée, cette fonction retourne le vainqueur.

### Fontion de back
