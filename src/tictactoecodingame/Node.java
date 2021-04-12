package tictactoecodingame;

import java.util.ArrayList;

public class Node {
    private int nbVisite;
    private int scoreVictoire;
    private double uctScore;
    private Coup coup;
    private Node parent;
    private ArrayList<Node> childArray;

    public Node() {
        this.uctScore = 0;
        this.childArray = new ArrayList<>();
    }

    public Node(Coup coup, Node parent) {
        this.uctScore = 0;
        this.coup = coup;
        this.parent = parent;
        this.childArray = new ArrayList<>();
    }

    public Node getParent() {
        return this.parent;
    }

    public int getNbVisite() {
        return this.nbVisite;
    }

    public void incrementNbVisite() {
        this.nbVisite++;
    }

    public void incrementScore() {
        this.scoreVictoire++;
    }

    public int getScoreVictoire() {
        return this.scoreVictoire;
    }

    public Coup getCoup() {
        return this.coup;
    }

    public double getUCT() {
        return this.uctScore;
    }

    public ArrayList<Node> getChildArray() {
        return this.childArray;
    }

    public void setCoup(Coup coup) {
        this.coup = coup;
    }

    public void setChildArray(ArrayList<Node> childArray) {
        this.childArray = childArray;
    }

    public void updateUctScore(double C) {
        double exploitation = this.scoreVictoire / this.nbVisite;
        double exploration = C * Math.sqrt(Math.log(this.parent.getNbVisite() / this.nbVisite));
        this.uctScore = exploitation + exploration;
    }
}
