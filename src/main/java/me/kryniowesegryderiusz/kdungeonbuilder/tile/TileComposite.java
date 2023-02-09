package me.kryniowesegryderiusz.kdungeonbuilder.tile;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.Coordinates;
import me.kryniowesegryderiusz.kdungeonbuilder.door.DoorList;

/**
 * Composite of tiles
 * 
 * @author user
 */
public class TileComposite {

	@Getter
	@Setter
	int wave = -1;
	@Getter
	@Setter
	int no = -1;

	public static int STANDARD_LENGTH = 45;

	public enum TileFlag {
		START, END, ERR, CLOSING
	}

	@Getter
	private ArrayList<TileFlag> flags = new ArrayList<TileFlag>();

	@Getter
	private int sizeX;
	@Getter
	private int sizeZ;

	@Getter
	private Tiles tiles;

	@Getter
	Coordinates compositeCoordinates;

	public TileComposite(int sizeX, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		tiles = new Tiles();
	}

	/**
	 * Changes coordinates of TileComposite including coordinates inside every Tile
	 * 
	 * @param Coordinates
	 * @return
	 */
	public TileComposite changeCoordinates(Coordinates tc) {
		this.changeCoordinates(tc.getX(), tc.getZ());
		return this;
	}

	/**
	 * Changes coordinates of TileComposite including coordinates inside every Tile
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public TileComposite changeCoordinates(int x, int z) {
		compositeCoordinates = new Coordinates(x, z);

		for (int nox = 0; nox < sizeX; nox++) {
			for (int noz = 0; noz < sizeZ; noz++) {
				tiles.getTiles()[nox][noz].initCoordinates(compositeCoordinates.getX() + nox * STANDARD_LENGTH,
						compositeCoordinates.getZ() + noz * STANDARD_LENGTH);
			}
		}
		return this;
	}

	public String toString() {
		String s = "SizeX: " + this.sizeX + " | SizeZ: " + this.sizeZ;
		if (this.compositeCoordinates != null)
			s = s + " XZ: " + this.compositeCoordinates.toString();
		return s;
	}

	public TileComposite clone() {
		TileComposite nc = new TileComposite(sizeX, sizeZ);

		for (int nox = 0; nox < sizeX; nox++) {
			for (int noz = 0; noz < sizeZ; noz++) {
				nc.getTiles().getTiles()[nox][noz].getDoors()
						.addDoors(tiles.getTiles()[nox][noz].getDoors().getDoors());
			}
		}

		if (compositeCoordinates != null)
			nc.changeCoordinates(compositeCoordinates.getX(), compositeCoordinates.getZ());

		return nc;
	}

	public class Tiles {

		@Getter
		private Tile[][] tiles;

		public Tiles() {
			tiles = new Tile[sizeX][sizeZ];

			for (int nox = 0; nox < sizeX; nox++) {
				for (int noz = 0; noz < sizeZ; noz++) {
					tiles[nox][noz] = new Tile(nox, noz);
				}
			}
		}

		public Tile getTile(Coordinates tc) {
			for (Tile[] tt : tiles) {
				for (Tile t : tt) {
					if (t != null && t.getCoordinates().equals(tc)) {
						return t;
					}
				}
			}
			return null;
		}

		public ArrayList<Tile> getBorderTiles() {
			ArrayList<Tile> borders = new ArrayList<Tile>();

			for (Tile[] tt : tiles) {
				for (Tile t : tt) {
					if (t.getRelativeX() == 0 || t.getRelativeZ() == 0 || t.getRelativeX() == sizeX - 1
							|| t.getRelativeZ() == sizeZ - 1)
						if (!borders.contains(t))
							borders.add(t);
				}
			}

			return borders;
		}

	}

	public class Tile {

		@Getter
		int relativeX;
		@Getter
		int relativeZ;

		public Tile(int relativeX, int relativeZ) {
			this.relativeX = relativeX;
			this.relativeZ = relativeZ;
		}

		@Getter
		private Coordinates coordinates;
		@Getter
		private DoorList doors = new DoorList();

		public void initCoordinates(int x, int z) {
			coordinates = new Coordinates(x, z);
		}

	}

}
