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
    private boolean _reversing = false;
    private Timer _ejectTimer= new Timer();
    private Timer _ejectReverseTimer = new Timer();
    private Timer _acqTimer = new Timer();
    private double _acqTimerLimit = 2.0;
    private int _movingRight = 1;
    private int _movingLeft = -1;
    private int _gearDirection = _movingRight;
    private XCatsDrive _xcDrive;
    private boolean _homing = false;
    private double _homingLimit = 0.45;
    private Timer _sanityTimer = new Timer();
    
	
	public Gear(XCatsDrive drive){
		
		//pass in the reference to the drive train
		_xcDrive = drive;
		
		// constructor
		_sol = new DoubleSolenoid(Enums.PCM_CAN_ID,Enums.GEAR_PCM_FORWARD,Enums.GEAR_PCM_REVERSE);
		_RS= new DigitalInput(Enums.GEAR_LS_CHANNEL);
		_LS= new DigitalInput(Enums.GEAR_RS_CHANNEL);
		_optoRotate = new DigitalInput(Enums.GEAR_POSITIONED_OPT);
		
//		_optoOnBoard = new DigitalInput(Enums.GEAR_ONBOARD_OPT);
		_gearRotator =  new XCatsSpeedController("Gear Rotator",Enums.GEAR_ROTATOR_PWM_ID,true,SCType.TALON,null,null);
		SmartDashboard.putNumber("AcquireTime", _acqTimerLimit);
				

	}
	
	public void goHome(){
		
		if (_homing)
			return;
		_homing = true;
		_acqTimer.reset();
		_acqTimer.start();
		
	}
	public void acquireGear(){
		
		System.out.println("Acquiring is " + _acquiring);
		if (_acquiring)
			return;
		
		System.out.println("Acquiring Gear "+ _LS.get() +", "+ _RS.get() +", "+_gearDirection );
		_acqTimerLimit = SmartDashboard.getNumber("AcquireTime",0.1);
		_acqTimer.reset();
		_acqTimer.start();
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
			_sanityTimer.reset();
			_sanityTimer.start();
		}
	}
	public boolean isEjecting(){
		return _ejecting;
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
		
		//this checks to see if we are in the ejection mode
		if (_ejecting){
			
			if (_sanityTimer.get() > Enums.GEAR_EJECT_TIME + Enums.GEAR_EJECT_REVERSE_START_TIME + 0.25){
				_ejectTimer.stop();
				_ejectReverseTimer.stop();
				_ejecting = false;
				_reversing = false;
				
			}else
			{
				//check to see if we have surpassed the start of the reverse time
				if (_ejectTimer.get() >= Enums.GEAR_EJECT_REVERSE_START_TIME){
					//if reversing then we should be setting the speed of the drive train to the reverse speed
					if (_reversing){
						//if the reverse time has timed out, we are done.
						if (_ejectReverseTimer.get() >= Enums.GEAR_EJECT_REVERSE_TIME){
							_ejectReverseTimer.stop();
							_ejectTimer.stop();
							_reversing = false;
							_ejecting = false;
						} else{
							_xcDrive.set(Enums.GEAR_EJECT_REVERSE_SPEED, Enums.GEAR_EJECT_REVERSE_SPEED);
						}
					} else {
						_ejectReverseTimer.reset();
						_ejectReverseTimer.start();
						_reversing = true;					
					}
					//check to see if we have retracted the piston yet. only do this once.
					if (_ejectTimer.get() >= Enums.GEAR_EJECT_TIME && _sol.get() == Value.kForward ){
						_sol.set(Value.kReverse);
					}				
				}
			
			}
			
		}
		
		SmartDashboard.putNumber("AcquireTime", _acqTimer.get());
		if (_acquiring){
			if((_LS.get() && _gearDirection == _movingLeft) || ( _RS.get() && _gearDirection == _movingRight) ){
				_gearRotator.set(0);
				_gearDirection = _gearDirection * -1;
				_acquiring=false;
				_acqTimer.stop();
			}
//			else if (!_optoRotate.get()){
//			else if (_acqTimer.get()>=_acqTimerLimit){
//				_gearRotator.set(0);
//				_gearDirection = _gearDirection * -1;
//				_acquiring=false;				
//			}
			else{
				_gearRotator.set(_gearDirection * Enums.GEAR_ROTATOR_SPEED);
			}				
		}
		
		if (_homing){
			if((_LS.get() && _gearDirection == _movingLeft) || ( _RS.get() && _gearDirection == _movingRight) || _acqTimer.get() > _homingLimit ){
				_gearRotator.set(0);
				_gearDirection = _gearDirection * -1;
				_homing=false;
				_acqTimer.stop();
			}
			else{
				_gearRotator.set(_gearDirection * Enums.GEAR_ROTATOR_SPEED);
			}				
			
		}
		
	}
	
}
