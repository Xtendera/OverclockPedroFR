package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import pedroPathing.constants.MConstants;

public class SliderAction {
    private final DcMotor sliderRightMotor;
    private final DcMotor sliderLeftMotor;

    private Action currAction;
    public SliderAction (HardwareMap hardwareMap) {
        sliderRightMotor = hardwareMap.dcMotor.get("slideRightMotor");
        sliderLeftMotor = hardwareMap.dcMotor.get("slideLeftMotor");
        sliderRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void clearAction() {
        currAction = null;
    }

    public boolean highChamberLoad() {
        if (!(currAction instanceof HighChamberLoad))
            currAction = new HighChamberLoad();
        return currAction.run();
    }

    public boolean highChamberScore() {
        if (!(currAction instanceof HighChamberScore))
            currAction = new HighChamberScore();
        return currAction.run();
    }

    public boolean specLoad() {
        if (!(currAction instanceof SpecLoad))
            currAction = new SpecLoad();
        return currAction.run();
    }

    public boolean reset() {
        if (!(currAction instanceof Reset))
            currAction = new Reset();
        return currAction.run();
    }

    public class HighChamberLoad implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.highChamberLoad));
                sliderLeftMotor.setTargetPosition((int) (MConstants.highChamberLoad));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return sliderRightMotor.getCurrentPosition() >= (int) MConstants.highChamberLoad || sliderLeftMotor.getCurrentPosition() >= (int) MConstants.highChamberLoad;
        }
    }

    public class HighChamberScore implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.highChamberScore));
                sliderLeftMotor.setTargetPosition((int) (MConstants.highChamberScore));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return sliderRightMotor.getCurrentPosition() >= (int) MConstants.highChamberScore || sliderLeftMotor.getCurrentPosition() >= (int) MConstants.highChamberScore;
        }
    }

    public class SpecLoad implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.specLoad));
                sliderLeftMotor.setTargetPosition((int) (MConstants.specLoad));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return sliderRightMotor.getCurrentPosition() >= (int) MConstants.specLoad || sliderLeftMotor.getCurrentPosition() >= (int) MConstants.specLoad;
        }
    }

    public class Reset implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.sliderReset));
                sliderLeftMotor.setTargetPosition((int) (MConstants.sliderReset));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return sliderRightMotor.getCurrentPosition() <= (int) MConstants.sliderReset + 10 || sliderLeftMotor.getCurrentPosition() <= (int) MConstants.sliderReset + 10;
        }
    }
}
