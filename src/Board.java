import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

public class Board {
	private Rectangle board;
	private Rectangle playBounds;
	private int velX;
	private int velY;
	private Rectangle centerPoint;
	private Game game;
	private GameObject player;
	private List<GameObject> obstacles;
	private List<GameObject> items;
	private GameObject itemFound;
	
	public static double BOARD_SIZE = 9; //Small: 5, Medium: 9, Large: 13
	public final int BOARD_HEIGHT = (int) (BOARD_SIZE * Main.HEIGHT);
	public final int BOARD_WIDTH = (int) (BOARD_SIZE * Main.WIDTH);
	
	public final int TOTAL_ITEMS = (int) (1.5 * (BOARD_SIZE - 1) + 2.9);
	public final int TOTAL_OBSTACLES = (int) (5 * (Math.pow(BOARD_SIZE - 1, 2)));

	public Board() {
		board = new Rectangle(-BOARD_WIDTH/2 + Main.WIDTH / 2, -BOARD_HEIGHT/2 + Main.HEIGHT / 2, BOARD_WIDTH, BOARD_HEIGHT);
		velX = 0;
		velY = 0; 
		centerPoint = new Rectangle(board.x + board.width / 2 - 2, board.y + board.height / 2 - 2, 4, 4);
		game = new Game(this);
		player = new Player("smiley_face.png");
		player.resizeImage(50, 50);
		player.setLocation(board.x + board.width / 2 - player.getWidth() / 2, board.y + board.height / 2 - player.getHeight() / 2);
		//playBounds = new Rectangle(-BOARD_WIDTH/2 + Main.WIDTH - player.getWidth() / 2, -BOARD_HEIGHT/2 + Main.HEIGHT - player.getHeight() / 2, BOARD_WIDTH - Main.WIDTH + 5 * player.getWidth() / 4 + 3, BOARD_HEIGHT - Main.HEIGHT + 7 * player.getHeight() / 4 + 3);
		playBounds = new Rectangle(-BOARD_WIDTH/2 + Main.WIDTH - player.getWidth() / 2, -BOARD_HEIGHT/2 + Main.HEIGHT - player.getHeight() / 2, BOARD_WIDTH - Main.WIDTH, BOARD_HEIGHT - Main.HEIGHT);
		obstacles = new ArrayList<GameObject>();
		createAndScatterObstacles(TOTAL_OBSTACLES); //make it a list of images for more than 1 type of obstacle (rock)
		items = new ArrayList<GameObject>();
		createAndScatterItems("mystery_pouch.png", TOTAL_ITEMS);
		itemFound = null;
	}
	
	public void update() {
		if (clamp()) {
			return;
		}
		if (willHitObstacle()) {
			return;
		}
		
		board.setLocation(board.x - this.velX, board.y - this.velY);
		playBounds.setLocation(playBounds.x - this.velX, playBounds.y - this.velY);
		centerPoint.setLocation(board.x + board.width / 2 - 2, board.y + board.height / 2 - 2);
		for (GameObject obstacle : obstacles) {
			obstacle.setLocation(obstacle.getX() - this.velX, obstacle.getY() - this.velY);
			((Obstacle) obstacle).setBlockedBounds();
		}
		
		for (GameObject item : items) {
			item.setLocation(item.getX() - this.velX, item.getY() - this.velY);
		}
		itemFound = findsItem();
	}
	
	public boolean clamp() {
		if (velX <= board.getX() - 1 || velY <= board.getY() - 1 ) {
			return true;
		}
		if (velX + Main.WIDTH >= board.getX() + board.getWidth() - player.getWidth() + 5 || velY + Main.HEIGHT >= board.getY() + board.getHeight() - player.getHeight() + 5) {
			return true;
		}
		return false;
	}
	
	public boolean willHitObstacle() {
		Rectangle instBounds = new Rectangle(player.getX() + this.velX, player.getY() + this.velY, player.getHeight(), player.getWidth());
		for (GameObject obstacle : obstacles) {
			if (instBounds.intersects(((Obstacle) obstacle).getBlockedBounds())) {
				return true;
			}
		}
		return false;
	}
	
	public GameObject getItemFound() {
		return this.itemFound;
	}
	
	public void resetItemFound() {
		this.itemFound = null;
	}
	
	public GameObject findsItem() {
		for (int i = 0; i < items.size(); i++) {
			if (this.player.getBounds().intersects(items.get(i).getBounds())) {
				//System.out.println("Item Removed!");
				this.getPlayer().addItem((Item) items.get(i));
				game.getPoints();
				return items.remove(i);
			}
		}
		return null;
	}
	
	public Rectangle getBoardBounds() {
		return board;
	}
	
	public Rectangle getPlayBounds() {
		return playBounds;
	}
	
	public Rectangle getCenterPoint() {
		return centerPoint;
	}
	
	public int getItemsLeft() {
		int itemsLeft = 0;
		return this.items.size();
	}

	
	public int getVelX() {
		return velX;
	}
	
	public int getVelY() {
		return velY;
	}
	
	
	public void setVelX(int velX) {
		this.velX = velX;
	}
	
