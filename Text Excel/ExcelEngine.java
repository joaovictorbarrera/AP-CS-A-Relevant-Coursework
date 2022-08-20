// John Barrera
// Period 1
// Text Excel Project

import java.io.*;
import java.util.*;

/**
 * The ExcelEngine is the main implementation of the TextExcel program. It
 * implements the requirements established by ExcelBase. Most methods will be
 * private as the only public methods necessary are to processCommand(). This
 * class will handle certain commands that the Grid does not: help and
 * load file.
 */
public class ExcelEngine extends ExcelBase {

   public static void main(String args[]) {

      // Create our Grid object and assign it to the GridBase
      // static field so that we can reference it later on.
      GridBase.grid = new Grid();
      ExcelEngine engine = new ExcelEngine();

      engine.runInputLoop();
   }

   /**
    * Method processCommand.<p>
    * 
    * This method will parse a line that contains a command. It will
    * delegate the command to the Grid if the Grid should handle it. It will call
    * the appropriate method to handle the command. Ignores case for all comammands.<p>
    *
    * ALL Commands will be distributed to the appropriate methods.
    * Here are the Checkpoint #1 commands: <ul>
    *  <li><b>help</b> : "provides help to the user" </li>
    *  <li><b>print</b> : "returns a string of the printed grid"</li> 
    *  <li><b>rows</b> : "returns the number of rows currently in the 
    *           grid"</li>
    *  <li><b>rows = 5</b> : "resizes the grid to have 5 rows. The
    *           grid contents will be cleared."</li>
    *  <li><b>cols</b> : "returns the number of columns currently in the 
    *           grid"</li>
    *  <li><b>cols = 3</b> : "resizes the grid to have 3 columns. The
    *           grid contents will be cleared."</li>
    *  <li><b>width</b> : "returns the width of an individual cell that
    *           is used when displaying the grid contents."</li>
    *  <li><b>width = 12</b> : "resizes the width of a cell to be 12
    *           characters wide when printing the grid."</li>
    *  <li><b>load file1.txt</b> : "opens the file and processes all
    *           commands in it"</li></ul>
    * 
    * @param command A command to be processed
    * @return The result of the command to be printed.
    */
   public String processCommand(String command) {
      String result = null;
      
      // Help information.
      if(command.equals("help")) {
         result =  "* Here's some useful help: *\n\n";
         result += "* gui   : Displays the grid on a separate window.\n";
         result += "* print : Prints the whole grid with its current properties and cell display values.\n";
         result += "* rows  : Prints the current amount of rows in the grid.\n";
         result += "* cols  : Prints the current amount of columns in the grid.\n";
         result += "* width : Prints the current width of each cell in the grid.\n";
         result += "* clear : Clears all the values in the grid\n";
         result += "* rows = [integer]    : Sets the amount of rows to the given value.\n";
         result += "* cols = [integer]    : Sets the amount of columns to the given value.\n";
         result += "* width = [integer]   : Sets the width of each cell to the given value.\n";
         result += "* load [filename.txt] : Loads all commands in the file into the program.\n";
         result += "* save [filename.txt] : Saves the grid properties and all the cell values into a text file\n\n";
         result += "* Cell related info: *\n";
         result += "* The cell location is defined as a letter(column) and a number(row). Ex: A1, B4, G12\n";
         result += "* [cell] = #                              : Sets this cell to a value, text, or date.\n";
         result += "                                            [Observation: Text needs quotes]\n";
         result += "* [cell] = (expression)                   ";
         result += ": Sets to this cell a mathematical expression containing values and/or ";
         result += "cells,\n                                            ";
         result += "which is calculated before being displayed.\n                              ";
         result += "              [Obs: needs to be inside parenthesis] Ex: A1 = (B2 + 12.23 / 123.62 * 9)\n";
         result += "* [cell] = (function [cell1] - [cell2])   : Sets to this cell a function (avg or sum) ";
         result += "with a range of cells\n                                            ";
         result += "for the function to be calculated with. Ex: A1 = (AVG A2 - B4)\n";
         result += "* [cell] / expr [cell]                    : Returns the expression of this cell\n";
         result += "* value [cell]          : Returns the value of this cell\n";
         result += "* display [cell]        : Returns how this cell is displayed\n";
         result += "* clear [cell]          : Clears this specific cell.";
         return result;
      }
      // handle all file related commands here
      result = handleFileCommands(command);

      // The method handleFileCommands will return null
      // if the input was NOT a command that it handled.
      // if result == null, the command wasn't a file command.
      //     let's see if it is a grid command. Delegate
      //     the command to the Grid object to see if it can handle it.
      if (result == null && GridBase.grid != null) {
         // The GridBase class has a static field, grid, of type GridBase.
         // Ask the grid object to process the command. 
         result = GridBase.grid.processCommand(command);
      }

      // the command is still not handled
      if (result == null)
         result = "Unhandled";

      return result;
   }

   /**
    * Method handleFileCommands.<p>
    * 
    * This method will handle commands related to file processing.<p>
    *
    * Required Commands to handle are: <ul>
    *  <li><b>load [filename]</b> : "opens the file and processes all
    *           commands in it"</li>
    *  @param command The full command to be handled.
    *  @return null if the command was not handled.<br>
    *          A String to print to the user if the command was handled.
    */
   private String handleFileCommands(String command) {
      String[] tokens = command.split(" ");
      // process the load command
      if(tokens[0].equalsIgnoreCase("load") && tokens.length == 2) {
         return loadFromFile(tokens[1]);
      }
      
      return null;
   }

   /**
    * Method loadFromFile. 
    *
    * This will process the command: load {filename}<p>
    *
    * Call processCommand() for every line in the file. During file processing,
    * there should be no output.
    * @param filename The name/path to the file
    * @return Success or Failure message to display to the user
    */
   private String loadFromFile(String filename) {

      File file = new File(filename);
      String result = "File loaded successfully";
      try {
         // load the file and process the commands in the file
         Scanner reader = new Scanner(file);
         
         // While there's still a command in the file, loops around
         // processing each line, then closes the scanner.
         while(reader.hasNextLine()) {
            String line = reader.nextLine(); 
            processCommand(line);
         }
         reader.close();
         
      } catch (FileNotFoundException e) {
         result = "Could not find file: " + file.getAbsolutePath();
      }

      return result;
   }

}
