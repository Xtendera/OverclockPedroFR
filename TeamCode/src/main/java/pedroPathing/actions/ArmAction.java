package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.MConstants;

public class ArmAction {
    private final Servo arm;

    public ArmAction(HardwareMap hardwareMap) {
        arm = hardwareMap.servo.get("arm");

        arm.setPosition(MConstants.armStowed);
    }

    public void stow() {
        arm.setPosition(MConstants.armStowed);
    }
    public boolean armPickup() {
        new ArmPickup().run();
        return true;
    }

    public boolean armScore() {
        new ArmScore().run();
        return true;
    }

    public boolean armScoreTele() {
        new ArmScoreTele().run();
        return true;
    }

    public class ArmPickup implements Action {

        @Override
        public boolean run() {
            arm.setPosition(MConstants.armPickup);
            return true;
        }
    }

    public class ArmScore implements Action {

        @Override
        public boolean run() {
            arm.setPosition(MConstants.armScore);
            return true;
        }
    }

    public class ArmScoreTele implements Action {

        @Override
        public boolean run() {
            arm.setPosition(MConstants.armScoreTele);
            return true;
        }
    }

}
