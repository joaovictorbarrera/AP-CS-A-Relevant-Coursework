// John Barrera
// Period 1
// Text Excel Project

import java.io.*;
import java.util.*;
/**
 * The Grid class will hold all the cells. It allows access to the cells via the
 * public methods. It will create a display String for the whole grid and process
 * many commands that update the cells. These command will include
 * setting the expression of cells and saving the grid contents to a file.
 *
 */
public class Grid extends GridBase {

   // instance fields of the grid's size (row and col),
   // each cell's width, and the 2D array of cells.
   public int colCount = 7;
   public int rowCount = 10;
   public int cellWidth = 9;
   public Cell[][] gridMatrix;
   
   // Constructor -> creates the matrix
   public Grid() {
      createMatrix();
   }
   
   /**
    * This processes a user command.
    * <p>
    * Checkpoint #1 commands are:
    * <ul>
    * <li><b>[cell] = [expression]</b> : "set the cell's expression, which may simply
    *  be a value such as 5, or it may more complicated such as: 'a3 * b3'."</li>
    * <li><b>[cell]</b> : "get the cell's expression, NOT the cell's value"</li>
    * <li><b>value [cell]</b> : "get the cell value"</li>
    * <li><b>print</b> : "render a text based version of the matrix"</li>
    * <li><b>clear</b> : "empty out the entire matrix"</li>
    * <li><b>clear [cell]</b> : "empty out a single cell"</li>
    * <li><b>width = [value]</b> : "set the cell width"</li>
    * <li><b>width</b> : "get the cell width"</li>
    * <li><b>rows = [value]</b> : "set the row count"</li>
    * <li><b>cols = [value]</b> : "set the column count"</li>
    * <li><b>rows</b> : "get the row count"</li>
    * <li><b>cols</b> : "get the column count"</li>
    * </ul>
    * 
    * @param command The command to be processed.
    * @return The results of the command as a string.
    */
   public String processCommand(String command) {
      String result = null;
      String[] tokens = command.split(" ");
      // Determines which grid command is being called and process it.
      if(command.equalsIgnoreCase("print")) 
         return printGrid();
         
      if(command.equalsIgnoreCase("width")) 
         return cellWidth + "";
         
      if(command.equalsIgnoreCase("rows")) 
         return rowCount + "";
      
      if(command.equalsIgnoreCase("cols")) 
         return colCount + "";
         
      if(tokens.length == 2 && tokens[0].equalsIgnoreCase("save")) 
         return saveToFile(tokens[1]);
         
      if(command.equalsIgnoreCase("clear")) {
         createMatrix();
         return "Cleared!";
      }
      
      // Tries another two sets of commands.
      result = propertyCommands(command);
      if(result == null) {
         result = cellCommands(command);
      }
      
      if (result == null)
         result = "unknown or malformed command: " + command;
   
      return result;
   }
   
   // Creates the matrix and populates each space with an instance of Cell.
   private void createMatrix() {
      // instantiates matrix object
      gridMatrix = new Cell[rowCount][colCount];
      
      // loops through each index populating every location with an instance of Cell
      for(int row = 0; row < gridMatrix.length; row++) {
         for(int col = 0; col < gridMatrix[row].length; col++) {
            gridMatrix[row][col] = new Cell();
         }
      }
   }
      
