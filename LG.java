package test;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Test - a robot by (your name here)
 */
public class LG extends TeamRobot
{
	/**
	 * run: Test's default behavior
	 */
	int moveDirection = 1;
	int direction = 1;
	boolean turning = true;
	int consecutiveVzero = 0;
	
	double lastEnemyX;
	double lastEnemyY;
	double lastEnemyH;
	
	double arenaHeight, arenaWidth;
	double edgeRatio = 7;
	 

	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		//robot colors
		setColors(Color.magenta,Color.green,Color.yellow); // body,gun,radar

		//makes it so gun turns independently of body
		setAdjustGunForRobotTurn(true);
		
		//gets area dimensions
		arenaWidth = getBattleFieldWidth();
		arenaHeight= getBattleFieldHeight();
		
		// Robot main loop
		while(true) {
			
			setAhead(100);
			turnGunRight(360*direction);
			//checks where the enemy last was and then move away from there
	
			
		}
	}
	
	//fuction for how the robot moves
	public void schmovement()
	{
		//checks if robot is near walls and moves accordingly
		if(getX() < arenaWidth/edgeRatio)
			{
				//System.out.println("too far left");
				turnToAngle(90);
				moveDirection = 1;
				setAhead(100 * moveDirection);
				//waitFor(new MoveCompleteCondition(this));
			}
			else if(getX() > 9*arenaWidth/edgeRatio)
			{
				//System.out.println("too far right");
				turnToAngle(90);
				moveDirection = -1;
				setAhead(100 * moveDirection);
				//waitFor(new MoveCompleteCondition(this));
			}else if(getY() < arenaHeight/edgeRatio)
			{
				//System.out.println("too low");
				turnToAngle(0);
				moveDirection = 1;
				setAhead(100 * moveDirection);
				//waitFor(new MoveCompleteCondition(this));
			}
			else if(getY() > 9*arenaHeight/edgeRatio)
			{
				//System.out.println("too High");
				turnToAngle(0);
				moveDirection = -1;
				setAhead(100 * moveDirection);
				//waitFor(new MoveCompleteCondition(this));
			}
			else
			{
				//trys to move perpendicularly to the last robot scanned
				setAhead(100 * moveDirection);
				turnToAngle((lastEnemyH+90)-getHeading());
				
			}
			
	}
	
	//a function that turns the robot to the given angle
	public void turnToAngle(double angle)
	{
		double heading = getHeading();
		if(Math.abs(angle - heading) < Math.abs(360 - heading + angle))
		{
			setTurnRight(angle - heading);
			//moveDirection = 1;
			
			/*
			 * IMPORTANT, when the robot uses turn to angle function it may turn around
			 * this breaks the schmovement code when the robot is close to a wall. maybe fix
			 * with a variable that says whether the robot turned around or not.
			 */
		}
		else
		{
			setTurnRight(360 - heading + angle);
			//moveDirection = -1;
		}
	}


	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//calls movement method
		schmovement();
		
		//gets variables regarding robot and enemy robot
		double dist = e.getDistance();
		double robotHeading = e.getHeading();
		double absBearing = getHeading() + e.getBearing();
		double selfHeading = getGunHeading();
		double bulletV = 19;
		double botV = e.getVelocity();
		double travelT = dist/bulletV;
		
		//predicts where this robot will be and where the enemy robot will be
		double selfX = (getVelocity() * Math.sin(getHeading() * (Math.PI/180)));
		double selfY = (getVelocity() * Math.cos(getHeading() * (Math.PI/180)));
		double botX = Math.sin(absBearing * (Math.PI/180)) * dist - selfX;
		double botY = Math.cos(absBearing * (Math.PI/180)) * dist - selfY;
		double botX2 = botX + travelT * botV * Math.sin(robotHeading * (Math.PI/180));
		double botY2 = botY + travelT * botV * Math.cos(robotHeading * (Math.PI/180));
		double globalHeading;
		
		//makes sure it doesnt shoot at a teammate
		if (isTeammate(e.getName())){
			System.out.println("TeamMate");
       	}
		else if(botV != 0)
		{
			//Determines what angle to shoot at 
			consecutiveVzero = 0;
			if(botX2 > 0 && botY2 < 0)//2nd quadrant
			{
				globalHeading = 90 - (Math.atan(botY2/ botX2)*(180/Math.PI));
			}
			else if(botX2 < 0 && botY2 < 0)//3rd quadrant
			{
				globalHeading = 180 + (90- (Math.atan(botY2/ botX2) * (180/Math.PI)));
			}
			else if(botX2 < 0 && botY2 > 0)//4th quadrant
			{
				globalHeading = 270 - (Math.atan(botY2/ botX2)*(180/Math.PI));
			}
			else //1rst quadrant
			{
				globalHeading = (90 - (Math.atan(botY2/ botX2)*(180/Math.PI)));
			}
			
			System.out.println("------------------------------");
			System.out.println("botX " + (botX+getX()));
			System.out.println("botY " + (botY+getY()));
			System.out.println("Predicted botX " + (botX2));
			System.out.println("Predicted botY " + (botY2));
			System.out.println("Atan " + (Math.atan(botY2/ botX2)*(180/Math.PI)));
			System.out.println("global heading" + globalHeading);
			System.out.println("robot heading" + selfHeading);
			System.out.println("predicted self x " + (getVelocity() * Math.sin(getHeading() * (Math.PI/180))));
			System.out.println("predicted self y " + (getVelocity() * Math.cos(getHeading() *(Math.PI/180))));			
			

			//turns gun accordingly
			if((360-globalHeading+selfHeading) > (globalHeading-selfHeading))
			{
				turnGunRight(globalHeading-selfHeading);
			}
			else
			{
				turnGunRight((360-globalHeading+selfHeading));
			}
			
			if (globalHeading-selfHeading > 0)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
			//waitFor(new TurnCompleteCondition(this));
			fire(1);
			
			//sweeps the radar back to try and scan the robot again
			setTurnGunRight(90 * (-1*direction));
			//setTurnGunRight(90 * direction);
			turning = true;
		}
		else
		{
			
			//if the enemy robot isnt moving this robot just shoots at it. it shoots a bigge bullet if it doesnt move for a while
			turnGunRight((e.getBearing()+ getHeading()) - getGunHeading());
			consecutiveVzero++;
			if(consecutiveVzero > 3)
			{
				fire(3);
			}
			else
			{
				fire(1);
			}
		}
		
	
		
		lastEnemyX = botX2;
		lastEnemyY = botY2;
		lastEnemyH = robotHeading;

	}
		

	
	/**
	 * onHitByRobot What to do when you're hit by a robot
	 */
	public void onHitRobot(HitRobotEvent e) {
		//if this robot is rammed it turns 90 degrees to the enemy and runs away
		turnToAngle(getHeading() + e.getBearing() + 90);
		ahead(100);
		waitFor(new MoveCompleteCondition(this));
		
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
	

	public void onHitWall(HitWallEvent e) {
		// Reverses direction when hitting a wall
		moveDirection *= -1;
	}	
	

}
