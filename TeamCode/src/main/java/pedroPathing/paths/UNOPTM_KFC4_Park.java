package pedroPathing.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.actions.ArmAction;
import pedroPathing.actions.IntakeAction;
import pedroPathing.actions.SliderAction;
import pedroPathing.actions.SpecClawAction;
import pedroPathing.actions.WristAction;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

//@Autonomous(name = "UNOPTM_KFC4_Park", group = "AAA")
public class UNOPTM_KFC4_Park extends OpMode {
    private Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private IntakeAction intake;
    private SpecClawAction specClaw;

    private final Pose startPose = new Pose(8, 114, Math.toRadians(90));

    private final Pose scoreControlPose = new Pose(36.6, 116.7);
    private final Pose scorePose = new Pose(18.5, 129, Math.toRadians(135));

    private final Pose pickup1PrePose = new Pose(27.75, 117.5, Math.toRadians(0));
    private final Pose pickup1Pose = new Pose(27.75, 122, Math.toRadians(0));

    private final Pose pickup2PrePose = new Pose(28.5, 124.5, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(28.5, 129, Math.toRadians(0));

    private final Pose pickup3PrePose = new Pose(45, 118, Math.toRadians(90));
    private final Pose pickup3Pose = new Pose(45, 123, Math.toRadians(90));
    private final Pose pickup3PostPose = new Pose(45, 120, Math.toRadians(90));

    private int pathState;
    private PathChain scorePreload, pickup1Pre, pickup1, score1, pickup2Pre, pickup2, score2, pickup3Pre, pickup3, pickup3Post, score3;

    private PathChain currPickupPre, currPickup, currScore;

    private void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addBezierCurve(new Point(startPose), new Point(scoreControlPose), new Point(scorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

        pickup1Pre = follower.pathBuilder()
                .addBezierLine(new Point(scorePose), new Point(pickup1PrePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1PrePose.getHeading())
                .build();

        pickup1 = follower.pathBuilder()
                .addBezierLine(new Point(pickup1PrePose), new Point(pickup1Pose))
                .setConstantHeadingInterpolation(pickup1PrePose.getHeading())
                .build();
        score1 = follower.pathBuilder()
                .addBezierLine(new Point(pickup1Pose), new Point(scorePose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        pickup2Pre = follower.pathBuilder()
                .addBezierLine(new Point(scorePose), new Point(pickup2PrePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2PrePose.getHeading())
                .build();

        pickup2 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2PrePose), new Point(pickup2Pose))
                .setConstantHeadingInterpolation(pickup2PrePose.getHeading())
                .build();
        score2 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2Pose), new Point(scorePose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();

        pickup3Pre = follower.pathBuilder()
                .addBezierLine(new Point(scorePose), new Point(pickup3PrePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3PrePose.getHeading())
                .build();
        pickup3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup3PrePose), new Point(pickup3Pose))
                .setConstantHeadingInterpolation(pickup3PrePose.getHeading())
                .build();
        pickup3Post = follower.pathBuilder()
                .addBezierLine(new Point(pickup3Pose), new Point(pickup3PostPose))
                .setConstantHeadingInterpolation(pickup3Pose.getHeading())
                .build();
        score3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup3PostPose), new Point(scorePose))
                .setLinearHeadingInterpolation(pickup3PostPose.getHeading(), scorePose.getHeading())
                .build();
    }
    public void autonomousPathUpdate() {
        telemetry.addData("Prox: ", intake.intakeFull());
        telemetry.update();
        switch (pathState) {
            case 0:
                wrist.wristUp();
                slider.highBasketScore();
                follower.followPath(scorePreload);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy() && slider.highBasketScore()) {
                    slider.clearAction();
                    arm.armScore();
                    intake.outake();
                    setPathState(2);
                }
                break;
            case 2:
                if (pathTimer.getElapsedTime() >= 3000 || (intake.outake() && pathTimer.getElapsedTime() >= 1000)) {
                    intake.stoptake();
                    intake.clearAction();
                    arm.stow();
                    setPathState(3);
                }
                break;
            case 3:
                if (pathTimer.getElapsedTime() >= 500) {
                    slider.reset();
                    currPickupPre = pickup1Pre;
                    currPickup = pickup1;
                    currScore = score1;
                    setPathState(4);
                }
                break;
            case 4:
                if (slider.reset()) {
                    slider.clearAction();
                    follower.followPath(currPickupPre, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    if (currScore == score3) {
                        wrist.wristUp();
                    } else {
                        wrist.wristLeft();
                    }
                    arm.armPickup();
                    intake.intake();
                    setPathState(6);
                }
                break;
            case 6:
                if (pathTimer.getElapsedTime() >= 350) {
                    follower.followPath(currPickup, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (intake.intake() || pathTimer.getElapsedTime() >= 2500) {
                    if (currScore == score3) {
                        follower.followPath(pickup3Post);
                    } else {
                        arm.stow();
                        wrist.wristUp();
                        slider.highBasketScore();
                    }
                    intake.stoptake();
                    intake.clearAction();
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    if (currScore == score3) {
                        arm.stow();
                        wrist.wristUp();
                        slider.highBasketScore();
                    }
                    setPathState(9);
                }
                break;
            case 9:
                if (slider.highBasketScore()) {
                    slider.clearAction();
                    follower.followPath(currScore, true);
                    setPathState(10);
                }
                break;
            case 10:
                if (!follower.isBusy()) {
                    arm.armScore();
                    setPathState(11);
                }
                break;
            case 11:
                if (pathTimer.getElapsedTime() >= 450) {
                    intake.outake();
                    setPathState(12);
                }
                break;
            case 12:
                if (pathTimer.getElapsedTime() >= 3000 || (intake.outake() && pathTimer.getElapsedTime() >= 1000)) {
                    intake.stoptake();
                    intake.clearAction();
                    slider.reset();
                    arm.stow();
                    if (currScore != score3) {
                        if (currScore == score1) {
                            currPickupPre = pickup2Pre;
                            currPickup = pickup2;
                            currScore = score2;
                        } else {
                            currPickupPre = pickup3Pre;
                            currPickup = pickup3;
                            currScore = score3;
                        }
                        setPathState(4);
                    } else {
                        setPathState(-1);
                    }
                }
                break;
        }
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        slider = new SliderAction(hardwareMap);
        wrist = new WristAction(hardwareMap);
        specClaw = new SpecClawAction(hardwareMap);
        arm = new ArmAction(hardwareMap);
        intake = new IntakeAction(hardwareMap);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(8, 112, Math.toRadians(90)));
        buildPaths();
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();

    }
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
}
