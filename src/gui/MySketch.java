package gui;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.Color;
import java.io.File;

public class MySketch extends PApplet {

    /**
     * 
     */
    private static final long serialVersionUID = -1530381177827393671L;
    public static final int OUTPUT_SIZE=256;
    public static final int DEFAULT_SIZE =256 ;
    public static final int  BUTTON_WIDTH=50;
    public static final int  BUTTON_HEIGHT=20;
    public static final int PADDING=50;
    private int frameMode=0;
    MainFrame mainFrame;
    public void setup() {
    	mainFrame=new MainFrame(this);
    	final int width=DEFAULT_SIZE*4+PADDING*5;
    	final int height=DEFAULT_SIZE*2+PADDING*6;
        size(width, 768);
        mainFrame.setup();
    }

    public void draw() {
    	mainFrame.draw();
    }

    public void mousePressed() {
    	mainFrame.onMouseClick(mouseX, mouseY);
    	}
    public void fileSelected(File selectFile){
		mainFrame.fileSelected(selectFile);
	}
    public static void main(final String args[]) {
        PApplet.main(new String[] {
                "--present", "gui.MySketch"
        });
    }
}
