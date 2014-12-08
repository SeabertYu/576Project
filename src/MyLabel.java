import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class MyLabel extends JLabel {
	public static final int ICON_WIDTH = MyApplication.IMAGE_WIDTH/CollagePanel.COUNT;
	public static final int ICON_HEIGHT = MyApplication.IMAGE_HEIGHT/CollagePanel.COUNT;
	
	public ImageIcon solidIcon;
	public ImageIcon transIcon;
	public ImageIcon currentIcon;
	public BufferedImage fullImage;
	
	protected void makeIcons(Image image){
		Image scaledImage = image.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT);	
		//default
		this.solidIcon = new ImageIcon(scaledImage);
		this.transIcon = new ImageIcon(ImageReader.makeTransparent(scaledImage, 0.7f));
		this.currentIcon = this.solidIcon;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
