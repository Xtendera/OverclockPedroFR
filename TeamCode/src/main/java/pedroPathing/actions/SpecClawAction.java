package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.MConstants;

public class SpecClawAction {
    private final Servo specClaw;

    public SpecClawAction(HardwareMap hardwareMap) {
        specClaw = hardwareMap.servo.get("specClaw");

        specClaw.setPosition(0);
        specClaw.setPosition(0.7);
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
}
