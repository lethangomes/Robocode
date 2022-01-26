package test;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Test - a robot by (your name here)
 */
public class Test extends TeamRobot
{
	/*
	 * run: Test's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.magenta,Color.green,Color.yellow); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			//setTurnGunRight(90);
			setTurnRight(90);
			ahead(100);
			//turnRight(90);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		if (isTeammate(e.getName())){
			System.out.println("Teammate");
		}
		else {
			//doGun();
			fire(1);
		}	
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
		turnLeft(90);
		// ahead(50);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(400);
		turnLeft(45);
	}	
	/* long fireTime = 0;
	void doGun() {
	    if (fireTime == getTime() && getGunTurnRemaining() == 0) {
	        setFire(2);
	    }
	 
	    // ... aiming code ...
	    // Don't need to check whether gun turn will complete in single turn because
	    // we check that gun is finished turning before calling setFire(...).
	    // This is simpler since the precise angle your gun can move in one tick
	    // depends on where your robot is turning.
	    fireTime = getTime() + 1;
}*/
}

