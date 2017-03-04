package org.usfirst.frc.xcats.robot;
//package org.usfirst.frc.team191.robot;
//package org.usfirst.frc.team191.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	private RobotControls _controls;
	private ArrayList<AutonomousStep> _steps;

	private int _currentStep=0;
	private float _initialYaw = 0;
	private float _initCompassHeading=0;
	private boolean _angleHasBeenCalculated = false;
	private double _calculatedAngle = 0;

	private double _totalAutoTime = Enums.AUTONOMOUS_TIME;
	private AutonomousStep _currentAutoStep;
	private Timer _stepTimer = new Timer();
	private Timer _autoTimer = new Timer();

	private boolean _isExecuting = false;
	private boolean _cancelExecution = false;
	private boolean _isEjecting = false;

	private static final double FIRST_LEG_DISTANCE = 73.0;	// this is the distance from the auto line to the lip of the defenses	

	final String _defaultAuto = "Do Nothing";
	final String _autoForwardOnly = "Go forward only and stop";
	final String _auto1 = "Left Gear Peg";
	final String _auto2 = "Center Gear Peg";
	final String _auto3 = "Right Gear Peg";
	final String _autoReadFile = "TextFile read";
	final String _autoTestSpeed = "Run 3 sec at input speed";
	final String _autoInTeleop = "TeleopCommands";
	final String _autoRotator = "Test Rotations";
	private Navx _navx;
	
	String _autoSelected;
	String _defenseSelected;
	String _shooterModeSelected;
	SendableChooser _defensePosition;

	public Autonomous (RobotControls controls)
	{

		_controls = controls;      	//passes the controls object reference

		_defensePosition = new SendableChooser();
		_defensePosition.addDefault("Default Auto", _defaultAuto);
		_defensePosition.addObject(_autoForwardOnly, _autoForwardOnly);
		_defensePosition.addObject(_auto1, _auto1);
		_defensePosition.addObject(_auto2, _auto2);
		_defensePosition.addObject(_auto3, _auto3);
		_defensePosition.addObject(_autoReadFile,_autoReadFile);
		_defensePosition.addObject(_autoTestSpeed, _autoTestSpeed);
		_defensePosition.addObject(_autoRotator, _autoRotator);
		SmartDashboard.putData("Auto choices", _defensePosition);			
		
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
		_autoSelected = this._autoInTeleop;
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

		if (_autoSelected != this._autoInTeleop){
			_autoSelected = (String) _defensePosition.getSelected();
			System.out.println("Auto selected: " + _autoSelected);		
	
	
			//build the steps for the selected autonomous
			setAuto();
		}
		
		_navx = _controls.getNavx();
		_navx.zeroYaw();
		_initCompassHeading = _navx.getCompassHeading();
		_initialYaw = _navx.getYaw();
		_currentStep = 0;
		_angleHasBeenCalculated =false;
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
//		int choice=1;
		double speedTest =SmartDashboard.getNumber(_autoTestSpeed, 0.5);
		String caseName="";
		_steps =  new ArrayList<AutonomousStep>();
		
		boolean blueAlliance = false;
		
		if (DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue){
			blueAlliance = true;
		}
		
		SmartDashboard.putBoolean("Alliance Color", blueAlliance);
		
		if (Enums.IS_FINAL_ROBOT){
			//use the switch on the robot to identify autonomous
			
			AnalogInput autoSelector = new AnalogInput(Enums.AUTO_SWITCH_ANALOG);
			SmartDashboard.putNumber("Auto Selector Value", autoSelector.getValue()/100);
			
			if (autoSelector.getValue()/100 < 0.5)
				_autoSelected = _defaultAuto;
			else if (autoSelector.getValue()/100 < 1.5 && autoSelector.getValue()/100 > 0.5)
				_autoSelected = _auto1;  //left
			else if (autoSelector.getValue()/100 < 2.5 && autoSelector.getValue()/100 > 1.5)
				_autoSelected = _auto2;  //center
			else if (autoSelector.getValue()/100 < 3.5 && autoSelector.getValue()/100 > 2.5)
				_autoSelected = _auto3;  //right
			else if (autoSelector.getValue()/100 < 4.5 && autoSelector.getValue()/100 > 3.5)
				//read from the sendable chooser
				System.out.println("Using Sendable Chooser Autonomous mode" + _autoSelected);
			else
				_autoSelected = _defaultAuto;
		}
		SmartDashboard.putString("AutoSelected", _autoSelected);
//			_autoSelected= _auto2;		
		switch (_autoSelected) {
		case _auto2: 
			caseName="Middle Gear";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.BRAKEMODE,"Brake Mode",0,0,0,0)); //Set brake mode for drive train
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward",0,.5,.5,91)); //assuming 99.64 inches
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.GEAR,"Place Gear",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.WAIT,"Wait for gear to eject",0,0,0,Enums.GEAR_EJECT_TIME));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
			
			//note we set coastmode in teleop init, but setting it here is a good practice
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.COASTMODE,"Coast Mode",0,0,0,0)); //Set COAST mode for drive train
		
			break;
		case _auto1: 
			caseName="Left Gear";
			addSideSteps(blueAlliance);
	
			break;
			
		case _auto3: 
			caseName="Right Gear";
			addSideSteps(blueAlliance);
			
			break;

		case _autoRotator:
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.BRAKEMODE,"Brake Mode",0,0,0,0)); //Set brake mode for drive train
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOW_SPEED,"Low speed transmission",0,0,0,0)); //make sure we are in low speed
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward1",0,.5,.5,30));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn 60",0,0,0,60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward2",0,.5,.5,30));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn 60",0,0,0,-60));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward3",0,.5,.5,30));
			
			break;
			
		case _autoTestSpeed:
			caseName="Speed Test";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.BRAKEMODE,"Brake Mode",0,0,0,0)); //Set brake mode for drive train
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE,"Drive",5,speedTest,speedTest,0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.COASTMODE,"Coast Mode",0,0,0,0)); //Set COAST mode for drive train
			break;
			
		default: 
			caseName="Do Nothing";
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
			break;
		}


		System.out.println("setAuto");
	}

	//	private void addPortcullisSteps(){
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Forward to Defense", 0, 0.7, 0.7, (FIRST_LEG_DISTANCE - (Enums.ROBOT_LENGTH_EXTENDED - OVERHANG ) +35)/12.0));
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Move shifter home", 0.5, 0, 0, 0));			
	//		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Navigate Defense", 0, 0.6, 0.6, (DEFENSE_DEPTH - OVERHANG + Enums.ROBOT_LENGTH_EXTENDED)/12.0));
	//	}


	private void addSideSteps(boolean isBlueAlliance){
		boolean isBoilerSide = false;
		
		double boilerAngle = 0;
		double rotationAngle = 0;
		double distanceLeg1 = 0;
		double distanceLeg2 = 0;
		double leftSpeed = 0;
		double rightSpeed = 0;
		
		double boilerSideLeg1 = 120 - 22;
		double boilerSideLeg2 = 33;
		double feederSideLeg1 = 100.5 - 22; //22 is half the robot length
		double feederSideLeg2 = 64;
		
		if ((isBlueAlliance && _autoSelected == _auto1) || (!isBlueAlliance && _autoSelected == _auto3)){
			isBoilerSide = true;
		}
		
				
		if (_autoSelected == _auto1){
			//left 
			rotationAngle = 59; //(isBlueAlliance ? 58 : -62);
			distanceLeg1 = (isBlueAlliance ? boilerSideLeg1 : feederSideLeg1 );
			distanceLeg2 = (isBlueAlliance ? boilerSideLeg2 : feederSideLeg2);			
		} else if (_autoSelected == _auto3){
			//right
			rotationAngle = -61 ; //(isBlueAlliance ? 58 : -62);
			distanceLeg1 = (isBlueAlliance ? feederSideLeg1 : boilerSideLeg1);
			distanceLeg2 = (isBlueAlliance ? feederSideLeg2 : boilerSideLeg2);			
		}
					
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.BRAKEMODE,"Brake Mode",0,0,0,0)); //Set brake mode for drive train
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LOW_SPEED,"Low speed transmission",0,0,0,0)); //make sure we are in low speed
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward 1",0,.7,.7,distanceLeg1));
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn 60",0,0,0,rotationAngle));
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive Forward 2",0,.7,.7,distanceLeg2));
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.GEAR,"Place Gear",0,0,0,60));
		
		//If we are isBoilerSide then add the steps for the ball handling
		SmartDashboard.putBoolean("is Boiler Side", isBoilerSide);
		if (isBoilerSide){			
			boilerAngle = (isBlueAlliance ? -32   : 32   );
			leftSpeed   = (isBlueAlliance ? -0.78 : -0.92);
			rightSpeed  = (isBlueAlliance ? -0.93 : -0.77);
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.ROTATE,"Turn to boiler1",0,0,0,boilerAngle));			
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.COASTMODE,"Coast Mode",0,0,0,0)); //Set COAST mode for drive train
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.DRIVE_DISTANCE,"Drive to Boiler1",0,leftSpeed,rightSpeed,129)); //drive forward about 20 inch
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.LIFT,"Lift Bottom",0,0,0,0)); 
			_steps.add( new AutonomousStep(AutonomousStep.stepTypes.FEED,"Feed Balls",10,0,0,0)); 
		}
		
		//finish the autonomous
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.STOP,"Stop",0,0,0,0));
		
