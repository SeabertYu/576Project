import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

//TODO click to open collage
public class CollageIconMouseListener implements MouseListener {
	
	public JLabel preview;
	public CollageLabel label;
	public CollagePanel collagePanel;
	
	public CollageIconMouseListener(JLabel preview, CollageLabel collageLabel, CollagePanel collagePanel) {
		this.preview = preview;
		this.label = collageLabel;
		this.collagePanel = collagePanel;
	}

	@Override
	public void mouseClicked(MouseEvent me) {//OPEN NEW PANEL
		//ImageReader.displayImage(new ImageIcon(label.fullImage), this.preview);
		System.out.println(me.getClickCount());
		if(me.getClickCount() == 2){
			this.collagePanel.addLayerImages(this.label.list);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		ImageReader.displayImage(new ImageIcon(label.fullImage), this.preview);
		highlight();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		lowlight();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	private void highlight(){
		this.label.setBorder(new LineBorder(Color.ORANGE));
	}
	
	private void lowlight(){
		this.label.setBorder(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
