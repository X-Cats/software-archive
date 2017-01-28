package org.usfirst.frc.XCATS.robot;

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
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.KinectStick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */
public class RobotControls {

    private Joystick mleftJS;
    private Joystick mrightJS;
    private Joystick mOpConJS;
    //private RobotDrive mAllDrives;
    private XCatDrive mAllDrives;
    private KinectStick mKinectStick1;
    private KinectStick mKinectStick2;
    private boolean mbKinectEnabled = Enums.KINECT_ENABLED;
    private Arm mArm;
    private DigitalInput mDISW1;
    private DigitalInput mDISW2;
    private DigitalInput mDISW3;
    private Compressor mCompressor;
    private double mShooterSpeed = 50.0;
    private boolean mShooterEnabled = false;
    private boolean mClearJam = false;
    private AutoTarget mAutoTarget;
    private DriverStationEnhancedIO mdsIO;
    private boolean mSpeedChanged = false;
    private double mLastSpeed = 0.0;


//    private Shooter mShooter;

    public void init() {
        System.out.println("Teleop Init");
        mAutoTarget.getShooter().getAcquisition().setShooterElevatorTimeout(Enums.SHOOTER_ELEVATOR_TIMOUT);
    }

    public void robotInit() {

        System.out.println("RobotControls Init!");

        mCompressor = new Compressor(Enums.COMPRESSOR_LIMIT, Enums.COMPRESSOR_RELAY_OUTPUT);
        mCompressor.start();

//        m_relay = new Relay(1, Enums.COMPRESSOR_RELAY_OUTPUT, Relay.Direction.kForward);

        //mAllDrives = new RobotDrive(Enums.LEFT_FRONT_DRIVE, Enums.RIGHT_FRONT_DRIVE);
        //mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearLeft,true);
        //mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearRight,true);
        mAllDrives = new XCatDrive(Enums.LEFT_FRONT_DRIVE, Enums.RIGHT_FRONT_DRIVE);

        //This is the enhanced IO on the driverstation
        mdsIO = DriverStation.getInstance().getEnhancedIO();

        mKinectStick1 = new KinectStick(Enums.LEFT_KINECT_STICK);
        mKinectStick2 = new KinectStick(Enums.RIGHT_KINECT_STICK);
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

        mAutoTarget = new AutoTarget();

        //Compressor stuff
        mArm = new Arm();

        mShooterSpeed = mAutoTarget.getShooter().getSpeed();


        /*
        DataLogger lf = new DataLogger("file:///mary.log");
        lf.println("mary had a little lamb");
        lf.close();
         */

    }
//    private double getScaledShooter(){
//        return mdsIO.getAnalogIn(3)
//    }

