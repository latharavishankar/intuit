package com.intuit.craftdemo.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class TicTacToe {
	
	private final int n;
	private final int maxNumPlays;
	private final char[][] board;
	private final int numPlayers;
	private final Map<Integer, Location> cellMap = new HashMap<>();
	private String gameId;
	private List<Player> players = new ArrayList<>(2);
	private BoardState boardState;
	private final String COMPUTER_NAME = "Computer";
	int currentPlayer = 0;
	int currentUserLocation = -1;
	int numOfPlaysCompleted = 0;
	boolean gameWon = false;
	int winner = -1;
	long startTime;
	

	public class Player {
		final String playerName;
		final char playerChar;
		
		Player(String playerName, char playerChar) {
			this.playerName = playerName;
			this.playerChar = playerChar;
		}
	}

	private class Location {
		int row = -1;
		int column = -1;
		
		Location(int row, int column) {
			this.row = row;
			this.column = column;
		}
		
		public String toString() {
			return "[" + row + ", " + column + "]";
		}
	}
	

	public TicTacToe(int n, int numPlayers) {
		this.n = n;;
		maxNumPlays = n*n;
		board = new char[n][n];
		this.numPlayers = numPlayers;
	}
	
	public String startNewGame(String player1Name, char ch1, String player2Name, char ch2) {
		gameId = generateUUID();
		initBoard();
		fillCellMap();
		boardState = new BoardState(n);
		printBoard();
		if (numPlayers == 2) {
			players.add(new Player(player1Name, ch1));
			players.add(new Player(player2Name, ch2));			
		}
		else {
			players.add(new Player(player1Name, ch1));
			char ch = players.get(0).playerChar;
			if (ch != 'X')
				ch = 'X';
			else 
				ch = 'O';
			players.add(new Player(COMPUTER_NAME, ch));
		}
		startTime = System.currentTimeMillis();
		return gameId;
	}
	
	private String generateUUID() {
		return UUID.randomUUID().toString();
	}
	
	public final String getGameId() {
		return gameId;
	}

	private void initBoard() {
		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++ ) {
				board[i][j] = ' ';
			}
		}
	}
	
	private void fillCellMap() {
		int k=1;
		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++ ) {
				cellMap.put(k++, new Location(i, j));
			}
		}
	}

	private void printBoard() {
		StringBuffer buff = new StringBuffer();
		int k=1;
		buff.append("::::TIC-TAC-TOE BOARD LAYOUT/STATE::::\n\n");
		for (int i=0; i<n; i++) {
			buff.append("  ");
			for (int j=0; j<n; j++ ) {
				buff.append(' ').append(board[i][j]).append(' ');
				if (j != n-1) {
					buff.append("    |   ");
				}
			}
			buff.append("\n");
			buff.append("  ");
			for (int j=0; j<n; j++ ) {
				buff.append("[").append(k++).append("]");
				if (j != n-1) {
					buff.append("    |   ");
				}
			}			
			if (i != n-1)
				buff.append("\n----------------------------\n");
			else 
				buff.append("\n\n");
		}
		buff.append("\n\n");
		System.out.print(buff.toString());
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public String whoseTurnIsIt() {
		Player player = players.get(currentPlayer);
		return player.playerName;
	}
	
	public int isValidMove(String playerInput) {
		int location = validateAndGetLocationInput(playerInput, maxNumPlays);
		if (location == -1 || cellAlreadyFilled(location, currentPlayer)) {
			return -1;
		}
		else {
			return location;
		}
	}
	
	private int validateAndGetLocationInput(String playerLocationInput, int maxNum) {
		int location = -1;
		try {
			location = new Integer(playerLocationInput).intValue();
		} catch (NumberFormatException iex) {
			return location;
		}
		
		if (location <= maxNum && location >=1) {
			return location;	
		}
		System.out.println(playerLocationInput + " either invalid or is outside the range of 1 - " + maxNum);
		return -1;
	}	
	
	
	public void setLocation(int location) {
		currentUserLocation = location;
	}

	public void playMove() {
		if(!gameWon && numOfPlaysCompleted < maxNumPlays) {
			Integer location = currentUserLocation;
			gameWon = markCellAndGetGameStatus(currentPlayer, location);			
			++numOfPlaysCompleted;
			if (!gameWon) {
				currentPlayer = currentPlayer == 0 ? 1 : 0;
			} else {
				winner = currentPlayer;
			}
			
			if (numPlayers == 1 ) {
				if (!gameWon && numOfPlaysCompleted < maxNumPlays) {
					// computer chooses input.
					int cellToFill = generateRandomNumber();
					while (cellAlreadyFilled(cellToFill, currentPlayer)) {
						cellToFill = generateRandomNumber();
					}				
					gameWon = markCellAndGetGameStatus(currentPlayer, cellToFill);			
					++numOfPlaysCompleted;
					if (!gameWon) {
						currentPlayer = currentPlayer == 0 ? 1 : 0;
					} else {
						winner = currentPlayer;
					}
				}					
			}
		}
		else {
			System.out.println("Game ahs either been won already or completed as a draw or terminated! Cannot play move!");
		}
	}
	
	private int generateRandomNumber() {
		Random rand = new Random();
		// rand.nextInt((max - min) + 1) + min; 
		// max = n*n, min = 1;
		return rand.nextInt((n*n - 1) + 1) + 1;
	}

	private boolean cellAlreadyFilled(int location, int currentPlayer) {
		Location loc = cellMap.get(location); 
		if (board[loc.row][loc.column] != ' ') {
			if (!COMPUTER_NAME.equals(players.get(currentPlayer).playerName)) {
				System.out.println("The cell - [" +  location + "] is already taken. Please enter another valid cell number!");
			}
			return true;
		}
		return false;
	}
	
	private boolean markCellAndGetGameStatus(int currentPlayer, Integer location) {
		boolean wasGameWon;
		int row = cellMap.get(location).row;
		int column = cellMap.get(location).column;
		markCell(currentPlayer, row, column);
		printBoard();
		wasGameWon = updateStateAndCheckIfGameHasBeenWon(currentPlayer, row, column);
		return wasGameWon;
	}
	
	private void markCell(int player, int i, int j) {
		char charToUse = players.get(player).playerChar;
		board[i][j] = charToUse;
	}
	
	private boolean updateStateAndCheckIfGameHasBeenWon(int player, int row, int column) {
		return boardState.incrementStatesAndCheckIfGameHasBeenWon(player, row, column);
	}

	public boolean getCurrentState() {
		boolean gameEnded = false;
		if (gameWon || numOfPlaysCompleted == maxNumPlays) {
			gameEnded = true;
		}			
		return gameEnded;
	}
	
	public void printGameEndStatus() {
		printBoard();
		if (gameWon || numOfPlaysCompleted == maxNumPlays) {
			System.out.println("***************************************************");
			System.out.println("****************GAME OUTCOME***********************");		
			if (gameWon) {
				System.out.println("GAME WON BY PLAYER - " + players.get(currentPlayer).playerName + "! CONGRATULATIONS!!");
			}
			else if (numOfPlaysCompleted == maxNumPlays) {
				System.out.println("NOBODY WON! GAME WAS A DRAW!");
			}
			System.out.println("***************************************************");
			System.out.println("***************************************************");			
		}			
	}
	
	public String whoWon() {
		if (gameWon) {
			Player player = players.get(winner);
			return player.playerName;			
		}
		else {
			return "No winner";
		}
	}
}
