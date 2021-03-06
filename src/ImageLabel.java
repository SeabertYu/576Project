import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ImageLabel extends MyLabel {
	public JLabel preview;
	public String imageFile;
	public ImageIcon pauseIcon;
	public ImageLabel(JLabel preview, String filename){
		this.preview = preview;
		this.setImage(filename);
		this.addMouseListener(new ImageIconMouseListener(this.preview, this));
	}
	public ImageLabel(String filename){
		this.setImage(filename);
	}
	
	public void setImage(String image){
		this.imageFile = image;
		this.fullImage = (BufferedImage) ImageReader.readImage(image, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT).getImage();
		makeIcons(fullImage);
		if(ImageReader.isVideo(image)){
			this.pauseIcon = new ImageIcon(ImageReader.addVideoLabel(fullImage, ImageReader.VIDEO_LABEL_PAUSE, 0, 0, ImageReader.FULL_SIZE).getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT));
		}
		this.setIcon(this.currentIcon);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setCurrentIcon(ImageIcon icon) {
		this.currentIcon = icon;
		this.setIcon(icon);
	}

}
