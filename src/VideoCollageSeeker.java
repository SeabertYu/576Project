import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.json.JSONArray;
import org.json.JSONObject;

public class VideoCollageSeeker extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8718842547412643L;

	private static final Map<String, List<Integer>> keyFrameMap = new HashMap<String, List<Integer>>();

	public static void initialize() {
		
		if (keyFrameMap.size() > 0) {
			return;
		}
		String kframeFile = "cluster/kframes.json";
		String jsonString = MyApplication.getJSON(kframeFile);
		JSONObject object = new JSONObject(jsonString);
		Iterator iterator = object.keys();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			JSONArray array = object.getJSONArray(key);
			List<Integer> kframes = new ArrayList<Integer>();
			for (int i = 0; i < array.length(); i++) {
				kframes.add(array.getInt(i));
			}
			keyFrameMap.put(MyApplication.FOLDER
					+ MyApplication.VIDEO_FILE
					+ String.format(MyApplication.VIDEO_NUM,
							Integer.parseInt(key)) + MyApplication.EXT, kframes);
		}
	}
	
	
	public VideoCollageSeeker() {
		super();
		this.setBounds(0, 0, 400, 720);
		this.setVisible(false);
	}
	
	public void display(ArrayList<ImageIcon> videos, String videoFileName, VideoPlayer player) {
		this.removeAll();
		this.revalidate();
		this.repaint();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		add(scrollPane);
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		scrollPane.setViewportView(layeredPane);
		JLayeredPane list = new JLayeredPane();
		list.setLayout(new GridLayout(0, keyFrameMap.get(videoFileName)
				.size(), 0, 0));
		layeredPane.add(list);

		List<Integer> kframeIndex = keyFrameMap.get(videoFileName);
		for (Integer index : kframeIndex) {
			JLabel frameLabel = new JLabel(videos.get(index));
			frameLabel.addMouseListener(new FrameClickListener(player, index));
			list.add(frameLabel);
		}
		this.setVisible(true);
	}
	public void displayCollage(ArrayList<ArrayList<String>> collage){
		this.removeAll();
		this.revalidate();
		this.repaint();
		JLayeredPane layer = new JLayeredPane();
		layer.setLayout(new GridLayout(0, 8, 0, 0));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setViewportView(layer);
		add(scrollPane);
		
		for(String filename:collage.get(0)){
			System.out.println(filename);
			layer.add(new ImageLabel(filename));
		}
		this.revalidate();
		this.setVisible(true);
	}

	public void close() {
		this.removeAll();
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) {
		ArrayList<ImageIcon> icons = ImageReader.readVideo(
				"./dataset/video01.rgb", MyApplication.IMAGE_WIDTH,
				MyApplication.IMAGE_HEIGHT);
//		new VideoCollageSeeker().display(icons, "./dataset/video01.rgb");
	}

}
