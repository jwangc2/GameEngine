package Game;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

import javax.swing.*;

import Entities.Entity;
import Entities.Follower;
import Entities.Player;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Game extends Canvas implements MouseMotionListener, MouseListener{
	
	private JFrame container;
	private boolean gameIsRunning;
	private long lastLoopTime;
	private int fps;
	
	private BufferStrategy strategy;
	public final int width = 640;
	public final int height = 480;
	
	private ArrayList<Entity> entityList, del_entityList, add_entityList, active_entityList;
	private DepthComparator depthComparator;
	
	private float mousex, mousey;
	private boolean mousePressed;
	private double collBuff, collAreaX, collAreaY, collAreaWidth, collAreaHeight;
	
	//fps
	final static int TICKS_PER_SECOND = 30; //ticks / second
	final static int SKIP_TICKS = 1000 / TICKS_PER_SECOND; // 1000 ms / tick
	final static int MAX_FRAMESKIP = 10;
	
	//constructors
	
	public Game(String label){
		//top-level container
		container = new JFrame(label);
		
		//get the content and set it up
		JPanel gamePanel = (JPanel)container.getContentPane();
		gamePanel.setPreferredSize(new Dimension(width, height));
		
		addMouseMotionListener(this);
		addMouseListener(this);
		gamePanel.add(this);
		
		//we want accelerated graphics, so we will paint on our own
		setIgnoreRepaint(true);
		
		//pack and send	
		container.setResizable(false);
		container.pack();
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setVisible(true);
		container.setLocationRelativeTo(null);
		
		//setup the buffer strategy
		createBufferStrategy(2);
	    strategy = getBufferStrategy();
	    
	    //collision area
  		collAreaX = 0;
  		collAreaY = 0;
  		collAreaWidth = width;
  		collAreaHeight = height;
  		collBuff = 128;
		
	    //misc. values
		gameIsRunning = false;
		lastLoopTime = 0;
		mousex = 0;
		mousey = 0;
		mousePressed = false;
		
		//sorters
		depthComparator = new DepthComparator();
	}
	
	//game methods
	public void startGame(){
		gameIsRunning = true;
		lastLoopTime = 0;
		fps = 0;
		
		//entity lists
		entityList = new ArrayList<Entity>();
		del_entityList = new ArrayList<Entity>();
		add_entityList = new ArrayList<Entity>();
		active_entityList = new ArrayList<Entity>();
		
		//create entities
		Player p = new Player(this, width - 32, height - 32, 64, 64);
		p.setOffset(32, 32);
		addEntity(p);
		
		Follower f = new Follower(this, 32.0f, 32.0f, 64, 64);
		f.setOffset(32, 32);
		f.setTarget(p);
		addEntity(f);
		
		f = new Follower(this, 128.0f, 128.0f, 64, 64);
		f.setOffset(32, 32);
		f.setTarget(p);
		addEntity(f);
	}
	
	public void runGame(){
		startGame();
		
		double nextGameTick = System.currentTimeMillis();
		int numTimesDrawn = 0;
		lastLoopTime = System.currentTimeMillis();
		
		while(gameIsRunning){
			//events
			int loops = 0;
			while(System.currentTimeMillis() > nextGameTick && loops < MAX_FRAMESKIP){
				run();
				
				nextGameTick += SKIP_TICKS;
				loops ++;
				numTimesDrawn ++;
			}
			
			//renders as fast as possible...
			draw();
			
			//calculate the fps
			double difference = System.currentTimeMillis() - lastLoopTime;
			if (difference >= 1000){
				fps = (int)(numTimesDrawn / (difference / 1000));
				lastLoopTime = System.currentTimeMillis();
				numTimesDrawn = 0;
			}
			
		}
		
		container.dispose();
		System.exit(0);
	}
	
	public void endGame(){
		gameIsRunning = false;
	}
		
	private void run(){
		//destroy entities (by depth)
		Collections.sort(del_entityList, depthComparator);
		for(Entity e : del_entityList){
			e.destroy();
			int pos = entityList.indexOf(e);
			entityList.remove(pos);
		}
		del_entityList.clear();
		
		//add entities via queue (first in first out)
		for(Entity e : add_entityList){
			entityList.add(e);
		}
		add_entityList.clear();
		
		//determine entities to use for collision
		/*careful, careful. some objects COULD overlap, since the collisions system is based on ACTIVE entities...careful. consider revising*/
		//collAreaX = mousex;
		//collAreaY = mousey;
		Rectangle2D.Double collisionArea = new Rectangle2D.Double(collAreaX - collBuff, collAreaY - collBuff, collAreaWidth + collBuff, collAreaHeight + collBuff);
		active_entityList.clear();
		for(Entity e : entityList){
			if (ShapeCollision.shapesIntersect(collisionArea, e.getMask())){
				active_entityList.add(e);
			}
		}
		
		//make each entity move (by depth)
		Collections.sort(active_entityList, depthComparator);
		for(Entity e : active_entityList){
			e.run();
		}
	}
	
	private void draw(){
		//get the surface and clear
		Graphics g = strategy.getDrawGraphics();
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(Color.gray);
		g.fillRect(0, 0, width, height);
		
		//draw the active area
		g.setColor(Color.white);
		Rectangle2D.Double collisionArea = new Rectangle2D.Double(collAreaX - collBuff, collAreaY - collBuff, collAreaWidth + collBuff, collAreaHeight + collBuff);
		g2.fill(collisionArea);
		
		//actual drawing
		Collections.sort(entityList, depthComparator);
		for(Entity e : entityList){
			e.draw(g);
		}
		
		//flip the buffer
		g.dispose();
		strategy.show();
	}
	
	//deal with entities
	public void addEntity(Entity e){
		add_entityList.add(e);
	}
	
	public void removeEntity(Entity e){
		del_entityList.add(e);
	}
	
	public ArrayList<Entity> getActiveEntityList(){
		return active_entityList;
	}
	
	//implementations
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
    	mousex = e.getX();
		mousey = e.getY();
    }
    
    @Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePressed = false;
	}
    
    //getters and setters
    public float getMouseX(){return mousex;}
    public float getMouseY(){return mousey;}
    public boolean getMousePressed(){return mousePressed;}
    public int getFps(){return fps;}

	//our runner
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game g = new Game("Basic Game");
		g.runGame();
	}

}

class DepthComparator implements Comparator<Entity>{

	@Override
	public int compare(Entity e0, Entity e1) {
		return (e0.getDepth() - e1.getDepth());
	}
	
}