   // Saves grid properties and cell values into a file. 
   // Takes filename, returns if succeeded or message if not.
   private String saveToFile(String fileName) {
      // checks if filename is of a text file
      if(fileName.endsWith(".txt")) {
         try {
            // instantiates the printer and records the grid properties
            File file = new File(fileName);
            PrintStream writer = new PrintStream(file);
            writer.printf("rows = %d\n", this.rowCount);
            writer.printf("cols = %d\n", this.colCount);
            writer.printf("width = %d\n", this.cellWidth);            
            // loops through each index of the matrix, if it's NOT an empty cell,
            // converts the indexes to a written cell, constructs 
            // the command (along with the value) and writes it to the file.
            for(int rowCounter = 0; rowCounter < rowCount; rowCounter++) {
               for(int colCounter = 0; colCounter < colCount; colCounter++) {
                  String value = this.gridMatrix[rowCounter][colCounter].getExpression();
                  if(!value.equals("")) {
                     writer.printf("%c%d = %s\n", (char)(65+colCounter), rowCounter + 1, value);
                  }  
               }
            }
            // returns successful
            return "Succesfully saved all grid properties to " + fileName;
         }
         catch(Exception e) {
            return "Unexpected error";
         }
      }
      // file name does not have .txt, returns invalid format.
      return "invalid file format";
   }
   
   // This method handles setters for grid-parameters.
   // Returns new value if sucessful, returns null if unhandled.
   private String propertyCommands(String command) {
      // splits command into tokens
      String[] tokens = command.split(" ");
      // if the command has three tokens, the second token is an
      // equal sign, and the first token is one of the three commands, 
      // then tries width = [int], rows = [int] and cols = [int].
      if(tokens.length == 3 && tokens[1].equals("=") && 
               (tokens[0].equalsIgnoreCase("width") || 
               tokens[0].equalsIgnoreCase("rows") ||
               tokens[0].equalsIgnoreCase("cols"))) {
         try {
            // Blocks user from setting properties to less than one.
            if(Integer.parseInt(tokens[2]) < 1) { 
               return "Not allowed, must be greater than one!";
            }
            // if any of the three commands, parses the third token to an integer, 
            // sets the requested instance field to it,
            // re-creates the grid and returns the set value. 
            if(tokens[0].equalsIgnoreCase("width")) {
               this.cellWidth = Integer.parseInt(tokens[2]);
               return cellWidth + "";
            }
            if(tokens[0].equalsIgnoreCase("rows")) {
               this.rowCount = Integer.parseInt(tokens[2]); 
               createMatrix();
               return rowCount + "";
            }
            if(tokens[0].equalsIgnoreCase("cols")) {
               this.colCount = Integer.parseInt(tokens[2]);
               createMatrix();
               return colCount + "";
            }
         }
         catch(Exception e) {
            // If it fails, returns invalid-input.
            return(String.format("%s is not a valid number", tokens[2]));
         }
      }
      return null;
   }

   // Runs all cell-related commands. Returns processed value or null if none match. 
   private String cellCommands(String command) {
      // splits commands in tokens
      String[] tokens = command.split(" ");
      // if the first token is a cell, run commands that start with a cell.
      if(parseCellNameFrom(tokens[0]) != null) {
         return startsWithCellNameCommands(command, tokens);
      }
      // if not, tries commands that end with a cell.
      return endsWithCellNameCommands(command, tokens);
   }
   
   // Handles commands that start with a cell name -> [cell] = # | [cell]
   // returns null if unhandled, returns string with the processed value otherwise                                                
   private String startsWithCellNameCommands(String command, String[] tokens) {
      // if the command ran, then the first token is a cellname.
      String cell = tokens[0];
      // if it's the only token, run [cell].
      if(tokens.length == 1) { 
         return retrieveCellFromLocation(cell).getExpression() + "";
      }
      // if the second token is the equal sign AND the 
      // command has 3 or more tokens, then runs [cell] = #.
      if(tokens.length >= 3 && tokens[1].equals("=")) {
         // Sets given cell to the given value (everything after the equal sign) 
         return findAndAssignCelltype(cell, command.substring(command.indexOf("=") + 2));
      }
      // if none, then it's probably a cellname with 
      // something invalid along with it. Returns null.
      return null;  
   }
   
