package com.apprisingsoftware.game2048.graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Applet extends Frame {
	
	static int size = 4;
	static int boxSize = 100;
	static int boxSpacing = 20;
	static int boxInterval = boxSize + boxSpacing;
	static int borderSize = 100;
	static int autoplayDelay = 20;
	
	static Font font = new Font("Dialog", Font.BOLD, 30);
	static Color failureColor = new Color(255, 150, 150);
	
	transient GameManager game;
	
	public static void main(String[] args) {
		Applet f = new Applet();
		f.setVisible(true);
	}
	
	public Applet() {
		this.setSize(borderSize*2 + boxInterval*size - boxSpacing,
				borderSize*2 + boxInterval*size - boxSpacing);
		
		JPanel panel = new JPanel();
		
		final class KeyAction extends AbstractAction {
			private Button button;
			public KeyAction(Button button) {
				super();
				this.button = button;
			}
			@Override public void actionPerformed(ActionEvent event) {
				game.playGame(button);
				repaint();
			}
		}
		
		Button[] buttons = {Button.LEFT, Button.RIGHT, Button.UP, Button.DOWN,
				Button.R, Button.H, Button.A, Button.P, Button.T};
		for (Button button : buttons) {
			panel.getInputMap().put(KeyStroke.getKeyStroke(button.name), button.name);
			panel.getActionMap().put(button.name, new KeyAction(button));
		}
		
		panel.setFocusable(true);
		
		this.add(panel);
		
		game = new GameManager(size, this);
	}
	
	@Override public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		if (game.getBoard().isValid()) g.setColor(Color.WHITE);
		else g.setColor(failureColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				int x = borderSize + boxInterval*i;
				int y = borderSize + boxInterval*j;
				int tile = game.getBoard().getTile(i, j);
				
				g.setColor(game.getBoard().colors[i][j]);
				g.fillRect(x, y, boxSize, boxSize);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, boxSize, boxSize);
				if (tile != 0) {
					drawText(g2, new Pos(x + boxSize/2, y + boxSize/2), Integer.toString(tile));
				}
			}
		}
		
		g.setColor(Color.BLACK);
		drawText(g2, new Pos((int)(getWidth()*3/4.0), borderSize/2), Integer.toString(game.getBoard().getScore()));
		
		if (game.getBoard().isValid()) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					game.doAutoplayIfEnabled();
				}
			}, autoplayDelay);
		}
		
	}
	
	public void drawText(Graphics2D g2, Pos center, String text) {
		g2.setColor(Color.BLACK);
		g2.setFont(font);
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, text);
		Rectangle rect = gv.getPixelBounds(null, 0, 0);
		int length = rect.width;
		int height = rect.height;
		g2.drawString(text, center.x-length/2, center.y+height/2);
	}
	
}
