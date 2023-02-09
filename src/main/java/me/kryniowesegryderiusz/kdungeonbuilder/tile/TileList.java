package me.kryniowesegryderiusz.kdungeonbuilder.tile;

import java.util.ArrayList;

import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.Coordinates;

public class TileList {
	
	private ArrayList<TileComposite> tiles = new ArrayList<>();
	
	public TileList addTile(TileComposite tile) {
		if (!tiles.contains(tile))
			tiles.add(tile);
		return this;
	}
	
	public TileList addTiles(ArrayList<TileComposite> tiles) {
		for (TileComposite t : tiles) {
			this.addTile(t);
		}
		return this;
	}
	
	public boolean containsReference(TileComposite tile) {
		return tiles.contains(tile);
	}
	
	public TileComposite getByCoordinates(Coordinates coordinates) {
		for (TileComposite tc : tiles) {
			if (tc.getTiles().getTile(coordinates) != null)
				return tc;
		}
		return null;
	}

	public ArrayList<TileComposite> getAll() {
		return tiles;
	}
	
}
