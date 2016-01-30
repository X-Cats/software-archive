package org.usfirst.frc.team191.robot;

public class Teleop
{
	private RobotControls controls;
	
	public Teleop (RobotControls controls)
	{
		this.controls = controls;
	}

	public void execute ()
	{
		controls.drive();
		controls.operate();
		controls.updateStatus();
	}
}
