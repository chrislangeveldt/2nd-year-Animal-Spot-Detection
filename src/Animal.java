import java.awt.Color;

/**
 * This is the main class which contains the methods of phase 1.
 * 
 * @author Chris Langeveldt
 */
public class Animal {

  /**
   * Save image with adjusted name
   * 
   * @param im The picture to be saved
   * @param name The name of the picture to be changed
   * @param mode The mode in which in this case dictates naming convention
   */
  public static void save(Picture im, String name, int mode) {
    String suffix = "";
    if (mode == 0) {
      suffix = "_GS";
    } else if (mode == 1) {
      suffix = "_NR";
    } else if (mode == 2) {
      suffix = "_ED";
    } else if (mode == 3) {
      suffix = "_SD";
    }
    int begin = name.lastIndexOf('/') + 1;
    int end = name.lastIndexOf('.');
    String newName = name.substring(begin, end) + suffix + ".png";
    im.save("../out/" + newName);
  }
  
  /**
   * Returns picture when converted from int array
   * 
   * @param pic The 2D int array of the image
   * @return the picture version of the array
   */
  public static Picture toPicture(int[][] pic) {
    int width = pic.length;
    int height = pic[0].length;
    Picture picture = new Picture(width, height);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int col = pic[i][j];
        picture.set(i, j, new Color(col, col, col));
      }
    }
    return picture;
  }
  
  /**
   * Returns picture when converted from boolean array
   * 
   * @param pic The 2D boolean array of the image
   * @return the picture version of the array
   */
  public static Picture toPicture(boolean[][] pic) {
    int width = pic.length;
    int height = pic[0].length;
    Picture picture = new Picture(width, height);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int col = 0;
        if (pic[i][j]) {
          col = 255;
        }
        picture.set(i, j, new Color(col, col, col));
      }
    }
    return picture;
  }

  /**
   * Returns the grey scaled version
   * 
   * @param im The original picture
   * @return the grey scaled version
   */
  public static int[][] greyScale(Picture im) {
    int width = im.width();
    int height = im.height(); 
    int[][] pic = new int[width][height];
    Picture temp = new Picture(width, height);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int red = im.get(i, j).getRed();
        int green = im.get(i, j).getGreen();
        int blue = im.get(i, j).getBlue();
        int gr = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
        pic[i][j] = gr;
      }
    }
    return pic;
  }

  /**
   * Returns noise reduced image
   * 
   * @param im The grey scaled image
   * @return noise reduced image
   */
  public static int[][] noiseReduce(int[][] im) {
    int width = im.length;
    int height = im[0].length; 
    int[][] temp = new int[width][height];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        // edges don't change
        if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
          temp[i][j] = im[i][j];
          continue;
        }
        // set pixel to must recurring color in neighborhood
        int[] nb = new int[5];
        nb[0] = im[i][j]; //center pixel
        nb[1] = im[i][j - 1];
        nb[2] = im[i][j + 1];
        nb[3] = im[i - 1][j];
        nb[4] = im[i + 1][j];

        int most = nb[0];
        int count = 1;
        for (int k = 0; k < 5; k++) {
          int tempCount = 1;
          for (int l = k + 1; l < 5; l++) {
            if (nb[k] == nb[l]) {
              tempCount++;
            }
          }
          if (tempCount > count) {
            count = tempCount;
            most = nb[k];
          }
        }
        temp[i][j] = most;
      }
    }
    return temp;
  }

  /**
   * Returns image with edge detection
   * 
   * @param im Noise reduced image
   * @param eps The epsilon value
   * @return image with edge detection
   */
  public static boolean[][] edgeDetect(int[][] im, int eps) {
    int width = im.length;
    int height = im[0].length; 
    boolean[][] pic = new boolean[width][height];
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++) {
        int[] nb = new int[5];
        nb[0] = im[i][j]; //center pixel
        nb[1] = im[i][j - 1];
        nb[2] = im[i][j + 1];
        nb[3] = im[i - 1][j];
        nb[4] = im[i + 1][j];

        for (int k = 1; k < 5; k++) {
          if (Math.abs(nb[0] - nb[k]) >= eps) {
            pic[i][j] = true;
            break;
          }
        }
      }
    }
    return pic;
  }

  /**
   * Main method used to call different modes
   * 
   * @param args The arguments entered by the user
   */
  public static void main(String[] args) {
    ErrorHandling.handleErrors(args);
    
    int mode = Integer.parseInt(args[0]);
    String filename = args[1];
    Picture picture = new Picture(filename);
    int[][] picInt = new int[picture.width()][picture.height()];
    boolean[][] picBool = new boolean[picture.width()][picture.height()];
    
    if (mode >= 0) {
      picInt = greyScale(picture);
    }
    if (mode >= 1) {
      picInt = noiseReduce(picInt);
    }
    if (mode >= 2) {
      int epsilon = Integer.parseInt(args[2]);
      picBool = edgeDetect(picInt, epsilon);
    }
    if (mode == 3) {
      int lower = Integer.parseInt(args[3]);
      int upper = Integer.parseInt(args[4]);
      picBool = SpotDetection.detectSpots(picBool, lower, upper);
      int count = SpotDetection.getCount();
      System.out.println(count);
    }
    
    if (mode < 2) {
      save(toPicture(picInt), filename, mode);
    } else {
      save(toPicture(picBool), filename, mode);
    }
  }
}
