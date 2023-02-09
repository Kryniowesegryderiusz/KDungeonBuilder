package me.kryniowesegryderiusz.kdungeonbuilder;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.CoordinatesList;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.Coordinates;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.Tile;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileList;

public class DungeonBuilder {

	/*
	 * Settings
	 */

	private int minTiles = 10;
	private int maxTiles = 16;

	private ArrayList<TileComposite> tileTemplates = new ArrayList<TileComposite>();
	
	private TileComposite startTile;

	public DungeonBuilder setMinTiles(int minTiles) {
		this.minTiles = minTiles;
		return this;
	}

	public DungeonBuilder setMaxTiles(int maxTiles) {
		this.maxTiles = maxTiles;
		return this;
	}

	public DungeonBuilder addTileTemplates(ArrayList<TileComposite> tileTemplates) {
		this.tileTemplates.addAll(tileTemplates);
		return this;
	}
	
	public DungeonBuilder setStartTile(TileComposite tile) {
		this.startTile = tile;
		return this;
	}

	/*
	 * Builder
	 */

	private int generatedAmount;

	@Getter private TileList generatedTiles;
	
	private TileList waveTiles;
	
	private int wave;

	public DungeonBuilder build() {
		generatedTiles = new TileList();
		waveTiles = new TileList();
		generatedAmount = 0;
		wave = 0;
		
		generatedTiles.addTile(startTile);
		waveTiles.addTile(startTile);
		
		logDebug("DungeonBuilder prepared");
		
		doWave();
		
		return this;
	}
	
	TileList newWave = new TileList();
	
	public void doWave() {
		logDebug("[--WAVE--] Creating wave " + wave);
		for (TileComposite composite : waveTiles.getAll()) {
			logDebug(composite.getCompositeCoordinates().toString() + ": Creating neighbours");
			for (Tile t : composite.getTiles().getBorderTiles()) {
				for (Door d : t.getDoors().getDoors()) {
					if (generatedAmount <= this.maxTiles) {
						Coordinates borderingCoordinates = t.getCoordinates().clone().moveByDoor(d);
						logDebug(composite.getCompositeCoordinates().toString() +": New border found: " +  borderingCoordinates.toString());
						if (generatedTiles.getByCoordinates(borderingCoordinates) == null && newWave.getByCoordinates(borderingCoordinates) == null) {
							logDebug(composite.getCompositeCoordinates().toString() +": Nothing yet at " +  borderingCoordinates.toString());
							TileComposite drawnComposite = drawTileComposite(borderingCoordinates, d, false);
							newWave.addTile(drawnComposite);
							generatedAmount++;
						} else logDebug(composite.getCompositeCoordinates().toString() +": Tile already generated at " +  borderingCoordinates.toString());
					}
				}
			}
		}
		
		if (newWave.getAll().isEmpty()) {
			logDebug("No new tiles created, ending generation");
			if (generatedTiles.getAll().size() < this.minTiles) {
				logDebug("Not enough tiles! Restarting!");
				build();
			}
			
			end();
			
			return;
		}
		
		generatedTiles.addTiles(newWave.getAll());
		waveTiles = newWave;
		newWave = new TileList();
		wave++;
		doWave();
		
	}

