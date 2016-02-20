package org.usfirst.frc.team192.robot;


import edu.wpi.first.wpilibj.CANJaguar;
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
	private CANJaguar _acqGrab,_acqShoot;
	private Timer _shootTimer = new Timer();
	private DigitalInput _optShooter;
	private double _acqSpeed;
	private double _maxPosition=0;
	private boolean _shotComplete=false;
	
	
	public Acquisition(Joystick oj){
		//_liftMotor = new CANTalon(Enums.ACQ_LIFT_MOTOR);
		_liftMotor = new XCatsSpeedController("Arm", Enums.ACQ_LIFT_MOTOR, false,true, 4096, 1, 0.125, 0, 0,null,null,CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		_liftMotor.setDashboardIO(false, true);

		_shooter = new Shooter(oj);
		
		//I don't know why, probably something in here is in the wrong order,  we have to call this twice but we do....
		zeroLifter();
		zeroLifter();
		
		_acqGrab = new CANJaguar(Enums.ACQ_MOTOR_GRAB);
		_acqShoot = new CANJaguar(Enums.ACQ_MOTOR_SHOOT);
		
//		_liftMotor.changeControlMode(CANTalon.TalonControlMode.Position);
		//_optShooter = new DigitalInput(Enums.SHOOTER_OPTICAL_DI);
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
		_liftMotor.set(-1.0);		
	}
	
	//this is all the way down
	public void gotoGround(){
		_liftMotor.set(1.0);
		
	}
	
	public void gotoLowGoal(){
		_liftMotor.set(-0.30);		
	}
	
	public void shoot(){
		_acqShoot.set(1.0);
	}
	
	public void zeroLifter(){
		_liftMotor.zeroSensorAndThrottle(CANTalon.FeedbackDevice.CtreMagEncoder_Relative, -1.00);
	}
	public void setPosition(double position){
		
		_liftMotor.set(position);
	}
	
	public void bumpPosition(double delta){
		setPosition(_liftMotor.getSetPoint()+delta);
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
