// John Barrera
// Period 1
// AP Computer Science A
// Fraction Calculator Project

import java.util. * ;
import java.math.BigInteger;
 // TODO: This program is a calculator. 
 // It works with integers, fractions and mixed numbers 
 // Type "help" for more information!
 // Type "quit" to close program.
public class FracCalc {

   // It is best if we have only one console object for input
   public static Scanner console = new Scanner(System. in );

    // this main method will loop through user input and then call the
    // correct method to execute the user's request for help, test, or
    // the mathematical operation on fractions. or, quit.
   public static void main(String[] args) {
   
      // initialize to false so that we start our loop653
      boolean done = false;
   
      // While we are not done, keep going.
      // When the user types in "quit", we are done.
      while (!done) {
         // prompt the user for input
         String input = getInput();
      
         // special case the "quit" command
         // special case null (won't allow it)
         if (input.equalsIgnoreCase("quit")) {
            done = true;
         } else if (input.equalsIgnoreCase("")) {
            System.out.println("Invalid input");
         } else if (!UnitTestRunner.processCommand(input, FracCalc::processCommand)) {
            // We allowed the UnitTestRunner to handle the command first.
            // If the UnitTestRunner didn't handled the command, process normally.
            String result = processCommand(input);
         
            // print the result of processing the command
            System.out.println(result);
         }
      }
   
      System.out.println("Goodbye!");
      console.close();
   }

    // Prompt the user with a simple, "Enter: " and get the line of input.
    // Return the full line that the user typed in.
   public static String getInput() {
      System.out.print("Enter: ");
      return console.nextLine();
   }

    // processCommand will process every user command except for "quit".
    // It will return the String that should be printed to the console.
    // This method won't print anything.
   public static String processCommand(String input) {
   
      if (input.equalsIgnoreCase("help")) {
         return provideHelp();
      }
   
      // if the command is not "help", it should be an expression.
      // Of course, this is only if the user is being nice.
      return processExpression(input);
   }

    // This method handles the input, calculates and returns.
   public static String processExpression(String input) {
      
      // If the input is invalid, catches 
      // exception and returns "Invalid input"
      // The program will iterate until all preferential expressions
      // have been processed and nothing is left 
      // to calculate except the final answer
      
      boolean done = false;
      try {
         // Tests if input is valid!
         // If not valid, generates an exception which 
         // is handled by top-level try/catch
         isInputValid(input);
      
         // Gives preference to the operator with highest precendence. 
         // Treats operators with same precedence 
         // level from left to right (/ and * for example)
      
         // Has an extra space next to the operator 
         // identifier to differentiate a negative from a subtraction 
         // sign, and a fraction sign from a division sign.
      
         while (!done) {
            // Uses the parenthesesPrecedence method to handle parentheses cases.
            if (input.contains("(")) {
               input = parenthesesPrecedence(input);
            }
            // If no parentheses left, calculates each expression
            // using the operator precedence
            else if (input.contains("+ ") || input.contains("- ") ||
                      input.contains("* ") || input.contains("/ ")) {
               input = operatorPrecedence(input);
            }
            // If no expresions left, ends loop.
            else {
               done = true;
            }
         }
      }
      // Returns invalid input if exception occurs.
      catch(Exception e) {
         return ("Invalid input.");
      }
      
      // removes underscore from final result if there's one and returns it.
      return (fixUnderscoreAndSpace(input));
   }

    // This method tries out a few minor tests into a given input 
    // to check if it's a valid expression.
    // If invalid, program generates exception which is handled by try/catch.
   public static void isInputValid(String input) {
   
      // Gets rid of parentheses to test num of tokens
      input = input.replace("(", "");
      input = input.replace(")", "");
   
      Scanner console = new Scanner(input);
      String test = "";
      // Checks if input has at least 3 tokens
      for (int i = 0; i < 3; i++) {
         test = console.next();
      }
      // Checks if expression has any operators
      if (! (input.contains("+") || input.contains("-") 
         || input.contains("*") || input.contains("/"))) {
         throw new IllegalArgumentException("invalid");
      }
   }
   
