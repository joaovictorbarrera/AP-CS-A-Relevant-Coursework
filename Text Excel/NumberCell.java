// John Barrera
// Period 1
// Text Excel Project

import java.util.*;

// NumberCells are responsible for holding expressions of values 
// or that result in values after being evaluated.
public class NumberCell extends Cell{
   
   // Will only allow NumberCells to be created with an expression.
   public NumberCell(String value) {
      setExpression(value);
   }
   
   // Returns the string to be presented on the grid
   public String toString() {
      // Calculates expression and returns the value as text.
      return getValue() + "";
   }
   
   // This will return the calculated or pure value for this cell
   // if receives an expression, pre-condition: 
   // It has an odd number of items, starts and ends with parenthesis, if only one item inside parenthesis,
   // it has to be a cell or a double/int value. If expression has only ONE token, it is a double value.
   public double getValue() {
      // Breaks expression into tokens smartly.
      String[] tokens = GridBase.grid.smartSplit(getExpression());
      
      // if only one token, parses to double and returns. (Ex: Expression: 7.13)
      if(tokens.length == 1) {
         return Double.parseDouble(getExpression());
      }
      
      // if 6 tokens and first token within parenthesis is avg or sum, then handle an avg/sum expression.
      if(tokens.length == 6 && (tokens[1].equalsIgnoreCase("avg") || tokens[1].equalsIgnoreCase("sum"))) {
         return handleAvgSum(tokens);
      }
      
      // Tests each index of the expression for cells, if current token is a cell, 
      // calls processCommand with "value " + cellName to get it's value and replaces it in the array.
      for(int i = 0; i < tokens.length; i++) {
         if(GridBase.grid.parseCellNameFrom(tokens[i]) != null) {
            tokens[i] = GridBase.grid.processCommand("value " + tokens[i]);
         }
      }
      // Only three tokens means it's either Expression: "(a Cell)" or "(a Double Value)", 
      // as cells were parsed to their values, parses the second token to a double and returns.
      if(tokens.length == 3) {
         return Double.parseDouble(tokens[1]);
      }
      
      // If none of the above, it might be a longer expression, 
      // so calculates it and returns. Ex: Expression: (1 + 2.52 / 54).
      return doCalculations(tokens);
   }
   
   // Takes a mathematical expression broken up in 
   // tokens (array) and calculates the full value and returns it.
   // Pre-Condition: even-index tokens are always operators, 
   // expression contains at least two values and one operator.
   private double doCalculations(String[] tokens) {
      // Assigns to a double "result" the result of 
      // the first occuring operation in the expression.
      double result = operate(tokens[1], tokens[2], tokens[3]);
      // Now, if there's more operations left to go 
      // (hasn't reached the end before the parenthesis), 
      // then calculates it over and over again until "result" is the final value.
      int count = 4;
      while(count < tokens.length - 1) {
         // ([result] +-*/ b) -> result from last operation, item at next
         // even index(operator), and the item after the operator.
         result = operate(result + "", tokens[count], tokens[count + 1]);
         // Adds 2 to the counter so the current index is always the operator.
         count += 2;
      }  
      return result;
   }
   
   // Takes two Strings containing values and one operator, calculates and returns it.
   private double operate(String num, String operator, String num2) {
      switch(operator) {
         case "+":
            return (Double.parseDouble(num) + Double.parseDouble(num2));
         case "-":
            return (Double.parseDouble(num) - Double.parseDouble(num2));
         case "*":
            return (Double.parseDouble(num) * Double.parseDouble(num2));
         case "/":
            return (Double.parseDouble(num) / Double.parseDouble(num2));
      }
      return Double.NaN;
   }
   
   // Handles expressions of AVG and SUM functions.
   // Returns the calculated result of the function.
   private double handleAvgSum(String[] tokens) {
      // gets row and col for first and second cell
      int rowCellOne = ((Grid) GridBase.grid).
                        parseRowColFromCellLocation(tokens[2])[0]; 
      int colCellOne = ((Grid) GridBase.grid).
                        parseRowColFromCellLocation(tokens[2])[1]; 
      int rowCellTwo = ((Grid) GridBase.grid).
                        parseRowColFromCellLocation(tokens[4])[0]; 
      int colCellTwo = ((Grid) GridBase.grid).
                        parseRowColFromCellLocation(tokens[4])[1]; 
      
      // evaluates if first cell is "smaller" or "is in the correct order" (A1 - A3)
      // if so, calls sum or avg depending on what's in the expression
      // if not, inverts the parameters so that the smaller one goes first. (A3 - A1) -> (A1 - A3)
      if(rowCellOne - rowCellTwo <= 0 && colCellOne - colCellTwo <= 0) {
         if(tokens[1].equalsIgnoreCase("SUM")) {
            return doSum(rowCellOne, colCellOne, rowCellTwo, colCellTwo);
         }
         else {
            return doAvg(rowCellOne, colCellOne, rowCellTwo, colCellTwo);
         }
      }
      else {
         if(tokens[1].equalsIgnoreCase("SUM")) {
            return doSum(rowCellTwo, colCellTwo, rowCellOne, colCellOne);
         }
         else {
            return doAvg(rowCellTwo, colCellTwo, rowCellOne, colCellOne);
         }
      }
   }
   
   // sums all values in a range of two cells and returns it.
   private double doSum(int row1, int col1, int row2, int col2) {
      double sum = 0;
      // accesses, gets the value, and adds this value to a cumulative sum.
      // bounds for row and col indexes are 
      // [row of first cell to row of other cell] and 
      // [col of first cell to col of other cell].
      for(int thisRow = row1; thisRow <= row2; thisRow++) {
         for(int thisCol = col1; thisCol <= col2; thisCol++) {
            sum += ((Grid) GridBase.grid).gridMatrix[thisRow][thisCol].getValue();
         }
      }
      return sum;
   }
   
   // sums all values in a range of two cells,
   // then divides by the amount of cells in the range.
   // This gives the average of the values, which is returned by this method.
   private double doAvg(int row1, int col1, int row2, int col2) {
      double sum = 0;
      int count = 0;
      // accesses, gets the value, and adds this value to a cumulative sum.
      // bounds for row and col indexes are 
      // [row of first cell to row of other cell] and 
      // [col of first cell to col of other cell].
      // Every time a cell is accessed, adds one to a counter.
      for(int thisRow = row1; thisRow <= row2; thisRow++) {
         for(int thisCol = col1; thisCol <= col2; thisCol++) {
            count++;
            sum += ((Grid) GridBase.grid).gridMatrix[thisRow][thisCol].getValue();
         }
      }
      // returns the average by dividing the cumulative sum by 
      // the number of times a cell was accessed.
      return sum/count;
   }
}