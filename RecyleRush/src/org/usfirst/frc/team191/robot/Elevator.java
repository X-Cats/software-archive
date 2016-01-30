package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator
{
	private XCatsSpeedContoller liftMotor, grabMotor;
	private DigitalInput grabberLimit, liftTop, liftBottom;
	private int grabPosition = -1;
	private Timer grabTimer;
	private boolean grab;
	private double liftSetPoint = 0, liftSpeed = 0;
	private double lastLiftInputValue = 0;
	private boolean lastLiftBottom = false, lastLiftTop = false;
	
	public Elevator (int liftMotorChannel, int grabMotorChannel, int codesPerRev, double p, double i, double d)
	{
		
		grabberLimit = new DigitalInput(Enums.GRABBER_LIMIT_SWITCH);
		liftTop = new DigitalInput(Enums.LIFT_TOP_LIMIT_SWITCH);
		liftBottom = new DigitalInput(Enums.LIFT_BOTTOM_LIMIT_SWITCH);
		
		if (Enums.ELEVATOR_PID)
			liftMotor = new XCatsSpeedContoller(liftMotorChannel, false, Enums.IS_FINAL_ROBOT, codesPerRev, p, i, d, liftTop, liftBottom);
		else
			liftMotor = new XCatsSpeedContoller(liftMotorChannel, Enums.USE_CAN, Enums.IS_FINAL_ROBOT, liftBottom, liftTop); 

		if (Enums.IS_FINAL_ROBOT)
			liftMotor.reverseSensor(true);
		
		grabMotor = new XCatsSpeedContoller(grabMotorChannel, Enums.USE_CAN, Enums.IS_FINAL_ROBOT, grabberLimit, null);
		
//		zero = liftMotor.getPosition();

//		liftMotor.setPercentMode();
//		liftMotor.setInverted(true);
		
		grabMotor.setCutOffSetPointP(.1);
		
		grabTimer = new Timer();
		
		
//		liftMotor.setDashboardIO(true, true, "lift_motor");
		liftMotor.setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT, "lift_motor");
		grabMotor.setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT, "grab_motor");
//		grabMotor.setDashboardIO(true, true, "grab_motor");
		
		if (Enums.IS_FINAL_ROBOT)
		{
			SmartDashboard.putNumber("lift rotations per level", Enums.FINAL_ELEVATOR_ROTATIONS_PER_LEVEL);
			SmartDashboard.putNumber("lift max", Enums.FINAL_LIFT_MAX);
			SmartDashboard.putNumber("lift speed scale", 512);
		}
		else
		{
			SmartDashboard.putNumber("lift rotations per level", Enums.ELEVATOR_ROTATIONS_PER_LEVEL);
			SmartDashboard.putNumber("lift max", Enums.LIFT_MAX);
		}
	}
	
	public void setGrabMotorSpeed (double grabSpeed)
	{
		grabMotor.set(grabSpeed);
	}
	
	//positive is closed, negative open, zero in the middle.
	public void setGrabMotor (int position)
	{
		if (position > 0)
			grabMotor.set(1);
		else if (position < 0)
			grabMotor.set(-1);
		else
			if (this.grabPosition > 0)
				grabMotor.set(-.5);
			else if (this.grabPosition < 0)
				grabMotor.set(.5);
		
		this.grabPosition = position;
		
		grabTimer.start();
	}
	
