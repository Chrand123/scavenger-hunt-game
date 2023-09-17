import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
	//private int velX, velY;
	private Game game;
	private List<GameObject> itemsCollected; //use this

	public Player(String image) {
		super(image);
		// TODO Auto-generated constructor stub
//		this.game = game;
		this.itemsCollected = new ArrayList<GameObject>();
	}
	
	public void update() {	
	}
	
	public void addItem(Item item) {
		itemsCollected.add(item);
	}
	
	public List<GameObject> getItemsCollected() {
		return itemsCollected;
	}
	
//	public GameObject findsItem(List<GameObject> items) {
//		for (int i = 0; i < items.size(); i++) {
//			if (this.getBounds().intersects(items.get(i).getBounds())) {
////				System.out.println("Item Removed!");
//				itemsCollected.add(items.get(i));
//				return items.remove(i);
//			}
//		}
//		return null;
//	}


}
