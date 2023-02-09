package me.kryniowesegryderiusz.kdungeonbuilder.door;

import java.util.ArrayList;

import lombok.Getter;

public class DoorList {
	
	@Getter private ArrayList<Door> doors = new ArrayList<Door>();
	
	public DoorList addDoor(Door door) {
		if (!doors.contains(door))
			doors.add(door);
		return this;
	}
	
	public DoorList addDoors(ArrayList<Door> doors) {
		for (Door d : doors) {
			this.addDoor(d);
		}
		return this;
	}
	
	public boolean contains(Door door) {
		return this.doors.contains(door);
	}
	
	public DoorList clone() {
		return new DoorList().addDoors(doors);
	}

}
