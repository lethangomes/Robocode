package test;
import robocode.*;
import robocode.Rules;
import java.awt.Color;
import java.util.Hashtable;
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
	//hashtable to hold all enemy energies
	Hashtable<String, Double> enemyEnergies = new Hashtable<String, Double>();
	
	//last scanned robot
	String lastScannedRobot = "";
	
	//move direction modifier
	int moveDirection = 1;
	
	//number of times a robot has been scanned not moving
	int consecutiveVzero = 0;
	

	public void run() {
		//makes gun turn independent of robot body
		setAdjustGunForRobotTurn(true);
	
		//team colors
		setColors(Color.magenta,Color.green,Color.yellow);

		// Robot main loop
		while(true) {
			//find an enemy robot
			turnGunLeft(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		
		//makes sure scanned robot is an enemy
		if (isTeammate(e.getName())){
			System.out.println("TeamMate");
       	}
		else
		{
			//gets robots name and checks if they are in the hashtable
			String name = e.getName();
			lastScannedRobot = name;
			checkName(name, e.getEnergy());
			
			//targeting code from trackfire
			double absoluteBearing = getHeading() + e.getBearing();
			double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
			
			//checks if the enemies energy has changed(meaning they shot a bullet)
			if(enemyEnergies.get(name) != e.getEnergy())
			{
				//moves out of the way 
				setAhead((Math.random() + 1) * 100 * moveDirection);
				enemyEnergies.replace(name, e.getEnergy());
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
			
			//if the enemy is close enough shoot
			if(e.getDistance() < 100)
			{
				fire(3);
			}
			
			//turn robot to be perpendicular to bearing to the enemy
			turnToAngle(absoluteBearing + 90);
			//System.out.println(enemyEnergies.toString());			

			//turn to face enemy
			turnGunRight(bearingFromGun);
			
			
			// Generates another scan event if we see a robot.
			// We only need to call this if the gun (and therefore radar)
			// are not turning.  Otherwise, scan is called automatically.
			if (bearingFromGun == 0) {
				scan();
			}
		
		}
	
	}
	
	//checks if the enemy's name is in the energy hashtable, and add them if they aren't.
	public void checkName(String name, double energy)
	{
		if(enemyEnergies.get(name) == null)
		{
			enemyEnergies.put(name, energy);
		}
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

	//turn to a specified heading
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
		//updates enemy energy after hitting them
		enemyEnergies.replace(e.getName(), e.getEnergy());
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		//Gets the power of the enemy bullet and updates their energy if the enemy is in the current hashtable
		if(enemyEnergies.get(e.getName()) != null)
		{
			double power = e.getBullet().getPower();
			double energyDiff = Rules.getBulletHitBonus(power);
			enemyEnergies.replace(e.getName(), enemyEnergies.get(e.getName()) + energyDiff);
		}
		if(lastScannedRobot != e.getName())
		{
			turnGunRight(e.getBearing());
		}
	}
	
	//plays when this robot wins
	public void onWin(WinEvent e)
	{
		//VICTORY DANCE
		stop();
		ahead(20);
		back(20);
		while(true)
		{
			turnRight(30);
			turnLeft(30);
		}
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// turns around
		moveDirection *= -1;
		setAhead(200 * moveDirection);
	}	
}
