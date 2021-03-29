package tictactoecodingame;

import java.util.ArrayList;

public class Node {
    private State state;
    private Node parent;
    private ArrayList<Node> childArray;

    public Node() {
        this.state = new State();
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
