
public class CPU {
	private String name;
	private int points;
	private int itemsFound;
	private double pointsRatio;
	private int rank;
	
	public CPU(String name) {
		this.name = name;
		points = 0;
		itemsFound = 0;
		pointsRatio = 1;
		rank = 0;
	}
	
	public void setPointsRatio(double ratio) {
		pointsRatio = ratio;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void addPoints(int points) {
		this.points += (points * pointsRatio);
	}
	
	public void foundItem() {
		itemsFound++;
	}
	
	public int getItemsFound() {
		return itemsFound;
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
