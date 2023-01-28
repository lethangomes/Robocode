package lucas;
import robocode.*;
import java.util.Hashtable;
import java.lang.Math;
import java.util.ArrayList;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * UltrainstinctV2 - a robot by (your name here)
 */
public class UltrainstinctV2 extends AdvancedRobot
{
	Hashtable<String, Double> enemyEnergies = new Hashtable<String, Double>();
	Hashtable<String, double[]>  enemyPositions = new Hashtable<String, double[]>();
	ArrayList<String> enemies = new ArrayList<String>();
	ArrayList<projectile> projectiles = new ArrayList<projectile>();
	boolean enemiesAlive = true;

	public void run() {
		//setAdjustRadarForRobotTurn(true);

		//Gun rotates independently of body
		setAdjustGunForRobotTurn(true);
		
		// Robot main loop
		while(true) {
			for(int i = 0; i < enemies.size(); i++)
			{
				if(enemyEnergies.get(enemies.get(i)) != 0)
				{
					enemiesAlive = true;
					break;
				}
				if(i == enemies.size() - 1)
				{
					enemiesAlive = false;
				}
			}

			//turn radar a lot to constantly scan for enemies
			setTurnRadarRight(3600);

			if(enemiesAlive)
			{
				turnGunRight(90);
				
				//checks if robot is in path of any projectiles
				for(int i = 0; i < projectiles.size(); i++)
				{
					if(projectiles.get(i).willHit(getX(), getY()))
					{
						//tries to find and go to safe coordinates
						double[] safeCoords = findSafeCoords();
						System.out.println( safeCoords[0]+ ", " + safeCoords[1]);
						goToPoint(safeCoords);

						break;
					}
				}

				
			}
			else
			{
				execute();
			}
			
		}
	}

