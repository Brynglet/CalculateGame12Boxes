import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 2018-12-11
 * @author Bryngel
 *
 * Result of run on my home pc
 * 64 bit Windows 10
 * Intel i3-6100
 * 8 GB RAM
 *
 * 	***Simulation***
 *	Time: 2692 ms
 *	Expected result simulation: €73.0
 *
 * 	***Calculation***
 *	Time: 3824 ms
 *	Expected result calculation: €72,8
 *
 */
public class Game {

	private static final int FIRST_ROUND = 1;
	private static final int SECOND_ROUND = 2;
	private static final int INITIAL_GAME_EARNINGS_ZERO = 0;
	private static final int NUMBER_OF_GAMES = 10000000;
	private static final boolean IGNORE_GAME_OVER_FALSE = false;
	private static final boolean IGNORE_GAME_OVER_TRUE = true;
	private static long totalEarningsSimulated = 0;

	private static final int BONUS_ARRAY_LENGTH_ROUND_1 = 4;
	private static final int BONUS_ARRAY_LENGTH_ROUND_2 = 3;

	private static double expectedResultCalculated = 0.00;

	/* Game exists of 12 boxes. */
	private static final Box[] gameBoxes = initializeGameBoxes();
	/* 4 bonus boxes. */
	private static final Box[] bonusBoxes = initializeBonusBoxes();
	/* The order in which way the boxes are opened. Will be shuffled before each game (and bonus game). */
	private static List<Integer> orderOfPickingBoxes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		/* Simulate solution */
		final long startTimeSimulation = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_GAMES; i++) {
			totalEarningsSimulated += playGame(FIRST_ROUND, INITIAL_GAME_EARNINGS_ZERO);
		}
		final double expectedResultSimulation = (totalEarningsSimulated / NUMBER_OF_GAMES);
		final long endTimeSimulation = System.currentTimeMillis();

		calculateExpectedResult(initializeGameBoxes(), FIRST_ROUND, IGNORE_GAME_OVER_FALSE, 1.0000);
		final long endTimeCalculation = System.currentTimeMillis();

		/* Print solutions */
		System.out.println("***Simulation***");
		System.out.println("Time: " + (endTimeSimulation - startTimeSimulation) + " ms");
		System.out.println("Expected result simulation: €" + expectedResultSimulation);
		System.out.println();
		System.out.println("***Calculation***");
		System.out.println("Time: " + (endTimeCalculation - endTimeSimulation) + " ms");
		/* Round for presentation */
		DecimalFormat df = new DecimalFormat("#.#");
		System.out.println("Expected result calculation: €" + df.format(expectedResultCalculated));
		System.out.println();
	}

	/**
	 * The idea is:
	 * Expected value for all boxes (for the whole game) =
	 *
	 * probability this box is chosen * value (or other action) for this box
	 * +
	 * Expected value for the rest (all other boxes not chosen).
	 *
	 * @param boxesIn
	 * @param round
	 * @param ignoreGameOver
	 * @param previousProb
	 */
	private static void calculateExpectedResult(Box[] boxesIn, int round, boolean ignoreGameOver, double previousProb) {

		for (int k = 0; k < boxesIn.length; k++) {

			double probabilityOpenedThisBox = previousProb / boxesIn.length;
			final Box box = boxesIn[k];
			Box[] remainingBoxes = getRemainingBoxes(boxesIn, k);

			if (box.getValue() != null) {
				/* Value box */
				expectedResultCalculated += probabilityOpenedThisBox * box.getValue().intValue();
				calculateExpectedResult(remainingBoxes, round, ignoreGameOver, probabilityOpenedThisBox);
			} else if (box.getExtraLife() == true) {
				/* Extra life box, nullify next game over box. */
				calculateExpectedResult(remainingBoxes, round, IGNORE_GAME_OVER_TRUE, probabilityOpenedThisBox);
			} else if (box.getGameOver() == true) {
				/* Game over box */
				if (ignoreGameOver) {
					calculateExpectedResult(remainingBoxes, round, IGNORE_GAME_OVER_FALSE, probabilityOpenedThisBox);
				} else {
					if (round == 1) {
						/* First round */
						expectedResultCalculated += probabilityOpenedThisBox / BONUS_ARRAY_LENGTH_ROUND_1 * (20 + 10 + 5);
						calculateExpectedResult(remainingBoxes, SECOND_ROUND, IGNORE_GAME_OVER_FALSE, probabilityOpenedThisBox / 4.0);
					} else {
						/* Second round */
						expectedResultCalculated += probabilityOpenedThisBox / BONUS_ARRAY_LENGTH_ROUND_2 * (20 + 10 + 5);
						// True game over
						return;
					}
				}
			}

		}

	}

	/**
	 *
	 * @param boxes
	 * @param k: include all but box k to the return array of boxes
	 * @return
	 */
	private static Box[] getRemainingBoxes(Box[] boxes, int k) {
		Box[] remainingBxs = new Box[boxes.length - 1];
		int index = 0;
		for (int m = 0; m < boxes.length; m++) {
			if (m != k) {
				remainingBxs[index++] = boxes[m];
			}
		}
		return remainingBxs;
	}

	/**
	 *
	 * @param round
	 *     1 first round
	 *     2 second round (bonus round)
	 * @param gameEarnings
	 * @return
	 */
	private static int playGame(int round, int gameEarnings) {

		shuffleOrderOfPickingBoxes();

		boolean ignoreGameOver = false;

		for (int k = 0; k < orderOfPickingBoxes.size(); k++) {

			final Box openedBox = gameBoxes[orderOfPickingBoxes.get(k)];

			if (openedBox.getValue() != null) {
				/* Value box */
				gameEarnings += openedBox.getValue().intValue();
			} else if (openedBox.getExtraLife() == true) {
				/* Extra life box, nullify next game over box. */
				ignoreGameOver = true;
			} else if (openedBox.getGameOver() == true) {
				/* Game over box */
				if (ignoreGameOver) {
					ignoreGameOver = false;
					continue;
				} else {
					final Box bonusBox = getBonusBox(round);
					if (bonusBox.getPlayNewGame() == true) {
						return playGame(SECOND_ROUND, gameEarnings);
					} else {
						return (gameEarnings + bonusBox.getValue().intValue());
					}
				}
			}
		}

		/* Will never get here. TODO Exception handling. */
		return -1;
	}

	/**
	 * Setup of the game board which consists of 12 boxes.
	 * Can be fixed as orderOfPickingBoxes is random.
	 *
	 * @return
	 */
	private static Box[] initializeGameBoxes() {
		/* Game consists of 12 boxes */
		final Box[] gameBoxes = new Box[12];

		/* Value boxes */
		gameBoxes[0] = new Box(100, false, false, false);
		gameBoxes[1] = new Box(20, false, false, false);
		gameBoxes[2] = new Box(20, false, false, false);
		gameBoxes[3] = new Box(5, false, false, false);
		gameBoxes[4] = new Box(5, false, false, false);
		gameBoxes[5] = new Box(5, false, false, false);
		gameBoxes[6] = new Box(5, false, false, false);
		gameBoxes[7] = new Box(5, false, false, false);

		/* Extra life box */
		gameBoxes[8] = new Box(null, true, false, false);

		/* Game over boxes */
		gameBoxes[9] = new Box(null, false, true, false);
		gameBoxes[10] = new Box(null, false, true, false);
		gameBoxes[11] = new Box(null, false, true, false);

		return gameBoxes;
	}

	/**
	 * Setup of the 4 bonus boxes.
	 *
	 * @return
	 */
	private static Box[] initializeBonusBoxes() {
		/* Bonus consists of 4 boxes */
		final Box[] bonusBoxes = new Box[4];

		/* Value boxes */
		bonusBoxes[0] = new Box(20, false, false, false);
		bonusBoxes[1] = new Box(10, false, false, false);
		bonusBoxes[2] = new Box(5, false, false, false);
		/* New game */
		bonusBoxes[3] = new Box(null, false, false, true);

		return bonusBoxes;
	}

	/**
	 *
	 * @param round
	 * @return
	 */
	private static Box getBonusBox(int round) {
		final Random rand = new Random();
		if (round == FIRST_ROUND) {
			/* Returns one of four boxes randomly */
			return bonusBoxes[rand.nextInt(BONUS_ARRAY_LENGTH_ROUND_1)];
		} else {
			/* Returns one of the first three boxes randomly */
			return bonusBoxes[rand.nextInt(3)];
		}
	}

	/**
	 * Randomize the way boxes are chosen by the player.
	 */
	private static void shuffleOrderOfPickingBoxes() {
		Collections.shuffle(orderOfPickingBoxes);
	}

}