package me.kryniowesegryderiusz.kdungeonbuilder.tile;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.Coordinates;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.door.DoorList;

/**
 * Composite of tiles
 * 
 * @author user
 */
public class TileComposite {
	
	public static int STANDARD_LENGTH = 45;
	
	@Getter
	private String id;
	
	@Getter
	private int weight;

	@Getter
	@Setter
	private int wave = -1;
	@Getter
	@Setter
	private int no = -1;

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
	private boolean rotatable = false;

	@Getter
	private Tiles tiles;

	@Getter
	private Coordinates compositeCoordinates;
	
	public TileComposite() {
		
	}
	
	public TileComposite init(String id, int sizeZ, int sizeX, int weight) {
		this.sizeZ = sizeZ;
		this.sizeX = sizeX;
		this.id = id;
		this.weight = weight;
		tiles = new Tiles();
		return this;
	}
	
	public TileComposite setId(String id) {
		this.id = id;
		return this;
	}
	
	public TileComposite setWeight(int weight) {
		this.weight = weight;
		return this;
	}
	
	public TileComposite setSize(int sizeX, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		tiles = new Tiles();
		return this;
	}

	/**
	 * Changes coordinates of TileComposite including coordinates inside every Tile
	 * 
	 * @param Coordinates
	 * @return
	 */
	public TileComposite changeCoordinates(Coordinates tc) {
		this.changeCoordinates(tc.getZ(), tc.getX());
		return this;
	}

	/**
	 * Changes coordinates of TileComposite including coordinates inside every Tile
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public TileComposite changeCoordinates(int z, int x) {
		compositeCoordinates = new Coordinates(z, x);
		
		for (int noz = 0; noz < sizeZ; noz++) {
			for (int nox = 0; nox < sizeX; nox++) {
				tiles.getTiles()[noz][nox].initCoordinates(compositeCoordinates.getZ() + noz * STANDARD_LENGTH, compositeCoordinates.getX() + nox * STANDARD_LENGTH);
			}
		}

		return this;
	}
	
	public TileComposite setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
		return this;
	}
	
	public TileComposite rotate90Deg() {
		this.tiles.rotate90Deg();
	    
	    //swap sizes
	    int temp = sizeX;
	    sizeX = sizeZ;
	    sizeZ = temp;
	    
		this.onRotate();
		return this;
	}

	/**
	 * Function for overriding in dependent projects
	 */
	public void onRotate(){}
	
	public TileComposite addDoor(int z, int x, Door door) {
		tiles.getTiles()[z][x].getDoors().addDoor(door);
		return this;
	}
	
	public TileComposite addDoor(int z, int x, Door... doors) {
		for (Door door : doors) {
			addDoor(z,x,door);
		}
		return this;
	}

	public String toString() {
		String s = "TileComposite: [Id: " + this.id + " | SizeZ: " + this.sizeZ + " | SizeX: " + this.sizeX + " | Weight: " + this.weight;
		if (this.compositeCoordinates != null)
			s = s + " Coordinates: " + this.compositeCoordinates.toString();
		
		if (tiles != null)
			s += " Tiles: [" + tiles.toString() + "]";
		return s + "]";
	}

	public TileComposite clone() {
		TileComposite nc = this.getTileCompositeCloneObject();
		
		nc.init(id, sizeZ, sizeX, weight);
		nc.setRotatable(rotatable);
		
		for (int noz = 0; noz < sizeZ; noz++) {
			for (int nox = 0; nox < sizeX; nox++) {
				nc.getTiles().getTiles()[noz][nox].getDoors().addDoors(tiles.getTiles()[noz][nox].getDoors().getDoors());
			}
		}

		if (compositeCoordinates != null)
			nc.changeCoordinates(compositeCoordinates.getZ(), compositeCoordinates.getX());

		return nc;
	}
	
	/**
	 * Function for extencion purposes
	 * @param sizeX
	 * @param sizeZ
	 * @return
	 */
	public TileComposite getTileCompositeCloneObject() {
		return new TileComposite();
	}

	public class Tiles {

		@Getter
		private Tile[][] tiles;

		public Tiles() {
			tiles = new Tile[sizeZ][sizeX];

			for (int noz = 0; noz < sizeZ; noz++) {
				for (int nox = 0; nox < sizeX; nox++) {
					tiles[noz][nox] = new Tile(noz, nox);
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
					if (t.getRelativeZ() == 0 || t.getRelativeX() == 0 || t.getRelativeZ() == sizeZ - 1 || t.getRelativeX() == sizeX - 1)
						if (!borders.contains(t))
							borders.add(t);
				}
			}

			return borders;
		}
		
		public void rotate90Deg() {
		    final int M = tiles.length;
		    final int N = tiles[0].length;
		    Tile[][] rotated = new Tile[N][M];
		    for (int r = 0; r < M; r++) {
		        for (int c = 0; c < N; c++) {
		        	rotated[c][M-1-r] = tiles[r][c];
		        	rotated[c][M-1-r].getDoors().rotate90Deg();
		        }
		    }
		    this.tiles = rotated;
		}
		
		public String toString() {
			String s = "";
			for (Tile[] tt : tiles) {
				for (Tile t : tt) {
					s += " Tile:  [" + t.toString() + "]";
				}
			}
			return s;
		}

	}

	/**
	 * Class user for storing specific information about one tile
	 * Relative Z and relative X are relative coordinates inside TileComposite starting from TileComposite coordinates
	 * z, x = 0, 1, 2, ...
	 */
	public class Tile {

		@Getter
		int relativeZ;
		@Getter
		int relativeX;

		public Tile(int relativeZ, int relativeX) {
			this.relativeZ = relativeZ;
			this.relativeX = relativeX;
		}

		@Getter
		private Coordinates coordinates;
		@Getter
		private DoorList doors = new DoorList();

		public void initCoordinates(int z, int x) {
			coordinates = new Coordinates(z, x);
		}
		
		public String toString() {
			return "relativeZ: " + relativeZ + " | relativeX " + relativeX + " | doors: " + doors.toString() + (coordinates != null ? " | coordinates: " + coordinates.toString() : "");
		}

	}
}
