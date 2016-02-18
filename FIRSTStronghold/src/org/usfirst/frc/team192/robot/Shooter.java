package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	
	private CANTalon _master;
	private CANTalon _follower;
	
	public Shooter(){
		
		_master = new CANTalon(Enums.SHOOTER_MOTOR_MASTER);
		_follower = new CANTalon(Enums.SHOOTER_MOTOR_FOLLOWER);
		_follower.changeControlMode(TalonControlMode.Follower);
		_follower.set(_master.getDeviceID());
		
		_master.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		_master.enableControl();	
		
	}
	
	public double getPosition(){
		return _master.getEncPosition();
	}
	public double getSpeed(){
		return _master.get();
	}
	public void set(double speed){
		_master.set(speed);
	}
	
	public void shootLow(){
	
	}
	public void shootHigh(){
		
	}
	
	public void updateStatus(){
		SmartDashboard.putNumber("Shooter Speed", this.getSpeed());
		SmartDashboard.putNumber("Shooter Encoder", this.getPosition());
//		SmartDashboard.putNumber("Shooter abs", _master.getPulseWidthPosition());
	}
	
}
