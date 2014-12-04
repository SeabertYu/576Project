import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class ClusterFactory {
	public static final String MATCHING = "cluster/matching.json";

	public static final String KEY_SIFT_1 = "SIFT1";

	public static final String KEY_SIFT_2 = "SIFT2";

	public static final String KEY_MATCHING = "matched";

	public static final int KEY_IMAGE_THRESHOLD = 5;

	public static final class Matching {
		private int sift1;
		private int sift2;
		private int matched;

		public Matching(int sift1, int sift2, int matched) {
			super();
			this.sift1 = sift1;
			this.sift2 = sift2;
			this.matched = matched;
		}

		public int getSift1() {
			return sift1;
		}

		public int getSift2() {
			return sift2;
		}

		public int getMatched() {
			return matched;
		}

		@Override
		public String toString() {
			return "Matching [sift1=" + sift1 + ", sift2=" + sift2
					+ ", matched=" + matched + "]";
		}

	}

	/**
	 * 
	 * @return the SIFT matching matrix 
	 */
	public static Map<String, Map<String, Matching>> getMatching() {
		String jsonString = MyApplication.getJSON(MATCHING);
		Map<String, Map<String, Matching>> matching = new HashMap<String, Map<String, Matching>>();
		JSONObject object = new JSONObject(jsonString);
		Iterator keys = object.keys();
		while (keys.hasNext()) {
			String img1 = keys.next().toString();
			JSONObject value = object.getJSONObject(img1);
			Iterator valueKeys = value.keys();
			Map<String, Matching> mm = new HashMap<String, Matching>();
			while (valueKeys.hasNext()) {
				String img2 = valueKeys.next().toString();
				JSONObject m = value.getJSONObject(img2);
				int sift1 = m.getInt(KEY_SIFT_1);
				int sift2 = m.getInt(KEY_SIFT_2);
				int mat = m.getInt(KEY_MATCHING);
				Matching matchingBean = new Matching(sift1, sift2, mat);
				mm.put(MyApplication.FOLDER
						+ MyApplication.IMAGE_FILE
						+ String.format(MyApplication.IMAGE_NUM,
								Integer.parseInt(img2) + 1) + MyApplication.EXT,
						matchingBean);
			}
			matching.put(
					MyApplication.FOLDER
							+ MyApplication.IMAGE_FILE
							+ String.format(MyApplication.IMAGE_NUM,
									Integer.parseInt(img1) + 1)
							+ MyApplication.EXT, mm);
		}
		return matching;
	}

	/**
	 * select key images for each cluster in given cluster of images.  
	 * 
	 * @param cluster2Img
	 * @param matching
	 * @return key images associated with each given cluster
	 */
	public static Map<String, List<String>> selectKeyImages(
			Map<String, ArrayList<String>> cluster2Img,
			Map<String, Map<String, Matching>> matching) {
		Set<String> clusters = cluster2Img.keySet();
		Map<String, List<String>> keyImageCluster = new HashMap<String, List<String>>();
		for (String cluster : clusters) {
			List<String> imgs = cluster2Img.get(cluster);
			keyImageCluster.put(cluster, keyImages(imgs, matching));
		}
		return keyImageCluster;
	}

	private static List<String> keyImages(List<String> imgs,
			Map<String, Map<String, Matching>> matching) {
		List<String> result = new ArrayList<String>();
		int index = 0;
		result.add(imgs.get(index));
		while (index < imgs.size()) {
			int next = index + 1;
			if (next == imgs.size() - 1) {
				Matching mat = matching.get(imgs.get(index)).get(imgs.get(next));
				if (mat.matched < KEY_IMAGE_THRESHOLD) {
					result.add(imgs.get(next));
				}
				break;
			}
			while (next < imgs.size()) {
				Matching mat = matching.get(imgs.get(index)).get(imgs.get(next));
				if (mat.matched > KEY_IMAGE_THRESHOLD) {
					next++;
				} else {
					break;
				}
			}
			if (next >= imgs.size()) {
				break;
			} else {
				index = next;
				result.add(imgs.get(index));
			}
		}
		return result;
	}

	public static void main(String[] args) {
		// get the matching matrix
		Map<String, Map<String, Matching>> map = getMatching();
		// get the image cluster
		HashMap<String, ArrayList<String>> cluster = new HashMap<String, ArrayList<String>>();
		String imageJSON = MyApplication.getJSON(MyApplication.IMAGE_CLUSTER);
		MyApplication.json2Map(imageJSON, cluster, MyApplication.IMAGE_FILE,
				MyApplication.IMAGE_NUM);
		// select key images for each cluster
		Map<String, List<String>> key = selectKeyImages(cluster, map);
		for (String k : key.keySet()) {
			if (cluster.get(k).size() == key.get(k).size()) {
				System.out.print(k+"## ");
				for (String v : cluster.get(k)) {
					System.out.print(v + ",,,,");
				}
				System.out.println();
			}
		}
	}
}
