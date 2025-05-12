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
        intake = hardwareMap.get(CRServoImplEx.class, "intake");
        intakeProx = hardwareMap.get(ColorSensor.class,"intakeSwitch");
        intake.setPower(0);
    }

    public boolean intakeFull(){
        return ((OpticalDistanceSensor) intakeProx).getLightDetected() > 0.37;
        //return false;
    }

    public boolean intakeFullColored(){
        return ((OpticalDistanceSensor) intakeProx).getLightDetected() > 0.37 && intakeProx.red() > 1100 && intakeProx.green() > 1100 && intakeProx.blue() < 900;
        //return false;
    }

    public void clearAction() {
        currAction = null;
    }

    public boolean intake() {
        if (!(currAction instanceof IntakeIAction)) {
            currAction = new IntakeIAction();
        }
        return currAction.run();
    }

    public boolean intake(boolean color) {
        if (!(currAction instanceof IntakeIAction)) {
            currAction = new IntakeIAction(color);
        }
        return currAction.run();
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

    private class IntakeIAction implements Action {
        boolean isInit = false;
        boolean color = false;

        public IntakeIAction() {

        }
        public IntakeIAction(boolean isColored) {
            color = isColored;
        }
        @Override
        public boolean run() {
            if (!isInit) {
                intake.setPower(1.0);
                isInit = true;
            }
            if (color)
                return intakeFullColored();
            else
                return intakeFull();

        }
    }
}
