package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

public class Climber {
//motor
//method climb
	private XCatsSpeedController _winch;
	
	public Climber(){
		//constructor
		_winch = new XCatsSpeedController("Winch",Enums.WINCH_CAN_ID,false,SCType.TALON,null,null);
		}
	
	
	public void climb(){
		
		_winch.set(Enums.WINCH_CLIMB_SPEED);
		
	}
	public void updateStatus(){
		
	}
}
