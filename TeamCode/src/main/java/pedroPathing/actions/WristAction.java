package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.MConstants;

public class WristAction {
    private final Servo wrist;

    public WristAction(HardwareMap hardwareMap) {
        wrist = hardwareMap.servo.get("wrist");
    }

    public void wristUp() {
        wrist.setPosition(MConstants.wristUp);
    }

}
