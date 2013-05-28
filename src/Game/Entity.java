package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public abstract class Entity implements Comparable<Entity>{
	protected int bboxLeft, bboxTop, bboxRight, bboxBottom;
	protected int xoff, yoff;
	protected int depth;
	protected double x, y, xscale, yscale;
	protected double hspeed, vspeed, speed, direction;
	protected Shape basicBbox, myBbox, basicMask, myMask;
	protected Game game;
	protected boolean isDestroyed;
	
	//constructors
	public Entity(Game game, double x, double y, int w, int h){
		//misc.
		this.game = game;
		isDestroyed = false;
		
		//transformations
		this.x = x;
		this.y = y;
		xscale = 1.0;
		yscale = 1.0;
		
		//movement - never set one of these variables directly; use the setters / getters! (for updating Cartesian vs. Polar vectors)
		hspeed = 0.0;
		vspeed = 0.0;
		speed = 0.0;
		direction = 0.0f;
		
		//inherent properties
		setOffset(0, 0);
		basicBbox = new Rectangle2D.Double(0, 0, w, h);
		myBbox = null;
		setBbox(0, 0, w, h);
		
		basicMask = new Rectangle2D.Double(0, 0, w, h);
		myMask = null;
		
		updateBbox();
		updateShape();
		
		//run code on creation
		create();
	}
	
	public Entity(Game game, int w, int h){
		this(game, 0, 0, w, h);
	}
	
	//methods called by the Game object
	public final boolean run(){ //do not overwrite
		if (isDestroyed){
			destroy();
			return true;
		}
		else{
			//run calculations
			step();
			
			//move based on speed
			x += hspeed;
			y += vspeed;
			
			//update our shapes for drawing and for the next step
			updateBbox();
			updateShape();
		}
		
		return false;
	}

	//event based methods (that need to be implemented / overridden)
	public abstract void create();
	public abstract void destroy();
	public abstract void step();
	public void draw(Graphics g){
		g.setColor(new Color(0, 0, 128, 64));
		drawBbox(g);
		g.setColor(new Color(0, 0, 0, 128));
		drawMask(g, Color.white);
	}
	
	//helper drawing methods
	public void drawBbox(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.fill(myBbox);
	}
	
	public void drawMask(Graphics g, Color lineColor){
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform atLine = updateShape();
		g2.fill(myMask);
		Shape pointer = atLine.createTransformedShape(new Line2D.Double(0, 0, getWidth(), 0));
		g2.setColor(lineColor);
		g2.draw(pointer);
	}
	
	//private methods
	protected final void destroySelf(){ //do not overwrite
		isDestroyed = true;
		game.removeEntity(this);
	}
	
	//masks and transformations
	protected Shape createMaskAt(double xpos, double ypos){
		AffineTransform at = new AffineTransform();
		at.translate(-xoff, -yoff);
		Shape tMask = at.createTransformedShape(basicMask);
		
		at = getMaskXForm(xpos, ypos);
		
		return at.createTransformedShape(tMask);
	}
	
	protected AffineTransform getMaskXForm(double xpos, double ypos){
		AffineTransform at = new AffineTransform();
		at.translate(xpos, ypos);
		at.rotate(direction);
		at.scale(xscale, yscale);
		
		return at;
	}
	
	protected AffineTransform updateShape(){
		myMask = createMaskAt(x, y);
		return getMaskXForm(x, y);
	}
	
	protected AffineTransform updateBbox(){
		AffineTransform at = new AffineTransform();
		at.setToIdentity();
		at.translate(x + bboxLeft, y + bboxTop);
		basicBbox = new Rectangle2D.Double(-xoff, -yoff, getWidth(), getHeight());
		myBbox = at.createTransformedShape(basicBbox);
		return at;
	}
	
	//collision detection between two entities
	public final boolean collidesWith(Entity other){
		return collidesWith(other, x, y);
	}
	
	public final boolean collidesWith(Entity other, double xpos, double ypos){
		Shape tempMask = createMaskAt(xpos, ypos);
		return ShapeCollision.shapesIntersect(tempMask, other.getMask());
	}
	
	//get all Entities that this Entity collides with
	public final ArrayList<Entity> getCollisions(ArrayList<Entity> collisionGroup){
		return getCollisions(x, y, collisionGroup);
	}
	
	public final ArrayList<Entity> getCollisions(double xpos, double ypos, ArrayList<Entity> collisionGroup){
		ArrayList<Entity> collisions = new ArrayList<Entity>();
		
		for(Entity e : collisionGroup){
			if (e != this){
				if (collidesWith(e, xpos, ypos)){collisions.add(e);}
			}
		}
		
		return collisions;
	}
	
	//given a list of Entities you are colliding with, return whether any should register an actual collision
	public abstract boolean isCollidingWith(ArrayList<Entity> collList);
	
	//use these functions to check for collisions
	public final boolean collides(ArrayList<Entity> collisionGroup){
		return collides(x, y, collisionGroup);
	}
	
	public final boolean collides(double xpos, double ypos, ArrayList<Entity> collisionGroup){
		return isCollidingWith(getCollisions(xpos, ypos, collisionGroup));
	}

	//getters
	public int getWidth() {return (bboxRight - bboxLeft);}
	public int getHeight() {return (bboxBottom - bboxTop);}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getHspeed(){return hspeed;}
	public double getVspeed(){return vspeed;}
	public double getSpeed(){return speed;}
	public double getDirection(){return direction;}
	public int getBboxLeft() {return bboxLeft;}
	public int getBboxTop() {return bboxTop;}
	public int getBboxRight() {return bboxRight;}
	public int getBboxBottom() {return bboxBottom;}
	public Shape getMask() {return myMask;}
	public Shape getBbox() {return myBbox;}
	
	//setters
	public void setX(double x) {this.x = x;}
	public void setBbox(int bboxLeft, int bboxTop, int bboxRight, int bboxBottom){
		this.bboxLeft = bboxLeft;
		this.bboxTop = bboxTop;
		this.bboxRight = bboxRight;
		this.bboxBottom = bboxBottom;
	}
	public void setBboxLeft(int bboxLeft) {setBbox(bboxLeft, bboxTop, bboxRight, bboxBottom);}
	public void setBboxTop(int bboxTop) {setBbox(bboxLeft, bboxTop, bboxRight, bboxBottom);}
	public void setBboxRight(int bboxRight) {setBbox(bboxLeft, bboxTop, bboxRight, bboxBottom);}
	public void setBboxBottom(int bboxBottom) {setBbox(bboxLeft, bboxTop, bboxRight, bboxBottom);}
	public void setOffset(int xoff, int yoff){
		this.xoff = xoff; 
		this.yoff = yoff;
	}
	
	//components
	public void setHspeed(double hspeed){
		this.hspeed = hspeed;
		updateSpeed();
	}
	public void setVspeed(double vspeed){
		this.vspeed = vspeed;
		updateSpeed();
	}
	public void updateSpeed(){
		speed = Math.sqrt(Math.pow(hspeed, 2) + Math.pow(vspeed, 2));
		direction = Math.atan2(vspeed, hspeed);
	}
	
	//vectors
	public void setSpeed(double speed){
		this.speed = speed;
		updateComponents();
	}
	public void setDirection(double direction){
		this.direction = direction;
		updateComponents();
	}
	public void updateComponents(){
		hspeed = speed * Math.cos(direction);
		vspeed = speed * Math.sin(direction);
	}
	
	//implementations
	public int compareTo(Entity other){
		return (other.depth - depth);
	}
}
