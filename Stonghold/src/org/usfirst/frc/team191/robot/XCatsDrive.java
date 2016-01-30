package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XCatsDrive {
	//we need to keep an array of the motors required for the drive
	private XCatsSpeedContoller  _motors[];
	
	public final int _frontLeft = 0, _rearLeft = 1, _frontRight = 2, _rearRight = 3;
	
	//the reduction factor allows us to scale the speed of the drive
	private double _reductionFactor = Enums.SPEED_REDUCTION_FACTOR;
	
	//we can control whether we can support mechanum movement with this
	private boolean _useMechanumWheels = Enums.HAS_MECHANUM_WHEELS;
	
	public XCatsDrive (int channels[], boolean useCAN)
	{
		this._motors = new XCatsSpeedContoller[4];
		
		for (int i = 0; i < 4; i++)
		{
			this._motors[i] = new XCatsSpeedContoller(channels[i], useCAN, Enums.IS_FINAL_ROBOT,null,null);
			this._motors[i].setDashboardIO(Enums.DASHBOARD_INPUT, Enums.DASHBOARD_OUTPUT, "motor" + i);
		}
		
		_motors[_frontLeft].setInverted(true);
		_motors[_rearLeft].setInverted(true);
	}
	
	public XCatsDrive (int channels[], boolean speedMode, int codesPerRev, double p, double i, double d)
	{
		this._motors = new XCatsSpeedContoller[4];
		
		for (int j = 0; j < 4; j++)
		{
			this._motors[j] = new XCatsSpeedContoller(channels[j], speedMode, Enums.IS_FINAL_ROBOT, codesPerRev, p, i, d,null,null);
			this._motors[j].setDashboardIO(true, true, "motor");
		}
		
		_motors[_frontLeft].setInverted(true);
		_motors[_rearLeft].setInverted(true);
	}
	
	public void set (Joystick drive_js)
	{
		
//		set(drive_js.getRawAxis(0), drive_js.getRawAxis(1), drive_js.getRawAxis(4), drive_js.getRawAxis(5));
		set(_reductionFactor*drive_js.getRawAxis(0), _reductionFactor*drive_js.getRawAxis(1), _reductionFactor*drive_js.getRawAxis(4), _reductionFactor*drive_js.getRawAxis(5));
	}
	
	public void set_reductionFactor (double _reductionFactor)
	{
		this._reductionFactor = _reductionFactor;
	}
	
	public double get_reductionFactor ()
	{
		return _reductionFactor;
	}
	
	public void set (Joystick left_js, Joystick right_js)
	{
		set(left_js.getX(), left_js.getY(), right_js.getX(), right_js.getY());
	}
	
	public void set (double left_x, double left_y, double right_x, double right_y)
	{
		if (_useMechanumWheels){
			_motors[_frontLeft].set(left_y - left_x);
			_motors[_rearLeft].set(left_y + left_x);
			_motors[_frontRight].set(right_y + right_x);
			_motors[_rearRight].set(right_y - right_x);			
		}
		else{
			_motors[_frontLeft].set(left_y);
			_motors[_rearLeft].set(left_x);
			_motors[_frontRight].set(right_y );
			_motors[_rearRight].set(right_x);						
		}
	}

	public void set (double leftSpeed, double rightSpeed)
	{
		_motors[_frontLeft].set(leftSpeed);
		_motors[_rearLeft].set(leftSpeed);
		_motors[_frontRight].set(rightSpeed);
		_motors[_rearRight].set(rightSpeed);
	}

	public void set (int motor, double speed)
	{
		_motors[motor].set(speed);
	}
	
	public double get (int motor)
	{
		return _motors[motor].getSetPoint();
	}
	
	public void updateStatus ()
	{
		for (int i = 0; i < 4; i++)
			_motors[i].updateStatus();
	}
}
