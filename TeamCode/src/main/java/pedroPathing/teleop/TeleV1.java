package pedroPathing.teleop;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import pedroPathing.actions.ArmAction;
import pedroPathing.actions.ExtendoAction;
import pedroPathing.actions.IntakeAction;
import pedroPathing.actions.SliderAction;
import pedroPathing.actions.SpecClawAction;
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
    private final Pose startPose = new Pose(8, 114, Math.toRadians(90));

    DcMotor leftFront, rightFront, leftBack, rightBack;
    IMU imu;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private IntakeAction intake;
    private SpecClawAction specClaw;

    private ExtendoAction extendo;

    VariantMachine sliderMachine;
    VariantMachine armMachine;
    VariantMachine specMachine;

    enum SliderState {
        RESET,
        SPECPICKUP,
        HIGHCHAMBERLOAD,
        HIGHCHAMBERSCORE
    }

    enum ArmState {
        STOWED,
        STOWEDOPTION,
        PICKUP,
        SCORE
    }

    enum SpecState {
        CLOSED,
        DETECTCLOSED,

        OPEN
    }

    @Override
    public void start() {
        slider = new SliderAction(hardwareMap);
        wrist = new WristAction(hardwareMap);
        specClaw = new SpecClawAction(hardwareMap);
        arm = new ArmAction(hardwareMap);
        intake = new IntakeAction(hardwareMap);
        extendo = new ExtendoAction(hardwareMap);

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
                .setSwitch(() -> gamepad2.options)
                .variant(SliderState.HIGHCHAMBERSCORE)
                .onEnter(() -> {
                    slider.highChamberScore();
                    extendo.goTo(MConstants.extendoIn); // TODO: Add machine
                })
                .afterTime(500, () -> specMachine.setState(SpecState.OPEN))
                .setSwitch(() -> gamepad2.options)
                .build();
        sliderMachine.start();

        armMachine = new VariantMachineBuilder()
                .variant(ArmState.STOWED)
                .onEnter(() -> arm.stow())
                .setDefault(true)
                .variant(ArmState.STOWEDOPTION)
                .onEnter(() -> {
                    arm.stow();
                })
                .setSwitch(() -> gamepad2.options)
                .variant(ArmState.PICKUP)
                .onEnter(() -> arm.armPickup())
                .setSwitch(() -> gamepad1.dpad_up)
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
                    extendo.goTo(MConstants.extendoIn); // TODO: Add machine
                })
                .setSwitch(() -> specClaw.specimenDetected() && sliderMachine.getState() == SliderState.RESET)
                .variant(SpecState.OPEN)
                .onEnter(() -> specClaw.openClaw())
                .setSwitch(() -> gamepad2.left_trigger > 0)
                .build();
        specMachine.start();

//        sliderMachine = new StateMachineBuilder()
//                .state(SliderState.IDLE)
//                .onEnter(() -> {
//                    slider.sliderLeftMotor.setPower(0);
//                    slider.sliderRightMotor.setPower(0);
//                })
//                .transition(() -> gamepad1.dpad_up)
//                .state(SliderState.MANUP)
//                .onEnter(() -> {
//                    slider.sliderLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                    slider.sliderRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                    slider.sliderLeftMotor.setPower(0.5);
//                    slider.sliderRightMotor.setPower(0.5);
//                })
//                .transition(() -> gamepad1.dpad_down)
//                .state(SliderState.MANDOWN)
//                .onEnter(() -> {
//                    slider.sliderLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                    slider.sliderRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                    slider.sliderLeftMotor.setPower(-0.5);
//                    slider.sliderRightMotor.setPower(-0.5);
//                })
//                .transition(() -> !gamepad1.dpad_up && !gamepad1.dpad_down, SliderState.IDLE)
//                .build();
//        sliderMachine.start();
    }

    @Override
    public void loop() {

        follower.update();
        sliderMachine.update();
        armMachine.update();
        specMachine.update();

        /* Telemetry Outputs of our Follower */
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Current State: ", sliderMachine.getState());

        if (gamepad1.right_trigger > 0.01 && !follower.isBusy()) {
            follower.followPath(follower.pathBuilder().addBezierLine(new Point(follower.getPose()), new Point(new Pose(17, 128, Math.toRadians(135)))).setLinearHeadingInterpolation(follower.getPose().getHeading(), Math.toRadians(135)).build(), false);
        }
        if (!follower.isBusy()) {
            updateDriveVelocities();
        }

        telemetry.update();
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

        leftFront.setPower(frontLeftPower);
        leftBack.setPower(backLeftPower);
        rightFront.setPower(frontRightPower);
        rightBack.setPower(backRightPower);
    }

    /** This method is call once when init is played, it initializes the follower **/
    @Override
    public void init() {
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