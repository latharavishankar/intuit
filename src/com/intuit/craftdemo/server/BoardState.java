package com.intuit.craftdemo.server;

import java.util.ArrayList;
import java.util.List;

public class BoardState {
	
	private final int n;
	private final List<State> rowStates;
	private final List<State> columnStates;
	private final List<State> diagonalStates;
	
	class State {
		int numChar1;
		int numChar2;
		int maxPossible;
		
		State(int numChar1, int numChar2, int maxPossible) {
			this.numChar1 = numChar1;
			this.numChar2 = numChar2;
			this.maxPossible = maxPossible;
		}
		
		public void incrementNumChar1() {
			++numChar1;
		}

		public void incrementNumChar2() {
			++numChar2;
		}
		
		public boolean reachedMaxPossible() {
			return (numChar1 == maxPossible || numChar2 == maxPossible);
		}
		
		public String toString() {
			return "[" + numChar1 + ", " + numChar2 + "]";
		}
	}
	
	public BoardState(int n) {
		this.n = n;
		rowStates = new ArrayList<>(n);
		columnStates = new ArrayList<>(n);
		diagonalStates = new ArrayList<>(2);
		initStates();
	}
	
	private void initStates() {
		for (int i=0; i<n; i++) {
			State rowState = new State(0, 0, n);
			rowStates.add(rowState);
			State columnState = new State(0, 0, n);
			columnStates.add(columnState);
		}
		for (int i=0; i<2; i++) {
			State diagonalState = new State(0, 0, n);
			diagonalStates.add(diagonalState);
		}
	}

	public boolean incrementStatesAndCheckIfGameHasBeenWon(int player, int row, int column) {
		
		State rowState = rowStates.get(row);
		if (player == 0) {
			rowState.incrementNumChar1();
		}
		else {
			rowState.incrementNumChar2();
		}
		
		if (rowState.reachedMaxPossible()) {
			return true;
		}
		
		
		State columnState = columnStates.get(column);
		if (player == 0) {
			columnState.incrementNumChar1();
		}
		else {
			columnState.incrementNumChar2();
		}
		
		if (columnState.reachedMaxPossible()) {
			return true;
		}		
		
		//check if cell was on the diagonal
		if ((row == column) || (row+column == (n-1))) {
			State diagonalState = null;
			if (row == column) { //diagonal 1
				diagonalState = setDiagonalState(0, player);
				// if n is odd, there is 1 cell which is common to both diagonals, 
				// if n is even no cell is common to both diagonals
				if (n %2 == 1 && row == n/2) {
					if (diagonalState.reachedMaxPossible()) {
						return true;
					}
					diagonalState = setDiagonalState(1, player); // update other diagonal as well
				}
			}
			else { //diagonal 2
				diagonalState = setDiagonalState(1, player);
			}
			if (diagonalState.reachedMaxPossible()) {
				return true;
			}
		}
		
		return false;
		
	}	

	private State setDiagonalState(int whichState, int player) {
		State diagonalState = diagonalStates.get(whichState);
		if (player == 0) {
			diagonalState.incrementNumChar1();
		}
		else {
			diagonalState.incrementNumChar2();
		}		
		return diagonalState;
	}
	
	public boolean getCurrentState() {
		for (int i=0; i<n; i++) {
			State currentRowState = rowStates.get(i);
			if (currentRowState.reachedMaxPossible()) {
				return true;
			}
		}
		for (int i=0; i<n; i++) {
			State currentColumnState = columnStates.get(i);
			if (currentColumnState.reachedMaxPossible()) {
				return true;
			}
		}
		for (int i=0; i<2; i++) {
			State currentDiagonalState = diagonalStates.get(i);
			if (currentDiagonalState.reachedMaxPossible()) {
				return true;
			}
		}
		return false;
	}

}
