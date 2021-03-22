# Algorithme MinMax et Monte Carlo
# Objectif

Votre objectif sera d’implémenter l’algorithme MinMax et l’algorithme MonteCarlo Tree Search sur le jeu TicTacToe présent sur la plateforme [Codingame](https://www.codingame.com/start). Vous partirez d’un code Java existant afin de vous focaliser essentiellement sur ces algorithmes. 

# Récupérer le code Java

Les sources sont sur [bitbucket](www.bitbucket.org). L’adresse du repository est la suivante :

```bash
git clone https://francktempet@bitbucket.org/Num-ILD/tictactoe.git
```

## Quelques explications sur le code

### La classe Player

Cette classe contient la méthode **main** du programme. Elle crée un joueur «*humain*» ainsi qu’un joueur «*Ordinateur*». L’ordinateur joue les coups de manière aléatoire.   

### La classe Plateau

Regarder les méthodes contenues dans cette classe ainsi que les commentaires associés. Cette classe est une classe abstraite qui liste les méthodes nécessaires à la manipulation d’un jeu de plateau à deux joueurs. Vous aurez besoin essentiellement des méthodes suivantes :
\newpage

>- partieTerminee()
>- getListeCoups()
>- vainqueur()
>- joueCoup()
>- sauvegardePosition()
>- restaurePosition()
>- annuleDernierCoup()
>- ….


### La classe GrilleTicTacToe3x3

Cette classe représente la grille du jeu TicTacToe de dimension 3x3. Cette classe hérite de Plateau, elle implémente donc toutes les méthodes abstraites présentes dans la classe Plateau. C’est cette grille que vous utiliserez dans un premier temps.

### La classe GrilleTicTacToe9x9

Cette classe représente la grille du jeu TicTacToe de dimension 9x9. Cette classe hérite de Plateau, elle implémente donc toutes les méthodes abstraites présentes dans la classe Plateau. Cette grille sera utilisée lorsque vous aurez franchi le premier niveau sur Codingame.

# Implémenter l'algorithme

Regardez en détail le contenu de la classe **Player**. La création de l’IA ( *joueurOrdi* ) nécessite  d’appeler la méthode **setAlgoRecherche** en lui passant en paramètre une instance d’une classe qui implémente l’interface **AlgoRecherche**. Dans notre cas c'est la classe **AlgoRechercheAleatoire** qui implémente cette interface. Elle implémente donc la méthode suivante :

```java
public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder)
```

On doit passer à cette méthode le plateau du jeu ( *ici GrilleTicTacToe3x3* ) , le joueur à qui c'est le tour de jouer et un dernier paramètre qui indique si l'ordinateur peut analyser la position lorsque c'est à son adversaire de jouer.

L'algorithme aléatoire récupère la liste des coups possibles à partir de la position courante et en choisit un au hasard.

```java
Random rnd;

public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {    

    ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);   
    return coups.get(rnd.nextInt( coups.size()));
}
```


Vous devrez donc écrire soit une classe AlgoMinMax soit une classe AlgoMCTS. Ces deux classes devront implémenter également l’interface *AlgoRecherche* ce qui revient à écrire la méthode *meilleurCoup*.



# Tester son IA en local

La classe Player actuelle vous permet de jouer contre votre IA. Vous pouvez  modifier facilement cette classe afin de faire jouer deux IA l’une contre l’autre.

# Tester son IA sur Codingame

Afin de tester votre IA sur *Codingame*, il vous faudra modifier le contenu de la classe **Player** pour être compatible avec le mode de fonctionnement de la plateforme, à savoir récupérer les données en entrée ( code déjà fourni par le site ) et retourner sur la sortie standard votre coup. La classe **Arbitre** ne sera plus nécessaire puisque se sera la plateforme Codingame qui fera office d'arbitre.  La classe **Player** contient également la version pour le bon fonctionnement de votre IA sur Codingame. Il suffit de dé-commenter la partie *Codingame* et commenter le jeu en local


Pour générer un seul fichier à partir de l’ensemble de vos classes  vous utiliserez le **builder.jar** en effectuant les commandes suivantes.

Lancez l’invite de commande **cmd.exe** et  tapez les commandes suivantes :
```bash 
# C est la lettre du disque dur où se trouve votre projet 
C: 

# Se positionner dans le répertoire src du projet
# exemple cd c:\netbean\TicTacToeCodingame\src
cd <nom_du_répertoire source>  

# Lancer le builder.jar
# Cette commande va générer un fichier Player.java dans le répertoire courant.
# C’est ce fichier qu’il faudra poster sur codingame.

java –jar builder.jar .\tictactoecodingame\Player.java 
```
# Amélioration des algorithmes

Lorsque vos algorithmes MinMax et MCTS seront implémentés et fonctionneront correctement, votre objectif sera de rechercher les possibilités d'améliorations de ces algorithmes. Vous indiquerez le résultat de vos recherches et les implémenterez afin d'améliorer votre IA.



