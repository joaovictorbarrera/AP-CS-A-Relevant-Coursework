// John Barrera
// AP CS A
// Period 1
// Spell Checker
//
// This program's purpose is to spell-check a text file
// using a dictionary word-list.
//
// It outputs the errors, the location of each error 
// in the file, and attempts to guess the correct 
// intended word for each error. 
// At the end, outputs the summary of information.

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

class Main {
   public static void main(String[] args) {
      SpellChecker sp = new SpellChecker("wordList.txt");
      sp.doSpellCheck("exampleEssay.txt");
   }
}
   
public class SpellChecker {
   
   // Declares all instance fields
   private ArrayList<String> dictionary = new ArrayList<String>();
   private String word;
   private String hyphenatedFirst;
   private String hyphenatedSecond;
   private int errorsCount;
   private int textFileWordAmount;
   private String[] alphabet = {"a","b","c","e","d","f","g","h","i",
                              "j","k","l","m","n","o","p","q","r","s",
                              "t","u","v","w","x","y","z"};
   
   // Loads dictionary when object is created.
   public SpellChecker(String dictionaryFileName) {  
      loadDictionary(dictionaryFileName);
   }
   
   // This will load all the words in the dictionary file into an
   // array that will be used as the dictionary of correclty spelled words.
   //
   // Parameters:
   //  - fileName : the name of the file using a relative path.
   //
   // Returns/outputs: nothing
   //
   // Pre-conditions:
   //  - No word has a space within it.
   //  - You do not know the number of words in the file.
   //  - The words will be sorted.
   private void loadDictionary(String fileName) {
      try {
         Scanner parser = new Scanner(new File(fileName));
          
         // Loads the dictionary file, adds
         // each token to the list
         parser = new Scanner(new File(fileName));
         while(parser.hasNext()) {
            dictionary.add(parser.next());
         }
         
         parser.close();
      }
      catch(Exception FileNotFoundException) {
         // Catches exception if file does not exist
         System.out.printf("\n%s was not found in the directory", fileName);
      }
   }
   
   // This will load the text file and check the spelling of each word.
   //
   // Parameters:
   //  - fileName : the name of the file to be spell checked.
   //
   // Outputs:
   //   If a word is misspelled, it will output to the console:
   //     Line   <#>, Word   <#>, <misspelling>
   //   The format will be: "Line %3d,  Word %2d", %s\n"
   //
   // Pre-conditions:
   //  - The file will contain punctuation.
   //  - The words may have upper and lowercase letters.
   public void doSpellCheck(String fileName) {
      try {
         // Declares a line counter and word(within current line) counter.
         int linesCount = 0;
         int wordsCount = 0;  
         
         Scanner parseLines = new Scanner(new File(fileName));
         while(parseLines.hasNextLine()) {
            // Parses an entire line from the text file and puts it into a String
            // Increases a line counter
            String line = parseLines.nextLine();
            linesCount++;
            
            Scanner parseWords = new Scanner(line);
            while(parseWords.hasNext()) {
               // Parses words from each line
               // Increases a word counter
               word = parseWords.next();
               wordsCount++;  
               
               // Uses the isSpeltRight boolean method to determine
               // whether a word is or isn't spelt correctly.
               // if NOT, then prints it to the console, together with the counters and the word
               // after that, gets and prints word suggestions
               // and at last, increases an error counter

               if(!isSpeltRight()) {
                  //System.out.printf("\nLine %3d,  Word %2d, %s", linesCount, wordsCount, word);
                  //getSuggestions();
                  errorsCount++;
               }
            } 
            // Resets word counter back to zero every 
            // time the program finishes analyzing a line
            wordsCount = 0;
            parseWords.close();
         }
         parseLines.close();
         
         // uses the printSummary method to print the summary
         // containing the amount of errors and amount of words in the file               
         printSummary();
      }
      catch(FileNotFoundException exception) {
         System.out.printf("\n%s was not found in the directory", fileName);
      }
      catch(Exception e) {
         System.out.println(e);
      }
   }