    public boolean getDSValue(int index) {
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

    public void disable() {
        System.out.println("Disable RobotControls");
        if (mAutoTarget != null) {
            mAutoTarget.disable();
        }

        if (mArm != null) {
            mArm.disable();
        }

        if (mAllDrives != null) {
            mAllDrives.disable();
        }

    }

    public void useKinect(boolean bEnabled) {
        mbKinectEnabled = bEnabled;

    }

    public void drive() {

        if (mbKinectEnabled) {
            //do the logic here for the kinect
            System.out.println("Y1:" + mKinectStick1.getY(GenericHID.Hand.kLeft));
            System.out.println("Y2:" + mKinectStick2.getY(GenericHID.Hand.kRight));
            mAllDrives.tankDrive(mKinectStick1, mKinectStick2);
        } else {
            mAllDrives.drive(mleftJS, mrightJS);
        }

    }

    public void driveAtSpeed(double speed) {
        mAllDrives.tankDrive(speed, speed);
    }

    public void pollEnhancedIO() {

        /* Enhanced IO
         * Digitals
         * 1        Left
         * 2        Home
         * 3        Right
         * 4        Shoot
         * 5        Enable Shooter
         * 6        Auto Off
         * 7
         * 8        Acq FWD
         * 9        Acq Rvs
         * 10       Elv FWD
         * 11       Elv Rev
         * 12
         *
         * Analog
         * 1        Trim Aim
         * 2        Trim Range
         * 3        Slider for Distance (Manual Range)
         */

        try {

            //Digitals -----------------------------------------------

            //This moves the turret to either left or right or the home position
            if (mdsIO.getDigital(1)) {
                mAutoTarget.moveTurretLeft();
            } else if (mdsIO.getDigital(2)) {
                mAutoTarget.moveTurretToCenter();
            } else if (mdsIO.getDigital(3)) {
                mAutoTarget.moveTurretRight();
            } else {
                mAutoTarget.stopTurret();
            }


            mClearJam = false;

            //this is the camera swivel
            if (mdsIO.getDigital(7)){
                mAutoTarget.moveCameraUp();
            } else if (mdsIO.getDigital(12)){
                mAutoTarget.moveCameraDown();
            } 

            //digital 5 is the forward backward
            if (mdsIO.getDigital(5)) {

                if (mdsIO.getDigital(4)) {
                    try {
                        //modal
                        mAutoTarget.target(true, false, false);
                    } catch (Exception ex) {
                    }
                } else {
   //Set speed at 100%
        if (mleftJS.getRawButton(1)){
            mAutoTarget.getShooter().setSpeedandEngage(1.0);
        } else {
                    //manual mode, no targetting

//                    double deltaSpeed = 0.0;
//                    deltaSpeed = Math.abs(getScaledSpeed(mdsIO.getAnalogIn(3)) - mLastSpeed);
//                    SmartDashboard.putDouble("DeltaSpeed",deltaSpeed);
//                    if (!mAutoTarget.getAutoTargetSuccess()) {
                        mAutoTarget.getShooter().setSpeedandEngage(getScaledSpeed(mdsIO.getAnalogIn(3)));
//                        System.out.println("NO Auto Target NO Speed");
//                    }
//                    mLastSpeed = getScaledSpeed(mdsIO.getAnalogIn(3));
                    }
                }

            } else {
                //go backwards to clear a jam
                mAutoTarget.getShooter().setSpeedandEngage(-getScaledSpeed(mdsIO.getAnalogIn(3)));
            }

            //we will probably need to check to see if we have started to shoot first
            if (!mAutoTarget.getShooter().getPrepToShoot()) {

                if (mdsIO.getDigital(8)) {
                    mAutoTarget.getShooter().getAcquisition().acqRollIn();
                } else if (mdsIO.getDigital(9)) {
                    mAutoTarget.getShooter().getAcquisition().acqRollOut();
                        System.out.println("RollOut");
                } else {
                    mAutoTarget.getShooter().getAcquisition().stopAcq();
                }

//                    if (mdsIO.getDigital(10) || mdsIO.getDigital(4)) {
                if (mdsIO.getDigital(10)) {
                    mAutoTarget.getShooter().getAcquisition().elevatorRollIn();
                } else if (mdsIO.getDigital(11)) {
                    mAutoTarget.getShooter().getAcquisition().elevatorRollOut();
//                        System.out.println("RollOut");
                } else {
                    mAutoTarget.getShooter().getAcquisition().stopElevator();
                }
            }

            //Analogs -----------------------------------------------
            SmartDashboard.putBoolean("D6", mdsIO.getDigital(6));
            SmartDashboard.putBoolean("D7", mdsIO.getDigital(7));
           // mAutoTarget.setTurretOffset(getScaledIO(mdsIO.getAnalogIn(1)));
            mAutoTarget.setSpeedOffset(getScaledIO(mdsIO.getAnalogIn(2)));

        } catch (EnhancedIOException ex) {
        }

    }

    private double getScaledSpeed(double val) {

//        return (1.0 - 1.25 * (Enums.ENH_IO_ANALOG_MAX - val)/Enums.ENH_IO_ANALOG_MAX);

        //lets try scaling it so that it is between 50 and 100 rather than 0-100
        if (val > 0.05){
           return (Enums.ENH_IO_SPEED_MAX - ( Enums.ENH_IO_SPEED_MAX - Enums.ENH_IO_SPEED_MIN) * (Enums.ENH_IO_ANALOG_MAX - val)/Enums.ENH_IO_ANALOG_MAX);
        }
        {
           return val / Enums.ENH_IO_ANALOG_MAX;
        }
    }

    private double getScaledIO(double val) {
        return (1.0 - 2 * (Enums.ENH_IO_ANALOG_MAX - val) / Enums.ENH_IO_ANALOG_MAX);
    }

    public void pollJSButtons() {
        /* BUTTONS ----------------------------------------------------
        Joystick    Button      Action
         *  Left        2           AutoTarget
         *  Left        7           Reset Robot
         *  Left        8           Reverse Elevator to clear jam
         *  Right       Trigger     Reverse Joysticks/Swap back to Front
         *  Right       2           Bring Arm Up/Down
         *  Right       3           Switch to Arcade Mode/Tank Mode
         *  Right       4/5         Move Camera Server Down/Up
         *  OpCon       10          AutoTarget
         *  OpCon       9           Move Turret to Center
         *  OpCon       l/r         Move Turret Left/Right
         *  OpCon       1           Shoot
         *  OpCon       3           Turns the Shooter on/off
         *  OpCon       u/d         Increase/Decrease Shooter Wheel Speed
         *  OpCon       5           Roll Acquisition in
         *  OpCon       7           Roll Acquisition out
         *  opCon       6           Shoot
         *
         *
         */



     
        //autotarget
        if (mleftJS.getRawButton(2)) {
            target(true, true, false);
        }

        //Reset robot to init settings (use this in the pit to reset the robot
        if (mleftJS.getRawButton(7)) {
            mAutoTarget.disable();
        }

        if (mrightJS.getRawButton(2)) {
//            System.out.println("ArmToggle");
            mArm.toggle();
        }
        if (mrightJS.getRawButton(1)) {
            System.out.println("SwapBackToFront");
            mAllDrives.swapBackToFront();
            Timer.delay(0.10);
        }

        //Switch between arcade and tank
        if (mrightJS.getRawButton(3)) {
            mAllDrives.toggleDriveRange();
            Timer.delay(0.1);
        }

        if (mrightJS.getRawButton(4)) {
            mAutoTarget.moveCameraDown();
        } else if (mrightJS.getRawButton(5)) {
            mAutoTarget.moveCameraUp();
        }

        if (Enums.ENHANCED_OPERATORCONTROL_ACTIVE) {
            pollEnhancedIO();
        } else {
            if (mOpConJS.getRawButton(10)) {
                target(true, true, false);
            }

            
            //this is the "shoot" button
            // on the final control system we will poll the state of the "Auto" buttons
            // We need to set a boolean somewhere that will be enabled disabled for auto target
            if (mOpConJS.getRawButton(6)) {
                mAutoTarget.getShooter().Shoot(false);//wait for speed?
            }

            if (mOpConJS.getRawButton(1)) {
                mShooterEnabled = !mShooterEnabled;
                Timer.delay(0.05);
                if (mShooterEnabled) {
                    mAutoTarget.getShooter().setSpeedandEngage(mShooterSpeed);
                } else {
                    mAutoTarget.getShooter().setSpeedandEngage(0);
                }
            }

            if (mShooterEnabled) {
                if (mOpConJS.getY() > 0) {
                    mShooterSpeed = mAutoTarget.getShooter().getSpeed() - 0.025;
                    outputToUserScreen("Shooter Speed: " + mShooterSpeed);
                    mAutoTarget.getShooter().setSpeedandEngage(mShooterSpeed);
                    Timer.delay(0.15);
                }
                if (mOpConJS.getY() < 0) {
                    mShooterSpeed = mAutoTarget.getShooter().getSpeed() + 0.025;
                    outputToUserScreen("Shooter Speed: " + mShooterSpeed);
                    mAutoTarget.getShooter().setSpeedandEngage(mShooterSpeed);
                    Timer.delay(0.15);
                }
            }

            if (mOpConJS.getRawButton(9)) {
                mAutoTarget.moveTurretToCenter();
            }


            //Move the turret based on operator control xAxis
            if (mOpConJS.getX() > 0) {
                mAutoTarget.moveTurretRight();
                //mAutoTarget.setTurretSpeedTest(Enums.TURRET_HIGH_SPEED);
            } else if (mOpConJS.getX() < 0) {
                mAutoTarget.moveTurretLeft();
                //mAutoTarget.setTurretSpeedTest(-Enums.TURRET_HIGH_SPEED);
            } else {
                mAutoTarget.stopTurret();
            }
            //mAutoTarget.setTurretSpeedTest(0.0);

            //The shooter speed is set from the Y axis of the operator joystick
            //  mShooter.setSpeedandEngage(mOpConJS.getY());
//        if (mOpConJS.getY() > 0.0){
//            mShooter.setSpeedandEngage(0.50) ;
//        }else  if (mOpConJS.getY() < -1.0){
//            mShooter.setSpeedandEngage(-0.50) ;
//        } else
//            mShooter.setSpeedandEngage(0.0);


//        if (mOpConJS.getRawButton(1)){
//            mShooter.Shoot(true);
//        }


            //This sets the acquisition rollers... both the elevator and acquisition together
            if (mOpConJS.getRawButton(5)) {
                mAutoTarget.shooterAcqRollIn();
            } else if (mOpConJS.getRawButton(7)) {
                mAutoTarget.shooterAcqRollOut();
            } else {
                mAutoTarget.shooterAcqStop();
            }

//        try{
//        System.out.println("Switches: "+mDISW1.get()+','+mDISW2.get()+','+mDISW3.get());
//        }
//                catch(Exception ex){}

//        if (mOpConJS.getRawButton(7)){
//            m_relay.set(Relay.Value.kOn);
//        } else
//            m_relay.set(Relay.Value.kOff);


            //mShooter.logShooter();

        }


    }

    public void setTurretSP(double deltaFromCenterSP){
        mAutoTarget.setTurretSPNew(deltaFromCenterSP);
    }
    public void moveTurretToSP() {
        mAutoTarget.moveTurretToSP();
    }

    public void target(boolean autoAim, boolean autoDistance, boolean autoShoot) {
        try {
            if (Enums.CAMERA_ACTIVE) {
                System.out.println("Targetting engaged!");
                mAutoTarget.target(autoAim, autoDistance, autoShoot);
            }
        } catch (AxisCameraException ex) {
            ex.printStackTrace();
        }
    }

    public Shooter getShooter() {
        return mAutoTarget.getShooter();
    }

    public Arm getArm() {
        return mArm;
    }

    public XCatDrive getXCatDrive() {
        return mAllDrives;
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

        SmartDashboard.putBoolean("DS 1", this.mDISW1.get());
        SmartDashboard.putBoolean("DS 2", this.mDISW2.get());
        SmartDashboard.putBoolean("DS 3", this.mDISW3.get());
        SmartDashboard.putBoolean("Clear Jam", this.mClearJam);
        SmartDashboard.putBoolean("Final Robot", Enums.IS_FINAL_ROBOT);

        mAutoTarget.updateStatus();
        mAllDrives.updateStatus();
        mArm.updateStatus();
    }
}
