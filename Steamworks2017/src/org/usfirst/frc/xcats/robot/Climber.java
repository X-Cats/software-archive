package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

public class Climber {
//motor
//method climb
	private XCatsSpeedController _winch;
	
	public Climber(){
		//constructor
		_winch = new XCatsSpeedController("Winch",Enums.WINCH_CAN_ID,true,SCType.TALON,null,null);
		_winch.setInverted(true);
		}
	
	
	public void climb(){
		
		_winch.set(Enums.WINCH_CLIMB_SPEED);
		
	}

	public void release(){
		_winch.set(-Enums.WINCH_CLIMB_SPEED);
		
	}
	public void stop(){
		_winch.set(0);
	}
	public void updateStatus(){
		
	}
}