//note we set coastmode in teleop init, but setting it here is a good practice
		_steps.add( new AutonomousStep(AutonomousStep.stepTypes.COASTMODE,"Coast Mode",0,0,0,0)); //Set COAST mode for drive train
		
		
	}

	
	public boolean isExecuting(){
		return _isExecuting;
	}
	public void cancelExecution(){
		_cancelExecution = true;
		_controls.setCoastMode();
	}

	public void execute ()
	{
//		System.out.println("auto execute");
		if (_steps == null ){
			_isExecuting = false;
			return;
			
		}
		
		if (_steps.size() == 0){
			System.out.println("trying to execute no steps in Autonomous 272");
			_isExecuting = false;	
			return;
		}

		double cTime=0;


		if (_autoTimer.get() > _totalAutoTime || _currentStep >= _steps.size() || _cancelExecution){
			_controls.getDrive().set(0, 0, 0, 0);
			_isExecuting = false;	
			disable();
		}
		else
		{
			_currentAutoStep = _steps.get(_currentStep);
			
			_isExecuting = true;
			//switch (_steps.get(_currentStep))
			switch (_currentAutoStep.stepType)
			{
			case DRIVE:
				drive(_currentAutoStep.stepTime,_currentAutoStep.leftSpeed,_currentAutoStep.rightSpeed);
				break;

			case DRIVE_DISTANCE:

				if (Enums.IS_FINAL_ROBOT)					
					cTime = Math.abs(_currentAutoStep.distance/(59*_currentAutoStep.leftSpeed - 2.5));
				else
					cTime = Math.abs(_currentAutoStep.distance/(59*_currentAutoStep.leftSpeed - 2.5));

				driveStraight(cTime,_currentAutoStep.leftSpeed,_currentAutoStep.rightSpeed);
				break;
				
			case CALCANGLE:
				if (!_angleHasBeenCalculated){
					
					if (DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue){
						//this is where we calculate the rotation angle
						//initial heading + 225 - current heading should be pretty close
						_calculatedAngle = 180 - 30;//_initCompassHeading + 225 - _navx.getCompassHeading();
					}	else{
						_calculatedAngle = 180 + 30; //_initCompassHeading + 135.0 - _navx.getCompassHeading();
						
					}
					
					_angleHasBeenCalculated = true;
				}
				SmartDashboard.putNumber("Init Compass Heading", _initCompassHeading);
				SmartDashboard.putNumber("CalcAngle", _calculatedAngle);
				//now just rotate the calculated distance
				rotate(_calculatedAngle);
				break;
				
			case LIFT:
				raise();
				break;
				
			case ROTATE:
				rotate(_currentAutoStep.distance);

				break;

			case FEED:
				feedBalls(_currentAutoStep.stepTime);
				
				break;
			case GEAR: 
				//if we have not requested an ejection, do so now
				if (!_isEjecting){
					_isEjecting = true;
					_controls.getGear().eject();					
				}					
				
				//wait until the gear is fully deployed, this will back up the robot too
				if (! _controls.getGear().isEjecting()){
					_isEjecting = false;
					startNextStep();
				} 
			
				break;
				
			case BRAKEMODE:
				_controls.setBrakeMode();
				startNextStep();
				break;

			case COASTMODE:
				_controls.setCoastMode();
				startNextStep();
				break;

			case LOW_SPEED:
				_controls.setLowSpeed();
				startNextStep();
				break;

			case HIGH_SPEED:
				_controls.setHighSpeed();
				startNextStep();
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

	private void rotate( double distance){
		//float deltaYaw;
		double  speed =0.25;
		double tolerance=0.5;
		int direction=1;


		//deltaYaw = _initialYaw + _controls.getNavx().getYaw();
		//SmartDashboard.putNumber("deltaYaw", deltaYaw);
		// 
		direction = (distance > 0 ? -1 : 1);
		speed = direction * speed;	
		_controls.getDrive().set(speed, speed, -speed, -speed);


		if(Math.abs(_controls.getNavx().getYaw()) > Math.abs(distance)){
			SmartDashboard.putNumber("Auto Yaw", _controls.getNavx().getYaw());
			speed=-speed/1.5;
			_controls.getDrive().set(speed, speed, -speed, -speed);
			if(Math.abs(_controls.getNavx().getYaw())-Math.abs(distance)<=tolerance){
				startNextStep();
			}
		}
	}
	
	private void raise(){
		_controls.getFeeder().lower();
		startNextStep();
	}
	
	private void feedBalls(double time){
		if (_stepTimer.get() <= time){
			_controls.getFeeder().feed();			
		} else
			startNextStep();
		
	}
	public void updateStatus(){

		if (_steps != null && _currentAutoStep != null){
			SmartDashboard.putNumber("Step Count", _steps.size());
			SmartDashboard.putString("Current Command", this._currentStep + " " + _currentAutoStep.name  + "\n " + _currentAutoStep.stepTime);			
		}

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
			_controls.getDrive().set(-left, -left, -right, -right);
		}
	}

	public void driveStraight (double time, double left, double right)
	{
		float deltaYaw;

		//deltaYaw = _initialYaw - _controls.getNavx().getYaw();
		deltaYaw = _navx.getYaw();
		double offsetLimit = 0.05;
		double offset=0;

		SmartDashboard.putNumber("currentYaw", _initialYaw);
		SmartDashboard.putNumber("deltaYaw", deltaYaw);
		if (left == right){
			offset = Math.abs(deltaYaw);
			if(offset > offsetLimit){
				offset = offsetLimit;
			}
			if(deltaYaw < 0){
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
		System.out.println("Step "+_currentStep + _currentAutoStep.name  + " is completed");
		_navx.zeroYaw();
		SmartDashboard.putNumber("Starting Yaw", _controls.getNavx().getYaw() );
		_currentStep++;
		_stepTimer.reset();
		_angleHasBeenCalculated=false;
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
