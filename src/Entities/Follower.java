package Entities;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import Utilities.MathTK;

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
	@Override
	public void create(){
		target = null;
	}
	
	@Override
	public void step(){
		if (target != null){
			double aimAngle = Math.atan2(target.getY() - y, target.getX() - x);
			double aimSpeed = 10;
//			double nx = x + Math.cos(aimAngle) * aimSpeed;
//			double ny = y + Math.sin(aimAngle) * aimSpeed;
//			
//			if (getCollisions(x, y, game.getActiveEntityList()).size() > 0){
//				aimSpeed = 0;
//				aimAngle = direction;
//			}
			
			setSpeed(aimSpeed);
			setDirection(aimAngle);
			
			setStaticMask(!game.getMousePressed());
		}
	}
	
	@Override
	public void draw(Graphics g){
		g.setColor(new Color(0, 0, 0, 128));
		drawMask(g, x, y, Color.white);
		//g.setColor(new Color(0, 0, 128, 128));
		//drawMask(g, xPrevious, yPrevious, Color.white);
	}
	
	@Override
	public void collision(Entity other){
		
		double backDirection = direction + Math.PI;
		//backout
		for(double d = speed; d >= 0; d --){
			double nx = x + d * Math.cos(backDirection);
			double ny = y + d * Math.sin(backDirection);
			
			if (getCollisions(nx, ny, game.getActiveEntityList()).size() > 0){
				x += (d + 1) * Math.cos(backDirection);
				y += (d + 1) * Math.sin(backDirection);
				break;
			}
		}
		
		//target = null;
		setSpeed(0);
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