	public void setVelY(int velY) {
		this.velY = velY;
	}
	
	public List<GameObject> getItems() {
		return this.items;
	}
	
	public Player getPlayer() {
		return (Player) this.player;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void newGame() {
		this.game = new Game(this);
	}
	
	public List<GameObject> getObstacles() {
		return this.obstacles;
	}
	
	private void createAndScatterItems(String image, int amount) {
		for (int i = 0; i < amount; i++) {
			items.add(new Item(image));
			items.get(i).resizeImage(64, 64);

//			System.out.println(items);
			int randX = (int) (Math.random() * (playBounds.width - items.get(i).getWidth()) + playBounds.x);
			int randY = (int) (Math.random() * (playBounds.height - items.get(i).getHeight()) + playBounds.y);
			items.get(i).setX(randX);
			items.get(i).setY(randY);
			while (overlapsItem(items.get(i))) {
//				System.out.println("Overlapped Item at i = " + i);
				randX = (int) (Math.random() * (playBounds.width  - items.get(i).getWidth()) + playBounds.x);
				randY = (int) (Math.random() * (playBounds.height - items.get(i).getHeight()) + playBounds.y);
				
				items.get(i).setX(randX);
				items.get(i).setY(randY);
			}
			game.assignItem((Item) items.get(i));
		}
	}
	
	public String itemDistanceString() {
		if (closestItemDistance() > 120) {
			return "You are not close to any items.";
		} else if (closestItemDistance() > 50) {
			return "You are close to an item.";
		} else {
			return "You are very close to an item.";
		}
	}
	
	public int closestItemDistance() {
		int minDistance = Integer.MAX_VALUE;
		for (GameObject item : items) {
			if (distance(player, item) < minDistance) {
				minDistance = distance(player, item);
			}
		}
		return minDistance / 10;
	}
	
	public int distance(GameObject obj1, GameObject obj2) {
		double x1 = obj1.getBounds().getCenterX();
		double y1 = obj1.getBounds().getCenterY();
		double x2 = obj2.getBounds().getCenterX();
		double y2 = obj2.getBounds().getCenterY(); 
		double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		return (int) distance;
	}
	
	private void createAndScatterObstacles(int amount) {
		String[] images = {"rock1.png", "rock2.png", "tree3.png"}; //grass?
		for (int i = 0; i < amount; i++) {
			String imageString = "";
			String imageID = "";
			double rand = Math.random();
			double shrinkFactor = 0.0;
			if (rand > 0.9) {
				imageString = images[0];
				imageID = "rock1";
				shrinkFactor = 0.4;
			} else if (rand > 0.85) {
				imageString = images[1];
				imageID = "rock2"; 
				shrinkFactor = 0.06;
			} else {
				imageString = images[2];
				imageID = "tree3";
				shrinkFactor = 0.2;
			}
			
			obstacles.add(new Obstacle(imageString, imageID));
			obstacles.get(i).resizeImage(shrinkFactor);
			
			int randX = (int) (Math.random() * (playBounds.width  - obstacles.get(i).getWidth()) + playBounds.x);
			int randY = (int) (Math.random() * (playBounds.height - obstacles.get(i).getHeight()) + playBounds.y);
			
			obstacles.get(i).setX(randX);
			obstacles.get(i).setY(randY);
			
			while (overlapsObstacle(obstacles.get(i))) {
//				System.out.println("Overlapped at i = " + i);
				randX = (int) (Math.random() * (playBounds.width  - obstacles.get(i).getWidth()) + playBounds.x);
				randY = (int) (Math.random() * (playBounds.height - obstacles.get(i).getHeight()) + playBounds.y);
				
				obstacles.get(i).setX(randX);
				obstacles.get(i).setY(randY);
			}
			((Obstacle) obstacles.get(i)).setBlockedBounds();
		}
	}
	
	private boolean overlapsObstacle(GameObject object) {
		for (int i = 0; i < obstacles.size() - 1; i++) {
			if (obstacles.get(i).getBounds().intersects(object.getBounds())) {
				return true;
			}
		}
		if (player.getBounds().intersects(object.getBounds())) {
			return true;
		}
		return false;
	}
	
	private boolean overlapsItem(GameObject object) {
		for (int i = 0; i < items.size() - 1; i++) {
			Rectangle boundsRange = getBoundsRange(items.get(i).getBounds(), 200);
			if (boundsRange.intersects(object.getBounds())) {
				return true;
			}
		}
		//added
		for (GameObject obstacle : obstacles) {
			if (((Obstacle) obstacle).getBlockedBounds().intersects(object.getBounds())) {
				return true;
			}
		}
		if (getBoundsRange(player.getBounds(), 100).intersects(object.getBounds())) {
			return true;
		}
		return false;
	}
	
	private Rectangle getBoundsRange(Rectangle bounds, int extraRange) {
		return (new Rectangle((int) bounds.getX() - extraRange,(int) bounds.getY() - extraRange, (int)bounds.getWidth() + 2 * extraRange, (int)bounds.getHeight() + 2 * extraRange));
	}
}
