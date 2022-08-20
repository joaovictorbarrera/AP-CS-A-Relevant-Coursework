// John Barrera
// Period 1
// Text Excel Project

// TextCells are responsible for holding 
// and displaying pure text values.
public class TextCell extends Cell{
   // Will only allow TextCells to be created with an expression.  
   public TextCell(String expression) {
      setExpression(expression);
   }
   
   // Returns the string to be presented on the grid
   public String toString() {
      // remove quotes from string
      String label = getExpression();
      return label.substring(1, label.length() - 1);
   }
}
