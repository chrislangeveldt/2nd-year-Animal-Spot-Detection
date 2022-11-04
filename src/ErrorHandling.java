/**
 * This class provides the methods used in handling the various errors
 * 
 * @author Chris Langeveldt
 */
public class ErrorHandling {

  /**
   * Check for invalid number of arguments 
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidNumOfArgs(String[] args) {
    int mode = -1;
    boolean error = false;
    
    if (args.length < 2 || args.length > 5 || args.length == 4) {
      System.err.println("ERROR: invalid number of arguments");
      System.exit(0);
    }
    try {
      Integer.parseInt(args[0]);
    } catch (Exception e) { //Another error
      return;
    }
    mode = Integer.parseInt(args[0]);
    if ((mode == 0 || mode == 1) && args.length != 2) {
      error = true;
    } else if (mode == 2 && args.length != 3) {
      error = true;
    } else if (mode == 3 && args.length != 5) {
      error = true;
    }
    
    if (error) {
      System.err.println("ERROR: invalid number of arguments");
      System.exit(0);
    }
  }
  
  
  /**
   * Check for invalid argument type 
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidArgs(String[] args) {
    int mode = -1;
    
    try {
      Integer.parseInt(args[0]);
    } catch (Exception e) {
      System.err.println("ERROR: invalid argument type");
      System.exit(0);
    }
    mode = Integer.parseInt(args[0]);
    if (mode == 2) {
      try {
        Integer.parseInt(args[2]);
      } catch (Exception e) {
        System.err.println("ERROR: invalid argument type");
        System.exit(0);
      }
    } else if (mode == 3) {
      try {
        Integer.parseInt(args[2]);
        Integer.parseInt(args[3]);
        Integer.parseInt(args[4]);
      } catch (Exception e) {
        System.err.println("ERROR: invalid argument type");
        System.exit(0);
      }
    }
  }
  
  /**
   * Check for invalid mode
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidMode(String[] args) {
    int mode = Integer.parseInt(args[0]);
    if (mode < 0 || mode > 3) {
      System.err.println("ERROR: invalid mode");
      System.exit(0);
    }
  }

  /**
   * Check for invalid epsilon
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidEps(String[] args) {
    int mode = Integer.parseInt(args[0]);
    if (mode == 2 || mode == 3) {
      int eps = Integer.parseInt(args[2]);
      if (eps < 0 || eps > 255) {
        System.err.println("ERROR: invalid epsilon");
        System.exit(0);
      }
    }
  }
  
  /**
   * Check for invalid file
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidFile(String[] args) {
    String filename = args[1];
    try {
      Picture pic = new Picture(filename);
    } catch (Exception e) {
      System.err.println("ERROR: invalid or missing file");
      System.exit(0);
    }
  }
  
  /**
   * Check for invalid radius limits
   * 
   * @param args The arguments entered by the user
   */
  private static void invalidRadLims(String[] args) {
    int mode = Integer.parseInt(args[0]);
    if (mode == 3) {
      int lower = Integer.parseInt(args[3]);
      int upper = Integer.parseInt(args[4]);
      if (lower < 4 || upper > 11 || lower > upper) {
        System.err.println("ERROR: invalid radius limits");
      }
    }    
  }
  
  /**
   * Runs through the error checks in specific order
   * 
   * @param args The arguments entered by the user
   */
  public static void handleErrors(String[] args) {
    invalidNumOfArgs(args);
    invalidArgs(args);
    invalidMode(args);
    invalidEps(args);
    invalidFile(args);
    invalidRadLims(args);
  }

  /**
   * Main method only used for testing
   * 
   * @param args The arguments entered by the user
   */
  public static void main(String[] args) {
    handleErrors(args);
    System.out.println("NO ERRORS");
  }
}
