package Utilities;
import java.awt.Shape;
import java.awt.geom.Area;

public class ShapeCollision {
	public static boolean shapesIntersect(Shape shapeA, Shape shapeB){
		Area areaA = new Area(shapeA);
		Area areaB = new Area(shapeB);
		areaA.intersect(areaB);
		return (!areaA.isEmpty());
	}
}