	//a function that makes the robot go to a given point
	public void goToPoint(double[] coords)
	{
		//find x and y distance to given coords
		double xDelta = coords[0] - getX();
		double yDelta = coords[1] - getY();
		
		//finds the heading robot needs to face to get to point
		double targetAngle;
		if(xDelta > 0 && yDelta < 0)
		{
			targetAngle = Math.abs(Math.toDegrees(Math.atan2(yDelta, xDelta))) + 90;
		}
		else
		{
			targetAngle = -Math.toDegrees(Math.atan2(yDelta, xDelta)) + 90;
		}

		//turns to correct heading and travels to point
		turnRight(normalRelativeAngleDegrees(targetAngle - getHeading()));
		ahead(Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2)));
	}

	//a function that finds coordinates not in the path of an enemy projectile
	public double[] findSafeCoords()
	{
		int distanceIncrement = 20;
		int degreeIncrement = 10;
		int borderSize = 100;
		int maxRadius = 400;
		int enemySafeDistance = 200;
		double botX = getX();
		double botY = getY();

		//checks for safe points along increasingly large circles around the robot
		for(int i = distanceIncrement; i < maxRadius; i += distanceIncrement)
		{
			for(double j = getHeading() - 90; j < getHeading() + 270; j += degreeIncrement)
			{
				boolean safe = true;

				//find x and y for current radius and angle being checked
				double pX = botX + (i * Math.sin(Math.toRadians(j)));
				double pY = botY + (i * Math.cos(Math.toRadians(j)));

				boolean farFromEnemy = true;

				//checks if the current point being checked is too close to an enemy
				for(int l = 0; l < enemies.size(); l++)
				{
					if(Math.sqrt(Math.pow(enemyPositions.get(enemies.get(l))[0] - pX, 2) + Math.pow(enemyPositions.get(enemies.get(l))[1] - pY, 2)) < enemySafeDistance)
					{
						farFromEnemy = false;
					}
				}

				//only considers the point if its not close to an enemy and far from the walls
				if(farFromEnemy && pX > borderSize && pY > borderSize && pX < getBattleFieldWidth() - borderSize && pY < getBattleFieldHeight() - borderSize)
				{
					//checks if point is out of the path of every projectile
					for(int k = 0; k < projectiles.size(); k++)
					{
						if(projectiles.get(k).willHit(pX, pY))
						{
							safe = false;
							break;
						}
						
					}
					if(safe)
					{
						//returns safe coordinates if out of all projectile paths
						return new double[] {pX, pY};
					}
				}
			}
		}

		//if no safe point is found returns current position
		System.out.println("No safe point found");
		return new double[]{getX(), getY()};
	}


	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		
		String name = e.getName();
		double energy = e.getEnergy();
		double distance = e.getDistance();

		//calculate position of enemy robot
		double angle = getHeadingRadians() + e.getBearingRadians();
		double enemyX = getX() + Math.sin(angle) * distance;
		double enemyY = getY() + Math.cos(angle) * distance;
		double[] enemyPosition = new double[] {enemyX, enemyY};

		//updates various lists tracking enemy names, positions, and energies
		if(enemyEnergies.containsKey(name))
		{
			if(enemyEnergies.get(name) > energy)
			{
				projectiles.add(new projectile(enemyEnergies.get(name) - energy, enemyX, enemyY, getTime()));
			}
			enemyEnergies.replace(name, energy);
			enemyPositions.replace(name, enemyPosition);
		}
		else
		{
			enemies.add(name);
			enemyEnergies.put(name, energy);
			enemyPositions.put(name, enemyPosition);
		}

		if(distance <= 200 || !enemiesAlive)
		{
			turnGunRight(e.getBearing() + (getHeading() - getGunHeading()));
			fire(3);
		}
	}

	public void onHitByBullet(HitByBulletEvent e) {
		//updates enemy energy upon being hit
		enemyEnergies.replace(e.getName(), enemyEnergies.get(e.getName()) + (e.getPower() * 3));
	}
	

	//removes a projectile from the projectiles list
	public void removeProjectile(projectile p)
	{
		projectiles.remove(p);
	}

	//projectile class
	public class projectile
	{
		//position it was fired from
		double x;
		double y;

		//tracks projectiles as a linear path, mx+b
		double slope;
		double b;

		//if the projectile is traveling vertically the slope will be undetermined
		boolean vertical = false;

		double speed;
		long startTime;

		//which way it is travelling along the x-axis
		int direction;

		public projectile(Double cost, Double newX, double newY, long time)
		{
			startTime = time;
			x = newX;
			y = newY;

			//check if projectile is traveling vertically
			if(x != getX())
			{
				//calculate slope of projectile
				slope = (y-getY())/(x - getX());
				b = slope * (x * -1) + y;

				//find direction
				if(x > getX())
				{
					direction = -1;
				}
				else
				{
					direction = 1;
				}
			}
			else
			{
				vertical = true;

				//if projectile travels vertically direction represents which way it is traveling along y-axis instead of x-axis
				if(y>getY())
				{
					direction = -1;
				}
				else
				{
					direction = 1;
				}
			}
			
			//calculate speed of projectile
			speed = 20 - (3 * cost);
		}

		//a function that returns whether or not projectile will hit at a given point
		public boolean willHit(double targetX, double targetY)
		{
			//distance from projectile path where robot is considered safe
			double safeDistance = 100;

			//checks if projectile travels vertically
			if(vertical)
			{
				if(Math.abs(targetX - x) < safeDistance)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			
			//creates line perpendicular to projecile path that passes through target coordinates
			double pSlope = -1/slope;
			double pb = pSlope * (-targetX) + targetY;
			
			//finds where the projectile path and projected, perpendicular line intersect
			double intersectX = (pb - b)/(slope - pSlope);
			double intersectY = slope * intersectX + b;

			//finds how far the projectile is from intersect
			double[] pos =  getCurrentPosition();
			double pDist = Math.sqrt(Math.pow(pos[0] - intersectX , 2) +  Math.pow(pos[1] - intersectY, 2));

			//checks if the projectile has already moved past target point
			if(intersectX - pos[0] * direction < 0 && pDist > 100)
			{
				//only delete projectile if the given position is the same as current position
				if(targetX == getX() && targetY == getY())
				{
					removeProjectile(this);
				}
				return false;
			}

			//finds how far given point is from intersect
			double dist = Math.sqrt(Math.pow(targetX - intersectX , 2) +  Math.pow(targetY - intersectY, 2));

			//if given point is too close to intersect return true
			if(dist < safeDistance)
			{
				return true;
			}
			return false;

		}

		//gets the current position of projectile
		public double[] getCurrentPosition()
		{
			//calculates how far its traveled
			double distanceTravelled = (speed *(getTime() - startTime));

			//checks if projectile is traveling vertically or horizontally
			if(vertical)
			{
				return new double[] {x, y + (distanceTravelled * direction)};
			}
			if(slope == 0)
			{
				return new double[] {x + (distanceTravelled * direction), y};
			}

			//calculates projectiles position
			double newX = x + (distanceTravelled/Math.sqrt(1 + (slope * slope))) * direction;
			double newY = (slope * newX) + b;
			return new double[] {newX, newY};
		}
	}
}
