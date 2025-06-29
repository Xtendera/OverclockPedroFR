package pedroPathing.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.List;

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
import pedroPathing.teleop.Data;

@Autonomous(name = "SSKFC5", group = "AAA")
public class SSKFC5 extends OpMode {
    private Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private IntakeAction intake;
    private SpecClawAction specClaw;

    private ExtendoAction extendo;
    private SweeperAction sweeper;

    private int subIndex = 0;

    private final Pose startPose = new Pose(8, 112, Math.toRadians(90));

    private final Pose scoreControlPose = new Pose(36.6, 116.7);
    private final Pose scorePose = new Pose(18.85, 125.4, Math.toRadians(135));
//    private final Pose scorePose2 = new Pose(18.5, 129, Math.toRadians(135));

    private final Pose pickup1PrePose = new Pose(21, 113, Math.toRadians(15));
    private final Pose pickup1Pose = new Pose(21, 116, Math.toRadians(15));

    private final Pose pickup2PrePose = new Pose(21, 118, Math.toRadians(15));
    private final Pose pickup2Pose = new Pose(21, 121, Math.toRadians(15));

    private final Pose pickup3PrePose = new Pose(45, 112, Math.toRadians(90));
    private final Pose pickup3Pose = new Pose(45, 115, Math.toRadians(90));
    private final Pose pickup3PostPose = new Pose(45, 110, Math.toRadians(90));

    private final Pose parkControlPose = new Pose(83, 135);
    private final Pose parkPose = new Pose(63, 98, Math.toRadians(270));

    private final Pose sub1Pose1 = new Pose(60, 98, Math.toRadians(270));
    private final Pose sub1Control = new Pose(83, 135);
    private final Pose sub1Pose2 = new Pose(60, 98, Math.toRadians(260));
    private final Pose sub1Pose3 = new Pose(60, 98, Math.toRadians(285));
    private final Pose sub1Pose4 = new Pose(60, 98, Math.toRadians(300));

    private int pathState;
    private PathChain scorePreload, pickup1Pre, pickup1, score1, pickup2Pre, pickup2, score2, pickup3Pre, pickup3, pickup3Post, score3, park, sub1P1, sub1S1, sub1P2, sub1P3, sub1P4;

    private PathChain currPickupPre, currPickup, currScore;

    private List<PathChain> subs = new ArrayList<PathChain>();

