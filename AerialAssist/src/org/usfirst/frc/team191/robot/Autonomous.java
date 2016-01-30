/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author mary farmer
 */
public class Autonomous {

    public static final double AUTONOMOUS_MODE_VOLTAGE = 110;
    public static final double FORWARD_SPEED = 0.55;
    public static final double TURN_SPEED = 0.4;
    public static final double KICK_TURN_SPEED = 0.3;
    public static final double FIRST_MOVE_DISTANCE_ZONES_1_2 = 2;
    public static final double FIRST_MOVE_DISTANCE_ZONES_3 = 4.5;
    private static final int STEP_FORWARD = 0;
    private static final int STEP_INCRSHOOTER = 1;
    private static final int STEP_STOPSHOOTER = 2;
    private static final int STEP_STARTSHOOTER = 3;
    private static final int STEP_DELAY = 4;
    private static final int STEP_STARTELEVATOR = 5;
    private static final int STEP_ARMDOWN = 6;
    private static final int STEP_STROKE_JAGUAR = 7;
    private static final int STEP_STARTUP = 8;
    private static final int STEP_TIMEOUT = 9;
    private static final int STEP_TARGET = 10;
    private static final int STEP_FORWARDTIME = 11;
    private static final int STEP_MOVETURRET = 12;
    private static final int STEP_REVERSE_ACQ = 13;
    private static final int STEP_DELAY1 = 14;
    private static final int STEP_DELAY2 = 15;
    private static final int STEP_SHOOTER_HIGH = 20;
    private static final int STEP_SHOOTER_LOW = 21;
    private static final int STEP_SHOOTER_START = 22;
    private static final int STEP_SHOOTER_SHOOT = 23;
    private static final int STEP_ARM_UP = 24;
    private static final int STEP_ARM_DOWN = 25;
    private static final double PAUSE_TIME = 0.1;
    private double AUTONOMOUS_COMPLETE_TIME = 15;
    //********************************************
    //CALIBRATE THIS LINE TO GET DISTANCE FOR ENCODER
    // THE SMALLER THIS NUMBER THE SMALLER THE DISTANCE PER PULSE
    // how many times it takes for the gear to rotate one wheel
//    private static final double LEFT_AVERAGE = 29.56236;
    ///////////////////////////////////////////////////////////////////////////////////////
    private RobotControls mRobotControls;
    private int[] m_steps;
    private int m_stepNumber;
    private Timer m_Timer;
    private Timer m_stepTimer;
    private double m_delay1 = 0.0;
    private double m_delay2 = 0.0;
    private double m_delay = 1.0;
    private double m_forwardTime = 0.0;
    private double mSpeed = 0.50;
    private boolean m_complete;
    private double mShooterSpeed = 0.0;
    private boolean mLoggingEnabled = false;
    private Jaguar mCalibrationJaguar;
    private boolean mStrokeHi = false;
    private DriverStationEnhancedIO mdsIO;
    private double mTurretSP= 0;


    Autonomous(RobotControls robControls) {
        this.mRobotControls = robControls;
        System.out.println("Autonomous created!");
    }

    public void disable() {
        mRobotControls.disable();

    }

