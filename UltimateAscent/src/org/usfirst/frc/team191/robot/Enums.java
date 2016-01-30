/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author mary farmer
 */
public class Enums {
    public static final boolean IS_FINAL_ROBOT = true;
    public static final boolean DEBUG_CAMERA_IMAGES = true;
    public static final boolean SWAP_BACK_TO_FRONT = true;
    public static final boolean CAMERA_ACTIVE = true;
    public static final boolean SHOOTER_ACTIVE = false;
    public static final boolean MECANUM_DRIVE_ACTIVE = true;    
    public static final boolean KINECT_ENABLED = false;
    public static final boolean RETARGET_ACTIVE = true;
    public static final boolean ENHANCED_OPERATORCONTROL_ACTIVE = false;
    public static final boolean SPEED_CONTROLLER_ACTIVE = false;
    
    public static final boolean USE_PID_CONTROLLER = false;


    //Enhanced IO
    public static final double ENH_IO_ANALOG_MAX = 3.3;
    public static final double ENH_IO_SPEED_MAX = 0.7;//100% top of the slider
    public static final double ENH_IO_SPEED_MIN = 0.5; //50% bottom of the slider

    //PWMs
    public static final int LEFT_FRONT_DRIVE = 4;
    public static final int LEFT_REAR_DRIVE = 3;
    public static final int RIGHT_FRONT_DRIVE = 2;
    public static final int RIGHT_REAR_DRIVE = 1;
    
    // ------------------JOYSTICKS -----------------------------
    public static final int LEFT_JOYSTICK_PORT = 1;
    public static final int RIGHT_JOYSTICK_PORT = 2;
    public static final int OPCON_JOYSTICK_PORT = 3;
    public static final int LEFT_KINECT_STICK = 1;
    public static final int RIGHT_KINECT_STICK = 2;

    //-------------------ENCODERS ---------------------------------

    public static final int LEFT_FRONT_ENCODER_A = 1;
    public static final int LEFT_FRONT_ENCODER_B = 2;
    public static final int RIGHT_FRONT_ENCODER_A = 5;
    public static final int RIGHT_FRONT_ENCODER_B = 6;
    public static final int LEFT_REAR_ENCODER_A = 3;
    public static final int LEFT_REAR_ENCODER_B = 4;
    public static final int RIGHT_REAR_ENCODER_A = 7;
    public static final int RIGHT_REAR_ENCODER_B = 8;  
    
    //Arm
    public static final int ARM_TOPLS = 3;
    public static final int ARM_BOTTOMLS = 2;
    public static final int ARM_JAGUAR = 7;
    
    //Shooter 
    public static final int SHOOTER_SPEED_CONTROLLER_FRONT = 6;     //PWM
    public static final int SHOOTER_SPEED_CONTROLLER_REAR = 5;      //PWM
    public static final int LOADER_LEVER_RELAY = 2;
    public static final int LOADER_FEED_RELAY = 1;
    public static final int LOADER_LEVER_LIMIT = 1;
    public static final double FEEDER_RUN_TIME = 1.0;
    public static final double FEED_LEVER_DELAY = 0.5;
    public static final double FEED_HOPPER_DELAY = 0.1;
    public static final double FEEDER_FAILSAFE_TIME = 3.0;
    public static final double FEEDER_CLEAR_TIME = 1.0;
    public static final double FEEDER_MANUAL_TIME = 0.5;
    
    public static final double SHOOTER_PACE_TARGET = 10.0;
    public static final double SHOOTER_OVERRIDE_TIME = .35;
    public static final double SHOOTER_IDLE_SPEED = 0.65;
    public static final double SHOOTER_FINAL_DEF_SPEED = 0.62;
    public static final double SHOOTER_PROT0_DEF_SPEED = 0.72;
    public static final double SHOOTER_PREP_TIME = 0.25;
    public static final int SHOOTER_OPTICAL_SENSOR = 10;
    public static final int OPTICAL_SENSOR_SLOT = 1;
    public static final int OPTICAL_SENSOR_FRONT = 11;
    public static final int OPTICAL_SENSOR_REAR = 9;//not in use

    //Camera stuff
    public static final int LED_COLOR = 0; //0 = green, 1 = UV
    public static final int REM_SMALL_OBJ_ITERATIONS = 7;
    public static final boolean IMAGE8_CONNECTIVITY = true;
    public static final int PARTICLE_FILTER_LIMIT = 450;
    public static final double PIXEL_TO_C_ROTATION = .0005;  //when using a servo to move the camera
    public static final double PIXEL_TO_T_ROTATION = .0015;  //when using the turret to move the camera


    public static final int THRESH_UV_RL = 85;
    public static final int THRESH_UV_RH = 245;
    public static final int THRESH_UV_GL = 255;
    public static final int THRESH_UV_GH = 255;
    public static final int THRESH_UV_BL = 90;
    public static final int THRESH_UV_BH = 255;

    ///Edison Settings
    public static final int THRESH_GR_RL = 0;
    public static final int THRESH_GR_RH = 210;
    public static final int THRESH_GR_GL = 246;
    public static final int THRESH_GR_GH = 255;
    public static final int THRESH_GR_BL = 97;
    public static final int THRESH_GR_BH = 224;

 /*  public static final int THRESH_GR_RL = 0;
    public static final int THRESH_GR_RH = 255;
    public static final int THRESH_GR_GL = 236;
    public static final int THRESH_GR_GH = 255;
    public static final int THRESH_GR_BL = 0;
    public static final int THRESH_GR_BH = 255; */

    // Settings for Rally lighting
//    public static final int THRESH_GR_RL = 0;
//    public static final int THRESH_GR_RH = 245;
//    public static final int THRESH_GR_GL = 235;
//    public static final int THRESH_GR_GH = 255;
//    public static final int THRESH_GR_BL = 0;
//    public static final int THRESH_GR_BH = 255;

    // --------------------- COMPRESSOR ------------------------------
//    public static final int COMPRESSOR_RELAY_OUTPUT = 3;
//    public static final int COMPRESSOR_LIMIT = 10;

    //public static final int COMPRESSOR_RELAY_SLOT = 1;
//    public static final int SOLENOID_RELAY_CLOSE = 2;
//    public static final int SOLENOID_RELAY_OPEN = 1;

    // autonomous mode detection
    public static final int AUTO_SWITCH_1 = 12;
    public static final int AUTO_SWITCH_2 = 13;//13
    public static final int AUTO_SWITCH_3 = 14;//14
    public static final double AUTO_SHOOTER_SPEED_FINAL = 0.60;
    public static final double AUTO_SHOOTER_SPEED_PROTO = 0.62;


}