    private void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addBezierCurve(new Point(startPose), new Point(scoreControlPose), new Point(scorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

//        scorePreload = follower.pathBuilder()
//                .addBezierLine(new Point(startPose), new Point(scorePose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
//                .build();

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
        park = follower.pathBuilder()
                .addBezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading())
                .build();
        sub1P1 = follower.pathBuilder()
                .addBezierCurve(new Point(scorePose), new Point(sub1Control), new Point(sub1Pose1))
                .setLinearHeadingInterpolation(scorePose.getHeading(), sub1Pose1.getHeading())
                .build();
        sub1S1 = follower.pathBuilder()
                .addBezierCurve(new Point(sub1Pose1), new Point(sub1Control), new Point(scorePose))
                .setLinearHeadingInterpolation(sub1Pose1.getHeading(), scorePose.getHeading())
                .build();
        sub1P2 = follower.pathBuilder()
                .addBezierLine(new Point(sub1Pose1), new Point(sub1Pose2))
                .setLinearHeadingInterpolation(sub1Pose1.getHeading(), sub1Pose2.getHeading())
                .build();
        sub1P3 = follower.pathBuilder()
                .addBezierLine(new Point(sub1Pose2), new Point(sub1Pose3))
                .setLinearHeadingInterpolation(sub1Pose2.getHeading(), sub1Pose3.getHeading())
                .build();
        sub1P4 = follower.pathBuilder()
                .addBezierLine(new Point(sub1Pose3), new Point(sub1Pose4))
                .setLinearHeadingInterpolation(sub1Pose3.getHeading(), sub1Pose4.getHeading())
                .build();

        subs.add(sub1P2);
        subs.add(sub1P3);
        subs.add(sub1P4);
    }
    public void autonomousPathUpdate() {
        Data.getInstance().currPose = follower.getPose();
        switch (pathState) {
            case 0:
                wrist.wristUp();
                slider.highBasketScore();
                extendo.goTo(MConstants.extendoScore);
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
                if (pathTimer.getElapsedTime() >= 500) {
                    intake.outake();
                    setPathState(3);
                }
                break;
            case 3:
                if (pathTimer.getElapsedTime() >= 3000 || (intake.outake() && pathTimer.getElapsedTime() >= 550)) {
                    intake.stoptake();
                    intake.clearAction();
                    arm.stow();
                    setPathState(4);
                }
                break;
            case 4:
                if (pathTimer.getElapsedTime() >= 500) {
                    slider.reset();
                    wrist.goTo(MConstants.wristStrafe);
                    currPickupPre = pickup1Pre;
                    currPickup = pickup1;
                    currScore = score1;
                    follower.followPath(currPickupPre, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (pathTimer.getElapsedTime() >= 350) {
                    extendo.goTo(MConstants.extendoOut);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy() && slider.reset()) {
                    slider.clearAction();
                    arm.armPickup();
                    intake.intake();
                    setPathState(7);
                }
                break;
            case 7:
                if (pathTimer.getElapsedTime() >= 500) {
                    follower.followPath(currPickup, true);
                    setPathState(8);
                }
                break;
            case 8:
                if (intake.intake() || pathTimer.getElapsedTime() >= 1500) {
//                    follower.followPath(pickup3Post);
                    extendo.goTo(MConstants.extendoScore);
                    arm.stow();
                    wrist.wristUp();
                    slider.highBasketScore();
                    intake.stoptake();
                    intake.clearAction();
                    setPathState(9);
                }
                break;
            case 9:
                if (currScore == score3) {
                    if (pathTimer.getElapsedTime() >= 500) {
                        follower.followPath(currScore, true);
                        setPathState(10);
                    }
                } else {
                    follower.followPath(currScore, true);
                    setPathState(10);
                }
                break;
            case 10:
                if (slider.highBasketScore() && !follower.isBusy()) {
                    slider.clearAction();
                    setPathState(11);
                }
                break;
            case 11:
                if (!follower.isBusy()) {
                    arm.armScore();
//                    intake.outake();
                    setPathState(12);
                }
                break;
            case 12:
                if (pathTimer.getElapsedTime() >= 450) {
                    intake.outake();
                    setPathState(13);
                }
                break;
            case 13:
                if (pathTimer.getElapsedTime() >= 3000 || (intake.outake() && pathTimer.getElapsedTime() >= 550)) {
                    intake.stoptake();
                    intake.clearAction();
                    if (currScore == score2) {
                        wrist.goTo(MConstants.wristUp);
                    } else {
                        wrist.goTo(MConstants.wristStrafe);
                    }
                    if (currScore != score3) {
                        slider.reset();
                        arm.stow();
                        if (currScore == score1) {
                            currPickupPre = pickup2Pre;
                            currPickup = pickup2;
                            currScore = score2;
                        } else {
                            currPickupPre = pickup3Pre;
                            currPickup = pickup3;
                            currScore = score3;
                        }
                        follower.followPath(currPickupPre, true);
                        setPathState(5);
                    } else {
//                        follower.followPath(park, true);
                        extendo.goTo(MConstants.extendoIn);
                        slider.reset();
                        arm.stow();
                        setPathState(14);
                    }
                }
                break;
            case 14:
                if (pathTimer.getElapsedTime() >= 350) {
                    follower.followPath(sub1P1, false);
                    setPathState(15);
                }
                break;
            case 15:
                if (!follower.isBusy()) {
                    sweeper.setPosition(MConstants.flipperOut - 0.05f);
                    setPathState(16);
                }
                break;
            case 16:
                if (pathTimer.getElapsedTime() >= 250) {
                    wrist.wristUp();
                    sweeper.setPosition(MConstants.flipperIn);
                    arm.armPickup();
                    extendo.goTo(MConstants.extendoScore);
                    setPathState(17);
                }
                break;
            case 17:
                if (pathTimer.getElapsedTime() >= 350) {
                    intake.intake(true);
                    extendo.goTo(MConstants.extendoOut);
                    setPathState(18);
                }
                break;
            case 18:
                if (intake.intakeFull() && !intake.intakeFullColored()) {
                    intake.outake();
                    arm.stow();
                    extendo.goTo(MConstants.extendoIn);
                    setPathState(-1);
                } else if (intake.intake(true) || pathTimer.getElapsedTime() > 2000) {
                    intake.stoptake();
                    arm.stow();
                    extendo.goTo(MConstants.extendoIn);
                    setPathState(19);
                }
                break;
            case 19:
                if (pathTimer.getElapsedTime() > 200) {
                    follower.followPath(sub1S1);
//                    arm.armScore();
                    setPathState(20);
                }
                break;
            case 20:
                if (pathTimer.getElapsedTime() > 500) {
                    slider.highBasketScore();
                    setPathState(21);
                }
                break;
            case 21:
                if (!follower.isBusy() & slider.highBasketScore()) {
                    slider.clearAction();
                    arm.armScore();
                    intake.outake();
                    setPathState(-1);
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
        extendo = new ExtendoAction(hardwareMap);
        sweeper = new SweeperAction(hardwareMap);

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
