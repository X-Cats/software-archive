package org.usfirst.frc.xcats.robot;

import org.usfirst.frc.xcats.robot.XCatsSpeedController.SCType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gear {
	private DoubleSolenoid _sol;
	private DigitalInput _optoRotate;
	private DigitalInput _optoOnBoard;
	private XCatsSpeedController _gearRotator;
	private boolean _acquiring = false;
    private boolean _ejecting = false;
    private Timer _ejectTimer= new Timer();
	
	public Gear(){
		
		// constructor
		_sol = new DoubleSolenoid(Enums.PCM_CAN_ID,Enums.GEAR_PCM_FORWARD,Enums.GEAR_PCM_REVERSE);
		_optoRotate = new DigitalInput(Enums.GEAR_POSITIONED_OPT);
		_optoOnBoard = new DigitalInput(Enums.GEAR_ONBOARD_OPT);
		_gearRotator =  new XCatsSpeedController("Gear Rotator",Enums.GEAR_ROTATOR_CAN_ID,false,SCType.TALON,null,null);
		
	}
	
	public void acquireGear(){
		if (_acquiring)
			return;
		
		_acquiring = true;
		
	}
	public void eject(){
		if (!_ejecting){
			_sol.set(Value.kForward);
			_ejecting = true;
			_ejectTimer.reset();
			_ejectTimer.start();
		}
	}
	public boolean isOnBoard(){
		return _optoOnBoard.get();
	}

	//this needs to be called by robot controls to update the acquisition of the gear
	public void updateStatus(){
		SmartDashboard.putBoolean("Gear on Board",_optoOnBoard.get());
		SmartDashboard.putBoolean("Gear Positioned", _optoRotate.get());
		
		if (_ejecting){
			if (_ejectTimer.get() >= Enums.GEAR_EJECT_TIME){
				_sol.set(Value.kReverse);
				_ejectTimer.stop();
				_ejecting = false;
			}
		}
		
		if (_acquiring){
			if (_optoRotate.get())
				_gearRotator.set(Enums.GEAR_ROTATOR_SPEED);
			else {
				_gearRotator.set(0);
				_acquiring = false;
			}
				
		}
		
	}
	
}