    // This method calculates multiple expressions, breaking them up bit by bit
   public static String calculatePreferential(String input, String operator) {
   
      // This method gets an operand before and after a given specific operator 
      // then it calculates this specific expression
      // then replaces part of the original input which contained the 
      // processed expression with the calculated result.
      // Ex: 1 + 2 * 3 -> 1 + 6 -> 7
   
      String firstOperand = leftToken(input, operator);
      String secondOperand = rightToken(input, operator);
   
      input = input.replace(firstOperand + " " + operator + secondOperand,
                            calculate(firstOperand, secondOperand, operator));
      return (input);
   }

    // Collects operand on the left of given sign
   public static String leftToken(String input, String operator) {
      
      // Finds index of operator
      int indexOfCurrentOperator = input.indexOf(operator);
      
      // Crops original input into only whats left of the sign 
      input = input.substring(0, indexOfCurrentOperator);
      String leftToken = "";
      
      // Scans it collecting the last token, which should be the first operand
      Scanner scanLeft = new Scanner(input);
      while (scanLeft.hasNext()) {
         input = scanLeft.next();
      }
      // Returns only the token
      return (input);
   }
   
    // Collects operand on the right of given sign
    // Similar to lefttoken method
   public static String rightToken(String input, String operator) {
      // Finds index of operator
      int indexOfCurrentOperator = input.indexOf(operator);
      // Crops original input into only what's to the right of operator
      input = input.substring(indexOfCurrentOperator + 1);
   
      // Scans it collecting the first token, which should be the second operand.
      Scanner scanRight = new Scanner(input);
      // Returns only the token
      return (scanRight.next());
   }

    // This method handles expressions containing parentheses.
   public static String parenthesesPrecedence(String input) {
   
      // First gets what's inside the parentheses and
      // assigns to a string inputCropped
   
      // looks for the last "(" parentheses in the string, then gets the first ")"
      // after the "(" it had gotten
   
      String inputCropped = input.substring(input.lastIndexOf("(") + 1);
      inputCropped = inputCropped.substring(0, inputCropped.indexOf(")"));
   
      // Tosses the expression into the calculator with precendence
      String result = operatorPrecedence(inputCropped);
   
      // Replaces part of the original input which contained the processed expression
      // with the calculated result.
      return (input.replace("(" + inputCropped + ")", result));
   }
   
    // This method processes the operator precedence
    // And calls the calculate method up for the most preferential
    // expression it has found
   public static String operatorPrecedence(String input) {
   
      boolean done = false;
      while (!done) {
         
         // Looks for Multiplications and Divisions.
         // Uses the indexOf the operators to figure out which one is farther left.
         
         // Handles bug when an operator does not exist, having its index at -1
         // To then mess with the program saying -1 is smaller than the index
         // of the actual operator it was supposed to work with.
         if (input.contains("* ") || input.contains("/ ")) {
            
            if (((input.indexOf("/ ") == -1) || (input.indexOf("* ") < 
                        input.indexOf("/ "))) && !(input.indexOf("* ") == -1)) {
               input = calculatePreferential(input, "* ");
            }
            else {
               input = calculatePreferential(input, "/ ");
            }
            
         }
         
         // Looks for Additions and Subtractions.
         // Uses the indexOf the operators to figure out which one is farther left.
          
         // Handles bug when an operator does not exist, having its index at -1
         // To then mess with the program saying -1 is smaller than the index
         // of the actual operator it was supposed to work with.
         else if (input.contains("+ ") || input.contains("- ")) {
            if (((input.indexOf("- ") == -1) || (input.indexOf("+ ") < input.indexOf("- ")))
                  && !(input.indexOf("+ ") == -1)) {
               input = calculatePreferential(input, "+ ");
            }
            else {
               input = calculatePreferential(input, "- ");
            }
         
         }
         // If no expresions left, ends loop.
         else {
            done = true;
         }
      }
      return (input);
   }
   
