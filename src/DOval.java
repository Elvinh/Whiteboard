
import java.awt.*;

public class DOval extends DShape {
	
	public DOval()
	{
		super();
	}
	
	public void draw(Graphics g)
	{	
		g.setColor(info.getC());
        g.fillOval(info.getX(),info.getY(),info.getWidth(),info.getHeight());
	}
	
	public String getName()
	{
		return "DOval";
	}
	
}
