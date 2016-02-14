package org.usfirst.frc.team192.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XCatsJSButton {
	Joystick _js;
	int _buttonNumber;
	private boolean _toggle = false, _mode = true;	
	

	public XCatsJSButton(Joystick js, int buttonNumber){
		_js = js;
		_buttonNumber = buttonNumber;
		
	}
	public boolean isPressed(){
		
		if (_js.getRawButton(_buttonNumber) && ! _toggle)
			_mode = !_mode;
		
		_toggle = _js.getRawButton(_buttonNumber);
		
		return _mode;		
		
	}
}
