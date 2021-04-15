import java.util.*;
import java.util.Collections;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.io.InputStreamReader;
import java.math.*;
import java.util.Random;

class Player {
	private static abstract class Piece {
		private Joueur joueur;

		public Piece(Joueur _joueur) {
			this.joueur = _joueur;
		}

		public void setJoueur(Joueur _joueur) {
			joueur = _joueur;
		}

		public Joueur getJoueur() {
			return joueur;
		}

		abstract public String toString();
	}

	private static class Arbitre {
		Joueur joueur1; // Joueur qui commence la partie
		Joueur joueur2; // Adversaire
		Joueur currentJoueur; // C'est à son tour de jouer
		Plateau plateau; // Le plateau du jeu

		public Arbitre(Plateau _plateau, Joueur _joueur1, Joueur _joueur2) {
			plateau = _plateau;
			joueur1 = _joueur1;
			joueur2 = _joueur2;
			currentJoueur = joueur1; // Le joueur1 commence la partie.
		}

		public Joueur startNewGame(boolean _trace) {
			Coup coup;
			plateau.init(); // Prépare le plateau pour le jeu.
			while (!plateau.partieTerminee()) {
				do {
					if (_trace) {
						System.out.println(plateau);
						System.out.println(currentJoueur + " ( " + currentJoueur.getIdJoueur() + "  )  joue : ");
					}
					coup = currentJoueur.joue(plateau);
					if (!plateau.isValide(coup)) {
						if (_trace)
							System.err.println("Coup non valide ! : " + currentJoueur);
						else { // en mode batch le joueur perd la partie
							System.err.println("Coup non valide ! : " + currentJoueur);
							if (currentJoueur == joueur1)
								return joueur2;
							else
								return joueur1;
						}
					}
				} while (!plateau.isValide(coup));
				plateau.joueCoup(coup);
				if (currentJoueur == joueur1) {
					currentJoueur = joueur2;
				} else {
					currentJoueur = joueur1;
				}
			}
			Joueur vainqueur = plateau.vainqueur();
			if (vainqueur != null)
				System.out.println(vainqueur + " gagne la partie ");
			else
				System.out.println("Partie nulle ");
			return vainqueur;
		}

		public void startTournament(int _nbPartie, boolean _trace) {
			double[] nbVictoire = new double[2];
			Joueur vainqueur;
			currentJoueur = joueur1;
			int numJoueur = 0;
			nbVictoire[0] = nbVictoire[1] = 0;
			for (int i = 0; i < _nbPartie; i++) {
				vainqueur = startNewGame(_trace);
				if (vainqueur == joueur1)
					nbVictoire[0]++;
				if (vainqueur == joueur2)
					nbVictoire[1]++;
				if (vainqueur == null) {
					nbVictoire[0] += 0.5;
					nbVictoire[1] += 0.5;
				}
				if (numJoueur == 0) {
					currentJoueur = joueur2;
					numJoueur = 1;
				} else {
					currentJoueur = joueur1;
					numJoueur = 0;
				}
				System.out.println(joueur1 + " score : " + nbVictoire[0]);
				System.out.println(joueur2 + " score : " + nbVictoire[1]);
			}
			System.out.println(joueur1 + " score : " + nbVictoire[0]);
			System.out.println(joueur2 + " score : " + nbVictoire[1]);
			if (nbVictoire[0] > nbVictoire[1])
				System.out.println(joueur1 + " GAGNE ");
			else if (nbVictoire[1] > nbVictoire[0])
				System.out.println(joueur2 + " GAGNE ");
			else
				System.out.println("Match nul");
		}

		public Joueur getCurrentJoueur() {
			return currentJoueur;
		}
	}

	private static class GrilleTicTacToe9x9 extends Plateau {
		Jeton[][] grille9x9 = new Jeton[9][9];
		Jeton[][] grille3x3 = new Jeton[3][3]; // indique si un joueur à gagné un case de la grande grille
		Jeton[][][] grille9x9Sav = new Jeton[100][9][9]; // Pour sauvegarder la position. 100 positions Max
		Jeton[][][] grille3x3Sav = new Jeton[100][3][3]; // Pour sauvegarder la position
		CoupTicTacToe[] dernierCoup;
		int nbCoupJoue;
		int nbCoupJoueSav;
		CoupTicTacToe dernierCoupSav;
		Joueur vainqueur;
		boolean grilleGagnee;
		boolean[] grilleGagneeSav; // vrai si dernier coup gagne une grille

