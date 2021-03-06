package test.project1;

public class Word {
	
	private String text;
	private int totalCharVal;
	
	public Word() {
	}
	
	public Word(String text) {
		this.text = text;
		this.setTotalCharVal();
	}
	
	public int getTotalCharVal() {
		return this.totalCharVal;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	private void setTotalCharVal() {
		int totalCharVal = 0;
		for(char letter : text.toCharArray()) {
			totalCharVal += letter - 96;
		}
		
		this.totalCharVal = totalCharVal;
	}
	
	@Override
	public String toString() {
		return "(" + this.getText() +", " + this.getTotalCharVal() + ")";
	}
}
