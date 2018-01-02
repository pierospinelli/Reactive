package it.pjsoft.reactive.core.shell;

public class TestEntity {
	private String id;
	
	private String name;
	
	private int score;

	public TestEntity(String id, String name, int score) {
		super();
		this.id = id;
		this.name = name;
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
	
	
}
