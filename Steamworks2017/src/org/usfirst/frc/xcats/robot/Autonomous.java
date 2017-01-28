package org.usfirst.frc.xcats.robot;
//package org.usfirst.frc.team191.robot;
//package org.usfirst.frc.team191.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	private RobotControls _controls;
	private ArrayList<AutonomousStep> _steps;

	private int _currentStep;
	private float _initialYaw;
	private double _totalAutoTime = Enums.AUTONOMOUS_TIME;
	private AutonomousStep _currentAutoStep;
	private Timer _stepTimer = new Timer();
	private Timer _autoTimer = new Timer();

	private boolean _isExecuting = false;
	private boolean _cancelExecution = false;

	private static final double FIRST_LEG_DISTANCE = 73.0;	// this is the distance from the auto line to the lip of the defenses	

	final String _defaultAuto = "Do Nothing";
	final String _autoForwardOnly = "Go forward only and stop";
	final String _auto1 = "Defense 1";
	final String _auto2 = "Defense 2";
	final String _auto3 = "Defense 3";
	final String _auto4 = "Defense 4";
	final String _auto5 = "Defense 5";
	final String _autoReadFile = "TextFile read";
	final String _autoTestSpeed = "Run 3 sec at input speed";
	final Navx _navx= new Navx();

	public Autonomous (RobotControls controls)
	{

		_controls = controls;      	//passes the controls object reference


		/**
		 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
		 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
		 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
		 * below the Gyro
		 *
		 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
		 * If using the SendableChooser make sure to add them to the chooser code above as well.
		 */




		SmartDashboard.putNumber(_autoTestSpeed, 0.5); 			//this is the speed to run the auto calibration test
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
		System.out.println("auto constructor");

	}

	public Autonomous (RobotControls controls, ArrayList<AutonomousStep> mysteps, double totalTime){
		// this "autonomous" instantiation can be called from teleop to do a set of steps
		_controls = controls;
		_steps = mysteps;
		_totalAutoTime = totalTime;
		init();	
	}

	public void setSteps(ArrayList<AutonomousStep> mysteps){
		_steps = mysteps;
		init();

	}
	public void init ()
	{
		System.out.println("auto init");


		//build the steps for the selected autonomous
		setAuto();
		_initialYaw = _controls.getNavx().getYaw();
		_currentStep = 0;
		_currentAutoStep = null;
		_autoTimer.start();
		_stepTimer.start();
		_isExecuting = false;
		_cancelExecution = false;
		this.updateStatus();


	}

	public void disable(){
		_steps = null;

		updateStatus();

	}
	private void setAuto ()
	{	

		//we are going to construct the steps needed for our autonomous mode
		int choice=1;
		String caseName;
		_steps =  new ArrayList<AutonomousStep>();
		switch (choice) {
		case 1: {
			caseName="Middle Gear";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,8.3)); //assuming 99.64 inches
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.GEAR,"Place Gear",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
		}
		break;
		case 2: {
			caseName="Left Gear";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,8.3));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn 60",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,3.75));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.GEAR,"Place Gear",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
		}
		case 3: {
			caseName="Right Gear";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,8.3));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn 60",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,3.75));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.GEAR,"Place Gear",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
		}
		default: {
			caseName="Do Nothing";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
		}
		}


		System.out.println("setAuto");
	}

	//	private void addPortcullisSteps(){
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG ) +35)/12.0));
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.6, 0.6, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
	//	}

	private void  addDefenseSteps() {


	}


	public boolean isExecuting(){
		return _isExecuting;
	}
	public void cancelExecution(){
		_cancelExecution = true;
	}

	public void execute ()
	{
		System.out.println("auto execute");

		double cTime=0;
		int direction=1;

		_currentAutoStep = _steps.get(_currentStep);

		if (_autoTimer.get() > _totalAutoTime || _currentStep >= _steps.size() || _cancelExecution){
			_controls.getDrive().set(0, 0, 0, 0);
			_isExecuting = false;	
			disable();
		}
		else
		{
			_isExecuting = true;
			//switch (_steps.get(_currentStep))
			switch (_currentAutoStep.stepType)
			{
			case DRIVE:
				drive(_currentAutoStep.stepTime,_currentAutoStep.leftSpeed,_currentAutoStep.rightSpeed);
				break;

			case DRIVE_DISTANCE:

				if (Enums.IS_FINAL_ROBOT)					
					cTime = _currentAutoStep.distance/(4.92*_currentAutoStep.leftSpeed + 0.01);
				else
					cTime = _currentAutoStep.distance/(6.892*_currentAutoStep.leftSpeed - 1.038);

				drive(cTime,_currentAutoStep.leftSpeed,_currentAutoStep.rightSpeed);
				break;

			case ROTATE:
				//float deltaYaw;
				double  speed =0.25;
				double tolerance=0.1;

				//deltaYaw = _initialYaw + _controls.getNavx().getYaw();
				//SmartDashboard.putNumber("deltaYaw", deltaYaw);
				// 
				direction = (_currentAutoStep.distance > 0 ? -1 : 1);
				speed = direction * speed;	
				_controls.getDrive().set(speed, speed, -speed, -speed);


				if(Math.abs(_controls.getNavx().getYaw()) > Math.abs(_currentAutoStep.distance)){
					SmartDashboard.putNumber("Auto Yaw", _controls.getNavx().getYaw());
					speed=-speed/2;
					_controls.getDrive().set(speed, speed, -speed, -speed);
					if(Math.abs(_controls.getNavx().getYaw())-Math.abs(_currentAutoStep.distance)<tolerance){
						startNextStep();
					}
				}

				break;

			case GEAR: {
				startNextStep();
			}
			break;
			case GRAB:
				break;

			case UNGRAB:
				break;


			case WAIT:
				wait(_currentAutoStep.stepTime);
				break;

			case STOP:
				stop();
				break;


			}
		}

		this.updateStatus();
		_controls.updateStatus();
	}

	private void updateStatus(){

		SmartDashboard.putNumber("Step Count", _steps.size());
		SmartDashboard.putString("Current Command", this._currentStep + " " + _currentAutoStep.name  + "\n " + _currentAutoStep.stepTime);

	}
	public void drive (double time, double left, double right)
	{
		float deltaYaw;
		deltaYaw = _initialYaw - _controls.getNavx().getYaw();
		double offset;

		SmartDashboard.putNumber("currentYaw", _initialYaw);
		SmartDashboard.putNumber("deltaYaw", deltaYaw);
		if (left == right){
			offset = Math.abs(deltaYaw);
			if(offset > .1){
				offset = .1;
			}
			if(deltaYaw > 0){
				left = left * (1+offset);
				right = right * (1-offset);
			}else{
				left = left * (1-offset);
				right = right * (1+offset);
			}
		}

		if (_stepTimer.get() > time)
		{
			_controls.getDrive().set(0, 0, 0, 0);
			startNextStep();
		}
		else
		{
			_controls.getDrive().set(-left, -left, -right, -right);
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








	public void wait (double time)
	{
		if (_stepTimer.get() > time)
			startNextStep();
	}



	public void stop ()
	{
		_controls.getDrive().set(0, 0, 0, 0);
		startNextStep();
	}

	public void startNextStep ()
	{
		_navx.zeroYaw();
		SmartDashboard.putNumber("Starting Yaw", _controls.getNavx().getYaw() );
		_currentStep++;
		_stepTimer.reset();
	}	

	private void ReadAutoFile(){
		BufferedReader br = null;

		try {

			String sCurrentLine;
			String temp[];	
			String sComment = "\\\\";
			String sSteps = "";
			AutonomousStep newStep;


			//			br = new BufferedReader(new FileReader("C:\\autonomous\\testing.txt"));
			br = new BufferedReader(new FileReader("/home/lvuser/autonomous.txt"));

			while ((sCurrentLine = br.readLine()) != null) {				
				if (sCurrentLine.startsWith(sComment)){
					System.out.println("Comment: ingoring -> "+ sCurrentLine);
				} else{
					System.out.println(sCurrentLine);
					sSteps = sCurrentLine + "\n" + sSteps; 
					temp = sCurrentLine.split(",");
					newStep =  new AutonomousStep();
					newStep.name = temp[1];
					newStep.stepType = AutonomousStep.stepTypes.valueOf( temp[0]);
					newStep.stepTime = Double.parseDouble(temp[2]);
					newStep.leftSpeed = Double.parseDouble(temp[3]);
					newStep.rightSpeed = Double.parseDouble(temp[4]);
					newStep.distance = Double.parseDouble(temp[5]);
					_steps.add(newStep);

					System.out.println("Autosteps now has "+_steps.size());
				}
			}
			SmartDashboard.putString("File Steps", sSteps);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}			

}
