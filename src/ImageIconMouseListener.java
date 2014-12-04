import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO add enter animation

public class ImageIconMouseListener extends MyMouseListener {
	private static final int INTERVAL = 200;
	public ImageLabel label;
	ArrayList<ImageIcon> video ;
	private volatile Thread display;
	private volatile boolean threadSuspended;
	
	public ImageIconMouseListener(JLabel preview, ImageLabel label){
		super(label, preview);
		this.label = label;
		this.video = ImageReader.readVideo(this.label.imageFile, MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			if(display == null){
				start();
			}
			else{
				stop();
			}
			
		}
		
	}

	private void start() {
		display = new Thread(new Runnable(){
			@Override
			public void run() {
				displayVideo();
			}
			
		});
		display.start();
		
	}
	
	private void stop(){
		if(display!= null){
			synchronized(display){
				Thread bound = display;
				display = null;
				bound.interrupt();
			}
		}
		
	}
	private void run(){
		Thread cur = Thread.currentThread();
		while(display == cur){
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(display != null){
			synchronized(display){
				this.threadSuspended = false;
				display.notify();
			}
			
		}
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(display != null){
			synchronized(display){
				this.threadSuspended = true;
			}
			
		}
		super.mouseExited(e);
	}
	
	private void displayVideo(){
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

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
