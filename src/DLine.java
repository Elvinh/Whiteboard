
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class DLine extends DShape {
	private static final int HIT_BOX_SIZE = 4;
	Line2D.Double line = new Line2D.Double();
	public DLine()
	{
		super();
	}
	
	public void draw(Graphics g)
	{	
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(info.getC());
		line.setLine(info.getX(), info.getY(), info.getWidth() + info.getX(), info.getHeight() + info.getY());
        g2.draw(line);
	}
	
	
	public boolean contains(Point2D p)
	{
		int rectangleX = (int) p.getX() - HIT_BOX_SIZE / 2;
		int rectangleY = (int) p.getY() - HIT_BOX_SIZE / 2;

		int width = HIT_BOX_SIZE;
		int height = HIT_BOX_SIZE;
		return line.intersects(rectangleX, rectangleY, width, height);
		
	}
	
	public String getName()
	{
		return "DLine";
	}
	
}
