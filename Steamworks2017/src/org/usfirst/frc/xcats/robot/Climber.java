package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
//motor
//method climb
	private DigitalInput _lsWinch;
	private DigitalOutput _climberLights;
	private XCatsSpeedController _winch;
	
	public Climber(){
		//constructor
		_winch = new XCatsSpeedController("Winch",Enums.WINCH_CAN_ID,true,SCType.TALON,null,null);
		//_lsWinch = new DigitalInput (Enums.WINCH_LIMIT_SWITCH);
		_climberLights = new DigitalOutput(Enums.WINCH_LIGHTS_CHANNEL);
		
		//Set this inverted on 2017-03-01 because the gear box has changed and we want to keep the wires on the 
		//speed controller green-to-black.
		_winch.setInverted(true);
		}
	
	
	public void climb(){
//		if (_lsWinch.get()){
//			_winch.set(Enums.WINCH_CLIMB_SPEED);
//			_climberLights.set(true);
//			
//		} else{
//			_winch.set(0);
//			_climberLights.set(false);
//		}
		_winch.set(Enums.WINCH_CLIMB_SPEED);
		_climberLights.set(true);
		
	}

	public void release(){
		_winch.set(-Enums.WINCH_CLIMB_SPEED);
		
	}
	public void stop(){
		_winch.set(0);
		_climberLights.set(false);
	}
	public void updateStatus(){
		SmartDashboard.putBoolean("Winch Limit Switch", _lsWinch.get());
		
	}
}
