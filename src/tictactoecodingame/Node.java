package tictactoecodingame;

import java.util.ArrayList;

public class Node {
    private int nbVisite; 
    private int scoreVictoire;
    private Coup coup;
    private Node parent;
    private ArrayList<Node> childArray;

    public Node() {
        this.childArray = new ArrayList<>();
    }

    public Node(Coup coup, Node parent){
        this.coup = coup;
        this.parent = parent;
        this.childArray = new ArrayList<>();
    }

    public State getState() {
        return this.state;
    }

    public Node getParent() {
        return this.parent;
    }

    public ArrayList<Node> getChildArray() {
        return this.childArray;
    }

    public void setChildArray(ArrayList<Node> childArray) {
        this.childArray = childArray;
    }
}