//	public void setLevel (int level)
//	{
//		this.level = level;
//		liftMotor.set(level * Enums.ELEVATOR_ROTATIONS_PER_LEVEL);
//	}
//	
//	public int getLevel ()
//	{
//		return level;
//	}
	
	public void setLift (double value)
	{
		// the transmission was reversed for Nationals
		//value = -1.0 * value;
		
		double revsPerLevel = SmartDashboard.getNumber("lift rotations per level");

		
		if (liftMotor.isTalon())
		{
			if (liftMotor.getTalonControlMode() == CANTalon.TalonControlMode.PercentVbus)
				liftSetPoint = value;
			else
			{
				if (value > .1 && lastLiftInputValue < .1)
					liftSetPoint = liftSetPoint + (liftSetPoint % revsPerLevel == 0 ? revsPerLevel : -liftSetPoint % revsPerLevel);
				else if (value < -.1 && lastLiftInputValue > -.1)
					liftSetPoint = (liftSetPoint - revsPerLevel) - liftSetPoint % revsPerLevel;
				
				if (liftSetPoint < Enums.FINAL_LIFT_MAX)
					liftSetPoint = Enums.FINAL_LIFT_MAX;
				
				lastLiftInputValue = value;
			}
		}
		else
		{
			if (liftMotor.getJaguarControlMode() == CANJaguar.JaguarControlMode.PercentVbus)
				liftSetPoint = value;
			else
			{
				if (value > .1 && lastLiftInputValue < .1)
					liftSetPoint = liftSetPoint + (liftSetPoint % revsPerLevel == 0 ? revsPerLevel : -liftSetPoint % revsPerLevel);
				else if (value < -.1 && lastLiftInputValue > -.1)
					liftSetPoint = (liftSetPoint - revsPerLevel) - liftSetPoint % revsPerLevel;
				
				if (liftSetPoint < Enums.LIFT_MAX)
					liftSetPoint = Enums.LIFT_MAX;
				
				lastLiftInputValue = value;
			}
		}
		
		if ((liftBottom.get() && liftSetPoint > 0) || (liftTop.get() && liftSetPoint < 0))
			liftSetPoint = 0;
		
		liftMotor.set(liftSetPoint);
		
	}
	
	public void setLiftPosition (double setPoint)
	{
		liftSetPoint = setPoint * SmartDashboard.getNumber("lift rotations per level");
		
		if (liftBottom.get() && liftSetPoint > 0)
			liftSetPoint = 0;
	}

	public double getLiftSetpoint ()
	{
		return liftSetPoint;
	}
	
	//intended for use in position mode
	public void setLiftSpeed (double setPoint)
	{
		if (Enums.ELEVATOR_PID)
		{
			if (Enums.IS_FINAL_ROBOT)
				this.liftSpeed = setPoint * SmartDashboard.getNumber("lift speed scale");
			else
				this.liftSpeed = setPoint;
		}
		else
		{
			this.liftSetPoint = setPoint;
			liftMotor.set(setPoint);
		}
	}
	
//	public void setLiftLevel (int level)
//	{
//		this.level += level;
//		if (this.level > 0)
//			this.level = 0;
//		else if (this.level < -36 / SmartDashboard.getNumber("lift rotations per level"))
//			this.level = (int) (-36 / SmartDashboard.getNumber("lift rotations per level"));
//		liftMotor.set(this.level * SmartDashboard.getNumber("lift rotations per level"));
//	}
//	
//	public int getLiftLevel ()
//	{
//		return level;
//	}
//	
//	public void setLiftMotor (double setPoint)
//	{
//		if (!liftBottom.get() || setPoint < 0)
//			liftMotor.set(setPoint);
//	}
	
	public void updateStatus ()
	{	
//		if ((liftMotor.isTalon() && liftMotor.getTalonControlMode() == ControlMode.Position) ||
//				(!liftMotor.isTalon() && liftMotor.getJaguarControlMode() == CANJaguar.ControlMode.Position))
		if (Enums.ELEVATOR_PID)
		{
			if (liftBottom.get() && liftSetPoint > 0)
			{
				liftMotor.zeroEncoder();
				liftSetPoint = 0;
				liftMotor.set(liftSetPoint);
			}
			else
			{
				liftSetPoint += liftSpeed;
				liftMotor.set(liftSetPoint);
			}
		}
//		else
//			if (liftBottom.get())
//				liftMotor.setCutOffDirection(1);
//			else if (liftTop.get())
//				liftMotor.setCutOffDirection(-1);
		
		SmartDashboard.putBoolean("lift bottom", liftBottom.get());
		SmartDashboard.putBoolean("lift top", liftTop.get());
		SmartDashboard.putNumber("lift setpoint", liftMotor.getSetPoint());
		SmartDashboard.putBoolean("grabber limit", grabberLimit.get());
//		SmartDashboard.putNumber("lift speed", liftSpeed);
		if (Enums.IS_FINAL_ROBOT)
			SmartDashboard.putNumber("lift encoder", liftMotor.getEncPosition());
//		SmartDashboard.putBoolean("lift top", liftTop.get());
//		SmartDashboard.putBoolean("grabber limit", grabberLimit.get());
		
		if (grabMotor.getCurrent() > Enums.GRABBER_CURRENT_CUTOFF ||
				(grabMotor.getCurrent() > 20 && grabTimer.get() > .5))
		{
			grabMotor.setCutOffDirection((int) (grabMotor.getSetPoint() > 0 ?
					Math.ceil(grabMotor.getSetPoint()) : Math.floor(grabMotor.getSetPoint())));
			grabTimer.stop();
			grabTimer.reset();
		}
		
		if (grabPosition == 0 && grabberLimit.get())
			grabMotor.set(0); //stop();
		
		liftMotor.updateStatus();
		grabMotor.updateStatus();
	}
}