    // call this function once to initialize the autonomous process
    void init() {
        System.out.print("Autonomous Init: ");

        //This is the enhanced IO on the driverstation
        mdsIO = DriverStation.getInstance().getEnhancedIO();

        int m_autonomousMode = 0;
        boolean sw1 = mRobotControls.getDSValue(1);
        boolean sw2 = mRobotControls.getDSValue(2);
        boolean sw3 = mRobotControls.getDSValue(3);

        /*  Autonomous modes
         * Switches Mode    Comment
         * 0 0 0 -  0       OFF
         * 1 0 0 -  1       Shoot (no autotarget)
         * 1 1 0 -  2       Target & Shoot
         * 1 0 1 -  3       tilt bridge for balls
         * 1 1 1 -  4       move forward and shoot
         * 0 0 1 -  5       move backwards and feed balls
         * 0 1 1 -  6       Step the shooter for encoder profile
         * 0 1 0 -  7       Jaguar Calibration mode
         */


        AUTONOMOUS_COMPLETE_TIME = 15;
        m_complete = false;
        m_stepNumber = -1;
        m_stepTimer = new Timer();
        m_Timer = new Timer();
//        mRobotControls.getShooter().getAcquisition().setShooterElevatorTimeout(0.5);

        System.out.println(sw1 + " - " + sw2 + " - " + sw3);

        if (!sw1 && !sw2 && !sw3)
        {
            m_autonomousMode = 0 ; //do nothing
        } else  if (sw1 && ! sw2 && !sw3){
            m_autonomousMode = 1;  // shoot without autotarget
        } else if (sw1 && sw2 & !sw3) {
            m_autonomousMode = 2; // auto target and shoot
        } else if (sw1 && !sw2 & sw3) {
            m_autonomousMode = 3; //move forward to get all the balls
        } else if (sw1 && sw2 & sw3) {
            m_autonomousMode = 4; //move forward to basket and shoot
        } else if (!sw1 && !sw2 & sw3) {
            m_autonomousMode = 5; //feed balls to another robot
        } else if (!sw1 && sw2 &&  sw3) {
           // m_autonomousMode = 6; // mapping of the encoder values
        } else if (!sw1 && sw2 && !sw3){
            //m_autonomousMode = 7;  //jaguar mode
        }

        m_autonomousMode = 9;


        //This timer for autonomous should not exceed 15 seconds
        m_Timer.start();
        switch (m_autonomousMode) {
            // Zone 1
            case 0:   //do nothing
                //do nothing
                m_steps = new int[7];
                m_steps[0] = STEP_TIMEOUT;
                break;

            case 1: //shoot
                m_delay = 4;
                m_steps = new int[5];
                if (Enums.IS_FINAL_ROBOT) {
                    mShooterSpeed = Enums.AUTO_SHOOTER_SPEED_FINAL;
                } else
                {
                    mShooterSpeed = Enums.AUTO_SHOOTER_SPEED_PROTO;
                }


                m_steps[0] = STEP_STARTSHOOTER;
                m_steps[1] = STEP_DELAY;
                m_steps[2] = STEP_STARTELEVATOR; //this shoots the ball
                m_steps[3] = STEP_DELAY;
                m_steps[4] = STEP_STARTELEVATOR; //this shoots the ball

                break;
            case 2:  //target and shoot
                m_delay = 4;
                m_steps = new int[5];

                if (Enums.IS_FINAL_ROBOT) {
                    mShooterSpeed = Enums.AUTO_SHOOTER_SPEED_FINAL;
                } else
                {
                    mShooterSpeed = Enums.AUTO_SHOOTER_SPEED_PROTO;
                }

                m_steps[0] = STEP_TARGET;
                m_steps[1] = STEP_DELAY;
                m_steps[2] = STEP_STARTELEVATOR; //this shoots the ball
                m_steps[3] = STEP_DELAY;
                m_steps[4] = STEP_STARTELEVATOR; //this shoots the ball

                break;
            case 3:  //move to steal the balls
                AUTONOMOUS_COMPLETE_TIME = 15;
                m_forwardTime = 3.5;
                mSpeed = -0.50;
                m_steps = new int[2];

                m_steps[0] = STEP_FORWARDTIME;
                m_steps[1] = STEP_ARMDOWN;

                break;
            case 4: //move forward and shoot
                AUTONOMOUS_COMPLETE_TIME = 15;
                m_forwardTime = 2.1;
                mSpeed = -0.7;
                mShooterSpeed = 0.51;
                mTurretSP =  -0.65;
                m_delay1 = 4.0;
                m_delay2 = 5.0;

                m_steps = new int[8];

                m_steps[0] = STEP_STARTSHOOTER;
                m_steps[1] = STEP_MOVETURRET;
                m_steps[2] = STEP_FORWARDTIME;
                m_steps[3] = STEP_DELAY1;
                m_steps[4] = STEP_STARTELEVATOR; //this shoots the ball
                m_steps[5] = STEP_DELAY2;
                m_steps[6] = STEP_STARTELEVATOR; //this shoots the ball
                m_steps[7] = STEP_TIMEOUT; //this shoots the ball

                break;
            case 5: // feed balls to another robot
                AUTONOMOUS_COMPLETE_TIME = 15;
                m_delay = 6.0;

                m_steps = new int[2];

                m_steps[0] = STEP_DELAY;
                m_steps[1] = STEP_REVERSE_ACQ; //this spits the balls out

                break;
            case 6:
                //This is for mapping the encoder on the shooter
                AUTONOMOUS_COMPLETE_TIME = 160;
                m_delay = 15.0;

                mShooterSpeed = 0;
//                mRobotControls.getShooter().setSpeedandEngage(mShooterSpeed);


                m_steps = new int[21];
                m_steps[0] = STEP_INCRSHOOTER; //10%
                m_steps[1] = STEP_DELAY;
                m_steps[2] = STEP_INCRSHOOTER; //20%
                m_steps[3] = STEP_DELAY;
                m_steps[4] = STEP_INCRSHOOTER; //30%
                m_steps[5] = STEP_DELAY;
                m_steps[6] = STEP_INCRSHOOTER; //40%
                m_steps[7] = STEP_DELAY;
                m_steps[8] = STEP_INCRSHOOTER; //50%
                m_steps[9] = STEP_DELAY;
                m_steps[10] = STEP_INCRSHOOTER; //60%
                m_steps[11] = STEP_DELAY;
                m_steps[12] = STEP_INCRSHOOTER; //70%
                m_steps[13] = STEP_DELAY;
                m_steps[14] = STEP_INCRSHOOTER; //80%
                m_steps[15] = STEP_DELAY;
                m_steps[16] = STEP_INCRSHOOTER; //90%
                m_steps[17] = STEP_DELAY;
                m_steps[18] = STEP_INCRSHOOTER; //100%
                m_steps[19] = STEP_DELAY;
                m_steps[20] = STEP_STOPSHOOTER;
                break;
            case 7:
                AUTONOMOUS_COMPLETE_TIME = 15;
                m_delay = 1.0;

//                mCalibrationJaguar = mRobotControls.getShooter().getShooterJaguar();

                m_steps = new int[20];
                m_steps[0] = STEP_STROKE_JAGUAR; //up
                m_steps[1] = STEP_DELAY;
                m_steps[2] = STEP_STROKE_JAGUAR; //down
                m_steps[3] = STEP_DELAY;
                m_steps[4] = STEP_STROKE_JAGUAR; //up
                m_steps[5] = STEP_DELAY;
                m_steps[6] = STEP_STROKE_JAGUAR; //down
                m_steps[7] = STEP_DELAY;
                m_steps[8] = STEP_STROKE_JAGUAR; //up
                m_steps[9] = STEP_DELAY;
                m_steps[10] = STEP_STROKE_JAGUAR; //down
                m_steps[11] = STEP_DELAY;
                m_steps[12] = STEP_STROKE_JAGUAR; //up
                m_steps[13] = STEP_DELAY;
                m_steps[14] = STEP_STROKE_JAGUAR; //down
                m_steps[15] = STEP_DELAY;
                m_steps[16] = STEP_STROKE_JAGUAR; //up
                m_steps[17] = STEP_DELAY;
                m_steps[18] = STEP_STROKE_JAGUAR; //down;
                m_steps[19] = STEP_DELAY;
                break;
            case 8:
                //This is the final production feed
                AUTONOMOUS_COMPLETE_TIME = 120;
                m_delay1 = 1.0;
                m_delay2 = 4.5;

                mShooterSpeed = 0;
                m_steps = new int[6];
//                m_steps[0] = STEP_SHOOTER_LOW; //10%
                m_steps[0] = STEP_DELAY2;
                m_steps[1] = STEP_SHOOTER_HIGH; //20%
                m_steps[2] = STEP_DELAY1;
                m_steps[3] = STEP_SHOOTER_LOW; //30%
                m_steps[4] = STEP_DELAY1;
                m_steps[5] = STEP_STOPSHOOTER; //40%
//                m_steps[7] = STEP_DELAY;
//                m_steps[8] = STEP_SHOOTER_LOW; //50%
//                m_steps[9] = STEP_DELAY;
//                m_steps[10] = STEP_SHOOTER_HIGH; //60%
//                m_steps[11] = STEP_DELAY;
//                m_steps[12] = STEP_SHOOTER_LOW; //70%
//                m_steps[13] = STEP_DELAY;
//                m_steps[14] = STEP_SHOOTER_HIGH; //80%
//                m_steps[15] = STEP_DELAY;
//                m_steps[16] = STEP_SHOOTER_LOW; //90%
//                m_steps[17] = STEP_DELAY;
//                m_steps[18] = STEP_SHOOTER_HIGH; //100%
//                m_steps[19] = STEP_DELAY;
//                m_steps[20] = STEP_SHOOTER_LOW;
                break;
            case 9:
                //runs an actual autonomous foir competition
                AUTONOMOUS_COMPLETE_TIME = 15;
                m_delay1 = 3.8;
                m_delay2 = 2.8;
                
                m_steps = new int[8];
                m_steps[0] = STEP_SHOOTER_START;
                m_steps[1] = STEP_DELAY1;
                m_steps[2] = STEP_SHOOTER_SHOOT;
                m_steps[3] = STEP_DELAY2;
                m_steps[4] = STEP_SHOOTER_SHOOT;
                m_steps[5] = STEP_DELAY2;
                m_steps[6] = STEP_SHOOTER_SHOOT;
                m_steps[7] = STEP_DELAY2;
                m_steps[8] = STEP_DELAY2;
                
                break;
            case 10:
                AUTONOMOUS_COMPLETE_TIME = 60;
                m_delay = 2.0;
                
                m_steps = new int[10];
                m_steps[0] = STEP_ARM_UP;
                m_steps[1] = STEP_ARM_DOWN;
                m_steps[2] = STEP_ARM_UP;
                m_steps[3] = STEP_ARM_DOWN;
                m_steps[4] = STEP_ARM_UP;
                m_steps[5] = STEP_ARM_DOWN;
                
                break;

            default:
                break;
        }
        System.out.println("Step number at init: "+m_stepNumber);

    }

