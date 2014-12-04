import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ImageLabel extends MyLabel {
	
	public JLabel preview;
	public String imageFile;
	public ImageLabel(JLabel preview, String filename){
		this.preview = preview;
		this.setImage(filename);
		this.addMouseListener(new ImageIconMouseListener(this.preview, this));
	}
	
	public void setImage(String image){
		this.imageFile = image;
		this.fullImage = (BufferedImage) ImageReader.readVideo(image, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT).get(0).getImage();
		makeIcons(fullImage);
		this.setIcon(this.solidIcon);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
