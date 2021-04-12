package tictactoecodingame;

import java.util.ArrayList;

public class Node implements Comparable<Node> {
    private int nbVisite;
    private int scoreVictoire;
    private double uctScore;
    private Coup coup;
    private Node parent;
    private Joueur joueur;
    private ArrayList<Node> childArray;

    public Node() {
        this.uctScore = 0;
        this.childArray = new ArrayList<>();
    }

    public Node(Coup coup, Node parent, Joueur joueur) {
        this.uctScore = 0;
        this.joueur = joueur;
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

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public Joueur getJoueur() {
        return this.joueur;
    }

    public void updateUctScore(double C) {
        if (this.nbVisite == 0) {
            this.uctScore = Double.MAX_VALUE;
        } else {
            double exploitation = this.scoreVictoire / this.nbVisite;
            double exploration = C * Math.sqrt(Math.log(this.parent.getNbVisite()) / this.nbVisite);
            this.uctScore = exploitation + exploration;
        }
    }

    @Override
    public int compareTo(Node o) {
        if (this.getUCT() > o.getUCT()) {
            return 1;
        } else if (this.getUCT() < o.getUCT()) {
            return -1;
        }
        return 0;
    }

}