    //This function is called in a loop continuoutsly
    void execute() {
        updateStatus();
//        mRobotControls.moveTurretToSP(); //this checks to see if we are at setpoint

        int currentStep = 0;
        Timer.delay(0.005); // this should get rid of the annoying message around Output not updated often enough

        //check the state of the elevator/arm assembly
               
        try {
            //Since execute is called continuously from the robot templte

            if (m_stepNumber == -1) {
                startNextStep();
            }
            if (m_complete == false && (m_Timer.get() >= AUTONOMOUS_COMPLETE_TIME || m_stepNumber == m_steps.length)) {
                m_complete = true;
                drive(0);
                stopShooter();
                System.out.println("Autonomous Complete");
            }
            if (m_stepNumber < m_steps.length && m_Timer.get() < AUTONOMOUS_COMPLETE_TIME) {
                currentStep = m_steps[m_stepNumber];

                System.out.println("At Step: "+m_stepNumber+", "+currentStep);
                switch (currentStep) {
                    case STEP_FORWARD:
                        //moveForTime(3.55, 0.45);
                        moveForAccumulator(10400, 0.25, 9.5); //8.92 9900
                        //moveDistance(10.0);
                        break;
                    case STEP_FORWARDTIME:
                        moveForTime( m_forwardTime,mSpeed); //time, speed
                        break;
                    case STEP_TIMEOUT:
                        m_delay = 15.0;
                        delay();
                        break;
                    case STEP_DELAY1:
                        m_delay = m_delay1;
                        delay();
                        break;
                    case STEP_DELAY2:
                        m_delay = m_delay2;
                        delay();
                        break;
                    case STEP_DELAY:
                        delay();
                        break;
                    case STEP_TARGET:
                        target();
                        break;
                    case STEP_STARTELEVATOR:
                        startElevator();
                        break;
                    case STEP_REVERSE_ACQ:
                        reverseElevator();
                        break;
                    case STEP_STARTSHOOTER:
                        startShooter();
                        break;
                    case STEP_STROKE_JAGUAR:
                        strokeJaguar();
                        break;
                    case STEP_INCRSHOOTER:
                        incrementShooter();
                        break;
                    case STEP_STOPSHOOTER:
                        stopShooter();
                        break;
                    case STEP_SHOOTER_LOW:
                        mShooterSpeed = -1.0;
                        startShooter();
                        break;
                    case STEP_SHOOTER_HIGH:
                        mShooterSpeed = 1.1;
                        startShooter();
                        break;
                    case STEP_MOVETURRET:
                        moveTurret(mTurretSP);
                        break;
                    case STEP_SHOOTER_START:
                        if (Enums.IS_FINAL_ROBOT){
                            mShooterSpeed = Enums.SHOOTER_FINAL_DEF_SPEED;

                        } else {
                            mShooterSpeed = Enums.SHOOTER_PROT0_DEF_SPEED;
                            
                        }
                        startShooter();
                        break;
                    case STEP_SHOOTER_SHOOT:
//                        mRobotControls.getShooter().feedFrisbee();
                        startNextStep();
                        break;
                    case STEP_ARM_UP:
//                        mRobotControls.getArm().setSpeed(.4);
//                        if (mRobotControls.getArm().isAtTop())
                            startNextStep();
                        break;
                    case STEP_ARM_DOWN:
//                        mRobotControls.getArm().setSpeed(-.4);
//                        if (mRobotControls.getArm().isAtBottom())
                            startNextStep();
                        break;
                    default:
                        System.out.println("Case not covered "+currentStep);
                        break;
                }
            }
        } catch (Exception ex) {
            //do something with the error
        }
//        mRobotControls.moveTurretToSP();
    }

