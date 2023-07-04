package me.kryniowesegryderiusz.kdungeonbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileList;

public class DungeonSim {
	
	static TileList templates = new TileList();
	
	static DungeonSim instance;

	public DungeonSim() {
		
		instance = this;
		
		//pojedyncze
		addTiles(9, new TileComposite().init("1x1-A", 1, 1, 10).addDoor(0, 0, Door.POSITIVE_X).setRotatable(true));
		
		addTiles(3, new TileComposite().init("1x1-B", 1, 1, 10).addDoor(0, 0, Door.POSITIVE_X, Door.POSITIVE_Z).setRotatable(true));
		
		addTiles(3, new TileComposite().init("1x1-C", 1, 1, 10).addDoor(0, 0, Door.POSITIVE_X, Door.POSITIVE_Z, Door.NEGATIVE_X).setRotatable(true));

		addTiles(3, new TileComposite().init("1x1-D", 1, 1, 10).addDoor(0, 0, Door.POSITIVE_X, Door.POSITIVE_Z, Door.NEGATIVE_X, Door.NEGATIVE_Z));
		
		addTiles(3, new TileComposite().init("1x1-E", 1, 1, 10).addDoor(0, 0, Door.POSITIVE_X, Door.NEGATIVE_X));
		
		//1x2
		
		addTiles(2, new TileComposite().init("1x2-A", 1, 2, 1000).addDoor(0, 0, Door.NEGATIVE_X).addDoor(0, 1, Door.POSITIVE_X).setRotatable(true));
		addTiles(2, new TileComposite().init("1x2-B", 1, 2, 1000).addDoor(0, 0, Door.NEGATIVE_Z).addDoor(0, 1, Door.NEGATIVE_Z).setRotatable(true));
		addTiles(1, new TileComposite().init("1x2-C", 1, 2, 1000).addDoor(0, 0, Door.NEGATIVE_X, Door.NEGATIVE_Z).addDoor(0, 1, Door.POSITIVE_Z, Door.POSITIVE_X).setRotatable(true));
		
		//1x3
		
		addTiles(2, new TileComposite().init("1x3-A", 1, 3, 1000).addDoor(0, 0, Door.NEGATIVE_X).addDoor(0, 2, Door.POSITIVE_X).setRotatable(true));
		addTiles(2, new TileComposite().init("1x3-B", 1, 3, 1000).addDoor(0, 0, Door.NEGATIVE_X).addDoor(0, 1, Door.POSITIVE_Z, Door.NEGATIVE_Z).addDoor(0, 2, Door.POSITIVE_X).setRotatable(true));
		
		//2x2
		
		addTiles(1, new TileComposite().init("2x2-A", 2, 2, 1000).addDoor(0, 0, Door.NEGATIVE_X).addDoor(1, 0, Door.NEGATIVE_X).addDoor(0, 1, Door.POSITIVE_X).addDoor(1, 1, Door.POSITIVE_X).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-B", 2, 2, 1000).addDoor(0, 0, Door.NEGATIVE_X, Door.NEGATIVE_Z).addDoor(1, 1, Door.POSITIVE_X, Door.POSITIVE_Z).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-C", 2, 2, 1000).addDoor(0, 0, Door.NEGATIVE_X, Door.NEGATIVE_Z).addDoor(0, 1, Door.POSITIVE_X, Door.NEGATIVE_Z).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-D", 2, 2, 1000).addDoor(0, 0, Door.NEGATIVE_Z).addDoor(1, 1, Door.POSITIVE_Z).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-E", 2, 2, 1000).addDoor(0, 1, Door.NEGATIVE_Z).addDoor(1, 0, Door.POSITIVE_Z).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-F", 2, 2, 1000).addDoor(0, 1, Door.POSITIVE_X).addDoor(1, 1, Door.POSITIVE_X).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-G", 2, 2, 1000).addDoor(0, 1, Door.POSITIVE_X).setRotatable(true));
		addTiles(1, new TileComposite().init("2x2-H", 2, 2, 1000).addDoor(1, 1, Door.POSITIVE_X).setRotatable(true));
		addTiles(2, new TileComposite().init("2x2-I", 2, 2, 1000).addDoor(0, 0, Door.NEGATIVE_Z).addDoor(1, 1, Door.POSITIVE_X).setRotatable(true));

		new GUI();
		
	}
	
	public static void addTiles(int amount, TileComposite tc) {
		for (int i = 0; i < amount; i++) {
			templates.addTile(tc.clone().setId(tc.getId()+"-"+i));
		}
	}
	
	public static DungeonBuilder getDungeonBuilder() {
		TileComposite startTile = new TileComposite().init("3x3-start", 3, 3, 0);
		startTile.getTiles().getTiles()[1][2].getDoors().addDoor(Door.POSITIVE_X);
		//TileComposite startTile = new TileComposite(2,2);
		//startTile.getTiles().getTiles()[1][1].getDoors().addDoor(Door.S);
		startTile.getFlags().add(TileFlag.START);
		startTile.changeCoordinates(0, 0);
		
		return instance.new DungeonBuilderTest().setMinTiles(16).setMaxTiles(16).addTileTemplates(templates).setStartTile(startTile);
	}
	
	public class DungeonBuilderTest extends DungeonBuilder {
		
		File f = new File(new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss-SSS").format(new Date()) + ".log");
		BufferedWriter writer;
		
		public DungeonBuilderTest() {
			super();
			
			try {
				System.out.println(f.getName());
				f.createNewFile();
				writer = new BufferedWriter(new FileWriter(f, true));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			setSeed(1686110511895L);
			
		}
		
		@Override
		public void logDebug(String debug) {
			//System.out.println(debug);
			
			try {
				writer.newLine();
				writer.write(debug);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
