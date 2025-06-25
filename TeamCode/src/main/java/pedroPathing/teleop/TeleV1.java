package pedroPathing.teleop;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import pedroPathing.actions.ArmAction;
import pedroPathing.actions.ExtendoAction;
import pedroPathing.actions.IntakeAction;
import pedroPathing.actions.SliderAction;
import pedroPathing.actions.SpecClawAction;
import pedroPathing.actions.SweeperAction;
import pedroPathing.actions.WristAction;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import pedroPathing.constants.MConstants;
import pedroPathing.fsm.VariantMachine;
import pedroPathing.fsm.VariantMachineBuilder;

/**
 * This is an example teleop that showcases movement and robot-centric driving.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 12/30/2024
 */

@TeleOp(name = "TeleV1", group = "AAA")
public class TeleV1 extends OpMode {
    private Follower follower;
    private Pose startPose = new Pose(0, 0, Math.toRadians(0));

    DcMotor leftFront, rightFront, leftBack, rightBack;
    IMU imu;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private IntakeAction intake;
    private SpecClawAction specClaw;

    private ExtendoAction extendo;
    private SweeperAction sweeper;

    VariantMachine sliderMachine;
    VariantMachine armMachine;
    VariantMachine specMachine;
    VariantMachine extendoMachine;
    VariantMachine intakeMachine;
    VariantMachine wristMachine;
    VariantMachine sweeperMachine;

    Gamepad old1 = new Gamepad();
    Gamepad old2 = new Gamepad();

    enum SliderState {
        RESET,
        SPECPICKUP,
        HIGHCHAMBERLOAD,
        HIGHCHAMBERSCORE,
        HIGHBASKET,
        LOWBASKET
    }

    enum ArmState {
        STOWED,
        STOWEDOPTION,
        PICKUP,
        SCORE,
        HOVER
    }

    enum SpecState {
        CLOSED,
        DETECTCLOSED,

        OPEN
    }

    enum ExtendoState {
        IN,
        OUT
    }

    enum IntakeState {
        ON,
        OFF,
        REVERSE
    }

    enum WristState {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        NONE
    }

    enum SweeperState {
        IN,
        OUT
    }

    public boolean facingSpecimen(){
        double botHeading = Math.toDegrees(follower.getPose().getHeading());
        boolean facingOurs = botHeading >-45 && botHeading < 45;
        boolean facingTheirs = botHeading < -135|| botHeading > 135;
        return facingOurs|| facingTheirs;
    }

