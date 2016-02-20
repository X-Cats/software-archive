package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	
	private XCatsSpeedController _master;
	private XCatsSpeedController _follower;
	private Joystick _oj;
	private boolean _readyToShoot=false;
	
	public Shooter(Joystick oj){
		
		_master = new XCatsSpeedController("Shooter Master", Enums.SHOOTER_MOTOR_MASTER, true, true, 4096, 10000, 0.125, 0, 0,null,null,CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		_master.reverseSensor(true);
		_master.setDashboardIO(false, true);
		
		_follower = new XCatsSpeedController("Shooter Follower", Enums.SHOOTER_MOTOR_FOLLOWER, true, true, null, null);
		_follower.setFollower(Enums.SHOOTER_MOTOR_MASTER);
			
		_oj = oj;
		
	}
	
	public double getPosition(){
		return _master.getEncPosition();
	}
	public double getSpeed(){
		return _master.getSpeed();
	}
	public void set(double speed){
		_readyToShoot = false;
		_master.set(speed);
	}
		
	public boolean readyToShoot(){
		return _readyToShoot;
	}
	
	public void updateStatus(){
		_master.updateStatus();
		SmartDashboard.putNumber("Shooter Setpoint", _master.getSetPoint());
		SmartDashboard.putNumber("Shooter Speed", this.getSpeed());
		SmartDashboard.putNumber("Shooter Encoder", this.getPosition());
		
		if (this.getSpeed() > 6000){
			_oj.setRumble(RumbleType.kRightRumble,1);
			_readyToShoot=true;
		} else
		{
			_oj.setRumble(RumbleType.kRightRumble,0);
			_readyToShoot = false;
		}
			
		

		//		SmartDashboard.putNumber("Shooter abs", _master.getPulseWidthPosition());
	}
	
}
