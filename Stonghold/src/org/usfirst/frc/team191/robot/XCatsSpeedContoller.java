package org.usfirst.frc.team191.robot;

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
	 * The scale and the units depend on the mode the Jaguar is in.<br>
	 * In percentVbus mode, the outputValue is from -1.0 to 1.0 (same as PWM Jaguar).<br>
	 * In voltage mode, the outputValue is in volts. <br>
	 * In current mode, the outputValue is in amps. <br>
	 * In speed mode, the outputValue is in rotations/minute.<br>
	 * In position mode, the outputValue is in rotations.
	 */
	private double scale;
	private int invert = 1;
	private boolean useRawInput = false;
	private double setPoint = 0;
	private double cutOffSetPointP = 0, cutOffSetPointN = 0; //what the motor should be set to when it hits a limit or something. the first is for positive speeds, the second negative.
	private double p, i, d;
	private int codesPerRev;
	private String name;
	private boolean dashboardInput = false;
	private boolean dashboardOutput = false;
	private double cutOffCurrent = -1;
	private int cutOffDirection = 0;
	private boolean stopping = false;
	private Timer stopTimer;
	private DigitalInput _upperLimit;
	private DigitalInput _lowerLimit;

	//	private motorType type;
	//	public enum motorType
	//	{
	//		pwmJaguar(0), percentJaguar(1), speedJaguar(2), positionJaguar(3), pwmTalon(4), percentTalon(5), speedTalon(6), positionTalon(7);
	//		
	//		int value;
	//		
	//		motorType (int value)
	//		{
	//			this.value = value;
	//		}
	//	};
	//	
	//	public XCatsSpeedContoller (int channel, motorType type)
	//	{
	//		if (type.value % motorType.pwmTalon.value != 0) //if using CAN bus
	//		{
	//			if (type.value > motorType.pwmTalon.value) //if is Talon
	//			{
	//				this.motor = new CANTalon(channel);
	//				((CANTalon) motor).changeControlMode(ControlMode.PercentVbus);
	//				((CANTalon) motor).enableControl();
	//			}
	//			else
	//			{
	//				this.motor = new CANJaguar(channel);
	//				((CANJaguar) motor).setPercentMode();
	//				((CANJaguar) motor).enableControl();
	//			}
	//		}
	//		else
	//			if (type.value == motorType.pwmTalon.value)
	//				this.motor = new Talon(channel);
	//			else
	//				this.motor = new Jaguar(channel);
	//
	//		this.type = type;
	//		scale = 1;	
	//		stopTimer = new Timer();
	//	}
	//	
	//	public XCatsSpeedContoller (int channel, motorType type, int codesPerRev, double p, double i, double d)
	//	{
	//		if (type.value % motorType.pwmTalon.value == 0)
	//			System.out.println("PID constructor called with non PID type. Calling non PID constructor!");
	//		
	//		if (type.value >= motorType.pwmTalon.value)
	//		{
	//			motor = new CANTalon(channel);
	//			
	//			if (type == motorType.speedTalon)
	//			{
	//				((CANTalon) motor).changeControlMode(ControlMode.Speed);
	//				scale = 500;
	//			}
	//			else
	//			{
	//				((CANTalon) motor).changeControlMode(ControlMode.Position);
	//				scale = Enums.ELEVATOR_ROTATIONS_PER_LEVEL;
	//			}
	//			
	//			((CANTalon) motor).setPID(p, i, d);
	//			((CANTalon) motor).enableControl();
	//		}
	//		else
	//		{
	//			motor = new CANJaguar(channel);
	//			if (type == motorType.speedJaguar)
	//			{
	//				((CANJaguar) motor).setSpeedMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
	//				scale = 500;
	//				((CANJaguar) motor).enableControl();
	//			}
	//			else
	//			{
	//				((CANJaguar) motor).setPositionMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
	//				((CANJaguar) motor).enableControl();
	//			}
	//		}
	//
	//		this.codesPerRev = codesPerRev;
	//		this.p = p;
	//		this.i = i;
	//		this.d = d;
	//		this.type = type;
	//		stopTimer = new Timer();
	//	}



	public XCatsSpeedContoller (int channel, boolean useCan, boolean isTalon, DigitalInput lowerLimit, DigitalInput upperLimit)
	{
		_upperLimit = upperLimit;
		_lowerLimit = lowerLimit;
		scale = 1;
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

		stopTimer = new Timer();
	}

	public XCatsSpeedContoller (int channel, boolean speedMode, boolean isTalon, int codesPerRev, double p, double i, double d,DigitalInput lowerLimit,DigitalInput upperLimit)
	{
		this.codesPerRev = codesPerRev;
		this.p = p;
		this.i = i;
		this.d = d;
		scale = 1;

		if (isTalon)
		{
			motor = new CANTalon(channel);

			if (speedMode)
			{
				((CANTalon) motor).changeControlMode(CANTalon.TalonControlMode.Speed);
				scale = 500;
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
				scale = 5000;
			}
			else
			{
				((CANJaguar) motor).setPositionMode(CANJaguar.kQuadEncoder, codesPerRev, p, i, d);
			}
			((CANJaguar) motor).enableControl();
		}

		stopTimer = new Timer();
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
				if (_lowerLimit.get() &&  cutOffDirection * setPoint < 0 )
					return;

			//if the upper limit is not null, then check to see if it is reached and the caller is trying to drive it higher
			if (_upperLimit != null)
				if (_upperLimit.get() && cutOffDirection * setPoint > 0 )
					return;
		 */

		if (cutOffDirection * setPoint <= 0)
		{
			this.setPoint = setPoint;
			cutOffDirection = 0;
		}
		else
			this.setPoint = cutOffDirection > 0 ? cutOffSetPointP : cutOffSetPointN;


			if (useRawInput)
				motor.set(this.setPoint);
			else
				motor.set(this.setPoint * invert * scale);

			if (dashboardOutput)
				SmartDashboard.putNumber(name + "_set_point", this.setPoint);					

			//		}
			//		catch (Exception e) {
			//			System.out.println ("Error! "+e.getStackTrace());
			//			return;
			//		}


	}

	public void stop ()
	{
		set(-setPoint);
		this.stopping = true;
		stopTimer.start();
	}

	public void setPID (double p, double i, double d)
	{
		this.p = p;
		this.i = i;
		this.d = d;

		if (dashboardOutput)
		{
			SmartDashboard.putNumber(name + "_p", p);
			SmartDashboard.putNumber(name + "_i", i);
			SmartDashboard.putNumber(name + "_d", d);
		}

		if (motor instanceof CANJaguar)
			((CANJaguar) motor).setPID(p, i, d);
		else
			((CANTalon) motor).setPID(p, i, d);
	}

	public void setCutOffDirection (int cutOffDirection)
	{
		this.cutOffDirection = cutOffDirection;
	}

	public void setCutOffSetPointP (double setPoint)
	{
		cutOffSetPointP = setPoint;
	}

	public void setCutOffSetPointN (double setPoint)
	{
		cutOffSetPointN = setPoint;
	}

	public void setUseRawInput (boolean useRawInput)
	{
		this.useRawInput = useRawInput;

		if (dashboardOutput)
			SmartDashboard.putBoolean(name + "_raw_input", useRawInput);
	}

	public void setInverted(boolean invert)
	{
		if (invert)
			this.invert = -1;
		else
			this.invert = 1;
	}

	public void setDashboardIO (boolean input, boolean output, String name)
	{
		this.dashboardInput = input;
		this.dashboardOutput = output;
		this.name = name;

		if (dashboardOutput)
		{
			SmartDashboard.putNumber(name + "_set_point", setPoint);
			SmartDashboard.putBoolean(name + "_raw_input", useRawInput);

			if ((motor instanceof CANJaguar && ((CANJaguar) motor).getControlMode() != CANJaguar.JaguarControlMode.PercentVbus)
					|| (motor instanceof CANTalon && ((CANTalon) motor).getControlMode() != CANTalon.TalonControlMode.PercentVbus) )
			{
				SmartDashboard.putNumber(name + "_p", p);
				SmartDashboard.putNumber(name + "_i", i);
				SmartDashboard.putNumber(name + "_d", d);
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
		return setPoint;
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
		return motor instanceof CANJaguar ? ((CANJaguar) motor).getSpeed() / invert / scale : ((CANTalon) motor).getSpeed() / invert / scale;
	}

	public double getPosition ()
	{
		return motor instanceof CANJaguar ? ((CANJaguar) motor).getPosition() / invert / scale : ((CANTalon) motor).getPosition() / invert / scale;
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
		if (stopping && stopTimer.get() > Enums.MOTOR_STOP_TIME)
		{
			set(0);
			stopping = false;
			stopTimer.stop();
			stopTimer.reset();
		}

		if (motor instanceof CANJaguar)
		{			
			if (dashboardInput && ((CANJaguar) motor).getControlMode() != CANJaguar.JaguarControlMode.PercentVbus)
			{
				p = SmartDashboard.getNumber(name + "_p");
				i = SmartDashboard.getNumber(name + "_i");
				d = SmartDashboard.getNumber(name + "_d");

				((CANJaguar) motor).setPID(p, i, d);
			}

			if (dashboardOutput)
			{
				SmartDashboard.putNumber(name + "_current", ((CANJaguar) motor).getOutputCurrent());
				SmartDashboard.putNumber(name + "_speed", ((CANJaguar) motor).getSpeed());
				SmartDashboard.putNumber(name + "_position", ((CANJaguar) motor).getPosition());
				//				SmartDashboard.putNumber(name + "_encoder", ((CANJaguar) motor).);
			}
		}

		if (motor instanceof CANTalon)
		{			
			if (dashboardInput && ((CANTalon) motor).getControlMode() != CANTalon.TalonControlMode.PercentVbus)
			{
				p = SmartDashboard.getNumber(name + "_p");
				i = SmartDashboard.getNumber(name + "_i");
				d = SmartDashboard.getNumber(name + "_d");

				((CANTalon) motor).setPID(p, i, d);
			}

			if (dashboardOutput)
			{
				SmartDashboard.putNumber(name + "_current", ((CANTalon) motor).getOutputCurrent());
				SmartDashboard.putNumber(name + "_speed", ((CANTalon) motor).getSpeed());
				SmartDashboard.putNumber(name + "_position", ((CANTalon) motor).getPosition() / invert / scale);
			}
		}

		if (dashboardInput)
		{
			useRawInput = SmartDashboard.getBoolean(name + "_raw_input");
			setPoint = SmartDashboard.getNumber(name + "_set_point");
		}

		set(setPoint);
	}
}
