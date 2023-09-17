import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {
	public static final int HEIGHT = 500;
	public static final int WIDTH = (int) (1.5 * HEIGHT);
	 
	public enum STATE {
		Menu,
		Game,
		Rules,
		End,
		Item,
		Play,
		Settings,
		Pause
	};
	
	public static STATE gameState = STATE.Menu; //we can cast STATE as a type that holds the values above
	public static STATE playState = STATE.Play; //in the STATE.Game (Item and Play)

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("Andrew's Scavenger Hunt");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(WIDTH, HEIGHT);
		//Board board = new Board();
		
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
//		canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
//		canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		
		class TimerListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (gameState == STATE.Game) {
					if (canvas.getBoard().getGame().getSeconds() == 0 || canvas.getBoard().getItemsLeft() == 0 && canvas.getBoard().getItemFound() == null) {
						gameState = STATE.End;
						playState = STATE.Play;
						canvas.getBoard().resetItemFound();
					}
					if (canvas.getBoard().getItemFound() != null) {
						playState = STATE.Item;
					}
				}
				canvas.update();
			}
		}

		Timer t = new Timer(13, new TimerListener()); //17 for 60FPS
		t.start();
		
		//CORRECT/INCORRECT FEEDBACK!!!!!!

		class GameKeyListener implements KeyListener {
			
			public boolean releasedPlayKey = false;
			
			public void keyTyped(KeyEvent e) {
				int key = e.getKeyChar();
				if (playState == STATE.Item && releasedPlayKey) {
					if (key == 8) {
						canvas.getBoard().getGame().removeChar();
					} else if (key >= 65 && key <= 90 || key >= 97 && key <= 122) {
						canvas.getBoard().getGame().addChar(e.getKeyChar());
					}
					if (canvas.getBoard().getGame().getItemGuess().length() == ((Item) canvas.getBoard().getItemFound()).getClue().getAnswer().length()) {
						
						canvas.getBoard().getGame().solvedClue();
						canvas.getBoard().resetItemFound();
						canvas.getBoard().getGame().resetItemGuess();
						playState = STATE.Play;
					}
				}
				
			}
			
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (gameState == STATE.Game) {
					if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ESCAPE) && playState != STATE.Item) {
						playState = STATE.Pause;
						canvas.getBoard().getGame().pauseGame();
					}
					if (playState == STATE.Play) {
						int speed = 5;
						if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
							canvas.getBoard().setVelY(-speed);
						} else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
							canvas.getBoard().setVelX(-speed);
						} else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
							canvas.getBoard().setVelY(speed);
						} else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
							canvas.getBoard().setVelX(speed);
						} else if (key == KeyEvent.VK_P) {
							//TODO: Pause Option
							
						} else if (key == KeyEvent.VK_I) {
							//TODO: Inventory Option
							//shows items and points for each item
							//BONUS: you can only carry 10 items so if you get another than you can choose to replace it
							
						}
					} else if (playState == STATE.Item) {
						
					}
					
				}
				
			}
			
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (gameState == STATE.Game) {
					if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
						canvas.getBoard().setVelY(0);
					} else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
						canvas.getBoard().setVelX(0);
					}
					if (playState == STATE.Item) {
						releasedPlayKey = true;
					} else {
						releasedPlayKey = false;
					}
				}
				
			}
			
		}
		
		canvas.addKeyListener(new GameKeyListener());
		
		canvas.setFocusable(true);
		canvas.requestFocus();
		
		class GameMouseListener implements MouseListener {
			
			//this includes pressing and releasing the button
			public void mouseClicked(MouseEvent e) {}
			
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (gameState == STATE.Menu) {
					if (mouseOver(x, y, Canvas.playBtn)) {
						gameState = STATE.Game;
						canvas.newBoard();
						canvas.getBoard().getGame().scheduleTimer();
					} else if (mouseOver(x, y, Canvas.rulesBtn)) {
						gameState = STATE.Rules;
					} else if (mouseOver(x, y, Canvas.settingsBtn)) {
						gameState = STATE.Settings;
					}
				} else if (gameState == STATE.Game) {
					if (playState == STATE.Item) {
						if (mouseOver(x, y, Canvas.skipBtn)) {
							playState = STATE.Play;
							canvas.getBoard().resetItemFound();
							canvas.getBoard().getGame().resetItemGuess();
						}
					} else if (playState == STATE.Pause) {
						if (mouseOver(x, y, Canvas.resumeBtn)) {
							playState = STATE.Play;
							canvas.getBoard().getGame().resumeGame();
						} else if (mouseOver(x, y, Canvas.quitBtn)) {
							playState = STATE.Play;
							gameState = STATE.End;
						}
					}
				} else if (gameState == STATE.End) {
					if (mouseOver(x, y, Canvas.playAgainBtn)) {
						gameState = STATE.Game;
						canvas.newBoard();
						canvas.getBoard().getGame().scheduleTimer();
					} else if (mouseOver(x, y, Canvas.menuBtn)) {
						gameState = STATE.Menu;
					}
//					System.out.println("End");
				} else if (gameState == STATE.Rules) {
					if (mouseOver(x, y, Canvas.backBtn)) {
						gameState = STATE.Menu;	
					}
				} else if (gameState == STATE.Settings) {
					if (mouseOver(x, y, Canvas.backBtn)) {
						gameState = STATE.Menu;	
					} else {
						int[] gameLengths = {2, 5, 10};
						for (int i = 0; i < Canvas.gameLength_Btns.length; i++) {
							if (mouseOver(x, y, Canvas.gameLength_Btns[i])) {
								Canvas.activeBtns[0] = i;
								Game.GAME_LENGTH = gameLengths[i];
							}
						}
						int[] boardSizes = {5, 9, 13};
						for (int i = 0; i < Canvas.mapSize_Btns.length; i++) {
							if (mouseOver(x, y, Canvas.mapSize_Btns[i])) {
								Canvas.activeBtns[1] = i;
								Board.BOARD_SIZE = boardSizes[i];
							}
						}
						int[] cpuDifficulties = {1, 2, 3};
						for (int i = 0; i < Canvas.cpuDifficulty_Btns.length; i++) {
							if (mouseOver(x, y, Canvas.cpuDifficulty_Btns[i])) {
								Canvas.activeBtns[2] = i;
								Game.DIFFICULTY = cpuDifficulties[i];
							}
						}
					}
				}
			}
			
			public void mouseReleased(MouseEvent e) {}
			
			//this is triggered when the mouse enters the area. You could use this for when the mouse enters or leaves a canvas or button for roll over effects
			public void mouseEntered(MouseEvent e) {}
			
			public void mouseExited(MouseEvent e) {}
		}
		
		//add it to the canvas
		canvas.addMouseListener(new GameMouseListener());

		frame.add(canvas);
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
//		System.out.println("Done.");
	}
	
	private static boolean mouseOver(int mx, int my, Rectangle r) {
		if (mx > (int) r.getX() && mx < (int) (r.getX() + r.getWidth())) {
			if (my > (int) r.getY() && my < (int) (r.getY() + r.getHeight())) {
				return true;
			}
		}
		return false;
	}

}
