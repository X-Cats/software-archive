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
	private double _totalAutoTime = Enums.AUTONOMOUS_TIME;
	private AutonomousStep _currentAutoStep;
	private Timer _stepTimer = new Timer();
	private Timer _autoTimer = new Timer();

	private boolean _isExecuting = false;
	private boolean _cancelExecution = false;
	private boolean _canNavDefense = false;
	
	private static final double FIRST_LEG_DISTANCE = 73.0;	// this is the distance from the auto line to the lip of the defenses
	private static final double DEFENSE_DEPTH = 47.0;		// this is the depth from front to back of all the defenses
	private static final double OVERHANG = 3.0;				// this is the distance into the defense to travel, and the amount hanging over the auto line
	private static final double SHOOT_DISTANCE = 35.0;		// this is the distance from the tower base to stop to shoot. This is measured from the back wheels
	

	final String _defaultAuto = "Do Nothing";
	final String _autoForwardOnly = "Go forward only and stop";
	final String _auto1 = "Defense 1";
	final String _auto2 = "Defense 2";
	final String _auto3 = "Defense 3";
	final String _auto4 = "Defense 4";
	final String _auto5 = "Defense 5";
	final String _autoReadFile = "TextFile read";
	final String _autoTestSpeed = "Run 3 sec at input speed";
	
	final String _defLowBar = "Low Bar";
	final String _defMoat = "Moat";
	final String _defRamparts = "Ramparts";
	final String _defPortCullis = "Portcullis";
	final String _defChevaldeFrise = "Cheval de Frise";
	final String _defSallyPort = "Sally Port";
	final String _defRockwall = "Rock Wall";
	final String _defRoughTerrain = "Rough Terrain";
	final String _defDrawbridge = "Drawbridge";
	
	final String _shootYesHi = "ShootHi";
	final String _shootYesLow = "ShootLow";
	final String _shootNo = "Don't Shoot";
	
	
	String _autoSelected;
	String _defenseSelected;
	String _shooterModeSelected;
	SendableChooser _defensePosition;
	SendableChooser _defenseType;
	SendableChooser _shooterMode;

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

		_defensePosition = new SendableChooser();
		_defensePosition.addDefault("Default Auto", _defaultAuto);
		_defensePosition.addObject(_autoForwardOnly, _autoForwardOnly);
		_defensePosition.addObject(_auto1, _auto1);
		_defensePosition.addObject(_auto2, _auto2);
		_defensePosition.addObject(_auto3, _auto3);
		_defensePosition.addObject(_auto4, _auto4);
		_defensePosition.addObject(_auto5, _auto5);
		_defensePosition.addObject(_autoReadFile,_autoReadFile);
		_defensePosition.addObject(_autoTestSpeed, _autoTestSpeed);
		SmartDashboard.putData("Auto choices", _defensePosition);	
		
		_defenseType = new SendableChooser();
		_defenseType.addDefault(_defLowBar, _defLowBar);
		_defenseType.addObject(_defPortCullis, _defPortCullis);
		_defenseType.addObject(_defChevaldeFrise, _defChevaldeFrise);
		_defenseType.addObject(_defMoat, _defMoat);
		_defenseType.addObject(_defRamparts, _defRamparts);
		_defenseType.addObject(_defDrawbridge, _defDrawbridge);
		_defenseType.addObject(_defSallyPort, _defSallyPort);
		_defenseType.addObject(_defRockwall, _defRockwall);
		_defenseType.addObject(_defRoughTerrain, _defRoughTerrain);
		SmartDashboard.putData("Defense Types", _defenseType);
		
		_shooterMode = new SendableChooser();
		_shooterMode.addDefault(_shootNo,_shootNo);
		_shooterMode.addObject(_shootYesHi,_shootYesHi);
		_shooterMode.addObject(_shootYesLow,_shootYesLow);
		SmartDashboard.putData("Shoot Options",_shooterMode);
		

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

		_autoSelected = (String) _defensePosition.getSelected();
		System.out.println("Auto selected: " + _autoSelected);		
		_defenseSelected = (String) _defenseType.getSelected();
		System.out.println("Defense selected: " + _defenseSelected);	
		_shooterModeSelected = (String) _shooterMode.getSelected();
		System.out.println("ShooterMode Selected:" + _shooterModeSelected);
		setAuto();  //build the steps for the selected autonomous

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
		_autoSelected = "";
		_defenseSelected = "";
		_shooterModeSelected = "";
		stopShooter();
		updateStatus();
		
	}
	private void setAuto ()
	{	
		switch (_defenseSelected){
		
		case _defLowBar:
		case _defPortCullis:
		case _defMoat:
		case _defChevaldeFrise:
		case _defRamparts:
		case _defRockwall:
		case _defRoughTerrain:
			_canNavDefense = true;
			break;
		default:
			_canNavDefense = false;
		}
		
		
		
		
		//we are going to construct the steps needed for our autonomous mode
		_steps =  new ArrayList<AutonomousStep>();
		switch (_autoSelected)
		{
		case _defaultAuto:
			
			break;
		case _autoForwardOnly:
			if (_defenseSelected == _defLowBar || _defenseSelected == _defPortCullis){
				//when we are the low bar or portcullis, we need to have the shooter assembly on the ground to fit under it. Use the extended length.
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOWER,"Move arm to ground", 1.5, 0, 0, 0));			
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG) + OVERHANG)/12.0));
			}
			else 
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));									
			break;

		case _auto1:
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOWER,"Move arm to ground", 2.8, 0, 0, 0));			
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG) + OVERHANG)/12.0));									

			if (_canNavDefense){
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.5, 0.5, (DEFENSE_DEPTH  + 2 * OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn", 0, 0.95, 0.95, (109 + 6 - 0.5*Enums.ROBOT_LENGTH_COMPACT)/12.0));									
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Right",0, 0, 0, 60));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to goal", 0, 0.95, 0.95, (98 - SHOOT_DISTANCE + 0.5*Enums.ROBOT_LENGTH_COMPACT - 21.0)/12.0));
				if (_shooterModeSelected == _shootYesLow){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT_TO_LOW,"Move shifter low", 0.5, 0, 0, 0));	
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));
				}
				else if (_shooterModeSelected == _shootYesHi){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));
				}
			}
			break;

		case _auto2:
			
			if (_canNavDefense){
				addDefenseSteps();

				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn", 0, 0.95, 0.95, (139 - 0.5*Enums.ROBOT_LENGTH_COMPACT)/12.0));									
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Right",0, 0, 0, 60));
				if (_shooterModeSelected == _shootYesLow){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT_TO_LOW,"Move arm to shoot", 3.0, 0, 0, 0));	
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
				else if (_shooterModeSelected == _shootYesHi){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
			}
			else
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));
				
			break;
			
		case _auto3:
			
			if (_canNavDefense){
				addDefenseSteps();
				
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn", 0, 0.7, 0.7, (70 - 0.5*Enums.ROBOT_LENGTH_COMPACT)/12.0));									
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Right",0, 0, 0, 90));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn back", 0, 0.7, 0.7, 24.0/12.0));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Left",0, 0, 0, -90));
				if (_shooterModeSelected == _shootYesHi || _shooterModeSelected == _shootYesLow){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
			}
			else
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));

			break;
			
		case _auto4:

			if (_canNavDefense){
				addDefenseSteps();
				
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn", 0, 0.7, 0.7, (70 - 0.5*Enums.ROBOT_LENGTH_COMPACT)/12.0));									
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Left",0, 0, 0, -90));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to turn back", 0, 0.7, 0.7, Math.abs(15)/12.0));
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Right",0, 0, 0, 90));
				if (_shooterModeSelected == _shootYesHi || _shooterModeSelected == _shootYesLow){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
			}
			else
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));

			break;
			
		case _auto5:
			
			if (_canNavDefense){
				addDefenseSteps();

				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Turn", 0, 0.7, 0.7, (151 - 0.5*Enums.ROBOT_LENGTH_COMPACT)/12.0));									
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn Left",0, 0, 0, -60));
				if (_shooterModeSelected == _shootYesLow){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT_TO_LOW,"Move arm to shoot", 3.0, 0, 0, 0));	
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
				else if (_shooterModeSelected == _shootYesHi){
					_steps.add( new AutonomousStep(AutonomousStep.stepTypes.SHOOT,"Shoot",0,0,0,0));				
				}
			}
			else
				_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));
			
			break;

		case _autoReadFile:
			this.ReadAutoFile();
			break;
		
		case _autoTestSpeed:
			double speed=SmartDashboard.getNumber(_autoTestSpeed);
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE,"Test for 3 seconds", 3, speed, speed, 0));

			break;
			
		default:
			_steps.add(new AutonomousStep(AutonomousStep.stepTypes.STOP,"stop",0,0,0,0));

		}
	}
	
