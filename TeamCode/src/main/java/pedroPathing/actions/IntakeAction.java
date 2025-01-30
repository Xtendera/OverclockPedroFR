package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class IntakeAction {
    private CRServoImplEx intake;
    public ColorSensor intakeProx;

    private Action currAction;
    public IntakeAction(HardwareMap hardwareMap) {
        intake = hardwareMap.get(CRServoImplEx.class, "claw");
        intakeProx = hardwareMap.get(ColorSensor.class,"intakeSwitch");
    }

    public boolean intakeFull(){
        return ((OpticalDistanceSensor) intakeProx).getLightDetected() > 0.37;
        //return false;
    }

    public void clearAction() {
        currAction = null;
    }

    public void intake() {
        intake.setPower(1.0);
    }

    public void stoptake() {
        intake.setPower(0);
    }

    public boolean outake() {
        if (!(currAction instanceof OutakeAction)) {
            currAction = new OutakeAction();
        }
        return currAction.run();
    }

    private class OutakeAction implements Action {
        boolean isInit = false;
        @Override
        public boolean run() {
            if (!isInit) {
                intake.setPower(-1.0);
                isInit = true;
            }
            return !intakeFull();
        }
    }
}
