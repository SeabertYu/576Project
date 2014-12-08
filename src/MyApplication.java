import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
	public static final String HEAD_CLUSTER_ID = "-2";

	private JFrame frmImageBrowser;
	private PreviewPanel previewPanel;
	private CollagePanel collagePanel;
	public static VideoCollageSeeker videoCollageSeeker;
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
		
		// remove head cluster
//		ArrayList<String> headCluster = map.remove(HEAD_CLUSTER_ID);
		//sort list		
		ArrayList<ArrayList<String>> list = sortMap(map);		
//		list = ClusterFactory.mergeCluster(list, 33);
		// add back in head cluster
//		if (headCluster != null) {
//			list.add(headCluster);
//		}
		sortList(list);
		final ArrayList<ArrayList<String>> finalList = new ArrayList<ArrayList<String>>(list);
		for(ArrayList<String> l:list){
			System.out.println(l.size());
		}
		VideoCollageSeeker.initialize();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				    }
//					UIManager.installLookAndFeel("SeaGlass", "com.seaglasslookandfeel.SeaGlassLookAndFeel");
//					UIManager.setLookAndFeel(new SeaGlassLookAndFeel());
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
		frmImageBrowser.setIconImage(Toolkit.getDefaultToolkit().getImage("./image/icon.png"));
		frmImageBrowser.setTitle("Image Browser");
		frmImageBrowser.setBounds(100, 100, 800, 380 * 2);
		frmImageBrowser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frmImageBrowser.getRootPane().putClientProperty(SeaGlassRootPaneUI.UNIFIED_TOOLBAR_LOOK, Boolean.TRUE);
		JMenuBar menuBar = new JMenuBar();
		frmImageBrowser.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		frmImageBrowser.getContentPane().setLayout(new GridLayout(2, 0, 5, 5));
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setLayout(new GridLayout(0, 2, 5, 5));
		
		previewPanel = new PreviewPanel();
		layeredPane.add(previewPanel);
		
		collagePanel = new CollagePanel(this.previewPanel.previewLabel, this.clusterlist);
		
		collagePanel.setAutoscrolls(true);
		layeredPane.add(collagePanel);
		frmImageBrowser.getContentPane().add(layeredPane);
		videoCollageSeeker = new VideoCollageSeeker();
		frmImageBrowser.getContentPane().add(videoCollageSeeker);
		videoCollageSeeker.setVisible(true);
	}
	
	public static ArrayList<ArrayList<String>> sortMap(HashMap<String, ArrayList<String>> map){
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			list.add(map.get(iterator.next()));
		}
		Collections.sort(list, new Comparator<ArrayList<String>>(){

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
	public static ArrayList<ArrayList<String>> sortList(ArrayList<ArrayList<String>> list){
		Collections.sort(list, new Comparator<ArrayList<String>>(){

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
	public static String getJSON(String filename){
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
	
	public static void json2Map(String jsonStr, HashMap<String, ArrayList<String>> map, String prefix, String numformat){
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
