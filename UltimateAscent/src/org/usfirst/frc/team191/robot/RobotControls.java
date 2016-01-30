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
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
    private MecanumDrive mAllDrives;
    private KinectStick mKinectStick1;
    private KinectStick mKinectStick2;
    private boolean mbKinectEnabled = Enums.KINECT_ENABLED;
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
    private boolean useTank = true;
    private boolean mecanumTest = false;
    private boolean tg = false;
    private boolean tg2 = false;
    private Shooter mShooter;
    private Jaguar mfrontLeftMotorJag;
    private Jaguar mfrontRightMotorJag;
    private Jaguar mrearLeftMotorJag;
    private Jaguar mrearRightMotorJag;
    private Arm mArm;
    private double mArmSpeed = 0.7;
    private boolean mtgShooter = false;

//    private Shooter mShooter;
    public void init() {
        System.out.println("RobotControls Init");
        //mAutoTarget.getShooter().getAcquisition().setShooterElevatorTimeout(Enums.SHOOTER_ELEVATOR_TIMOUT);
    }

    public void robotInit() {

        System.out.println("robotInit!");

//        mCompressor = new Compressor(Enums.COMPRESSOR_LIMIT, Enums.COMPRESSOR_RELAY_OUTPUT);
//        mCompressor.start();

//        m_relay = new Relay(1, Enums.COMPRESSOR_RELAY_OUTPUT, Relay.Direction.kForward);

        //mAllDrives = new RobotDrive(Enums.LEFT_FRONT_DRIVE, Enums.RIGHT_FRONT_DRIVE);
        //mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearLeft,true);
        //mAllDrives.setInvertedMotor(RobotDrive.MotorType.kRearRight,true);

        mfrontLeftMotorJag = new Jaguar(Enums.LEFT_FRONT_DRIVE);
        mfrontRightMotorJag = new Jaguar(Enums.RIGHT_FRONT_DRIVE);
        mrearLeftMotorJag = new Jaguar(Enums.LEFT_REAR_DRIVE);
        mrearRightMotorJag = new Jaguar(Enums.RIGHT_REAR_DRIVE);

        mArm = new Arm();

//        mAllDrives = new MecanumDrive(Enums.LEFT_FRONT_DRIVE, Enums.LEFT_REAR_DRIVE, Enums.RIGHT_FRONT_DRIVE, Enums.RIGHT_REAR_DRIVE);
        mAllDrives = new MecanumDrive(mfrontLeftMotorJag, mrearLeftMotorJag, mfrontRightMotorJag, mrearRightMotorJag);

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

        mShooter = new Shooter();


        initLiveWindow();
    }

    protected Shooter getShooter() {
        return mShooter;
    }

    protected Arm getArm() {
        return mArm;
    }

    private void initLiveWindow() {
        System.out.println("Init XCats Live Window!");
        if (mfrontLeftMotorJag == null) {
            mfrontLeftMotorJag = new Jaguar(Enums.LEFT_FRONT_DRIVE);
            mfrontRightMotorJag = new Jaguar(Enums.RIGHT_FRONT_DRIVE);
            mrearLeftMotorJag = new Jaguar(Enums.LEFT_REAR_DRIVE);
            mrearRightMotorJag = new Jaguar(Enums.RIGHT_REAR_DRIVE);

        }
        LiveWindow.addActuator("Drives", "Left Front", mfrontLeftMotorJag);
        LiveWindow.addActuator("Drives", "Left Rear", mrearLeftMotorJag);
        LiveWindow.addActuator("Drives", "Right Front", mfrontRightMotorJag);
        LiveWindow.addActuator("Drives", "Right Rear", mrearRightMotorJag);


    }

    public void initTestMode() {
        //Test mode

        System.out.println("Test Mode Initialized");
        initLiveWindow();

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

        mShooter.disable();
        mShooterSpeed = 0.0;

        if (mAutoTarget != null) {
            mAutoTarget.disable();
        }

        if (mAllDrives != null) {
            mAllDrives.disable();
        }

    }

    public void drive() {

        if (mbKinectEnabled) {
            //do the logic here for the kinect
            System.out.println("Y1:" + mKinectStick1.getY(GenericHID.Hand.kLeft));
            System.out.println("Y2:" + mKinectStick2.getY(GenericHID.Hand.kRight));
            mAllDrives.tankDrive(mKinectStick1, mKinectStick2);
        } else {
            //mAllDrives.drive(mleftJS, mrightJS);
            if (mleftJS.getRawButton(1)) {
                System.out.println("Left JS");
            }
            if (mrightJS.getRawButton(1)) {
                System.out.println("Right JS");
            }

            //use this to test when drives are funky, all wheels should go forward
//           mAllDrives.mecanumDrive_Cartesian(0.0, 0.5, 0.0, 0.0);
            //mAllDrives.mecanumDrive_Cartesian(mleftJS.getX(), mleftJS.getY(), mrightJS.getX(), 0.0);           
//           if(mleftJS.getRawButton(10) || mrightJS.getRawButton(10))


            if ((mleftJS.getRawButton(2) || mrightJS.getRawButton(2)) && !tg) {
                if (useTank) {
                    useTank = false;
                } else {
                    useTank = true;
                }
                tg = true;
            }
            if (tg && !mleftJS.getRawButton(2) && !mrightJS.getRawButton(2)) {
                tg = false;
            }
            
            //the toggle for the mecanumTest variable
            if ((mleftJS.getRawButton(9) || mrightJS.getRawButton(9)) && !tg2) {
                if (mecanumTest) {
                    mecanumTest = false;
                } else {
                    mecanumTest = true;
                }
                tg2 = true;
            }
            if (tg2 && !mleftJS.getRawButton(9) && !mrightJS.getRawButton(9)) {
                tg2 = false;
            }
            
            //use this to test if the robot strafes in straight lines
            if(mecanumTest){
                mAllDrives.mecanumDrive_Cartesian(0.5, 0.0, 0.0, 0.0);
            } else
            if (useTank) {
                mAllDrives.mecanumDrive_Tank(mleftJS, mrightJS);
            } else {
                mAllDrives.mecanumDrive_Cartesian(mrightJS.getX(), mrightJS.getY(), mleftJS.getX(), 0.0);
            }
            
            SmartDashboard.putBoolean("Tank", useTank);
            
            
        }

        //Shooter increase/decrease
        if (mOpConJS.getRawButton(2)) {
            if (mLastSpeed > -1) {
                mLastSpeed = mLastSpeed - 0.005;
            }
            mShooter.setSpeedandEngage(mLastSpeed);

        } else if (mOpConJS.getRawButton(4)) {
            if (mLastSpeed < 1) {
                mLastSpeed = mLastSpeed + 0.005;
            }
            mShooter.setSpeedandEngage(mLastSpeed);
        } else if (mOpConJS.getRawButton(3)) {
            //toggle between off and on setting speed
            if (!mtgShooter) {
                if (mShooter.getSpeed() == 0.0) {
                    if (Enums.IS_FINAL_ROBOT) {
                        mLastSpeed = Enums.SHOOTER_FINAL_DEF_SPEED;

                    } else {
                        mLastSpeed = Enums.SHOOTER_PROT0_DEF_SPEED;
                    }
                    mShooter.setSpeedandEngage(mLastSpeed);
                } else {
                    mLastSpeed = 0.0;
                    mShooter.setSpeedandEngage(0.0);
                }
                mtgShooter = true;
            } 
        }
        if (!mOpConJS.getRawButton(3)) {
            mtgShooter = false;
        }

        SmartDashboard.putBoolean("Shooter Toggle", mtgShooter);

        if (mOpConJS.getRawButton(1)) {
            mShooter.feedFrisbee();
        }
        if (mOpConJS.getRawButton(10)) {
            mShooter.clearJam();
        }
        if (mOpConJS.getRawButton(6)) {
            mShooter.manualFeed();
        }
        if (mOpConJS.getRawButton(8)) {
            mShooter.overrideShooter();
        }

        //Change the arm speed, checking that they are in range
        if (mOpConJS.getRawButton(5) && mArmSpeed < 0.99) {
            mArmSpeed += 0.01;
        } else if (mOpConJS.getRawButton(7) && mArmSpeed > 0.01) {
            mArmSpeed -= 0.01;
        }
        
        //Move the arm up or down
        if (mOpConJS.getY() < 0) {
//            mArm.setSpeed(Math.abs(mArmSpeed));
            mShooter.incrementPidKp(-1);
        } else if (mOpConJS.getY() > 0) {
//            mArm.setSpeed(-Math.abs(mArmSpeed));
            mShooter.incrementPidKp(1);
        } else {
//            mArm.setSpeed(.0);
            mShooter.incrementPidKp(0);
        }

        SmartDashboard.putNumber("gamepad Y: ", mOpConJS.getY());
        SmartDashboard.putNumber("gamepad X: ", mOpConJS.getX());


//      System.out.println("OpCon Button: "+ mOpConJS.getButton(Joystick.ButtonType.kNumButton));

    }

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


            mClearJam = false;

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
                    if (mleftJS.getRawButton(1)) {
//            mAutoTarget.getShooter().setSpeedandEngage(1.0);
                    } else {
                        //manual mode, no targetting
//                    double deltaSpeed = 0.0;
//                    deltaSpeed = Math.abs(getScaledSpeed(mdsIO.getAnalogIn(3)) - mLastSpeed);
//                    SmartDashboard.putDouble("DeltaSpeed",deltaSpeed);
//                    if (!mAutoTarget.getAutoTargetSuccess()) {
                        //                      mAutoTarget.getShooter().setSpeedandEngage(getScaledSpeed(mdsIO.getAnalogIn(3)));
//                        System.out.println("NO Auto Target NO Speed");
//                    }
//                    mLastSpeed = getScaledSpeed(mdsIO.getAnalogIn(3));
                    }
                }

            }


            //Analogs -----------------------------------------------
