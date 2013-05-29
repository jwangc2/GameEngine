package Utilities;

public class MathTK {
	public static int sign(int i){
		return i < 0 ? -1 : i > 0 ? 1 : 0;
	}
	
	public static int sign(double d){
		return d < 0 ? -1 : d > 0 ? 1 : 0;
	}
}
