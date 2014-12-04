import java.awt.image.BufferedImage;
import java.util.ArrayList;
//deprecated for now

public class Collage {
	private BufferedImage[][] grid;
	
	
	public Collage(ArrayList<BufferedImage> imageList){
		int length = imageList.size();
		for(int i = 1; i<=10; i++){
			int width = (int) Math.round(Math.sqrt(i));
			System.out.println(i+":"+width);
		}
		int width = (int) Math.round(Math.sqrt(length));
		int height = length/width+length%width == 0?0:1;
		grid = new BufferedImage[height][width];
		int index = 0;
		for(int i = 0; i<width; i++){
			for(int j = 0; j<height; j++){
				if(index<imageList.size()){
					grid[i][j] = imageList.get(index);
					index++;
				}
				else{
					break;
				}
				
			}
		}
		
		
	}
	
	public BufferedImage getFirst(){
		return grid[0][0];
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();
		imageList.add(new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB));
		imageList.add(new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB));
		imageList.add(new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB));
		imageList.add(new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB));
		imageList.add(new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB));
		Collage col = new Collage(imageList);
	}

}