	/**
	 * Draws composite for position
	 * @param coordinates - coordinates for generating tile
	 * @param leadingDoor - if null tiles with size higher than 1x1 wont be adjusted (basically its eliminating this)
	 * @param closing - should it leave doors open for new tiles
	 * @return
	 */
	public TileComposite drawTileComposite(Coordinates coordinates, Door leadingDoor, boolean closing) {
		
		logDebug(coordinates.toString() + ": Generating tile");
		
		ArrayList<TileComposite> possibilities = new ArrayList<TileComposite>();
		
		for (TileComposite template : tileTemplates) {
			
			logDebug(coordinates.toString() + ": Generating tile: CHECKING " + template.toString());
			
			Coordinates coordinatesClone = coordinates.clone();
			
			//sprawdzanie kazdej mozliwosci wzgledem boku
			int moveNLoop = 0;
			int moveWLoop = 0;
			
			if (leadingDoor != null) {
				if (leadingDoor == Door.N) {
					coordinatesClone.moveByDoor(leadingDoor, (template.getSizeZ()-1));
					moveWLoop = template.getSizeX()-1;
					logDebug(coordinates.toString() + ": Generating tile: Leading door N " + coordinates.toString() + " -> " + coordinatesClone.toString() + " FOR " + template.toString());
				}
				
				if (leadingDoor == Door.S) {
					moveWLoop = template.getSizeX()-1;
				}
				
				if (leadingDoor == Door.W) {
					coordinatesClone.moveByDoor(leadingDoor, (template.getSizeX()-1));
					moveNLoop = template.getSizeZ()-1;
					logDebug(coordinates.toString() + ": Generating tile: Leading door W " + coordinates.toString() + " -> " + coordinatesClone.toString() + " FOR " + template.toString());
				}
				
				if (leadingDoor == Door.E) {
					moveNLoop = template.getSizeZ()-1;
				}
			}
			
			logDebug(coordinates.toString() + ": Generating tile: moveNLoop-" + moveNLoop+" moveWLoop-"+moveWLoop);
			
			for (int moveN = 0 ; moveN <= moveNLoop; moveN++) {
				for (int moveW = 0 ; moveW <= moveWLoop; moveW++) {
					
					//testowy kompozyt w miejscu generowania
					TileComposite clone = template.clone();
					Coordinates finalCoordinates = coordinatesClone.clone();
					
					finalCoordinates.moveByDoor(Door.N, moveN);
					finalCoordinates.moveByDoor(Door.W, moveW);
					
					clone.changeCoordinates(finalCoordinates);
					
					boolean doReturn = false;
					
					logDebug(coordinates.toString() + ": Generating tile: Checking in:" + clone.toString());
					
					if (clone.getSizeX() > 1 || clone.getSizeZ() > 1) {
						logDebug(coordinates.toString() + ": Generating tile: Size check:" + clone.toString());
						//sprawdzenie, czy sie zmiesci
						//nie dziala do kladnie tak, jakby moglo, bo nie sa przesuwane te duże
						tilesLoop:
						for (Tile[] tt : clone.getTiles().getTiles()) {
							for (Tile t : tt) {
								logDebug(coordinatesClone.toString() + ": Generating tile: Size check:" + clone.getCompositeCoordinates().toString() + ": Checking " + t.getCoordinates().toString());
								if (generatedTiles.getByCoordinates(t.getCoordinates()) != null || newWave.getByCoordinates(t.getCoordinates()) != null) {
									logDebug(t.getCoordinates().toString() + ": Generating tile:  Size check:" + clone.getSizeX() + " " + clone.getSizeZ() + " sie nie zmiesci");
									doReturn = true;
									break tilesLoop;
								}
							}
						}
					}
					
					if (doReturn)
						continue;
					
					logDebug(coordinates.toString() + ": Generating tile: Door check: " + clone.toString());
					//sprawdzanie, czy sie zgadzają drzwi
					doorCheckLoop:
					for(Tile t : clone.getTiles().getBorderTiles()) {
						//dla kazdego elementu sprawdzam po kazdej stronie co jes
						for (Door side : Door.values()) {
							Coordinates sideCoordinates = t.getCoordinates().clone().moveByDoor(side);
							
							//sprawdzamy tylko tilesy, ktore nie wchodzą w skład kompozytu
							if (clone.getTiles().getTile(sideCoordinates) == null) {

								TileComposite neighbourComposite = generatedTiles.getByCoordinates(sideCoordinates);
								if (neighbourComposite == null)
									neighbourComposite = newWave.getByCoordinates(sideCoordinates);
								
								//sprawdzamy tylko, jesli mamy sasiada
								if (neighbourComposite != null) {
									Tile neighbour = neighbourComposite.getTiles().getTile(sideCoordinates);
									
									if (neighbour != null) {
										if (t.getDoors().contains(side) && !neighbour.getDoors().contains(side.getOpposite())) {
											doReturn = true;
											logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + ": " + t.getCoordinates().toString() + " " + side + " ma drzwi, a " + neighbour.getCoordinates().toString() + " nie ma " + side.getOpposite());
											break doorCheckLoop;
										}
										if (!t.getDoors().contains(side) && neighbour.getDoors().contains(side.getOpposite())) {
											doReturn = true;
											logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + ": " + t.getCoordinates().toString() + " " + side + " nie ma drzwi, a " + neighbour.getCoordinates().toString() + " ma " + side.getOpposite());
											break doorCheckLoop;
										}										
										logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + " drzwi gituwa z " + t.getCoordinates().toString());
									} else logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma siada (tile) ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROR TAK NIE POWINNO BYĆ CHUJ W DUPIE LZLALALALALA!");
								} else {
									if (!closing)
										logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma siada (kompozyt)!");
									else {
										if (t.getDoors().contains(side)) {
											logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma siada (kompozyt), a ma drzwi, a to CLOSING, to odpada!");
											doReturn = true;
											break doorCheckLoop;
										}
									}
								}
							} else logDebug(coordinates.toString() + ": Generating tile: Door check:" + t.getCoordinates().toString() + ": " + sideCoordinates.toString() + " jest wewnatrz!");

							
						}
						
					}
					
					if (doReturn)
						continue;		
					
					possibilities.add(clone);
					
				}
			}
		}
		
		TileComposite drawn;
		
		if (possibilities.size() != 0) {
			
			int amnt=0;
			for (TileComposite pos : possibilities) {
				if (pos.getSizeX() > 1 || pos.getSizeZ() > 1) {
					amnt++;
				}
			}
			logDebug(coordinates.toString() + ": Generating tile: W losowaniu było " + amnt + " dużych tilesów");
			drawn = possibilities.get(new Random().nextInt(possibilities.size())).clone();
		} else {
			drawn = new TileComposite(1,1);
			drawn.getFlags().add(TileFlag.ERR);
		}
		
		drawn.setNo(generatedAmount+1);
		drawn.setWave(wave);
		
		if (closing)
			drawn.getFlags().add(TileFlag.CLOSING);
		
		logDebug(coordinates.toString() + ": Generating tile: Drawn: " + drawn.toString() + " No. " + drawn.getNo() + " Wave: " + wave);
		
		return drawn;
	}
	
	
	
	boolean ended = false;
	
	public void end() {
		
		if (ended) {
			logDebug("DungeonBuilder already closed");
			return;
		}
		
		logDebug("Closing");
		
		CoordinatesList freeTiles = new CoordinatesList();
		
		for (TileComposite generatedComposite : generatedTiles.getAll()) {
			for (Tile borderTile : generatedComposite.getTiles().getBorderTiles()) {
				for (Door door : borderTile.getDoors().getDoors()) {
					Coordinates sideCoordinates = borderTile.getCoordinates().clone().moveByDoor(door);
					if (generatedTiles.getByCoordinates(sideCoordinates) == null) {
						freeTiles.addCoordinate(sideCoordinates);
						logDebug("Closing: " + sideCoordinates.toString() + " needs closure!");
					}
				}
			}
		}
		
		logDebug("Closing: " + freeTiles.getAll().size() + " coordinates needs closure!");
		
		wave = -10;
		for (Coordinates tc : freeTiles.getAll()) {
			generatedTiles.addTile(this.drawTileComposite(tc, null, true));
		}
		
		ended = true;
		
	}
	
	public void logDebug(String debug) {
		System.out.println(debug);
	}

}
