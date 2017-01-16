
import java.awt.*;
import java.util.ArrayList;

public class DShapeModel {
	public int x;
	public int y;
	public int width;
	public int height;
	public Color c;
	public int id;
	public static ArrayList<DShapeModel> listeners = new ArrayList<>();
	
	public DShapeModel()
	{
		x = 10;
		y = 10;
		width = 10;
		height = 10;
		c = Color.GRAY;
	}
	
	public DShapeModel(int x, int y,int w, int h)
	{
		this.x = x;
		this.y = y;
	    width = w;
	    height = h;
	    this.c = Color.GRAY;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(this.x,this.y,this.width,this.height);
	}
	
	public void setBounds(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height =h;
	}
	
	public void resize(int w, int h)
	{
		this.width = w;
		this.height = h;
		
	}
	
	public void setLocation(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}
	
	public boolean equals(DShapeModel d)
	{
		if (d.getX() == this.x && d.getY() == this.y && d.getWidth() == this.width && d.getHeight() == this.height && d.getC() == this.c)
		{
			return true;
		}
		return false;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public void mimic(DShapeModel other) {
		this.c = other.getC();
		this.x = other.getX();
		this.y = other.getY();
		this.height = other.getHeight();
		this.width = other.getWidth();

		
	}
}