   // This will normalize words and check the spelling of them.
   //
   // Parameters: None
   //
   // Returns:
   //   Returns boolean information of whether the word
   //   is spelt correctly (true) or not (false).
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - "word" String must exist and contain a valid word within the class. 
   //  - "word" String may contain punctuation.
   //  - "word" String may have upper and lowercase letters.
   //  - "word" may be a hyphenated word.
   private boolean isSpeltRight() {
      boolean found = false;
      // If a word is being tested, then it exists in the file
      // so increases a file word counter
      textFileWordAmount++;
      
      // if word is a hyphenated word, treats it specially.
      if(word.contains("-")) {
         // Breaks up hyphenated word into two words using
         // the location of the hyphen.
         // Normalizes each piece.
         this.hyphenatedFirst = word.substring(0, word.indexOf("-")).
                                       toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
         this.hyphenatedSecond = word.substring(word.indexOf("-") + 1).
                                       toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
         
         // Uses the searchDict method to search each of the parts in the dictionary
         // if both are found, then updates the "found" boolean to true.                                 
         if(searchDict(this.hyphenatedFirst) && 
                     searchDict(this.hyphenatedSecond)) {
            found = true;
         }
         
         // Builds the word again using the pieces for normalization/printing purposes.
         word = this.hyphenatedFirst + "-" + this.hyphenatedSecond;
         
      } else {
         // if not a hyphenated word, word is normalized
         // and searched in the dictionary by using the searchDict method.
         //
         // Updates the "found" boolean to true if word is found in the dictionary.
         word = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
         found = searchDict(word);
      }
      
      // returns if word was found (true) or not (false).
      return(found);      
   }
   
   // This will look for a certain word in the dictionary file.
   //
   // Parameters:
   //    word: The word that will be searched.
   //
   // Returns or Outputs:
   //    Returns a boolean indicating whether 
   //    the word was found or not.
   //
   // Pre-conditions:
   //  - The dictionary array must exist within the class
   private boolean searchDict(String word) {
      return Arrays.binarySearch(dictionary.toArray(), word) >= 0;
   }
   
   // This will take a misspelled word and try to guess
   // which word it was supposed to be. If word is hyphenated, makes 
   // suggestions on each side and combines into many possibilies.
   //
   // Up to 5 suggestions on screen.
   //
   // Parameters: none
   //
   // Returns or Outputs:
   //    Outputs the suggestions in the following format:
   //    [suggestion1, suggestion2, ...]
   //
   // Pre-conditions:
   //  - "word" String must exist and contain a valid normalized word within the class.  
   //  - If a hyphenated word is being made suggestions, then "hyphenatedFirst" and "hyphenatedSecond"
   //    must exist and contain a valid normalized word within the class.
   private void getSuggestions() {
      ArrayList<String> suggestions = new ArrayList<String>();
      
      // First determines if the word being analyzed is a hyphenated word or not.
      if(this.word.contains("-")) {
         // Adds to a suggestion list every possibility of first word with second word considering
         // that each part may have multiple suggestions. Suggestions are made with the makeSuggestions method.
         for(String leftSugg : makeSuggestions(this.hyphenatedFirst)) {
            for(String rightSugg : makeSuggestions(this.hyphenatedSecond)) {
               suggestions.add(leftSugg + "-" + rightSugg);
            } 
         }
      }
      else {
         // if not hyphenated, just adds to a suggestion list all the 
         // suggestions found with the makeSuggestions method.
         suggestions.addAll(makeSuggestions(this.word));
      }
      
      // If array list contains less than 5 items, uses to toString to print it all
      // if more than 5 items, prints the first 5 items of the array by traversing it and using fencepost.
      if(suggestions.size() < 5) {
         System.out.print("\n" + suggestions.toString());
      }
      else {
         System.out.print("\n[" + suggestions.get(0));
         for(int i = 1; i < 5; i++) {
            System.out.print(", " + suggestions.get(i));
         }
         System.out.print("]");
      }
   }
   
   // This will call on several methods which are all algorithms 
   // and combine each suggestion list produced by each of them
   // into one single final one.
   //
   // Parameters:
   //    word: the word to be made suggestions with.
   //
   // Returns:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method.
   //
   // Pre-conditions: 
   // - "word" String must exist and contain a valid normalized word.  
   private ArrayList<String> makeSuggestions(String word) {
      // Creates an array list of suggestions for the current misspelled word.
      // Calls on several methods (which are little algorithms) to add more and more suggestions to the list.
      //
      // Farther to the left are always the suggestions that are more likely, 
      // these are the ones that will be printed first.
      //
      // Be noted that if word exists in the dictionary, 
      // no algorithm is called (this is for hyphenated words)
      ArrayList<String> suggestions = new ArrayList<String>();
      if(!searchDict(word)) {
         suggestions = addAllElements(suggestions, missingLetter(word)); 
         suggestions = addAllElements(suggestions, shuffledLetters(word)); 
         suggestions = addAllElements(suggestions, additionalLetter(word)); 

         // in last case scenario, if nothing was found
         // calls on the brute force method.
         if(suggestions.size() == 0 && word.length() > 1) {
            suggestions = addAllElements(suggestions, similarLetters(word));
         }
      }
      else {
         suggestions.add(word);
      }
      return(suggestions);
   }
   
