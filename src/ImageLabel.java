import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ImageLabel extends JLabel {
	public static final int ICON_WIDTH = MyApplication.IMAGE_WIDTH/CollagePanel.COUNT;
	public static final int ICON_HEIGHT = MyApplication.IMAGE_HEIGHT/CollagePanel.COUNT;
	
	public JLabel preview;
	public String imageFile;
	public ImageLabel(JLabel preview){
		this.preview = preview;
		BufferedImage image = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.setIcon(new ImageIcon(image));
		//default
		this.setImage("./dataset/image001.rgb");
		this.addMouseListener(new ImageIconMouseListener(this.preview, this));
	}
	
	public void setImage(String image){
		this.imageFile = image;
		this.setIcon(new ImageIcon(ImageReader.readVideo(image, ICON_WIDTH, ICON_HEIGHT).get(0).getImage()));
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
