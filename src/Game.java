import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
	private Board board;
	private Timer timerToStart;
	private Timer answerInputTimer;
	private String answerInputString;
	private boolean inputEnded;
	private String timeLeft;
	private int playerRank;
	
	public static int GAME_LENGTH = 5; //minutes: Short = 2, Medium = 5, Long = 10
	private int minutes;
	private int seconds;
	
	private List<String> itemBank;
	private List<Clue> clueBank;
	
	private int points;
	private String itemGuess;
	
	private boolean countdownEnded;
	
	public static int DIFFICULTY = 2; //"easy", "normal", "hard"
	private List<CPU> cpuPlayers;
	
	String[] nameArray = {"Daniel", "Mary", "Chris", "Andrew", "Jo", "Joy", "Rachel", "Andy", 
			"Jemi", "Jane", "Benn", "Jeffrey", "Candrea", "Audrea", "Abishek", "Terlin", "Rayan",
			"George", "Michael", "Mr. Kinney"};

	public Game(Board board) {
		//initializes length of game in min and sec
		minutes = GAME_LENGTH;
		seconds = 1;
		
		points = 0;
		itemGuess = "";
		
		playerRank = 0;
		inputEnded = true;
		answerInputString = "";
		
		this.board = board;
		cpuPlayers = addCPUs(3);
		itemBank = new ArrayList<String>();
		for (String item : ITEM_BANK) {
			itemBank.add(item);
		}
		clueBank = new ArrayList<Clue>();
		for (String[] clueString : CLUES) {
			Clue clue = new Clue(clueString[0], clueString[1]);
			clueBank.add(clue);
		}
		
		timerToStart = new Timer();
		answerInputTimer = new Timer();
		timeLeft = "Time Left";
		countdownEnded = false;
		
		
	}
	
	public void pauseGame() {
		timerToStart.cancel(); 
	}
	
	public void resumeGame() {
		timerToStart = new Timer();
		scheduleTimer();
	}
	
	public boolean solvedClue() {
		inputEnded = false;
		answerInputTimer = new Timer();
		answerInputTimer.schedule(new InputTimerTask(), 0, 100);
		if (itemGuess.toLowerCase().equals(((Item) board.getItemFound()).getClue().getAnswer().toLowerCase())) {
			points += ((Item) board.getItemFound()).getPoints();
			answerInputString = "Correct!";
			return true;
		}
		answerInputString = "Incorrect";
		return false;
	}
	
	public void resetItemGuess() {
		itemGuess = "";
	}
	
	public String getItemGuess() {
		return itemGuess;
	}
	
	public void addChar(char letter) {
		if (this.itemGuess.length() < ((Item) this.board.getItemFound()).getClue().getAnswer().length()) {
			this.itemGuess += letter;
		}
		this.itemGuess = this.itemGuess.toLowerCase();
	}
	
	public void removeChar() {
		if (this.itemGuess.length() > 0)
			this.itemGuess = this.itemGuess.substring(0, this.itemGuess.length() - 1);
	}
	
	public void setPlayerRank(int rank) {
		this.playerRank = rank;
	}
	
	public int getPlayerRank() {
		return playerRank;
	}
	
	public void updateCPUs() {
		for (CPU cpu : cpuPlayers) {
			if (Math.random() < 0.0005 / (Board.BOARD_SIZE / 6) * DIFFICULTY && cpu.getItemsFound() < board.TOTAL_ITEMS) {
				int points = (int) (Math.random() * DIFFICULTY * cpu.getItemsFound()) + DIFFICULTY;
				cpu.addPoints(points);
				cpu.foundItem();
				if (points < 2) {
					cpu.setPointsRatio(4);
				} else if (points < 4) {
					cpu.setPointsRatio(2);
				} else if (points < 8) {
					cpu.setPointsRatio(1);
				} else if (points < 12) {
					cpu.setPointsRatio(0.5);
				} else {
					cpu.setPointsRatio(0.25);
				}
//				System.out.println(cpu);
			}
		}
	
	}
	
	private List<CPU> addCPUs(int amount) {
		List<CPU> cpuList = new ArrayList<CPU>();
		List<String> cpuNames = new ArrayList<String>();
		for (String name : nameArray) {
			cpuNames.add(name);
		} 
		for (int i = 0; i < amount; i++) {
			int randIndex = (int) (Math.random() * cpuNames.size());
			cpuList.add(new CPU(cpuNames.remove(randIndex)));
		}
		return cpuList;
	}
	
	public List<CPU> getCPUs() {
		return cpuPlayers;
	}
	
	public String getTime() {
		return this.timeLeft;
	}
	
	public int getSeconds() {
		return minutes * 60 + seconds;
	}
	
	public void scheduleTimer() {
		timerToStart.schedule(new CountdownTask(), 0, 1000);
	}
	
	public int getPoints() {
//		int points = 0;
//		List<GameObject> itemsCollected = board.getPlayer().getItemsCollected();
//		for (GameObject item : itemsCollected) {
//			points += ((Item) item).getPoints();
//		}
		return points;
	}
	
	public String playerString() {
		return "You";
	}
	
	class CountdownTask extends TimerTask {
		public void run() {
			if (seconds > 0) {
				seconds--;
			} else {
				minutes--;
				seconds = 59;
			}
			timeLeft = minutes + ":";
			if (seconds < 10) {
				timeLeft += "0";
			}
			timeLeft += seconds;
			
			
			if (minutes == 0 && seconds == 0) {
				timeLeft = "Time's Up!";
				timerToStart.cancel();
				countdownEnded = true;
			}	
			//System.out.println(timeLeft);
		}
	}
	
	class InputTimerTask extends TimerTask {
		int sec = 20;
		public void run() {
			sec--;
			//System.out.println("SEC: " + sec);
			if (sec == 0 || board.getItemFound() != null && sec < 18) {
				answerInputTimer.cancel();
				inputEnded = true;
				answerInputTimer = new Timer();
				//System.out.println("ENDED");
			}	
			//System.out.println(timeLeft);
		}
	}
	
	public Timer inputTimer() {
		return answerInputTimer;
	}
	
	public String getInputString() {
		return answerInputString;
	}
	
	public boolean inputEnded() {
		return inputEnded;
	}
	
	public boolean countdownEnded() {
		return countdownEnded;
	}
	
	//Item Bank: (greater index --> more points)
	public static final String[] ITEM_BANK = {
		//Common
	    "acorn", "leaf", "twig", "shoe", "pencil", "pen", "biscuits",
	    "rubber band", "tennis ball", "soccer ball", "basketball", "button",
	    
	    //Uncommon
	    "cup", "napkin", "book", "phone", "map", "blanket", "smooth stone", 
	    "shiny stone", "red stone", "green stone", "blue stone", "yellow stone",
	    
	    //Rare
	    "coin", "flower", "fork", "calculator",  "ruler", "flashlight", "compass", "drum",
	    "silver stone", "golden stone",
	    
	    //Legendary
	    "jar of honey", "yellow rose", "four-leaf clover", "golden acorn",
	    "silver spoon", "golden egg", "gold ring"
	};
	
	//Word Chains are x1 points, "Riddle Me This" are x2 points, another could be x3 and so on
	private static String wordChainClue(String wordChain) {
		String[] formats = {
				"These three words are related to me: ",
				"I am often associated with these three words: ",
				"I belong with this word chain: ",
				"I am related to this set of words: "};
		int random = (int) (Math.random() * formats.length);
		return formats[random] + wordChain + ". What am I?";
	}
	