    // This method gets the numerator and denominator of an operand.
    // input 1 into parameter "mode" to return numerator and 2 to return denominator
    // Needs the wholeNumber for verification.  
   public static BigInteger getDenOrNum(String operand, int mode,
                                        BigInteger wholeVerifier) {
      // initializes variables and finds numerator with findNumerator method
      BigInteger denominator = new BigInteger("0");
      BigInteger numerator = findNum(operand);
   
      // sees if operand has a fraction
      if (operand.contains("/")) {
         // if so, calls methods to get denominator
         denominator = findDen(operand);
      }
      // if there's no fraction, the denominator has to be 1
      else {
         denominator = new BigInteger("1");
      }
   
      // Corrects numerator number for calculations. 
      // Mixed numbers rule says that if the whole num upfront is negative, 
      //so is the fraction, even though the fraction is written as positive.
      
      // This is a correction for that rule.
      // Basically, if whole number is negative, numerator becomes negative 
      // so the program treats it as a negative fraction and not a positive one.
      if (wholeVerifier.compareTo(new BigInteger("0")) == -1) {
      
         numerator = numerator.multiply(new BigInteger("-1"));
         
      }
   
      // Looks for the mode to return the correct variable
      if (mode == 1) {
         return (numerator);
      }
      else {
         return (denominator);
      }
   }

    // This method finds the numerator       
   public static BigInteger findNum(String input) {
   
      // Initializes my variable, then looks if there's a 
      // whole number in front of the numerator
      // If so, gets everything between the underscore and fractional sign
      BigInteger numerator = new BigInteger("0");
      if (input.contains("_")) {
         String num = input.substring(input.indexOf("_") + 1, input.indexOf("/"));
         numerator = new BigInteger(num);
      }
      // If not, but contains a fraction, gets the
      // whole string up to the fractional sign
      else if (input.contains("/")) {
         String num = input.substring(0, input.indexOf("/"));
         numerator = new BigInteger(num);
      } 
      // If just integer gets the whole thing (sets denominator to 1 later on)
      else {
         String num = input;
         numerator = new BigInteger(num);
      }
      return (numerator);
   }

    // This method finds the value for the denominator
   public static BigInteger findDen(String input) {
   
      // It gets everything that is after the fractional sign "/"
      String den = input.substring(input.indexOf("/") + 1);
      BigInteger denominator = new BigInteger(den);
      return (denominator);
   
   }
   
    // This method finds the whole number if it exists.   
   public static BigInteger findWhole(String input) {
   
      // Initializes variables
      BigInteger whole = new BigInteger("-99");
      String stringWhole = "";
   
      // Evaluates if there's a underscore, and if so, 
      //looks for the underscore and gets what number before it.
      if (input.contains("_")) {
         stringWhole = input.substring(0, input.indexOf("_"));
         whole = new BigInteger(stringWhole);
         return (whole);
      }
      // If not, the whole thing is an integer which I preferred 
      // to call numerator to makes things easier, so it returns 0.
      else {
         return (new BigInteger("0"));
      }
   }

