import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener extends MyMouseListener {
	
	public ImageLabel label;
	ArrayList<ImageIcon> video ;
	private Thread display;
	
	public ImageIconMouseListener(JLabel preview, ImageLabel label){
		super(label, preview);
		this.label = label;
		this.video = ImageReader.readVideo(this.label.imageFile, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			if(display == null){
				display = new Thread(new Runnable(){
					@Override
					public void run() {
						displayVideo();
					}
					
				});
				display.start();
			}
			else{
				display.destroy();
				display = null;
			}
			
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(display != null){
			display.resume();
		}
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(display != null){
			display.stop();
		}
		super.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void displayVideo(){
		ImageReader.displayVideo(video, 30, this.preview);
	}
}
