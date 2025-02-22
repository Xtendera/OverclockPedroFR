package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ExtendoAction {
    private final Servo extendoR;
    private final Servo extendoL;

    public ExtendoAction(HardwareMap hardwareMap) {
        extendoR = hardwareMap.servo.get("extendoRight");
        extendoL = hardwareMap.servo.get("extendoLeft");

        extendoR.setDirection(Servo.Direction.REVERSE);
    }

    public void goTo(double pos) {
        extendoL.setPosition(pos);
        extendoR.setPosition(pos);
    }
}
