import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class VideoPlayer {
	public ImageLabel label;
	public JLabel preview;
	ArrayList<ImageIcon> video ;
	public volatile Thread display;
	private volatile boolean threadSuspended;
	
	public VideoPlayer(ImageLabel label, JLabel preview,
			ArrayList<ImageIcon> video) {
		super();
		this.label = label;
		this.preview = preview;
		this.video = video;
	}

	public void start() {
		display = new Thread(new Runnable(){
			@Override
			public void run() {
				displayVideo();
			}
			
		});
		display.start();
	}
	
	public void stop(){
		if(display!= null){
			synchronized(display){
				Thread bound = display;
				display = null;
				bound.interrupt();
			}
		}
		
	}
	public void displayVideo(){
		displayVideo(video, 30, this.preview);
	}
		
	private void displayVideo(ArrayList<ImageIcon> video, int frameRate, JLabel frame) {
		System.out.println(video.size());
		for (ImageIcon image : video) {
			long start = System.nanoTime();
			try {

				frame.setIcon(image);
				long end = System.nanoTime();
				long deltaTime = end - start;
				if (1000000000 / frameRate > deltaTime) {
					long time = 1000000000 / frameRate - deltaTime;
					Thread.sleep(time/1000000, (int) (time%1000000));
					if(this.threadSuspended){
						synchronized(this.display){
							while(threadSuspended){
								display.wait();
							}
						}
					}					
				}
				long realend = System.nanoTime();
				System.out.println(1000000000 / (realend - start));

			} catch (InterruptedException e) {
				System.out.println("paused!");
				return;
			}
		}
	}

	public boolean isThreadSuspended() {
		return threadSuspended;
	}

	public void setThreadSuspended(boolean threadSuspended) {
		this.threadSuspended = threadSuspended;
	}
}
