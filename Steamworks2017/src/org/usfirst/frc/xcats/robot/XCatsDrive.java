package org.usfirst.frc.xcats.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class XCatsDrive {
	//we need to keep an array of the motors required for the drive. This will work for a 2 or 4 drive system
	private XCatsSpeedController  _motors[];
	
	
	//the reduction factor allows us to scale the speed of the drive
	private double _reductionFactor = Enums.SPEED_REDUCTION_FACTOR;
	
	//we can control whether we can support mechanum movement with this
	private boolean _useMechanumWheels = Enums.HAS_MECHANUM_WHEELS;
	
	private PowerDistributionPanel _pdp;
	private double _pdpVoltageThreshold;
	private double _pdpVoltageReductionFactor = 0; // setpoint = setpoint - _pdpVoltageReductionFactor* setpoint;

	
	public XCatsDrive (boolean useCAN, boolean isTalon)
	{
		int channels[];
		XCatsSpeedController.SCType sctype;
		sctype = XCatsSpeedController.SCType.TALON;
		
		channels = new int[Enums.DRIVE_MOTOR_NUMBERS.length];
		if (useCAN){
			channels = Enums.CAN_DRIVE_MOTOR_NUMBERS;
		}
		else{
			channels =Enums.DRIVE_MOTOR_NUMBERS;		
		}
		
		
		//We will assume that if the Enums.DRIVE_MOTOR_NUMBERS array has length 4 then there are 4 motors, otherwise there are 2 (front left and front right)
		
		this._motors = new XCatsSpeedController[Enums.DRIVE_MOTOR_NUMBERS.length];

		this._motors[Enums.FRONT_LEFT] = new XCatsSpeedController("motor"+Enums.FRONT_LEFT, channels[Enums.FRONT_LEFT], useCAN, sctype, null,null);
		this._motors[Enums.FRONT_RIGHT] = new XCatsSpeedController("motor"+Enums.FRONT_RIGHT,channels[Enums.FRONT_RIGHT], useCAN, sctype, null,null);
		_motors[Enums.FRONT_LEFT].setInverted(true);
		
		if (Enums.DRIVE_MOTOR_NUMBERS.length > 2){
			this._motors[Enums.REAR_LEFT] = new XCatsSpeedController("motor"+Enums.REAR_LEFT,channels[Enums.REAR_LEFT], useCAN, sctype, null,null);
			this._motors[Enums.REAR_RIGHT] = new XCatsSpeedController("motor"+Enums.REAR_RIGHT,channels[Enums.REAR_RIGHT], useCAN, sctype, null,null);			
			_motors[Enums.REAR_LEFT].setInverted(true);
			
			if (Enums.USE_2SC_TANK){
				this._motors[Enums.REAR_LEFT].setFollower(Enums.CAN_DRIVE_MOTOR_NUMBERS[Enums.FRONT_LEFT]);
				this._motors[Enums.REAR_RIGHT].setFollower(Enums.CAN_DRIVE_MOTOR_NUMBERS[Enums.FRONT_RIGHT]);				
			}
		}		
		
		if (Enums.DRIVE_MOTOR_NUMBERS.length > 4){
			this._motors[Enums.AUX_LEFT] = new XCatsSpeedController("motor"+Enums.AUX_LEFT,channels[Enums.AUX_LEFT], useCAN, sctype, null,null);
			this._motors[Enums.AUX_RIGHT] = new XCatsSpeedController("motor"+Enums.AUX_RIGHT,channels[Enums.AUX_RIGHT], useCAN, sctype, null,null);
			_motors[Enums.AUX_LEFT].setInverted(true);

			if (Enums.USE_2SC_TANK){			
				this._motors[Enums.AUX_LEFT].setFollower(Enums.CAN_DRIVE_MOTOR_NUMBERS[Enums.FRONT_LEFT]);
				this._motors[Enums.AUX_RIGHT].setFollower(Enums.CAN_DRIVE_MOTOR_NUMBERS[Enums.FRONT_RIGHT]);
			}
		}				
	}
	

	public XCatsDrive (int channels[], boolean speedMode, boolean isTalon, int codesPerRev, double p, double i, double d)
	{
		XCatsSpeedController.SCType sctype;
		
		sctype = XCatsSpeedController.SCType.TALON;
		
		this._motors = new XCatsSpeedController[Enums.DRIVE_MOTOR_NUMBERS.length];
		
		this._motors[Enums.FRONT_LEFT] = new XCatsSpeedController("motor",  channels[Enums.FRONT_LEFT], sctype, speedMode, codesPerRev,1, p, i, d,null,null,null);
		this._motors[Enums.FRONT_RIGHT] = new XCatsSpeedController("motor", channels[Enums.FRONT_RIGHT], sctype,  speedMode, codesPerRev,1, p, i, d,null,null,null);
		_motors[Enums.FRONT_LEFT].setInverted(true);

		if (Enums.DRIVE_MOTOR_NUMBERS.length > 2){
			this._motors[Enums.REAR_LEFT] = new XCatsSpeedController("motor", channels[Enums.REAR_LEFT], sctype, speedMode, codesPerRev,1, p, i, d,null,null,null);
			this._motors[Enums.REAR_RIGHT] = new XCatsSpeedController("motor", channels[Enums.REAR_RIGHT], sctype, speedMode, codesPerRev,1,  p, i, d,null,null,null);
			_motors[Enums.REAR_LEFT].setInverted(true);
		}
	}
	
	public void setPDP(PowerDistributionPanel pdp, double voltageThreshold, double reductionFactor){
		_pdp = pdp;
		this._pdpVoltageThreshold = voltageThreshold;
		this._pdpVoltageReductionFactor=reductionFactor;
		
		for (int i=0; i<_motors.length; i++){
			_motors[i].setPDP(pdp,voltageThreshold,reductionFactor);
			
		}				
	}	
	public void set (Joystick drive_js)
	{
		
//		set(drive_js.getRawAxis(0), drive_js.getRawAxis(1), drive_js.getRawAxis(4), drive_js.getRawAxis(5));
//		set(_reductionFactor*drive_js.getRawAxis(0), _reductionFactor*drive_js.getRawAxis(1), _reductionFactor*drive_js.getRawAxis(4), _reductionFactor*drive_js.getRawAxis(5));
		
		set(_reductionFactor*drive_js.getRawAxis(1),_reductionFactor*drive_js.getRawAxis(1),_reductionFactor*drive_js.getRawAxis(5),_reductionFactor*drive_js.getRawAxis(5));
	}
	
	public void setInverted(){
		
		for (int i = 0; i < Enums.DRIVE_MOTOR_NUMBERS.length; i ++){
			_motors[i].setInverted(_motors[i].getInverted());
		}
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
		try {
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

				if (_motors.length > 2 && !Enums.USE_2SC_TANK) {
					_motors[Enums.REAR_LEFT].set(left_y);
					_motors[Enums.REAR_RIGHT].set(right_y);			
				}
				if (_motors.length > 4 && !Enums.USE_2SC_TANK) {
					_motors[Enums.AUX_LEFT].set(left_y);
					_motors[Enums.AUX_RIGHT].set(right_y);			
				}
			}			
		}
		catch (Exception e){
			System.out.println(e);
			e.printStackTrace();		}
	}

	public void set (double leftSpeed, double rightSpeed)
	{
		_motors[Enums.FRONT_LEFT].set(leftSpeed);
		_motors[Enums.FRONT_RIGHT].set(rightSpeed);

		if (_motors.length > 2 && !Enums.USE_2SC_TANK){
			_motors[Enums.REAR_LEFT].set(leftSpeed);
			_motors[Enums.REAR_RIGHT].set(rightSpeed);
		}
		if (_motors.length > 4 && !Enums.USE_2SC_TANK){
			_motors[Enums.AUX_LEFT].set(leftSpeed);
			_motors[Enums.AUX_RIGHT].set(rightSpeed);
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
	
	public void setDashboardIO(boolean input, boolean output){
		
		for (int i=0; i<_motors.length; i++){
			_motors[i].setDashboardIO(input, output);
			
		}		
	}
	public void updateStatus ()
	{
		for (int i=0; i<_motors.length; i++){
			_motors[i].updateStatus();
			
		}						
	}
}
