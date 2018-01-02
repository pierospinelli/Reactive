package it.pjsoft.reactive.core.shell;

import java.util.HashMap;
import java.util.Map;

public class TestRepository {
	private static Map<String, TestEntity> db = new HashMap<String, TestEntity>();
	private static TestEntity current = null;

	static {
		db.put("1", new TestEntity("1", "primo", 10));
		db.put("2", new TestEntity("2", "secondo", 20));
		db.put("3", new TestEntity("3", "terzo", 30));
		db.put("4", new TestEntity("4", "quarto", 40));
		db.put("5", new TestEntity("5", "quinto", 50));
		db.put("6", new TestEntity("6", "sesto", 60));
	}
	
	public static TestEntity getEntity(String id) {
		return db.get(id);
	}
	
	public static TestEntity setCurrentEntity(String id) {
		current = getEntity(id);
		return current;
	}
	
	public static TestEntity getCurrentEntity() {
		return current;
	}

}
