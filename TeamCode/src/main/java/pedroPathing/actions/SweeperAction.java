package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.MConstants;

public class SweeperAction {
    public Servo flipper;
    public SweeperAction(HardwareMap hardwareMap) {
        flipper = hardwareMap.servo.get("sweeper");
        flipper.setPosition(0);
        flipper.setPosition(MConstants.flipperIn);
    }

    public void setPosition(double pos) {
        flipper.setPosition(pos);
    }
}
