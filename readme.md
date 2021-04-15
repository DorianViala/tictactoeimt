# TicTacToe

<img src="https://user-images.githubusercontent.com/25727549/114904864-991ab480-9e18-11eb-8f50-e112673487b0.gif" alt="tictactoe-gif" width="300">

## Sommaire

- [Résultats MonteCarlo vs Minimax](#résultats-montecarlo-vs-minimax)
  - [Plateau 9x9](#plateau-9x9)
  - [Plateau 3x3](#plateau-3x3)
- [Algorithme de MonteCarlo](#MonteCarlo)

  - [Fonction de sélection](#fonction-de-sélection)
  - [Fonction expansion](#fonction-expansion)
  - [Fonction de simulation](#fonction-de-simulation)
  - [Fontion de backPropagation](#fonction-de-backPropagation)
  - [Fonction findBestChild](#fonction-findBestChild)

- [Algorithme MinMax](#MinMax)

  - [Fonctionnement de l'algorithme](#fonctionnement-minmax)
  - [Pruning](#pruning)
  - [Implémentation](#implémentation)
  - [Résulat contre Bot Aléatoire](#Résulat-contre-Aléatoire)

- [Nos sources](#nos-sources)
  - [Sites internet](#sites-internet)
  - [Vidéos Youtube](#vidéos-Youtube)

## Résultats MonteCarlo vs Minimax

### Plateau 9x9

<img src="https://user-images.githubusercontent.com/25727549/114910439-35938580-9e1e-11eb-8fda-efae1c605819.png" alt="tictactoe-score" width="300">

```java
   public static void main(String args[]) {

        JoueurOrdi minimaxJoueur = new JoueurOrdi("Minimax");
        JoueurOrdi monteCarloJoueur = new JoueurOrdi("MonteCarlo");

        AlgoMiniMax9x9 minmax = new AlgoMiniMax9x9(minimaxJoueur, monteCarloJoueur);
        AlgoRechercheMonteCarlo monte = new AlgoRechercheMonteCarlo(minimaxJoueur, monteCarloJoueur);

        GrilleTicTacToe9x9 grille9 = new GrilleTicTacToe9x9();

        // Remplacer ici l'algorithme aléatoire par votre algorithme.
        // Créer une nouvelle classe qui hérite de la class AlgoRecherche

        monteCarloJoueur.setAlgoRecherche(monte);
        minimaxJoueur.setAlgoRecherche(minmax);

        Arbitre a = new Arbitre(grille9, monteCarloJoueur, minimaxJoueur);

        // a.startNewGame(true); // Demarre une partie en affichant la grille du jeu

        // Pour lancer un tournoi de 100 parties en affichant la grille du jeu
        //
        a.startTournament(20, false);

    }
}

```

### Plateau 3x3

<img src="https://user-images.githubusercontent.com/25727549/114910807-9e7afd80-9e1e-11eb-8b98-9a42fc7f3db1.png" alt="tictactoe-score" width="300">

```java

    public static void main(String args[]) {

        JoueurOrdi minimaxJoueur = new JoueurOrdi("Minimax");
        JoueurOrdi monteCarloJoueur = new JoueurOrdi("MonteCarlo");

        AlgoMinimax minmax = new AlgoMinimax(minimaxJoueur, monteCarloJoueur);
        AlgoRechercheMonteCarlo monte = new AlgoRechercheMonteCarlo(minimaxJoueur, monteCarloJoueur);

        GrilleTicTacToe3x3 grille3 = new GrilleTicTacToe3x3();

        // Remplacer ici l'algorithme aléatoire par votre algorithme.
        // Créer une nouvelle classe qui hérite de la class AlgoRecherche

        monteCarloJoueur.setAlgoRecherche(monte);
        minimaxJoueur.setAlgoRecherche(minmax);

        Arbitre a = new Arbitre(grille3, monteCarloJoueur, minimaxJoueur);

        // a.startNewGame(true); // Demarre une partie en affichant la grille du jeu

        // Pour lancer un tournoi de 100 parties en affichant la grille du jeu
        //
        a.startTournament(200, false);

    }
```

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

## MinMax

### Fonctionnement MinMax

L'algorithme MinMax est un algorithme qui s'applique pour les jeux à 2 joueurs et à information complète.

Il amène l'ordinateur à tester tous les coups possibles et à leur attribuer un score. L'ordinateur sélectionnera le coup avec le meilleur score.

Pour affecter à un coup un score, l'algorithme va alterner entre chaque joueur un certain nombre de fois. Ce nombre de fois est déterminé par le seul et unique paramètre de l'algorithme MinMax : la **profondeur** de l'arbre des possibles.

On a l'habitude de représenter l'ensembe des disposition de plateau par un graphe, et à chaque noeud correspond un état de plateau.

![arbre](https://user-images.githubusercontent.com/81106609/114912076-df274680-9e1f-11eb-8472-8630151d8906.PNG)

On observe sur ce graphe que plus il est profond, plus il y a d'état de plateau, et donc qu'on a joué de coup, et donc qu'on a calculé de coups. Ce nombre de coup croit de façon exponentielle, il arrivera donc des cas de figure où la charge de travail sera trop importante. C'est pourquoi on introduit la notion de profondeur limite dans l'algorithme MinMax. On introduit un cas d'arrêt : l'ordinateur calcule des états de plateau, jusqu'a une certaine profondeur.

Pour construire cet arbre, il faut alterner de joueur (les couleurs sur ce schéma représentent un joueur différent). MinMax est basé sur ce changment de joueur. Il utilise le fait qu'un joueur cherche à maximiser l'état d'un plateau et l'autre cherche à le minimiser.

Cette alternance et le cas d'arrêt correspond exactement à un algorithme récursif. On appellera l'algorithme MinMax en comptant la profondeur restante et en changeant de joueur.

Quand on a atteint la profondeur maximale décidée lors de l'implémentation de l'algorithme ou qu'il n'y a plus de coup à jouer (donc que la partie est terminée), on effectue une évaluation statique du plateau.
Cela veut dire que l'on regarde l'état du plateau, et on y attribue un score, c'est ce score qui sera retourné et qui nous dirigera vers le choix du coup.
Cette évaluation du tableau est centrale dans l'algorithme MinMax. On défini pour cela une fonction d'évaluation qui prend en paramètre un plateau.

On a choisi pour le morpion 3x3 cette fonction d'évaluation:

- Le joueur Max gagne → 1 point
- Égalité → 0 point
- Le joueur Min gagne → -1 point

Notez que la partie est forcément terminée pour calculer ces scores. Cela n'est pas un problème car il y a suffisament peu de combinaison de coups possible pour un morpion 3x3 pour être calculé.

Cela est différent pour la grille 9x9 on a donc choisi d'implémenter cette fonction d'évaluation statique :

- Le joueur Max gagne → 100 points
- Égalité → 0 point
- Le joueur Max a 1,2,3,4,5,6,7,8 grille 3x3 gagnées → 52,53,54,55,56,57,58,59 points
- Le joueur Max a 1,2,3,4,5,6,7,8,9 case du milieu → 2,3,4,5,6,7,8,9,10 points
- L'exact opposé pour le joueur Min

On peut alors consruire notre arbre en ayant dans chaque noeud un score. Chaque noeud blanc (c'est au joueur Max de jouer) ayant un noeud enfant, retourne la plus grand valeur possible de ses noeuds enfant, le contraire pour les noeuds noirs.
![arbrescore](https://user-images.githubusercontent.com/81106609/114916499-27953300-9e25-11eb-9918-1760e2a44ea7.PNG)

On observe que tout en haut de l'arbre, c'est au joueur Max de joueur. Il a le choix entre 2 coups :

- 1 qui au mieux lui retournera un plateau de score 3
- L'autre qui lui retournera au mieu un plateau de score -4

→ Le jouer Max cherchant à maximiser l'évaluation statique du plateau, prendra donc le coup de gauche, de valeur 3.

Ce sera alors au joueur de blanc de jouer.
Et on rappellera l'algorithme MinMax pour qu'il redonne une évaluation des 9-2 coups restants dans le cadre du morpion 3x3 et lors du 2ème appel de la fonction.

Le pseudo code de l'algorthme MinMax se retrouve donc ici :
![pseudocodeminmax](https://user-images.githubusercontent.com/81106609/114917297-1567c480-9e26-11eb-9142-785e5e1069e2.PNG)

### Pruning

On peut voir sur l'image précédente qu'il y a beaucoup de calcul qui ont été effectués et pas utilisés. On peut donc optimiser l'algorithme. C'est le but du _pruning_, élagage en français.

Explicitons le raisonnmement :
On se place sur le noeud noir de gauche ayant pour score 3, à la profondeur 2 en partant du haut. On sait que ce joueur cherche à Minimiser l'évaluation du plateau. Pendant le calcul des coups, on est forcé de passer d'abord par son noeud enfant de gauche (3 blanc) et ensuite par son noeud enfant de droite (5 blanc). Or, on a dit qu'il cherche à minimiser le score, il prendra, quel que soit le résultat qui sera retourné par le noeud blanc de doite, le noeud de gauche, de valeur 3. En effet celui-ci cherche à maximiser le score, donc pour lui, dans tous les cas, il retournera une valeur plus grande que 5, ce qui n'interressera pas le joueur Noir. Cela ne sert donc à rien de calculer les nodes extrêmes noires de valeur 5 et 1, on passera directement au calcul des nodes de la partie droite de l'arbre.

![pruning](https://user-images.githubusercontent.com/81106609/114918691-a8552e80-9e27-11eb-8631-30eaec9e568b.PNG)

Pour implémenter ce "pruning" on va donc ajouter 2 paramètres à la fonction MinMax : Alpha et Beta, qui auront respectivement une valeur extrême négative et extrême positive lors du 1er appel. Ils auront comme utilité de garder en mémoire pour les noeuds parents les valeurs Maximum et Minimum de leur noeuds enfants.

Voyez plutôt le pseudo-code. Je vous invite à essayer de créer vous-même un arbre pour voir l'importance de l'alternance joueur cherchant à maximiser l'évaluation statique du plateau / joueur cherchant à minimiser l'évaluation statique du plateau, et à comment on en retire le coup idéal pour chaque joueurs :
![pseudocode](https://user-images.githubusercontent.com/81106609/114919540-a93a9000-9e28-11eb-9f46-13dc8212f9b2.png)

### Implémentation

Pour instancier la classe AlgoMiniMax9x9 et 3x3 il faut mettre en paramètre les 2 joueurs dans cet ordre :
Le joueur qui appelle la méthode MinMax, donc le joueur Max est en premier.

```java
    private Joueur joueurMax;
    private Joueur joueurMin;

    public AlgoMiniMax9x9(Joueur _joueur1, Joueur _joueur2) {
        this.joueurMax = _joueur1;
        this.joueurMin = _joueur2;
    }
```

La classe Player cherche en instanciant les joueur Ordi à faire appel à la fonction meilleurCoup qui retournera un type Coup déterminé par l'algorithme MinMax.
L'algorithme prend en paramètre le plateau actuel, que c'est au joueur Min de jouer, une **_profondeur de 5_**, une valeur d'alpha et beta respectivement -20 et 20.
Je ne présente ici que le strict nécessaire, veuillez vous réferer au code source, qui est proprement commenté pour le détail des méthodes.

```java
public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {

            score = minimax(_plateau, false, 5, -20, 20);

    }
```

Pour la fonction d'évaluation, on a dû implémenter 2 méthodes dans les classes GrilleTicTacToe3x3 et GrilleTicTacToe9x9. Elles serviront au calcul du nombre de grille3x3 gagnées et de cases de milieu gagnées.

```java
public Jeton[][] getGrille3x3() {
    return this.grille3x3;
}

public Jeton[][] getGrille9x9() {
    return this.grille9x9;
}
```

A chaque appel récursif, on change la valeur du booléen "isMaximizing" et on décrémente la valeur de la profondeur.

```java
if (isMaximizing){
eval = minimax(_plateau, false, depth - 1, alpha, beta);
} else {
eval = minimax(_plateau, true, depth - 1, alpha, beta);
}
```

### Résulat contre Aléatoire

#### Morpion 3x3

- 1000 parties
- Depth 10

- Aléatoire score : 50.5
- Minimax score : 949.5
- Minimax GAGNE
- Parties nulles : 101

- Temps pris : 45 608ms (sans pruning)

#### Morpion 9x9

- 100 parties

##### Depth 7

- Random score : 17.5
- Minimax score : 82.5
- Minimax GAGNE
- Parties nulles : 1
- Temps pris : 177 434ms (avec pruning)

##### Depth 8

- Random score : 22.5
- Minimax score : 77.5
- Minimax GAGNE
- Parties nulles : 1
- Temps pris : 599 461ms (avec pruning)

## Nos sources

Vous trouverez ci-dessous une liste de nos principales sources pour réaliser ce projet :

### Sites internet

- [Wikipédia ](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search#Pure_Monte_Carlo_game_search)
- [SANDIPANWEB](https://sandipanweb.wordpress.com/2017/03/26/using-monte-carlo-simulation-to-play-tictactoe-in-computer/)
- [Deakos Data Science](http://matthewdeakos.me/2018/03/10/monte-carlo-tree-search/)
- [Baeldung](https://www.baeldung.com/java-monte-carlo-tree-search)

### Vidéos Youtube

- [SANDIPANWEB](https://sandipanweb.wordpress.com/2017/03/26/using-monte-carlo-simulation-to-play-tictactoe-in-computer/)
- [Monte Carlo Tree Search](https://www.youtube.com/watch?v=UXW2yZndl7U&ab_channel=JohnLevine)
- [Algorithms Explained – minimax and alpha-beta pruning ](https://www.youtube.com/watch?v=l-hh51ncgDI&ab_channel=SebastianLague)
