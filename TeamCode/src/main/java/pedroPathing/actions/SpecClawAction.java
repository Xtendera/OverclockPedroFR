package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SpecClawAction {
    private final Servo specClaw;

    public SpecClawAction(HardwareMap hardwareMap) {
        specClaw = hardwareMap.servo.get("claw2");

        specClaw.setPosition(0);
        specClaw.setPosition(0.7);
    }
}
