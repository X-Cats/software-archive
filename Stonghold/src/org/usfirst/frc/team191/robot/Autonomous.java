package org.usfirst.frc.team191.robot;

import java.util.ArrayList;
import org.usfirst.frc.team191.robot.Autonomous.stepTypes;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {
	public enum stepTypes {DRIVE_FORWARD, DRIVE_A_LITTLE_BIT_FORWARD, DRIVE_BACKWARD,
		DRIVE_A_LITTLE_BIT_BACKWARD, DRIVE_RIGHT, TURN_LEFT, TURN_RIGHT, TURN_LEFT_SHORT,
		TURN_RIGHT_SHORT, GRAB,	UNGRAB, LIFT, LIFT_A_LITTLE_BIT, LOWER, WAIT, STOP};
		private RobotControls controls;
		private ArrayList<stepTypes> steps;
		private int currentStep;
		private Timer stepTimer, autoTimer;
		private DigitalInput switches[];
		int autoType;

		public Autonomous (RobotControls controls)
		{
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
			SmartDashboard.putNumber("short lift time", .4


					);
			SmartDashboard.putNumber("lower speed", 0); //.8 proto
			SmartDashboard.putNumber("lower time", 1.4); //1.2 proto
			SmartDashboard.putNumber("wait time", .4);
			SmartDashboard.putNumber("auto type", 0);

			this.controls = controls;

			autoTimer = new Timer();
			stepTimer = new Timer();

			switches = new DigitalInput[3];

			for (int i = 0; i < switches.length; i++)
				switches[i] = new DigitalInput(Enums.AUTO_SWITCH_NUMBERS[i]);

			autoType = readSwitches();
			setAuto();
		}

		public void init ()
		{
			autoType = (int) SmartDashboard.getNumber("auto type");
			setAuto();

			currentStep = 0;
			autoTimer.start();
			stepTimer.start();
		}

		public void execute ()
		{
			if (autoTimer.get() > Enums.AUTONOMOUS_TIME || currentStep >= steps.size())
				controls.getDrive().set(0, 0, 0, 0);
			else
			{
				switch (steps.get(currentStep))
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

			controls.updateStatus();
		}

		private int readSwitches ()
		{
			int value = 0;
			for (int i = 0; i < switches.length; i++)
			{
				if (switches[i].get())
					value += Math.pow(2, i);
			}
			return value;
		}

		//private interface AutoMode
		//{
		//public abstract void execute (int currentStep);
		//}

		private void setAuto ()
		{	
			steps = new ArrayList<stepTypes>();
			switch (autoType)
			{
			case 1:
				steps.add(stepTypes.DRIVE_A_LITTLE_BIT_FORWARD);
				break;
			case 2:
				steps.add(stepTypes.TURN_RIGHT);
				break;
			case 3:
				steps.add(stepTypes.GRAB);
				break;
			case 4:
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.TURN_RIGHT);
				steps.add(stepTypes.DRIVE_FORWARD);
				steps.add(stepTypes.TURN_LEFT_SHORT);
				steps.add(stepTypes.STOP);
				break;
			case 5:
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.TURN_LEFT);
				steps.add(stepTypes.DRIVE_FORWARD);
				steps.add(stepTypes.TURN_RIGHT_SHORT);
				steps.add(stepTypes.STOP);
				break;
			case 6:
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.DRIVE_RIGHT);
				steps.add(stepTypes.STOP);
				break;
			case 7:
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.LIFT_A_LITTLE_BIT);
				steps.add(stepTypes.DRIVE_BACKWARD);
				steps.add(stepTypes.STOP);
				break;
			case 8: //don't use this one
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.LIFT);
				steps.add(stepTypes.DRIVE_A_LITTLE_BIT_FORWARD);
				steps.add(stepTypes.WAIT);
				steps.add(stepTypes.UNGRAB);
				steps.add(stepTypes.DRIVE_A_LITTLE_BIT_BACKWARD);
				steps.add(stepTypes.LOWER);
				steps.add(stepTypes.GRAB);
				steps.add(stepTypes.LIFT_A_LITTLE_BIT);
				steps.add(stepTypes.TURN_LEFT);
				steps.add(stepTypes.DRIVE_FORWARD);
				steps.add(stepTypes.WAIT);
				steps.add(stepTypes.TURN_LEFT);
				steps.add(stepTypes.STOP);
				break;

				//ArrayList<AutoMode> list = new ArrayList<AutoMode>();
				//
				//list.add(new AutoMode () {
				//public void execute (int currentStep)
				//{
				//	switch (currentStep)
				//	{
				//		case 0:
				//			drive(2.3, .5, .5);
				//	}
				//}
				//});

			}
			SmartDashboard.putNumber("auto type", autoType);
		}

		public void drive (double time, double left, double right)
		{
			if (stepTimer.get() > time)
			{
				controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
			{
				controls.getDrive().set(0, left, 0, right);
			}
		}

		public void drive (double time, double leftX, double leftY, double rightX, double rightY)
		{
			if (stepTimer.get() > time)
			{
				controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
			{
				controls.getDrive().set(leftX, leftY, rightX, rightY);
			}
		}

		public void grab (int grabSetpoint)
		{
//			controls.getElevator().setGrabMotor(grabSetpoint);
			startNextStep();
		}

		public void lift (double time, double setPoint)
		{
			if (stepTimer.get() > time)
			{
				startNextStep();
				//controls.getElevator().setLiftSpeed(0);
			}
			else
			{
//				controls.getElevator().setLiftPosition(setPoint);
				//controls.getElevator().setLiftSpeed(setPoint);
			}
		}

		public void wait (double time)
		{
			if (stepTimer.get() > time)
				startNextStep();
		}

		public void stop ()
		{
			if (stepTimer.get() > SmartDashboard.getNumber("stop time"))
			{
				controls.getDrive().set(0, 0, 0, 0);
				startNextStep();
			}
			else
				for (int i = 0; i < 4; i++)
					controls.getDrive().set(i, -controls.getDrive().get(i));

		}

		public void startNextStep ()
		{
			currentStep++;
			stepTimer.reset();
		}	

}
