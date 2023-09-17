import java.awt.Rectangle;

public class Obstacle extends GameObject {
	
	private Rectangle blockedBounds; //bounds that player cannot move through
	private final String ID;

	public Obstacle(String image, String id) {
		super(image);
		// TODO Auto-generated constructor stub
		blockedBounds = new Rectangle();
		ID = id;
	}
	
	public Rectangle getBlockedBounds() {
		return this.blockedBounds;
	}
	
	public void setBlockedBounds() {
		if (ID.equals("rock1")) {
			this.blockedBounds = new Rectangle(this.getX() + this.getWidth() / 18, this.getY() + 70 * this.getHeight() / 100, this.getWidth() - this.getWidth() / 9, 17 * this.getHeight() / 100);
		} else if (ID.equals("rock2")) {
			this.blockedBounds = new Rectangle(this.getX() + this.getWidth() / 13, this.getY() + 81 * this.getHeight() / 100, this.getWidth() - this.getWidth() / 5, 15 * this.getHeight() / 100);
		} else if (ID.equals("tree3")) {
			this.blockedBounds = new Rectangle(this.getX() + this.getWidth() / 3 + this.getWidth() / 36, this.getY() + 81 * this.getHeight() / 100, this.getWidth() / 4, 15 * this.getHeight() / 100);
		} //maybe grass next
	}
	
	public void setBlockedBounds(Rectangle bounds) {
		this.blockedBounds = bounds;
	}
	
	public void setBlockedBounds(int x, int y, int width, int height) {
		this.blockedBounds = new Rectangle(x, y, width, height);
	}
	
}
