package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

//package org.usfirst.frc.team191.robot;



import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Acquisition {
	private Shooter _shooter;
	private boolean _isLow=false;
	//private CANTalon _liftMotor;
	private XCatsSpeedController _liftMotor;
	private XCatsSpeedController _acqGrab,_acqShoot;
	private Timer _shootTimer = new Timer();
	private DigitalInput _optShooter;
	private double _acqSpeed;
	private double _maxPosition=0;
	private boolean _shotComplete=false;
	private double _liftDirection = 1.0;

	
	
	public Acquisition(Joystick oj){

		if (Enums.IS_FINAL_ROBOT){
			// the motor on the final robot is reversed orientation from the prototype. We need to invert the drive, but not the encoder sensor
			_liftMotor = new XCatsSpeedController("Arm", Enums.ACQ_LIFT_MOTOR, XCatsSpeedController.SCType.TALON,false, 4096, 1, 0.125, 0, 0,null,null,CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
			_liftMotor.setInverted(true);
			_liftMotor.setDashboardIO(false, true);
		}
		else
			_liftMotor = new XCatsSpeedController("Arm", Enums.ACQ_LIFT_MOTOR,  XCatsSpeedController.SCType.TALON, false, 4096, 1, 0.125, 0, 0,null,null,CANTalon.FeedbackDevice.CtreMagEncoder_Relative);

		_liftMotor.setDashboardIO(false, true);

		_shooter = new Shooter(oj);
		
		//I don't know why, probably something in here is in the wrong order,  we have to call this twice but we do....
		zeroLifter();
		zeroLifter();
		
		
		if (Enums.IS_FINAL_ROBOT)
			_acqGrab  = new XCatsSpeedController("AcqGrab",Enums.ACQ_MOTOR_GRAB , true, SCType.TALON, null, null );
		else
			_acqGrab = new XCatsSpeedController("AcqGrab",Enums.ACQ_MOTOR_GRAB , true, SCType.JAGUAR, null, null );
			
		
		if (Enums.IS_FINAL_ROBOT)
			_acqShoot = new XCatsSpeedController("AcqShoot",Enums.ACQ_MOTOR_SHOOT_PWM , false, SCType.VICTOR_SP, null, null );
		else
			_acqShoot = new XCatsSpeedController("AcqShoot",Enums.ACQ_MOTOR_SHOOT , true, SCType.JAGUAR, null, null );
			
		_acqSpeed = Enums.ACQ_SPEED;
		
	}
	public void acquire(){
		_acqGrab.set(_acqSpeed);
		
		
	}
	public void release(){
		_acqGrab.set(-_acqSpeed);
			
	}
	public void stop(){
		_acqGrab.set(0);
		
	}
	
	//this is all the way up
	public void goHome(){
		_liftMotor.set(_liftDirection * -1.0);		
	}
	
	//this is all the way down
	public void gotoGround(){
		_liftMotor.set(_liftDirection * 0.9);
		
	}
	
	public void gotoLowGoal(){
		_liftMotor.set(_liftDirection * -0.20);		
	}
	
	public void shoot(){
		_acqShoot.set(1.0);
	}
	
	public void reverseShooter(){
		_acqShoot.set(-1.0);
	}
	
	public void zeroLifter(){
		if (Enums.IS_FINAL_ROBOT)
			_liftMotor.zeroSensorAndThrottle(CANTalon.FeedbackDevice.CtreMagEncoder_Relative,  1.00);
		else
			_liftMotor.zeroSensorAndThrottle(CANTalon.FeedbackDevice.CtreMagEncoder_Relative,  -1.00);
		
	}
	
	private void setPosition(double position){	
		_liftMotor.set(_liftDirection * position);
	}
	
	public void bumpPosition(double delta){
		setPosition(_liftDirection * _liftMotor.getSetPoint()+delta);
	}
	public void stopShoot(){
		_acqShoot.set(0);
	}
	public void positionForLowShot(){
		_isLow = true;
		
	}
	public void positionForHighShot(){
		_isLow = false;
	}
	
	public void setShooterSpeed(double speed){
		//if the liftMotor is positioned in the low shooting position, only set the speed to 0.5 instead of full
		if (speed > 0){
			if (Math.abs(_liftMotor.getSetPoint()) > 0.5 &&  Math.abs(_liftMotor.getSetPoint()) < 0.9)
				_shooter.set(0.5);
			else
				_shooter.set(speed);			
		}
		else
			_shooter.set(speed);
	}
	
	public boolean setShooterAndShoot(){
		if (_shootTimer.get() == 0) {
			_shotComplete = false;
			_shooter.set(1.0);
			_shootTimer.reset();
			_shootTimer.start();
		} 
		return _shotComplete;
	}
	
	
	public void bumpAcqSpeed(double delta){
		_acqSpeed = _acqSpeed+delta;
		if (_acqSpeed > 1)
			_acqSpeed = 1;
		if (_acqSpeed < -1)
			_acqSpeed = -1;
	}
	
	public double getPosition(){
		return _liftMotor.getEncPosition();
	}
	public void updateStatus(){
		//print out stuff, timer stuff
		
		if (_shootTimer.get() > 0){
			if (_shooter.readyToShoot()){
				shoot();
				_shotComplete = true;
				_shootTimer.stop();
				_shootTimer.reset();
			}				
		}
		
		_maxPosition = Math.max(_maxPosition, _liftMotor.getPosition());
		SmartDashboard.putNumber("Acq Speed", _acqSpeed);
		SmartDashboard.putNumber("Arm Position", this.getPosition());
		_shooter.updateStatus();
	}
}