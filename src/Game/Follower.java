package Game;
import java.util.ArrayList;


public class Follower extends Entity{
	Entity target;
	
	//constructors
	public Follower(Game game, double x, double y, int w, int h){
		super(game, x, y, w, h);
	}
	
	public Follower(Game game, int w, int h){
		super(game, w, h);
	}
	
	//basic event abstracts
	public void create(){
		target = null;
	}
	
	public void destroy(){
	}
	
	public void step(){
		double aimAngle = Math.atan2(target.getY() - y, target.getX() - x);
		double aimSpeed = 10;
		double nx = x + Math.cos(aimAngle) * aimSpeed;
		double ny = y + Math.sin(aimAngle) * aimSpeed;
		
		if (collides(nx, ny, game.getActiveEntityList())){
			aimSpeed = 0;
			aimAngle = direction;
		}
		
		setSpeed(aimSpeed);
		setDirection(aimAngle);
	}
	
	//basic collision abstracts
	public boolean isCollidingWith(ArrayList<Entity> collList){
		
		//collide only with Player objects
		for(Entity e: collList){
			if (e instanceof Player){
				return true;
			}
		}
		
		return false;
	}
	
	//setters
	public void setTarget(Entity e){
		target = e;
	}
}
