package blake_s_ch_20_e_13;

import java.util.Collections;
import java.util.LinkedList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*************************************************************************************\
 * Chapter 15 Exercise 1
 * 9/21/2021
 * @author Blake S
 * This program plays the 24 point game.  In a window, you are given four card images.  
 * Each card has a value, 1 for aces through 11, 12, and 13 for Jacks, Queens, and Kings 
 * respectively.  Mousing over the card displays its value.
 * 
 * The goal is to use any mathematical expression that uses each number once that evaluates
 * to 24 (rounded down for the result, as its converted to int at the very end).
 * 
 * When you win or when you click the "Draw next hand" button, you put your current hand on the
 * bottom of the deck and draw the top four cards.  You can also click "Shuffle and draw"
 * to  shuffle your old hand into the deck and draw a new set of four cards.
 * 
 * This program re-uses and revises a small amount of code from Exercise 15.1, but was mostly
 * re-written.
\*************************************************************************************/
public class Blake_S_Ch_20_e_13 extends Application {
    
    //has to be a data field due to the nature of event handling in Java.
    
    
    /**
     * Default required launch method.
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {
        
        Label infoLbl = new Label("24 Point Game");
        VBox basePane = new VBox();
        HBox inputPane = new HBox();
        Button drawBtn = new Button("Draw next hand");
        Button shuffleBtn = new Button("Shuffle and draw");
        Button submitBtn = new Button("Submit");
        TextField inputTxt = new TextField();
        
        
        CardBox cardPane = new CardBox(new Deck(loadCards()));
        
        basePane.setAlignment(Pos.CENTER);
        basePane.setSpacing(10);
        
        cardPane.setAlignment(Pos.CENTER);
        cardPane.setSpacing(5);
        
        inputPane.setAlignment(Pos.CENTER);
        inputPane.setSpacing(10);
        
        inputPane.getChildren().addAll(inputTxt, submitBtn, drawBtn, shuffleBtn);
        basePane.getChildren().add(infoLbl);
        basePane.getChildren().add(cardPane);
        basePane.getChildren().add(inputPane);
        
        submitBtn.setOnAction((ActionEvent e) -> {
            //this is where the logic for the math is checked.
            if(inputTxt.getText().contains("" + cardPane.getHandVal(0)) && inputTxt.getText().contains("" + cardPane.getHandVal(1)) && inputTxt.getText().contains("" + cardPane.getHandVal(2)) && inputTxt.getText().contains("" + cardPane.getHandVal(3))) {
                if((int)(eval(inputTxt.getText())) == 24) {
                    infoLbl.setText("Correct!");
                    cardPane.redraw();
                }
                else {
                    infoLbl.setText("Incorrect");
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter all the values for the cards.", ButtonType.OK);
                alert.showAndWait();
            }
        });
        
        drawBtn.setOnAction((ActionEvent e) -> {
            cardPane.redraw();
        });
        
        shuffleBtn.setOnAction((ActionEvent e) -> {
            cardPane.shuffleDraw();
        });
        
        inputTxt.setOnMouseEntered((Event e) -> {
            infoLbl.setText("24 Point Game");
        });
        
        
        
        Scene scene = new Scene(basePane);
        
        primaryStage.setTitle("24 point game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * the usual thing.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    /**
     * This method loads card png files into an Image array.  This method is set
     * to work with a specified number of 54 cards.  If you removed the Jokers, this
     * method would break without first editing it.  Doing so is outside the scope 
     * of the assignment however.
     * @return an Image array containing 54 playing card images.
     */
    public Card[] loadCards() {
        Card[] cards = new Card[52]; //without jokers
        
        //its a good thing the files have numbers as names.
        for(int i = 0; i < cards.length; i++) {
            cards[i] = new Card(new Image("card/" + (i + 1) + ".png"), i + 1);
        }
        return cards;
    }
    