   // This will merge two arrayLists into a single one without
   // repeating any elements. 
   //
   // Parameters:
   //    a1: primary arrayList (the one that's getting stuff added)
   //    a2: secondary arrayList (the one being scanned for duplicated and being
   //    appended to the first one)
   //
   // Returns:
   //    ArrayList with merged result.
   //
   // Pre-conditions: 
   // - Duplicate elements are exactly equal, no spaces or anything allowed.
   private ArrayList<String> addAllElements(ArrayList<String> a1, ArrayList<String> a2) {
      // Traverses the second array adding each
      // non duplicate element to the end of the first array.
      for(String x : a2) {
         if(!a1.contains(x)) {
            a1.add(x);
         }
      }
      return(a1);
   }
      
   // This will take a misspelled word and test every possibility
   // of alphabet letter added at any index of the word in a dictionary search.
   // If exists in the dictionary, saves and then returns as suggestion.
   // 
   // This technique would be suitable for the following examples:
   // asignment -> assignment | ord -> word | banan -> banana
   //
   // Parameters: 
   //    word: the word to be made suggestions with.
   //
   // Returns or Outputs:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method.
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - "word" String must exist and contain a valid normalized word within the class.  
   //  - Alphabet array must exist and have all 
   //    letters of the alphabet in it within the class.
   private ArrayList<String> missingLetter(String word) {
      
      // For each letter, concatenate a new String of each position 
      // this letter could be in the word.
      // If produced variant exists in the dictionary, add it to an arrayList.
      ArrayList<String> suggestions = new ArrayList<String>();
      for(String letter : alphabet) {
         for(int j = 0; j < word.length()+1; j++) {
            String newWord = word.substring(0,j) + letter + word.substring(j);
         
            if(searchDict(newWord)) {
               suggestions.add(newWord);
            }
         }
      }
      // Return arrayList with all suggestions created in this method.
      return(suggestions);
   }
   
   // This will take a misspelled word and test every possibility
   // of removing one letter from the word, searching the dictionary
   // and saving(then returning) existing words.
   //
   // This includes before the word (uEXAMPLE), after the word (EXAMPLEy)
   // and at each spot in the middle of it (EXAMbPLE)
   //
   // This technique would be suitable for the following examples:
   // asssignment -> assignment | woord -> word | bananan -> banana
   //
   // Parameters: 
   //    word: the word to be made suggestions with.
   //
   // Returns or Outputs:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method.
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - "word" String must exist and contain a valid normalized word within the class.  
   private ArrayList<String> additionalLetter(String word) {
      ArrayList<String> suggestions = new ArrayList<String>();
      
      // For each index of the misspelled word, concatenate
      // a new String without the char at each position.
      //
      // If generates a valid word (is found in the dictionary)
      // then add to my suggestions list.
      for(int i = 0; i < word.length(); i++) {
         String wordWithoutLetter = word.substring(0,i) + word.substring(i+1);
         if(searchDict(wordWithoutLetter)) {
            suggestions.add(wordWithoutLetter);
         } 
      }
      // Return arrayList with all suggestions created in this method.
      return(suggestions);
   }
   
   // This will take a misspelled word and test if there's
   // a word in the dictionary with the same length and same
   // letters as the misspelled one.
   //
   // This technique would be suitable for the following examples:
   // assignmetn -> assignment | wrod -> word | nnabaa -> banana
   //
   // Parameters: 
   //    word: the word to be made suggestions with.
   //
   // Returns or Outputs:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method.
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - word String must contain a valid normalized word within the class.  
   private ArrayList<String> shuffledLetters(String word) {
      ArrayList<String> suggestions = new ArrayList<String>();
      
      // Creates an array of characters of the
      // misspelled word and sorts it
      char[] lettersOfWord = word.toCharArray();
      Arrays.sort(lettersOfWord);
      for(String dictWord : dictionary) {
         // Then, creates an array of characters of each
         // dictionary word and sorts it, if it matches the misspelled one,
         // adds it to the an arraylist of suggestions.
         char[] lettersOfDictWord = dictWord.toCharArray();
         Arrays.sort(lettersOfDictWord);
         if(new String(lettersOfWord).equals(new String(lettersOfDictWord))){
            suggestions.add(dictWord);
         }
      }
      // Return arrayList with all suggestions created in this method.
      return(suggestions);    
   }
   
