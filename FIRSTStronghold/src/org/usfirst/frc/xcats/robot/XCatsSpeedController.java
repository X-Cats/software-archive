package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class XCatsSpeedController{

	public enum SCType{TALON,JAGUAR,VICTOR_SP};
	private SpeedController motor;
	private CANSpeedController _CANmotor;
	private SCType _sctype; 
	/*
	 * The _scale and the units depend on the mode the Jaguar is in.
	 * In percentVbus mode, the outputValue is from -1.0 to 1.0 (same as PWM Jaguar).
	 * In voltage mode, the outputValue is in volts. 
	 * In current mode, the outputValue is in amps. 
	 * In speed mode, the outputValue is in rotations/minute.
	 * In position mode, the outputValue is in rotations.
	 */
	private double _scale;
	private int _invert = 1;
	private boolean _useRawInput = false;
	private double _setPoint = 0;
	private double _cutOffSetPointP = 0, _cutOffSetPointN = 0; //what the motor should be set to when it hits a limit or something. the first is for positive speeds, the second negative.
	private int _cutOffDirection = 0;
	private double _p, _i, _d;
	private int _codesPerRev;
	private String _name;
	private boolean _dashboardInput = false;
	private boolean _dashboardOutput = false;
	private boolean _stopping = false;
	private boolean _speedMode = false;
	private Timer _stopTimer;
	private DigitalInput _upperLimit;
	private DigitalInput _lowerLimit;

	//this constructor is used with a controller that has a digital input that acts as a switch
	public XCatsSpeedController (String name, int channel, boolean useCan, SCType sctype,  DigitalInput lowerLimit, DigitalInput upperLimit)
	{
		try{
			_upperLimit = upperLimit;
			_lowerLimit = lowerLimit;
			_name = name;
			_scale = 1;
			_sctype = sctype;
			if (useCan)
			{
				switch ( sctype){
				case TALON :
					this._CANmotor = new CANTalon(channel);
					this.motor = _CANmotor;			// all CANSpeedControllers are SpeedControllers
					//this makes sure the talon operates between 0 and 12 volts, voltage > 12 is unpredictable
					((CANTalon) _CANmotor).configNominalOutputVoltage(0f, -0f);
					((CANTalon) _CANmotor).configPeakOutputVoltage(12.0f, -12.0f);
					((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.PercentVbus);
					((CANTalon) _CANmotor).enableControl();
					break;
				case JAGUAR :
					this._CANmotor = new CANJaguar(channel);
					this.motor = _CANmotor;
					//					((CANJaguar) motor).setPercentMode();
					//					((CANJaguar) motor).enableControl();
					break;
				default:
					System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
				}				
			}
			else
			{
				switch ( sctype){
				case TALON:
					this.motor = new Talon(channel);				
					break;
				case JAGUAR:
					this.motor = new Jaguar(channel);				
					break;
				case VICTOR_SP:
					this.motor = new VictorSP(channel);				
					break;
				default:
					System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
				}				
				
			}
			

			_stopTimer = new Timer();

			this.setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT);			
		}
		catch (Exception e){

			System.out.println("------------------------------------------------------");
			System.out.println("Speed controller: "+channel +" could not intitialize!");
			System.out.println("");
			e.printStackTrace();			
			System.out.println("------------------------------------------------------");
		}


	}

	
	//this constructor is only used when we want to try using PID control
	public XCatsSpeedController (String name, int channel, SCType sctype, boolean speedMode, int codesPerRev, int scale, double p, double i, double d,DigitalInput lowerLimit,DigitalInput upperLimit, CANTalon.FeedbackDevice feedback)
	{
		this._codesPerRev = codesPerRev;
		this._p = p;
		this._i = i;
		this._d = d;
		_name = name;
		_scale = scale ;
		_speedMode = speedMode;
		_sctype = sctype;

		switch ( sctype){
		case TALON:
			_CANmotor = new CANTalon(channel);
			motor = _CANmotor;
			
			this.setFeedbackDevice(feedback);
			
			//this makes sure the talon operates between 0 and 12 volts, voltage > 12 is unpredictable
			((CANTalon) _CANmotor).configNominalOutputVoltage(0f, -0f);
			((CANTalon) _CANmotor).configPeakOutputVoltage(12.0f, -12.0f);

			this.switchModeToPID();
			break;
		case JAGUAR:
			_CANmotor = new CANJaguar(channel);
			motor = _CANmotor;
			this.switchModeToPID();
			break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}				
				
		_stopTimer = new Timer();
	}


	public void switchModeToPID(){
		if (_CANmotor != null){
			
			switch ( _sctype){
			case TALON:
				if (_speedMode)
				{
					((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.Speed);
				}
				else
				{
					((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.Position);
				}

				((CANTalon) _CANmotor).enableControl();			

				break;
			case JAGUAR:
				if (_speedMode)
				{					
					((CANJaguar) _CANmotor).setSpeedMode(CANJaguar.kQuadEncoder, _codesPerRev, _p, _i, _d);
				}
				else
				{
					((CANJaguar) _CANmotor).setSpeedMode(CANJaguar.kQuadEncoder, _codesPerRev, _p, _i, _d);
				}
				
				((CANJaguar) _CANmotor).enableControl();				
				break;
			default:
				System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
			}			
		}
	}
	
	public void switchModeToPercentVBus(){
		if (_CANmotor != null){
			switch ( _sctype){
			case TALON:
				((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.PercentVbus);
				((CANTalon) _CANmotor).enableControl();
				break;
			case JAGUAR:
				((CANJaguar) motor).setPercentMode();
				((CANJaguar) motor).enableControl();
				break;
			default:
				System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
			}								
		}		
	}

	public void set (double setPoint)
	{
		//i don't know why we are getting a stack dump at the beginning of the code, maybe it is an initialization thing
		//		try
		//		{
		/*
			//check to see if we have a limit switch for this speed controller
			//if you do not interrogate the limit switch before you set the speed controller value you can get "chatter" where the 
			//speed controller tries to drive it past the limit.

			//if the lower limit is not null, then check to see if it is reached and the caller is trying to drive it lower
			if (_lowerLimit != null)			
				if (_lowerLimit.get() &&  _cutOffDirection * setPoint < 0 )
					return;

			//if the upper limit is not null, then check to see if it is reached and the caller is trying to drive it higher
			if (_upperLimit != null)
				if (_upperLimit.get() && _cutOffDirection * setPoint > 0 )
					return;
		 */

		if (_cutOffDirection * setPoint <= 0)
		{
			this._setPoint = setPoint;
			_cutOffDirection = 0;
		}
		else
			this._setPoint = _cutOffDirection > 0 ? _cutOffSetPointP : _cutOffSetPointN;


		if (_useRawInput)
			motor.set(this._setPoint);
		else
			motor.set(this._setPoint * _invert * _scale);

		if (_dashboardOutput)
			SmartDashboard.putNumber(_name + "_set_point", this._setPoint);					

			//		}
			//		catch (Exception e) {
			//			System.out.println ("Error! "+e.getStackTrace());
			//			return;
			//		}


			//			if (setPoint ==0)
			//				this.stop();
	}

	public void stop ()
	{
		set(-_setPoint);
		this._stopping = true;
		_stopTimer.start();
	}

	public void setFeedbackDevice(CANTalon.FeedbackDevice device){
		switch ( _sctype){
		case TALON:
			((CANTalon) _CANmotor).setFeedbackDevice(device);
			break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}					
		
	}
	public void zeroSensorAndThrottle(CANTalon.FeedbackDevice feedbackdevice,double zero){
		switch ( _sctype){
		case TALON:
			_setPoint = zero;
			((CANTalon) _CANmotor).setPosition(zero); /* start our position at zero, this example uses relative positions */
			setFeedbackDevice(feedbackdevice);
			((CANTalon) _CANmotor).set(zero);
//			 Thread.Sleep(100); 		
			break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}							
	}
	public void setPID (double p, double i, double d)
	{
		if (_CANmotor != null){
			this._p = p;
			this._i = i;
			this._d = d;

			if (_dashboardOutput)
			{
				SmartDashboard.putNumber(_name + "_p", p);
				SmartDashboard.putNumber(_name + "_i", i);
				SmartDashboard.putNumber(_name + "_d", d);
			}

			_CANmotor.setPID(p, i, d);			
		}
	}

	public void setCutOffDirection (int cutOffDirection)
	{
		this._cutOffDirection = cutOffDirection;
	}

	public void setCutOffSetPointP (double setPoint)
	{
		_cutOffSetPointP = setPoint;
	}

	public void setCutOffSetPointN (double setPoint)
	{
		_cutOffSetPointN = setPoint;
	}

	public void setUseRawInput (boolean useRawInput)
	{
		this._useRawInput = useRawInput;

		if (_dashboardOutput)
			SmartDashboard.putBoolean(_name + "_raw_input", _useRawInput);
	}

	public void setInverted(boolean invert)
	{
		if (invert)
			this._invert = -1;
		else
			this._invert = 1;
	}

	public void setDashboardIO (boolean input, boolean output)
	{
		this._dashboardInput = input;
		this._dashboardOutput = output;


		if (_dashboardOutput)
		{
			SmartDashboard.putNumber(_name + "_set_point", _setPoint);
			SmartDashboard.putBoolean(_name + "_raw_input", _useRawInput);

			if (_CANmotor != null ){
				if (_CANmotor.getControlMode() != CANJaguar.JaguarControlMode.PercentVbus && 
						_CANmotor.getControlMode() != CANTalon.TalonControlMode.PercentVbus){
					SmartDashboard.putNumber(_name + "_p", _p);
					SmartDashboard.putNumber(_name + "_i", _i);
					SmartDashboard.putNumber(_name + "_d", _d);				
				}
			}
		}
	}

	//	public void setPercentMode ()
	//	{
	//		if (motor instanceof CANJaguar)
	//		{
	//			((CANJaguar) motor).setPercentMode();
	//			((CANJaguar) motor).enableControl();
	//		}
	//		else
	//		{
	//			((CANTalon) motor).changeControlMode(ControlMode.PercentVbus);
	//			((CANTalon) motor).enableControl();
	//		}
	//	}

	//only use this if p, i, d, and codesPerRev values have already been set (as through the constructor).
	//	public void setPositionMode ()
	//	{
	//		if (motor instanceof CANJaguar)
	//		{
	//			((CANJaguar) motor).setPositionMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
	//			((CANJaguar) motor).enableControl();
	//		}
	//		else
	//		{
	//			((CANTalon) motor).changeControlMode(ControlMode.Position);
	//			((CANTalon) motor).setPID(p, i, d);
	//			((CANTalon) motor).enableControl();
	//		}
	//	}

	public boolean isTalon ()
	{
		return motor instanceof CANTalon;
	}

	public CANSpeedController.ControlMode getTalonControlMode ()
	{
		if (_CANmotor == null){
			return null;
		}

		switch (_sctype){
		case TALON:
			return ((CANTalon) _CANmotor).getControlMode();			
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
			return null;
		}
	}

	public void setFollower(int masterChannel){
		
		switch (_sctype){
		case TALON:
			 ((CANTalon) _CANmotor).changeControlMode(TalonControlMode.Follower);
			 ((CANTalon) _CANmotor).set(masterChannel);
			 break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}		
	}
	
	public void zeroEncoder ()	
	{

		if (_CANmotor != null){		
			switch (_sctype){
			case TALON:
				((CANTalon) _CANmotor).setPosition(0);			
				break;
			case JAGUAR:
				((CANJaguar) _CANmotor).enableControl(0);
				break;
			default:
				System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
			}
		}
	}

	public double getSetPoint ()
	{
		return _setPoint;
	}

	public double getCurrent ()
	{
		return _CANmotor.getOutputCurrent();
	}

	public CANSpeedController.ControlMode getJaguarControlMode ()
	{
		if (_CANmotor != null){		
			switch (_sctype){
			case JAGUAR:
				return ((CANJaguar) motor).getControlMode();
			default:
				System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
				return null;
			}		
		}
		else
			return null;
	}

	public double getSpeed ()
	{
		if (_CANmotor == null){
			return motor.get();
		}else
			return _CANmotor.getSpeed();
	}
	
	public double get(){
		return motor.get();
	}

	public void setPosition(double position)
	{
		switch (_sctype){
		case TALON:
			((CANTalon)_CANmotor).setPosition(position) ;
			break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}
				
	}
	
	public double getPosition ()
	{
		if (_CANmotor == null){
			return 0;
		}
		else
			return _CANmotor.getPosition() / _invert / _scale;
	}

	public double getEncPosition ()
	{
		switch (_sctype){
		case TALON:
			return ((CANTalon) motor).getEncPosition();			
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
			return 0;
		}		
	}

	public void reverseSensor (boolean invert)
	{
		//note: you may need to use this if the RPMs are negative and you have an encoder
		switch (_sctype){
		case TALON:
			((CANTalon) motor).reverseSensor(invert);
			break;
		default:
			System.out.println("DANGER DANGER DANGER -- speed controller type in XCatsSpeedController not handled!");
		}				
	}

	public void selfTest ()
	{
		//		((CANTalon) motor).
	}

	public void updateStatus()
	{
			
		if (_CANmotor != null){
			//don't do anything with following motors
			if (_CANmotor.getControlMode() == CANTalon.TalonControlMode.Follower)
				return;			
		}

		
		//SmartDashboard.putBoolean(_name+"_I/O output", _dashboardOutput);
		
		/*
		if (motor instanceof CANTalon){
			
		}*/
		
		if (_stopping && _stopTimer.get() > Enums.MOTOR_STOP_TIME)
		{
			set(0);
			_stopping = false;
			_stopTimer.stop();
			_stopTimer.reset();
		}

		if (_CANmotor != null)
		{			
			//check the control mode, if it's not PercentVbus its going to be PID control
			if (_dashboardInput )
			{
				if (_CANmotor.getControlMode() != CANJaguar.JaguarControlMode.PercentVbus && 
						_CANmotor.getControlMode() != CANTalon.TalonControlMode.PercentVbus){
					_p = SmartDashboard.getNumber(_name + "_p");
					_i = SmartDashboard.getNumber(_name + "_i");
					_d = SmartDashboard.getNumber(_name + "_d");					
				}

				_CANmotor.setPID(_p, _i, _d);
			}

			if (_dashboardOutput)
			{
				if (motor instanceof CANTalon)
					SmartDashboard.putBoolean(_name + " is RPM Mode", ((CANTalon) motor).getControlMode() == CANTalon.TalonControlMode.Speed ? true : false);

				SmartDashboard.putNumber(_name + "_current", _CANmotor.getOutputCurrent());
				SmartDashboard.putNumber(_name + "_speed", _CANmotor.getSpeed());
				
				if (_CANmotor instanceof CANJaguar)
					SmartDashboard.putNumber(_name + "_position", ((CANJaguar) _CANmotor).getPosition());
				else
					SmartDashboard.putNumber(_name + "_position", ((CANTalon) _CANmotor).getPosition() / _invert / _scale);					
				//				SmartDashboard.putNumber(_name + "_encoder", ((CANJaguar) motor).);
			}
		}
		
		if (_dashboardInput)
		{
			_useRawInput = SmartDashboard.getBoolean(_name + "_raw_input");
			_setPoint = SmartDashboard.getNumber(_name + "_set_point");
		}

		set(_setPoint);
	}
}
