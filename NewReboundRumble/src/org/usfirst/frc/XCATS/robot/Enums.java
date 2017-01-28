package org.usfirst.frc.XCATS.robot;

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
    public static final boolean DEBUG_CAMERA_IMAGES = false;
    public static final boolean SWAP_BACK_TO_FRONT = true;
    public static final boolean CAMERA_ACTIVE = true;
    public static final boolean TELEOP_CONTINUOUS_ACTIVE = false;
    public static final boolean KINECT_ENABLED = false;
    public static final boolean CAMERA_SERVOS_ACTIVE = true;
    public static final boolean RETARGET_ACTIVE = true;
    public static final boolean ENHANCED_OPERATORCONTROL_ACTIVE = false;

    //Enhanced IO
    public static final double ENH_IO_ANALOG_MAX = 3.3;
    public static final double ENH_IO_SPEED_MAX = 0.7;//100% top of the slider
    public static final double ENH_IO_SPEED_MIN = 0.5; //50% bottom of the slider

    //PWMs
    public static final int LEFT_FRONT_DRIVE = 1; // left
    public static final int RIGHT_FRONT_DRIVE = 2;  //  right
    
    // ------------------JOYSTICKS -----------------------------
    public static final int LEFT_JOYSTICK_PORT = 1;
    public static final int RIGHT_JOYSTICK_PORT = 2;
    public static final int OPCON_JOYSTICK_PORT = 3;
    public static final int LEFT_KINECT_STICK = 1;
    public static final int RIGHT_KINECT_STICK = 2;

    //-------------------ENCODERS ---------------------------------
    public static final int LEFT_FRONT_ENCODER_A = 1;
    public static final int LEFT_FRONT_ENCODER_B = 2;
    public static final int LEFT_REAR_ENCODER_A = 3;
    public static final int LEFT_REAR_ENCODER_B = 4;
    public static final int SHOOTER_ENCODER_A = 5;
    public static final int SHOOTER_ENCODER_B = 6;

    //Turret
    public static final int TURRET_VOLTAGE = 1;
    public static final int TURRET_LS_LEFT = 7;
    public static final int TURRET_LS_RIGHT = 8;
    public static final int TURRET_SPEED_CONTROLLER = 6;
    public static final double TURRET_HIGH_SPEED = 1.0;
    public static final double TURRET_LOW_SPEED = 0.33;
    public static final double TURRET_MIN_VOLT_FINAL = 1.31; //Final robot Turret min
    public static final double TURRET_MAX_VOLT_FINAL = 4.71; //Final robot Turret max
    public static final double TURRET_MIN_VOLT_PROTO = 0.17; //Proto robot Turret min
    public static final double TURRET_MAX_VOLT_PROTO = 3.12; //Proto robot Turret max

    public static final double SHOOTER_IDLE_SPEED = 0.65;
    public static final int SHOOTER_SPEED_CONTROLLER = 5;
    public static final double SHOOTER_PREP_TIME = 0.25;
    public static final int SHOOTER_ENC_TOLERANCE = 300;
    public static final double SHOOTER_ENC_TOLERANCE_PCT = 0.075;
    public static final int SHOOTER_LIGHT_RELAY = 1;
    public static final double SHOOTER_ELEVATOR_TIMOUT = 0.50;
    public static final double SHOOTER_ENCODER_SLOPE_FINAL = 6798.113095;
    public static final double SHOOTER_ENCODER_INTERCEPT_FINAL = 64.8952381;
    public static final double SHOOTER_ENCODER_SLOPE_PROTO = 10882.4881;
    public static final double SHOOTER_ENCODER_INTERCEPT_PROTO = -1380.504762;


    public static final double ACQUISITION_SPEED = 1;
    public static final double ACQUISITION_SLOW = 1;
    public static final double ACQUISITION_SHOOT_SPEED = 0.80;
    public static final int ACQUIS_FRONT_SPEED_CONTROLLER = 4;

    public static final int ELEVATOR_SPEED_CONTROLLER = 3;
    public static final int ELEVATOR_BALL_LS = 9;
    public static final int ELEVATOR_BALLREADY_LS = 14;

    //Camera stuff
    public static final int CAMERA_SERVO_MODULE = 1;
    //public static final int CAMERA_SWIVEL_PWM = 2;
    public static final int CAMERA_SWIVELUD_PWM = 7;
    public static final double CAMERA_SWIVEL_INIT_POS = 0.6;
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

    ///30 hart street settings
    public static final int THRESH_GR_RL = 0;
    public static final int THRESH_GR_RH = 220;
    public static final int THRESH_GR_GL = 236;
    public static final int THRESH_GR_GH = 255;
    public static final int THRESH_GR_BL = 0;
    public static final int THRESH_GR_BH = 255;

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
    public static final int COMPRESSOR_RELAY_OUTPUT = 3;
    public static final int COMPRESSOR_LIMIT = 10;

    //public static final int COMPRESSOR_RELAY_SLOT = 1;
    public static final int SOLENOID_RELAY_CLOSE = 2;
    public static final int SOLENOID_RELAY_OPEN = 1;

    // autonomous mode detection
    public static final int AUTO_SWITCH_1 = 11;
    public static final int AUTO_SWITCH_2 = 12;//13
    public static final int AUTO_SWITCH_3 = 13;//14
    public static final double AUTO_SHOOTER_SPEED_FINAL = 0.60;
    public static final double AUTO_SHOOTER_SPEED_PROTO = 0.62;


}
