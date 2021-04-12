package tictactoecodingame;

public class Arbre {
    Node root;

    public Arbre(Joueur joueur) {
        root = new Node(joueur);
    }

    public Node getRoot() {
        return this.root;
    }

    public void addChild(Node parent, Node enfant) {
        parent.getChildArray().add(enfant);
    }
}
