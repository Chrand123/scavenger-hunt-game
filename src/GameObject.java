import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class GameObject extends JComponent {
	private Image image;
	private int x, y;
	private int height, width;
	
	public GameObject(String image, int x, int y) {
		this.image = new ImageIcon(image).getImage();
		this.x = x;
		this.y = y;
		this.height = this.image.getHeight(this);
		this.width = this.image.getWidth(this);
		this.y = y;
	}
	
	public GameObject(String image) {
		this.image = new ImageIcon(image).getImage();
		this.x = 0;
		this.y = 0;
		this.height = this.image.getHeight(this);
		this.width = this.image.getWidth(this);
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public void resizeImage(int width, int height) {
		this.width = width;
		this.height = height;
		image = image.getScaledInstance(this.width, this.height, 4);
	}
	
	public void resizeImage(double scale) {
		this.width = (int) (image.getWidth(this) * scale);
		this.height = (int) (image.getHeight(this) * scale);
		image = image.getScaledInstance(this.width, this.height, 0);
	}
}
