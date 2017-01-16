import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Canvas extends JPanel implements MouseMotionListener, MouseListener {
	
	public static ArrayList<DShape> shapesList;
	
	static double previousFontSize;
	static DShape selected;
	static DShapeModel selectedModel;
	
	private int x,y;
	private ArrayList<Point> knobs;
	private ControlPanel controls;
	private WhiteboardNetworking net;
	
	public Point upperLeft, bottomLeft, upperRight, bottomRight;
	public Rectangle ul, bl, ur,br;
	public Point movingPoint;
	public Point anchorPoint;
	public JButton saveImage;
	public JPanel save;
	
	int offsetX, offsetY;
	DRect rect;
	boolean dragging, draggingUl, draggingBl, draggingUr, draggingBr;
	
	
	public Canvas(ControlPanel controls, WhiteboardNetworking net)
	{
		
		super();
		saveImage = new JButton("Save as PNG");
		save = new JPanel();
		save.add(saveImage);
		this.setPreferredSize(new Dimension(500,500));
		this.setLayout(new BorderLayout());
		this.add(save, BorderLayout.BEFORE_FIRST_LINE);
		addButton();
		this.setOpaque(true);
	    this.setBackground(Color.WHITE);
	    shapesList = new ArrayList<>();
	    knobs = new ArrayList<>();
	    //Adds clicking on shapes to select it.
	    addMouseMotionListener(this);
	    addMouseListener(this);
	    this.controls = controls;
	    this.net = net;
	}
	
	public void defineKnobs()
	{
		upperLeft = new Point(selected.getBounds().x, selected.getBounds().y);
 		bottomLeft= new Point(selected.getBounds().x, (int)selected.getBounds().getMaxY());
 		upperRight = new Point((int)selected.getBounds().getMaxX(), selected.getBounds().y);
 		bottomRight = new Point((int)selected.getBounds().getMaxX(), (int)selected.getBounds().getMaxY());
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		for (ListIterator<DShape> iterator = shapesList.listIterator(shapesList.size()); iterator.hasPrevious();)
		{
			DShape d = (DShape) iterator.previous();
			if (d.getName().equals("DRect"))
			{	
				DRect rect = (DRect) d;
				rect.draw(g2);
			}
			else if (d.getName().equals("DOval"))
			{
				DOval oval = (DOval) d;
				oval.draw(g2);
			}
			else if (d.getName().equals("DLine"))
			{
				DLine line = (DLine) d;
				line.draw(g2);
			}
			else if (d.getName().equals("DText"))
			{
				DText text = (DText) d;
				text.draw(g2);
			}
			if (selected != null)
			{
				if (shapesList.contains(selected))
				{
					
					DShape shape = shapesList.get(shapesList.indexOf(selected));
					if ("DRect".equals(shape.getName()))
					{
						// creates point objects for the knobs
						defineKnobs();
						DRect rect = (DRect) shape;
						g2.setColor(Color.CYAN);
						g2.drawRect(rect.info.getX(),rect.info.getY(),rect.info.getWidth(),rect.info.getHeight());
						
						// creating the knobs as rectangle objects
						ul = new Rectangle(upperLeft.x -3 , upperLeft.y - 3, 9 , 9);
						bl = new Rectangle(bottomLeft.x-3, bottomLeft.y -3, 9, 9);
						ur = new Rectangle(upperRight.x -3, upperRight.y-3, 9, 9);
						br = new Rectangle(bottomRight.x -3, bottomRight.y -3, 9 ,9);
						
						// drawing the knobs
						g2.setColor(Color.BLACK);
						g2.fillRect(ul.x, ul.y, ul.width, ul.height);
						g2.fillRect(bl.x, bl.y, bl.width, bl.height);
						g2.fillRect(ur.x, ur.y, ur.width, ur.height);
						g2.fillRect(br.x, br.y, br.width, br.height);
						
						
					}
					else if (shape.getName().equals("DOval"))
					{
						DOval oval = (DOval) shape;
						g2.setColor(Color.CYAN);
						g2.drawOval(oval.info.getX(),oval.info.getY(),oval.info.getWidth(),oval.info.getHeight());
						defineKnobs();
						drawSquares(g2);
						
						
					}
					else if (shape.getName().equals("DLine"))
					{
						upperLeft = new Point(selected.info.getX(), selected.info.getY());
						bottomLeft = new Point((int)selected.info.getBounds().getMaxX(), (int)selected.info.getBounds().getMaxY());
						DLine line = (DLine) shape;
						//g2.setColor(Color.CYAN);
						//g2.drawRect(line.info.getX(),line.info.getY(),line.info.getWidth(),line.info.getHeight());
						g2.setColor(Color.BLACK);
						g2.fillRect(upperLeft.x-3, upperLeft.y-3, 9, 9);
						g2.fillRect(bottomLeft.x-3, bottomLeft.y-3, 9, 9);	
					}
					
					else if (shape.getName().equals("DText"))
					{
						DText text = (DText) shape;
						g2.setColor(Color.CYAN);
						g2.drawRect(text.info.getX(),text.info.getY(),text.info.getWidth(),text.info.getHeight());
						drawSquares(g2);
					} 
					
				}
			}
		}
		repaint();
	}
	
	// testing purposes
	public static void printList()
	{
		int count = 1;
		for (DShape d : shapesList)
		{
			System.out.println(count + " " + d.getName());
			count++;
		}
	}
	
	// testing purposes
	public static void printReverse()
	{
		int count = 1;
		for (ListIterator<DShape> iterator = shapesList.listIterator(shapesList.size()); iterator.hasPrevious();)
		{
			System.out.println(count + " " + ((DShape) iterator.previous()).getName());
			count++;
		
		}
	}
	
	public static void addShape(DShapeModel shapeModel)
	{
		if (shapeModel instanceof DRectModel)
		{
			DRect rect = new DRect();
			rect.info = (DRectModel) shapeModel;
			shapesList.add(rect);
			DShapeModel.listeners.add(shapeModel);
		}
		if (shapeModel instanceof DOvalModel)
		{
			DOval oval = new DOval();
			oval.info = (DOvalModel) shapeModel;
			shapesList.add(oval);
			DShapeModel.listeners.add(shapeModel);
		}
		if (shapeModel instanceof DLineModel)
		{
			DLine line = new DLine();
			line.info = (DLineModel) shapeModel;
			shapesList.add(line);
			DShapeModel.listeners.add(shapeModel);
		}
		if (shapeModel instanceof DTextModel)
		{
			DText text = new DText();
			text.info = (DTextModel) shapeModel;
			shapesList.add(text);
			DShapeModel.listeners.add(shapeModel);
		} 
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(!net.isClient()){
			Point p = e.getPoint();
			int dx = p.x - selected.info.getX();
			int dy = p.y - selected.info.getY();
			
			if(selected.equals(rect)) {
				if(dragging == true)
				{
					selected.info.setX(p.x - offsetX);
					selected.info.setY(p.y - offsetY);
					if(net.isServer())
						net.doSend("change",selected.info);
				}
				else if(draggingUl == true)
				{
					int width = selected.info.getWidth() -dx;
					int height = selected.info.getHeight() -dy;
					if(ul.x - anchorPoint.x >= 0 || ul.y - anchorPoint.y >= 0)
					{
						
						System.out.println("working");
						//anchorPoint = br.getLocation();
						//Point newAnchorPoint = br.getLocation();
						//System.out.println("bottom Location" + anchorPoint);
						
						//selected.info.setBounds(selected.info.getX() + dx, selected.info.getY() + dy, Math.abs(e.getX()-newAnchorPoint.x), Math.abs(e.getY()-newAnchorPoint.y));
					}
					//int width = Math.abs(anchorPoint.x - movingPoint.x);
					//int height = Math.abs(anchorPoint.y - movingPoint.y);
					//selected.info.setBounds(selected.info.getX() + dx, selected.info.getY() + dy, width, height);
					System.out.println(ul);
					//selected.info.setBounds(selected.info.getX() + dx, selected.info.getY() + dy, Math.abs(e.getX()-anchorPoint.x), Math.abs(e.getY()-anchorPoint.y));
					selected.info.setBounds(selected.info.getX() + dx, selected.info.getY() + dy, width, height);
					if(net.isServer())
						net.doSend("change",selected.info);
	
	
					//System.out.println(ul);
					
					//selected.info.setBounds(, y, w, h);
					
					/*if(selected.info.width == 0 || selected.info.height == 0)
					{
						selected.info.setBounds(selected.info.x + dx, selected.info.y + dy, 10, 10);
						
					}*/
					//selected.info.setBounds(movingPoint.x + dx, movingPoint.y + dy, selected.info.getWidth() -dx, selected.info.getHeight()-dy);
					
				}
				else if(draggingBl == true)
				{
					int width = selected.info.getWidth() - dx;
					int height = dy;
					selected.info.setBounds(selected.info.getX() + dx, selected.info.getY() , width, height);
					if(net.isServer())
						net.doSend("change",selected.info);
				}
				else if(draggingUr == true)
				{
					int width = dx;
					int height = selected.info.getHeight() -dy;
					selected.info.setBounds(selected.info.getX(), selected.info.getY() + dy, width, height);
					if(net.isServer())
						net.doSend("change",selected.info);
				}
				else if(draggingBr == true)
				{
					int width = dx;
					int height = dy;
					selected.info.setBounds(selected.info.getX(), selected.info.getY(), width, height);
					if(net.isServer())
						net.doSend("change",selected.info);
				}
			 }
		}
	}
		/*int x = e.getX();
		int y = e.getY();
		Point point = e.getPoint();
		
		if(selected != null)
		{
			if(selected.equals(rect))
			{
				//if(ul.contains(e.getPoint()) || bl.contains(e.getPoint()) || ur.contains(e.getPoint()) || br.contains(e.getPoint()))
				{
					selected.info.setBounds(selected.info.getX() + point.x -selected.info.getX() , y, selected.info.getWidth()-e.getX(), selected.info.getHeight()-e.getY());
				}
				if(selected.contains(e.getPoint())){
				
					//selected.info.setX(x - offsetX);
					//selected.info.setY(y - offsetY);
				}
				
		 			//selected.info.setWidth(selected.info.getWidth());
		 			//selected.info.setHeight(selected.info.getHeight());
		 			
					
		 			//selected.info.setWidth(y - anchorPoint.x - (movingPoint.x -e.getX()));
		 			//selected.info.setHeight(x - anchorPoint.y - (movingPoint.y-e.getY()));
				
				
			}
			
			
			else if(selected.getName().equals("DOval"))
			{
				selected.info.setX(x - offsetX);
				selected.info.setY(y - offsetY);
			}
			
			else if(selected.getName().equals("DLine"))
			{
				selected.info.setX(x - offsetX);
				selected.info.setY(y - offsetY);
			}
			
			else if(selected.getName().equals("DText"))
			{
				selected.info.setX(x - offsetX);
				selected.info.setY(y - offsetY);
				setKnobs();
			}
		}
		
		
		
	}*/
	
	public void setKnobs()
	{
		getKnobs().get(0).setLocation(new Point(selected.getBounds().x-3, selected.getBounds().y-3));
		getKnobs().get(1).setLocation(new Point(selected.getBounds().x-3, (int)selected.getBounds().getMaxY()-3));
		getKnobs().get(2).setLocation(new Point((int)selected.getBounds().getMaxX()-3, selected.getBounds().y-3));
		getKnobs().get(3).setLocation(new Point((int)selected.getBounds().getMaxX()-3, (int)selected.getBounds().getMaxY()-3));
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<Point> getKnobs()
	{
		return knobs;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		 //super.mouseClicked(e);
		if(!net.isClient()){
	         System.out.println(e.getPoint());
	         for (DShape d : shapesList)
	         {
	     		if (d.getName().equals("DRect"))
	     	    {
	     	    	 rect = (DRect) d;
	     	    	if (rect.contains(e.getPoint()))
	     	    	{	
	     	    		
	     	    		selected = rect;
	     	    		selectedModel = rect.info;
	     	    		ControlPanel.enableButtons();
	     	    		knobs.add(new Point(selected.getBounds().x-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point(selected.getBounds().x-3, (int)selected.getBounds().getMaxY()-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, (int)selected.getBounds().getMaxY()-3));
	     	    		System.out.println(selected.getName());
	     	    		System.out.println("Knobs are: " + getKnobs());
	     	    		System.out.println(selected.getBounds());
	     	    		break;
	     	    	}
	     	    	
	     	    	/*if(ul.contains(e.getPoint()) || bl.contains(e.getPoint()) || ur.contains(e.getPoint()) || br.contains(e.getPoint()))
	 	    		{
	 	    			System.out.println(selected);
	 	    			System.out.println("pressing from knobs");
	 	    		}*/
	     	    	else {
	     	    		ControlPanel.disableButtons();
	     	    		selected = null;
	     	    		selectedModel = null;
	     	    		getKnobs().clear();
	     	    	}
	     	   	}
	     	    else if (d.getName().equals("DOval"))
	     	    {
	     	    	ControlPanel.enableButtons();
	     	    	DOval oval = (DOval) d;
	     	    	if (oval.contains(e.getPoint()))
	     	    	{
	     	    		selected = oval;
	     	    		selectedModel = oval.info;
	     	    		knobs.add(new Point(selected.getBounds().x-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point(selected.getBounds().x-3, (int)selected.getBounds().getMaxY()-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, (int)selected.getBounds().getMaxY()-3));
	     	    		ControlPanel.enableButtons();
	     	    		System.out.println(selected.getName());
	     	    		System.out.println("bounds for the selected oval " + selected.getBounds());
	     	    		System.out.println(getKnobs());
	     	    		break;
	     	    	}
	     	    	else {
	     	    		ControlPanel.disableButtons();
	     	    		selected = null;
	     	    		selectedModel = null;
	     	    		getKnobs().clear();
	     	    	}	
	     	  	}
	     	    else if (d.getName().equals("DLine"))
	     	    {	  
	     	    	ControlPanel.enableButtons();
	     	    	DLine line = (DLine) d;
	     	    	if (line.contains(e.getPoint()))
	     	    	{	        
	     	    		
	     	    		selected = line;
	     	    		selectedModel = line.info;
	     	    		knobs.add(new Point(selected.getBounds().x-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, (int)selected.getBounds().getMaxY()-3));
	     	    		System.out.println("Knobs are at: " + getKnobs());
	     	    		ControlPanel.enableButtons();
	     	    		System.out.println(selected.getName());
	     	    		break;
	     	    	}
	     	    	else {
	     	    		ControlPanel.disableButtons();
	      	    		selected = null;
	      	    		selectedModel = null;
	      	    		getKnobs().clear();
	      	    	}
	     	    	
	     	  	}
	     	    else if (d.getName().equals("DText"))
	     	    {	        
	     	    	ControlPanel.enableButtons();
	     	    	DText text = (DText) d;
	     	    	if (text.contains(e.getPoint()))
	     	    	{	        
	     	    		selected = text;
	     	    		selectedModel = text.info;
	     	    		knobs.add(new Point(selected.getBounds().x-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point(selected.getBounds().x-3, (int)selected.getBounds().getMaxY()-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, selected.getBounds().y-3));
	     	    		knobs.add(new Point((int)selected.getBounds().getMaxX()-3, (int)selected.getBounds().getMaxY()-3));
	     	    		ControlPanel.enableButtons();
	     	    		System.out.println(selected.getName());
	     	    		break;
	     	    	}
	     	    	else {
	     	    		ControlPanel.disableButtons();
	      	    		selected = null;
	      	    		selectedModel = null;
	      	    		getKnobs().clear();
	      	    	}
	     	  	}
	     	   	
	         } 
		}
       
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		movingPoint = e.getPoint();
		int x = e.getX();
		int y = e.getY();
		if(!net.isClient()){
			if(selected != null)
			{
				if(ul.contains(movingPoint))
				{
					anchorPoint = br.getLocation();
					System.out.println("upper left knob has been pressed. anchor point is " + anchorPoint);
				}
				
				if(selected.contains(e.getPoint()))
				{
					dragging = true;
					draggingUl =false;
					draggingBl = false;
					draggingUr = false;
					draggingBr = false;
					offsetX = x - selected.info.x;
					offsetY = y - selected.info.y;
				}
				
				if(bl.contains(e.getPoint()))
				{
					dragging = false;
					draggingBl = true;
				}
				 
			  else if(ul.contains(e.getPoint())) 
		    		{
						dragging = false;
						draggingUl = true;
		    		}
			  else if(ur.contains(e.getPoint()))
			  {
				  dragging = false;
				  draggingUr = true;
			  }
			  else if(br.contains(e.getPoint()))
			  {
				  dragging = false;
				  draggingBr = true;
			  }
				
			}
		}
		
	}
	
	private void drawSquares(Graphics2D g2)
	{
		g2.setColor(Color.BLACK);
		g2.fillRect(upperLeft.x-3, upperLeft.y-3, 9, 9);
		g2.fillRect(bottomLeft.x-3, bottomLeft.y-3, 9, 9);
		g2.fillRect(upperRight.x-3, upperRight.y-3, 9, 9);
		g2.fillRect(bottomRight.x-3, bottomRight.y-3, 9, 9);
	}

	public void mouseReleased(MouseEvent e) {
		movingPoint = null;
		//draggingUl = false;
		draggingBl = false;
		draggingUr = false;
		draggingBr = false;
	}
	
	public void saveImage(File file)
	{
		BufferedImage image = (BufferedImage) createImage(this.getWidth(), this.getHeight());
		Graphics g = image.getGraphics();
		paintAll(g);
		g.dispose();
		try{
			ImageIO.write(image, "png", file);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void addButton()
	{
		saveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selected != null)
				{
					selected = null;
				}
				String result = JOptionPane.showInputDialog("File Name", null);
				if(result != null) {
					File f = new File(result+".png");
					saveImage(f);
					
				}
			}
		});
	}


}
