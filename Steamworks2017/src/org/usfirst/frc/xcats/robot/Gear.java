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
	private DigitalInput _LS;
	private DigitalInput _RS;
	private XCatsSpeedController _gearRotator;
	private boolean _acquiring = false;
    private boolean _ejecting = false;
    private Timer _ejectTimer= new Timer();
    private int _movingRight = 1;
    private int _movingLeft = -1;
    private int _gearDirection = _movingRight;
	
	public Gear(){
		
		// constructor
		_sol = new DoubleSolenoid(Enums.PCM_CAN_ID,Enums.GEAR_PCM_FORWARD,Enums.GEAR_PCM_REVERSE);
		_RS= new DigitalInput(Enums.GEAR_LS_CHANNEL);
		_LS= new DigitalInput(Enums.GEAR_RS_CHANNEL);
		_optoRotate = new DigitalInput(Enums.GEAR_POSITIONED_OPT);
		
//		_optoOnBoard = new DigitalInput(Enums.GEAR_ONBOARD_OPT);
		_gearRotator =  new XCatsSpeedController("Gear Rotator",Enums.GEAR_ROTATOR_PWM_ID,true,SCType.TALON,null,null);
		
	}
	
	public void acquireGear(){
		if (_acquiring)
			return;
		
		if ((_LS.get() && _gearDirection == _movingLeft) || (_RS.get() && _gearDirection == _movingRight)){
			_gearDirection = -1 * _gearDirection;
		}
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
		if (_optoOnBoard != null){
			return _optoOnBoard.get();			
		} else {
			return false;
		}
	}

	//this needs to be called by robot controls to update the acquisition of the gear
	public void updateStatus(){
//		SmartDashboard.putBoolean("Gear on Board",_optoOnBoard.get());
//		SmartDashboard.putBoolean("Gear Positioned", _optoRotate.get());
		
		SmartDashboard.putBoolean("Left Limit Switch Direction", _LS.get());
		SmartDashboard.putBoolean("Right Limit Switch Direction", _RS.get());
		SmartDashboard.putBoolean("Gear Positioner", _optoRotate.get());
		SmartDashboard.putBoolean("Gear Aquiring", _acquiring);
		SmartDashboard.putNumber("Gear Rotation",_gearDirection);
		
		if (_ejecting){
			if (_ejectTimer.get() >= Enums.GEAR_EJECT_TIME){
				_sol.set(Value.kReverse);
				_ejectTimer.stop();
				_ejecting = false;
			}
		}
		
		if (_acquiring){
			if((_LS.get() && _gearDirection == _movingLeft) || ( _RS.get() && _gearDirection == _movingRight) ){
				SmartDashboard.putBoolean("Running", false);
				_gearRotator.set(0);
				_gearDirection = _gearDirection * -1;
				_acquiring=false;
			}
			else if (!_optoRotate.get()){
				_gearRotator.set(0);
				_gearDirection = _gearDirection * -1;
				_acquiring=false;				
			}
			else{
				SmartDashboard.putBoolean("Running", true);
				_gearRotator.set(_gearDirection * Enums.GEAR_ROTATOR_SPEED);
			}				
		}
		
	}
	
}
