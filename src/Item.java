import java.util.ArrayList;
import java.util.List;

public class Item extends GameObject {
	private String name;
	private int points;
	private Clue clue;

	public Item(String image) {
		super(image);
		// TODO Auto-generated constructor stub
		this.points = 0;
		this.clue = new Clue();
		this.name = "";
	}
	
	public void setClue(Clue clue) {
		this.clue = clue;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Clue getClue() {
		return this.clue;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public String toString() {
		return this.name;
	}



}