    // This method calculates and returns the answer of a given expression.
   public static String calculate(String firstOperand, String 
                                    secondOperand, String operator) {
   
      // Initializes variables (parts of the answer);
      BigInteger numeratorAnswer = new BigInteger("0");
      BigInteger denominatorAnswer = new BigInteger("0");
      BigInteger wholeAnswer = new BigInteger("0");
   
      // Gets all parts of all fractions
      // getDenOrNum commands need the operand, a mode and 
      // the whole number for verification.
      BigInteger wholeFirst = findWhole(firstOperand);
      BigInteger numeratorFirst = getDenOrNum(firstOperand, 1, wholeFirst);
      BigInteger denominatorFirst = getDenOrNum(firstOperand, 2, wholeFirst);
   
      BigInteger wholeSecond = findWhole(secondOperand);
      BigInteger numeratorSecond = getDenOrNum(secondOperand, 1, wholeSecond);
      BigInteger denominatorSecond = getDenOrNum(secondOperand, 2, wholeSecond);
   
      // Looks for which operator the program should calculate with.
      // Defines both denominator and numerator using equations I came up with.
      // Equations are derived from w_a/b (operator) z_x/y format.
      if (operator.equals("+ ")) {
         // Equation: (a*y+(w*b*y))+(x*b+(z*b*y)) / b*y
         denominatorAnswer = denominatorFirst.multiply(denominatorSecond);
      
         numeratorAnswer = numeratorFirst.multiply(denominatorSecond).add(wholeFirst
                            .multiply(denominatorAnswer)).add(numeratorSecond
                            .multiply(denominatorFirst)
                            .add(wholeSecond.multiply(denominatorAnswer)));
      
      }
      else if (operator.equals("- ")) {
      
         // Equation: (a*y+(w*b*y))-(x*b+(z*b*y)) / b*y
         denominatorAnswer = denominatorFirst.multiply(denominatorSecond);
      
         numeratorAnswer = numeratorFirst.multiply(denominatorSecond).add(wholeFirst
                            .multiply(denominatorAnswer)).subtract(numeratorSecond
                            .multiply(denominatorFirst)
                            .add(wholeSecond.multiply(denominatorAnswer)));
      
      }
      else if (operator.equals("* ")) {
      
         // Equation: (a+(w*b))*(x+(z*y)) / b*y
         denominatorAnswer = denominatorFirst.multiply(denominatorSecond);
      
         numeratorAnswer = numeratorFirst.add(wholeFirst.multiply(denominatorFirst))
                            .multiply(numeratorSecond.add(wholeSecond
                            .multiply(denominatorSecond)));
      
      }
      else if (operator.equals("/ ")) {
      
         // Equation: (a+(w*b))*y / b*(x+(z*y))
         denominatorAnswer = denominatorFirst.multiply(numeratorSecond
                              .add(wholeSecond.multiply(denominatorSecond)));
      
         numeratorAnswer = numeratorFirst.add(wholeFirst.multiply(denominatorFirst))
                           .multiply(denominatorSecond);
      }
   
      // Corrects, reduces fractions, formarts string and 
      //returns the result for the calculated expression.
      return (formatAndReduce(numeratorAnswer, denominatorAnswer, wholeAnswer));
   
   }
   
    // This method processes answer's info, reduces fractions, formarts string and returns it.
   public static String formatAndReduce(BigInteger numeratorAnswer,
                                      BigInteger denominatorAnswer, BigInteger wholeAnswer) {
   
      // Fixes improper fractions, taking out the answer's whole number from the fraction
      // Only proceeds if numerator is bigger than denominator
      // and fraction is not a improper whole number (ex: 4/4, -8/4, 15/3)
      if (numeratorAnswer.abs().compareTo(denominatorAnswer.abs()) == 1 
            && !(numeratorAnswer.gcd(denominatorAnswer).equals(denominatorAnswer))) {
                       
         wholeAnswer = numeratorAnswer.divide(denominatorAnswer);
         numeratorAnswer = numeratorAnswer.subtract((denominatorAnswer
                           .multiply(wholeAnswer)));
         
      }
   
      // Calculates GCD and reduces fraction
      BigInteger myGcd = numeratorAnswer.gcd(denominatorAnswer);
      numeratorAnswer = numeratorAnswer.divide(myGcd);
      denominatorAnswer = denominatorAnswer.divide(myGcd);
      
      // Does normalization to the fraction, always keeping 
      // negative signs on numerator if 
      // one argument is negative and making it all positive if both are negative
      if (denominatorAnswer.compareTo(new BigInteger("0")) == -1) {
         numeratorAnswer = numeratorAnswer.multiply(new BigInteger("-1"));
         denominatorAnswer = denominatorAnswer.multiply(new BigInteger("-1"));
      }
   
   
      // Formats and returns answer
      return (format(wholeAnswer, denominatorAnswer, numeratorAnswer));
   
   }
   
