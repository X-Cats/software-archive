package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotControls {
	private Joystick _leftJS, _rightJS, _driveJS, _operatorJS;
	private XCatsDrive _drive;
	private CameraServer _camera;
	private boolean _reductionToggle = false, slowMode = true;
	private Acquisition _acq;
	private XCatsJSButton _speedToggleButton;
	private XCatsJSButton _highSpeedButton;
	private boolean _highSpeed = false;
	private DigitalOutput _doUltraPing;
	private DigitalInput _diUltraEcho;
	private Ultrasonic _ultra;



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
		_highSpeedButton = new XCatsJSButton(_operatorJS,8);
		
		if (Enums.DASHBOARD_INPUT)
			SmartDashboard.putBoolean("Use Joysticks", false);
		_acq = new Acquisition(_operatorJS);

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
			_camera.setQuality(25);
			_camera.startAutomaticCapture("cam0");
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
		
//		_acq.setShooterSpeed(_operatorJS.getRawAxis(5));
		
		
		//in and out is using up/down of the left joystick button
		if (_operatorJS.getRawAxis(1)> 0.15)
			_acq.release();
		else if (_operatorJS.getRawAxis(1)< -0.15)
			_acq.acquire();
		else	
			_acq.stop();
		
		//up and down is using the up/down of the left joystick button
//		if (_operatorJS.getRawAxis(1)> 0.2)
//			_acq.lower();
//		else if (_operatorJS.getRawAxis(1)<-0.2)
//			_acq.raise();
//		else	
//			_acq.holdPosition();

		//all the way up/home
		if (_operatorJS.getRawButton(1)){
			_acq.goHome();
		}
		
		//all the way down
		if (_operatorJS.getRawButton(4)){
			_acq.gotoGround();
		}
		
		//go to the low goal shooting position 
		if (_operatorJS.getRawButton(2)){
			_acq.gotoLowGoal();
		}		

		//home
		if (_operatorJS.getRawButton(3)){
			_acq.zeroLifter();
		}
	
		//this is the little black rotating keypad, not the joystick
		//180 is all the way down
		if (_operatorJS.getPOV() == 180){
			_acq.bumpPosition(-0.05);
		}

		//0 is all the way up
		if (_operatorJS.getPOV() == 0){
			_acq.bumpPosition(+0.05);
		}

		//check to see if the shooter speed button has been toggled on
		_highSpeed = _highSpeedButton.isPressed();		
		_acq.setShooterSpeed(!_highSpeed ? 0.9 : 0);
		
		
		//the right bump button shoots the left bump button reverses to clear a jam
		if (_operatorJS.getRawButton(6)){
			_acq.shoot();
		}
		else if (_operatorJS.getRawButton(5)){
			_acq.reverseShooter();
		}
		else{
			_acq.stopShoot();
		}
		

	
	}

	public void updateStatus ()
	{
		//		_drive.updateStatus();
		_acq.updateStatus();
		
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
	public Acquisition acquisition(){
		return _acq;
	}
}
