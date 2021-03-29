package tictactoecodingame;

public class State {
    private Plateau plateau;
    private Joueur joueur;
    private int nbDeVisite;
    private double scoreVictoire;

    public State() {
        this.plateau.init();
    }

    public Plateau getPlateau() {
        return this.plateau;
    }

    public Joueur getJoueur() {
        return this.joueur;
    }

    public double getScoreVictoire() {
        return this.scoreVictoire;
    }

    public int getNbDeVisite() {
        return this.nbDeVisite;
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public void incrementVisit() {
        this.nbDeVisite++;
    }
}
