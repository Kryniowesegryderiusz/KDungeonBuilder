package me.kryniowesegryderiusz.kdungeonbuilder.door;

import java.util.HashMap;

public enum Door {
	
	POSITIVE_X,
	NEGATIVE_X,
	POSITIVE_Z,
	NEGATIVE_Z;
	
	private Door() {
		
	}
	
	/*
	 * Opposites
	 */
	
	private static HashMap<Door, Door> opposites = new HashMap<>();
	
	static {
		opposites.put(Door.POSITIVE_X, Door.NEGATIVE_X);
		opposites.put(Door.NEGATIVE_X, Door.POSITIVE_X);
		opposites.put(Door.POSITIVE_Z, Door.NEGATIVE_Z);
		opposites.put(Door.NEGATIVE_Z, Door.POSITIVE_Z);
	}
	
	public Door getOpposite() {
		return opposites.get(this);
	}
	
	/*
	 * Clockwise rotate
	 */
	
	private static HashMap<Door, Door> clockwiseRotate90Deg = new HashMap<>();
	
	static {
		clockwiseRotate90Deg.put(Door.POSITIVE_X, Door.POSITIVE_Z);
		clockwiseRotate90Deg.put(Door.POSITIVE_Z, Door.NEGATIVE_X);
		clockwiseRotate90Deg.put(Door.NEGATIVE_X, Door.NEGATIVE_Z);
		clockwiseRotate90Deg.put(Door.NEGATIVE_Z, Door.POSITIVE_X);
	}
	
	public Door getRotateClockwise90Deg() {
		return clockwiseRotate90Deg.get(this);
	}

}
