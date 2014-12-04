import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class CollageLabel extends MyLabel {
	
	public JLabel preview;
	public String imageFile;
	public ArrayList<ArrayList<String>> list;
	
	public CollageLabel(CollagePanel collagePanel, JLabel preview, ArrayList<ArrayList<String>> list){
		this.preview = preview;
		this.list = list;
		
		this.fullImage = ImageReader.generateCollage(list, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
		makeIcons(this.fullImage);
		this.setIcon(this.solidIcon);
		this.addMouseListener(new CollageIconMouseListener(this.preview, this, collagePanel));
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
