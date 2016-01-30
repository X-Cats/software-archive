package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotControls {
	private Joystick left_js, right_js, drive_js, operator_js;
	private double lastOperatorJSValueA1, lastOperatorJSValueA5 = 0;
	private XCatsDrive drive;
	private CameraServer camera;
	private boolean reductionToggle = false, slowMode = true;

	public RobotControls ()
	{
		if (!Enums.USE_CAN)
			
			drive = new XCatsDrive (Enums.DRIVE_MOTOR_NUMBERS, Enums.USE_CAN);
		else
			drive = new XCatsDrive (Enums.CAN_DRIVE_MOTOR_NUMBERS, true, 128, .5, 0, 0);
		//drive motors are currently up to 5000 rpm, 128 codes per rev.


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
		}
	}

	public void updateStatus ()
	{
		drive.updateStatus();
	}

	
	public XCatsDrive getDrive()
	{
		return drive;
	}
}
