import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener extends MyMouseListener {
	public static LRU cache = new LRU(LRU.SIZE);
	
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
				if(cache.containsKey(label.imageFile)){
					video = cache.get(label.imageFile);
				}
				else{
					video = ImageReader.readVideo(label.imageFile,
							MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
					cache.put(label.imageFile, video);
					System.out.println(label.imageFile);
				}
				
				
			}
			if (ImageReader.isImage(this.label.imageFile)) {
				VideoPlayer.closeIfExist();
				MyApplication.videoCollageSeeker.close();
				return;
			}
			if (player != null && player.isVideoSuspended()) {
				this.label.setCurrentIcon(this.label.pauseIcon);
				player.start();
			} else if (player == null || player.isFinished()) {
				player = VideoPlayer.acquireVideoPlayer(label, preview, video);
				if (!this.label.imageFile.contains(MyApplication.IMAGE_FILE)) {
					MyApplication.videoCollageSeeker.display(video,
							this.label.imageFile, player);
				}
				this.label.setCurrentIcon(this.label.pauseIcon);
				player.start();
			} else {
				this.label.setCurrentIcon(this.label.solidIcon);
				player.suspend();
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!VideoPlayer.isPlaying()){
			if(ImageReader.isImage(this.label.imageFile)||this.video == null||this.player == null || this.player.isFinished()){
				ImageReader.displayImage(this.fullIcon, this.preview);
			}
			else if (ImageReader.isVideo(this.label.imageFile) || player != null && player.isVideoSuspended()) {
				ImageReader.displayImage(video.get(player.getFrameIndex()), this.preview);
			}
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
