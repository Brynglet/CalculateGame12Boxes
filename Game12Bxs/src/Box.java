/**
 * These are the boxes in the game, and bonus boxes.
 * @author Bryngel
 *
 */
public class Box {

	private Integer value;
	private boolean extraLife;
	private boolean gameOver;
	private boolean playNewGame;

	public Box(Integer value, boolean extraLife, boolean gameOver, boolean playNewGame) {
		this.value = value;
		this.extraLife = extraLife;
		this.gameOver = gameOver;
		this.playNewGame = playNewGame;
	}

	@Override
	public String toString() {
		return ("getValue:" + this.getValue()  + " extraLife:" + this.getExtraLife() + " gameOver:" + this.gameOver + " playNewGame:" + this.getPlayNewGame());
	}

	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public boolean getExtraLife() {
		return extraLife;
	}
	public void setExtraLife(boolean extraLife) {
		this.extraLife = extraLife;
	}
	public boolean getGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	public boolean getPlayNewGame() {
		return playNewGame;
	}
	public void setPlayNewGame(boolean playNewGame) {
		this.playNewGame = playNewGame;
	}
}
