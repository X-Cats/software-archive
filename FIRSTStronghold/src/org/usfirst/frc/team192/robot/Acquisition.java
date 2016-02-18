package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
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
	
	
	public Acquisition(){
		_shooter = new Shooter();
		//_liftMotor = new CANTalon(Enums.ACQ_LIFT_MOTOR);
		_liftMotor = new XCatsSpeedController("Arm", Enums.ACQ_LIFT_MOTOR, false,true, 4096, 0.125, 0, 0,null,null);
		_liftMotor.setDashboardIO(false, true);
		
		//I dont know why we have to call this twice but we do....
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
	
	public void raise(){
		_liftMotor.set(Enums.ACQ_LIFT_SPEED);		
	}
	public void lower(){
		_liftMotor.set(-Enums.ACQ_LIFT_SPEED);
		
	}
//	public void holdPosition(){
//		_liftMotor.set(0);
//	}
	public void shoot(){
//		_shootTimer.reset();
//		_shootTimer.start();
		_acqShoot.set(1.0);
//		if (_isLow)			
//		_shooter.shootLow();
//		else
//			_shooter.shootHigh();
	}
	
	public void zeroLifter(){
		_liftMotor.zeroSensorAndThrottle(CANTalon.FeedbackDevice.CtreMagEncoder_Relative, -1.00);
	}
	public void setPosition(double position){
		
		_liftMotor.set(position);
	}
	
	public void bumpPosition(double delta){
		_liftMotor.set(_liftMotor.getSpeed()+delta);
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
		_shooter.set(-speed);
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
		_maxPosition = Math.max(_maxPosition, _liftMotor.getPosition());
		SmartDashboard.putNumber("Acq Speed", _acqSpeed);
//		SmartDashboard.putBoolean("Shooter Optical", _optShooter.get());
		SmartDashboard.putNumber("Arm Position", this.getPosition());
//		SmartDashboard.putNumber("Arm Postion abs", _liftMotor.getPulseWidthPosition());
		_shooter.updateStatus();
//		if (_shootTimer.get() > Enums.SHOOT_TIME){
//			_shootTimer.stop();
//		}
	}
}
