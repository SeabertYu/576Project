import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

//TODO click to open collage
public class CollageIconMouseListener extends MyMouseListener {
	public CollageLabel label;
	public CollagePanel collagePanel;
	
	public CollageIconMouseListener(JLabel preview, CollageLabel collageLabel, CollagePanel collagePanel) {
		super(collageLabel, preview);
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
		super.mouseEntered(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		super.mouseExited(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
