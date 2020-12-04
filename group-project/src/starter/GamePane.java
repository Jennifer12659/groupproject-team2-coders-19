package starter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class GamePane extends GraphicsPane implements ActionListener, KeyListener {
	private MainApplication program; // you will use program to get access to
										// all of the GraphicsProgram calls
	
	private ArrayList<ArrayList<Alien>> aliens;
	private ArrayList<Laser> aLasers = new ArrayList<Laser>();
	private ArrayList<Laser> sLasers = new ArrayList<Laser>();
	private Alien enemy;
	private int xCoordinate = 0;
	private int yCoordinate = 50;
	private int amount;
	private int xVelocity = 5;
	private int yVelocity = 0;
	private int LaserCounter = 0;
	private int AlienCounter = 0;
	private int currScore = 0, currLives = 3;
	private GLabel currentLives = new GLabel ("Lives: " + currLives);
	Random r = new Random();
	private Timer someTimer;
 
	private Spaceship ship;
	Rectangle bullet;
	double dx = 0, x = 0, y = 0, velx = 0, vely = 0;
	int playerX, playerY, bx, by;
	boolean readyToFire, shot = false;
	int xPos, yPos;
	private Graphics shoot;

	public static final int HITBOX_WIDTH = 2;
	

	
	public GamePane(MainApplication app) {
		this.program = app;
		drawAliens();
		drawSpaceship(shoot);
		someTimer = new Timer(program.TIMER_SPEED, this);
		someTimer.start();
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
	}
	
	private void setFocusTraversalKeysEnabled(boolean b) {} // TODO Auto-generated method stub
	private void addKeyListener(GamePane gamePane) {} // TODO Auto-generated method stub
	
	private boolean outOfBounds() {
		for (int i = 0; i < program.ROW_ALIENS; i++) { 
			for (int j = 0; j < program.COLUMN_ALIENS; j++) { 
				if (aliens.get(i).get(j).getX() < 0 || aliens.get(i).get(j).getX() + aliens.get(i).get(j).getImage().getWidth() > program.WINDOW_WIDTH) {
					return true; 
				}
			}
		  }
		return false;
	}
	
	private boolean bottomScreen() {
		for (int i = 0; i < program.ROW_ALIENS; i++) { 
			for (int j = 0; j < program.COLUMN_ALIENS; j++) { 
				if (aliens.get(i).get(j).getY() + aliens.get(i).get(j).getImage().getHeight() == program.WINDOW_HEIGHT) { 
					return true; 
					} 
				}
		  } return false;
	}

 
		  /*
			 * private boolean alienHitShip() { for (int i = 0; i < program.ROW_ALIENS; i++)
			 * { for (int j = 0; j < program.COLUMN_ALIENS; j++) { //if
			 * (aliens.get(i).get(j).getX() == ship.getShipImg().getX()) { if
			 * (getElementAt(aliens.get(i).get(j).getX(), aliens.get(i).get(j).getY()) ==
			 * enemy) { System.out.println("Hit the spaceship"); return true; } } } return
			 * false; }
			 */
	 

	
	
	private boolean alienHitShip() { 
		for (int i = 0; i < program.ROW_ALIENS; i++) { 
			for (int j = 0; j < program.COLUMN_ALIENS; j++) { 
				if ( !aliens.get(i).get(j).isDead() && aliens.get(i).get(j).getY() + aliens.get(i).get(j).getImage().getHeight() == ship.getShipImg().getY()) {  
				  return true; } 
			  } 
		  } return false; 
	}
	 
	

	public void actionPerformed(ActionEvent e) {
		x += velx;
		y += vely;
		LaserCounter++;
		AlienCounter++;
		int rowRand = r.nextInt(program.ROW_ALIENS);
		int colRand = r.nextInt(program.COLUMN_ALIENS);
		
		if (outOfBounds()) {
			xVelocity *=-1;
			for (int i = 0; i < program.ROW_ALIENS; i++) {
				for (int j = 0; j < program.COLUMN_ALIENS; j++) {
					aliens.get(i).get(j).move(0, 1); //moves all the aliens down 1
				}
			}
		}
		
		if (AlienCounter % program.ALIEN_MODULUS == 0) { //constant variable
			for (int i = 0; i < program.ROW_ALIENS; i++) {
				for (int j = 0; j < program.COLUMN_ALIENS; j++) {
					aliens.get(i).get(j).move(xVelocity, yVelocity); //moves all the aliens together
				}
			}
		}
		
		if (LaserCounter % program.LASER_MODULUS == 0) {
			Laser tempLaser = aliens.get(rowRand).get(colRand).addLaser();
			tempLaser.getImage().sendToBack();
			aLasers.add(tempLaser);
			program.add(tempLaser.getImage());
			program.playAlienLaser();
		}
		
		for (Laser temp: aLasers) {
			temp.tickDown();
		}
		
		for (Laser temp: sLasers) {
			temp.tickUp();
		}

		//if (amount == 0) { //condition for winning is when the player has killed all aliens
			//program.switchToWin();
		//}
		
		
		if (alienHitShip()) { //checks to see if aliens hit the spaceship
			someTimer.stop(); 
			program.removeAll();
			program.switchToLose(); 
		}
		
		
		if (bottomScreen()) { //this condition checks to see if the aliens hit the bottom of the screen
			//if(currLives == -1) {
				someTimer.stop(); 	
				program.removeAll();
				program.switchToLose();
			//}
		}
		checkAlienHitBox();
	}
	
	private void checkAlienHitBox() {
		// TODO Auto-generated method stub
		for (int i = 0; i < program.ROW_ALIENS; i++) {
			for (int j = 0; j < program.COLUMN_ALIENS; j++) {
			ArrayList<Laser>temp=new ArrayList<Laser>();
			for(Laser shipLaser:sLasers) {

				if(shipLaser.getImage().getBounds().intersects(aliens.get(i).get(j).getImage().getBounds()) && !aliens.get(i).get(j).isDead()) {
					program.remove(aliens.get(i).get(j).getImage());
					aliens.get(i).get(j).setDead();
					temp.add(shipLaser);
					program.remove(shipLaser.getImage());
				}
			}
			 
			sLasers.removeAll(temp);
		 }
		 }
	}

	private void drawAliens() {
		aliens = new ArrayList<>();
		for (int i = 0; i < program.ROW_ALIENS; i++) {
			aliens.add(new ArrayList<Alien>());
		}
		for (int i = 0; i < program.ROW_ALIENS; i++) {
			for (int j = 0; j < program.COLUMN_ALIENS; j++) {
				enemy = new Alien(xCoordinate, yCoordinate);
				aliens.get(i).add(enemy);
				xCoordinate+=50;
				amount++;
			}
			xCoordinate=0;
			yCoordinate+=50;
		}
	}

	@Override
	public void showContents() {
		for (int i = 0; i < program.ROW_ALIENS; i++ ) {
			for (int j = 0; j < program.COLUMN_ALIENS; j++) {
				program.add(aliens.get(i).get(j).getImage());
			}
		}
		program.add(ship.getShipImg());
		currentLives.setFont("Lato-30");
		currentLives.setColor(Color.WHITE);
		program.add(currentLives, 20, 40);
	}

	@Override
	public void hideContents() {
		for(Laser temp : aLasers) {
			if (temp.getY() == ship.getxPos()) {
				System.out.println("hit alien");
				int tempX = ship.getxPos(), tempY = ship.getyPos();
				program.remove(ship.getShipImg());
				if(currLives != -1) {
					currLives -= 1;
					program.remove(currentLives);
					currentLives = new GLabel ("Lives: "+currLives);
				}
				else {
					return;
				}
				program.add(ship.getShipImg(), tempX, tempY);
				program.add(currentLives, 150, 550);
			}
		}
		
		
		
				for (int i = 0; i < program.ROW_ALIENS; i++)
			 { for (int j = 0; j < program.COLUMN_ALIENS; j++) {  
				
			  if(aliens.get(xPos).get(i)==aliens.get(xPos).get(i) &&
			     aliens.get(yPos).get(j)==aliens.get(yPos).get(j)) {  
				  
			 
			  System.out.println("BOOOM");
			  currScore += 10;
			 
			  }
			 }
			 }
			
				//program.remove(aliens.get(xCoordinate).get(yCoordinate).getImage());
			  
			
		}
		
	public int finalScore() {
		if(amount == 0 || currLives == 0) {
			return currScore;
		}
		return 0;
	}
	
	public void drawSpaceship(Graphics g) {
		ship = new Spaceship(program.SHIP_X, program.SHIP_Y);
		setFocusTraversalKeysEnabled(false);
		addKeyListener(this);
		
		if(shot) {
			g.setColor(Color.RED);
			g.fillRect(bullet.x,  bullet.y,  bullet.width, bullet.height);
		}
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_LEFT) {
			ship.moveLeft();
		}
		if (code == KeyEvent.VK_RIGHT) {
			ship.moveRight();
		}
		if (code == KeyEvent.VK_SPACE) {
			Laser tempLaser = ship.addLaser();
			tempLaser.getImage().sendToBack();
			sLasers.add(tempLaser);
			program.add(tempLaser.getImage());
			program.playShipLaser();
			/*
			 * if(bullet == null) readyToFire = true; if (readyToFire) { by = playerY; bx =
			 * playerX; bullet = new Rectangle(bx, by, 3, 10); shot = true; }
			 */
		}
	}

	
		
		
	public void shoot() {
		if(shot)
			bullet.y--;
	}
		
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT) {
			ship.moveLeft();
		}
		if (key == KeyEvent.VK_RIGHT) {
			ship.moveRight();
		}
		if (key == KeyEvent.VK_SPACE) {
			/*
			 * readyToFire = false; if(bullet.y <= -5) { bullet = new Rectangle(0, 0, 0, 0);
			 * shot = false; readyToFire = true; }
			 */
		}
	}
	
	public boolean isFocusTraversable() {
		return true;
	}
	
}
