import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class GameMain extends JPanel implements MouseListener{
	//Constants for game 
	//Number of ROWS by COLS cell constants 
	public static final int ROWS = 3;     
	public static final int COLS = 3;  
	public static final String TITLE = "Tic Tac Toe";

	//Constants for dimensions used for drawing
	//Cell width and height
	public static final int CELL_SIZE = 100;
	//Drawing canvas
	public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
	public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
	//Noughts and Crosses are displayed inside a cell, with padding from border
	public static final int CELL_PADDING = CELL_SIZE / 6;    
	public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;    
	public static final int SYMBOL_STROKE_WIDTH = 8;
	
	//Declare game object variables
	//The game board 
	private Board board;
	//Create a GameState called currentState
	private GameState currentState; 
	//The current player
	private Player currentPlayer; 
	//JLabel for displaying game status message
	private JLabel statusBar;       
	//Booleans to check for and end the game after a win/draw
	Boolean gameEnded = false;
	Boolean gameEndedCheck = false;
	
	/**Constructor to setup the UI and game components on the panel */
	public GameMain() {   
		
		//Initialise MouseListener to operate on the MouseEvent of MouseClicked
		this.addMouseListener(this);
        addMouseListener(this);
	    
		//Setup the status bar (JLabel) to display status message       
		statusBar = new JLabel("         ");       
		statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));       
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));       
		statusBar.setOpaque(true);       
		statusBar.setBackground(Color.LIGHT_GRAY);  
		
		//Layout of the panel is in border layout
		setLayout(new BorderLayout());       
		add(statusBar, BorderLayout.SOUTH);
		//Account for statusBar height in overall height
		setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 30));
		
		//Check to make sure board has a new instance AND the method initialises the game board.
		board = new Board();
		currentState = GameState.Playing;
		currentPlayer = Player.Cross;

	}
	
	public static void main(String[] args) {
		//Run GUI code in Event Dispatch thread for thread safety.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	         public void run() {
				//create a main window to contain the panel
				JFrame frame = new JFrame(TITLE);

				frame.add(new GameMain());
				
				frame.pack();             
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	         }
		 });
	}
	
	/**Custom painting codes on this JPanel */
	public void paintComponent(Graphics g) {
		//Fill background and set colour to white
		super.paintComponent(g);
		
		setBackground(Color.WHITE);
		//Ask the game board to paint itself
		board.paint(g);
		
		//Set status bar message
		if (currentState == GameState.Playing) {          
			statusBar.setForeground(Color.BLACK);          
			if (currentPlayer == Player.Cross) {   
			
				statusBar.setText("X's turn");
			}
			
			else {    
				statusBar.setText("O's turn");
			}
		}
		
		else if (currentState == GameState.Draw) {          
			statusBar.setForeground(Color.RED);          
			statusBar.setText("It's a Draw! Click to play again.");       
		}
		
		else if (currentState == GameState.Cross_won) {          
			statusBar.setForeground(Color.RED);          
			statusBar.setText("'X' Won! Click to play again.");       
		}
		
		else if (currentState == GameState.Nought_won) {          
			statusBar.setForeground(Color.RED);          
			statusBar.setText("'O' Won! Click to play again.");       
		}
	}
		
	
	/**Initialise the game-board contents and the current status of GameState and Player) */
	public void initGame() {
		for (int row = 0; row < ROWS; ++row) {          
			for (int col = 0; col < COLS; ++col) {  
				//All cells empty
				board.cells[row][col].content = Player.Empty;           
			}
		}
		 currentState = GameState.Playing;
		 currentPlayer = Player.Cross;
	}
	
	
	/**
	 * After each turn check to see if the current player has Won by putting their symbol in that position, 
	 * If they have the GameState is set to won for that player
	 * If no winner then isDraw is called to see if deadlock, if not GameState stays as PLAYING 
	 */
	public void updateGame(Player thePlayer, int row, int col) {
		//Check for win after play; set winner, indicate that the game has ended with gameEndedCheck
		if(board.hasWon(thePlayer, row, col)) {
			
			if (currentPlayer == Player.Cross) {
				currentState = GameState.Cross_won;
				gameEndedCheck = true;
			}
			
			if (currentPlayer == Player.Nought) {
				currentState = GameState.Nought_won;
				gameEndedCheck = true;
			}
		
		//Check for draw after play; set as draw, indicate that the game has ended with gameEndedCheck
		} else 
			if (board.isDraw ()) {
				currentState = GameState.Draw;
				gameEndedCheck = true;
		}
		
		//Otherwise no change to current state of playing
	}
	
			

	/** 
	 * Event handler for the mouse click on the JPanel. If selected cell is valid and Empty then current player is added to cell content.
	 * UpdateGame is called which will call the methods to check for winner or Draw. if none then GameState remains playing.
	 * If win or Draw then call is made to method that resets the game board.  Finally a call is made to refresh the canvas so that new symbol appears
	 */

	public void mouseClicked(MouseEvent e) {  
	    //Get the coordinates of where the click event happened            
		int mouseX = e.getX();             
		int mouseY = e.getY();
		//Get the row and column clicked             
		int rowSelected = mouseY / CELL_SIZE;             
		int colSelected = mouseX / CELL_SIZE;
	
		//If the game is in GameState.Playing check for clicks in cells to set its content based on the currentPlayer
		if (currentState == GameState.Playing) {                
			if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0 && colSelected < COLS && board.cells[rowSelected][colSelected].content == Player.Empty) {
				//Set cell content  
				board.cells[rowSelected][colSelected].content = currentPlayer; 
				//Update currentState                  
				updateGame(currentPlayer, rowSelected, colSelected);
				
				//Switch player
				if (currentPlayer == Player.Cross) {
					currentPlayer =  Player.Nought;
				}
				
				else {
					currentPlayer = Player.Cross;
				}
			}             
		}
		
		//If the gameState is another state (indicating a winner/draw) then the game will allow for a click before reseting the game
		else {
			
			//If gameEnded has been set to true, a click will reset the game
			if (gameEnded) {
				initGame();
				gameEnded = false;
				gameEndedCheck = false;
			}
			
			//A win-state determined by the updateGame() method will set gameEndCheck to true so that the next click will reset the game
			if (gameEndedCheck) {
				gameEnded = true;
			}
			           
		}   
		
		//After any click the game will repaint the graphics
		repaint();
	}
		
	@Override
	public void mousePressed(MouseEvent e) {
		//  Auto-generated, event not used
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		//  Auto-generated, event not used
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// Auto-generated,event not used
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// Auto-generated, event not used
	}
}
