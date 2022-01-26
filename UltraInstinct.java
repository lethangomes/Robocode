package test;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * UltraInstinct - a robot by (your name here)
 */
public class UltraInstinct extends TeamRobot
{
	/**
	 * run: UltraInstinct's default behavior
	 */
	
	double lastEnergy = 100;
	int moveDirection = 1;
	int consecutiveVzero = 0;
	public void run() {
		setAdjustGunForRobotTurn(true);
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.magenta,Color.green,Color.yellow);

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			
			turnGunLeft(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//targeting code from trackfire
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		//turn to face enemy
		turnGunRight(bearingFromGun);
		
		
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0) {
			scan();
		}
		if(lastEnergy != e.getEnergy())
		{
			setAhead((Math.random() + 1) * 100 * moveDirection);
			lastEnergy = e.getEnergy();
			moveDirection *= -1;
		}
		
		//checks if the robot hasn't been moving and shoots
		if(e.getVelocity() == 0)
		{
			consecutiveVzero++;
			if (consecutiveVzero > 20) {
				fire(3);
			}
		}
		else
		{
			consecutiveVzero = 0;
		}
		
		//turnGunToAngle(absoluteBearing);
		if(e.getDistance() < 100)
		{
			fire(3);
		}
		
		turnToAngle(absoluteBearing + 90);
	
	}
	
/*
	public void turnGunToAngle(double angle)
	{
		double heading = getGunHeading();
		if(Math.abs(angle - heading) < 180)
		{
			setTurnGunRight(angle - heading);
			
			System.out.println(angle-heading);
			
			
			//moveDirection = 1;
			
		
		}
		else
		{
			setTurnGunRight(Math.abs(heading - angle) - 360);
			System.out.println("+" + (Math.abs(heading - angle) - 360));
			//moveDirection = -1;
		}
		
	}
	*/
//test
	
	public void turnToAngle(double angle)
	{
			double heading = getHeading();
		if(Math.abs(angle - heading) < 180)
		{
			setTurnRight(angle - heading);
		}
		else
		{
			setTurnRight(Math.abs(heading - angle) - 360);
			
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onBulletHit(BulletHitEvent e) {
		lastEnergy = e.getEnergy();
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		moveDirection *= -1;
		setAhead(100 * moveDirection);
	}	
}
