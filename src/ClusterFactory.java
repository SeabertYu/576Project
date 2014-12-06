import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class ClusterFactory {
	public static final String MATCHING = "cluster/matching.json";

	public static final String KEY_SIFT_1 = "SIFT1";

	public static final String KEY_SIFT_2 = "SIFT2";

	public static final String KEY_MATCHING = "matched";

	public static final int KEY_IMAGE_THRESHOLD = 5;

	private static Map<String, Map<String, Matching>> cachedMatchingMatrix = null;

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
		if (cachedMatchingMatrix != null) {
			return cachedMatchingMatrix;
		}
		String jsonString = MyApplication.getJSON(MATCHING);
		cachedMatchingMatrix = new HashMap<String, Map<String, Matching>>();
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
			cachedMatchingMatrix.put(
					MyApplication.FOLDER
							+ MyApplication.IMAGE_FILE
							+ String.format(MyApplication.IMAGE_NUM,
									Integer.parseInt(img1) + 1)
							+ MyApplication.EXT, mm);
		}
		return cachedMatchingMatrix;
	}

	/**
	 * select key images for each cluster in given cluster of images.
	 * 
	 * @param cluster2Img
	 * @param matching
	 * @return key images associated with each given cluster
	 */
	public static ArrayList<ArrayList<String>> selectKeyImages(
			ArrayList<ArrayList<String>> cluster2Img) {
		Map<String, Map<String, Matching>> matching = getMatching();
		ArrayList<ArrayList<String>> keyImageCluster = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> cluster : cluster2Img) {
			keyImageCluster.add(keyImages(cluster, matching));
		}
		return keyImageCluster;
	}

	private static ArrayList<String> keyImages(ArrayList<String> imgs,
			Map<String, Map<String, Matching>> matching) {
		ArrayList<String> result = new ArrayList<String>();
		int index = 0;
		result.add(imgs.get(index));
		while (index < imgs.size()) {
			int next = index + 1;
			if (next == imgs.size() - 1) {
				Matching mat = matching.get(imgs.get(index))
						.get(imgs.get(next));
				if (mat.matched < KEY_IMAGE_THRESHOLD) {
					result.add(imgs.get(next));
				}
				break;
			}
			while (next < imgs.size()) {
				Matching mat = matching.get(imgs.get(index))
						.get(imgs.get(next));
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

	/**
	 * merge the cluster so that the size of clusters will be equal to the given target cluster size. 
	 * 
	 * @param clusters
	 * @param targetClusterSize
	 * @return merged cluster
	 */
	public static ArrayList<ArrayList<String>> mergeCluster(ArrayList<ArrayList<String>> clusters,
			int targetClusterSize) {
		if (targetClusterSize == clusters.size()) {
			return clusters;
		}
		ArrayList<ArrayList<String>> mergedClusters = new ArrayList<ArrayList<String>>();
		if (targetClusterSize == 1) {
			for (ArrayList<String> cluster : clusters) {
				mergedClusters.add(cluster);
			}
			return mergedClusters;
		}
		
		Collections.sort(clusters, new Comparator<ArrayList<String>>() {

			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				return o1.size() - o2.size();
			}
		});
		
		// start merging clusters
		for (int i = 0; i < targetClusterSize; i++) {
			mergedClusters.add(clusters.get(i));
		}
		
		int decreasedSize = clusters.size() - targetClusterSize;
		int k = 1;
		while (k <= decreasedSize) {
			ArrayList<String> mergingCluster = clusters.get(clusters.size() - k);
			Map<Integer, Double> scores = new HashMap<Integer,Double>();
			for (int i = 0; i < targetClusterSize; i++) {
				scores.put(i, distance(clusters.get(i), mergingCluster));
			}
			// merge into the cluster with highest score
			double heighstScore = 0;
			int toIndex = -1;
			Set<Integer> candidates = scores.keySet();
			for (Integer cand : candidates) {
				if (scores.get(cand) > heighstScore) {
					heighstScore = scores.get(cand);
					toIndex = cand;
				}
			}
			mergedClusters.get(toIndex).addAll(mergingCluster);
			k++;
		}
		
		return mergedClusters;
	}

	private static double distance(ArrayList<String> cluster1, ArrayList<String> cluster2) {
		double score = 0.0;
		int videoCount1 = 0;
		int videoCount2 = 0;
		videoCount1 = getNumberOfVideo(cluster1);
		videoCount2 = getNumberOfVideo(cluster2);
		Map<String, Map<String, Matching>> matching = getMatching();
		for (String img1 : cluster1) {
			if (img1.contains(MyApplication.VIDEO_FILE)) {
				continue;
			}
			for (String img2 : cluster2) {
				if (img2.contains(MyApplication.VIDEO_FILE)) {
					continue;
				}
				if (matching.get(img1).containsKey(img2)) {
					score += matching.get(img1).get(img2).matched;
				} else {
					score += matching.get(img2).get(img1).matched;
				}
			}
		}
		return score / ((cluster1.size() - videoCount1) * (cluster2.size() - videoCount2));
	}

	private static int getNumberOfVideo(ArrayList<String> cluster) {
		int video = 0;
		for (String img1 : cluster) {
			if (img1.contains(MyApplication.VIDEO_FILE)) {
				video++;
			}
		}
		return video;
	}
	
	public static void main(String[] args) {
		testSelectKeyImage();
	}

	private static void testMerge() {
		String imageJSON = MyApplication.getJSON(MyApplication.IMAGE_CLUSTER);
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		MyApplication.json2Map(imageJSON, map, MyApplication.IMAGE_FILE, MyApplication.IMAGE_NUM);
		//sort list
		ArrayList<ArrayList<String>> list = MyApplication.sortMap(map);
		
		ArrayList<ArrayList<String>> mergerdCluster = mergeCluster(list, 20);
		System.out.println(mergerdCluster.size());
	}

	private static void testSelectKeyImage() {
		// get the image cluster
		HashMap<String, ArrayList<String>> cluster = new HashMap<String, ArrayList<String>>();
		String imageJSON = MyApplication.getJSON(MyApplication.IMAGE_CLUSTER);
		MyApplication.json2Map(imageJSON, cluster, MyApplication.IMAGE_FILE,
				MyApplication.IMAGE_NUM);
		// select key images for each cluster
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		for (String key : cluster.keySet()) {
			list.add(cluster.get(key));
		}
		ArrayList<ArrayList<String>> result = selectKeyImages(list);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("original cluster size: " + list.get(i).size() + " key cluster size:" + result.get(i).size());
		}
	}
}
