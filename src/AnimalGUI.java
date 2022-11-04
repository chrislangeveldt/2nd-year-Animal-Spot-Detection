import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Run this class to enter GUI mode: java AnimalGUI
 *
 * 
 * @author Chris Langeveldt
 */
public class AnimalGUI {

  /**
   * Creates a button on canvas with given information
   * 
   * @param x The x coordinate of the button
   * @param y The y coordinate of the button
   * @param halfWidth Half the width of the button
   * @param halfHeight Half the height of the button
   * @param text The text on the button
   * @param cl The color of the text
   */
  private static void createButton(double x, double y, double halfWidth,
      double halfHeight, String text, Color cl) {
    if (!cl.equals(Color.WHITE)) {
      StdDraw.setPenColor(75, 75, 75);
    } else {
      StdDraw.setPenColor(90, 90, 90);
    }
    StdDraw.filledRectangle(x, y, halfWidth, halfHeight);
    StdDraw.setPenColor(75, 75, 75);
    StdDraw.filledRectangle(x, y, halfWidth - 0.5, halfHeight - 0.5);
    StdDraw.setPenColor(cl);
    StdDraw.text(x, y, text);
  }

  /**
   * Return the positive number entered or -1 for back button
   * 
   * @param min The minimum bound for the value to be entered
   * @param max The maximum bound for the value to be entered
   * @param prompt The text that asks for certain type of value
   * @return the positive number entered or -1 for back button
   */
  private static int numberPrompt(int min, int max, String prompt) {
    StdDraw.clear(Color.DARK_GRAY);
    StdDraw.setPenColor(Color.WHITE);
    StdDraw.text(50, 65, prompt);
    StdDraw.setPenColor(Color.WHITE);
    StdDraw.filledRectangle(50, 50, 10, 5);
    createButton(75, 50, 10, 5, "Enter", Color.WHITE);
    createButton(50, 35, 10, 5, "Clear", Color.WHITE);
    createButton(11, 6, 10, 5, "Back", Color.WHITE);
    StdDraw.show();

    boolean[] isRed = new boolean[3];
    String eps = "";
    while (true) {
      while (!StdDraw.hasNextKeyTyped()) {
        for (int i = 0; i < 3; i++) { // Buttons
          int x = 75;
          int y = 50;
          String str = "Enter";
          if (i == 1) {
            x = 50;
            y = 35;
            str = "Clear";
          } else if (i == 2) {
            x = 11;
            y = 6;
            str = "Back";
          }

          boolean mouseX = StdDraw.mouseX() > x - 10 
              && StdDraw.mouseX() < x + 10;
          boolean mouseY = StdDraw.mouseY() > y - 5 
              && StdDraw.mouseY() < y + 5;
          if (mouseX && mouseY) {
            if (!isRed[i]) {
              createButton(x, y, 10, 5, str, Color.RED);
              StdDraw.show();
              isRed[i] = true;
            }
            if (StdDraw.isMousePressed()) {
              StdDraw.isMousePressed = false;
              StdDraw.mouseX = 0;
              StdDraw.mouseY = 0;
              if (i == 0) { // Enter
                if (eps.length() > 0) {
                  int temp = Integer.parseInt(eps);
                  if (temp >= min && temp <= max) {
                    return temp;
                  } else {
                    eps = "";
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.filledRectangle(50, 50, 10, 5);
                    StdDraw.setPenColor(Color.RED);
                    StdDraw.text(50, 50, "[" + min + ", " + max + "]");
                  }
                }
              } else if (i == 1) { // Clear
                eps = "";
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.filledRectangle(50, 50, 10, 5);
              } else { // Back
                return -1;
              }
            } else {
              StdDraw.isMousePressed = false;
            }
          } else if (isRed[i]) {
            createButton(x, y, 10, 5, str, Color.WHITE);
            StdDraw.show();
            isRed[i] = false;
          }
        }
      }
      char key = StdDraw.nextKeyTyped();
      if (Character.isDigit(key)) {
        int temp = Integer.parseInt(eps + key);
        if (temp > 0 && temp < 1000) {
          eps = Integer.toString(temp);
          StdDraw.setPenColor(Color.WHITE);
          StdDraw.filledRectangle(50, 50, 10, 5);
          StdDraw.setPenColor();
          StdDraw.text(50, 50, eps);
          StdDraw.show();
        }
      }
    }
  }

