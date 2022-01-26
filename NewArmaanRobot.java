/*
 * Author: Armaan Hajarizadeh
 * Date: January 24, 2022
 * Description: Armaan Hajar's robot for the Robocode project
 * 
 * Main Goal of Robot:
 * 1. Find enemy
 * 2. Lock onto and track enemy
 * 3. Drive towards enemy
 * 4. Ram into enemy and fire if close enough
 * 5. If not close to enemy, shoot from a distance
 */

package test;
import robocode.*;
import java.awt.Color;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.TeamRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * ArmaanRobot - a robot by (Armaan Hajarizadeh)
 */

public class NewArmaanRobot extends TeamRobot {
	int count = 0; // Keeps track of how long we've been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Tracks the name of other robots on field
	
	public void run() {
		setColors(Color.magenta,Color.green,Color.yellow); // body,gun,radar

		// Robot main loop
		while(true) {
			// turn the Gun (looks for enemy)
			turnGunRight(gunTurnAmt);
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			if (count > 11) {
				trackName = null;
				}
			}		
		}
	}

	/**
	 * turnToAngle: Turns tank to an angle rather than a bearing
	 */
	public void turnToAngle(double angle) {
		double heading = getHeading();
		setTurnRight(angle - heading);
	}
	
	/**
	 * armaanMovement: Turns body to the gun heading to synchronize gun and tank movement
	 */
	public void armaanMovement() {
		double a = getGunHeading(); // finds gun heading

		System.out.println();
		System.out.println(a); // for debugging
		
		turnToAngle(a); // turns to gun heading
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())) { // stops the tank from shooting teammates
	           System.out.println("Teammate Detected - Firing Halted");;
	    } 
		else { // when a teammates is not detected
			
		armaanMovement();
		setAhead(100);
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0) {
				fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
			}
		} // otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnGunRight(bearingFromGun);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0) {
			scan();
			}
		}
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {	
		double arenaHeight = getBattleFieldHeight(); // get the battle field height
		double arenaWidth = getBattleFieldWidth(); // get the battle field width
		
		double x = getX(); // get x coordinate of tank
		double y = getY(); // get y coordinate of tank
		
		// debugging
		System.out.println();
		System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("Arena Width = " + arenaWidth);
		System.out.println("Arena Height = " + arenaHeight);
		
		// when the corners are hit
		if (x <= 50 && y <= 50) { // bottom left corner
			turnToAngle(45); // turn to 45 degrees
			System.out.println("Bottom Left Corner");
			System.out.println();
		}
		else if (x >= arenaWidth - 50 && y <= 50) { // bottom right corner
			turnToAngle(315); // turn to 315 degrees
			System.out.println("Bottom Right Corner");
			System.out.println();
		}
		else if (x >= arenaWidth - 50 && y >= arenaHeight - 50) { // top right corner
			turnToAngle(225); // turn to 225 degrees
			System.out.println("Top Right Corner");
			System.out.println();
		}
		else if (x <= 50 && y >= arenaHeight - 50) { // top left corner
			turnToAngle(135); // turn to 135 degrees
			System.out.println("Top Left Corner");
			System.out.println();
		}
		else {
			// if the walls are hit		
			if (y <= 50) { // bottom wall
				double temp = 315 + (Math.random() * 91); // pick random number between 315 and 406
				double temp1 = 0;
				if (temp >= 360) {
					temp1 = temp - 360; // subtract temp by 90 if over 360
				}
				System.out.println("Going in Angle " + temp1); // debugging
				turnToAngle(temp1); // turn to temp
				System.out.println("Bottom Wall");
				System.out.println();
			}
			else if (x >= arenaWidth - 50) { // right wall
				double temp = 225 + (Math.random() * 91); // pick random number between 225 and 316
				System.out.println("Going in Angle " + temp); // debugging
				turnToAngle(temp); // turn to temp
				System.out.println("Right Wall");
				System.out.println();
			}
			else if (y >= arenaHeight - 50) { // top wall
				double temp = 135 + (Math.random() * 91); // pick random number between 135 and 226
				System.out.println("Going in Angle " + temp); // debugging
				turnToAngle(temp); // turn to temp
				System.out.println("Top Wall");
				System.out.println();
			}
			else if (x <= 50) { // left wall
				double temp = 45 + (Math.random() * 91); // pick random number between 45 and 136
				System.out.println("Going in Angle " + temp); // debugging
				turnToAngle(temp); // turn to temp
				System.out.println("Left Wall");
				System.out.println();
			}
		}
		execute();
		waitFor(new TurnCompleteCondition(this)); // prioritize this
	}
	
	/**
	 * onDeath: When tank is killed
	 */
	public void onDeath (DeathEvent event) {
		System.out.println("I bless the rains down in Africa"); // gonna take some time to do the things we never haaaaaaaaaad
		execute();
	}
	/**
	 * onWin: When tank wins match
	 */
	public void onWin(WinEvent e) {
		turnGunRight(360); // victory dance
	}
}