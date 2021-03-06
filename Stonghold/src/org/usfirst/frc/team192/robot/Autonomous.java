package org.usfirst.frc.team192.robot;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {
	public enum stepTypes {DRIVE_FORWARD, DRIVE_A_LITTLE_BIT_FORWARD, DRIVE_BACKWARD,
		DRIVE_A_LITTLE_BIT_BACKWARD, DRIVE_RIGHT, TURN_LEFT, TURN_RIGHT, TURN_LEFT_SHORT,
		TURN_RIGHT_SHORT, GRAB,	UNGRAB, LIFT, LIFT_A_LITTLE_BIT, LOWER, WAIT, STOP};

		private RobotControls _controls;
		private ArrayList<stepTypes> _steps;
		private int _currentStep;
		private Timer _stepTimer, _autoTimer;
		//private DigitalInput switches[];
		int autoType;

		final String _defaultAuto = "Do Nothing";
		final String _autoForwardOnly = "Go forward only and stop";
		final String _auto1 = "Defense 1";
		final String _auto2 = "Defense 2";
		final String _auto3 = "Defense 3";
		final String _auto4 = "Defense 4";
		final String _auto5 = "Defense 5";
		String _autoSelected;
		SendableChooser _chooser;

		public Autonomous (RobotControls controls)
		{
			
			_controls = controls;      	//passes the controls object reference
			_autoTimer = new Timer(); 	//create the overall autonmous timer
			_stepTimer = new Timer();	//Create the stepTimer

			
			/**
			 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
			 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
			 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
			 * below the Gyro
			 *
			 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
			 * If using the SendableChooser make sure to add them to the chooser code above as well.
			 */
			
			_chooser = new SendableChooser();
			_chooser.addDefault("Default Auto", _defaultAuto);
			_chooser.addObject(_autoForwardOnly, _autoForwardOnly);
			_chooser.addObject(_auto1, _auto1);
			_chooser.addObject(_auto2, _auto2);
			_chooser.addObject(_auto3, _auto3);
			_chooser.addObject(_auto4, _auto4);
			_chooser.addObject(_auto5, _auto5);
			SmartDashboard.putData("Auto choices", _chooser);	
			
			//put any properties here on the smart dashboard that you want to adjust from there.
/*			
			SmartDashboard.putNumber("forward time", 2.8); //1.4 old
			SmartDashboard.putNumber("forward speed", -.5); //-.8 old 
			SmartDashboard.putNumber("short forward time", 1.8);
			SmartDashboard.putNumber("short forward speed", -.5);
			SmartDashboard.putNumber("backward time", 2.0); //2.8 OLD
			SmartDashboard.putNumber("backward speed",0.5);
			SmartDashboard.putNumber("short backward time", .25); //.1 proto
			SmartDashboard.putNumber("short backward speed", .3);
			SmartDashboard.putNumber("turn time", 2.3); //2.3  //2 old
			SmartDashboard.putNumber("short turn time", 2); //2  //2 old
			SmartDashboard.putNumber("turn right speed", .25); //.5 proto
			SmartDashboard.putNumber("turn left speed", .25); //.5 proto
			SmartDashboard.putNumber("stop time", .1);
			SmartDashboard.putNumber("strafe time", 2.5);
			SmartDashboard.putNumber("strafe speed", 1);
			SmartDashboard.putNumber("strafe right speed", .1);
			SmartDashboard.putNumber("strafe left speed", -.1);
			SmartDashboard.putNumber("lift speed", -1.4); //-1 proto
			SmartDashboard.putNumber("lift time", 1.4);
			SmartDashboard.putNumber("short lift speed", -.3); //-.4 proto
			SmartDashboard.putNumber("short lift time", .4);
			SmartDashboard.putNumber("lower speed", 0); //.8 proto
			SmartDashboard.putNumber("lower time", 1.4); //1.2 proto
			SmartDashboard.putNumber("wait time", .4);
			SmartDashboard.putNumber("auto type", 0);

*/

			/*
			switches = new DigitalInput[3];

			for (int i = 0; i < switches.length; i++)
				switches[i] = new DigitalInput(Enums.AUTO_SWITCH_NUMBERS[i]);

			autoType = readSwitches();
			*/
			//setAuto();  
		}

		public void init ()
		{
//			autoType = (int) SmartDashboard.getNumber("auto type");

		 	_autoSelected = (String) _chooser.getSelected();
//			autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
			System.out.println("Auto selected: " + _autoSelected);			
			setAuto();  //build the steps for the selected autonomous
			
			_currentStep = 0;
			_autoTimer.start();
			_stepTimer.start();
		}

		private void setAuto ()
		{	
			//we are going to construct the steps needed for our autonomous mode
			_steps = new ArrayList<stepTypes>();
			switch (_autoSelected)
			{
			case _autoForwardOnly:
				_steps.add(stepTypes.DRIVE_A_LITTLE_BIT_FORWARD);
				break;
			case _auto1:
				_steps.add(stepTypes.TURN_RIGHT);
				break;
			case _auto2:
				_steps.add(stepTypes.GRAB);
				break;
			case _auto3:
				_steps.add(stepTypes.GRAB);
				_steps.add(stepTypes.TURN_RIGHT);
				_steps.add(stepTypes.DRIVE_FORWARD);
				_steps.add(stepTypes.TURN_LEFT_SHORT);
				_steps.add(stepTypes.STOP);
				break;
			case _auto4:
				_steps.add(stepTypes.GRAB);
				_steps.add(stepTypes.TURN_LEFT);
				_steps.add(stepTypes.DRIVE_FORWARD);
				_steps.add(stepTypes.TURN_RIGHT_SHORT);
				_steps.add(stepTypes.STOP);
				break;
			case _auto5:
				_steps.add(stepTypes.GRAB);
				_steps.add(stepTypes.DRIVE_RIGHT);
				_steps.add(stepTypes.STOP);
				break;
				
			default:
				_steps.add(stepTypes.STOP);

			}
		}
		
		
		public void execute ()
		{
			if (_autoTimer.get() > Enums.AUTONOMOUS_TIME || _currentStep >= _steps.size())
				_controls.getDrive().set(0, 0, 0, 0);
			else
			{
				switch (_steps.get(_currentStep))
				{
				case DRIVE_FORWARD:
					drive(SmartDashboard.getNumber("forward time"),
							SmartDashboard.getNumber("forward speed"),
							SmartDashboard.getNumber("forward speed"));
					break;
				case DRIVE_BACKWARD:
					drive(SmartDashboard.getNumber("backward time"),
							SmartDashboard.getNumber("backward speed"),
							SmartDashboard.getNumber("backward speed"));
					break;
				case DRIVE_A_LITTLE_BIT_FORWARD:
					drive(SmartDashboard.getNumber("short forward time"),
							SmartDashboard.getNumber("short forward speed"),
							SmartDashboard.getNumber("short forward speed"));
					break;
				case DRIVE_A_LITTLE_BIT_BACKWARD:
					drive(SmartDashboard.getNumber("short backward time"),
							SmartDashboard.getNumber("short backward speed"),
							SmartDashboard.getNumber("short backward speed"));
					break;
				case DRIVE_RIGHT:
					drive(SmartDashboard.getNumber("strafe time"),
							SmartDashboard.getNumber("strafe speed"),
							SmartDashboard.getNumber("strafe left speed"),
							SmartDashboard.getNumber("strafe speed"),
							SmartDashboard.getNumber("strafe right speed"));
					break;
				case TURN_RIGHT:
					drive(SmartDashboard.getNumber("turn time"),
							-SmartDashboard.getNumber("turn left speed"),
							SmartDashboard.getNumber("turn right speed"));
					break;
				case TURN_LEFT:
					drive(SmartDashboard.getNumber("turn time"),
							SmartDashboard.getNumber("turn left speed"),
							-SmartDashboard.getNumber("turn right speed"));
				case TURN_RIGHT_SHORT:
					drive(SmartDashboard.getNumber("short turn time"),
							-SmartDashboard.getNumber("turn left speed"),
							SmartDashboard.getNumber("turn right speed"));
					break;
				case TURN_LEFT_SHORT:
					drive(SmartDashboard.getNumber("short turn time"),
							SmartDashboard.getNumber("turn left speed"),
							-SmartDashboard.getNumber("turn right speed"));
					break;
				case GRAB:
					grab(1);
					break;
				case UNGRAB:
					grab(-1);
					break;
				case LIFT:
					lift(SmartDashboard.getNumber("lift time"), 
							SmartDashboard.getNumber("lift speed"));
					break;
				case LIFT_A_LITTLE_BIT:
					lift(SmartDashboard.getNumber("short lift time"), 
							SmartDashboard.getNumber("short lift speed"));
					break;
				case LOWER:
					lift(SmartDashboard.getNumber("lower time"), 
							SmartDashboard.getNumber("lower speed"));
					break;
				case WAIT:
					wait(SmartDashboard.getNumber("wait time"));
					break;
				case STOP:
					stop();
					break;
				}
			}

			_controls.updateStatus();
		}

		public void drive (double time, double left, double right)
		{
			if (_stepTimer.get() > time)
			{
				_controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
			{
				_controls.getDrive().set(0, left, 0, right);
			}
		}

		public void drive (double time, double leftX, double leftY, double rightX, double rightY)
		{
			if (_stepTimer.get() > time)
			{
				_controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
			{
				_controls.getDrive().set(leftX, leftY, rightX, rightY);
			}
		}

		public void grab (int grabSetpoint)
		{
//			_controls.getElevator().setGrabMotor(grabSetpoint);
			startNextStep();
		}

		public void lift (double time, double setPoint)
		{
			if (_stepTimer.get() > time)
			{
				startNextStep();
				//_controls.getElevator().setLiftSpeed(0);
			}
			else
			{
//				_controls.getElevator().setLiftPosition(setPoint);
				//_controls.getElevator().setLiftSpeed(setPoint);
			}
		}

		public void wait (double time)
		{
			if (_stepTimer.get() > time)
				startNextStep();
		}

		public void stop ()
		{
			if (_stepTimer.get() > SmartDashboard.getNumber("stop time"))
			{
				_controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
				for (int i = 0; i < 4; i++)
					_controls.getDrive().set(i, -_controls.getDrive().get(i));

		}

		public void startNextStep ()
		{
			_currentStep++;
			_stepTimer.reset();
		}	

}
