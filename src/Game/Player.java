package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class Player extends Entity{
	int deathTimer;
	
	//constructors
	public Player(Game game, double x, double y, int w, int h){
		super(game, x, y, w, h);
	}
	
	public Player(Game game, int w, int h){
		super(game, w, h);
	}
	
	//basics event abstracts
	public void create(){
		deathTimer = 100;
	}
	
	public void destroy(){}
	
	public void draw(Graphics g){
		g.setColor(new Color(0, 128, 0, 128));
		drawMask(g, Color.white);
	}
	
	public void step(){
		setHspeed(0);
		setVspeed(0);
		x = game.getMouseX();
		y = game.getMouseY();
	}
	
	//collision abstracts
	public boolean isCollidingWith(ArrayList<Entity> collList){
		return (collList.size() > 0);
	}
}
