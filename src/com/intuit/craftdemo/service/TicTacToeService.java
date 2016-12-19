package com.intuit.craftdemo.service;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.intuit.craftdemo.server.TicTacToe;

public class TicTacToeService {
	
	private final ConcurrentMap<String, TicTacToe> mapOfGames = new ConcurrentHashMap<>();
	ScheduledExecutorService scheduledExecService = Executors.newScheduledThreadPool(1);
	public final static long TIME_TO_LIVE_FOR_GAME = 30 * 60 * 1000; //30 mts
	public final static String GAME_EXPIRED_EXCEPTION_MSG = "Session of game expired, as it was open for longer than 30 minutes";
	
	
	class CleanupMapTask implements Runnable {
		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			Set<String> gameIdsThatAreTooOld = new HashSet<>();
			for (Entry<String, TicTacToe> game : mapOfGames.entrySet()) {
				if (currentTime - game.getValue().getStartTime() > TIME_TO_LIVE_FOR_GAME) {
					gameIdsThatAreTooOld.add(game.getKey());
				}
			}
			for (String gameId:gameIdsThatAreTooOld) {
				mapOfGames.remove(gameId);
			}
			System.out.println("GameIds removed as they were old - " + gameIdsThatAreTooOld);
		}
		
	}
	
	public class GameExpiredException extends Exception {
		private static final long serialVersionUID = 1L;
		public GameExpiredException() {super();}
		public GameExpiredException(String message) {super(message);}
	}
	public class TicTacToeServiceException extends Exception {
		private static final long serialVersionUID = 1L;
		public TicTacToeServiceException() {super();}
		public TicTacToeServiceException(String message) {super(message);}
	}
	
	
	public TicTacToeService() {
		CleanupMapTask task = new CleanupMapTask();
		scheduledExecService.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
	}


	public String startNewGame(int n, int numPlayers, String player1Name, char ch1, String player2Name, char ch2) {
		TicTacToe ticTacToe = new TicTacToe(n, numPlayers);
		String gameId = ticTacToe.startNewGame(player1Name, ch1, player2Name, ch2);		
		mapOfGames.put(gameId, ticTacToe);
		return gameId;
	}

	public boolean getCurrentState(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			return ticTacToe.getCurrentState();
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}
	
	public String whoseTurnIsIt(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			return ticTacToe.whoseTurnIsIt();
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}
	
	public int isValidMove(String gameId, String playerInput) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			return ticTacToe.isValidMove(playerInput);
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}
	
	public void setLocation(String gameId, int location) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			ticTacToe.setLocation(location);
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}

	public void playMove(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			ticTacToe.playMove();
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}
	
	public String ifGameEndedWhoWon(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null) {
			if (ticTacToe.getCurrentState()) {
				return ticTacToe.whoWon();
			}
			else {
				return "Nobody, game not yet won";
			}			
		}
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}	
	
	public boolean didGameEnd(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			return ticTacToe.getCurrentState();	
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);
	}
	
	public String whoWon(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			return ticTacToe.whoWon();
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);		
	}

	public void printGameEndStatus(String gameId) throws GameExpiredException {
		TicTacToe ticTacToe = mapOfGames.get(gameId);
		if (ticTacToe != null)
			ticTacToe.printGameEndStatus();		
		else 
			throw new GameExpiredException(GAME_EXPIRED_EXCEPTION_MSG);		
	}	
	
	public void gameCompleted(String gameId) throws GameExpiredException {
		mapOfGames.remove(gameId);
	}

}
