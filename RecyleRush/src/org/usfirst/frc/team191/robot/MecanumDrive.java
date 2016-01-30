package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.Joystick;

public class MecanumDrive
{
	private XCatsSpeedContoller  motors[];
	public final int front_left = 0, rear_left = 1, front_right = 2, rear_right = 3;
	private double reductionFactor = Enums.SPEED_REDUCTION_FACTOR;
	
	public MecanumDrive (int channels[], boolean useCAN)
	{
		this.motors = new XCatsSpeedContoller[4];
		
		for (int i = 0; i < 4; i++)
		{
			this.motors[i] = new XCatsSpeedContoller(channels[i], useCAN, Enums.IS_FINAL_ROBOT,null,null);
			this.motors[i].setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT, "motor" + i);
		}
		
		motors[front_left].setInverted(true);
		motors[rear_left].setInverted(true);
	}
	
	public MecanumDrive (int channels[], boolean speedMode, int codesPerRev, double p, double i, double d)
	{
		this.motors = new XCatsSpeedContoller[4];
		
		for (int j = 0; j < 4; j++)
		{
			this.motors[j] = new XCatsSpeedContoller(channels[j], speedMode, Enums.IS_FINAL_ROBOT, codesPerRev, p, i, d,null,null);
			this.motors[j].setDashboardIO(true, true, "motor");
		}
		
		motors[front_left].setInverted(true);
		motors[rear_left].setInverted(true);
	}
	
	public void set (Joystick drive_js)
	{
		
//		set(drive_js.getRawAxis(0), drive_js.getRawAxis(1), drive_js.getRawAxis(4), drive_js.getRawAxis(5));
		set(reductionFactor*drive_js.getRawAxis(0), reductionFactor*drive_js.getRawAxis(1), reductionFactor*drive_js.getRawAxis(4), reductionFactor*drive_js.getRawAxis(5));
	}
	
	public void setReductionFactor (double reductionFactor)
	{
		this.reductionFactor = reductionFactor;
	}
	
	public double getReductionFactor ()
	{
		return reductionFactor;
	}
	
	public void set (Joystick left_js, Joystick right_js)
	{
		set(left_js.getX(), left_js.getY(), right_js.getX(), right_js.getY());
	}
	
	public void set (double left_x, double left_y, double right_x, double right_y)
	{
		motors[front_left].set(left_y - left_x);
		motors[rear_left].set(left_y + left_x);
		motors[front_right].set(right_y + right_x);
		motors[rear_right].set(right_y - right_x);
	}
	
	public void set (int motor, double speed)
	{
		motors[motor].set(speed);
	}
	
	public double get (int motor)
	{
		return motors[motor].getSetPoint();
	}
	
	public void updateStatus ()
	{
		for (int i = 0; i < 4; i++)
			motors[i].updateStatus();
	}
}
