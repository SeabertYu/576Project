import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.json.JSONArray;
import org.json.JSONObject;

public class VideoCollageSeeker {

	private JFrame frame;

	private ArrayList<ImageIcon> video;

	private String videoFileName;

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
	
	public VideoCollageSeeker(ArrayList<ImageIcon> icons, String string) {
		this.video = icons;
		this.videoFileName = string;
	}

	public void show() {
		frame = new JFrame("frame seeker");
		JPanel panel = new JPanel();
		frame.setBounds(100, 468, 755, 360);
		frame.getContentPane().add(panel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		panel.add(scrollPane);
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		scrollPane.setViewportView(layeredPane);
		JLayeredPane list = new JLayeredPane();
		list.setLayout(new GridLayout(0, keyFrameMap.get(this.videoFileName)
				.size(), 0, 0));
		layeredPane.add(list);

		List<Integer> kframeIndex = keyFrameMap.get(this.videoFileName);
		for (Integer index : kframeIndex) {
			list.add(new JLabel(this.video.get(index)));
		}
		frame.validate();
		frame.setVisible(true);
	}

	public void close() {
		frame.dispose();
	}
	
	public static void main(String[] args) {
		ArrayList<ImageIcon> icons = ImageReader.readVideo(
				"./dataset/video01.rgb", MyApplication.IMAGE_WIDTH,
				MyApplication.IMAGE_HEIGHT);
		new VideoCollageSeeker(icons, "./dataset/video01.rgb").show();
	}

}
