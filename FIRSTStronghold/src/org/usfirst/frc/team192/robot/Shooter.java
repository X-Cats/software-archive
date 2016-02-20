package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	
	private CANTalon _master;
	private CANTalon _follower;
	private Joystick _oj;
	
	public Shooter(Joystick oj){
		
		_master = new CANTalon(Enums.SHOOTER_MOTOR_MASTER);
		_follower = new CANTalon(Enums.SHOOTER_MOTOR_FOLLOWER);
		_follower.changeControlMode(TalonControlMode.Follower);
		_follower.set(_master.getDeviceID());
		
		_master.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		_master.enableControl();	
		
		_oj = oj;
		
	}
	
	public double getPosition(){
		return _master.getEncPosition();
	}
	public double getSpeed(){
		return _master.get();
	}
	public void set(double speed){
		_master.set(speed);
		if (speed == 0){
			_master.setPosition(0);
		}
	}
	
	public void shootLow(){
	
	}
	public void shootHigh(){
		
	}
	
	public void updateStatus(){
		SmartDashboard.putNumber("Shooter Speed", this.getSpeed());
		SmartDashboard.putNumber("Shooter Encoder", this.getPosition());
		
		if (this.getPosition() < -6000000){
			_oj.setRumble(RumbleType.kRightRumble,1);
		} else
			_oj.setRumble(RumbleType.kRightRumble,0);
			
		

		//		SmartDashboard.putNumber("Shooter abs", _master.getPulseWidthPosition());
	}
	
}
