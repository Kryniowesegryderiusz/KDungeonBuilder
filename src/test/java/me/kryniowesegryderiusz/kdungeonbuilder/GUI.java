package me.kryniowesegryderiusz.kdungeonbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.AllArgsConstructor;
import me.kryniowesegryderiusz.kdungeonbuilder.door.Door;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.Tile;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileComposite.TileFlag;
import me.kryniowesegryderiusz.kdungeonbuilder.tile.TileList;

public class GUI {
	
	public GUI () {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("Dungeon simulator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setMinimumSize(new Dimension(1000, 1000));
                
                //Display the window.
                frame.pack();
                frame.setVisible(true);
                
                frame.add(new PaintPanel());
            }
        });
	}
	
	public class PaintPanel extends JPanel {
		
		TileList composites;
		
		JButton btn = new JButton("Draw");
		
		public PaintPanel() {
			add(btn);
			//btn.setBounds(0, 0, 50, 40);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					composites = DungeonSim.getDungeonBuilder().getGeneratedTiles();
					repaint();
				}
			});
			
			composites = DungeonSim.getDungeonBuilder().getGeneratedTiles();
			repaint();
		}
		

		public int doorSize = 7;
		public int halfTile = (int) Math.ceil((double) TileComposite.STANDARD_LENGTH / 2);;

		
		
		@Override
		public void paint(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			
            g2.translate(0, getHeight());
            
            g2.scale(1, -1);
			
			g2.drawLine(500, 500, 1000, 1000);
			
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, 1000, 1000);

			Font font = new Font("TimesRoman", Font.PLAIN, 10);
			AffineTransform affineTransform = new AffineTransform();
			//affineTransform.translate(0, getHeight());
			//affineTransform.rotate(Math.toRadians(180), 0, 0);
			affineTransform.scale(1, -1);
			font = font.deriveFont(affineTransform);
			g2.setFont(font);
			
			g2.setColor(Color.BLUE);
			//drawPosition(g2, 0, 250);
			//drawPosition(g2, 0, 125);
			//drawPosition(g2, 250, 0);
			//drawPosition(g2, 125, 100);
			//drawPosition(g2, 0, 0);
			
			//System.out.println("Painting " + composites.size() + " tiles ");
			
			for (TileComposite tc : composites.getAll()) {
				
				int startX = tc.getCompositeCoordinates().getX();
				int startZ = tc.getCompositeCoordinates().getZ();
				
				//System.out.println("Painting Composite at " + startX + "," + startZ + " with sizes " + tc.getSizeX() + "x," + tc.getSizeZ()+"z");

				g2.setColor(Color.ORANGE);
				if (tc.getFlags().contains(TileFlag.START))
					g.setColor(Color.GREEN);
				if (tc.getFlags().contains(TileFlag.END))
					g.setColor(Color.RED);
				if (tc.getFlags().contains(TileFlag.ERR))
					g.setColor(Color.MAGENTA);
				if (tc.getFlags().contains(TileFlag.CLOSING))
					g.setColor(Color.GRAY);
				fillFixed(g2, startX, startZ, tc.getSizeX()*TileComposite.STANDARD_LENGTH, tc.getSizeZ()*TileComposite.STANDARD_LENGTH);

				
				g2.setColor(Color.YELLOW);
				for (Tile[] tt : tc.getTiles().getTiles()) {
					for (Tile t : tt) {
						//System.out.println("Drawing doors " + t.getDoors().getDoors() + " for relative " + t.getRelativeX() + ","+t.getRelativeZ());
						if (t.getDoors().contains(Door.N)) {
							int x = t.getCoordinates().getX() + halfTile - doorSize;
							int z = t.getCoordinates().getZ();
							int sizex = 2*doorSize+1;
							int sizez = doorSize;
									
							//System.out.println("Drawing S as " + x + " " + z + " " + sizex + " " + sizez);
							fillFixed(g2, x, z, sizex, sizez);
						}
						if (t.getDoors().contains(Door.S)) {
							int x = t.getCoordinates().getX() + halfTile - doorSize;
							int z = t.getCoordinates().getZ() + TileComposite.STANDARD_LENGTH - doorSize;
							int sizex = 2*doorSize+1;
							int sizez = doorSize;
									
							//System.out.println("Drawing S as " + x + " " + z + " " + sizex + " " + sizez);
							fillFixed(g2, x, z, sizex, sizez);
						}
						if (t.getDoors().contains(Door.E)) {
							int x = t.getCoordinates().getX() + TileComposite.STANDARD_LENGTH - doorSize;
							int z = t.getCoordinates().getZ() + halfTile - doorSize;
							int sizex = doorSize;
							int sizez = 2*doorSize+1;
									
							//System.out.println("Drawing S as " + x + " " + z + " " + sizex + " " + sizez);
							fillFixed(g2, x, z, sizex, sizez);
						}
						if (t.getDoors().contains(Door.W)) {
							int x = t.getCoordinates().getX();
							int z = t.getCoordinates().getZ() + halfTile - doorSize;
							int sizex = doorSize;
							int sizez = 2*doorSize+1;
									
							//System.out.println("Drawing S as " + x + " " + z + " " + sizex + " " + sizez);
							fillFixed(g2, x, z, sizex, sizez);
						}
					}
				}
				
				g.setColor(Color.BLUE);
				drawFixed(g2, startX, startZ, tc.getSizeX()*TileComposite.STANDARD_LENGTH, tc.getSizeZ()*TileComposite.STANDARD_LENGTH);

				
				/*
				if (d.doors.contains(Door.S)) {
					fillFixed(g, d.centerX - doorSize, startY, 2 * doorSize + 1, doorSize);
				}
				if (d.doors.contains(Door.N)) {
					fillFixed(g, d.centerX - doorSize, startY + size - doorSize, 2 * doorSize + 1, doorSize);
				}
				if (d.doors.contains(Door.W)) {
					fillFixed(g, startX, d.centerY - doorSize, doorSize, 2 * doorSize + 1);
				}
				if (d.doors.contains(Door.E)) {
					fillFixed(g, startX + size - doorSize, d.centerY - doorSize, doorSize, 2 * doorSize + 1);
				}
				*/
				
				
				g2.setColor(Color.BLACK);
				drawPosition(g2, startX, startZ);
				
				texts.add(new MapText(startX + (tc.getSizeX()*TileComposite.STANDARD_LENGTH/2), startZ + (tc.getSizeZ()*TileComposite.STANDARD_LENGTH/4), "no" + tc.getNo() + "w" + tc.getWave()));
				
				/*
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			
			for (MapText mt : texts) {
				drawText(g2, mt.x, mt.z, mt.text);
			}
			texts.clear();

		}
		
		private void fillFixed(Graphics2D g, int startX, int startY, int width, int height) {
			g.fillRect(startX+500, startY+500, width, height);
		}
		
		private void drawFixed(Graphics2D g, int startX, int startY, int width, int height) {
			g.drawRect(startX+500, startY+500, width, height);
		}
		
		private void drawPosition(Graphics2D g, int startX, int startY) {
			g.drawString(startX+","+startY, startX-10+500, startY+500);
		}
		
		private void drawText(Graphics2D g, int startX, int startY, String text) {
			g.drawString(text, startX-10+500, startY+500);
		}
	}
	
	ArrayList<MapText> texts = new ArrayList<MapText>();
	@AllArgsConstructor
	class MapText {
		int x;
		int z;
		String text;
	}

}
