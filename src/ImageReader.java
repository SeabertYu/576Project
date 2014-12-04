import java.awt.AlphaComposite;
import java.awt.BorderLayout;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ImageReader {
	public final static int WIDTH = MyApplication.IMAGE_WIDTH;
	public final static int HEIGHT = MyApplication.IMAGE_HEIGHT;
	static int SIZE = HEIGHT * WIDTH;
	
	
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
	
	public static BufferedImage makeTransparent(Image image){
		BufferedImage result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = result.createGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
		g2.setComposite(ac);
		g2.drawImage(image, 0, 0, null);
		return result;
	}
	
	/**
	 * 
	 * @param map
	 * @param width: collage width
	 * @param height: collage height
	 * @return
	 */
	public static BufferedImage generateCollage(// TODO debug!
			ArrayList<ArrayList<String>> map, int width,
			int height) {
		printMap(map);
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ArrayList<String> imagelist = new ArrayList<String>();
		int num = 0;
		if(map.size()>0){//2x2
			imagelist.addAll(map.get(0));
			if(imagelist.size()<=4){
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
				
				
				
				offsetX+=deltaW;
			}
			offsetY+=deltaH;
		}
		
		
		return result;
	}
	
	private static void printMap(ArrayList<ArrayList<String>> map) {
		System.out.println("Current Collage:===============================");
		for(ArrayList<String> list:map){
			System.out.println(list);
		}
		System.out.println("===============================================");
	}

	public static ArrayList<ImageIcon> readVideo(String filename, int width,
			int height){
		float widthFactor = ((float)width)/WIDTH;
		float heightFactor = ((float)height)/HEIGHT;

		
		InputStream is = null;
		ArrayList<ImageIcon> video = new ArrayList<ImageIcon>();
		try {
			File file = new File(filename);
			is = new FileInputStream(file);

			byte[] bytes = new byte[SIZE * 3];
			
			int numRead = 0;
			while (numRead >= 0) {// not end of file
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
		
		ImageReader.displayImage(ImageReader.readVideo(fileName, width, height).get(0), frame);

	}

	

}