   // Handles commands that end with a cell name -> [command] [cell]
   // returns null if unhandled, returns string with the processed value otherwise    
   private String endsWithCellNameCommands(String command, String[] tokens) {
      // if there's only two tokens and the second token 
      // is a cellLocation, tries cell-related getters.
      if(tokens.length == 2 && parseCellNameFrom(tokens[1]) != null) {
         String cell = tokens[1];
         // tests if cell exists in the grid (is NOT out of bounds)
         if(isOutOfBounds(cell)) {
            // if it doesn't exist, returns that the cell is out of bounds.
            return "Cell is out of bounds";  
         }
         // evaluates if the command is value, display or expr
         if(tokens[0].equalsIgnoreCase("value")) {
            // returns value of the cell
            return retrieveCellFromLocation(cell).getValue() + "";
         }
         if(tokens[0].equalsIgnoreCase("display")) {
            // returns how the cell is displayed
            return retrieveCellFromLocation(cell).toString() + "";
         }
         if(tokens[0].equalsIgnoreCase("expr")){
            // returns the cell's expression
            return retrieveCellFromLocation(cell).getExpression() + "";
         } 
         if(tokens[0].equalsIgnoreCase("clear")) {
            // transforms the cell to row and col, 
            // creates a new empty cell at row/col location.
            int row = parseRowColFromCellLocation(cell)[0];
            int column = parseRowColFromCellLocation(cell)[1];
            this.gridMatrix[row][column] = new Cell();
            return "Cleared " + cell;
         }
      }
      return null;
   }
   
   // Takes a cell location and returns the cell found.
   private Cell retrieveCellFromLocation(String cellLocation) {
      // transforms the cell number into a row and col index
      int row = parseRowColFromCellLocation(cellLocation)[0];
      int col = parseRowColFromCellLocation(cellLocation)[1];
   
      // returns cell at given location
      return gridMatrix[row][col];
   }
   
   // Figures out what type of cell this input is about, 
   // then assigns value to the cell at the given location. 
   // Returns "" if success.
   // If none, returns a message to the user.
   private String findAndAssignCelltype(String cellLocation, String value) {
      String result = null;
      // transforms celllocation to row and col
      int row = parseRowColFromCellLocation(cellLocation)[0];
      int column = parseRowColFromCellLocation(cellLocation)[1];
      
      // checks if this cell is inside the bounds of the current matrix
      if(isOutOfBounds(row, column)) {
         return "Requested cell is out of bounds.";
      }
      // tries textcell cases, numbercell cases, and date cell cases.
      result = textCellTest(row, column, value);
      if (result == null)
         result = numberCellTest(row, column, value);  
      if (result == null)
         result = dateCellTest(row, column, value);
      
      // prints a message from result
      // if result gathered one along the way
      if(result != null) {
         return result;
      }
      
      // returns null if none of the cases above.
      return null;  
   }
   
   // TextCell tester. If value is text between quotes, 
   // this method will assign it to a TextCell at the given cell location.
   // Returns "" if success, null if not this cell.
   private String textCellTest(int row, int column, String value) {
      // assigns value to the matrix at the given cell location as a TextCell.
      if(value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
         gridMatrix[row][column] = new TextCell(value);
         return "";
      }
      return null;
   }
   
   // NumberCell tester. If value is a valid num expression, this method 
   // will assignit to a NumberCell at the given cell location.
   // Returns "" if success, null if not this cell,
   // a message if an error was identified.
   private String numberCellTest(int row, int column, String value) {
      // Separates value into tokens smartly and loops through it.
      String[] exprTokens = smartSplit(value);
      // If not expression returns null
      if(exprTokens == null) {
         return null;
      }
      // For each token, see if it's a cell and if this cell is out of bounds. 
      // If so, returns invalid to the user.
      for(String token : exprTokens) {
         String tokenToCell = parseCellNameFrom(token);
         if(tokenToCell != null && isOutOfBounds(tokenToCell)) {
            return "At least of one of the cells in this expression is out of bounds.";  
         }
      }  
      
      // Uses a few parameters to figure out whether this 
      // value is a valid expression, just a num, or invalid
      // If it passes, assigns value to a NumberCell 
      // at the provided matrix location.
      if(isDoubleValue(value) || validateNumberCell(exprTokens)) {
         gridMatrix[row][column] = new NumberCell(value);
         return ""; 
      }
      return null;
   }
   
