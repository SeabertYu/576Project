import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;


public class LRU {
	LinkedHashMap<String, ArrayList<ImageIcon>> cache;
	int size = SIZE;
	public static final int SIZE = 10;
	
	public LRU(int size){
		this.size = size;
		cache = new LinkedHashMap<String, ArrayList<ImageIcon>>(){
			@Override
			protected boolean removeEldestEntry (Map.Entry<String,ArrayList<ImageIcon>> eldest) {
		         return size() > LRU.this.size;
			}
		};
	}
	
	public synchronized void put(String key, ArrayList<ImageIcon> value){
		cache.put(key, value);
			
	}
	
	public synchronized ArrayList<ImageIcon> get(String key){
		return cache.get(key);
	}
	public synchronized int size(){
		return cache.size();
	}
	public synchronized boolean containsKey(String key){
		return cache.containsKey(key);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LRU lru = new LRU(3);
		lru.put("a", new ArrayList<ImageIcon>());
		lru.put("b", new ArrayList<ImageIcon>());
		lru.put("c", new ArrayList<ImageIcon>());
		lru.get("a");
		lru.put("d", new ArrayList<ImageIcon>());
		System.out.println(lru);
	}

}
