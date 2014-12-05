import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class CollagePanel extends JPanel {
	public static final int COUNT = 4;
	public static final int THRESHOLD = 2;//threshold for merging clusters
	
	private JScrollPane scrollPane;
	private JLayeredPane layeredPane;
	private JLabel preview;
	private ArrayList<ArrayList<String>> lists;
	private Stack<Component> comStack = new Stack<Component>();
	
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
				VideoPlayer.closeIfExist();
				MyApplication.videoCollageSeeker.close();
				deleteLayerImages();
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
		layeredPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		scrollPane.setViewportView(layeredPane);
		addLayerImages(this.lists);
		
		
	}
	
	public void addLayerImages(ArrayList<ArrayList<String>> lists ){
		
		JLayeredPane layer = makeGridPanel(COUNT);//TODO with layered panel we can shift the position and make collage
		Component[] comList = this.layeredPane.getComponents();
		if(comList.length != 0){
			for(int i = comList.length-1; i>=0;i--){
				comStack.push(comList[i]);
				this.layeredPane.remove(comList[i]);
			}
		}
		
		if(lists.size() == 1){//add image
			for(String filename: lists.get(0)){
				layer.add(new ImageLabel(preview, filename));
			}
		}
		else{//add collage
			ArrayList<String> mergelist = null;
			for(ArrayList<String> list: lists){
				if(list.size()<= THRESHOLD){
					if(mergelist == null){
						mergelist = new ArrayList<String>();
					}
					mergelist.addAll(list);
					if(mergelist.size()>= COUNT){
						ArrayList<ArrayList<String>> tmplist = new ArrayList<ArrayList<String>>();
						tmplist.add(mergelist);
						layer.add(new CollageLabel(this, this.preview, tmplist));
						mergelist = null;
					}
				}else{
					ArrayList<ArrayList<String>> tmplist = new ArrayList<ArrayList<String>>();
					tmplist.add(list);
					layer.add(new CollageLabel(this, this.preview, tmplist));
				}
			}
			if(mergelist != null){
				ArrayList<ArrayList<String>> tmplist = new ArrayList<ArrayList<String>>();
				tmplist.add(mergelist);
				layer.add(new CollageLabel(this, this.preview, tmplist));
			}
		}
		
		
		
		
		this.layeredPane.add(layer);
		this.validate();
		System.out.println("highest: "+this.layeredPane.highestLayer());
		
	}
	public void deleteLayerImages(){
		if(comStack.isEmpty()){
			return;
		}
		Component[] list = this.layeredPane.getComponents();
		System.out.println("deleting "+list.length+" component");
		if(list!= null && list.length != 0){
			for(int i = 0; i<list.length; i++){
				this.layeredPane.remove(list[i]);
			}
		}
		this.layeredPane.add(comStack.pop());
		this.validate();
	}
	
	private JLayeredPane makeGridPanel(int rowNum){
		JLayeredPane panel = new JLayeredPane();
		panel.setLayout(new GridLayout(0, rowNum, 0, 0));
		return panel;
	}

}