    private double getScaledIO(double val) {
    //    return (1.0 - 2 * (Enums.ENH_IO_ANALOG_MAX - val) / Enums.ENH_IO_ANALOG_MAX);
        return 0;
    }

    private void drive(double speed) {
        mRobotControls.driveAtSpeed(speed);
    }

    private void strokeJaguar(){
        if (mStrokeHi) {
            mCalibrationJaguar.set(-1);
        } else
        {
            mCalibrationJaguar.set(1.0);
        }
        startNextStep();
    }

    private void delay() {

        if (m_stepTimer.get() < m_delay) {
            System.out.println("Delay.. "+m_stepTimer.get() + " target: "+m_delay );
        } else {
            startNextStep();
        }
    }

    private void target() {
        //mRobotControls.getShooter().setSpeed(0.65);
        try
        {
//            mRobotControls.getShooter().setSpeedandEngage(mShooterSpeed);
        }catch (Exception ex){

        }

//        mRobotControls.target(true,false,false);
        startNextStep();
    }

    private void startElevator() {
        //the intent of this step type is to be raising the elevator as we are moving forward
        System.out.println("Start Elevator");
//        mRobotControls.getShooter().getAcquisition().elevatorRollIn();
//        mRobotControls.getShooter().getAcquisition().acqRollIn();
        startNextStep();
    }

