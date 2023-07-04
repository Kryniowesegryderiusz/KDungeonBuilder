package me.kryniowesegryderiusz.kdungeonbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import lombok.Getter;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.CoordinatesList;
import me.kryniowesegryderiusz.kdungeonbuilder.coordinates.Coordinates;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.Tile;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileList;
import me.kryniowesegryderiusz.kdungeonbuilder.utils.RandomSelector;

public class DungeonBuilder {

	/*
	 * Settings
	 */

	private int minTiles = 10;
	private int maxTiles = 16;

	private ArrayList<TileComposite> tileTemplates = new ArrayList<TileComposite>();
	
	private TileComposite startTile;
	
	protected Random rand = new Random();

	public DungeonBuilder setMinTiles(int minTiles) {
		this.minTiles = minTiles;
		return this;
	}

	public DungeonBuilder setMaxTiles(int maxTiles) {
		this.maxTiles = maxTiles;
		return this;
	}

	public DungeonBuilder addTileTemplates(TileList tileTemplates) {
		this.tileTemplates.addAll(tileTemplates.createAllVariants().getAll());
		return this;
	}
	
	public DungeonBuilder setStartTile(TileComposite tile) {
		this.startTile = tile;
		return this;
	}
	
	public DungeonBuilder setSeed(long seed) {
		rand.setSeed(seed);
		if (isDebug()) logDebug("Setting seed to " + seed);
		return this;
	}

	/*
	 * Builder
	 */

	private int generatedAmount;

	@Getter private TileList generatedTiles;
	
	private TileList waveTiles;
	
	private int wave;

	public DungeonBuilder build() throws CantFitTileCompositeException {
		generatedTiles = new TileList();
		waveTiles = new TileList();
		generatedAmount = 0;
		wave = 0;
		
		generatedTiles.addTile(startTile);
		waveTiles.addTile(startTile);
		
		if (isDebug()) logDebug("DungeonBuilder prepared");
		
		doWave();
		
		return this;
	}
	
	TileList newWave = new TileList();
	
