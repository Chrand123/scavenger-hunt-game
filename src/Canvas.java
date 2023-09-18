import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class Canvas extends JComponent {
	public final int HEIGHT;
	public final int WIDTH;

	private static final int BTN_WIDTH = Main.WIDTH / 2 - Main.WIDTH / 20;
	private static final int BTN_HEIGHT = Main.HEIGHT / 6;
	private static final int CENTER_X = (Main.WIDTH / 2 - BTN_WIDTH / 2);
	private static final int CENTER_Y = (Main.HEIGHT - BTN_HEIGHT) / 2;
	
	//create custom button class to replace these
	//MENU
	public static final Rectangle playBtn = new Rectangle(CENTER_X, CENTER_Y - BTN_HEIGHT / 2 - BTN_HEIGHT / 10, BTN_WIDTH, BTN_HEIGHT);
	public static final Rectangle rulesBtn = new Rectangle(CENTER_X, CENTER_Y + BTN_HEIGHT - 2 * BTN_HEIGHT / 10, BTN_WIDTH, BTN_HEIGHT);
	public static final Rectangle settingsBtn = new Rectangle(CENTER_X, CENTER_Y + 5 * BTN_HEIGHT / 2 - 3 * BTN_HEIGHT / 10, BTN_WIDTH, BTN_HEIGHT);

	
	//Game End Screen
	public static final Rectangle playAgainBtn = new Rectangle(CENTER_X - BTN_WIDTH / 2 - Main.WIDTH / 60, Main.HEIGHT - BTN_HEIGHT - Main.HEIGHT / 20, BTN_WIDTH, BTN_HEIGHT);
	public static final Rectangle menuBtn = new Rectangle(CENTER_X + BTN_WIDTH / 2 + Main.WIDTH / 60, Main.HEIGHT - BTN_HEIGHT - Main.HEIGHT / 20, BTN_WIDTH, BTN_HEIGHT);
	
	//Rules
	public static final Rectangle backBtn = new Rectangle(CENTER_X, Main.HEIGHT - BTN_HEIGHT - Main.HEIGHT / 20, BTN_WIDTH, BTN_HEIGHT);
	
	//Settings
	private static final int SMALLBTN_WIDTH = 140;
	private static final int SMALLBTN_HEIGHT = 50;
	public static int[] activeBtns = {0, 0, 0}; //game length, map size, cpu difficulty
	public static final Rectangle[] gameLength_Btns = {new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT)};
	public static final Rectangle[] mapSize_Btns = {new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT)};
	public static final Rectangle[] cpuDifficulty_Btns = {new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT), new Rectangle(SMALLBTN_WIDTH, SMALLBTN_HEIGHT)};

	//Item
	public static final Rectangle skipBtn = new Rectangle(CENTER_X + BTN_WIDTH / 4, Main.HEIGHT - BTN_HEIGHT / 2 - Main.WIDTH / 20, BTN_WIDTH / 2, BTN_HEIGHT / 2);

	//Pause
	public static final Rectangle resumeBtn = new Rectangle(CENTER_X, CENTER_Y, BTN_WIDTH, BTN_HEIGHT);
	public static final Rectangle quitBtn = new Rectangle(CENTER_X, CENTER_Y + 7 * BTN_HEIGHT / 5, BTN_WIDTH, BTN_HEIGHT);

	
	private Image menu_background;
	private Image rank1;
	private Image rank2;
	private Image rank3;
	private Image rank4;
	
	private Board board;

	public Canvas(int width, int height) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.board = null;
		menu_background = makeNewImage("menu_background.jpg", Main.WIDTH, Main.HEIGHT, 0);
		
		int rankSize = 30;
		rank1 = makeNewImage("1st.png", rankSize, rankSize, 4);
		rank2 = makeNewImage("2nd.png", rankSize, rankSize, 4);
		rank3 = makeNewImage("3rd.png", rankSize, rankSize, 4);
		rank4 = makeNewImage("4th.png", rankSize, rankSize, 4);
		
	}

	public Image makeNewImage(String name, int width, int height, int style) {
		Image img = new ImageIcon("res/" + name).getImage();
		img = resizeImage(img, width, height, style);
		return img;
	}
	
	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		if (Main.gameState == Main.STATE.Menu) {
			g.setColor(new Color(30, 180, 30));
			g.fill(new Rectangle(0, 0, Main.WIDTH, Main.HEIGHT));
			g.drawImage(menu_background, 0, 0, this);
			drawHeader(g, "Andrew's Scavenger Hunt", 90);
			drawButton(g, "Play Game", playBtn);
			drawButton(g, "How to Play", rulesBtn);
			drawButton(g, "Settings", settingsBtn);
		} else if (Main.gameState == Main.STATE.Game) {
			gr.setColor(new Color(0, 80, 30));
			g.fill(board.getBoardBounds());
			gr.setColor(new Color(30, 180, 30));
			g.fill(board.getPlayBounds());
			gr.setColor(new Color(200, 0, 0));
			g.fillOval(board.getCenterPoint().x, board.getCenterPoint().y, board.getCenterPoint().width, board.getCenterPoint().height);
			for (GameObject object : board.getItems()) {
				g.drawImage(object.getImage(), object.getX(), object.getY(), this);
			}
			
			g.drawImage(board.getPlayer().getImage(), board.getPlayer().getX(), board.getPlayer().getY(), this);
			
			for (GameObject object : board.getObstacles()) {
				g.drawImage(object.getImage(), object.getX(), object.getY(), this);
			}
			
			//TIMER and other LABELS
			gr.setFont(new Font("monospaced", 1, 40));
			drawTimer(gr);
//			gr.drawString(board.getGame().getTime(), board.getPlayer().getX() - Main.WIDTH / 2 + 30, board.getPlayer().getY() - Main.HEIGHT / 2 + 60);
			gr.setFont(new Font("monospaced", 1, 20));
			drawTopRightLabels(gr, "Points = " + board.getGame().getPoints(), "Items Left = " + board.getItemsLeft());

			//Hints (with command H):
			gr.setFont(new Font("monospaced", 1, itemDistanceFontSize(board.itemDistanceString())));
			drawBottomLeftLabels(gr, board.itemDistanceString());
			//Rankings (with command L): (1st, 2nd, 3rd, and 4th place) - show same place as hints, toggle them on/off
			drawRankings(gr);
			if (Main.playState == Main.STATE.Item) {
				drawItemState(g);
				
			}
			if (Main.playState == Main.STATE.Pause) {
				drawPauseState(g);
			}
			if (!board.getGame().inputEnded()) {
				g.setColor(new Color(0, 0, 0));
				g.drawOval(Main.WIDTH / 2 - 75, Main.HEIGHT / 2 - 140, 150, 70);
				g.setColor(new Color(0, 80, 30));
				g.fillOval(Main.WIDTH / 2 - 75, Main.HEIGHT / 2 - 140, 150, 70);
				g.setFont(new Font("monospaced", 1, 20));
				if (board.getGame().getInputString().equals("Correct!")) {
					g.setColor(new Color(30, 180, 30));
				} else {
					g.setColor(new Color(200, 0, 0));
				}
				drawInRect(g, board.getGame().getInputString(), new Rectangle(Main.WIDTH / 2 - 75, Main.HEIGHT / 2 - 140, 150, 70));
			}
		} else if (Main.gameState == Main.STATE.End) {
			g.setColor(new Color(30, 180, 30));
			g.fill(new Rectangle(0, 0, Main.WIDTH, Main.HEIGHT));
			g.drawImage(menu_background, 0, 0, this);
			drawHeader(g, "Results", 60);
			drawResults(g);
			drawButton(g, "Play Again", playAgainBtn);
			drawButton(g, "Back to Menu", menuBtn);
		} else if (Main.gameState == Main.STATE.Rules) {
			g.setColor(new Color(30, 180, 30));
			g.fill(new Rectangle(0, 0, Main.WIDTH, Main.HEIGHT));
			drawRules(g);
		} else if (Main.gameState == Main.STATE.Settings) {
			g.setColor(new Color(30, 180, 30));
			g.fill(new Rectangle(0, 0, Main.WIDTH, Main.HEIGHT));
			drawSettings(g);
		}
		
	}
	
	private void drawHeader(Graphics2D g, String string, int y) {
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 50));
		g.drawString(string, (Main.WIDTH - (int) g.getFontMetrics().stringWidth(string)) / 2, y);		
	}
	
	private void drawButton(Graphics2D g, String string, Rectangle rect) {
		g.setColor(new Color(0, 80, 30));
		g.fill(rect);
		g.setColor(new Color(0, 0, 0));
		g.draw(rect);
		g.setColor(new Color(30, 180, 30));
		g.setFont(new Font("monospaced", 1, 40));
		drawInRect(g, string, rect);
	}
	
	private void drawSmallButton(Graphics2D g, String string, Rectangle rect) {
		g.setColor(new Color(0, 80, 30));
		g.fill(rect);
		g.setColor(new Color(0, 0, 0));
		g.draw(rect);
		g.setColor(new Color(30, 180, 30));
		g.setFont(new Font("monospaced", 1, 25));
		drawInRect(g, string, rect);
	}
	
	private void drawInRect(Graphics2D g, String string, Rectangle rect) {
		int x = rect.x + rect.width / 2 - (int) (g.getFontMetrics().getStringBounds(string, g).getWidth() / 2);
		int y = rect.y + rect.height / 2 + (int) (g.getFontMetrics().getStringBounds(string, g).getHeight() / 4);
		g.drawString(string, x, y);
	}
	
	private int drawText(Graphics2D g, String text, int x, int y) {
		while (text.length() > 0) {
			String line = text;
			int i = text.length();
			while ((int) g.getFontMetrics().getStringBounds(line, g).getWidth() > Main.WIDTH - 2 * x) {
				i--;
				line = line.substring(0, i);
			}
			if ((int) g.getFontMetrics().getStringBounds(text, g).getWidth() > Main.WIDTH - 2 * x) {
				while (!text.substring(i - 1, i).equals(" ")) {
					i--;
					line = line.substring(0, i);
				}
			}
			
			text = text.substring(i);
			g.drawString(line, x, y);
			y += (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
		}
		return y;
	}
	
	private void drawRect(Graphics2D g, int x, int y, int width, int height) {
		g.setColor(new Color(0, 80, 30));
		g.fillRect(x, y, width, height);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(x, y, width, height);
	}
	
	private int stringWidth(Graphics2D g, String string) {
		return (int) (g.getFontMetrics().getStringBounds(string, g).getWidth());
	}
	
	private int stringHeight(Graphics2D g, String string) {
		return (int) (g.getFontMetrics().getStringBounds(string, g).getHeight());
	}
	
	private void drawToggleButton(Graphics2D g, String string, Rectangle rect, boolean active) {
		if (!active) {
			g.setColor(new Color(0, 80, 30));
		} else {
			g.setColor(new Color(30, 180, 30));
		}
		g.fill(rect);
		g.setColor(new Color(0, 0, 0));
		g.draw(rect);
		if (active) {
			g.setColor(new Color(0, 80, 30));
		} else {
			g.setColor(new Color(30, 180, 30));
		}
		g.setFont(new Font("monospaced", 1, 25));
		drawInRect(g, string, rect);
	}
	
	private void drawSettings(Graphics2D g) {
		drawHeader(g, "Settings", 70);
		drawButton(g, "Back to Menu", backBtn);
		int y = 110;
		int x = Main.WIDTH / 40;
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 30));
		y += 40;
		drawText(g, "Game Length:", x, y);
		int tempX = x;
		tempX += stringWidth(g, "CPU Difficulty: ");
		String[] labels = {"Short", "Medium", "Long"};
		for (int i = 0; i < gameLength_Btns.length; i++) {
			String label = labels[i];
			boolean active = activeBtns[0] == i;
			gameLength_Btns[i].setLocation(tempX, y - 30);
			drawToggleButton(g, label, gameLength_Btns[i], active);
			tempX += SMALLBTN_WIDTH;
		}
		y += 80;
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 30));
		drawText(g, "Map Size:", x, y);
		tempX = x;
		tempX += stringWidth(g, "CPU Difficulty: ");
		String[] labels2 = {"Small", "Medium", "Large"};
		for (int i = 0; i < mapSize_Btns.length; i++) {
			String label = labels2[i];
			boolean active = activeBtns[1] == i;
			mapSize_Btns[i].setLocation(tempX, y - 30);
			drawToggleButton(g, label, mapSize_Btns[i], active);
			tempX += SMALLBTN_WIDTH;
		}
		y += 80;
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 30));
		drawText(g, "CPU Difficulty:", x, y);
		tempX = x;
		tempX += stringWidth(g, "CPU Difficulty: ");
		String[] labels3 = {"Easy", "Normal", "Hard"};
		for (int i = 0; i < cpuDifficulty_Btns.length; i++) {
			String label = labels3[i];
			boolean active = activeBtns[2] == i;
			cpuDifficulty_Btns[i].setLocation(tempX, y - 30);
			drawToggleButton(g, label, cpuDifficulty_Btns[i], active);
			tempX += SMALLBTN_WIDTH;
		}
	}
	
	private void drawPauseState(Graphics2D g) {
		int x = Main.WIDTH / 40;
		int y = Main.HEIGHT / 8;
		g.setColor(new Color(180, 180, 100)); //replace this with ancient scroll pic
		g.fillRect(x, y, Main.WIDTH - 2 * x, Main.HEIGHT - y - x);
		g.setColor(new Color(0, 80, 30));
		g.drawRect(x, y, Main.WIDTH - 2 * x, Main.HEIGHT - y - x);
		g.setFont(new Font("monospaced", 1, 20));
		x += Main.WIDTH / 80;
		y += 2 * (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		drawHeader(g, "Paused", y);
		y += (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		drawButton(g, "Resume Game", resumeBtn);
		drawButton(g, "Quit Game", quitBtn);
		y += 2 * (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		g.setFont(new Font("monospaced", 2, 20));
		y += (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		
	}
	
	private void drawItemState(Graphics2D g) {
		int x = Main.WIDTH / 40;
		int y = Main.HEIGHT / 8;
		g.setColor(new Color(180, 180, 100)); //replace this with ancient scroll pic
		g.fillRect(x, y, Main.WIDTH - 2 * x, Main.HEIGHT - y - x);
		g.setColor(new Color(0, 80, 30));
		g.drawRect(x, y, Main.WIDTH - 2 * x, Main.HEIGHT - y - x);
		g.setFont(new Font("monospaced", 1, 20));
		x += Main.WIDTH / 80;
		y += (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		g.drawString("Item: " + ((Item) board.getItemFound()).getName(), x, y);
		y += (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		g.drawString("Points: " + ((Item) board.getItemFound()).getPoints(), x, y);
		y += 2 * (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		g.setFont(new Font("monospaced", 2, 20));
		y = drawText(g, ((Item) board.getItemFound()).getClue().toString(), x, y);
		y += (int) g.getFontMetrics().getStringBounds("a", g).getHeight();
		for (int i = 0; i < ((Item) board.getItemFound()).getClue().getAnswer().length(); i++) {
			drawRect(g, x, y, 20, 20); 
			g.setFont(new Font("monospaced", 1, 20));
			g.setColor(new Color(30, 180, 30));
			if (i < board.getGame().getItemGuess().length()) {
				drawInRect(g, board.getGame().getItemGuess().substring(i, i + 1), new Rectangle(x, y, 20, 20));
			}
			x += 30;
		}
		drawSmallButton(g, "Skip Item", skipBtn);
	}
	
	
	
	private void drawRules(Graphics2D g) { 
		drawHeader(g, "How to Play", 70);
		drawButton(g, "Back to Menu", backBtn);
		int y = 110;
		int x = Main.WIDTH / 40;
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 30));
		String text = "Move around by using the WASD or the arrow keys. "
				+ "Collect items to gain points, as long as you can answer the riddle. "
				+ "Finish with the most points by the end of the time limit. "
				+ "You can pause the game by pressing the SPACE bar, as long as you are not solving a clue.";
		drawText(g, text, x, y);
	}
	
	private void drawResults(Graphics g) {
		int y = 110;
		int x = Main.WIDTH / 40;
		g.setColor(new Color(0, 80, 30));
		g.setFont(new Font("monospaced", 1, 30));
		if (!board.getGame().countdownEnded() && board.getItemsLeft() > 0) {
			String itemsFound = "Items Found: " + board.getPlayer().getItemsCollected().size();
			g.drawString(itemsFound, x, y);	
			y += (int) (g.getFontMetrics().getStringBounds(itemsFound, g).getHeight());
			String pointsString = "Total Points: " + board.getGame().getPoints();
			g.drawString(pointsString, x, y);
		} else {
			List<CPU> cpuPlayers = board.getGame().getCPUs();			
			List<PlayerObject> players = new ArrayList<PlayerObject>();
			players.add(new PlayerObject(board.getGame().playerString(), board.getGame().getPoints()));
			for (CPU cpu : cpuPlayers) {
				String name = cpu.getName();
				int points = cpu.getPoints();
				players.add(new PlayerObject(name, points));
			}
			
			//SORT:
			sort(players);
			String[] place = {"1st", "2nd", "3rd", "4th"};
			for (int i = 4; i > 0; i--) {
				g.drawString(place[4 - i] + " - " + players.get(i - 1).getName() + " (" + players.get(i - 1).getPoints() + " pts)", x, y);
				y += (int) (g.getFontMetrics().getStringBounds("a", g).getHeight());
			}
		}
	}
	
	private void drawTimer(Graphics g) {
		String time = board.getGame().getTime();
		int x = (Main.WIDTH - (int) g.getFontMetrics().getStringBounds(time, g).getWidth()) / 2;
		int y = (int) g.getFontMetrics().getStringBounds(time, g).getHeight();
		g.drawString(time, x, y);
	}
	
	private void drawTopRightLabels(Graphics g, String label1, String label2) {
		int padding = 5;
		int x = Main.WIDTH - (int) g.getFontMetrics().getStringBounds(label1, g).getWidth() - padding;
		int y = (int) g.getFontMetrics().getStringBounds(label1, g).getHeight();
		g.drawString(label1, x, y);
		int x2 = Main.WIDTH - (int) g.getFontMetrics().getStringBounds(label2, g).getWidth() - padding;
		int y2 = y + padding + (int) g.getFontMetrics().getStringBounds(label2, g).getHeight();
		g.drawString(label2, x2, y2);
	}
	
	private void drawBottomLeftLabels(Graphics g, String label1) {
		int padding = 5;
		int x = padding;
		int y = Main.HEIGHT - padding;
		g.drawString(label1, x, y);
	}
	
	class PlayerObject {
		int points;
		String name;
		
		public PlayerObject(String playerName, int playerPoints) {
			name = playerName;
			points = playerPoints;
		}
		
		public int getPoints() {
			return points;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			return name + " - " + points + " pts";
		}
	}
	
	private static void sort(List<PlayerObject> players) {
		for (int i = 1; i < players.size(); i++) {
			PlayerObject curPlayer = players.get(i);
			int targetIndex = i;
			while (targetIndex > 0 && curPlayer.getPoints() < players.get(targetIndex - 1).getPoints()) {
				players.set(targetIndex, players.get(targetIndex - 1));
				targetIndex--;
			}
			players.set(targetIndex, curPlayer);
		}
	}
	
	private void drawRankings(Graphics g) {
		//TOP LEFT:
		g.setFont(new Font("monospaced", 1, 15));
		List<CPU> cpuPlayers = board.getGame().getCPUs();		
		
		
		List<PlayerObject> players = new ArrayList<PlayerObject>();
		players.add(new PlayerObject(board.getGame().playerString(), board.getGame().getPoints()));
		for (CPU cpu : cpuPlayers) {
			String name = cpu.getName();
			int points = cpu.getPoints();
			players.add(new PlayerObject(name, points));
		}
		
		//SORT:
		sort(players);
		for (int i = 0; i < 4; i++) {
			if (players.get(i).getName().equals(board.getGame().playerString())) {
				board.getGame().setPlayerRank(4 - i);
			}
		}
		
		for (PlayerObject cpu : players) {
		}
			
		int padding = 5;
		int x = padding;
		int y = (int) g.getFontMetrics().getStringBounds("Rankings:", g).getHeight();
		g.drawString("Rankings:", x, y);
		x -= padding;
		x += rank1.getWidth(this);
		int y2 = y;
		for (int i = 0; i < 4; i++) {
			Image rank;
			if (i == 0) {
				rank = rank1;
			} else if (i == 1) {
				rank = rank2;
			} else if (i == 2) {
				rank = rank3;
			} else {
				rank = rank4;
			}
			x -= rank.getWidth(this);
			y += padding;
			y2 += padding;
			g.drawImage(rank, x, y, this);
			y += rank.getHeight(this);
			y2 += rank.getHeight(this) - 3 * (int) g.getFontMetrics().getStringBounds("Rankings:", g).getHeight() / 5;
			x += rank.getWidth(this);
			g.drawString(players.get(3 - i).toString(), x, y2);
			
			y2 += 3 * (int) g.getFontMetrics().getStringBounds("Rankings:", g).getHeight() / 5;
		}
	}
	
	private int itemDistanceFontSize(String string) {
		if (string.equals("You are very close to an item.")) {
			return 25;
		} else if (string.equals("You are close to an item.")) {
			return 20;
		} else {
			return 15;
		}
	}
	

	public void update() {
		if (Main.gameState == Main.STATE.Game) {
			if (Main.playState == Main.STATE.Play) {
				board.update();
			}
			if (Main.playState != Main.STATE.Pause) {
				board.getGame().updateCPUs();
			}
		}
		
		repaint();
		revalidate();
		//System.out.println(user.getX() + "  " + user.getY() + "   " + this.getX() + "  " + this.getY());
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void newBoard() {
		board = new Board();
	}
	
	public Image resizeImage(Image image, int width, int height) {
		return image.getScaledInstance(width, height, 0);
	}
	
	public Image resizeImage(Image image, int width, int height, int style) {
		return image.getScaledInstance(width, height, style);
	}

}
