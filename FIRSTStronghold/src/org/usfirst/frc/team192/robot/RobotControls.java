package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotControls {
	private Joystick _leftJS, _rightJS, _driveJS, _operatorJS;
	private XCatsDrive _drive;
	private CameraServer _camera;
	private boolean _reductionToggle = false, slowMode = true;
	private Acquisition _acq;
	private XCatsJSButton _speedToggleButton;


	public RobotControls ()
	{
		if (!Enums.USE_PID){
			// in our final robot, we have talon drives, in the prototype they are jaguars
			if (Enums.DRIVE_CONTROLLER_TYPE == "Talon")
				_drive = new XCatsDrive (Enums.USE_CAN,true);
			else
				_drive = new XCatsDrive (Enums.USE_CAN,false);			
		}		
		else {
			// in our final robot, we have talon drives, in the prototype they are jaguars
			if (Enums.DRIVE_CONTROLLER_TYPE == "Talon")			
				_drive = new XCatsDrive (Enums.CAN_DRIVE_MOTOR_NUMBERS, true, true, 128, .5, 0, 0);
			else			
				_drive = new XCatsDrive (Enums.CAN_DRIVE_MOTOR_NUMBERS, true, false, 128, .5, 0, 0);			
		}


		//drive motors are currently up to 5000 rpm, 128 codes per rev.


		if (Enums.TWO_JOYSTICKS)
		{
			_leftJS = new Joystick(Enums.LEFT_DRIVE_JS);
			_rightJS = new Joystick(Enums.RIGHT_DRIVE_JS);
		}
		else{
			_driveJS = new Joystick(Enums.DRIVE_JS);
			_speedToggleButton = new XCatsJSButton(_driveJS,6);
		}

				_operatorJS = new Joystick(Enums.OPERATOR_JS);
		
		if (Enums.DASHBOARD_INPUT)
			SmartDashboard.putBoolean("Use Joysticks", false);
		_acq = new Acquisition();

		//		try
		//		{
		//			_camera = CameraServer.getInstance();
		//			_camera.setQuality(25);
		//			_camera.startAutomaticCapture("cam0");
		//		}
		//		catch (Exception e)
		//		{
		//			System.out.println(e);
		//			e.printStackTrace();
		//		}
	}

	public void drive ()
	{


		if (Enums.TWO_JOYSTICKS)
			_drive.set(_leftJS, _rightJS);
		else
		{
			_drive.set(_driveJS);

			if (_driveJS.getRawButton(6) && !_reductionToggle)
				slowMode = !slowMode;

			_reductionToggle = _driveJS.getRawButton(6);

//			slowMode = _speedToggleButton.isPressed();
			
			_drive.setReductionFactor(slowMode ? 1.0 : Enums.SPEED_REDUCTION_FACTOR )	;
		}


	}

	public void operate ()
	{
		
		_acq.setShooterSpeed(_operatorJS.getRawAxis(5));
		
		
		//in and out is using left/right of the left joystick button
		if (_operatorJS.getRawAxis(2)> 0.2)
			_acq.release();
		else if (_operatorJS.getRawAxis(2)< -0.2)
			_acq.acquire();
		else	
			_acq.stop();
		
		//up and down is using the up/down of the left joystick button
		if (_operatorJS.getRawAxis(1)> 0.2)
			_acq.lower();
		else if (_operatorJS.getRawAxis(1)<-0.2)
			_acq.raise();
		else	
			_acq.stop();
		
		if (_operatorJS.getRawButton(1)){
			
		}
		
		//		if (!Enums.DASHBOARD_INPUT || SmartDashboard.getBoolean("Use Joysticks"));
		//		{
		//		}
	}

	public void updateStatus ()
	{
		//		_drive.updateStatus();
		_acq.updateStatus();
	}


	public XCatsDrive getDrive()
	{
		return _drive;
	}
	public Acquisition acquisition(){
		return _acq;
	}
}
