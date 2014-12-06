import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;


public class MyMouseListener implements MouseListener {
	public JLabel preview;
	public MyLabel label;
	public MyMouseListener(MyLabel label, JLabel preview){
		this.label = label;
		this.preview = preview;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		highlight();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
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
	
	protected void highlight(){
//		this.label.setBorder(new LineBorder(Color.ORANGE));
		this.label.setIcon(this.label.transIcon);
	}
	
	protected void lowlight(){
//		this.label.setBorder(null);
		this.label.setIcon((this.label.currentIcon));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
