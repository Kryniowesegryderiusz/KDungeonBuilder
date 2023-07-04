package me.kryniowesegryderiusz.kdungeonbuilder.coordinates;

import lombok.Getter;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;

public class Coordinates {
	
	@Getter private int z;
	@Getter private int x;
	
	public Coordinates(int z, int x) {
		this.set(z, x);
	}
	
	public Coordinates set(int z, int x) {
		this.z = z;
		this.x = x;
		return this;
	}
	
	public boolean equals(Coordinates tc) {
		return tc.getX() == x && tc.getZ() == z;
	}
	
	public Coordinates clone() {
		return new Coordinates(z, x);
	}
	
	public Coordinates moveByDoor(Door door) {
		return this.moveByDoor(door, 1);
	}
	
	public Coordinates moveByDoor(Door door, int amount) {
		if (door == Door.POSITIVE_X) {
			x = x + (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.NEGATIVE_X) {
			x = x - (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.POSITIVE_Z) {
			z = z + (TileComposite.STANDARD_LENGTH*amount);
		} else if (door == Door.NEGATIVE_Z) {
			z = z - (TileComposite.STANDARD_LENGTH*amount);
		}
		return this;
	}
	
	public String toString() {
		return "[Z: " + z + " X: " + x + "]";
	}

}