//            SmartDashboard.putBoolean("D6", mdsIO.getDigital(6));
//            SmartDashboard.putBoolean("D7", mdsIO.getDigital(7));
            // mAutoTarget.setTurretOffset(getScaledIO(mdsIO.getAnalogIn(1)));
            mAutoTarget.setSpeedOffset(getScaledIO(mdsIO.getAnalogIn(2)));

        } catch (EnhancedIOException ex) {
        }

    }

    private double getScaledSpeed(double val) {

//        return (1.0 - 1.25 * (Enums.ENH_IO_ANALOG_MAX - val)/Enums.ENH_IO_ANALOG_MAX);

        //lets try scaling it so that it is between 50 and 100 rather than 0-100
        if (val > 0.05) {
            return (Enums.ENH_IO_SPEED_MAX - (Enums.ENH_IO_SPEED_MAX - Enums.ENH_IO_SPEED_MIN) * (Enums.ENH_IO_ANALOG_MAX - val) / Enums.ENH_IO_ANALOG_MAX);
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
         *  OpCon       2           AutoTarget
         *  OpCon       3           Move Turret to Center
         *  OpCon       4/5         Move Turret Left/Right
         *  OpCon       6           Shoot
         *  OpCon       7           Turns the Shooter on/off
         *  OpCon       8/9         Increase/Decrease Shooter Wheel Speed
         *  OpCon       10          Roll Acquisition in
         *  OpCon       11          Roll Acquisition out
         *  opCon       Trigger     Shoot
         *
         *
         */
    }

//    public XCatDrive getXCatDrive() {
//        return mAllDrives;
//    }
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
        double shooterSP = 0;
        //SmartDashboard.putString("Test", "mary had a little lamb");  
        SmartDashboard.putNumber("Arm Speed", Math.abs(mArmSpeed));
        SmartDashboard.putNumber("Shooter Speed", mLastSpeed);
        mShooter.updateStatus();
        mArm.updateStatus();

        //  shooterSP = SmartDashboard.getNumber("Shooter SP");

//        SmartDashboard.putNumber("Echo Shooter SP",shooterSP);

//         SmartDashboard.putBoolean("DS 1", this.mDISW1.get());
//        SmartDashboard.putBoolean("DS 2", this.mDISW2.get());
//        SmartDashboard.putBoolean("DS 3", this.mDISW3.get());
//        SmartDashboard.putBoolean("Clear Jam", this.mClearJam);
//        SmartDashboard.putBoolean("Final Robot", Enums.IS_FINAL_ROBOT);

    }
}
