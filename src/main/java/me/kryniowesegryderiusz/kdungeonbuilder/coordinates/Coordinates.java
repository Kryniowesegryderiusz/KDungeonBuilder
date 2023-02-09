package me.kryniowesegryderiusz.kdungeonbuilder.coordinates;

import lombok.Getter;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;

public class Coordinates {
	
	@Getter private int x;
	@Getter private int z;
	
	public Coordinates(int x, int z) {
		this.set(x, z);
	}
	
	public Coordinates set(int x, int z) {
		this.x = x;
		this.z = z;
		return this;
	}
	
	public boolean equals(Coordinates tc) {
		return tc.getX() == x && tc.getZ() == z;
	}
	
	public Coordinates clone() {
		return new Coordinates(x, z);
	}
	
	public Coordinates moveByDoor(Door door) {
		return this.moveByDoor(door, 1);
	}
	
	public Coordinates moveByDoor(Door door, int amount) {
		if (door == Door.N) {
			z = z - (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.S) {
			z = z + (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.W) {
			x = x - (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.E) {
			x = x + (TileComposite.STANDARD_LENGTH*amount);
		}
		return this;
	}
	
	public String toString() {
		return "[X: " + x + " Z: " + z + "]";
	}

}
