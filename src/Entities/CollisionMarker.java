package Entities;

import java.awt.Color;
import java.awt.Graphics;

import Game.Game;

public class CollisionMarker extends Entity{

	public CollisionMarker(Game game, double x, double y, int w, int h) {
		super(game, x, y, w, h);
		// TODO Auto-generated constructor stub
	}
	
	public void draw(Graphics g){
		g.setColor(new Color(128, 0, 0, 128));
		drawMask(g, Color.WHITE);
	}

	@Override
	public boolean isCollidingWith(Entity other) {
		// TODO Auto-generated method stub
		return false;
	}

}
