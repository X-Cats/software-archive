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
	public static final int PDP_CAN_ID = 20;
	public static final int PCM_CAN_ID = 21;
	public static final double SPEED_REDUCTION_FACTOR = 1.0;
	
	/*
	 * offset to calibrate so that at 0.5 on the right side, the left side is going at the same travel
	 * right = right + speed_calibration * right;
	 * so if the right side is going 5% farther than the left when the speeds are the same
	 * the right = right + -0.05 * right
	*/
	public static final double SPEED_CALIBRATION = 0.0;  
	
	public static final boolean IS_FINAL_ROBOT = false;
	public static final String  DRIVE_CONTROLLER_TYPE = "Talon"; // choices are "Jaguar" or "Talon"
	public static final boolean HAS_MECHANUM_WHEELS = false;
	public static final boolean USE_PID = false;    	// This is if the drive train is using PID control
	public static final int     MAX_CAN_SPEED = 6000;
	public static final boolean USE_CAN = true;			// if the motors are wired with CAN bus use this
	public static final double  MOTOR_STOP_TIME = .1;	//when using coast mode this is how to stop gradually	
	public static final boolean USE_NAVX = true;		//when using NAVX set this to true;
	public static final boolean USE_COMPRESSOR = true;  //set to true when using a compressor
	public static final boolean USE_SOFTWARE_SPEED_REDUCTION = false; 	//set to true only if you wish to use the trigger button to engage a sofware reduction of speed low/high
	public static final boolean USE_2SC_TANK = true;     //when true, then the robot drive is 2 motor controllers and the rest are followers
	public static final double BROWNOUT_VOLTAGE_THRESHOLD = 7.5;
	public static final double BROWNOUT_VOLTAGE_REDUCTIONFACTOR = 0.5;
	
	public static final double  ROBOT_LENGTH_COMPACT = 30.0; //length of robot with shooter in home position
	public static final double  ROBOT_LENGTH_EXTENDED = 40; // length of robot with shooter down
	//vision constants
	public static final int PIXEL_PER_DEGREE = 14; 
	public static final int CAMERA_X_PIXELS_TOTAL = 640;
	public static final int CAMERA_Y_PIXELS_TOTAL = 480;
	public static final double PEG_LENGTH = 10.5;  // inches
	public static final double PEG_CHANNEL_DEPTH = 3.375; // inches
	public static final double CAMERA_DIST_FROM_FRONT = 5.0;  // inches

	//these values keep track specifically of the specific motor controllers
	//if we only need 2 motors in the drive, use FRONT_LEFT and FRONT_RIGHT. Make sure that the arrays below have a length of 2
	public static final int  REAR_LEFT = 0,  FRONT_LEFT = 1, AUX_LEFT = 2, REAR_RIGHT = 3, FRONT_RIGHT = 4,  AUX_RIGHT = 5 ;
//	public static final int DRIVE_MOTOR_NUMBERS[] = {FRONT_LEFT, FRONT_RIGHT}; //if we do not use CAN bus, the motors are created in this sequence
	public static final int     DRIVE_MOTOR_NUMBERS[] = { REAR_LEFT, FRONT_LEFT, AUX_LEFT,  REAR_RIGHT, FRONT_RIGHT, AUX_RIGHT}; //if we do not use CAN bus, the motors are created in this sequence
	public static final int     CAN_DRIVE_MOTOR_NUMBERS[] = {1, 2, 3, 4, 5, 6}; //these are the CAN bus ids of the motors
		
	//This is for the use of the compressor
	public static final int     PCM_SHIFTER_FORWARD = 4, PCM_SHIFTER_REVERSE=5;  //this is used to shift the gear ration on the drive train from low to high (SHIFTER)
	public static final double  SHIFTER_DELAY_TIME = 0.25;  // this time is used for the "slack speed" inbetween gear shifts
	public static final double  SHIFTER_DELAY_SPEED = 0.4;  // this is the speed for the "slack" during shifting
		
	//Joysticks
	public static final int LEFT_DRIVE_JS = 1, RIGHT_DRIVE_JS = 2, DRIVE_JS = 1, OPERATOR_JS = 0;
	public static final boolean TWO_JOYSTICKS = true;
	public static final boolean DASHBOARD_INPUT = false, DASHBOARD_OUTPUT = false;
	
	//Winch
	public static final int WINCH_CAN_ID =11; //CAN ID of the winch
	public static final double WINCH_CLIMB_SPEED = 1.0; // speed to climb
	public static final int WINCH_LIMIT_SWITCH = 3;// limit switch for the winch
	
	//Gear Ejector
	public static final int GEAR_ROTATOR_PWM_ID = 7;
	public static final int GEAR_PCM_FORWARD = 7; // PCM id for the gear ejector
	public static final int GEAR_PCM_REVERSE = 6; // PCM id for the gear ejector
	public static final double GEAR_EJECT_TIME = 1; // time in seconds before retracting ejector piston
	public static final double GEAR_EJECT_REVERSE_START_TIME = 0.5; // Start reversing this long after the piston deploys
	public static final double GEAR_EJECT_REVERSE_TIME = 2; // time to reverse
	public static final double GEAR_EJECT_REVERSE_SPEED = 0.4; //speed to reverse with when ejecting
	public static final int GEAR_POSITIONED_OPT = 2; //optical sensor that detects if gear is position(rotated) correctly
	public static final int GEAR_ONBOARD_OPT = 2;    //optical sensor that detects if the gear is positioned far enough in the robot to eject
	public static final double GEAR_ROTATOR_SPEED = 0.25;
	public static final int GEAR_LS_CHANNEL=0; //Left limit switch
    public static final int GEAR_RS_CHANNEL=1; //Right limit switch
	
	//Feeder
	public static final int FEEDER_TOP_CAN = 9;
	public static final int FEEDER_BOTTOM_CAN = 10;
	public static final double FEEDER_INTAKE_SPEED = 0.5; //speed at which balls are sucked in
	public static final double FEEDER_FEED_SPEED = 0.5;  //speed at which balls are spit out
	public static final int FEEDER_LIFT_PCM_FORWARD = 2;
	public static final int FEEDER_LIFT_PCM_BACKWARD = 3;
	
	//Autonomous uses the chooser object to select mode
	public static final int AUTONOMOUS_TIME = 15;
	
	
	
}
