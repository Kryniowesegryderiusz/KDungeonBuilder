package me.kryniowesegryderiusz.kdungeonbuilder;

import java.util.ArrayList;

import lombok.Getter;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;

public class DungeonSim {
	
	static ArrayList<TileComposite> templates = new ArrayList<TileComposite>();

	public DungeonSim() {
		//pojedyncze
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.S).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.W).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.E).getTc());
		
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.W).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.S, Door.W).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.S, Door.E).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.E).getTc());
		
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.S).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.W, Door.E).getTc());
		
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.S, Door.W).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.W, Door.E).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.W, Door.S, Door.E).getTc());
		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.S, Door.E).getTc());

		templates.add(new TileCompositeFactory(1,1).addDoor(0, 0, Door.N, Door.S, Door.W, Door.E).getTc());
		
		//dlugie
		templates.add(new TileCompositeFactory(1,2).addDoor(0, 0, Door.N).addDoor(0, 1, Door.S).getTc());
		templates.add(new TileCompositeFactory(1,3).addDoor(0, 0, Door.N).addDoor(0, 2, Door.S).getTc());
		templates.add(new TileCompositeFactory(2,1).addDoor(0, 0, Door.W).addDoor(1, 0, Door.E).getTc());
		templates.add(new TileCompositeFactory(3,1).addDoor(0, 0, Door.W).addDoor(2, 0, Door.E).getTc());
		
		//inne
		templates.add(new TileCompositeFactory(2,3).addDoor(0, 0, Door.W, Door.N).addDoor(1, 2, Door.E, Door.S).getTc());
		templates.add(new TileCompositeFactory(2,2).addDoor(0, 0, Door.N).addDoor(1, 1, Door.E, Door.S).getTc());
		templates.add(new TileCompositeFactory(2,2).addDoor(0, 0, Door.N).addDoor(1, 1, Door.E).getTc());
		
		templates.add(new TileCompositeFactory(3,2).addDoor(0, 0, Door.W).addDoor(0, 1, Door.W).addDoor(2, 0, Door.E).addDoor(2, 1, Door.E).getTc());
		
		new GUI();
		
	}
	
	public static DungeonBuilder getDungeonBuilder() {
		TileComposite startTile = new TileComposite(1,1);
		startTile.getTiles().getTiles()[0][0].getDoors().addDoor(Door.S);
		//TileComposite startTile = new TileComposite(2,2);
		//startTile.getTiles().getTiles()[1][1].getDoors().addDoor(Door.S);
		startTile.getFlags().add(TileFlag.START);
		startTile.changeCoordinates(0, 0);
		
		DungeonBuilder db = new DungeonBuilder().setMinTiles(16).setMaxTiles(16).addTileTemplates(templates).setStartTile(startTile).build();
		
		return db;
	}
	
	public class TileCompositeFactory {
		
		@Getter private TileComposite tc;
		
		public TileCompositeFactory(int sizeX, int sizeZ) {
			tc = new TileComposite(sizeX, sizeZ);
		}
		
		public TileCompositeFactory addDoor(int x, int z, Door door) {
			tc.getTiles().getTiles()[x][z].getDoors().addDoor(door);
			return this;
		}
		
		public TileCompositeFactory addDoor(int x, int z, Door... doors) {
			for (Door door : doors) {
				tc.getTiles().getTiles()[x][z].getDoors().addDoor(door);
			}
			return this;
		}		
	}
	
}
