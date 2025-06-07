package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.MConstants;

public class SpecClawAction {
    private final Servo specClaw;
    private final ColorSensor color;

    public SpecClawAction(HardwareMap hardwareMap) {
        specClaw = hardwareMap.servo.get("specClaw");
        color = hardwareMap.get(ColorSensor.class, "color");

        specClaw.setPosition(0);
        specClaw.setPosition(0.27);
    }

    public boolean openClaw() {
        new OpenClaw().run();
        return true;
    }

    public boolean closeClaw() {
        new CloseClaw().run();
        return true;
    }

    public class OpenClaw implements Action {

        @Override
        public boolean run() {
            specClaw.setPosition(MConstants.specClawOpen);
            return true;
        }
    }

    public class CloseClaw implements Action {

        @Override
        public boolean run() {
            specClaw.setPosition(MConstants.specClawClosed);
            return true;
        }
    }

    public boolean specimenDetected(){
        return ((OpticalDistanceSensor) color).getLightDetected() > 0.25;
    }
}