	private void doWave() throws CantFitTileCompositeException {
		if (isDebug()) logDebug("[--WAVE--] Creating wave " + wave);
		
		//Getting all tiles to fill and drawing matching TileComposite
		for (TileComposite composite : waveTiles.getAll()) {
			if (isDebug()) logDebug(composite.getCompositeCoordinates().toString() + ": Creating neighbours");
			for (Tile t : composite.getTiles().getBorderTiles()) {
				for (Door d : t.getDoors().getDoors()) {
					if (generatedAmount <= this.maxTiles) {
						Coordinates borderingCoordinates = t.getCoordinates().clone().moveByDoor(d);
						if (isDebug()) logDebug(composite.getCompositeCoordinates().toString() +": New border found: " +  borderingCoordinates.toString());
						if (generatedTiles.getByCoordinates(borderingCoordinates) == null && newWave.getByCoordinates(borderingCoordinates) == null) {
							if (isDebug()) logDebug(composite.getCompositeCoordinates().toString() +": Nothing yet at " +  borderingCoordinates.toString());
							TileComposite drawnComposite = drawTileComposite(borderingCoordinates, d, false);
							if (drawnComposite != null) {
								newWave.addTile(drawnComposite);
								generatedAmount++;
							}
						} else if (isDebug()) logDebug(composite.getCompositeCoordinates().toString() +": Tile already generated at " +  borderingCoordinates.toString());
					}
				}
			}
		}
		
		//Ending generation or restarting it
		if (newWave.getAll().isEmpty()) {
			if (isDebug()) logDebug("No new tiles created, ending generation");
			if (generatedTiles.getAll().size() < this.minTiles) {
				if (isDebug()) logDebug("Not enough tiles! Restarting!");
				build();
			}
			
			end();
			
			return;
		}
		
		//Preparing next wave if not ended
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
	 * @throws CantFitTileCompositeException 
	 */
	private TileComposite drawTileComposite(Coordinates coordinates, Door leadingDoor, boolean closing) throws CantFitTileCompositeException {
		
		if (isDebug()) logDebug(coordinates.toString() + ": Generating tile");
		
		if (generatedTiles.getByCoordinates(coordinates) != null) {
			if (isDebug()) logDebug(coordinates.toString() + ": Coordinates already generated! There is:" + generatedTiles.getByCoordinates(coordinates).toString());
			return null;
		}
		
		HashMap<TileComposite, Integer> possibilitiesWithWeights = new HashMap<TileComposite, Integer>();
		
		for (TileComposite template : tileTemplates) {
			
			if (isDebug()) logDebug(coordinates.toString() + ": 	Generating tile: Checking " + template.getId() + " " + template.toString());
			
			Coordinates coordinatesClone = coordinates.clone();
			
			//sprawdzanie kazdej mozliwosci wzgledem boku
			int moveNZLoop = 0;
			int moveNXLoop = 0;
			
			
			if (leadingDoor != null) {
				if (isDebug()) logDebug(coordinates.toString() + ": 		Generating tile: LeadingDoor check for " + leadingDoor);
				if (leadingDoor == Door.POSITIVE_X) {
					moveNZLoop = template.getSizeZ() - 1;
				} else if (leadingDoor == Door.POSITIVE_Z) {
					moveNXLoop = template.getSizeX() -1;
				} else if (leadingDoor == Door.NEGATIVE_X) {
					coordinatesClone.moveByDoor(leadingDoor, template.getSizeX()-1);
					moveNZLoop = template.getSizeZ() -1;
					if (isDebug()) logDebug(coordinates.toString() + ": 			Generating tile: Leading door " + leadingDoor + " " + coordinates.toString() + " -> " + coordinatesClone.toString() + " FOR " + template.toString());
				} else if (leadingDoor == Door.NEGATIVE_Z) {
					coordinatesClone.moveByDoor(leadingDoor, template.getSizeZ()-1);
					moveNXLoop = template.getSizeX() -1;
					if (isDebug()) logDebug(coordinates.toString() + ": 			Generating tile: Leading door " + leadingDoor + " " + coordinates.toString() + " -> " + coordinatesClone.toString() + " FOR " + template.toString());
				}
			}
			
			if (isDebug()) logDebug(coordinates.toString() + ": 		Generating tile: moveNZLoop-" + moveNZLoop+" moveNXLoop-"+moveNXLoop);
			
			for (int moveNZ = 0 ; moveNZ <= moveNZLoop; moveNZ++) {
				for (int moveNX = 0 ; moveNX <= moveNXLoop; moveNX++) {
					
					//testowy kompozyt w miejscu generowania
					TileComposite clone = template.clone();
					Coordinates finalCoordinates = coordinatesClone.clone();
					
					finalCoordinates.moveByDoor(Door.NEGATIVE_X, moveNX);
					finalCoordinates.moveByDoor(Door.NEGATIVE_Z, moveNZ);
					
					clone.changeCoordinates(finalCoordinates);
					
					boolean doReturn = false;
					
					if (isDebug()) logDebug(coordinates.toString() + ": 			Generating tile: Checking in:" + clone.toString());
					
					if (clone.getSizeX() > 1 || clone.getSizeZ() > 1) {
						if (isDebug()) logDebug(coordinates.toString() + ": 				Generating tile: Size check:" + clone.toString());
						//sprawdzenie, czy sie zmiesci
						tilesLoop:
						for (Tile[] tt : clone.getTiles().getTiles()) {
							for (Tile t : tt) {
								if (isDebug()) logDebug(coordinatesClone.toString() + ": 					Generating tile: Size check:" + clone.getCompositeCoordinates().toString() + ": Checking " + t.getCoordinates().toString());
								if (generatedTiles.getByCoordinates(t.getCoordinates()) != null || newWave.getByCoordinates(t.getCoordinates()) != null) {
									if (isDebug()) logDebug(t.getCoordinates().toString() + ": 					Generating tile: Size check: " + clone.getSizeX() + "x" + clone.getSizeZ() + " sie nie zmiesci");
									doReturn = true;
									break tilesLoop;
								}
							}
						}
					}
					
					if (doReturn)
						continue;
					
					if (isDebug()) logDebug(coordinates.toString() + ": 				Generating tile: Door check: " + clone.toString());
					//sprawdzanie, czy sie zgadzają drzwi
					doorCheckLoop:
					for(Tile borderTile : clone.getTiles().getBorderTiles()) {
						//dla kazdego elementu sprawdzam po kazdej stronie co jes
						for (Door side : Door.values()) {
							Coordinates sideCoordinates = borderTile.getCoordinates().clone().moveByDoor(side);
							
							if (isDebug()) logDebug(coordinates.toString() + ": 					Generating tile: Door check: Side:" + side + " | sideCoordinates: " + sideCoordinates + " | borderTile: " + borderTile.toString());
							
							//sprawdzamy tylko tilesy, ktore nie wchodzą w skład kompozytu
							if (clone.getTiles().getTile(sideCoordinates) == null) {

								TileComposite neighbourComposite = generatedTiles.getByCoordinates(sideCoordinates);
								if (neighbourComposite == null)
									neighbourComposite = newWave.getByCoordinates(sideCoordinates);
								
								//sprawdzamy tylko, jesli mamy sasiada
								if (neighbourComposite != null) {
									Tile neighbour = neighbourComposite.getTiles().getTile(sideCoordinates);
									
									if (neighbour != null) {
										if (borderTile.getDoors().contains(side) && !neighbour.getDoors().contains(side.getOpposite())) {
											doReturn = true;
											if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + ": " + borderTile.getCoordinates().toString() + " " + side + " ma drzwi, a " + neighbour.getCoordinates().toString() + " nie ma " + side.getOpposite());
											break doorCheckLoop;
										}
										if (!borderTile.getDoors().contains(side) && neighbour.getDoors().contains(side.getOpposite())) {
											doReturn = true;
											if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + ": " + borderTile.getCoordinates().toString() + " " + side + " nie ma drzwi, a " + neighbour.getCoordinates().toString() + " ma " + side.getOpposite());
											break doorCheckLoop;
										}										
										if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + " drzwi gituwa z " + borderTile.getCoordinates().toString());
									} else if (isDebug()) logDebug(coordinates.toString() + ": 							Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma sasiada (tile) ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROR TAK NIE POWINNO BYĆ CHUJ W DUPIE LZLALALALALA!");
								} else {
									if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma sasiada (kompozyt)!");
									if (closing && borderTile.getDoors().contains(side)) {
										if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + " nie ma sasiada (kompozyt), a ma drzwi, a to CLOSING, to odpada!");
										doReturn = true;
										break doorCheckLoop;
									}
								}
							} else if (isDebug()) logDebug(coordinates.toString() + ": 						Generating tile: Door check:" + borderTile.getCoordinates().toString() + ": " + sideCoordinates.toString() + " jest wewnatrz!");

							
						}
						
					}
					
					if (doReturn)
						continue;		
					
					weightByTileCompositeID.putIfAbsent(clone.getId(), clone.getWeight());
					possibilitiesWithWeights.put(clone, weightByTileCompositeID.get(clone.getId()));
					
				}
			}
		}
		
		TileComposite drawn;
		
		if (possibilitiesWithWeights.size() != 0) {

			if (isDebug()) logDebug(coordinates.toString() + ": Generating tile: Weights map: " + possibilitiesWithWeights);
			drawn = RandomSelector.weighted(possibilitiesWithWeights.keySet(), s -> possibilitiesWithWeights.get(s)).next(rand);
			weightByTileCompositeID.put(drawn.getId(), (int) Math.ceil((double) weightByTileCompositeID.get(drawn.getId()) / 2));
			
		} else {
			throw new CantFitTileCompositeException("Cannot fit any possible TileComposite to " + coordinates.toString());
		}
		
		drawn.setNo(generatedAmount+1);
		drawn.setWave(wave);
		
		if (closing)
			drawn.getFlags().add(TileFlag.CLOSING);
		
		if (isDebug()) logDebug(coordinates.toString() + ": Generating tile: Drawn: " + drawn.toString() + " No. " + drawn.getNo() + " Wave: " + wave);
		
		return drawn;
	}
	
	private HashMap<String, Integer> weightByTileCompositeID = new HashMap<String, Integer>();
	
	
	private boolean ended = false;
	
	private void end() throws CantFitTileCompositeException {
		
		if (ended) {
			if (isDebug()) logDebug("DungeonBuilder already closed");
			return;
		}
		
		if (isDebug()) logDebug("Closing");
		
		CoordinatesList freeTiles = new CoordinatesList();
		
		for (TileComposite generatedComposite : generatedTiles.getAll()) {
			for (Tile borderTile : generatedComposite.getTiles().getBorderTiles()) {
				for (Door door : borderTile.getDoors().getDoors()) {
					Coordinates sideCoordinates = borderTile.getCoordinates().clone().moveByDoor(door);
					if (generatedTiles.getByCoordinates(sideCoordinates) == null) {
						freeTiles.addCoordinate(sideCoordinates);
						if (isDebug()) logDebug("Closing: " + sideCoordinates.toString() + " needs closure!");
					}
				}
			}
		}
		
		if (isDebug()) logDebug("Closing: " + freeTiles.getAll().size() + " coordinates needs closure!");
		
		wave = -10;
		for (Coordinates tc : freeTiles.getAll()) {
			generatedAmount++;
			TileComposite composite = this.drawTileComposite(tc, null, true);
			if (composite != null)
				generatedTiles.addTile(composite);
		}
		
		ended = true;
		
	}
	
	public boolean isDebug() {
		return true;
	}
	
	public void logDebug(String debug) {
		System.out.println(debug);
	}
	
	public class CantFitTileCompositeException extends Exception {
		public CantFitTileCompositeException(String string) {
			// TODO Auto-generated constructor stub
		}

		private static final long serialVersionUID = 1L;
	}

}
