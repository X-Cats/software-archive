package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class XCatsSpeedController {

	private SpeedController motor;
	private CANSpeedController _CANmotor;
	/*
	 * The _scale and the units depend on the mode the Jaguar is in.<br>
	 * In percentVbus mode, the outputValue is from -1.0 to 1.0 (same as PWM Jaguar).<br>
	 * In voltage mode, the outputValue is in volts. <br>
	 * In current mode, the outputValue is in amps. <br>
	 * In speed mode, the outputValue is in rotations/minute.<br>
	 * In position mode, the outputValue is in rotations.
	 */
	private double _scale;
	private int _invert = 1;
	private boolean _useRawInput = false;
	private double _setPoint = 0;
	private double _cutOffSetPointP = 0, _cutOffSetPointN = 0; //what the motor should be set to when it hits a limit or something. the first is for positive speeds, the second negative.
	private double _p, _i, _d;
	private int _codesPerRev;
	private String _name;
	private boolean _dashboardInput = false;
	private boolean _dashboardOutput = false;
	private int _cutOffDirection = 0;
	private boolean _stopping = false;
	private Timer _stopTimer;
	private DigitalInput _upperLimit;
	private DigitalInput _lowerLimit;

	//this constructor is used with a controller that has a digital input that acts as a switch
	public XCatsSpeedController (String name, int channel, boolean useCan, boolean isTalon, DigitalInput lowerLimit, DigitalInput upperLimit)
	{
		try{
			_upperLimit = upperLimit;
			_lowerLimit = lowerLimit;
			_name = name;
			_scale = 1;
			if (useCan)
			{
				if (isTalon)
				{
					this._CANmotor = new CANTalon(channel);
					this.motor = _CANmotor;			// all CANSpeedControllers are SpeedControllers
					((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.PercentVbus);
					((CANTalon) _CANmotor).enableControl();
				}
				else
				{
					this._CANmotor = new CANJaguar(channel);
					this.motor = _CANmotor;
					//					((CANJaguar) motor).setPercentMode();
					//					((CANJaguar) motor).enableControl();
				}
			}
			else
				this.motor = new Jaguar(channel);				
			

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
	public XCatsSpeedController (String name, int channel, boolean speedMode, boolean isTalon, int codesPerRev, double p, double i, double d,DigitalInput lowerLimit,DigitalInput upperLimit)
	{
		this._codesPerRev = codesPerRev;
		this._p = p;
		this._i = i;
		this._d = d;
		_name = name;
		_scale = 1;

		if (isTalon)
		{
			
			_CANmotor = new CANTalon(channel);
			motor = _CANmotor;

			if (speedMode)
			{
				((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.Speed);
				_scale = 500;
			}
			else
			{
				((CANTalon) _CANmotor).changeControlMode(CANTalon.TalonControlMode.Position);
			}

			_CANmotor.setPID(p, i, d);
//			((CANTalon) _CANmotor).SelectProfileSlot(0);
			((CANTalon) _CANmotor).enableControl();
		}
		else
		{
			_CANmotor = new CANJaguar(channel);
			motor = _CANmotor;
			if (speedMode)
			{
				((CANJaguar) _CANmotor).setSpeedMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
				_scale = 5000;
			}
			else
			{
				((CANJaguar) _CANmotor).setPositionMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
			}
			((CANJaguar) _CANmotor).enableControl();
		}

		_stopTimer = new Timer();
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


	}

	public void stop ()
	{
		set(-_setPoint);
		this._stopping = true;
		_stopTimer.start();
	}

	public void setFeedbackDevice(CANTalon.FeedbackDevice device){
		((CANTalon) _CANmotor).setFeedbackDevice(device);
		
	}
	public void zeroSensorAndThrottle(CANTalon.FeedbackDevice feedbackdevice,double zero){
		_setPoint = zero;
		((CANTalon) _CANmotor).setPosition(zero); /* start our position at zero, this example uses relative positions */
		setFeedbackDevice(feedbackdevice);
		((CANTalon) _CANmotor).set(zero);
//		 Thread.Sleep(100); 		
	}
//	public void setSensorDirection(boolean direction){
//		((TalonSRX) _CANmotor).SetSensorDirection(direction);
//	}
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
		if (_CANmotor != null){
			return ((CANTalon) _CANmotor).getControlMode();			
		}
		return null;
	}

	public void setFollower(int masterChannel){
		if (motor instanceof CANTalon){
			 ((CANTalon) _CANmotor).changeControlMode(TalonControlMode.Follower);
			 ((CANTalon) _CANmotor).set(masterChannel);			
		}
	}
	
	public void zeroEncoder ()
	{
		if (_CANmotor != null){
			if (_CANmotor instanceof CANJaguar)
				((CANJaguar) _CANmotor).enableControl(0);
			else
				((CANTalon) _CANmotor).setPosition(0);			
		}
	}

	public double getSetPoint ()
	{
		return _setPoint;
	}

	public double getCurrent ()
	{
		return _CANmotor.getOutputCurrent();
//		return motor instanceof CANJaguar ? ((CANJaguar) motor).getOutputCurrent() : ((CANTalon) motor).getOutputCurrent();
	}

	public CANSpeedController.ControlMode getJaguarControlMode ()
	{
		if (_CANmotor != null && _CANmotor instanceof CANJaguar)
			return ((CANJaguar) motor).getControlMode();
		else
			return null;
	}

	public double getSpeed ()
	{
		if (_CANmotor == null){
			return motor.get();
		}else
			return _CANmotor.getSpeed() / _invert / _scale;
		//return motor instanceof CANJaguar ? ((CANJaguar) motor).getSpeed() / _invert / _scale : ((CANTalon) motor).getSpeed() / _invert / _scale;
	}

	public void setPosition(double position)
	{
		if (_CANmotor != null && _CANmotor instanceof CANTalon)
			((CANTalon)_CANmotor).setPosition(position) ;
				
	}
	
	public double getPosition ()
	{
		if (_CANmotor == null){
			return 0;
		}
		else
			return _CANmotor.getPosition() / _invert / _scale;
		//return motor instanceof CANJaguar ? ((CANJaguar) motor).getPosition() / _invert / _scale : ((CANTalon) motor).getPosition() / _invert / _scale;
	}

	public double getEncPosition ()
	{
		if (motor instanceof CANTalon){
			return ((CANTalon) motor).getEncPosition();			
		}
		else
			return 0;
	}

	public void reverseSensor (boolean invert)
	{
		if (motor instanceof CANTalon)
			((CANTalon) motor).reverseSensor(invert);
	}

	public void selfTest ()
	{
		//		((CANTalon) motor).
	}

	public void updateStatus()
	{
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
