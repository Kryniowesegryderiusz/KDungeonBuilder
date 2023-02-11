package me.kryniowesegryderiusz.kdungeonbuilder;

import java.util.ArrayList;

import lombok.Getter;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileList;

public class DungeonSim {
	
	static TileList templates = new TileList();

	public DungeonSim() {
		
		//pojedyncze
		templates.addTile(new TileComposite(1,1).addDoor(0, 0, Door.S).setRotatable(true));
		
		templates.addTile(new TileComposite(1,1).addDoor(0, 0, Door.N, Door.W).setRotatable(true));
		
		templates.addTile(new TileComposite(1,1).addDoor(0, 0, Door.N, Door.S).setRotatable(true));
		
		templates.addTile(new TileComposite(1,1).addDoor(0, 0, Door.N, Door.S, Door.W).setRotatable(true));

		templates.addTile(new TileComposite(1,1).addDoor(0, 0, Door.N, Door.S, Door.W, Door.E));
		
		//dlugie
		templates.addTile(new TileComposite(1,2).addDoor(0, 0, Door.N).addDoor(0, 1, Door.S).setRotatable(true));
		templates.addTile(new TileComposite(1,3).addDoor(0, 0, Door.N).addDoor(0, 2, Door.S).setRotatable(true));
		
		//inne
		templates.addTile(new TileComposite(2,3).addDoor(0, 0, Door.W, Door.N).addDoor(1, 2, Door.E, Door.S).setRotatable(true));
		templates.addTile(new TileComposite(2,2).addDoor(0, 0, Door.N).addDoor(1, 1, Door.E, Door.S).setRotatable(true));
		templates.addTile(new TileComposite(2,2).addDoor(0, 0, Door.N).addDoor(1, 1, Door.E).setRotatable(true));
		
		templates.addTile(new TileComposite(3,2).addDoor(0, 0, Door.W).addDoor(0, 1, Door.W).addDoor(2, 0, Door.E).addDoor(2, 1, Door.E).setRotatable(true));
		
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
	
}