		@Override
		public void init() {
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					grille9x9[c][l] = null;
				}
			}
			for (int c = 0; c < 3; c++) {
				for (int l = 0; l < 3; l++) {
					grille3x3[c][l] = null;
				}
			}
			dernierCoup = new CoupTicTacToe[100];
			nbCoupJoue = 0;
			nbCoupJoueSav = 0;
			grilleGagneeSav = new boolean[100];
			vainqueur = null;
			grilleGagnee = false;
		}

		@Override
		public Jeton[][] getGrille3x3() {
			return this.grille3x3;
		}

		@Override
		public Jeton[][] getGrille9x9() {
			return this.grille9x9;
		}

		@Override
		public Piece getPiece(Case _case) {
			return grille9x9[_case.getColonne()][_case.getLigne()];
		}

		@Override
		public void joueCoup(Coup _coup) {
			CoupTicTacToe coup = (CoupTicTacToe) _coup;
			grille9x9[coup.getColonne()][coup.getLigne()] = coup.getJeton();
			dernierCoup[nbCoupJoue] = coup;
			grilleGagneeSav[nbCoupJoue] = grilleGagnee;
			nbCoupJoue++;
			int x0 = (coup.getColonne() / 3) * 3;
			int y0 = (coup.getLigne() / 3) * 3;
			if (caseGagnante(grille9x9, x0, y0, coup.getColonne(), coup.getLigne())) {
				grille3x3[coup.getColonne() / 3][coup.getLigne() / 3] = coup.getJeton();
				grilleGagnee = true;
			}
		}

		@Override
		public void annuleDernierCoup() {
			nbCoupJoue--;
			grille9x9[dernierCoup[nbCoupJoue].getColonne()][dernierCoup[nbCoupJoue].getLigne()] = null;
			grilleGagnee = grilleGagneeSav[nbCoupJoue];
			grille3x3[dernierCoup[nbCoupJoue].getColonne() / 3][dernierCoup[nbCoupJoue].getLigne() / 3] = null;
			vainqueur = null;
		}

		@Override
		public int getNbColonnes() {
			return 9;
		}

		@Override
		public int getNbLignes() {
			return 9;
		}

		@Override
		public boolean partieTerminee() {
			if (vainqueur != null) {
				return true;
			}
			if (partieGagnee()) {
				return true;
			}
			return isGrillePleine();
		}

		public boolean caseGagnante(Jeton[][] _grille, int _x0, int _y0, int _coupX, int _coupY) {
			int[][] dir = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { 1, -1 } };
			int[][] dirOps = { { -1, 0 }, { -1, -1 }, { 0, -1 }, { -1, 1 } };
			int xMax = _x0 + 3, yMax = _y0 + 3;
			int x, y;
			int nbJetonAligne;
			Joueur dernierJoueur = dernierCoup[nbCoupJoue - 1].getJeton().getJoueur();
			for (int d = 0; d < 4; d++) {
				nbJetonAligne = 0;
				x = _coupX;
				y = _coupY;
				while (x < xMax && x >= _x0 && y < yMax && y >= _y0 && _grille[x][y] != null
						&& _grille[x][y].getJoueur() == dernierJoueur) {
					nbJetonAligne++;
					if (nbJetonAligne >= 3) {
						return true;
					}
					x += dir[d][0];
					y += dir[d][1];
				}
				x = _coupX;
				y = _coupY;
				nbJetonAligne--;
				while (x < xMax && x >= _x0 && y < yMax && y >= _y0 && _grille[x][y] != null
						&& _grille[x][y].getJoueur() == dernierJoueur) {
					nbJetonAligne++;
					if (nbJetonAligne >= 3) {
						return true;
					}
					x += dirOps[d][0];
					y += dirOps[d][1];
				}
			}
			return false;
		}

		@Override
		public boolean partieGagnee() {
			if (nbCoupJoue == 0) {
				return false;
			}
			if (grilleGagnee) { // si le dernier coup a gagne une grille on regarde si on gagne sur la grande
				if (caseGagnante(grille3x3, 0, 0, dernierCoup[nbCoupJoue - 1].getColonne() / 3,
						dernierCoup[nbCoupJoue - 1].getLigne() / 3)) {
					vainqueur = dernierCoup[nbCoupJoue - 1].getJeton().getJoueur();
					return true;
				}
			}
			if (isGrillePleine()) {
				int[] nbCase = new int[2];
				Joueur[] joueur = new Joueur[2];
				for (int c = 0; c < 3; c++) {
					for (int l = 0; l < 3; l++) {
						if (grille3x3[c][l] != null) {
							nbCase[grille3x3[c][l].getJoueur().getIdJoueur()]++;
							joueur[grille3x3[c][l].getJoueur().getIdJoueur()] = grille3x3[c][l].getJoueur();
						}
					}
				}
				if (nbCase[0] > nbCase[1]) {
					vainqueur = joueur[0];
					return true;
				}
				if (nbCase[1] > nbCase[0]) {
					vainqueur = joueur[1];
					return true;
				}
				vainqueur = null;
				return true;
			}
			return false;
		}

		@Override
		public boolean partieNulle() {
			if (vainqueur != null) {
				return false;
			}
			if (partieGagnee()) {
				return false;
			}
			return isGrillePleine();
		}

		@Override
		public ArrayList<Coup> getListeCoups(Joueur _joueur) {
			ArrayList<Coup> listeCoups = new ArrayList<Coup>();
			if (nbCoupJoue != 0) {
				int x0 = (dernierCoup[nbCoupJoue - 1].getColonne() % 3) * 3;
				int y0 = (dernierCoup[nbCoupJoue - 1].getLigne() % 3) * 3;
				for (int c = x0; c < x0 + 3; c++) {
					for (int l = y0; l < y0 + 3; l++) {
						if (grille3x3[(c / 3)][(l / 3)] != null)
							continue; // on ne peut pas jouer dans une grille gagnée
						if (grille9x9[c][l] == null) {
							listeCoups.add(new CoupTicTacToe(c, l, new Jeton(_joueur)));
						}
					}
				}
			}
			if (listeCoups.isEmpty()) {
				for (int c = 0; c < this.getNbColonnes(); c++) {
					for (int l = 0; l < this.getNbLignes(); l++) {
						if (grille3x3[(c / 3)][(l / 3)] != null)
							continue; // on ne peut pas jouer dans une grille gagnée
						if (grille9x9[c][l] == null) {
							listeCoups.add(new CoupTicTacToe(c, l, new Jeton(_joueur)));
						}
					}
				}
			}
			return listeCoups;
		}

		@Override
		public boolean isValide(Coup _coup) {
			CoupTicTacToe coup = (CoupTicTacToe) _coup;
			if (grille9x9[coup.getColonne()][coup.getLigne()] != null)
				return false;
			if (nbCoupJoue == 0)
				return true;
			int x0 = (dernierCoup[nbCoupJoue - 1].getColonne() % 3) * 3;
			int y0 = (dernierCoup[nbCoupJoue - 1].getLigne() % 3) * 3;
			boolean grillePleine = true;
			for (int c = x0; c < x0 + 3; c++) {
				for (int l = y0; l < y0 + 3; l++) {
					if (grille3x3[c / 3][l / 3] != null)
						continue; // les grilles gagneés sont pleines
					if (grille9x9[c][l] == null) {
						grillePleine = false;
						break;
					}
				}
			}
			if (grillePleine) {
				return true;
			}
			return (coup.getColonne() >= x0 && coup.getColonne() < x0 + 3 && coup.getLigne() >= y0
					&& coup.getLigne() < y0 + 3);
		}

		@Override
		public Coup stringToCoup(String _coup, Joueur _joueur) {
			int colonne = Integer.valueOf(_coup.charAt(0) + "");
			int ligne = Integer.valueOf(_coup.charAt(1) + "");
			return new CoupTicTacToe(colonne, ligne, new Jeton(_joueur));
		}

		@Override
		public void sauvegardePosition(int _index) {
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					grille9x9Sav[_index][c][l] = grille9x9[c][l];
				}
			}
			for (int c = 0; c < 3; c++) {
				for (int l = 0; l < 3; l++) {
					grille3x3Sav[_index][c][l] = grille3x3[c][l];
				}
			}
			grilleGagneeSav[nbCoupJoue] = grilleGagnee;
			nbCoupJoueSav = nbCoupJoue;
		}

		@Override
		public void restaurePosition(int _index) {
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					grille9x9[c][l] = grille9x9Sav[_index][c][l];
				}
			}
			for (int c = 0; c < 3; c++) {
				for (int l = 0; l < 3; l++) {
					grille3x3[c][l] = grille3x3Sav[_index][c][l];
				}
			}
			vainqueur = null;
			grilleGagnee = grilleGagneeSav[nbCoupJoueSav];
			nbCoupJoue = nbCoupJoueSav;
		}

		private boolean isGrillePleine() {
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					if (grille3x3[c / 3][l / 3] != null)
						continue; // les grilles gagnées sont pleines
					if (grille9x9[c][l] == null) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public Joueur vainqueur() {
			return vainqueur;
		}

		void trace() {
			for (int l = 2; l >= 0; l--) {
				for (int c = 0; c < 3; c++) {
					System.out.print(grille3x3[c][l] + " ");
				}
				System.out.println("");
			}
		}

		@Override
		public Coup getDernierCoup() {
			if (nbCoupJoue < 1)
				return null;
			return dernierCoup[nbCoupJoue - 1];
		}
	}

	private static class AlgoRechercheMonteCarlo extends AlgoRecherche {
		Random rnd;
		private int max_iteration;
		private Joueur ennemi;
		private Joueur bot;

		public AlgoRechercheMonteCarlo(Joueur ennemi, Joueur bot) {
			rnd = new Random();
			this.ennemi = ennemi;
			this.bot = bot;
			this.max_iteration = 5000;
		}

		public AlgoRechercheMonteCarlo(int max_iteration, Joueur ennemi) {
			rnd = new Random();
			this.ennemi = ennemi;
			this.max_iteration = max_iteration;
		}

		private Joueur getJoueurEnnemi(Joueur joueurEnCours) {
			if (joueurEnCours == this.ennemi) {
				return this.bot;
			}
			return this.ennemi;
		}

		private Node selection(Node root, Plateau _plateau) {
			ArrayList<Node> childArray = new ArrayList<Node>();
			while (!root.getChildArray().isEmpty() && _plateau.partieTerminee() == false) {
				childArray = root.getChildArray();
				childArray.forEach(node -> {
					node.updateUctScore(2);
				});
				root = Collections.max(childArray);
				_plateau.joueCoup(root.getCoup());
			}
			return root;
		}

		private Node expension(Node node, Plateau _plateau) {
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

		private Joueur simulation(Node node, Plateau _plateau) {
			Joueur joueurEnCours = node.getJoueur();
			_plateau.joueCoup(node.getCoup());
			if (_plateau.vainqueur() == this.ennemi) {
				node.setScoreVictoire(Integer.MAX_VALUE);
				return _plateau.vainqueur();
			}
			while (!_plateau.partieTerminee()) {
				joueurEnCours = getJoueurEnnemi(joueurEnCours);
				ArrayList<Coup> coupPossible = _plateau.getListeCoups(joueurEnCours);
				Coup coupjoue = coupPossible.get(rnd.nextInt(coupPossible.size()));
				_plateau.joueCoup(coupjoue);
			}
			return _plateau.vainqueur();
		}

		private void backPropagation(Node node, Joueur joueurGagnant, Plateau _plateau) {
			Node nodeAux = node;
			while (nodeAux != null) {
				nodeAux.incrementNbVisite();
				if (joueurGagnant == this.bot) {
					if (nodeAux.getJoueur() == this.bot) {
						nodeAux.incrementScore();
					}
				}
				nodeAux = nodeAux.getParent();
			}
		}

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

		@Override
		public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
			Arbre arbre = new Arbre(this.ennemi);
			Node root = arbre.getRoot();
			Node newNode;
			Joueur gagnant;
			_plateau.sauvegardePosition(0);
			for (int iter = 0; iter < this.max_iteration; iter++) {
				Node nodeSelectionne = this.selection(root, _plateau);
				newNode = this.expension(nodeSelectionne, _plateau);
				gagnant = this.simulation(newNode, _plateau);
				this.backPropagation(nodeSelectionne, gagnant, _plateau);
				_plateau.restaurePosition(0);
			}
			return findBestChild(root).getCoup();
		}
	}

	private static class Jeton extends Piece {
		public Jeton(Joueur _joueur) {
			super(_joueur);
		}

		public String toString() {
			if ((getJoueur().getIdJoueur()) == 1) {
				return "X";
			} else {
				return "O";
			}
		}
	}

	private static abstract class Coup {
		private int note;

		public Coup() {
			note = Integer.MIN_VALUE; // un coup est tres mauvais tant qu'il n'est pas analysé.
		}

		public void setNote(int _note) {
			note = _note;
		}

		public int getNote() {
			return note;
		}

		abstract public String toString();

		abstract public boolean equals(Object obj);

		abstract public int hashCode();
	}

	private static abstract class Joueur {
		private String nom;
		private static int nbJoueur = 0;
		private int idJoueur;

		public Joueur(String _nom) {
			idJoueur = nbJoueur;
			nbJoueur++;
			this.nom = _nom;
		}

		public void setNom(String _nom) {
			this.nom = _nom;
		}

		public String getNom() {
			return nom;
		}

		public int getIdJoueur() {
			return idJoueur;
		}

		abstract public Coup joue(Plateau p_plateau);

		public String toString() {
			return nom;
		}
	}

	private static class Node implements Comparable<Node> {
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

		public Node(Joueur joueur) {
			this.uctScore = 0;
			this.childArray = new ArrayList<>();
			this.joueur = joueur;
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
			this.scoreVictoire += 1;
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

		public void setScoreVictoire(int score) {
			this.scoreVictoire = score;
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
				double exploitation = this.scoreVictoire / (double) this.nbVisite;
				double exploration = C
						* Math.sqrt(Math.log((double) this.parent.getNbVisite()) / (double) this.nbVisite);
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

	private static class AlgoMinimax extends AlgoRecherche {
		private Joueur joueurMax;
		private Joueur joueurMin;

		public AlgoMinimax(Joueur _joueur1, Joueur _joueur2) {
			this.joueurMax = _joueur1;
			this.joueurMin = _joueur2;
		}

		public int eval(Plateau _plateau) {
			if (_plateau.partieNulle()) {
				return 0;
			}
			if (_plateau.vainqueur() == joueurMax) {
				return 10;
			} else {
				return -10;
			}
		}

		public int minimax(Plateau _plateau, boolean isMaximizing, int depth) {
			if (depth == 0 || _plateau.partieTerminee()) {
				return eval(_plateau);
			}
			if (isMaximizing) {
				int maxEval = -100;
				ArrayList<Coup> coupsMax = _plateau.getListeCoups(joueurMax);
				for (int i = 0; i < coupsMax.size(); i++) {
					int eval;
					_plateau.joueCoup(coupsMax.get(i));
					eval = minimax(_plateau, false, depth - 1);
					_plateau.annuleDernierCoup();
					maxEval = max(eval, maxEval);
				}
				return maxEval;
			} else {
				int minEval = 100;
				ArrayList<Coup> coupsMin = _plateau.getListeCoups(joueurMin);
				for (int i = 0; i < coupsMin.size(); i++) {
					int eval;
					_plateau.joueCoup(coupsMin.get(i));
					eval = minimax(_plateau, true, depth - 1);
					_plateau.annuleDernierCoup();
					minEval = min(eval, minEval);
				}
				return minEval;
			}
		}

		@Override
		public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
			ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
			int bestScore = -1000;
			int move = 0;
			int score;
			for (int i = 0; i < coups.size(); i++) {
				_plateau.joueCoup(coups.get(i));
				score = minimax(_plateau, false, 10);
				_plateau.annuleDernierCoup();
				if (score > bestScore) {
					bestScore = score;
					move = i;
				}
			}
			return coups.get(move);
		}

		public int max(int a, int b) {
			if (a >= b) {
				return a;
			} else {
				return b;
			}
		}

		public int min(int a, int b) {
			if (a <= b) {
				return a;
			} else {
				return b;
			}
		}
	}

	private static class Arbre {
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

	private static class AlgoRechercheAleatoire extends AlgoRecherche {
		Random rnd;

		public AlgoRechercheAleatoire() {
			rnd = new Random();
		}

		@Override
		public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
			ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
			return coups.get(rnd.nextInt(coups.size()));
		}
	}

	private static abstract class AlgoRecherche {
		abstract public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder);
	}

	private static class Case {
		int colonne, ligne;

		public Case(int _colonne, int _ligne) {
			this.colonne = _colonne;
			this.ligne = _ligne;
		}

		public int getColonne() {
			return colonne;
		}

		public int getLigne() {
			return ligne;
		}

		public void setLigne(int _ligne) {
			this.ligne = _ligne;
		}

		public void setColonne(int _colonne) {
			this.colonne = _colonne;
		}
	}

	private static class JoueurOrdi extends Joueur {
		boolean ponder; // Si vrai l'ordinateur réfléchi pendant la reflexion de son adversaire
		AlgoRecherche algoRecherche;

		public JoueurOrdi(String _nom) {
			super(_nom);
			ponder = false;
		}

		public JoueurOrdi(String _nom, AlgoRecherche _algo, boolean _ponder) {
			super(_nom);
			algoRecherche = _algo;
			ponder = _ponder;
		}

		public JoueurOrdi(String _nom, AlgoRecherche _algo) {
			this(_nom, _algo, false);
		}

		public AlgoRecherche getAlgoRecherche() {
			return algoRecherche;
		}

		public void setAlgoRecherche(AlgoRecherche _algoRecherche) {
			algoRecherche = _algoRecherche;
		}

		public Coup joue(Plateau _plateau) {
			Coup coupOrdi;
			coupOrdi = algoRecherche.meilleurCoup(_plateau, this, ponder);
			return coupOrdi;
		}
	}

	private static class GrilleTicTacToe3x3 extends Plateau {
		Jeton[][] grille = new Jeton[3][3];
		Jeton[][][] grilleSav = new Jeton[100][3][3]; // Pour sauvegardr la position. 100 au maximum
		CoupTicTacToe[] dernierCoup = new CoupTicTacToe[10];
		int nbCoupJoue, nbCouJoueSav;
		Joueur vainqueur;

		@Override
		public void init() {
			for (int c = 0; c < this.getNbColonnes(); c++)
				for (int l = 0; l < this.getNbLignes(); l++)
					grille[c][l] = null;
			dernierCoup = new CoupTicTacToe[10];
			vainqueur = null;
			nbCoupJoue = 0;
		}

		@Override
		public Jeton[][] getGrille3x3() {
			return this.grille;
		}

		@Override
		public Jeton[][] getGrille9x9() {
			Jeton[][] grille9x9 = new Jeton[1][1];
			return grille9x9;
		}

		@Override
		public Piece getPiece(Case _case) {
			return grille[_case.getColonne()][_case.getLigne()];
		}

		@Override
		public void joueCoup(Coup _coup) {
			CoupTicTacToe coup = (CoupTicTacToe) _coup;
			grille[coup.getColonne()][coup.getLigne()] = coup.getJeton();
			dernierCoup[nbCoupJoue] = coup;
			nbCoupJoue++;
		}

		@Override
		public void annuleDernierCoup() {
			nbCoupJoue--;
			grille[dernierCoup[nbCoupJoue].getColonne()][dernierCoup[nbCoupJoue].getLigne()] = null;
			vainqueur = null;
		}

		@Override
		public int getNbColonnes() {
			return 3;
		}

		@Override
		public int getNbLignes() {
			return 3;
		}

		@Override
		public boolean partieTerminee() {
			if (partieGagnee()) {
				return true;
			}
			return isGrillePleine();
		}

		@Override
		public boolean partieGagnee() {
			int[][] dir = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { 1, -1 } };
			int[][] dirOps = { { -1, 0 }, { -1, -1 }, { 0, -1 }, { -1, 1 } };
			int x, y;
			int nbJetonAligne;
			if (nbCoupJoue == 0) {
				return false;
			}
			Joueur dernierJoueur = dernierCoup[nbCoupJoue - 1].getJeton().getJoueur();
			for (int d = 0; d < 4; d++) {
				nbJetonAligne = 0;
				x = dernierCoup[nbCoupJoue - 1].getColonne();
				y = dernierCoup[nbCoupJoue - 1].getLigne();
				while (x < this.getNbColonnes() && x >= 0 && y < this.getNbLignes() && y >= 0 && grille[x][y] != null
						&& grille[x][y].getJoueur() == dernierJoueur) {
					nbJetonAligne++;
					if (nbJetonAligne >= 3) {
						vainqueur = dernierJoueur;
						return true;
					}
					x += dir[d][0];
					y += dir[d][1];
				}
				x = dernierCoup[nbCoupJoue - 1].getColonne();
				y = dernierCoup[nbCoupJoue - 1].getLigne();
				nbJetonAligne--;
				while (x < this.getNbColonnes() && x >= 0 && y < this.getNbLignes() && y >= 0 && grille[x][y] != null
						&& grille[x][y].getJoueur() == dernierJoueur) {
					nbJetonAligne++;
					if (nbJetonAligne >= 3) {
						vainqueur = dernierJoueur;
						return true;
					}
					x += dirOps[d][0];
					y += dirOps[d][1];
				}
			}
			return false;
		}

		@Override
		public boolean partieNulle() {
			if (partieGagnee()) {
				return false;
			}
			return isGrillePleine();
		}

		@Override
		public ArrayList<Coup> getListeCoups(Joueur _joueur) {
			ArrayList<Coup> listeCoups = new ArrayList<Coup>();
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					if (grille[c][l] == null)
						listeCoups.add(new CoupTicTacToe(c, l, new Jeton(_joueur)));
				}
			}
			return listeCoups;
		}

		@Override
		public boolean isValide(Coup _coup) {
			CoupTicTacToe coup = (CoupTicTacToe) _coup;
			return grille[coup.getColonne()][coup.getLigne()] == null;
		}

		@Override
		public Coup stringToCoup(String _coup, Joueur _joueur) {
			int colonne = Integer.valueOf(_coup.charAt(0) + "");
			int ligne = Integer.valueOf(_coup.charAt(1) + "");
			return new CoupTicTacToe(colonne, ligne, new Jeton(_joueur));
		}

		@Override
		public void sauvegardePosition(int _index) {
			for (int c = 0; c < this.getNbColonnes(); c++)
				for (int l = 0; l < this.getNbLignes(); l++)
					grilleSav[_index][c][l] = grille[c][l];
			nbCouJoueSav = nbCoupJoue;
		}

		@Override
		public void restaurePosition(int _index) {
			for (int c = 0; c < this.getNbColonnes(); c++)
				for (int l = 0; l < this.getNbLignes(); l++)
					grille[c][l] = grilleSav[_index][c][l];
			vainqueur = null;
			nbCoupJoue = nbCouJoueSav;
		}

		private boolean isGrillePleine() {
			for (int c = 0; c < this.getNbColonnes(); c++) {
				for (int l = 0; l < this.getNbLignes(); l++) {
					if (grille[c][l] == null) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public Joueur vainqueur() {
			return vainqueur;
		}

		@Override
		public Coup getDernierCoup() {
			return dernierCoup[nbCoupJoue - 1];
		}
	}

	private static abstract class Plateau {
		abstract public Jeton[][] getGrille3x3();

		abstract public Jeton[][] getGrille9x9();

		abstract public void init(); // Initialise le plateau pour le démarrage d'une nouvelle partie.

		abstract public Piece getPiece(Case _case); // Retourne la pièce presente sur la case _case

		abstract public void joueCoup(Coup _coup); // Joue le coup _coup sur le plateau

		abstract public void annuleDernierCoup(); // Annule le dernier coup joué

		abstract public int getNbColonnes(); // Retourne le nombre de colonnes du plateau

		abstract public int getNbLignes(); // Retourne le nombre de lignes du plateau

		abstract public boolean partieTerminee(); // Vrai si la partie est terminee ( soit un gagnant soit un match
													// nulle )

		abstract public boolean partieGagnee(); // Vrai si le dernier joueur a gagné la partie

		abstract public boolean partieNulle(); // Vrai si la partie est nulle

		abstract public Joueur vainqueur(); // Retourne le joueur qui a gagné la partie

		abstract public ArrayList<Coup> getListeCoups(Joueur _joueur); // Retourne la liste des coups possibles.

		abstract public boolean isValide(Coup _coup); // Retourne Vrai si le coup est valide.

		abstract public Coup stringToCoup(String _coup, Joueur _joueur); // Convertion d'une chaine de caractères en un
																			// Coup

		abstract public void sauvegardePosition(int _index); // Sauvegarde la position courante dans l'indice _index

		abstract public void restaurePosition(int _index); // restaure la position sauvegarde en indice _index

		abstract public Coup getDernierCoup(); // Retourne le dernierCoup joue

		public String toString() { // Donne une représentation du plateau sous forme d'une chaine de caractères
			Piece piece;
			String chainePlateau = new String();
			for (int ligne = getNbLignes() - 1; ligne >= 0; ligne--) {
				chainePlateau += ligne;
				for (int colonne = 0; colonne < getNbColonnes(); colonne++) {
					piece = getPiece(new Case(colonne, ligne));
					if (piece == null) {
						chainePlateau += "|   ";
					} else {
						chainePlateau += "|" + piece + "  ";
					}
				}
				chainePlateau += "|\n  ";
				for (int colonne = 0; colonne < getNbColonnes(); colonne++) {
					chainePlateau += "____";
				}
				chainePlateau += "\n";
			}
			for (int colonne = 0; colonne < getNbColonnes(); colonne++)
				chainePlateau += "  " + colonne + " ";
			return chainePlateau;
		}
	}

	private static class AlgoMiniMax9x9 extends AlgoRecherche {
		private Joueur joueurMax;
		private Joueur joueurMin;

		public AlgoMiniMax9x9(Joueur _joueur1, Joueur _joueur2) {
			this.joueurMax = _joueur1;
			this.joueurMin = _joueur2;
		}

		public int eval(Plateau _plateau) {
			if (_plateau.vainqueur() == joueurMax) {
				return 100;
			}
			if (_plateau.partieNulle()) {
				return 0;
			}
			int nbCase3x3Max = 0;
			int nbCase3x3Min = 0;
			for (int c = 0; c < 3; c++) {
				for (int l = 0; l < 3; l++) {
					Jeton[][] grille3x3 = _plateau.getGrille3x3(); // Retourne l'état des grilles3x3
					if (grille3x3[c][l] != null) {
						if (grille3x3[c][l].getJoueur().getIdJoueur() == joueurMax.getIdJoueur()) {
							nbCase3x3Max++;
						} else {
							nbCase3x3Min++;
						}
					}
				}
			}
			if (nbCase3x3Max != 0) {
				if (nbCase3x3Max == 1) {
					return 53;
				} else if (nbCase3x3Max == 2) {
					return 54;
				} else if (nbCase3x3Max == 4) {
					return 55;
				} else if (nbCase3x3Max == 5) {
					return 56;
				} else if (nbCase3x3Max == 6) {
					return 57;
				} else if (nbCase3x3Max == 7) {
					return 58;
				} else if (nbCase3x3Max == 8) {
					return 59;
				}
			}
			if (nbCase3x3Min != 0) {
				if (nbCase3x3Min == 1) {
					return -53;
				} else if (nbCase3x3Min == 2) {
					return -54;
				} else if (nbCase3x3Min == 4) {
					return -55;
				} else if (nbCase3x3Min == 5) {
					return -56;
				} else if (nbCase3x3Min == 6) {
					return -57;
				} else if (nbCase3x3Min == 7) {
					return -58;
				} else if (nbCase3x3Min == 8) {
					return -59;
				}
			}
			int nbCaseMilieuMax = 0;
			int nbCaseMilieuMin = 0;
			for (int c = 1; c < 9; c = c + 3) {
				for (int l = 1; l < 9; l = l + 3) {
					Jeton[][] grille9x9 = _plateau.getGrille9x9(); // Retourne la grille à l'état actuel
					if (grille9x9[c][l] != null) {
						if (grille9x9[c][l].getJoueur().getIdJoueur() == joueurMax.getIdJoueur()) {
							nbCaseMilieuMax++;
						} else {
							nbCaseMilieuMin++;
						}
					}
				}
			}
			if (nbCaseMilieuMax != 0) {
				if (nbCaseMilieuMax == 1) {
					return 3;
				} else if (nbCaseMilieuMax == 2) {
					return 4;
				} else if (nbCaseMilieuMax == 3) {
					return 5;
				} else if (nbCaseMilieuMax == 4) {
					return 6;
				} else if (nbCaseMilieuMax == 5) {
					return 7;
				} else if (nbCaseMilieuMax == 6) {
					return 8;
				} else if (nbCaseMilieuMax == 7) {
					return 9;
				} else if (nbCaseMilieuMax == 8) {
					return 10;
				} else if (nbCaseMilieuMax == 9) {
					return 11;
				}
			}
			if (nbCaseMilieuMin != 0) {
				if (nbCaseMilieuMin == 1) {
					return -3;
				} else if (nbCaseMilieuMin == 2) {
					return -4;
				} else if (nbCaseMilieuMin == 3) {
					return -5;
				} else if (nbCaseMilieuMin == 4) {
					return -6;
				} else if (nbCaseMilieuMin == 5) {
					return -7;
				} else if (nbCaseMilieuMin == 6) {
					return -8;
				} else if (nbCaseMilieuMin == 7) {
					return -9;
				} else if (nbCaseMilieuMin == 8) {
					return -10;
				} else if (nbCaseMilieuMin == 9) {
					return -11;
				}
			}
			return -100;
		}

		public int minimax(Plateau _plateau, boolean isMaximizing, int depth, int alpha, int beta) {
			if (depth == 0 || _plateau.partieTerminee()) {
				return eval(_plateau);
			}
			if (isMaximizing) {
				int maxEval = -100;
				ArrayList<Coup> coupsMax = _plateau.getListeCoups(joueurMax);
				for (int i = 0; i < coupsMax.size(); i++) {
					int eval;
					_plateau.joueCoup(coupsMax.get(i));
					eval = minimax(_plateau, false, depth - 1, alpha, beta);
					_plateau.annuleDernierCoup();
					maxEval = max(eval, maxEval);
					alpha = max(alpha, eval);
					if (beta <= alpha) {
						break;
					}
				}
				return maxEval;
			} else {
				int minEval = 100;
				ArrayList<Coup> coupsMin = _plateau.getListeCoups(joueurMin);
				for (int i = 0; i < coupsMin.size(); i++) {
					int eval;
					_plateau.joueCoup(coupsMin.get(i));
					eval = minimax(_plateau, true, depth - 1, alpha, beta);
					_plateau.annuleDernierCoup();
					minEval = min(eval, minEval);
					beta = min(beta, eval);
					if (beta <= alpha) {
						break;
					}
				}
				return minEval;
			}
		}

		@Override
		public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
			ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
			int bestScore = -1000;
			int move = 0;
			int score;
			for (int i = 0; i < coups.size(); i++) {
				_plateau.joueCoup(coups.get(i));
				score = minimax(_plateau, false, 5, -20, 20);
				_plateau.annuleDernierCoup();
				if (score > bestScore) {
					bestScore = score;
					move = i;
				}
			}
			return coups.get(move);
		}

		public int max(int a, int b) {
			if (a >= b) {
				return a;
			} else {
				return b;
			}
		}

		public int min(int a, int b) {
			if (a <= b) {
				return a;
			} else {
				return b;
			}
		}
	}

	private static class JoueurHumain extends Joueur {
		public JoueurHumain(String _joueur) {
			super(_joueur);
		}

		public Coup joue(Plateau _plateau) {
			InputStreamReader clavier = new InputStreamReader(System.in);
			char buffer[] = new char[10];
			String monCoup = null;
			System.out.println("Votre coup : ");
			try {
				int nbCar = clavier.read(buffer, 0, 5);
				monCoup = new String(buffer, 0, nbCar - 1);
				return _plateau.stringToCoup(monCoup, this);
			} catch (IOException e) {
				return null;
			}
		}
	}

	private static class CoupTicTacToe extends Coup {
		private int colonne, ligne;
		private Jeton jeton;

		public CoupTicTacToe(int _colonne, int _ligne, Jeton _jeton) {
			super();
			colonne = _colonne;
			ligne = _ligne;
			jeton = _jeton;
		}

		public int getColonne() {
			return colonne;
		}

		public int getLigne() {
			return ligne;
		}

		public Jeton getJeton() {
			return jeton;
		}

		public String toString() {
			return colonne + " " + ligne;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			CoupTicTacToe coup = (CoupTicTacToe) obj;
			return colonne == coup.getColonne() && ligne == coup.ligne
					&& jeton.toString().equals(coup.getJeton().toString());
		}

		@Override
		public int hashCode() {
			return jeton.toString().hashCode() + colonne * 10 + ligne;
		}
	}

	public static void main(String args[]) {
		JoueurHumain humain = new JoueurHumain("codinggame");
		JoueurOrdi monteCarloJoueur = new JoueurOrdi("MonteCarlo");
		AlgoRechercheMonteCarlo monte = new AlgoRechercheMonteCarlo(humain, monteCarloJoueur);
		Jeton jetonHumain = new Jeton(humain);
		GrilleTicTacToe3x3 grille3 = new GrilleTicTacToe3x3();
		monteCarloJoueur.setAlgoRecherche(monte);
		Arbitre a = new Arbitre(grille3, monteCarloJoueur, humain);
		Scanner in = new Scanner(System.in);
		boolean firstTour = true;
		while (true) {
			int opponentRow = in.nextInt();
			int opponentCol = in.nextInt();
			if (!firstTour) {
				CoupTicTacToe coupEnnemi = new CoupTicTacToe(opponentCol, opponentRow, jetonHumain);
				System.err.println("mon coup " + opponentCol);
				grille3.joueCoup(coupEnnemi);
				System.err.println(grille3);
			}

			int validActionCount = in.nextInt();
			for (int i = 0; i < validActionCount; i++) {
				int row = in.nextInt();
				int col = in.nextInt();
			}
			System.err.println("mon cotezs");

			Coup coupMonteCarlo = monte.meilleurCoup(grille3, monteCarloJoueur, false);
			System.err.println(coupMonteCarlo);
			System.out.println("0 0");

		}
	}
}
