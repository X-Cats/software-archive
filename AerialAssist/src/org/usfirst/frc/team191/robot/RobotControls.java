/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.KinectStick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 *
 * @author mary farmer
 */
public class RobotControls {

    private Joystick mleftJS;
    private Joystick mrightJS;
    private Joystick mOpConJS;
    private RobotDrive mAllDrives;
    private DigitalInput mDISW1;
    private DigitalInput mDISW2;
    private DigitalInput mDISW3;
    private Compressor mCompressor;
    private Ultrasonic mUltrasonic;
    private AutoTarget mAutoTarget;
    private DriverStationEnhancedIO mdsIO;

//    private Shooter mShooter;
    public void init() {
        System.out.println("RobotControls Init");
    }

    public void robotInit() {

        System.out.println("robotInit!");


        mAllDrives = new RobotDrive(Enums.LEFT_DRIVE, Enums.RIGHT_DRIVE);
        mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearLeft,true);
        mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearRight,true);

        mUltrasonic = new Ultrasonic(Enums.ULTRASONIC_PING, Enums.ULTRASONIC_ECHO);
        mUltrasonic.setDistanceUnits(Ultrasonic.Unit.kMillimeter);
        
        mAutoTarget = new AutoTarget();
        
        //This is the enhanced IO on the driverstation
        mdsIO = DriverStation.getInstance().getEnhancedIO();

        mleftJS = new Joystick(Enums.LEFT_JOYSTICK_PORT);
        mrightJS = new Joystick(Enums.RIGHT_JOYSTICK_PORT);

        mOpConJS = new Joystick(Enums.OPCON_JOYSTICK_PORT);

        try {
            mDISW1 = new DigitalInput(Enums.AUTO_SWITCH_1);
            mDISW2 = new DigitalInput(Enums.AUTO_SWITCH_2);
            mDISW3 = new DigitalInput(Enums.AUTO_SWITCH_3);
        } catch (Exception ex) {
            System.out.println("Digital inputs not found");
        }
    }

    public void initTestMode() {
        //Test mode

        System.out.println("Test Mode Initialized");

        //We don't want the robot drive engaged for this
        mAllDrives.setSafetyEnabled(false);
        //mAllDrives = null;        


    }

    public void executeTestPeriodic() {
        LiveWindow.run();
        //System.out.println("Run LW...");
    }

    public void disable() {
        System.out.println("Disable RobotControls");


        if (mAllDrives != null) {
            mAllDrives.stopMotor();
        }

    }

    public void drive() {
        mAllDrives.tankDrive(mleftJS, mrightJS);
        //poll for button pushes here
        pollJSButtons();
        
    }
    public void pollJSButtons(){
        
    }

    public boolean getDSValue(int index) {
        //this gets the dipswitch values
        switch (index) {
            case 1:
                return mDISW1.get();

            case 2:
                return mDISW2.get();

            case 3:
                return mDISW3.get();
            default:
                return false;
        }
    }

    public void driveAtSpeed(double speed) {
        mAllDrives.tankDrive(speed, speed);
    }

    public void pollEnhancedIO() {

  
    }

    private void outputToUserScreen(String strMsg) {
        outputToUserScreen(strMsg, DriverStationLCD.Line.kUser5);
    }

    private void outputToUserScreen(String strMsg, DriverStationLCD.Line line) {
        try {
            System.out.println(strMsg);

            DriverStationLCD driverScreen = DriverStationLCD.getInstance();
            driverScreen.println(line, 1, strMsg.substring(0, Math.min(strMsg.length(), DriverStationLCD.kLineLength)));
            driverScreen.updateLCD();

        } catch (Exception ex) {
            //do nothing
        }
    }

    public void updateStatus() {
        //double shooterSP = 0;
        mUltrasonic.ping();
        SmartDashboard.putNumber("Distance:", mUltrasonic.getRangeMM());
        SmartDashboard.putBoolean("Ultralsonc Enabled: ", mUltrasonic.isEnabled());  
        SmartDashboard.putString("Test", "mary had a little lamb");  
    
    }
}
