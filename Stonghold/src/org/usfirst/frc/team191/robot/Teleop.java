package org.usfirst.frc.team191.robot;

public class Teleop {
	
	private RobotControls _controls;
	
	public Teleop (RobotControls controls)
	{
		this._controls = controls;
	}

	public void execute ()
	{
		_controls.drive();
		_controls.operate();
		_controls.updateStatus();
	}

}
