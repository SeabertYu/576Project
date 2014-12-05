import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener extends MyMouseListener {
	public ImageLabel label;
	ArrayList<ImageIcon> video;
	private volatile VideoPlayer player;
	private ImageIcon fullIcon;

	public ImageIconMouseListener(JLabel preview, ImageLabel label) {
		super(label, preview);
		this.label = label;
		this.fullIcon = new ImageIcon(this.label.fullImage);
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			if(video == null){
				/*Thread loadImage = new Thread(new Runnable(){

					@Override
					public void run() {
						
					}
					
				});
				loadImage.start();*/
				video = ImageReader.readVideo(label.imageFile,
						MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
				System.out.println(label.imageFile);
				
			}
			if (this.label.imageFile.contains(MyApplication.IMAGE_FILE)) {
				VideoPlayer.closeIfExist();
				MyApplication.videoCollageSeeker.close();
				return;
			}
			if (player != null && player.isVideoSuspended()) {
				player.start();
			} else if (player == null || player.isFinished()) {
				player = VideoPlayer.acquireVideoPlayer(label, preview, video);
				if (!this.label.imageFile.contains(MyApplication.IMAGE_FILE)) {
					MyApplication.videoCollageSeeker.display(video,
							this.label.imageFile, player);
				}
				player.start();
			} else {
				player.suspend();
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(this.video == null){
			ImageReader.displayImage(this.fullIcon, this.preview);
		}
		else if (this.label.imageFile.contains(MyApplication.VIDEO_FILE) || player != null && player.isVideoSuspended()) {
			ImageReader.displayImage(video.get(player.getFrameIndex()), this.preview);
		}
		super.mouseEntered(e);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

}
