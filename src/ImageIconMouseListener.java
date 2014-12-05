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
		if (e.getClickCount() == 2) {
			if (player == null || player.isFinished()) {
				if (!this.label.imageFile.contains(MyApplication.IMAGE_FILE)) {
					MyApplication.videoCollageSeeker.display(video,
							this.label.imageFile);
				}
				player = new VideoPlayer(label, preview, video);
				player.start();
			} else {
				if (!this.label.imageFile.contains(MyApplication.IMAGE_FILE)) {
					MyApplication.videoCollageSeeker.close();
				}
				player.stop();
			}

		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (player != null) {
			if (player.display != null) {
				synchronized (player.display) {
					player.setThreadSuspended(false);
					player.display.notify();
				}
			}
		}
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (player != null) {
			synchronized (player) {
				player.setThreadSuspended(true);
			}
		}
		super.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

}
