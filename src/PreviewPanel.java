import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * manipulated by CollagePabel
 * @author Boyang
 *
 */

public class PreviewPanel extends JPanel {

	private BufferedImage image;
	public JLabel previewLabel;
	/**
	 * Create the panel.
	 */
	public PreviewPanel() {
		this.image = new BufferedImage(MyApplication.IMAGE_WIDTH, MyApplication.IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		setSize(new Dimension(440, 500));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new BorderLayout(10, 0));

		previewLabel = new JLabel("");
		previewLabel.setSize(new Dimension(352, 288));
		previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		previewLabel.setBorder(new LineBorder(new Color(128, 128, 128)));
		add(previewLabel);
		
		JLabel previewTextLabel = new JLabel("Preview");
		previewTextLabel.setIcon(new ImageIcon("./image/preview.png"));
		previewTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		previewTextLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		previewTextLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(previewTextLabel, BorderLayout.SOUTH);

	}
}
