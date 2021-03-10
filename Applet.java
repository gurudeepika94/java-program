/*
 * Name:Deepika Karunakaran
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

public class A3 extends Applet implements Runnable {
	
	private enum CollisionSoundType {
		WALL, 
		BALL
	}
	
	// Private Properties
	
	private Ball ball1, ball2, ball3;
	private AudioClip wallSound, ballSound, currentSound; 
	private Graphics graphics;
	private Image imageToDraw; 
	Thread animationThread;

	// flag to determine when to show/start animation and when to stop animation
	volatile boolean showAnimation;
	
	// Animation duration in milliseconds
	long animationDuration = 50;
	
	// Initializer
	
	public void init() {
	
		// Set Applet Dimension. This would be used by Applet Viewer.
		setSize(800, 800);
		
		// Get Applet Dimension.
		Dimension dimension = getSize();
		
		// Creates off screen drawable image for double buffering useful for remove flickering 
		imageToDraw = createImage(dimension.height, dimension.height);
		
		// Creates Graphics context to be used for off screen image drawing.
		graphics = imageToDraw.getGraphics();

		// Load Sound for Wall Collision and Ball Collision
		wallSound = getAudioClip( getDocumentBase(), "sound1.au" );
		ballSound = getAudioClip( getDocumentBase(), "sound2.au" );
		
		// Create three Balls with different color, origins and speed.
		ball1 = new Ball(Color.YELLOW, 100, 100, 5, 3);
		ball2 = new Ball(Color.CYAN, 250, 250, 3, 5);
		ball3 = new Ball(Color.MAGENTA, 550, 550, 4, 6);
	}
	
	// Applet Life Cycle Method - Start would be called as and when Applet is loaded.
	public void start() {
		
		// Thread would be in New State
		animationThread = new Thread(this);
		
		// Set initial flag to true as we want our animation to work.
		showAnimation = true;
		
		// Thread will move from New State to Runnable State
		animationThread.start();
	}
	
	// Applet Life Cycle Method - Run is implemented as part of Runnable interface.
	public void run() {
		
		while (showAnimation) {
			
			ball1.translate();
			ball2.translate();
			ball3.translate();
			
			repaint();
			
			try {
				Thread.sleep(animationDuration);
			}
			catch(InterruptedException e) {
				System.out.println(e);
			}
			
			if (ball1.collideOrIntersect(ball2)) {
				ball1.invereseSpeedInXYDirection();
				ball2.invereseSpeedInXYDirection();
				playSoundForCollisionType(CollisionSoundType.BALL);
			}
			
			if (ball1.collideOrIntersect(ball3)) {
				ball1.invereseSpeedInXYDirection();
				ball3.invereseSpeedInXYDirection();
				playSoundForCollisionType(CollisionSoundType.BALL);
			}
			
			if (ball2.collideOrIntersect(ball3)) {
				ball2.invereseSpeedInXYDirection();
				ball3.invereseSpeedInXYDirection();
				playSoundForCollisionType(CollisionSoundType.BALL);
			}
			
			playWallCollisionSoundIfCollisionWithBall(ball1);
			playWallCollisionSoundIfCollisionWithBall(ball2);
			playWallCollisionSoundIfCollisionWithBall(ball3);
		}
	}
	
	public void paint( Graphics g) {
		
		Dimension dimension = getSize();
				
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, dimension.width, dimension.height);
		
		paintBall(ball1, graphics);
		paintBall(ball2, graphics);
		paintBall(ball3, graphics);
		
		g.drawImage(imageToDraw, 0, 0, this);
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	private void paintBall(Ball ball, Graphics g) {
		
		g.setColor(ball.getColor());
		
		Point centerPoint = ball.getCenter();
		int diameter = ball.getRadius() * 2;
		
		g.fillOval(centerPoint.getX(), centerPoint.getY(), diameter, diameter);	
	}
	
	private void playWallCollisionSoundIfCollisionWithBall(Ball ball) {
	
		Rectangle bounds = getBounds();
		
		Point centerPoint = ball.getCenter();
		int diameter = ball.getRadius() * 2;
		
		boolean shouldBounceBallInXDirection = false;
		boolean shouldBounceBallInYDirection = false;
		
		if ((centerPoint.getX() + diameter) > bounds.width || centerPoint.getX() < 0) {
			shouldBounceBallInXDirection = true;
		}
			
		if ((centerPoint.getY() + diameter) > bounds.height || centerPoint.getY() < 0) {
			shouldBounceBallInYDirection = true;
		}
		
		boolean shouldPlayWallCollisionSound = false;
		
		if (shouldBounceBallInXDirection && shouldBounceBallInYDirection) {
			ball.invereseSpeedInXYDirection();
			shouldPlayWallCollisionSound = true;
		}
		else if (shouldBounceBallInXDirection) {
			ball.invereseSpeedInXDirection();
			shouldPlayWallCollisionSound = true;
		}
		else if (shouldBounceBallInYDirection) {
			ball.invereseSpeedInYDirection();
			shouldPlayWallCollisionSound = true;
		}
	
		if (shouldPlayWallCollisionSound) {
			playSoundForCollisionType(CollisionSoundType.WALL);
		}
	}
	
	// Applet Life Cycle Method - This method will be called when we close browser window having applet
		public void stop() {
			
			// Stop Animation when we close the applet
			showAnimation = false;
		}
		
	private void playSoundForCollisionType(CollisionSoundType type) {
		
		switch (type) {
		case WALL:
			wallSound.play();
		case BALL:
			ballSound.play();
		}	
	}
}

// Point represented in form of x, y similar to any point in graph system.

class Point {
	
	private int x, y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int distanceFromPoint(Point point) {
		int centerX1X2 = this.getX() - point.getX();
		int centerY1Y2 = this.getY() - point.getY();
		return (int)Math.sqrt((centerX1X2*centerX1X2) + (centerY1Y2*centerY1Y2));
	}
}

// Represents speed measured by providing x and y value which will be used as translation value.
class Speed {
	
	private int x, y;
	
	public Speed(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void invert() {
		invertX();
		invertY();
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void invertX() {
		this.x = -this.x;
	}
	
	public void invertY() {
		this.y = - this.y;
	}
}

// Ball class represents current center point, color, speed and radius
class Ball {
	
	private Color color;
	private Point center;
	private Speed speed;
	private int radius;
	
	public Ball(Color color, int centerX, int centerY, int speedX, int speedY) {
		this.color = color;
		this.center = new Point(centerX, centerY);
		this.speed = new Speed(speedX, speedY);
		this.radius = 30;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Point getCenter() {
		return this.center;
	}
	
	public int getRadius() {
		return this.radius;
	}
	
	public void invereseSpeedInXDirection() {
		this.speed.invertX();
	}
	
	public void invereseSpeedInYDirection() {
		this.speed.invertY();
	}
	
	public void invereseSpeedInXYDirection() {
		invereseSpeedInXDirection();
		invereseSpeedInYDirection();
	}
	
	public void translate() {
		int x = this.center.getX() + this.speed.getX();
		int y = this.center.getY() + this.speed.getY();
		this.center = new Point(x, y);
	}
	
	public boolean collideOrIntersect(Ball ball) {
		int distanceBetweenTwoBallCenterPoint = this.center.distanceFromPoint(ball.getCenter());
		int cutOffDistanceForCollision = this.getRadius() + ball.getRadius();
		return distanceBetweenTwoBallCenterPoint < cutOffDistanceForCollision;
	}
}