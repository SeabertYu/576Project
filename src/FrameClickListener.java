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

	@Override
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

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
