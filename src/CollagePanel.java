import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CollagePanel extends JPanel {
	public static final int COUNT = 4;
	public static final int THRESHOLD = 2;//threshold for merging clusters
	private JScrollPane scrollPane;
	private JLayeredPane layeredPane;
	private JLabel preview;
	private ArrayList<ArrayList<String>> lists;
	/**
	 * Create the panel.
	 */
	public CollagePanel(JLabel preview, ArrayList<ArrayList<String>> lists) {
		this.preview = preview;
		this.lists = lists;
		setSize(new Dimension(440, 500));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new BorderLayout(10, 0));
		
		JButton btnBack = new JButton("Back");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//TODO BACK BUTTON
			}
		});
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnBack.setBorder(new EmptyBorder(10, 100, 5, 100));
		add(btnBack, BorderLayout.SOUTH);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		layeredPane = new JLayeredPane();
		scrollPane.setViewportView(layeredPane);
		layeredPane.setLayout(new GridLayout(0, COUNT, 0, 0));
		
		displayImages();
		
		
	}
	
	private void displayImages(){
		ArrayList<ArrayList<String>> mergelist = null;
		for(ArrayList<String> list:this.lists){
			if(list.size()<= THRESHOLD){
				if(mergelist == null){
					mergelist = new ArrayList<ArrayList<String>>();
				}
				mergelist.add(list);
				if(mergelist.size()>= COUNT){
					layeredPane.add(new CollageLabel(this.preview, mergelist));
					mergelist = null;
				}
			}else{
				ArrayList<ArrayList<String>> tmplist = new ArrayList<ArrayList<String>>();
				tmplist.add(list);
				layeredPane.add(new CollageLabel(this.preview, tmplist));
			}
		}
		if(mergelist != null){
			layeredPane.add(new CollageLabel(this.preview, mergelist));
		}
		
	}

}