   // Tests if expression is a valid case and correctly written.
   // returns true if valid, false if not valid.
   private boolean validateNumberCell(String[] exprTokens) {
      // guard expression, all expressions should have this
      // should exist, be inside parenthesis
      if(exprTokens == null || !exprTokens[0].equals("(") ||
         !exprTokens[exprTokens.length - 1].equals(")")) {
         return false;
      }
      // ( num ) single number expression or ( cell ) single cell expression
      // Only has 3 tokens, the one in the middle is a num or a cell
      if(exprTokens.length == 3 && isDoubleValue(exprTokens[1]) || 
         parseCellNameFrom(exprTokens[1]) != null) {
         return true;
      }
      // sum / avg expression ( SUM B1 - B4 )
      // second token is sum/avg, followed by a cell, a "-". and another cell.
      if(exprTokens.length == 6 && (exprTokens[1].equalsIgnoreCase("sum") ||
         exprTokens[1].equalsIgnoreCase("avg")) &&
         parseCellNameFrom(exprTokens[2]) != null && 
         parseCellNameFrom(exprTokens[4]) != null && exprTokens[3].equals("-")) {
         return true;
      }
      // common expression ( A1 + 2 / 123 * Q3 )
      // Odd tokens, even indexes = an operator, odd indexes = a cell or a num
      if(exprTokens.length % 2 == 1 || exprTokens.length > 3) {
         final String operators = "+-/*";
         for(int i = 2; i < exprTokens.length - 1; i++) {
            if(i % 2 == 0 && !operators.contains(exprTokens[i])) {
               return false;
            }
            if(i % 2 == 1 && parseCellNameFrom(exprTokens[i]) == null 
                           && !isDoubleValue(exprTokens[i])) {
               return false;
            }
         }   
         return true;
      }
      
      // reached the end, no matches
      return false;
   }
   
   // DateCell tester. If value is a valid date, this method will assign
   // it to a DateCell at the given cell location.
   // Returns "" if success, null if not this cell,
   // a message if an error was identified.
   private String dateCellTest(int row, int column, String value) {
      // divides value using the date bars "/"
      String[] dateCellTester = value.split("/");
      // all valid dates should make an array of length 3
      if(dateCellTester.length == 3) {
         try {
            // collects info about the month, day, and year in the reported date
            int month = Integer.parseInt(dateCellTester[0]);
            int day = Integer.parseInt(dateCellTester[1]);
            int year = Integer.parseInt(dateCellTester[2]);
            // makes sure that the date is within the bounds of our calendar
            if(day < 1 || day > 31 || month < 1 || month > 12 || year < 0) {
               return "Invalid date";
            }
            // assigns value to a datecell in the matrix at the given location 
            gridMatrix[row][column] = new DateCell(value);
            return "";
         }
         // if it fails, it just does nothing, returns null in 
         // case it might be something else in the program
         catch(Exception e) {  
         }
      }
      // if nothing above, just returns null
      return null;
   }
   
   // takes a cellLocation and returns an array containing the
   // row and the column represented in that cellLocation.
   public int[] parseRowColFromCellLocation(String cellLocation) {
      // normalizes cell name
      cellLocation = cellLocation.toUpperCase();
      // transforms the alphabetic letter to a column index
      int column = cellLocation.charAt(0) - 65;
      // transforms the cell number into a row index
      int row = Integer.parseInt(cellLocation.substring(1)) - 1;
      
      return new int[] {row, column};
   }
   
   // Tests if cellLocation is out of bounds or not. 
   // Returns true if it is. False if isn't
   private boolean isOutOfBounds(int row, int col) {
      if(col + 1 > this.colCount || row + 1 > this.rowCount) {
         return true;
      }
      return false;
   }
   
