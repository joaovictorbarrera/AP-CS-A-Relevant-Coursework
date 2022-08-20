// John Barrera
// Period 1
// Text Excel Project

import java.text.SimpleDateFormat;
import java.util.Date;

// DateCells are responsible for holding dates and presenting 
// them already formatted to glossed version.
public class DateCell extends Cell{
   public DateCell(String expression) {
      setExpression(expression);
   }
   
   // Returns the string to be presented on the grid
   public String toString() {
      // Instantiates a SimpleDateFormat object with the 
      // correct formatting pattern as it's state.
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, YYYY");
      // Creates a date object using the expression from the cell.
      Date date = new Date(getExpression());
      // Uses the simpledateformat object to 
      // format the date object into something pretty
      return simpleDateFormat.format(date);
      
      // Date: 10/25/01
      // SDF: MMM d, YYYY
      // Both: Oct 25, 2001
   }
}