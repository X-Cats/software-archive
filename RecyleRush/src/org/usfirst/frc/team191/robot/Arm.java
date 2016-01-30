package org.usfirst.frc.team191.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {
	private XCatsSpeedContoller armMotor1;
	private XCatsSpeedContoller armMotor2;

	public Arm(int armMotorChannel){
		armMotor1 = new XCatsSpeedContoller(armMotorChannel, Enums.USE_CAN, Enums.IS_FINAL_ROBOT,null,null);
	}
	
	public Arm(int armMotor1Channel, int armMotor2Channel){
		armMotor1 = new XCatsSpeedContoller(armMotor1Channel, Enums.USE_CAN, Enums.IS_FINAL_ROBOT,null,null);
		armMotor2 = new XCatsSpeedContoller(armMotor2Channel, Enums.USE_CAN, Enums.IS_FINAL_ROBOT,null,null);
	}
	
	public void setArmMotor (double armSpeed)
	{
//		if (armSpeed < 0)			
//			armSpeed = 0.25 * armSpeed; // go slow
//		else
//			armSpeed = 0.5 * armSpeed; // go slow			
		
		armMotor1.set(armSpeed);
		if (armMotor2 != null)
			armMotor2.set(armSpeed);
	}	
	
	public void updateStatus(){
		SmartDashboard.putNumber("arm speed", armMotor1.getSpeed());
	}

}
