package me.kryniowesegryderiusz.kdungeonbuilder.door;

import java.util.HashMap;

public enum Door {
	
	N, 
	S,
	W, 
	E;
	
	private Door() {
		
	}
	
	/*
	 * Opposites
	 */
	
	private static HashMap<Door, Door> opposites = new HashMap<>();
	
	static {
		opposites.put(Door.N, Door.S);
		opposites.put(Door.S, Door.N);
		opposites.put(Door.W, Door.E);
		opposites.put(Door.E, Door.W);
	}
	
	public Door getOpposite() {
		return opposites.get(this);
	}
	
	/*
	 * Clockwise rotate
	 */
	
	private static HashMap<Door, Door> clockwiseRotate90Deg = new HashMap<>();
	
	static {
		clockwiseRotate90Deg.put(Door.N, Door.W);
		clockwiseRotate90Deg.put(Door.S, Door.E);
		clockwiseRotate90Deg.put(Door.W, Door.S);
		clockwiseRotate90Deg.put(Door.E, Door.N);
	}
	
	public Door getRotateClockwise90Deg() {
		return clockwiseRotate90Deg.get(this);
	}

}
