package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotControls {
	private Joystick _leftJS, _rightJS, _driveJS, _operatorJS;
	private XCatsDrive _drive;
	private CameraServer _camera;
	private boolean _reductionToggle = false, slowMode = true;
	private XCatsJSButton _speedToggleButton;
	private XCatsJSButton _highSpeedButton;
	private boolean _highSpeed = false;
	private DigitalOutput _doUltraPing;
	private DigitalInput _diUltraEcho;
	private Ultrasonic _ultra;
	private Navx _navx;



	/**
	 * 
	 */
	/**
	 * 
	 */
	public RobotControls ()
	{
		_navx=new Navx();
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
		_highSpeedButton = new XCatsJSButton(_operatorJS,8);
		
		if (Enums.DASHBOARD_INPUT)
			SmartDashboard.putBoolean("Use Joysticks", false);
		//_acq = new Acquisition(_operatorJS);

		try
		{
			if (!Enums.IS_FINAL_ROBOT){
				_ultra = new Ultrasonic(1,2,Ultrasonic.Unit.kInches);
				_ultra.setAutomaticMode(false);
				_ultra.setEnabled(true);				
			}			
		}
		catch (Exception e){
			System.out.println(e);
			e.printStackTrace();
			
		}
		
		
		try
		{
			_camera = CameraServer.getInstance();
			//_camera.setQuality(25);
			_camera.startAutomaticCapture(0);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
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
		
//		

	
	}

	public void updateStatus ()
	{
		_navx.updateStatus();
		
		try {
			if (!Enums.IS_FINAL_ROBOT) {
				_ultra.ping();
				SmartDashboard.putNumber("Ultrasonic", _ultra.getRangeInches());							
			}
		}
		catch (Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
		

	}


	public XCatsDrive getDrive()
	{
		return _drive;
	}
	
}