//	private static String riddleClue(String riddle) {
//		return "Riddle Me This:";
//	}
	  
	public static final String[][] CLUES = {
	    {wordChainClue("apple, mango, kiwi"), "fruit"},
	    {wordChainClue("orange, yellow, purple"), "color"},
	    {wordChainClue("sky, ocean, sapphire"), "blue"},
	    {wordChainClue("calculator, protractor, numbers"), "math"},
	    {wordChainClue("eraser, wood, point"), "pencil"},
	    {wordChainClue("page, chapter, cover"), "book"},
	    {wordChainClue("rat, mouse, Pikachu"), "rodent"},
	    {wordChainClue("chicken, dog, horse"), "animal"},
	    {wordChainClue("Nevada, Maine, Kansas"), "state"},
	    {wordChainClue("El Paso, Dallas, State"), "Texas"},
	    {wordChainClue("gold, west, 1848"), "California"},
	    {wordChainClue("monitor, keyboard, mouse"), "computer"},
	    {wordChainClue("barn, cow, crops"), "farm"},
	    {wordChainClue("turkey, Pilgrims, Thursday"), "Thanksgiving"},
	    {wordChainClue("fins, sea, scales"), "fish"},
	    {wordChainClue("golf, soccer, basketball"), "sport"},
	    {wordChainClue("H2O, slice, sweet"), "watermelon"},
	    {wordChainClue("call, text, apps"), "phone"},
	    {wordChainClue("wheel, seats, road"), "car"},
	    {wordChainClue("seats, sky, wing"), "plane"},
	    {wordChainClue("flipper, Antarctica, bird"), "penguin"},
	    {wordChainClue("case, court, attorney"), "lawyer"},
	    {wordChainClue("waist, tie, pants"), "belt"},
	    {wordChainClue("sky, twinkle, bright"), "star"},
	    {wordChainClue("cut, tool, sharp"), "knife"},
	    {wordChainClue("bird, colorful, Batman"), "robin"},
	    {wordChainClue("triangle, ocean, disappearance"), "bermuda"},
	    {wordChainClue("bee, leader, chess"), "queen"},
	    {wordChainClue("tree, coating, dog"), "bark"},
	    {wordChainClue("robbery, money, vault"), "bank"},
	    {wordChainClue("straw, blue, cran"), "berry"},
	    {wordChainClue("month, walk, Patrick"), "March"},
	    {wordChainClue("J, name, cards"), "Jack"},
	    {wordChainClue("hat, bottle, captain"), "cap"},
	    {wordChainClue("extinct, big, lizard"), "dinosaur"},
	    {wordChainClue("-guard, -span, -time"), "life"},
	    {wordChainClue("thorn, smell, red"), "rose"},
	    {wordChainClue("almond, pistachio, cashew"), "nut"},
	    {wordChainClue("leg, cow, half"), "calf"},
	    {wordChainClue("carrot, hop, ears"), "bunny"},
	    {wordChainClue("ring, taco-, -pepper"), "bell"},
	    {wordChainClue("Egypt, big, shape"), "pyramid"},
	    {wordChainClue("English, Spanish, French"), "language"},
	    {wordChainClue("hour, minute, hands"), "clock"},
	    {wordChainClue("summer, fireworks, 4th"), "July"},
	    {wordChainClue("month, 25th, 31st"), "December"},
	    {wordChainClue("pawns, king, board"), "chess"},
	    {wordChainClue("charge, volts, energy"), "battery"},
	    {wordChainClue("cows, dairy, white"), "milk"},
	    {wordChainClue("oak, palm, pine"), "tree"}


	};
	
	
	public static final int SCALE = 12; //scales the points
	public static final int EXTRA_POINTS = 2; //maximum extra points possible
	  
	
	public int assignPoints(int index) {
		  double indexRatio = (double) (index + 1) / ITEM_BANK.length; //range: (0, 1]
		  int offset = (int) (Math.random() * (EXTRA_POINTS + 1)); //random value for extra points
		  int points = (int) Math.round(indexRatio * (double) SCALE) + offset; //formula for points
		  if (points < 1) {
			  return 1;
		  }
		  return points;
	  }
	
	public void assignItem(Item item) {
		int random = (int) (Math.random() * this.itemBank.size());
	    item.setName(this.itemBank.remove(random));
		item.setPoints(assignPoints(random));
		int random2 = (int) (Math.random() * this.clueBank.size());
		item.setClue(this.clueBank.remove(random2));
	}
	
}
