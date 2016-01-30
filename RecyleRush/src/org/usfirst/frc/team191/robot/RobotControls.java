package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotControls
{
	private Joystick left_js, right_js, drive_js, operator_js;
	private double lastOperatorJSValueA1, lastOperatorJSValueA5 = 0;
	private MecanumDrive drive;
	private Elevator elevator;
	private CameraServer camera;
	private Arm arm;
	private boolean reductionToggle = false, slowMode = true;
	
	public RobotControls ()
	{
		if (!Enums.USE_CAN)
			drive = new MecanumDrive (Enums.DRIVE_MOTOR_NUMBERS, Enums.USE_CAN);
		else if (!Enums.USE_PID)
			drive = new MecanumDrive(Enums.CAN_DRIVE_MOTOR_NUMBERS, Enums.USE_CAN);
		else
			drive = new MecanumDrive (Enums.CAN_DRIVE_MOTOR_NUMBERS, true, 128, .5, 0, 0);
		//drive motors are currently up to 5000 rpm, 128 codes per rev.
		
		elevator = new Elevator (Enums.ELEVATOR_LIFT_MOTOR, Enums.ELEVATOR_GRAB_MOTOR, (int) (128 * 2.57), Enums.IS_FINAL_ROBOT ? 1 : 32, 0, 0.0);
		arm = new Arm(Enums.ARM_MOTOR_2);//, Enums.ARM_MOTOR_2);
	
		if (Enums.TWO_JOYSTICKS)
		{
			left_js = new Joystick(Enums.LEFT_DRIVE_JS);
			right_js = new Joystick(Enums.RIGHT_DRIVE_JS);
		}
		else
			drive_js = new Joystick(Enums.DRIVE_JS);
		
		operator_js = new Joystick(Enums.OPERATOR_JS);
		
		if (Enums.DASHBOARD_INPUT)
			SmartDashboard.putBoolean("Use Joysticks", false);
		
		try
		{
			camera = CameraServer.getInstance();
			camera.setQuality(25);
			camera.startAutomaticCapture("cam0");
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void drive ()
	{
		if (!Enums.DASHBOARD_INPUT || SmartDashboard.getBoolean("Use Joysticks"))
		{
			if (Enums.TWO_JOYSTICKS)
				drive.set(left_js, right_js);
			else
			{
				drive.set(drive_js);
				
				if (drive_js.getRawButton(6) && !reductionToggle)
					slowMode = !slowMode;
				
				reductionToggle = drive_js.getRawButton(6);
				
				drive.setReductionFactor(slowMode ? (drive_js.getRawAxis(3) + drive_js.getRawAxis(2)) / 2  + .5:
										 1 - (drive_js.getRawAxis(3) + drive_js.getRawAxis(2)) / 2);
			}
		}
	}
	
	public void operate ()
	{
		if (!Enums.DASHBOARD_INPUT || SmartDashboard.getBoolean("Use Joysticks"));
		{
			if (operator_js.getRawButton(3))
				elevator.setGrabMotor(1);
			else if (operator_js.getRawButton(2))
				elevator.setGrabMotor(-1);
			else if (operator_js.getRawButton(1))
				elevator.setGrabMotor(0);
			
//			if (operator_js.getRawAxis(1) > .1 && lastOperatorJSValueA1 < .1)
//				elevator.incrementLevel(1);
//			else if (operator_js.getRawAxis(1) < -.1 && lastOperatorJSValueA1 > -.1)
//				elevator.incrementLevel(-1);
			
			if (Enums.ELEVATOR_PID)
			{
				elevator.setLift(operator_js.getRawAxis(1));
				
				if (Math.abs(operator_js.getRawAxis(2) - operator_js.getRawAxis(3)) > .1)
					elevator.setLiftSpeed(operator_js.getRawAxis(2) - operator_js.getRawAxis(3));
				else
					elevator.setLiftSpeed(0);
			}
			else
				elevator.setLift(operator_js.getRawAxis(1));
			
//			elevator.setLiftMotor(operator_js.getRawAxis(1));
			
//			if (Math.abs(operator_js.getRawAxis(5) - lastOperatorJSValueA5) > .05)
//			{
//				elevator.setGrabMotorSpeed(operator_js.getRawAxis(5));
//				lastOperatorJSValueA5 = operator_js.getRawAxis(5);
//			}
			
			// the up direction has the axis < 0, the down direction has the axis > 0
			// so we are reversing the joystick signal so we can pass it in correctly to the arm 
			// positive arm speed moves the arm up towards the center, negative armspeed moves the arm down 
			arm.setArmMotor(operator_js.getRawAxis(5)); 
			
//			lastOperatorJSValueA1 = operator_js.getRawAxis(1);
		}
	}
	
	public void updateStatus ()
	{
		drive.updateStatus();
		elevator.updateStatus();
	}
	
	public Elevator getElevator()
	{
		return elevator;
	}
	
	public MecanumDrive getDrive()
	{
		return drive;
	}
	public Arm getArm()
	{
		return arm;		
	}
}