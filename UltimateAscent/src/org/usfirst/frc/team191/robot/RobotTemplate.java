/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    private RobotControls mRobotControls;
    private Teleop mTeleop;
    private Autonomous m_autonomous;

    public void robotInit() {
        System.out.println("robotInit!");
        Watchdog.getInstance().setEnabled(true);

    }

    public void disabledInit() {
        Watchdog.getInstance().feed();

        //this function is called when the disabled button is pushed on the
        //driver station.
        if (mTeleop != null) {
            mTeleop.disable();
        }
        if (m_autonomous != null) {
            m_autonomous.disable();
        }
    }

    public void autonomousInit() {
        if (mRobotControls == null) {
            mRobotControls = new RobotControls();
            mRobotControls.robotInit();
        }

        if (m_autonomous == null) {
            m_autonomous = new Autonomous(mRobotControls);
        }

        try {
            m_autonomous.init();
        } catch (Exception ex) {
        }
    }

    public void testInit() {
        if (mRobotControls == null) {
            mRobotControls = new RobotControls();
        }
        mRobotControls.initTestMode();

    }

    /**
     * This function is called periodically during autonomous
     *
     */
    public void autonomousPeriodic() {
        Watchdog.getInstance().feed();
        try {
            m_autonomous.execute();
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
    }

    public void teleopInit() {
        if (mRobotControls == null) {
            mRobotControls = new RobotControls();
            mRobotControls.robotInit();
        }

        if (mTeleop == null) {
            mTeleop = new Teleop(mRobotControls);
        }

        mTeleop.init();
        Watchdog.getInstance().feed();
    }

    public void teleopPeriodic() {
        Watchdog.getInstance().feed();
        mTeleop.execute();
    }

    public void disabledPeriodic() {
        Watchdog.getInstance().feed();

    }

    public void testPeriodic() {
        Watchdog.getInstance().feed();
        mRobotControls.executeTestPeriodic();
        Timer.delay(0.1);
        //test routine
    }
}