    @Override
    public void start() {
        slider = new SliderAction(hardwareMap);
        wrist = new WristAction(hardwareMap);
        specClaw = new SpecClawAction(hardwareMap);
        arm = new ArmAction(hardwareMap);
        intake = new IntakeAction(hardwareMap);
        extendo = new ExtendoAction(hardwareMap);
        sweeper = new SweeperAction(hardwareMap);

        old1.copy(gamepad1);

        old2.copy(gamepad2);

        sliderMachine = new VariantMachineBuilder()
                .variant(SliderState.RESET)
                .onEnter(() -> slider.reset())
                .setSwitch(() -> gamepad2.a)
                .setDefault(true)
                .variant(SliderState.SPECPICKUP)
                .onEnter(() -> {
                    slider.specLoad();
                    armMachine.setState(ArmState.STOWED);
                })
                .afterTime(250, () -> specMachine.setState(SpecState.OPEN))
                .setSwitch(() -> gamepad2.share)
                .variant(SliderState.HIGHCHAMBERLOAD)
                .onEnter(() -> {
                    slider.highChamberLoad();
                })
                .setSwitch(() -> gamepad2.options && !old2.options)
                .variant(SliderState.HIGHCHAMBERSCORE)
                .onEnter(() -> {
                    slider.highChamberScore();
                    extendoMachine.setState(ExtendoState.IN);
                    wristMachine.setState(WristState.UP);
                })
                .afterTime(500, () -> specMachine.setState(SpecState.OPEN))
                .setSwitch(() -> gamepad2.options && !old2.options)
                .variant(SliderState.HIGHBASKET)
                .onEnter(() -> slider.highBasket())
                .setSwitch(() -> gamepad2.y && sliderMachine.getState() == SliderState.RESET)
                .variant(SliderState.LOWBASKET)
                .onEnter(() -> slider.lowBasket())
                .setSwitch(() -> gamepad2.x && sliderMachine.getState() == SliderState.RESET)
                .build();
        sliderMachine.start();

        armMachine = new VariantMachineBuilder()
                .variant(ArmState.STOWED)
                .onEnter(() -> arm.stow())
                .setSwitch(() -> extendoMachine.getState() == ExtendoState.IN && intakeMachine.getState() == IntakeState.OFF && gamepad2.a && !old2.a)
                .setDefault(true)
                .variant(ArmState.STOWEDOPTION)
                .onEnter(() -> arm.stow())
                .setSwitch(() -> gamepad2.options)
                .variant(ArmState.PICKUP)
                .onEnter(() -> arm.armPickup())
                .setSwitch(() -> sliderMachine.getState() == SliderState.RESET && armMachine.getState() == ArmState.HOVER && gamepad2.b && !old2.b)
                .variant(ArmState.SCORE)
                .onEnter(() -> arm.armScoreTele())
                .setSwitch(() -> (gamepad2.y && sliderMachine.getState() == SliderState.HIGHBASKET) || (gamepad2.x && sliderMachine.getState() == SliderState.LOWBASKET))
                .variant(ArmState.HOVER)
                .onEnter(() -> arm.armHover())
                .setSwitch(() -> (sliderMachine.getState() == SliderState.RESET && gamepad2.b && !old2.b))
                .build();
        armMachine.start();

        specMachine = new VariantMachineBuilder()
                .variant(SpecState.CLOSED)
                .onEnter(() -> specClaw.closeClaw())
                .setSwitch(() -> gamepad2.right_trigger > 0)
                .setDefault(true)
                .variant(SpecState.DETECTCLOSED)
                .onEnter(() -> specClaw.closeClaw())
                .afterTime(150, () -> {
                    sliderMachine.setState(SliderState.HIGHCHAMBERLOAD);
                    extendoMachine.setState(ExtendoState.IN);
                })
                .setSwitch(() -> specClaw.specimenDetected() && sliderMachine.getState() == SliderState.RESET)
                .variant(SpecState.OPEN)
                .onEnter(() -> specClaw.openClaw())
                .setSwitch(() -> gamepad2.left_trigger > 0)
                .build();
        specMachine.start();

        extendoMachine = new VariantMachineBuilder()
                .variant(ExtendoState.IN)
                .onEnter(() -> extendo.goTo(MConstants.extendoIn))
                .setSwitch(() -> gamepad2.a || (gamepad2.y && sliderMachine.getState() == SliderState.HIGHBASKET) || (gamepad2.x && sliderMachine.getState() == SliderState.LOWBASKET))
                .setDefault(true)
                .variant(ExtendoState.OUT)
                .onEnter(() -> extendo.goTo(MConstants.extendoOut))
                .setSwitch(() -> (sliderMachine.getState() == SliderState.RESET && gamepad2.b))
                .build();
        extendoMachine.start();

        intakeMachine = new VariantMachineBuilder()
                .variant(IntakeState.OFF)
                .onEnter(() -> {
                    intake.stoptake();
                    intake.clearAction();

                    if (intake.intakeFull()) {
                        if (facingSpecimen()) {
                            armMachine.setState(ArmState.HOVER);
                        } else {
                            armMachine.setState(ArmState.STOWED);
                            wristMachine.setState(WristState.LEFT);
                        }
                    }
                })
                .setSwitch(() -> gamepad2.a || (intake.intakeFull() && intakeMachine.getState() == IntakeState.ON))
                .setDefault(true)
                .variant(IntakeState.ON)
                .onEnter(() -> intake.intake())
                .setSwitch(() -> (sliderMachine.getState() == SliderState.RESET && gamepad2.b) || gamepad2.right_bumper)
                .variant(IntakeState.REVERSE)
                .onEnter(() -> intake.outake())
                .setTrigger(() -> gamepad2.left_bumper)
                .build();
        intakeMachine.start();

        wristMachine = new VariantMachineBuilder()
                .variant(WristState.UP)
                .onEnter(() -> wrist.goTo(MConstants.wristUp))
                .setSwitch(() -> gamepad2.dpad_up)
                .setDefault(true)
                .variant(WristState.DOWN)
                .onEnter(() -> wrist.goTo(MConstants.wristDown))
                .setSwitch(() -> gamepad2.dpad_down)
                .variant(WristState.LEFT)
                .onEnter(() -> wrist.goTo(MConstants.wristLeft))
                .setSwitch(() -> gamepad2.dpad_left)
                .variant(WristState.RIGHT)
                .onEnter(() -> wrist.goTo(MConstants.wristRight))
                .setSwitch(() -> gamepad2.dpad_right)
                .variant(WristState.NONE)
                .setSwitch(() -> gamepad2.left_stick_x != 0.0f)
                .build();
        wristMachine.start();

        sweeperMachine = new VariantMachineBuilder()
                .variant(SweeperState.IN)
                .onEnter(() -> sweeper.setPosition(MConstants.flipperIn))
                .setDefault(true)
                .variant(SweeperState.OUT)
                .onEnter(() -> sweeper.setPosition(MConstants.flipperOutTeleOP))
                .setTrigger(() -> gamepad1.a)
                .build();
        sweeperMachine.start();
    }