    /**
     * This method is imported from StackOverflow from a user named Boann, who
     * released it into the public domain.  It takes a String value and evaluates
     * the mathematical equation contained within.  You can find the source document on
     * this question: https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
     * I chose to do this because the string evaluation isnt the important part for the
     * chapter being evaluated.
     * @param str the equation to be evaluated.
     * @return the solution to the equation as a double.
     */
    public static double eval(final String str) {
    return new Object() {
        int pos = -1, ch;

        void nextChar() {
            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();
            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
            return x;
        }

        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor
        // factor = `+` factor | `-` factor | `(` expression `)`
        //        | number | functionName factor | factor `^` factor

        double parseExpression() {
            double x = parseTerm();
            for (;;) {
                if      (eat('+')) x += parseTerm(); // addition
                else if (eat('-')) x -= parseTerm(); // subtraction
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            for (;;) {
                if      (eat('*')) x *= parseFactor(); // multiplication
                else if (eat('/')) x /= parseFactor(); // division
                else return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return parseFactor(); // unary plus
            if (eat('-')) return -parseFactor(); // unary minus

            double x;
            int startPos = this.pos;
            if (eat('(')) { // parentheses
                x = parseExpression();
                eat(')');
            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(str.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') { // functions
                while (ch >= 'a' && ch <= 'z') nextChar();
                String func = str.substring(startPos, this.pos);
                x = parseFactor();
                if (func.equals("sqrt")) x = Math.sqrt(x);
                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                else throw new RuntimeException("Unknown function: " + func);
            } else {
                throw new RuntimeException("Unexpected: " + (char)ch);
            }

            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

            return x;
        }
    }.parse();
}
    
    
    /**
     *  a custom HBox that can re-draw images, or "redraw" them in this case.
     */
    private static class CardBox extends HBox {
        Deck deque;
        
        public CardBox(Deck deque) {
            this.deque = deque;
            deque.shuffle();
            redraw();
        }
        
        /**
         *
         * Clears the cards in the box and sets out new ones.
         */
        public void redraw() {
            getChildren().clear();
            getChildren().addAll(deque.drawHand());
        }
        
        public void shuffleDraw() {
            deque.shuffle();
            redraw();
        }
        
        public int getHandVal(int index) {
            return deque.getHandVal(index);
        }
        
        public int getHandSize() {
            return deque.getHandSize();
        }
    }
    
    /**
     * This class simulates the deck, keeping track of the cards and their numbers.
     * 2022 Blake here.  The original implementation had decisions that made it difficult 
     * to use for anything that required manipulating the deck or knowing things about 
     * the cards.  By reorganizing into a class, I can more easily keep track of that information.
     */
    class Deck {
        
        private LinkedList<Card> deque;
        private int[] handVal = new int[4];
        
        Deck(Card... cards) {
            deque = new LinkedList();
            
            for(Card c : cards)
                deque.offer(c);
        }
        
        /**
         * getter for the values of the current hand.
         * @param index the card to get the value from.  Currently, 0 to 4 are the
         * only acceptable values.
         * @return the value for the card in hand or -1 if out of bounds.
         */  
        public int getHandVal(int index) {
            try {
            return handVal[index];
            }
            catch(ArrayIndexOutOfBoundsException e) {
                return -1;
            }
        }
        
        public int getHandSize() {
            return handVal.length;
        }
        
        /**
         * This method shuffles the deck of cards.
         * This is not an efficient method for shuffling.  One of the problems
         * is that, since it is iterating through the list, it can put cards at the bottom
         * of the deck and make it take longer to shuffle with a chance inverse to the number
         * of remaining cards.  This makes it hard to also put a notation to it.  Also like the
         * previous shuffle method, this does not behave like real-world shuffling, meaning
         * the results will be different than what you can expect in real life.
         */
        void shuffle() {
            Collections.shuffle(deque);
        }
        
        /**
         * This method draws four cards from the deck as ImageView objects.  It does
         * not clear the cards from the deque, however- it moves them to the bottom.
         * @return the four card images as 
         */
        public ImageView[] drawHand() {
            ImageView[] hand = new ImageView[4];
            
            for(int i = 0; i < hand.length; i++) {
                hand[i] = new ImageView(deque.peek().getImage());
                handVal[i] = deque.peek().getValue();
                
                deque.offerLast(deque.poll());
                
                //This is an optional thing I'm doing where I install a tooltip onto the ImageView
                Tooltip.install(hand[i], new Tooltip("" + handVal[i]));
            }
            return hand;
        }
        
        
    }
    
    /**
     * This is a wrapper class for an Image that also attaches other information
     * to that image, such as the numerical value of the card and the sorted number
     * of the card.
     */
    class Card {
        int number;
        int value;
        Image card;
        
        Card(Image img, int num) {
            card = img;
            number = num;
            value = (int)((num % 13 == 0 ? 13 : num % 13));
        }
        
        int getValue() {
            return value;
        }
        
        int getNumber() {
            return number;
        }
        
        Image getImage() {
            return card;
        }
    }
    
     /*
     * depreciated, but this text can stay here.
     * 
     * This method takes an array containing Images of cards, shuffles them, 
     * draws four cards, and returns the hand as a ready-to-use ImageView array.
     * 
     * While the program only uses one deck, I tried to make this work with any number
     * of cards.  If you remove two joker images, this method will still work.  However,
     * to draw more or less than four cards requires editing.
     * (NOTE: Blake from 2022 here.  I removed the jokers)
     * 
     * A fun fact: machine-based random functions are going to produce subtly different
     * results from a human bridge-shuffle.  If you wanted to try to mimic it, you'd need
     * to split a list into two lists from the middle, then put a card from one list at +2 to -2
     * in the other list.  Then you'd want to repeat that process about 4 times for 
     * optimal randomness.  This is a simple program meant to produce simple results,
     * but you can tell a well thought-out card game player from a poor one by the 
     * shuffle results.
     * 
     * NOTE: Hey, Blake from 2022 here. I also added a tooltip functionality to these 
     * card images so a mouse-over shows the card value.
     * 
     * @param cards the deck you want to draw from.
     * @return an ImageView array representing a random hand from the deck.
     */
    /*
    public static ImageView[] drawHand(Image... cards) {
        //this is here to prevent duplicates.
        int[] numbers = {cards.length + 1, cards.length + 1,
                         cards.length + 1, cards.length + 1}; //starting with this since its an unreachable result
        
        //is is good practice to define variables to prevent possible null errors or not?
        int randNum = 0;
        boolean isRepeated = false;
        int counter = 0;
        
        //maybe there's a more elegant way to do this, but I didnt want to look it up.
        while(numbers[3] >= cards.length) {
            //this works because Math.random() selects a number between 0 and 1 
            //but never 1.0, and casting always rounds down.
            randNum = (int)(Math.random() * cards.length);
            
            //this is here is to prevent duplicates.
            for(int i = 0; i < numbers.length; i++) {
                isRepeated = (numbers[i] == randNum);
                if(isRepeated)
                    break;
            }
            
            if(!isRepeated) {
                numbers[counter] = randNum;
                counter++;
            }
        }
        
        ImageView[] cardViews = {new ImageView(cards[numbers[0]]), 
                                 new ImageView(cards[numbers[1]]), 
                                 new ImageView(cards[numbers[2]]), 
                                 new ImageView(cards[numbers[3]])};
        
        //Hey, Blake from 2022 here.  Thanks for saving the numbers in an array.
        //this little bit of text installs a tooltip on each image.  When they get mouse-overed,
        //it displays thier value.
        for(int i = 0; i < numbers.length; i++)
            Tooltip.install(cardViews[i], new Tooltip("" + ((int)((numbers[i] % 13)) + 1)));
        //This part was easy, but there's a problem I need to overcome.  I need to somehow
        //extract numbers[] into other parts of the program.  I suppose I can move the random
        //function to start()?
        
        return cardViews;
    }
    */
}

