package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * Buttons and inputs:
 * 
 * Driver's joysticks
 *		button		Action
 *		6			Shift between low and high speed. The compressor is used to do this, but the DO controls the signal to the solenoid that shifts the gears
 * 
 * 
 * Operator's joysticks
 *		button		Action
 *		1			reset the NAVX status 
 * 
 * 
 * 
 * 
 */


public class RobotControls {
	private Joystick _leftJS, _rightJS, _driveJS, _operatorJS;
	private XCatsDrive _drive;
	private CameraServer _camera;
	private boolean _reductionToggle = false, slowMode = true;
	private XCatsJSButton _speedToggleButton;
	private XCatsJSButton _highSpeedButton;
	private boolean _highSpeed = false;
	private DigitalOutput _doShifter;
	private DigitalOutput _do;
	private Solenoid _sol;
	private Solenoid _sol2;
	private DigitalInput _diUltraEcho;
	private Ultrasonic _ultra;
	private Navx _navx;
	private Compressor _compressor;
	

	public RobotControls ()
	{

		//simple XCatsDrive, no PID etc
		_drive = new XCatsDrive (Enums.USE_CAN,true);
		//_drive.setInverted();

		if (Enums.USE_COMPRESSOR){
			_compressor = new Compressor();
			
			_doShifter = new DigitalOutput(Enums.DO_SHIFTER);
			_do = new DigitalOutput(5);
			_sol = new Solenoid(4);
			_sol2 = new Solenoid(5);
			
		}
	
	    //the NAVX board is our gyro subsystem	
		if (Enums.USE_NAVX){
			_navx= new Navx(this);
			_navx.resetStatus();
			_navx.zeroYaw();			
		}
		

		
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
				
		try
		{
			_camera = CameraServer.getInstance();
			_camera.startAutomaticCapture(0);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	public Navx getNavx(){
		return _navx;
	}

	public void drive ()
	{


		if (Enums.TWO_JOYSTICKS)
			_drive.set(_leftJS, _rightJS);
		else
		{
//			System.out.println("in drive");
			_drive.set(_driveJS);

//			if (_driveJS.getRawButton(6) && !_reductionToggle){
//				slowMode = !slowMode;
//				_doShifter.set(slowMode);
//				_do.set(slowMode);
//				_sol.set(slowMode);
//				_sol2.set(slowMode);
//			}
//			
//			SmartDashboard.putBoolean("Shifter", slowMode);
//
//			_reductionToggle = _driveJS.getRawButton(6);
//
////			slowMode = _speedToggleButton.isPressed();
//			
//			if (Enums.USE_SOFTWARE_SPEED_REDUCTION){
//				_drive.setReductionFactor(slowMode ? 1.0 : Enums.SPEED_REDUCTION_FACTOR )	;				
//			}
		}
		
		if (_navx != null){
			if(_driveJS.getRawButton(5)){
				_navx.navxMode = "rotate";
				_navx.navxRotateDistance = 90;
			}
			if(_driveJS.getRawButton(8)){
				_navx.navxMode = "";
			}			
		}

	}

	public void operate ()
	{
		if (_navx != null) {
			if (_operatorJS.getRawButton(1)){
				_navx.resetStatus();
			}
		}
	}


	public XCatsDrive getDrive()
	{
		return _drive;
	}
	
	public void updateStatus ()
	{
		if (_navx != null){
			_navx.updateStatus();
			
		}		
	}
}