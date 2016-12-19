package com.intuit.craftdemo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.intuit.craftdemo.service.TicTacToeService;
import com.intuit.craftdemo.service.TicTacToeService.GameExpiredException;

public class TicTacToeClient {
	
	private class Player {
		final String playerName;
		final char playerChar;
		
		Player(String playerName, char playerChar) {
			this.playerName = playerName;
			this.playerChar = playerChar;
		}
	}
	
	private static class InputValidation {
		static boolean validateNumberOfPlayers(char ch) {
			if (ch == '1' || ch == '2')
				return true;
			else
				return false;
		}

		static boolean validateCharacterToUseToMarkLocationOnBoard(char ch) {
			if (ch == 'X' || ch == 'O') {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	private final int n;
	private final int maxNumPlays;
	private final BufferedReader reader;
	private int numPlayers;
	private String gameId;
	List<Player> players = new ArrayList<>();


	public TicTacToeClient() {
		n = 3;
		maxNumPlays = n*n;
		reader = new BufferedReader(new InputStreamReader(System.in));
		
	}

	public int getN() {
		return n;
	}

	public int getNumberOfPlayersAsInput() {
		System.out.println("Welcome to a game of TicTacToe!!");
		String numPlayersInput = getInput("Do you want to play as 2 players or as 1 player against the computer (Valid inputs - 2 or 1)?");
		char ch = numPlayersInput.charAt(0);
		while (!InputValidation.validateNumberOfPlayers(ch)) {
			numPlayersInput = getInput("Please enter 1/2");
			ch = numPlayersInput.charAt(0);
		}
		numPlayers = ch - '0';
		return numPlayers;
	}
	
	private String getInput(String prompt) {
		String input = "";
		while (input.equals("")) {
			System.out.println(prompt);
			try {
				input = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return input;
	}
	
	public List<Player> getPlayerNamesAndCharactersToUse() {
		if (numPlayers == 2) {
			players.add(createPlayer(1));
			players.add(createPlayer(2));
			System.out.println(players.get(0).playerName + " and " + players.get(1).playerName + ". We are ready to play!");
		}
		else {
			players.add(createPlayer(1));
			System.out.println(players.get(0).playerName + " is playing against the Computer!");
		}
		System.out.println("---------------------------------------------------");
		return players;
	}
	
	private Player createPlayer(int playerNum) {
		String player = getInput("Player" + playerNum + ", input your name: ");
		if (playerNum == 2) { // Input validation of name only for player 2.
			while (player.equals(players.get(0).playerName)) {
				player = getInput("Player" + playerNum + ", re-input your name (" + player + ") already taken!");
			}			
		}
		char ch;
		if (playerNum == 1) { // Input validation of character only for player 1.
			String character = getInput(player + ", input character to use (X / O).");
			ch = character.charAt(0);
			while (!InputValidation.validateCharacterToUseToMarkLocationOnBoard(ch)) {
				character = getInput(player + ", invalid character (" + ch + "). Please enter (X / O).");
				ch = character.charAt(0);
			}			
		} else {
			if (players.get(0).playerChar != 'X')
				ch = 'X';
			else 
				ch = 'O';
		}
		return new Player(player, ch);
	}
	
	public String getNextMove(String player) {
		return getInput(player + ", pick your location [1 - " + maxNumPlays + "]");	
	}
	
	
	public static void main(String[] args) {
		TicTacToeClient client = new TicTacToeClient();
		int numPlayers =  client.getNumberOfPlayersAsInput();
		List<Player> players = client.getPlayerNamesAndCharactersToUse();
		int n = client.getN();
		
		TicTacToeService ticTacToeGameService = new TicTacToeService();
		
		try {
			String gameId;
			if (numPlayers == 2) {
				gameId = ticTacToeGameService.startNewGame(n, numPlayers, players.get(0).playerName,
						players.get(0).playerChar, players.get(1).playerName, players.get(1).playerChar);
			} else {
				gameId = ticTacToeGameService.startNewGame(n, numPlayers, players.get(0).playerName,
						players.get(0).playerChar, null, ' ');			
			}
			while(true) {
				String player = ticTacToeGameService.whoseTurnIsIt(gameId);
				String playerInput = client.getNextMove(player);
				int location = ticTacToeGameService.isValidMove(gameId, playerInput);
				while (location == -1) {
					playerInput = client.getNextMove(player);
					location = ticTacToeGameService.isValidMove(gameId, playerInput);
				}
				ticTacToeGameService.setLocation(gameId, location);
				ticTacToeGameService.playMove(gameId);
				boolean gameEnded = ticTacToeGameService.didGameEnd(gameId);
				if (gameEnded) {
					ticTacToeGameService.printGameEndStatus(gameId);
					break;
				}
			}
			ticTacToeGameService.gameCompleted(gameId);			
		}
		catch (GameExpiredException gee) {
			System.out.println("Game expired - " + gee.getMessage());
			System.out.println("Game has to be restarted!!");
		}
	}	
	
	
}
