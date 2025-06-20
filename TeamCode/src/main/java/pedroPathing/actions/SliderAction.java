package pedroPathing.actions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import pedroPathing.constants.MConstants;

public class SliderAction {
    public final DcMotor sliderRightMotor;
    public final DcMotor sliderLeftMotor;

    private final int threshold = 50;

    private Action currAction;
    private double currPosition;
    public SliderAction (HardwareMap hardwareMap) {
        sliderRightMotor = hardwareMap.dcMotor.get("slideRightMotor");
        sliderLeftMotor = hardwareMap.dcMotor.get("slideLeftMotor");
        sliderLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        sliderLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sliderRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void clearAction() {
        currAction = null;
        currPosition = 0;
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

    public boolean highBasketScore() {
        if (!(currAction instanceof HighBasketScore))
            currAction = new HighBasketScore();
        return currAction.run();
    }

    // TeleOP only variant
    public boolean highBasket() {
        if (!(currAction instanceof HighBasket))
            currAction = new HighBasket();
        return currAction.run();
    }

    public boolean lowBasket() {
        if (!(currAction instanceof LowBasket))
            currAction = new LowBasket();
        return currAction.run();
    }

    public boolean reset() {
        if (!(currAction instanceof Reset))
            currAction = new Reset();
        return currAction.run();
    }

    public void goTo(double position) {
        if (currPosition != position) {
            currAction = new GoTo(position);
            currPosition = position;
        }
        currAction.run();
    }

    public boolean isBusy() {
        return !currAction.run();
    }

    private class GoTo implements Action {
        double position;
        public GoTo(double position) {
            this.position = position;
            sliderRightMotor.setTargetPosition((int) position);
            sliderLeftMotor.setTargetPosition((int) position);
            sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sliderRightMotor.setPower(1.0f);
            sliderLeftMotor.setPower(1.0f);
        }

        @Override
        public boolean run() {
            return Math.abs(sliderLeftMotor.getCurrentPosition() - position) <= threshold && Math.abs(sliderRightMotor.getCurrentPosition() - position) <= threshold;
        }
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

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.highChamberLoad) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.highChamberLoad) <= threshold;
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

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.highChamberScore) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.highChamberScore) <= threshold;
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

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.specLoad) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.specLoad) <= threshold;
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

    public class HighBasketScore implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.highBasketScore));
                sliderLeftMotor.setTargetPosition((int) (MConstants.highBasketScore));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.highBasketScore) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.highBasketScore) <= threshold;
        }
    }

    public class HighBasket implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.highBasket));
                sliderLeftMotor.setTargetPosition((int) (MConstants.highBasket));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.highBasket) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.highBasket) <= threshold;
        }
    }

    public class LowBasket implements Action {
        private boolean isInit = false;

        @Override
        public boolean run() {
            if (!isInit) {
                sliderRightMotor.setTargetPosition((int) (MConstants.lowBasket));
                sliderLeftMotor.setTargetPosition((int) (MConstants.lowBasket));
                sliderRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderRightMotor.setPower(1.0f);
                sliderLeftMotor.setPower(1.0f);
                isInit = true;
            }

            return Math.abs(sliderRightMotor.getCurrentPosition() - (int) MConstants.lowBasket) <= threshold && Math.abs(sliderLeftMotor.getCurrentPosition() - (int) MConstants.lowBasket) <= threshold;
        }
    }
}