   // This is a last case technique. It takes a misspelled word and
   // guesses similar words by how similar they are in length and letters.
   //
   // This technique would be suitable for the following examples:
   // ettiquitt -> etiquette | literelly -> literally | banena -> banana
   //
   // Parameters: 
   //    word: the word to be made suggestions with.
   //
   // Returns or Outputs:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method.
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - word String must contain a valid normalized word within the class.  
   private ArrayList<String> similarLetters(String word) {
      ArrayList<String> suggestions = new ArrayList<String>();
      
      // Creates an array of character containing the letters
      // of the misspelled word. Calls on the similarLetterSearch method
      // with a tolerance of 1 difference and equal word length.
      char[] lettersOfWord = word.toCharArray();
      suggestions = similarLetterSearch(lettersOfWord, 1, 0);
      
      // If nothing is found, gives some more tolerance by increasing both
      // the difference tolerance and length tolerance by 1. Keeps doing this until
      // a suggestion is gathered or looped 3 times and nothing was found.
      int counter = 1;
      while(suggestions.size() == 0 && counter != 3) {
         suggestions = similarLetterSearch(lettersOfWord, 1 + counter, 0 + counter);
         counter++;
      } 
      
      // Return arrayList with all suggestions created in this method.
      return(suggestions);
   }
   
   // This is the search mechanism of the previous method. It will search the 
   // dictionary for words that start with the same two letters as the misspelled given one
   // and are of misspelled word length - 1 or bigger.
   //
   // If these have enough similar letters and a certain set max length, then suggests them to the user. 
   //
   // This technique would be suitable for the following examples:
   // ettiquitt -> etiquette | literelly -> literally | banena -> banana
   //
   // Parameters: 
   // lettersOfWord: array with the characters of misspelled word
   // diffTolerance: number indicating how many differences are tolerated 
   //                between the misspelled word and the dictionary word.
   // lengthTolerance: number indicating whats the maximum size of dictionary word
   //              in relation to the misspelled one (0 for same length, 1 for length + 1)
   //
   // Returns or Outputs:
   //    Returns an ArrayList with all the suggestions 
   //    created within this method. 
   //
   // Pre-conditions:
   //  - Dictionary array must exist and be loaded within the class.
   //  - word String must contain a valid normalized word within the class.  
   //  - char[] lettersOfWord's elements are the same letters/hyphen in the same order as "word" String. 
   private ArrayList<String> similarLetterSearch(char[] lettersOfWord, int diffTolerance, 
                                                int lengthTolerance) {
      ArrayList<String> suggestions = new ArrayList<String>();
      
      for(String dictWord : dictionary) {
         // For each dictionary word, first validates if current word is at 
         // least of misspelled word Length - 1 or bigger; 
         // doesn't go over misspelled word Length + lengthTolerance; 
         // and starts with the same two letters.
         if(dictWord.length() >= lettersOfWord.length - 1 && 
            dictWord.length() <= (word.length() + lengthTolerance) &&
            dictWord.charAt(0) == lettersOfWord[0] && 
            dictWord.charAt(1) == lettersOfWord[1]) {
            
            // Creates a separate string dictWordTemp which will maintain
            // the original value, while dictWord is being manipulated.
            String dictWordTemp = dictWord;
            
            // Creates a counter for the amount of similar letters.
            int equalLettersCount = 0;
         
            for(char c : lettersOfWord) { 
               // For each element(letter) in the misspelled word char array
               // Sees if its contained in the current dictionary word, 
               // if so, removes it from the string (words can have the same letter multiple times)
               // and increases the counter by 1.
               String letter = Character.toString(c);
               if(dictWord.contains(letter)) {
                  dictWord = dictWord.replaceFirst(letter, "");
                  equalLettersCount++;
               }
            }
            
            // Evaluates whether the counted amount of similar letters satisfies
            // the paremether of tolerated differences.
            // If so, adds word as suggestion to an arrayList.
            if(equalLettersCount >= (lettersOfWord.length - diffTolerance)) {
                  
               suggestions.add(dictWordTemp);
            }
         }
      }
      // Return arrayList with all suggestions created in this method.
      return(suggestions);
   }
   
   // This prints the summary information of the spellchecking process.
   // It will print the amount of errors (misspelled words) and the amount of words in the file checked.
   //
   // Parameters: None
   //
   // Returns or Outputs:
   //    Outputs the amount of errors and the amount of words in the file checked.
   //    Will be in the format: 
   //    "\n\nDocument contains %d error(s)."
   //    "\nThere are %d words in the file."
   //
   // Pre-conditions:
   // - errorsCount must exist and contain a value within the class
   // - textFileWordAmount must exist and contain a value within the class
   private void printSummary() {
      System.out.printf("\n\nDocument contains %d error(s).", errorsCount);
      System.out.printf("\nThere are %d words in the file.", textFileWordAmount);
   }
}