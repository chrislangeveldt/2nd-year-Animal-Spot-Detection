/**
 * This class provides the methods used in spot detection.
 * 
 * @author Chris Langeveldt
 */
public class SpotDetection {
  
  private static int count = 0;
  
  /**
   * Returns the spot count. Only call this method after detectSpots
   * 
   * @return the spot count
   */
  public static int getCount() {
    return count;
  }
  
  /**
   * Returns array with index 0 = width, index 1 = delta, index 2 = difference
   * 
   * @param rad The radius
   * @return array with info regarding radius
   */
  private static int[] getInfo(int rad) {
    int[] info = new int[3];
    info[0] = (rad - 2) * 3; //width
    if (rad == 4) { //delta
      info[1] = 0;
    } else if (rad > 4 && rad < 10) {
      info[1] = 1;
    } else if (rad == 10 || rad == 11) {
      info[1] = 2;
    }
    if (rad == 4) { //difference
      info[2] = 4800;
    } else if (rad == 5) {
      info[2] = 6625;
    } else if (rad == 6) {
      info[2] = 11000;
    } else if (rad == 7) {
      info[2] = 15000;
    } else if (rad == 8) {
      info[2] = 19000;
    } else if (rad == 9) {
      info[2] = 23000;
    } else if (rad == 10) {
      info[2] = 28000;
    } else if (rad == 11) {
      info[2] = 35000;
    }
    
    return info;
  }
  
  /**
   * Returns the mask as a 2D array where true = white
   * 
   * @param radius The radius of the mask
   * @param width The width corresponding to the given radius
   * @param delta The delta value corresponding to the given radius
   * @return mask as a 2D array
   */
  private static boolean[][] createMask(int radius, int width, int delta) { 
    int bound = radius * 2 + 1;
    boolean[][] mask = new boolean[bound][bound];
    
    for (int i = 0; i < bound; i++) {
      for (int j = 0; j < bound; j++) {
        double circle = Math.pow((i - radius), 2) + Math.pow((j - radius), 2);
        boolean donut1 = circle < (Math.pow((radius - delta), 2) + width);
        boolean donut2 = circle > (Math.pow((radius - delta), 2) - width);
        if (donut1 && donut2) {
          mask[i][j] = true;
        } 
      }
    }
    return mask;
  }
  
  /**
   * Returns 2D array with spots where true = white
   * 
   * @param edges The image after edge detection
   * @param spots The 2D array with spots
   * @param mask The mask to compare to edges
   * @param diff The difference value associated with radius
   * @return 2D array with spots
   */
  private static boolean[][] findSpots(boolean[][] edges, boolean[][] spots,
      boolean[][] mask, int diff) {
    int maskLen = mask.length;
    int edgesWidth = edges.length;
    int edgesHeight = edges[0].length;
    for (int x = 0; x < edgesWidth - maskLen + 1; x++) {
      outerloop: for (int y = 0; y < edgesHeight - maskLen + 1; y++) {
        int sum = 0;
        boolean onlyBlack = true;
        for (int i = 0; i < maskLen; i++) {
          for (int j = 0; j < maskLen; j++) {
            if (spots[x + i][y + j]) { // if white skip to next position
              continue outerloop; // avoiding recounting 
            }
            if (edges[x + i][y + j] != mask[i][j]) {
              sum += 255;
            }
            if (edges[x + i][y + j]) {
              onlyBlack = false;
            }
          }
        }
        if (sum < diff) {
          count++;
          for (int i = 0; i < maskLen; i++) {
            for (int j = 0; j < maskLen; j++) {
              spots[x + i][y + j] = edges[x + i][y + j];
            }
          }
        } else if (onlyBlack) {
          y += maskLen - 1;
        }
      }
    }
    return spots;
  }
  
  /**
   * Returns image with spot detection done
   * 
   * @param edges The boolean array with edge detection done
   * @param lower The lower radius limit
   * @param upper The upper radius limit
   * @return image with spot detection
   */
  public static boolean[][] detectSpots(boolean[][] edges, 
      int lower, int upper) {
    count = 0;
    boolean[][] spots = new boolean[edges.length][edges[0].length];
    for (int i = lower; i <= upper; i++) {
      int radius = i;
      int[] info = getInfo(i);
      int width = info[0];
      int delta = info[1];
      int diff = info[2];
      boolean[][] mask = createMask(radius, width, delta);
      spots = findSpots(edges, spots, mask, diff);
    }
    return spots;
  }
  
  /**
   * This method only for testing purposes
   * 
   * @param args The arguments entered by user
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
  }
}