        private void reverseElevator() {
        //the intent of this step type is to be raising the elevator as we are moving forward
        System.out.println("Reverse Elevator");
//        mRobotControls.getShooter().getAcquisition().elevatorRollOutSlow();
//        mRobotControls.getShooter().getAcquisition().acqRollOutSlow();
        //startNextStep();
    }

    private void moveTurret(double sp)
    {
        System.out.println("Setting Turret to "+sp);
//        mRobotControls.setTurretSP(mTurretSP);
        startNextStep();
    }

    private void startShooter() {
        //the intent of this step type is to be raising the elevator as we are moving forward
        System.out.println("Start Shooter");
        try
        {
//            mRobotControls.getShooter().setSpeedandEngage(mShooterSpeed);
            
//            mRobotControls.getShooter().setSpeedandEngage(mShooterSpeed);
        }catch (Exception ex){

        }
        startNextStep();
    }

    private void stopShooter() {
        //the intent of this step type is to be raising the elevator as we are moving forward
        System.out.println("Stop Shooter");
//        mRobotControls.getShooter().setSpeedandEngage(0.0);
        startNextStep();
    }
    private void stopElevator(){
        System.out.println("Stop Elevator");
//        mRobotControls.getShooter().getAcquisition().stopAcq();
//        mRobotControls.getShooter().getAcquisition().stopElevator();
    }

    private void incrementShooter() {
        //the intent of this step type is to be raising the elevator as we are moving forward
        System.out.println("Incrementing Shooter");
        mShooterSpeed += 0.10;
        System.out.println("Incrementing Shooter to " + mShooterSpeed);
//        mRobotControls.getShooter().setSpeedandEngage(mShooterSpeed);
        startShooter();
    }

    private void moveForAccumulator(int accumulator, double speed, double time) {
        // time acts as an upper limit for this function so that if the accumulator is off we stop

//        if (Math.abs(mRobotControls.getXCatDrive().getAccumulator()) < accumulator) {
//            if (m_stepTimer.get() < time) {
//                if (Enums.IS_FINAL_ROBOT) {
//                    drive(speed); //0.17
//                } else {
//                    drive(speed);
//                }
//
//            } else {
//                System.out.println("Accumulator Timeout!! " + mRobotControls.getXCatDrive().getAccumulator());
//                drive(0);
//                startNextStep();
//            }
//        } else {
//            System.out.println("Accumulator Complete!! " + mRobotControls.getXCatDrive().getAccumulator());
//            drive(0);
//            startNextStep();
//        }
    }

    private void moveForTime(double time, double speed) {
        if (m_stepTimer.get() < time) {
            //outputToUserScreen("moving, "+m_stepTimer.get());
            drive(speed);
        } else {
            //System.out.println("Accumulator: " + mRobotControls.getXCatDrive().getAccumulator());
            System.out.println("Finished driving");
            drive(0);
            startNextStep();
        }
    }

    private void startNextStep() {
        Timer.delay(PAUSE_TIME);
        m_stepTimer.stop();
        m_stepNumber++;
        outputToUserScreen("Starting Step: " + m_stepNumber + " System time: " + m_Timer.get());
        m_stepTimer.reset();
        m_stepTimer.start();
//        m_branch = 0;
//        mRobotControls.getXCatDrive().resetAccumulator();
//        m_backupTime = 0.0;
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


    private void updateStatus() {
        if (mCalibrationJaguar != null){
//            SmartDashboard.putDouble("Jag SP", mCalibrationJaguar.get());
        }
        mRobotControls.updateStatus();
    }
}