  /**
   * This is the menu where the mode is chosen and the corresponding image is
   * shown
   * 
   * @param original The original picture
   */
  private static void chooseScreen(Picture original) {
    StdDraw.clear(Color.DARK_GRAY);
    StdDraw.setPenColor(Color.WHITE);
    StdDraw.text(50, 90, "Choose Mode:");
    String[] str = {"Original", "Grey Scale", "Noise Reduction",
        "Edge Detection", "Spot Detection", "Back"};
    for (int i = 0; i < 5; i++) {
      int y = 75 - i * 15;
      createButton(50, y, 20, 5, str[i], Color.WHITE);
    }
    createButton(11, 6, 10, 5, "Back", Color.WHITE);
    StdDraw.show();

    boolean[] isRed = new boolean[6];
    while (true) {
      for (int i = 0; i < 6; i++) {
        int y = 75 - i * 15;
        int x = 50;
        int halfWidth = 20;
        if (i == 5) {
          x = 11;
          y = 6;
          halfWidth = 10;
        }
        boolean mouseX = StdDraw.mouseX() > x - halfWidth 
            && StdDraw.mouseX() < x + halfWidth;
        boolean mouseY = StdDraw.mouseY() > y - 5 && StdDraw.mouseY() < y + 5;
        if (mouseX && mouseY) {
          if (!isRed[i]) {
            createButton(x, y, halfWidth, 5, str[i], Color.RED);
            StdDraw.show();
            isRed[i] = true;
          }
          if (StdDraw.isMousePressed()) {
            StdDraw.isMousePressed = false;
            StdDraw.mouseX = 0;
            StdDraw.mouseY = 0;
            Picture pic = new Picture(original);
            int[][] picInt = new int[pic.width()][pic.height()];
            boolean[][] picBool = new boolean[pic.width()][pic.height()];
            if (i == 5) { // Back
              startupScreen();
            }
            if (i >= 1) {
              picInt = Animal.greyScale(pic);
              pic = Animal.toPicture(picInt);
            }
            if (i >= 2) {
              picInt = Animal.noiseReduce(picInt);
              pic = Animal.toPicture(picInt);
            }
            int eps = 0;
            if (i >= 3) {
              eps = numberPrompt(1, 999, "Enter Epsilon Value:");
              if (eps == -1) { // Back
                chooseScreen(original);
              } else {
                picBool = Animal.edgeDetect(picInt, eps);
                pic = Animal.toPicture(picBool);
              }
            }
            if (i == 4) {
              int low = 0;
              low = numberPrompt(4, 11, "Enter Lower Limit For Radius:");
              if (low == -1) { // Back
                chooseScreen(original);
              }
              int up = 0;
              up = numberPrompt(low, 11, "Enter Upper Limit For Radius:");
              if (up == -1) { // Back
                chooseScreen(original);
              }
              picBool = SpotDetection.detectSpots(picBool, low, up);
              int count = SpotDetection.getCount();
              pic = Animal.toPicture(picBool);
              pic.filename = original.filename;
              pic.show("Spots Detected: " + count);
            } else {
              pic.filename = original.filename;
              pic.show();
            }
            chooseScreen(original);
          } else {
            StdDraw.isMousePressed = false;
          }
        } else if (isRed[i]) {
          createButton(x, y, halfWidth, 5, str[i], Color.WHITE);
          StdDraw.show();
          isRed[i] = false;
        }
      }
    }
  }

  /**
   * The first menu which asks the user to browse for an image
   */
  private static void startupScreen() {
    StdDraw.clear(Color.DARK_GRAY);
    StdDraw.setPenColor(Color.WHITE);
    StdDraw.text(50, 65, "Choose Image From File System:");
    createButton(50, 50, 10, 5, "Browse", Color.WHITE);
    StdDraw.show();

    boolean isRed = false;
    while (true) {
      boolean mouseX = StdDraw.mouseX() > 40 && StdDraw.mouseX() < 60;
      boolean mouseY = StdDraw.mouseY() > 45 && StdDraw.mouseY() < 55;
      if (mouseX && mouseY) {
        if (!isRed) {
          createButton(50, 50, 10, 5, "Browse", Color.RED);
          StdDraw.show();
          isRed = true;
        }
        if (StdDraw.isMousePressed()) {
          StdDraw.isMousePressed = false;
          StdDraw.mouseX = 0;
          StdDraw.mouseY = 0;
          createButton(50, 50, 10, 5, "Browse", Color.WHITE);
          File directory = new File("../");
          JFileChooser chooser = new JFileChooser(directory);
          chooser.setDialogTitle("Choose Image");
          FileNameExtensionFilter filter =
              new FileNameExtensionFilter("JPEG & PNG Images", "jpeg", "png");
          chooser.setFileFilter(filter);
          int returnVal = chooser.showOpenDialog(null);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            chooseScreen(new Picture(chooser.getSelectedFile()));
          }
        }
      } else if (isRed) {
        createButton(50, 50, 10, 5, "Browse", Color.WHITE);
        StdDraw.show();
        isRed = false;
      }
    }
  }

  /**
   * The main method only initializes the canvas and calls the start method
   * 
   * @param args No arguments needed to run
   */
  public static void main(String[] args) {
    StdDraw.enableDoubleBuffering();
    StdDraw.setScale(0, 99);

    startupScreen();
  }
}
