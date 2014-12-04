import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener implements MouseListener {
	public JLabel preview;
	public ImageLabel icon;
	ArrayList<ImageIcon> video ;
	
	public ImageIconMouseListener(JLabel preview, ImageLabel icon){
		this.preview = preview;
		this.icon = icon;
		this.video = ImageReader.readVideo(this.icon.imageFile, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			ImageReader.displayVideo(video, 30, this.preview);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		ImageReader.displayImage(video.get(0), this.preview);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
