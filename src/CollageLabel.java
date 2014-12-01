import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class CollageLabel extends JLabel {
	public static final int ICON_WIDTH = ImageLabel.ICON_WIDTH;
	public static final int ICON_HEIGHT = ImageLabel.ICON_HEIGHT;
	
	public JLabel preview;
	public String imageFile;
	public ArrayList<ArrayList<String>> list;
	public BufferedImage fullImage;
	
	public CollageLabel(JLabel preview, ArrayList<ArrayList<String>> list){
		this.preview = preview;
		this.list = list;
		
		this.fullImage = ImageReader.generateCollage(list, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
		//default
		this.setIcon(new ImageIcon(this.fullImage.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT)));
		this.addMouseListener(new CollageIconMouseListener(this.preview, this));
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
