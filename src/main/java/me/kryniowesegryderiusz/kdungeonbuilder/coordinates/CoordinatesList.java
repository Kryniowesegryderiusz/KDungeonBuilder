package me.kryniowesegryderiusz.kdungeonbuilder.coordinates;

import java.util.ArrayList;

public class CoordinatesList {
	
	private ArrayList<Coordinates> coordinates = new ArrayList<>();
	
	public CoordinatesList addCoordinate(Coordinates co) {
		
		boolean exist = false;
		
		for (Coordinates tc : coordinates) {
			if (tc.equals(co))
				exist = true;
		}
		
		if (!exist)
			coordinates.add(co);
		
		return this;
	}
	
	public CoordinatesList addCoordinates(ArrayList<Coordinates> cos) {
		for (Coordinates co : cos) {
			this.addCoordinate(co);
		}
		return this;
	}
	
	public boolean containsReference(Coordinates tile) {
		return coordinates.contains(tile);
	}

	public ArrayList<Coordinates> getAll() {
		return coordinates;
	}
	
}
