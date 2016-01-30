/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author mary farmer
 */
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.can.CANNotInitializedException;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class MecanumDrive extends RobotDrive {

    /**
     * The location of a motor on the robot for the purpose of driving
     */
    public static class MotorType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kFrontLeft_val = 0;
        static final int kFrontRight_val = 1;
        static final int kRearLeft_val = 2;
        static final int kRearRight_val = 3;
        /**
         * motor type: front left
         */
        public static final MotorType kFrontLeft = new MotorType(kFrontLeft_val);
        /**
         * motor type: front right
         */
        public static final MotorType kFrontRight = new MotorType(kFrontRight_val);
        /**
         * motor type: rear left
         */
        public static final MotorType kRearLeft = new MotorType(kRearLeft_val);
        /**
         * motor type: rear right
         */
        public static final MotorType kRearRight = new MotorType(kRearRight_val);

        private MotorType(int value) {
            this.value = value;
        }
    }
    private Encoders m_encoders;
    private double m_wheelSpeeds[] = new double[kMaxNumberOfMotors];
    private double m_maxRate = 0.0;
    private boolean m_useSpeedController = false;
    private boolean m_swapBackToFront;


    MecanumDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor,
            SpeedController frontRightMotor, SpeedController rearRightMotor) {
        super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_wheelSpeeds[i] = 0.0;
        }
        super.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        super.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        if (Enums.SWAP_BACK_TO_FRONT) {
            invertMotors();
        }

    }
    MecanumDrive(final int frontLeftMotor, final int rearLeftMotor, final int frontRightMotor, final int rearRightMotor) {
        super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);

        
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_wheelSpeeds[i] = 0.0;
        }
        super.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        super.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        if (Enums.SWAP_BACK_TO_FRONT ){
            invertMotors();         
        }

    }

    /**
     * Drive method for Mecanum wheeled robots.
     *
     * A method for driving with Mecanum wheeled robots. There are 4 wheels
     * on the robot, arranged so that the front and back wheels are toed in 45 degrees.
     * When looking at the wheels from the top, the roller axles should form an X across the robot.
     *
     * This is designed to be directly driven by joystick axes.
     *
     * @param x The speed that the robot should drive in the X direction. [-1.0..1.0]
     * @param y The speed that the robot should drive in the Y direction.
     * This input is inverted to match the forward == -1.0 that joysticks produce. [-1.0..1.0]
     * @param rotation The rate of rotation for the robot that is completely independent of
     * the translation. [-1.0..1.0]
     * @param gyroAngle The current angle reading from the gyro.  Use this to implement field-oriented controls.
     */
    public void setEncoders(Encoders newEncoders) {
        if (Enums.SPEED_CONTROLLER_ACTIVE){
            m_encoders = newEncoders;            
        }
    }

    public void setUseSpeedController(boolean useSpeedController) {
        m_useSpeedController = useSpeedController;
    }

    public boolean getUseSpeedController() {
        return m_useSpeedController;
    }

    public void setBackToFrontOrienation(boolean swapBackToFront){
        m_swapBackToFront = swapBackToFront;
    }
    public boolean getBackToFrontOrientation(){
        return m_swapBackToFront;
    }
    public void swapBackToFront() {
        m_swapBackToFront = !m_swapBackToFront;
        // need to invert the motors
        invertMotors();
    }

    public void disable(){
        
    }
    public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {
    
        if (false)
        {
//            System.out.println(x +" "+ y + " " + rotation);
            super.mecanumDrive_Cartesian(x, y, rotation, gyroAngle);
            return;
        }
            //System.out.println("MD "+x+", "+y+", "+rotation);
        //System.out.println("LF: "+m_encoders.getLFrate());

        //m_encoders.printRate();

        //System.out.println("LF: "+m_encoders.getLFrate()+", RF: "+m_encoders.getRFrate()+", LR: "+m_encoders.getLRrate()+", RR: "+m_encoders.getRRrate());

        // Negate y for the joystick.
        double xIn = x;
        double yIn = -y;
        if (!m_swapBackToFront) {
            rotation = -1 * rotation;
        }

        // Compenstate for gyro angle.
        double rotated[] = rotateVector(xIn, yIn, gyroAngle);
        xIn = rotated[0];
        yIn = rotated[1];

        double wheelSpeeds[] = new double[kMaxNumberOfMotors];
        wheelSpeeds[MotorType.kFrontLeft_val] = xIn + yIn - rotation;
        wheelSpeeds[MotorType.kFrontRight_val] = -xIn + yIn + rotation;
        wheelSpeeds[MotorType.kRearLeft_val] = -xIn + yIn - rotation;
        wheelSpeeds[MotorType.kRearRight_val] = xIn + yIn + rotation;

        normalize(wheelSpeeds);

        if (Enums.SPEED_CONTROLLER_ACTIVE) {
//            m_encoders.printCalcRates();
            if (m_useSpeedController) {
                wheelSpeeds[MotorType.kFrontLeft_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kFrontLeft_val], wheelSpeeds[MotorType.kFrontLeft_val], m_encoders.getLFrate());
                wheelSpeeds[MotorType.kFrontRight_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kFrontRight_val], wheelSpeeds[MotorType.kFrontRight_val], m_encoders.getRFrate());
                wheelSpeeds[MotorType.kRearLeft_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kRearLeft_val], wheelSpeeds[MotorType.kRearLeft_val], m_encoders.getLRrate());
                wheelSpeeds[MotorType.kRearRight_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kRearRight_val], wheelSpeeds[MotorType.kRearRight_val], m_encoders.getRRrate());
            }
        }

        byte syncGroup = (byte) 0x80;

        m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft_val] * m_invertedMotors[MotorType.kFrontLeft_val] * m_maxOutput, syncGroup);
        m_frontRightMotor.set(wheelSpeeds[MotorType.kFrontRight_val] * m_invertedMotors[MotorType.kFrontRight_val] * m_maxOutput, syncGroup);
        m_rearLeftMotor.set(wheelSpeeds[MotorType.kRearLeft_val] * m_invertedMotors[MotorType.kRearLeft_val] * m_maxOutput, syncGroup);
        m_rearRightMotor.set(wheelSpeeds[MotorType.kRearRight_val] * m_invertedMotors[MotorType.kRearRight_val] * m_maxOutput, syncGroup);

        m_wheelSpeeds[MotorType.kFrontLeft_val] = wheelSpeeds[MotorType.kFrontLeft_val];
        m_wheelSpeeds[MotorType.kFrontRight_val] = wheelSpeeds[MotorType.kFrontRight_val];
        m_wheelSpeeds[MotorType.kRearLeft_val] = wheelSpeeds[MotorType.kRearLeft_val];
        m_wheelSpeeds[MotorType.kRearRight_val] = wheelSpeeds[MotorType.kRearRight_val];

        if (m_isCANInitialized) {
            try {
                CANJaguar.updateSyncGroup(syncGroup);
            } catch (CANNotInitializedException e) {
                m_isCANInitialized = false;
            } catch (CANTimeoutException e) {
            }
        }

        if (m_safetyHelper != null) {
            m_safetyHelper.feed();
        }

        //reset the encoders here
       // m_encoders.reset();

    }
    
    public void mecanumDrive_Tank(Joystick leftJS, Joystick rightJS) {
        
        
        double wheelSpeeds[] = new double[kMaxNumberOfMotors];
        
        
        
        wheelSpeeds[MotorType.kFrontLeft_val] = -leftJS.getY() + leftJS.getX();
        wheelSpeeds[MotorType.kFrontRight_val] = -rightJS.getY() - rightJS.getX();
        wheelSpeeds[MotorType.kRearLeft_val] = -leftJS.getY() - leftJS.getX();
        wheelSpeeds[MotorType.kRearRight_val] = -rightJS.getY() + rightJS.getX();
        
        
        normalize(wheelSpeeds);
        
        if (Enums.SPEED_CONTROLLER_ACTIVE) {
//            m_encoders.printCalcRates();
            if (m_useSpeedController) {
                wheelSpeeds[MotorType.kFrontLeft_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kFrontLeft_val], wheelSpeeds[MotorType.kFrontLeft_val], m_encoders.getLFrate());
                wheelSpeeds[MotorType.kFrontRight_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kFrontRight_val], wheelSpeeds[MotorType.kFrontRight_val], m_encoders.getRFrate());
                wheelSpeeds[MotorType.kRearLeft_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kRearLeft_val], wheelSpeeds[MotorType.kRearLeft_val], m_encoders.getLRrate());
                wheelSpeeds[MotorType.kRearRight_val] = getFeedbackSpeed(m_wheelSpeeds[MotorType.kRearRight_val], wheelSpeeds[MotorType.kRearRight_val], m_encoders.getRRrate());
            }
        }

        byte syncGroup = (byte) 0x80;

        m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft_val] * m_invertedMotors[MotorType.kFrontLeft_val] * m_maxOutput, syncGroup);
        m_frontRightMotor.set(wheelSpeeds[MotorType.kFrontRight_val] * m_invertedMotors[MotorType.kFrontRight_val] * m_maxOutput, syncGroup);
        m_rearLeftMotor.set(wheelSpeeds[MotorType.kRearLeft_val] * m_invertedMotors[MotorType.kRearLeft_val] * m_maxOutput, syncGroup);
        m_rearRightMotor.set(wheelSpeeds[MotorType.kRearRight_val] * m_invertedMotors[MotorType.kRearRight_val] * m_maxOutput, syncGroup);

        m_wheelSpeeds[MotorType.kFrontLeft_val] = wheelSpeeds[MotorType.kFrontLeft_val];
        m_wheelSpeeds[MotorType.kFrontRight_val] = wheelSpeeds[MotorType.kFrontRight_val];
        m_wheelSpeeds[MotorType.kRearLeft_val] = wheelSpeeds[MotorType.kRearLeft_val];
        m_wheelSpeeds[MotorType.kRearRight_val] = wheelSpeeds[MotorType.kRearRight_val];

        if (m_isCANInitialized) {
            try {
                CANJaguar.updateSyncGroup(syncGroup);
            } catch (CANNotInitializedException e) {
                m_isCANInitialized = false;
            } catch (CANTimeoutException e) {
            }
        }

        if (m_safetyHelper != null) {
            m_safetyHelper.feed();
        }

        //reset the encoders here
       // m_encoders.reset();

    }
    
    public int getAccumulator(){
        return m_encoders.getAccumulator();
    }

    public void resetAccumulator(){
        m_encoders.resetAccumulator();
    }

    private double getFeedbackSpeed(double lastValue, double currentValue, double rate) {
        double retVal = currentValue;
        double maxRate = 6000;  // start out at 6000
        double stepCorrection = 0.15;
        double deadband = 0.02;
        double effectiveValue;

        if (m_swapBackToFront) {
            rate = -1 * rate;
        }

        m_maxRate = Math.max(m_maxRate, rate);  // reestimate the max based on these encoders, they may have changed
        effectiveValue = rate / maxRate;

        if (Math.abs(lastValue - currentValue) <= deadband || currentValue < deadband) {
            retVal = currentValue;
        } else {

            //the rate cannot exceed 1.00
            retVal = Math.min((currentValue - effectiveValue) * stepCorrection + lastValue, 1.00);
           // System.out.println("NV " + retVal + " =  CV: " + currentValue + ", EV: " + effectiveValue + ", LV: " + lastValue);

        }


        return retVal;
    }
    /*
    public double getDistance(){
    return 0.0;
    }
     */

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

    public void doNotInvertMotors() {
        setInvertedMotor(RobotDrive.MotorType.kFrontLeft, m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kFrontRight, m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kRearLeft, m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kRearRight, m_swapBackToFront);
    }

    public void invertMotors() {

        setInvertedMotor(RobotDrive.MotorType.kFrontLeft,  m_swapBackToFront || !Enums.MECANUM_DRIVE_ACTIVE);
        setInvertedMotor(RobotDrive.MotorType.kFrontRight, ! m_swapBackToFront);
        setInvertedMotor(RobotDrive.MotorType.kRearLeft,  m_swapBackToFront || !Enums.MECANUM_DRIVE_ACTIVE);
        setInvertedMotor(RobotDrive.MotorType.kRearRight, ! m_swapBackToFront);

    }
}