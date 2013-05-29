package Entities;
import java.util.ArrayList;

import Game.Game;


public class Follower extends Entity{
	Entity target;
	
	//constructors
	public Follower(Game game, double x, double y, int w, int h){
		super(game, x, y, w, h);
	}
	
	public Follower(Game game, int w, int h){
		super(game, w, h);
	}
	
	//basic event methods
	public void create(){
		target = null;
	}
	
	public void step(){
		double aimAngle = Math.atan2(target.getY() - y, target.getX() - x);
		double aimSpeed = 10;
		double nx = x + Math.cos(aimAngle) * aimSpeed;
		double ny = y + Math.sin(aimAngle) * aimSpeed;
		
		if (getCollisions(nx, ny, game.getActiveEntityList()).size() > 0){
			aimSpeed = 0;
			aimAngle = direction;
		}
		
		setSpeed(aimSpeed);
		setDirection(aimAngle);
		
		setStaticMask(!game.getMousePressed());
	}
	
	//basic collision abstracts
	public boolean isCollidingWith(Entity other){	
		//collide only with Player objects
		return (other instanceof Player);
	}
	
	//setters
	public void setTarget(Entity e){
		target = e;
	}
}
