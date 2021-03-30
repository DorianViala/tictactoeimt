package tictactoecodingame;

public class Arbre {
    Node root;

    public Arbre() {
        root = new Node();
    }

    public Node getRoot() {
        return this.root;
    }

    public void addChild(Node parent, Node enfant) {
        parent.getChildArray().add(enfant);
    }
}
