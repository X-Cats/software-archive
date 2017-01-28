package org.usfirst.frc.xcats.robot;


public class Enums {

	/*
	public static final int FRONT_LEFT_DRIVE = 0,
			REAR_LEFT_DRIVE = 1,
			FRONT_RIGHT_DRIVE = 2,
			REAR_RIGHT_DRIVE = 3;
	public static final int CAN_FRONT_LEFT_DRIVE = 1,
			CAN_REAR_LEFT_DRIVE = 2,
			CAN_FRONT_RIGHT_DRIVE = 3,
			CAN_REAR_RIGHT_DRIVE = 4;
			*/
	
	 //front left, rear left, front right, rear right
	public static final double SPEED_REDUCTION_FACTOR = 0.7;
	
	/*
	 * offset to calibrate so that at 0.5 on the right side, the left side is going at the same travel
	 * right = right + speed_calibration * right;
	 * so if the right side is going 5% farther than the left when the speeds are the same
	 * the right = right + -0.05 * right
	*/
	public static final double SPEED_CALIBRATION = 0.0;  
	
	public static final boolean IS_FINAL_ROBOT = true;
	public static final String DRIVE_CONTROLLER_TYPE = "Talon"; // choices are "Jaguar" or "Talon"
	public static final boolean HAS_MECHANUM_WHEELS = false;
	public static final boolean USE_PID = false;    	// This is if the drive train is using PID control
	public static final int MAX_CAN_SPEED = 6000;
	public static final boolean USE_CAN = true;			// if the motors are wired with CAN bus use this
	public static final double MOTOR_STOP_TIME = .1;	//when using coast mode this is how to stop gradually	
	
	public static final double ROBOT_LENGTH_COMPACT = 30.0; //length of robot with shooter in home position
	public static final double ROBOT_LENGTH_EXTENDED = 40; // length of robot with shooter down

	//these values keep track specifically of the specific motor controllers
	//if we only need 2 motors in the drive, use FRONT_LEFT and FRONT_RIGHT. Make sure that the arrays below have a length of 2
	public static final int FRONT_LEFT = 0, REAR_LEFT = 1, FRONT_RIGHT = 2, REAR_RIGHT = 3, AUX_LEFT = 4, AUX_RIGHT =5 ;
//	public static final int DRIVE_MOTOR_NUMBERS[] = {FRONT_LEFT, FRONT_RIGHT}; //if we do not use CAN bus, the motors are created in this sequence
	public static final int DRIVE_MOTOR_NUMBERS[] = {FRONT_LEFT, REAR_LEFT, FRONT_RIGHT, REAR_RIGHT}; //if we do not use CAN bus, the motors are created in this sequence
	public static final int CAN_DRIVE_MOTOR_NUMBERS[] = {1, 2, 3, 4}; //these are the CAN bus ids of the motors
		
	//this assembly rotates up and down and in and out
	public static final int ACQ_LIFT_MOTOR = 7;
	public static final double ACQ_LIFT_SPEED = 0.40;
	public static final int ACQ_MOTOR_GRAB = 6;	//this motor controls the green acquisition into the robot 
	public static final int ACQ_MOTOR_SHOOT = 5;    //this motor controls the dispense of the ball into the shooter
	public static final int ACQ_MOTOR_SHOOT_PWM = 9;    //this motor controls the dispense of the ball into the shooter
	public static final double ACQ_SPEED = 1;
	
	//This assembly controls the shooter wheels
	public static final int SHOOTER_MOTOR_MASTER = 8, SHOOTER_MOTOR_FOLLOWER = 9;
	public static final double SHOOT_TIME = 2.0;
	public static final int SHOOTER_OPTICAL_DI = 1;
	
	//Joysticks
	public static final int LEFT_DRIVE_JS = 1, RIGHT_DRIVE_JS = 2, DRIVE_JS = 1, OPERATOR_JS = 0;
	public static final boolean TWO_JOYSTICKS = false;
	public static final boolean DASHBOARD_INPUT = false, DASHBOARD_OUTPUT = false;
	
	//Autonomous uses the chooser object to select mode
	public static final int AUTONOMOUS_TIME = 15;
	
/*	old stuff from last year		
	public static final int GRABBER_LIMIT_SWITCH = 2, LIFT_TOP_LIMIT_SWITCH = 1,
			LIFT_BOTTOM_LIMIT_SWITCH = 0;
	public static final double LIFT_MAX = -60, FINAL_LIFT_MAX = -120000;
	public static final double ELEVATOR_ROTATIONS_PER_LEVEL = 16, FINAL_ELEVATOR_ROTATIONS_PER_LEVEL = 21000;
	public static final double GRABBER_CURRENT_CUTOFF = 60;

*/
	
	
}
