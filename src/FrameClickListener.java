import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class FrameClickListener implements MouseListener {

	private VideoPlayer videoPlayer;
	private int frameIndex;

	public FrameClickListener(VideoPlayer videoPlayer, int frameIndex) {
		super();
		this.videoPlayer = videoPlayer;
		this.frameIndex = frameIndex;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			if (videoPlayer != null) {
				videoPlayer.setFrameIndex(frameIndex);
				if (videoPlayer.isVideoSuspended()) {
					videoPlayer.updatePreview(frameIndex);
				} else if (videoPlayer.isFinished()) {
					videoPlayer.restart();
				}
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
