import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener extends MyMouseListener {
	public ImageLabel label;
	ArrayList<ImageIcon> video;
	private volatile VideoPlayer player;

	public ImageIconMouseListener(JLabel preview, ImageLabel label) {
		super(label, preview);
		this.label = label;
		this.video = ImageReader.readVideo(this.label.imageFile,
				MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
		System.out.println(this.label.imageFile);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
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
		if (this.label.imageFile.contains(MyApplication.IMAGE_FILE) || player == null || player.isFinished()) {
			super.mouseEntered(e);
		}
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
