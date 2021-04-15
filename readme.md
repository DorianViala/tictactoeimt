# TicTacToe

<img src="https://user-images.githubusercontent.com/25727549/114904864-991ab480-9e18-11eb-8f50-e112673487b0.gif" alt="tictactoe-gif" width="300">

## Sommaire

- [Algorithme de MonteCarlo](#MonteCarlo)
  - [Fonction de sélection](#fonction-de-sélection)
  - [Fonction expansion](#fonction-expansion)

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