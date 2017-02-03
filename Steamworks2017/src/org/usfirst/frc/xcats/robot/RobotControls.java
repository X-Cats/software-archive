package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
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
	private boolean _slowMode = true;
	private XCatsJSButton _speedToggleButton;
	private XCatsJSButton _highSpeedButton;
	private boolean _highSpeed = false;
	private DoubleSolenoid _dblSolShifter;
	private DigitalInput _diUltraEcho;
	private Ultrasonic _ultra;
	private Navx _navx;
	private Compressor _compressor;
	private Timer _shiftTimer;
	private boolean _shifting=false;
	

	public RobotControls ()
	{

		//
		_shiftTimer = new Timer();
		
		//simple XCatsDrive, no PID etc
		_drive = new XCatsDrive (Enums.USE_CAN,true);
		_drive.setDashboardIO(false, true);
	
		if (Enums.USE_COMPRESSOR){
			_compressor = new Compressor();
			
			_dblSolShifter = new DoubleSolenoid(Enums.DO_SHIFTER_LOW,Enums.DO_SHIFTER_HI);			
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
			_speedToggleButton = new XCatsJSButton(_rightJS,1);
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

		boolean reductionToggle = _slowMode;
		int directionLeft = 1; // this is used to tell if we are going forward or backwards. The shifting speed needs to be in the same direction!
		int directionRight = 1;

		if (!_shifting){
			if (Enums.TWO_JOYSTICKS){
				_drive.set(_leftJS, _rightJS);
			}
			else {
				_drive.set(_driveJS);
			}
		}

		//get the direction of the drive
		if (_drive.get(Enums.FRONT_LEFT)< 0)
			directionLeft = -1;
		
		if (_drive.get(Enums.FRONT_RIGHT)< 0)
			directionRight = -1;
		

		
		//only transition on the "downstroke" of the button, we dont care how long it is held
		reductionToggle  = _speedToggleButton.isPressed();
		if (reductionToggle != _slowMode){
			_slowMode = !_slowMode;
			_shiftTimer.reset();
			_shiftTimer.start();
			_shifting = true;
			if (_slowMode)
				_dblSolShifter.set(DoubleSolenoid.Value.kForward);
			 else 
				_dblSolShifter.set(DoubleSolenoid.Value.kReverse);										
		}

		//we need to drop the speed for a small time frame so that the gears can handle the gear shift
		//so this loop will detect the shifting command and at the end of it will resume speed
		if (_shifting ){
			if (_shiftTimer.get() >= Enums.SHIFTER_DELAY_TIME)
				_shifting = false;				
			else
				_drive.set( directionLeft * Enums.SHIFTER_DELAY_SPEED,  directionRight * Enums.SHIFTER_DELAY_SPEED);			
		}
		
		SmartDashboard.putNumber("LeftSpeed", _drive.get(Enums.FRONT_LEFT));
		SmartDashboard.putNumber("Direction", directionLeft);
		SmartDashboard.putBoolean("Shifter", _slowMode);		

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
		_drive.updateStatus();
		if (_navx != null){
			_navx.updateStatus();
			
		}		
	}
}