//	private void addPortcullisSteps(){
//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG ) +35)/12.0));
//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.6, 0.6, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
//	}

	private void  addDefenseSteps() {
		
		switch (_defenseSelected){		
		case _defLowBar:
			break;
		case _defPortCullis:
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOWER,"Move arm to ground", 3.0, 0, 0, 0));			
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG ) +26)/12.0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.6, 0.6, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
			break;
		case _defChevaldeFrise:
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG ) +11 + 0)/12.0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOWER,"Move arm to ground", 3.0, 0, 0, 0));			
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.6, 0.6, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
			break;
		default:
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_COMPACT - OVERHANG) + OVERHANG)/12.0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.5, 0.5, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
		}		
	}
	
	
	public boolean isExecuting(){
		return _isExecuting;
	}
	public void cancelExecution(){
		_cancelExecution = true;
	}

	public void execute ()
	{
		if (_autoSelected != (String) _defensePosition.getSelected() ||
			_defenseSelected != (String) _defenseType.getSelected() ||
		    _shooterModeSelected != (String) _shooterMode.getSelected()	){
			init();
		}
		
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
				// assume 102 degrees per second
				direction = (_currentAutoStep.distance > 0 ? 1 : -1);

				if (Enums.IS_FINAL_ROBOT)					
					cTime = Math.abs( _currentAutoStep.distance/112.0);
				else
					cTime = Math.abs( _currentAutoStep.distance/115.0);
				
				drive(cTime, direction * 0.5, direction * -0.5);
				break;
				
			case GRAB:
				break;
				
			case UNGRAB:
				break;
			
			case LIFT:
				lift();
				break;	
				
			case LOWER:
				lower();
				break;
				
			case LIFT_TO_LOW:
				liftToLow();
				break;
				
			case SHOOT:
				shoot();
				break;
				
			case WAIT:
				wait(_currentAutoStep.stepTime);
				break;
				
			case STOP:
				stop();
				break;
				
			case RELEASE:
				release();
				break;
			}
		}

		this.updateStatus();
		_controls.updateStatus();
	}

	private void updateStatus(){

		SmartDashboard.putString("Selected Auto mode", _autoSelected);
		SmartDashboard.putString("Selected Shooter Mode", _shooterModeSelected);
		SmartDashboard.putString("Selected Defense", _defenseSelected);
		SmartDashboard.putNumber("Step Count", _steps.size());
		SmartDashboard.putString("Current Command", this._currentStep + " " + _currentAutoStep.name  + "\n " + _currentAutoStep.stepTime);

	}
	public void drive (double time, double left, double right)
	{
		//if (left == right){
			if (Enums.IS_FINAL_ROBOT)
				right = right - 0.035;
			else
				right = right;
		//}
		
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
	private void release(){
		_controls.acquisition().release();
		startNextStep();
	}
	
	public void lower ()
	{
		
		if (_stepTimer.get() > _currentAutoStep.stepTime)
		{
			startNextStep();
		}
		else
			_controls.acquisition().gotoGround();
	}
	
	public void lift ()
	{
		
		if (_stepTimer.get() > _currentAutoStep.stepTime)
		{
			startNextStep();
		}
		else
			_controls.acquisition().goHome();
	}

	public void liftToLow ()
	{
		
		if (_stepTimer.get() > _currentAutoStep.stepTime)
		{
			startNextStep();
		}
		else
			_controls.acquisition().gotoLowGoal();
	}

	public void wait (double time)
	{
		if (_stepTimer.get() > time)
			startNextStep();
	}

	public void shoot (){
		
		if (_controls.acquisition().setShooterAndShoot()){
			startNextStep();
		}

	}
	public void stopShooter (){
		
		_controls.acquisition().stopShoot();
		startNextStep();

	}
	public void stop ()
	{
		_controls.getDrive().set(0, 0, 0, 0);
		startNextStep();
	}

	public void startNextStep ()
	{
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
