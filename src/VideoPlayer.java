import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class VideoPlayer {
	public ImageLabel label;
	public JLabel preview;
	ArrayList<ImageIcon> video ;
	public volatile Thread display;
	private volatile boolean suspendVideo;
	private volatile boolean switchVideo;
	private AtomicInteger frameIndex;
	private boolean finished;
	private static VideoPlayer currentVideoPlayer = null;
	
	private VideoPlayer(ImageLabel label, JLabel preview,
			ArrayList<ImageIcon> video) {
		super();
		this.label = label;
		this.preview = preview;
		this.video = video;
	}
	
	public static VideoPlayer acquireVideoPlayer(ImageLabel label, JLabel preview,
			ArrayList<ImageIcon> video) {
		if (currentVideoPlayer != null) {
			// stop playing the current video
			synchronized (currentVideoPlayer) {
				currentVideoPlayer.switchVideo(true);
			}
			// wait until the current video player is done
//			while (currentVideoPlayer.display.isAlive());
			// set the current video as finished
			currentVideoPlayer.setFinished(true);
		}
		currentVideoPlayer = new VideoPlayer(label, preview, video);
		return currentVideoPlayer;
	}

	public static void closeIfExist() {
		if (currentVideoPlayer != null) {
			// stop playing the current video
			synchronized (currentVideoPlayer) {
				currentVideoPlayer.switchVideo(true);
			}
			// wait until the current video player is done
//			while (currentVideoPlayer.display.isAlive());
			// set the current video as finished
			currentVideoPlayer.setFinished(true);
		}
	}
	
	public static boolean isPlaying(){
		if(currentVideoPlayer == null){
			return false;
		}
		else{
			return !currentVideoPlayer.isFinished()&&!currentVideoPlayer.isVideoSuspended();
		}
	}
	
	public static boolean isCurrentLabel(JLabel label){
		if(currentVideoPlayer == null){
			return false;
		}
		else{
			return currentVideoPlayer.label == label;
		}
	}
	
	public void start() {
		this.label.setCurrentIcon(this.label.pauseIcon);
		if (display == null) {
			display = new Thread(new Runnable(){
				@Override
				public void run() {
					displayVideo();
				}
				
			});
			display.start();
		} else if (suspendVideo) {
			synchronized (display) {
				System.out.println("video is resumed");
				this.suspendVideo = false;
				display.notifyAll();
			}
		}
	}
	
	/**
	 * restart the video
	 */
	public void restart() {
		this.label.setCurrentIcon(this.label.pauseIcon);
		if (isFinished()) {
			display = new Thread(new Runnable(){
				@Override
				public void run() {
					displayVideo();
				}
				
			});
			display.start();
			setFinished(false);
		}
	}
	
	public void suspend(){
		this.label.setCurrentIcon(this.label.solidIcon);
		if(display!= null){
			synchronized(this){
				suspendVideo = true;
			}
		}
		
	}
	public void displayVideo(){
		this.frameIndex = new AtomicInteger(0);
		displayVideo(video, 30, this.preview);
	}
		
	private void displayVideo(ArrayList<ImageIcon> video, int frameRate, JLabel frame) {
		for (;frameIndex.get() < video.size(); frameIndex.incrementAndGet()) {
			if (switchVideo) {
				break;
			}
			if (suspendVideo) {
				System.out.println("video is suspended");
				synchronized (display) {
					try {
						display.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			long start = System.nanoTime();
			try {
				frame.setIcon(video.get(frameIndex.get()));
				long end = System.nanoTime();
				long deltaTime = end - start;
				if (1000000000 / frameRate > deltaTime) {
					long time = 1000000000 / frameRate - deltaTime;
					Thread.sleep(time/1000000, (int) (time%1000000));
				}
				long realend = System.nanoTime();
			} catch (InterruptedException e) {
				System.out.println("paused!");
				return;
			}
		}
		setFinished(true);
		
	}
	
	public void updatePreview(int frameIndex) {
		this.preview.setIcon(video.get(frameIndex));
	}
	
	public void setFrameIndex(int index) {
		this.frameIndex.set(index);
	}
	public int getFrameIndex() {
		return frameIndex.get();
	}

	public boolean isVideoSuspended() {
		return suspendVideo;
	}
	
	private void setVideoSuspended(boolean videoSuspended) {
		this.suspendVideo = videoSuspended;
	}
	
	public boolean isVideoSwitched() {
		return switchVideo;
	}

	public void switchVideo(boolean threadSwitch) {
		this.switchVideo = threadSwitch;
	}

	public boolean isFinished() {
		return finished;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
		this.frameIndex = new AtomicInteger(0);
		this.label.setCurrentIcon(this.label.solidIcon);
	}

	public static void main(String[] args) {
	}
}