    @Override
    public void loop() {
        // Update all Subsystems
        follower.update();
        sliderMachine.update();
        armMachine.update();
        specMachine.update();
        extendoMachine.update();
        intakeMachine.update();
        wristMachine.update();
        sweeperMachine.update();

        old1.copy(gamepad1);
        old2.copy(gamepad2);

        /* Telemetry Outputs of our Follower */
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Current Slider State: ", sliderMachine.getState());
        telemetry.addData("Current Wrist State: ", wristMachine.getState());
        telemetry.addData("Current Extendo L: ", extendo.extendoL.getPosition());
        telemetry.addData("Current Extendo R: ", extendo.extendoR.getPosition());

        if (gamepad1.right_trigger > 0.01 && !follower.isBusy()) {
            follower.followPath(follower.pathBuilder().addBezierLine(new Point(follower.getPose()), new Point(new Pose(17, 128, Math.toRadians(135)))).setLinearHeadingInterpolation(follower.getPose().getHeading(), Math.toRadians(135)).build(), false);
        }
        if (!follower.isBusy()) {
            updateDriveVelocities();
        }

        updateWristPosition();

        telemetry.update();
    }

    void updateWristPosition() {
        double wristPos = wrist.wrist.getPosition();
        if(gamepad2.left_stick_x < 0 && wristPos < 1){
            wristPos +=0.03 * -gamepad2.left_stick_x;
        }else if(gamepad2.left_stick_x > 0 && wristPos > -1){
            wristPos -=0.03 * gamepad2.left_stick_x;
        }
        wrist.wrist.setPosition(wristPos);
    }

    void updateDriveVelocities() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if (gamepad1.options) {
            imu.resetYaw();
        }
        double botHeading = follower.getPose().getHeading();
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        float speedMultiplier = 1.0f;
        if (gamepad1.b) {
            speedMultiplier = 0.5f;
        }

        leftFront.setPower(frontLeftPower * speedMultiplier);
        leftBack.setPower(backLeftPower * speedMultiplier);
        rightFront.setPower(frontRightPower * speedMultiplier);
        rightBack.setPower(backRightPower * speedMultiplier);
    }

    /** This method is call once when init is played, it initializes the follower **/
    @Override
    public void init() {
        if (Data.getInstance().currPose != null)
            startPose = Data.getInstance().currPose;
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightBack = hardwareMap.dcMotor.get("rightBack");
        imu = hardwareMap.get(IMU.class, "imu");
    }

    /** This method is called once at the start of the OpMode. **/

    /** We do not use this because everything automatically should disable **/
    @Override
    public void stop() {
    }
}