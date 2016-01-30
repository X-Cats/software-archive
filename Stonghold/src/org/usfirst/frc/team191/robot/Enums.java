package org.usfirst.frc.team191.robot;

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
	public static final double SPEED_REDUCTION_FACTOR = 0.5;

	//these values keep track specifically of the specific motor controllers
	//if we only need 2 motors in the drive, use FRONT_LEFT and FRONT_RIGHT. Make sure that the arrays below have a length of 2
	public static final int FRONT_LEFT = 0, REAR_LEFT = 1, FRONT_RIGHT = 2, REAR_RIGHT = 3;
	public static final int DRIVE_MOTOR_NUMBERS[] = {FRONT_LEFT, REAR_LEFT, FRONT_RIGHT, REAR_RIGHT}; //if we do not use CAN bus, the motors are created in this sequence
	public static final int CAN_DRIVE_MOTOR_NUMBERS[] = {2, 3, 4, 5}; //these are the CAN bus ids of the motors
	
	
	public static final int AUTO_SWITCH_NUMBERS[] = {9, 8, 7};
	
	public static final int ARM_MOTOR_1 = 9, ARM_MOTOR_2 = 10;
	
	public static final int ELEVATOR_LIFT_MOTOR = 8, ELEVATOR_GRAB_MOTOR = 6;
	public static final boolean ELEVATOR_PID = false;
	public static final int GRABBER_LIMIT_SWITCH = 2, LIFT_TOP_LIMIT_SWITCH = 1,
			LIFT_BOTTOM_LIMIT_SWITCH = 0;
	public static final double LIFT_MAX = -60, FINAL_LIFT_MAX = -120000;
	public static final double ELEVATOR_ROTATIONS_PER_LEVEL = 16, FINAL_ELEVATOR_ROTATIONS_PER_LEVEL = 21000;
	public static final double GRABBER_CURRENT_CUTOFF = 60;
	public static final double MOTOR_STOP_TIME = .1;
	
	public static final int LEFT_DRIVE_JS = 0, RIGHT_DRIVE_JS = 1, DRIVE_JS = 0, OPERATOR_JS = 2;
	public static final boolean TWO_JOYSTICKS = false;
	public static final boolean DASHBOARD_INPUT = false, DASHBOARD_OUTPUT = false;
	
	public static final boolean IS_FINAL_ROBOT = true;
	public static final boolean HAS_MECHANUM_WHEELS = false;
	public static final boolean USE_CAN = true, USE_PID = false;
	public static final int ENCODER_CODES_PER_REVOLUTION = 360;
	public static final int MAX_CAN_SPEED = 6000;
	
	public static final int AUTONOMOUS_TIME = 15;
	
			

}
