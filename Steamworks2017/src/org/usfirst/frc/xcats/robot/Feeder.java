package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feeder {
	private XCatsSpeedController _topFeeder;
	private XCatsSpeedController _bottomFeeder;
	private DoubleSolenoid _lifter;
	private Timer _initTimer = new Timer();
	private boolean _init = false;
	
	public Feeder(){
		
		_topFeeder =  new XCatsSpeedController("Top Feeder",Enums.FEEDER_TOP_CAN,true,SCType.TALON,null,null);
		_bottomFeeder =  new XCatsSpeedController("Bottom Feeder",Enums.FEEDER_BOTTOM_CAN,true,SCType.TALON,null,null);
		_lifter = new DoubleSolenoid(Enums.PCM_CAN_ID,Enums.FEEDER_LIFT_PCM_FORWARD,Enums.FEEDER_LIFT_PCM_BACKWARD);
		dropBar();
		
	}

	public void toggleLifter(){
		if (_lifter.get() == Value.kForward)
			lower();
		else
			lift();
	}
	
	public void lift(){
		_lifter.set(Value.kForward);
	}
	public void lower(){
		_lifter.set(Value.kReverse);
	}
	public void intake(){
		//These should be opposite directions
		_topFeeder.set(Enums.FEEDER_INTAKE_SPEED);
		_bottomFeeder.set( - Enums.FEEDER_INTAKE_SPEED);
	}
	public void lowGoal(){
		
		//these should be set to the SAME value
		_topFeeder.set(Enums.FEEDER_FEED_SPEED);
		_bottomFeeder.set(Enums.FEEDER_FEED_SPEED);
		
	}
	public void feed(){
		
		_topFeeder.set(- Enums.FEEDER_FEED_SPEED);
		_bottomFeeder.set(0);
	}
	public void dropBar(){
		if(_init){
			return;
		}
		_init = true;
		_initTimer.reset();
		_initTimer.start();
	}
	public void stop(){
		_topFeeder.set(0);
		_bottomFeeder.set(0);
	}
	
	public void updateStatus(){
		
		if (_lifter == null)
			return;
		
		boolean liftState=false;
		
		if (_lifter.get() == Value.kForward)
			liftState = true;
		
		SmartDashboard.putBoolean("Ball Lifter is Up", !liftState);
		
		if(_init){
			if(_initTimer.get() < 0.10){
				System.out.println("initializing feeder");
			_topFeeder.set(- Enums.FEEDER_FEED_SPEED);
			}else{
				_initTimer.stop();
				_topFeeder.set(0);
				_init = false;
			}
		}
	}
}
