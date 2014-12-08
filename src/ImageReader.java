import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ImageReader {
	public static final int WIDTH = MyApplication.IMAGE_WIDTH;
	public static final int HEIGHT = MyApplication.IMAGE_HEIGHT;
	public static final String VIDEO_LABEL_PLAY = "./image/play.png";
	public static final String VIDEO_LABEL_PAUSE = "./image/pause.png";
	public static final float FULL_SIZE = 1.0f;
	private static final Color DEFAULT_COLOR = new Color(214, 217, 223);
	
	static int SIZE = HEIGHT * WIDTH;
	private static HashMap<String, Image> videoLabel = new HashMap<String, Image>();
	

	
	
	public static synchronized void displayImage(ImageIcon image, JLabel frame){
		JFrame window = new JFrame();
		if(frame == null){
			frame = new JLabel(image);
			JLabel title = new JLabel("<html><h2>Initializing...</h2></html>",SwingConstants.CENTER);
			window.getContentPane().add(frame, BorderLayout.CENTER);
			window.getContentPane().add(title,BorderLayout.NORTH);
			window.pack();
			window.setVisible(true);
			window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		frame.setIcon(image);
	}
	
	public static BufferedImage makeTransparent(Image image, float alpha){
		BufferedImage result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = result.createGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2.setComposite(ac);
		g2.drawImage(image, 0, 0, null);
		return result;
	}
	
	public static BufferedImage addVideoLabel(Image image, String label, int offsetX, int offsetY, float factor){
		Image videoLabelImage = null;
		if(!videoLabel.containsKey(label)){
			try {
				videoLabelImage= ImageIO.read(new File(label));
				videoLabel.put(label, videoLabelImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else{
			videoLabelImage = videoLabel.get(label);
		}
		int h = videoLabelImage.getHeight(null);
		int w = videoLabelImage.getWidth(null);
		int H = image.getHeight(null);
		int W = image.getWidth(null);
		int videoLabelPlayX = (W-w)/2;
		int videoLabelPlayY = (H-h)/2;
		int x = (int) (videoLabelPlayX*factor);
		int y = (int) (videoLabelPlayY*factor);
		w = (int) (videoLabelImage.getWidth(null)*factor);
		h = (int) (videoLabelImage.getHeight(null)*factor);
		Image tmpLabelImage = videoLabelImage;
		if(x != videoLabelPlayX){
			tmpLabelImage = videoLabelImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);
		}
		 
		BufferedImage result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = result.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.drawImage(tmpLabelImage, x+offsetX,y+offsetY, null);
		
		return result;
	}
	
	
	/**
	 * 
	 * @param map
	 * @param width: collage width
	 * @param height: collage height
	 * @return
	 */
	public static BufferedImage generateCollage(
			ArrayList<ArrayList<String>> map, int width,
			int height) {
		printMap(map);
		ArrayList<ArrayList<String>> keyImages = ClusterFactory.selectKeyImages(map);
		printMap(keyImages);
		map = keyImages;
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		initializeImage(result);
		ArrayList<String> imagelist = new ArrayList<String>();
		int num = 0;
		if(map.size()>0){
			imagelist.addAll(map.get(0));
			if(imagelist.size()<=1){
				num = 1;
			}
			else if(imagelist.size()<=4){
				num = 2;
			}
			else{
				num = 4;
			}
			
		}
		else {
			return result;
		}
		int deltaW = width/num;
		int deltaH = height/num;
		
		
		int offsetY = 0;
		float factor = (float) (1.0/num);
		for(int i = 0; i<num; i++){
			int offsetX = 0;
			for(int j = 0; j<num; j++){
				if(imagelist.isEmpty()){
					return result;
				}
				String filename = imagelist.remove(0);
				try {
					BufferedImage current = new BufferedImage(deltaW, deltaH, BufferedImage.TYPE_INT_RGB);
					FileInputStream is = new FileInputStream(new File(filename));
					byte[] bytes = new byte[SIZE * 3];
					int offset = 0;
					int numRead = 0;
					while (offset < bytes.length
							&& (numRead = is.read(bytes, offset, bytes.length
									- offset)) >= 0) {
						offset += numRead;
					}
					is.close();
					int ind = 0;
					for(int y = 0; y<deltaH; y++){
						int mapY = linearMapping(y, factor);
						if (mapY >= HEIGHT) {
							mapY = HEIGHT - 1;
						}
						ind = (mapY * WIDTH);
						for(int x = 0; x<deltaW; x++){
							int mapX = linearMapping(x, factor);
							if (mapX >= WIDTH) {
								mapX = WIDTH - 1;
							}
							
							int indent = ind+mapX;

							byte r = bytes[indent];
							byte g = bytes[indent + SIZE];
							byte b = bytes[indent + SIZE * 2];

							int pix = 0xff000000 | ((r & 0xff) << 16)
									| ((g & 0xff) << 8) | (b & 0xff);

							result.setRGB(x+offsetX, y+offsetY, pix);
							current.setRGB(x, y, pix);
						}
					}
					
					//ImageReader.displayImage(new ImageIcon(current), null);
					
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(isVideo(filename)){
					result = addVideoLabel(result, VIDEO_LABEL_PLAY, offsetX, offsetY, factor);
				}
				
				
				offsetX+=deltaW;
			}
			offsetY+=deltaH;
		}
		
		
		return result;
	}
	private static void initializeImage(BufferedImage image){
		int h = image.getHeight();
		int w = image.getWidth();
		for(int x = 0; x<w; x++){
			for(int y = 0; y<h; y++){
				image.setRGB(x, y, DEFAULT_COLOR.getRGB());
			}
		}
	}
	
	private static void printMap(ArrayList<ArrayList<String>> map) {
		System.out.println("Current Collage:===============================");
		for(ArrayList<String> list:map){
			System.out.println(list);
		}
		System.out.println("===============================================");
	}
	public static ImageIcon readImage(String filename, int width, int height){
		Image result = readVideo(filename, width, height, 1).get(0).getImage();
		if(isVideo(filename)){
			result = addVideoLabel(result, VIDEO_LABEL_PLAY,0, 0, FULL_SIZE);
		}
		return new ImageIcon(result);
	}
	public static ArrayList<ImageIcon> readVideo(String filename, int width,
			int height){
		return readVideo(filename, width, height, -1);
	}
	
	public static boolean isImage(String imageFile){
		return imageFile.contains(MyApplication.IMAGE_FILE);
	}
	public static boolean isVideo(String imageFile){
		return imageFile.contains(MyApplication.VIDEO_FILE);
	}

	public static ArrayList<ImageIcon> readVideo(String filename, int width,
			int height, int frameNum){
		float widthFactor = ((float)width)/WIDTH;
		float heightFactor = ((float)height)/HEIGHT;
		boolean infinite = frameNum < 0? true:false;
		
		InputStream is = null;
		ArrayList<ImageIcon> video = new ArrayList<ImageIcon>();
		try {
			File file = new File(filename);
			is = new FileInputStream(file);

			byte[] bytes = new byte[SIZE * 3];
			
			int numRead = 0;
			while ((infinite||(frameNum--)>0) && numRead >= 0) {// not end of file
				int offset = 0;
				while (offset < bytes.length
						&& (numRead = is.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
				int ind = 0;
				BufferedImage currentImage = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				for (int y = 0; y < height; y++) {
					int mapY = 0;
					mapY = linearMapping(y, heightFactor);
					if (mapY >= HEIGHT) {
						mapY = HEIGHT - 1;
					}
					ind = (mapY * WIDTH);
					for (int x = 0; x < width; x++) {
						int pix = 0;
						int mapX = 0;
						mapX = linearMapping(x, widthFactor);
						
						if (mapX >= WIDTH) {
							mapX = WIDTH - 1;
						}
						int indent = ind + mapX;

						byte r = bytes[indent];
						byte g = bytes[indent + SIZE];
						byte b = bytes[indent + SIZE * 2];

						pix = 0xff000000 | ((r & 0xff) << 16)
								| ((g & 0xff) << 8) | (b & 0xff);

						currentImage.setRGB(x, y, pix);
					}
				}
				video.add(new ImageIcon(currentImage));

			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return video;
	}
	
	
	
	

	private static int linearMapping(int val, float factor) {
		return Math.round(val / factor);
	}

	public static void main(String[] args) {
		
		if(args.length<4){
			System.err.println("Wrong args given");
			System.exit(-1);
		}
		String fileName = args[0];
		int width = Integer.parseInt(args[1]);
		int height = Integer.parseInt(args[2]);
		int frameRate = Integer.parseInt(args[3]);

		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		JFrame window = new JFrame();
		JLabel frame = new JLabel(new ImageIcon(img));
		JLabel title = new JLabel("<html><h2>Initializing...</h2></html>",SwingConstants.CENTER);
		window.getContentPane().add(frame, BorderLayout.CENTER);
		window.getContentPane().add(title,BorderLayout.NORTH);
		window.pack();
		window.setVisible(true);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		ImageReader.displayImage(ImageReader.readVideo(fileName, width, height, -1).get(0), frame);

	}

	

}