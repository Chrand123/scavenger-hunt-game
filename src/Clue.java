
public class Clue {
	private String clue;
	private String answer;
	
	public Clue(String clue, String answer) {
		this.clue = clue;
		this.answer = answer;
	}
	
	public Clue() {
		this.clue = "";
		this.answer = "";
	}
	
	public void setClue(String clue) {
		this.clue = clue;
		
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getClue() {
		return this.clue;
	}
	
	public String getAnswer() {
		return this.answer;
	}
	
	public String toString() {
		return this.clue;
	}

}
