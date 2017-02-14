package org.usfirst.frc.xcats.robot;


import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	private boolean _slowMode = true;
	private boolean _liftMode = false;
	private XCatsJSButton _speedToggleButton;
	private XCatsJSButton _highSpeedButton;
	private XCatsJSButton _feederLifter;
	private boolean _highSpeed = false;
	private DoubleSolenoid _dblSolShifter;
	private DigitalInput _diUltraEcho;
	private Ultrasonic _ultra;
	private Navx _navx;
	private Compressor _compressor;
	private Timer _shiftTimer;
	private boolean _shifting=false;
	private PowerDistributionPanel _pdp;
	private Gear _gear;
	private Feeder _feeder;
	private Climber _climber;
	private AutoTarget _autoTarget;
	private UsbCamera _camera;

	public RobotControls (UsbCamera camera)
	{

		//
		_shiftTimer = new Timer();
		_pdp = new PowerDistributionPanel(Enums.PDP_CAN_ID);
		
		//simple XCatsDrive, no PID etc
		_drive = new XCatsDrive (Enums.USE_CAN,true);
		_drive.setDashboardIO(false, false);
	
		_feeder = new Feeder();
		_climber = new Climber();
		_gear = new Gear(_drive);
		_autoTarget = new AutoTarget(_camera,false);
		
		//_drive.setPDP(_pdp, Enums.BROWNOUT_VOLTAGE_THRESHOLD, Enums.BROWNOUT_VOLTAGE_REDUCTIONFACTOR);
		
		
		if (Enums.USE_COMPRESSOR){
			_compressor = new Compressor(Enums.PCM_CAN_ID);
			
			_dblSolShifter = new DoubleSolenoid(Enums.PCM_CAN_ID,Enums.PCM_SHIFTER_FORWARD,Enums.PCM_SHIFTER_REVERSE);			
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
		_feederLifter = new XCatsJSButton(_operatorJS,5);
				
		try
		{
//			CameraServer camera = CameraServer.getInstance();
//			camera.startAutomaticCapture(0);
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

		SmartDashboard.putBoolean("isEjecting", _gear.isEjecting());
		
		if (_gear.isEjecting())
			return;
			
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
			else if (Math.abs(_drive.get(Enums.FRONT_LEFT)) > Enums.SHIFTER_DELAY_SPEED)
				_drive.set( directionLeft * Enums.SHIFTER_DELAY_SPEED,  directionRight * Enums.SHIFTER_DELAY_SPEED);			
		}
		
		SmartDashboard.putNumber("LeftSpeed", _drive.get(Enums.FRONT_LEFT));
		SmartDashboard.putNumber("Direction", directionLeft);
		SmartDashboard.putBoolean("Shifter", _slowMode);		

		if (_navx != null){
			
			if (Enums.TWO_JOYSTICKS){
			}
			else {
				if(_driveJS.getRawButton(5)){
				_navx.navxMode = "rotate";
				_navx.navxRotateDistance = 90;
				}
				if(_driveJS.getRawButton(5)){
				_navx.navxMode = "rotate";
				_navx.navxRotateDistance = 90;
				}
			}
		}
		if(Enums.TWO_JOYSTICKS){
			if(_leftJS.getRawButton(1)){
				_autoTarget.captureImage();
			}
		}else{
			if(_driveJS.getRawButton(7)){
				_autoTarget.captureImage();
			}
		}

	}

	public void operate ()
	{
		if(_operatorJS.getRawButton(8))
			_gear.eject();
		
		if (_operatorJS.getRawButton(6))
			_gear.acquireGear();
		
		//these feeder buttons are mutually exclusive, but need to be on a 
		//different thumb from the feeder raise and lower
		if(_operatorJS.getRawButton(1))
			_feeder.intake();
		else if (_operatorJS.getRawButton(2))
			_feeder.feed();
		else if(_operatorJS.getRawButton(3))
			_feeder.lowGoal();
		else
			_feeder.stop();

		boolean liftToggle = _feederLifter.isPressed();
		if (liftToggle != _liftMode ){
			_liftMode = ! _liftMode;
			_feeder.toggleLifter();
		}
		
	
		
		if(_operatorJS.getRawButton(7))
			_climber.climb();
//		else if (_operatorJS.getRawButton(5))
//			_climber.release();
		else
			_climber.stop();
		
	}


	public XCatsDrive getDrive()
	{
		return _drive;
	}
	
	public Gear getGear(){
		return _gear;
	}
	
	public void updateStatus ()
	{
		try {
//			SmartDashboard.putNumber("pdp total current", _pdp.getTotalCurrent());
//			SmartDashboard.putNumber("pdp total energy",_pdp.getTotalEnergy());
//			SmartDashboard.putNumber("pdp total power" ,_pdp.getTotalPower());
//			SmartDashboard.putNumber("pdp temperature",_pdp.getTemperature());
//			SmartDashboard.putNumber("pdp voltage",_pdp.getVoltage());			
		}
		catch (Exception e){
			System.out.println("error reading PDP... RobotControls.updateStatus");
		}
	
		_drive.updateStatus();
		_gear.updateStatus();
		_feeder.updateStatus();
		_climber.updateStatus();
		
		if (_navx != null){
			_navx.updateStatus();
			
		}
		if(_gear.isOnBoard())
			_operatorJS.setRumble(RumbleType.kRightRumble, 1);
	}
}