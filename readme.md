# TicTacToe

<img src="https://user-images.githubusercontent.com/25727549/114904864-991ab480-9e18-11eb-8f50-e112673487b0.gif" alt="tictactoe-gif" width="300">

## Sommaire

- [Algorithme de MonteCarlo](#MonteCarlo)
  - [Fonction de sélection](#fonction-de-sélection)
  - [Fonction expansion](#fonction-expansion)
  - [Fonction de simulation](#fonction-de-simulation)
  - [Fontion de backPropagation](#fonction-de-backpropagation)
  - [Fonction findBestChild](#fonction-findBestChild)

## MonteCarlo

L'algorithme de MonteCarlo est un algorithme qui explore l'arbre des possibles. Il se déroule en 4 étapes principales qui sont répétées un grand nombre de fois. Ces 4 étapes sont les suivantes :

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

Dans un premier temps, nous initialisons notre `arbre` avec comme root un `node`.

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

Le but de la fonction sélection est de sélectionner successivement les nœud de l'arbre jusqu'à arriver à une feuille. Pour sélectionner les nodes, on utilise le score UCT. Ce score permet de faire un compromis entre l'exploitation d'une node qui nous fait gagner souvent et l'exploration de node qui pourrait nous faire gagner.

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

### Fonction expansion

```java
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
```

La fonction expansion ajoute tous les coups possibles à partir de la position actuelle au node enfant que l'on a sélectionné précédemment. Pour ce faire, nous récupérons la liste des coups possible avec

```java
ArrayList<Coup> listeCoups = _plateau.getListeCoups(joueurEnCours);
```

A partir de ça, il nous suffit d'itérer à travers cette liste de coups et de créer les nodes enfants auxquels on associe le coup, le joueur qui joue ce coup et la node parent. Une fois que c'est fait, on associe la `ArrayList`, dans laquelle on stocke les nodes enfants, au node parent.

```java
node.setChildArray(newArrayChild);
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

### Fontion de backPropagation

```java
private void backPropagation(Node node, Joueur joueurGagnant, Plateau _plateau) {
        // permet de remonter tout l'arbre et d'y affecter
        // les scores correspondant a chaque node parcourut
        Node nodeAux = node;
        while (nodeAux != null) {
            nodeAux.incrementNbVisite();
            //si notre bot est le gagnant et si le joueur de cett
            //node est notre bot, alors on incrémente son score de victoire
            if (joueurGagnant == this.bot) {
                if (nodeAux.getJoueur() == this.bot) {
                    nodeAux.incrementScore();
                }
            }

            nodeAux = nodeAux.getParent();
        }
    }

```

Cette fonction permet de **remonter les scores de visites et de victoire** sur toutes les nodes de la branche. Pour cela, on définit une node auxilaire qui est la node en cours et on lui incrémente son score de visite et son score de victoire si et seulement si le joueur contenu dans cette node est notre bot et que le joueur gagnant est notre bot.

Ensuite, on définit la nodeAux comme étant le parent de la node en cours et on répète cette opération tant que la nodeAux éxiste ( diférente de null).

### Fonction findBestChild

```java
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
```

Cette fonction permet de trouver le meilleur coup à jouer lorsque l'algorithme a terminé de réaliser toutes les itérations pour générer l'arbre.

Tout simplement, elle retourne la node enfant de la node root de l'arbre qui possède le plus de visite. C'est le coup contenu dans cette node qui est le meilleur à jouer.
