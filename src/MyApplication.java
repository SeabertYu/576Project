import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.json.JSONArray;
import org.json.JSONObject;


public class MyApplication {
	public static int IMAGE_HEIGHT = 288;
	public static int IMAGE_WIDTH = 352;
	public static final String IMAGE_CLUSTER = "./cluster/image_cluster.json";
	public static final String VIDEO_CLUSTER = "./cluster/video_cluster.json";
	public static final String IMAGE_FILE = "image";
	public static final String VIDEO_FILE = "video";
	public static final String IMAGE_NUM = "%03d";
	public static final String VIDEO_NUM = "%02d";
	public static final String EXT = ".rgb";
	public static final String FOLDER = "./dataset/";

	private JFrame frmImageBrowser;
	private PreviewPanel previewPanel;
	private CollagePanel collagePanel;
	private ArrayList< ArrayList<String>> clusterlist;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String imageJSON = getJSON(IMAGE_CLUSTER);
		String videoJSON = getJSON(VIDEO_CLUSTER);
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		json2Map(imageJSON, map, IMAGE_FILE, IMAGE_NUM);
		json2Map(videoJSON, map, VIDEO_FILE, VIDEO_NUM);
		
		//sort list
		ArrayList<ArrayList<String>> list = sortMap(map);		
		final ArrayList<ArrayList<String>> finalList = new ArrayList<ArrayList<String>>(list);
		for(ArrayList<String> l:list){
			System.out.println(l.size());
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyApplication window = new MyApplication(finalList);
					window.frmImageBrowser.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MyApplication(ArrayList<ArrayList<String>> lists) {
		this.clusterlist = lists;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmImageBrowser = new JFrame();
		frmImageBrowser.setTitle("Image Browser");
		frmImageBrowser.setBounds(100, 100, 755, 368);
		frmImageBrowser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmImageBrowser.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		frmImageBrowser.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));
		
		previewPanel = new PreviewPanel();
		frmImageBrowser.getContentPane().add(previewPanel);
		
		collagePanel = new CollagePanel(this.previewPanel.previewLabel, this.clusterlist);
		
		collagePanel.setAutoscrolls(true);
		frmImageBrowser.getContentPane().add(collagePanel);
	}
	
	private static ArrayList<ArrayList<String>> sortMap(HashMap<String, ArrayList<String>> map){
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			list.add(map.get(iterator.next()));
		}
		Collections.sort(list, new Comparator<ArrayList<String>>(){

			@Override
			public int compare(ArrayList<String> arg0, ArrayList<String> arg1) {
				int sizeA =  arg0.size();
				int sizeB = arg1.size();
				if(sizeA<sizeB){
					return 1;
				}
				if(sizeA==sizeB){
					return 0;
				}
				return -1;
			}
			
		});
		return list;
	}
	private static String getJSON(String filename){
		String jsonStr = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while((line = reader.readLine()) != null){
				jsonStr+=line;
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	private static void json2Map(String jsonStr, HashMap<String, ArrayList<String>> map, String prefix, String numformat){
		JSONObject json = new JSONObject(jsonStr);
		Iterator<?> keys = json.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			JSONArray list = json.getJSONArray(key);
			ArrayList<String> filelist = new ArrayList<String>();
			if(map.containsKey(key)){
				filelist = map.get(key);
			}
			for(int i = 0; i<list.length(); i++){
				String filename = FOLDER+prefix+String.format(numformat, list.getInt(i)+1)+EXT;
				filelist.add(filename);
			}
			map.put(key, filelist);
			
		}
	}

}
