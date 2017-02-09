package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

public class Feeder {
	private XCatsSpeedController _topFeeder;
	private XCatsSpeedController _bottomFeeder;
	
	public Feeder(){
		
		_topFeeder =  new XCatsSpeedController("Top Feeder",Enums.FEEDER_TOP_CAN,true,SCType.TALON,null,null);
		_bottomFeeder =  new XCatsSpeedController("Top Feeder",Enums.FEEDER_TOP_CAN,true,SCType.TALON,null,null);
		
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
	public void stop(){
		_topFeeder.set(0);
		_bottomFeeder.set(0);
	}
	
	public void updateStatus(){
		
	}
}