   // @Overloaded, takes cellLocation and 
   // NOT row and col, calculates on the fly.
   private boolean isOutOfBounds(String cellLocation) {
      // gets row and col
      int thisRow = parseRowColFromCellLocation(cellLocation)[0];
      int thisCol = parseRowColFromCellLocation(cellLocation)[1];
      return isOutOfBounds(thisRow, thisCol);
   }

   // Tests if string is a double value and returns 
   // true if it is, and false if it isn't
   private boolean isDoubleValue(String value) {
      // tries parsing to double, it it fails returns false,
      // if not returns true.
      try {
         Double.parseDouble(value);
         return true;
      }
      catch(Exception e) {
         return false;
      }
   }
   
   // Returns a string with the full grid.
   private String printGrid() {
      String grid = "";
      // Adds header and a dividing line.
      grid += printHeader();
   
      // Prints rowCount number of rows. 
      // For each row, first adds the line counter, 
      // then cellWidth number of cells, then a dividing line.
      for(int rowCounter = 1; rowCounter < rowCount + 1; rowCounter++) {
         grid += String.format("%3d |", rowCounter);
         for(int columnCounter = 0; columnCounter < this.colCount; columnCounter++) {
            grid += printCell(rowCounter - 1, columnCounter);
         }
         grid += printDividingLine();
      }
      return grid;
   }
   
   // Returns the dividing line. Its size depends on the amount
   // of colons and the cell width.
   // "----+------+------+"
   // "----+-----------+-----------+-----------+"
   private String printDividingLine() {
      // Starts off with a immutable 4-width section for the line counter formatting.
      String line = "\n----+";
      
      // a section = "-----+"
      // It has a colCount amount of sections, for each section, 
      // adds a cellWidth amount of dashes and a single "+".
      for(int i = 0; i < this.colCount; i++) {
         for(int j = 0; j < this.cellWidth; j++) {
            line += "-";
         }
         line += "+";
      }
      
      // Goes to the next line.
      line += "\n";
      return line;
   }
   
   // Takes a row and column index and returns a String containing
   // the cell at the given coordinates. Correctly truncated if necessary.
   private String printCell(int rowCounter, int columnCounter) {
      String cell = "";
      // gets the value from the cell in the matrix at the given location
      String cellValue = gridMatrix[rowCounter][columnCounter].toString();
      
      // if value length is equal to cellWidth, prints value
      // "3.1415926|"
      // if value length is smaller than cellWidth, fills the remaining capacity as spaces.
      // "      3.0|"
      if(cellValue.length() <= this.cellWidth) {
         cell += printSpaces(this.cellWidth - cellValue.length());
         cell += cellValue;
      }
      // if value length is bigger than cellWidth, truncates it to the cellWidth.
      else {
         cell += cellValue.substring(0, this.cellWidth);
      }
       
      // adds bar and returns
      cell += "|";    
      return cell;
   }
   
   // Returns the header and a dividing line. Its size depends on the
   // colCount and cellWidth.
   private String printHeader() {
      // Starts off with a immutable 4-width section for the line counter formatting.
      String grid = "    |";
      for(int i = 0; i < this.colCount; i++) {  
         // Adds half the spaces and the column's alphabet letter (ascii conversion).
         grid += printSpaces(cellWidth/2) + ((char)(i + 65));
         
         // Alignment
         // if the cellWidth is even, adds half the spaces - 1,
         // if odd, just half the spaces. Then adds a "|".
         if(cellWidth % 2 == 0) {
            grid += printSpaces(cellWidth/2 - 1)  + "|";
         }
         else {
            grid += printSpaces(cellWidth/2)  + "|";
         }
      }
      // Adds a dividing line.
      grid += printDividingLine();
      return grid;
   }
   
   // Accepts a number and returns a "number" amount of spaces.
   private String printSpaces(int num) {
      String toReturn = "";
      for(int i = 0; i < num; i++) {
         toReturn += " ";
      }
      return toReturn;
   }
}
