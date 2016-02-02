package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.hal.CanTalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class XCatsSpeedContoller
{
	private SpeedController motor;
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
	public XCatsSpeedContoller (String name, int channel, boolean useCan, boolean isTalon, DigitalInput lowerLimit, DigitalInput upperLimit)
	{
		_upperLimit = upperLimit;
		_lowerLimit = lowerLimit;
		_name = name;
		_scale = 1;
		if (useCan)
		{
			if (isTalon)
			{
				this.motor = new CANTalon(channel);
				((CANTalon) motor).changeControlMode(CANTalon.TalonControlMode.PercentVbus);
				((CANTalon) motor).enableControl();
			}
			else
			{
				this.motor = new CANJaguar(channel);
				((CANJaguar) motor).setPercentMode();
				((CANJaguar) motor).enableControl();
			}
		}
		else
			this.motor = new Jaguar(channel);

		_stopTimer = new Timer();
		
		this.setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT);
	}

	//this constructor is only used when we want to try using PID control
	public XCatsSpeedContoller (String name, int channel, boolean speedMode, boolean isTalon, int codesPerRev, double p, double i, double d,DigitalInput lowerLimit,DigitalInput upperLimit)
	{
		this._codesPerRev = codesPerRev;
		this._p = p;
		this._i = i;
		this._d = d;
		_name = name;
		_scale = 1;

		if (isTalon)
		{
			motor = new CANTalon(channel);

			if (speedMode)
			{
				((CANTalon) motor).changeControlMode(CANTalon.TalonControlMode.Speed);
				_scale = 500;
			}
			else
			{
				((CANTalon) motor).changeControlMode(CANTalon.TalonControlMode.Position);
			}

			((CANTalon) motor).setPID(p, i, d);
			((CANTalon) motor).enableControl();
		}
		else
		{
			motor = new CANJaguar(channel);
			if (speedMode)
			{
				((CANJaguar) motor).setSpeedMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
				_scale = 5000;
			}
			else
			{
				((CANJaguar) motor).setPositionMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
			}
			((CANJaguar) motor).enableControl();
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

	public void setPID (double p, double i, double d)
	{
		this._p = p;
		this._i = i;
		this._d = d;

		if (_dashboardOutput)
		{
			SmartDashboard.putNumber(_name + "_p", p);
			SmartDashboard.putNumber(_name + "_i", i);
			SmartDashboard.putNumber(_name + "_d", d);
		}

		if (motor instanceof CANJaguar)
			((CANJaguar) motor).setPID(p, i, d);
		else
			((CANTalon) motor).setPID(p, i, d);
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

	private void setDashboardIO (boolean input, boolean output)
	{
		this._dashboardInput = input;
		this._dashboardOutput = output;
		

		if (_dashboardOutput)
		{
			SmartDashboard.putNumber(_name + "_set_point", _setPoint);
			SmartDashboard.putBoolean(_name + "_raw_input", _useRawInput);

			if ((motor instanceof CANJaguar && ((CANJaguar) motor).getControlMode() != CANJaguar.JaguarControlMode.PercentVbus)
					|| (motor instanceof CANTalon && ((CANTalon) motor).getControlMode() != CANTalon.TalonControlMode.PercentVbus) )
			{
				SmartDashboard.putNumber(_name + "_p", _p);
				SmartDashboard.putNumber(_name + "_i", _i);
				SmartDashboard.putNumber(_name + "_d", _d);
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
		return ((CANTalon) motor).getControlMode();
	}

	public void zeroEncoder ()
	{
		if (motor instanceof CANJaguar)
			((CANJaguar) motor).enableControl(0);
		else
			((CANTalon) motor).setPosition(0);
	}

	public double getSetPoint ()
	{
		return _setPoint;
	}

	public double getCurrent ()
	{
		return motor instanceof CANJaguar ? ((CANJaguar) motor).getOutputCurrent() : ((CANTalon) motor).getOutputCurrent();
	}

	public CANSpeedController.ControlMode getJaguarControlMode ()
	{
		return ((CANJaguar) motor).getControlMode();
	}

	public double getSpeed ()
	{
		return motor instanceof CANJaguar ? ((CANJaguar) motor).getSpeed() / _invert / _scale : ((CANTalon) motor).getSpeed() / _invert / _scale;
	}

	public double getPosition ()
	{
		return motor instanceof CANJaguar ? ((CANJaguar) motor).getPosition() / _invert / _scale : ((CANTalon) motor).getPosition() / _invert / _scale;
	}

	public double getEncPosition ()
	{
		return ((CANTalon) motor).getEncPosition();
	}

	public void reverseSensor (boolean invert)
	{
		((CANTalon) motor).reverseSensor(invert);
	}

	public void selfTest ()
	{
		//		((CANTalon) motor).
	}

	public void updateStatus()
	{
		if (_stopping && _stopTimer.get() > Enums.MOTOR_STOP_TIME)
		{
			set(0);
			_stopping = false;
			_stopTimer.stop();
			_stopTimer.reset();
		}

		if (motor instanceof CANJaguar)
		{			
			if (_dashboardInput && ((CANJaguar) motor).getControlMode() != CANJaguar.JaguarControlMode.PercentVbus)
			{
				_p = SmartDashboard.getNumber(_name + "_p");
				_i = SmartDashboard.getNumber(_name + "_i");
				_d = SmartDashboard.getNumber(_name + "_d");

				((CANJaguar) motor).setPID(_p, _i, _d);
			}

			if (_dashboardOutput)
			{
				SmartDashboard.putNumber(_name + "_current", ((CANJaguar) motor).getOutputCurrent());
				SmartDashboard.putNumber(_name + "_speed", ((CANJaguar) motor).getSpeed());
				SmartDashboard.putNumber(_name + "_position", ((CANJaguar) motor).getPosition());
				//				SmartDashboard.putNumber(_name + "_encoder", ((CANJaguar) motor).);
			}
		}

		if (motor instanceof CANTalon)
		{			
			if (_dashboardInput && ((CANTalon) motor).getControlMode() != CANTalon.TalonControlMode.PercentVbus)
			{
				_p = SmartDashboard.getNumber(_name + "_p");
				_i = SmartDashboard.getNumber(_name + "_i");
				_d = SmartDashboard.getNumber(_name + "_d");

				((CANTalon) motor).setPID(_p, _i, _d);
			}

			if (_dashboardOutput)
			{
				SmartDashboard.putNumber(_name + "_current", ((CANTalon) motor).getOutputCurrent());
				SmartDashboard.putNumber(_name + "_speed", ((CANTalon) motor).getSpeed());
				SmartDashboard.putNumber(_name + "_position", ((CANTalon) motor).getPosition() / _invert / _scale);
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
