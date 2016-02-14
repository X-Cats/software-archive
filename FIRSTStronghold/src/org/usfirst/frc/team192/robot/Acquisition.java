package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Acquisition {
	private Shooter _shooter;
	private boolean _isLow=false;
	private CANTalon _liftMotor;
	private CANJaguar _acq1,_acq2;
	
	public Acquisition(){
		_shooter = new Shooter();
		_liftMotor = new CANTalon(Enums.ACQ_LIFT_MOTOR);
		_acq1 = new CANJaguar(Enums.ACQ_MOTOR_1);
		_acq2 = new CANJaguar(Enums.ACQ_MOTOR_2);
		
	}
	public void acquire(){
		_acq1.set(Enums.ACQ_SPEED);
		
		
	}
	public void release(){
		_acq1.set(-Enums.ACQ_SPEED);
			
	}
	public void stop(){
		_acq1.set(0);
		
	}
	
	public void raise(){
		_liftMotor.set(Enums.ACQ_LIFT_SPEED);		
	}
	public void lower(){
		_liftMotor.set(-Enums.ACQ_LIFT_SPEED);
		
	}
	public void holdPosition(){
		_liftMotor.set(0);
	}
	public void shoot(){
		_acq2.set(1.0);
//		if (_isLow)			
//		_shooter.shootLow();
//		else
//			_shooter.shootHigh();
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
	
	public double getPosition(){
		return _liftMotor.getEncPosition();
	}
	public void updateStatus(){
		//print out stuff, timer stuff
		
		SmartDashboard.putNumber("Arm Position", this.getPosition());
		_shooter.updateStatus();
	}
}