    // This method formats and returns answer
   public static String format(BigInteger wholeAnswer, BigInteger 
                           denominatorAnswer, BigInteger numeratorAnswer) {
      // Initializes variable
      String result = "";
   
      // Formatting:
      
      // Fixes formatting for mixed numbers notation  
      // Remember the mixed number rule? -3/2 is equal to -1_1/2
      // But our parts at this moment would it is say "-1_-1/2"
      // For this reason, it identifies that and fixes it.
      if (wholeAnswer.compareTo(new BigInteger("0")) == -1 && 
            numeratorAnswer.compareTo(new BigInteger("0")) == -1) {
            
         numeratorAnswer = numeratorAnswer.multiply(new BigInteger("-1"));
      }
            
      // if denominator equals 1, its a whole number fraction, so eliminates
      // the denominator by printing only the numerator (ex: 14/1 -> 14)      
      if (denominatorAnswer.equals(new BigInteger("1"))) {
         result = numeratorAnswer + "";
      }
      
      // if no whole number, first checks if the two parameters 
      // are equal, if so, prints 1  (ex: 4/4, 1/1, 2/2 -> 1).
      // if not, formats into common fraction (ex: 2/3, 8/9, 17/13), 
      else if (wholeAnswer.equals(new BigInteger("0"))) {
      
         if (numeratorAnswer != denominatorAnswer) {
            result = numeratorAnswer + "/" + denominatorAnswer;
         }
         else {
            result = 1 + "";
         }
      }
       
      // if none of those cases, formats into mixed number answer
      // which is the last possible case remaining
      else {
         result = wholeAnswer + "_" + numeratorAnswer + "/" + denominatorAnswer;
      }
   
      // returns result of expression
      return (result);
   }
   
    // This method removes the underscore of a given 
    // string, replacing it with a space.
    // Also removes extra spaces from answer from a bad user input.
   public static String fixUnderscoreAndSpace(String answer) {
   
      // removes spaces from answer
      Scanner fixer = new Scanner(answer);
      answer = fixer.next();
   
      // removes underscore if there's one
      if (answer.contains("_")) {
         answer = answer.replace("_", " ");
         return (answer);
      }
      // returns final answer
      return (answer);
   
   }
   
    // Returns a string that is helpful to the user about how
    // to use the program.
   public static String provideHelp() {  
      String help = "Here's your help: \n";
      help += "\n";
      help += "Type \"quit\" to close program.\n";
      help += "This program is a calculator.\n \n";
      help += ">It accepts integers, fractions and mixed numbers\n";
      help += ">Format is: [operand][space][operator][space][operand]\n";
      help += ">Negative numbers need to be prefixed with a \"-\"\n";
      help += ">Inputs should only contain 4 types of operator: \"+\", \"-\",";
      help += " \"*\", and \"/\"\n";
      help += ">Inputs may contain muliple operands as long ";
      help += "as they are spaced correctly\n";
      help += ">You may use parentheses to give preference ";
      help += "to a specific expression. \n";
      help += ">Precedence order is (), * and /, - and + \n";
      help += ">Spacing needs to be correct and whole numbers ";
      help += "separated from fractions using underscores\n";
      help += " ex: 1 and 1/2 should be 1_1/2\n";
      help += ">Mixed numbers reminder: If whole number upfront ";
      help += "is negative, fraction is also negative\n";
      help += " ex: -1_1/2 = -3/2\n";
      help += ">Infinite precision! The limit is the sky.\n \n";
      help += ">Examples:\n";
      help += "2/5 + 7/8\n";
      help += "12 * 32\n";
      help += "9_1/2 / -7_16/19\n";
      help += "7_1/4 / -2_26/15 + 23 - -2_1/2 * 73\n";
      help += "(1 + 2) * 5 - (6 + -7_1/2)\n";
      help += "5 + (((1 - 2) * (1 - 2)) * (1 - 2))\n";
      return help;
   }
}