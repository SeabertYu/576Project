import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//TODO click to open collage
public class CollageIconMouseListener implements MouseListener {
	
	public JLabel preview;
	public CollageLabel label;
	
	public CollageIconMouseListener(JLabel preview, CollageLabel collageLabel) {
		this.preview = preview;
		this.label = collageLabel;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		ImageReader.displayImage(new ImageIcon(label.fullImage), this.preview);
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

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
