package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XCatsDrive {
	//we need to keep an array of the motors required for the drive. This will work for a 2 or 4 drive system
	private XCatsSpeedController  _motors[];
	
	
	//the reduction factor allows us to scale the speed of the drive
	private double _reductionFactor = Enums.SPEED_REDUCTION_FACTOR;
	
	//we can control whether we can support mechanum movement with this
	private boolean _useMechanumWheels = Enums.HAS_MECHANUM_WHEELS;
	
	public XCatsDrive (boolean useCAN, boolean isTalon)
	{
		int channels[];
		
		channels = new int[Enums.DRIVE_MOTOR_NUMBERS.length];
		if (useCAN){
			channels = Enums.CAN_DRIVE_MOTOR_NUMBERS;
		}
		else{
			channels =Enums.DRIVE_MOTOR_NUMBERS;		
		}
		
		//We will assume that if the Enums.DRIVE_MOTOR_NUMBERS array has length 4 then there are 4 motors, otherwise there are 2 (front left and front right)
		this._motors = new XCatsSpeedController[Enums.DRIVE_MOTOR_NUMBERS.length];

		this._motors[Enums.FRONT_LEFT] = new XCatsSpeedController("motor"+Enums.FRONT_LEFT, channels[Enums.FRONT_LEFT], useCAN, isTalon,null,null);
		this._motors[Enums.FRONT_RIGHT] = new XCatsSpeedController("motor"+Enums.FRONT_RIGHT,channels[Enums.FRONT_RIGHT], useCAN, isTalon,null,null);
		_motors[Enums.FRONT_LEFT].setInverted(true);
		
		if (Enums.DRIVE_MOTOR_NUMBERS.length > 2){
			this._motors[Enums.REAR_LEFT] = new XCatsSpeedController("motor"+Enums.REAR_LEFT,channels[Enums.REAR_LEFT], useCAN, isTalon,null,null);
			this._motors[Enums.REAR_RIGHT] = new XCatsSpeedController("motor"+Enums.REAR_RIGHT,channels[Enums.REAR_RIGHT], useCAN, isTalon,null,null);			
			_motors[Enums.REAR_LEFT].setInverted(true);
		}		
	}
	

	public XCatsDrive (int channels[], boolean speedMode, boolean isTalon, int codesPerRev, double p, double i, double d)
	{
		this._motors = new XCatsSpeedController[Enums.DRIVE_MOTOR_NUMBERS.length];
		
		this._motors[Enums.FRONT_LEFT] = new XCatsSpeedController("motor",  channels[Enums.FRONT_LEFT], speedMode, isTalon, codesPerRev, p, i, d,null,null);
		this._motors[Enums.FRONT_RIGHT] = new XCatsSpeedController("motor", channels[Enums.FRONT_RIGHT], speedMode, isTalon, codesPerRev, p, i, d,null,null);
		_motors[Enums.FRONT_LEFT].setInverted(true);

		if (Enums.DRIVE_MOTOR_NUMBERS.length > 2){
			this._motors[Enums.REAR_LEFT] = new XCatsSpeedController("motor", channels[Enums.REAR_LEFT], speedMode, isTalon, codesPerRev, p, i, d,null,null);
			this._motors[Enums.REAR_RIGHT] = new XCatsSpeedController("motor", channels[Enums.REAR_RIGHT], speedMode, isTalon, codesPerRev, p, i, d,null,null);
			_motors[Enums.REAR_LEFT].setInverted(true);
		}
	}
	
	public void set (Joystick drive_js)
	{
		
//		set(drive_js.getRawAxis(0), drive_js.getRawAxis(1), drive_js.getRawAxis(4), drive_js.getRawAxis(5));
//		set(_reductionFactor*drive_js.getRawAxis(0), _reductionFactor*drive_js.getRawAxis(1), _reductionFactor*drive_js.getRawAxis(4), _reductionFactor*drive_js.getRawAxis(5));
		
		set(_reductionFactor*drive_js.getRawAxis(1),_reductionFactor*drive_js.getRawAxis(1),_reductionFactor*drive_js.getRawAxis(5),_reductionFactor*drive_js.getRawAxis(5));
	}
	
	public void setReductionFactor (double reductionFactor)
	{
		this._reductionFactor = reductionFactor;
	}
	
	public double getReductionFactor ()
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
			_motors[Enums.FRONT_LEFT].set(left_y - left_x);
			_motors[Enums.FRONT_RIGHT].set(right_y + right_x);
			if (_motors.length > 2) {
				_motors[Enums.REAR_LEFT].set(left_y + left_x);
				_motors[Enums.REAR_RIGHT].set(right_y - right_x);							
			}
		}
		else{
			_motors[Enums.FRONT_LEFT].set(left_y);
			_motors[Enums.FRONT_RIGHT].set(right_y );

			if (_motors.length > 2) {
				_motors[Enums.REAR_LEFT].set(left_x);
				_motors[Enums.REAR_RIGHT].set(right_x);			
			}
		}
	}

	public void set (double leftSpeed, double rightSpeed)
	{
		_motors[Enums.FRONT_LEFT].set(leftSpeed);
		_motors[Enums.FRONT_RIGHT].set(rightSpeed);

		if (_motors.length > 2){
			_motors[Enums.REAR_LEFT].set(leftSpeed);
			_motors[Enums.REAR_RIGHT].set(rightSpeed);
		}
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
		_motors[Enums.FRONT_LEFT].updateStatus();
		_motors[Enums.FRONT_RIGHT].updateStatus();
		if (_motors.length > 2) {
			_motors[Enums.REAR_LEFT].updateStatus();
			_motors[Enums.REAR_RIGHT].updateStatus();			
		}
		
	}
}
