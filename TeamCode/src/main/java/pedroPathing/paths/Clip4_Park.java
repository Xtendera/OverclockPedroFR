package pedroPathing.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
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

@Autonomous(name = "Clip4_Park", group = "AAA")
public class Clip4_Park extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState;

    private int loopState = 0;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private SpecClawAction specClaw;
    private ExtendoAction extendo;
    private IntakeAction intake;
    private SweeperAction sweeper;

    /** Start Pose of our robot */
    private final Pose startPose = new Pose(8, 64, Math.toRadians(0));

    /** Scoring Pose of our robot. It is facing the submersible at a -45 degree (315 degree) angle. */

//    private final Pose scoreSlidePose = new Pose(32, 69, Math.toRadians(0));


    private final Pose scorePose = new Pose(38, 69, Math.toRadians(0));

    private final Pose pickup1PrePose = new Pose(21, 35, Math.toRadians(0));
    private final Pose pickup1Pose = new Pose(58, 35, Math.toRadians(0));
    private final Pose pickup1Dep = new Pose(10, 32, Math.toRadians(270));

    private final Pose pickup2PrePose = new Pose(21, 26, Math.toRadians(345));
    private final Pose pickup2Pose = new Pose(21, 23, Math.toRadians(345));
    private final Pose pickup2Dep = new Pose(21, 29, Math.toRadians(255));

    private final Pose pickup3PrePose = new Pose(45, 32, Math.toRadians(270));
    private final Pose pickup3Pose = new Pose(45, 29, Math.toRadians(270));
    private final Pose pickup3Dep = new Pose(12.8, 10);

    private final Pose specCollectPose = new Pose(8.3, 18, Math.toRadians(180));
    private final Pose specSlidePose = new Pose(8.3, 28, Math.toRadians(180));
    

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private PathChain scorePreload, pickup1Pre, pickup1, dep1, pickup2Pre, pickup2, dep2, pickup3Pre, pickup3, dep3;


    private List<PathChain> prePickups = new ArrayList<PathChain>();
    private List<PathChain> pickups = new ArrayList<PathChain>();

    private List<PathChain> deps = new ArrayList<PathChain>();
    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {

        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePose)))
                .setConstantHeadingInterpolation(startPose.getHeading())
                .build();
        /* Here is an example for Constant Interpolation
        scorePreload.setConstantInterpolation(startPose.getHeading()); */
        pickup1Pre = follower.pathBuilder()
                .addBezierLine(new Point(scorePose), new Point(pickup1PrePose))
                .setConstantHeadingInterpolation(pickup1PrePose.getHeading())
                .build();

        pickup1 = follower.pathBuilder()
                .addBezierLine(new Point(pickup1PrePose), new Point(pickup1Pose))
                .setLinearHeadingInterpolation(pickup1PrePose.getHeading(), pickup1Pose.getHeading())
                .build();
        dep1 = follower.pathBuilder()
                .addBezierLine(new Point(pickup1Pose), new Point(pickup1Dep))
                .setConstantHeadingInterpolation(pickup1Dep.getHeading())
                .build();

        pickup2Pre = follower.pathBuilder()
                .addBezierLine(new Point(pickup1Dep), new Point(pickup2PrePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2PrePose.getHeading())
                .build();

        pickup2 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2PrePose), new Point(pickup2Pose))
                .setConstantHeadingInterpolation(pickup2PrePose.getHeading())
                .build();

        dep2 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2Pose), new Point(pickup2Dep))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), pickup2Dep.getHeading())
                .build();

        pickup3Pre = follower.pathBuilder()
                .addBezierLine(new Point(pickup2Dep), new Point(pickup3PrePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3PrePose.getHeading())
                .build();
        pickup3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup3PrePose), new Point(pickup3Pose))
                .setConstantHeadingInterpolation(pickup3PrePose.getHeading())
                .build();

        dep3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup3Pose), new Point(pickup3Dep))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), pickup3Dep.getHeading())
                .build();

        prePickups.add(pickup1Pre);
        prePickups.add(pickup2Pre);
        prePickups.add(pickup3Pre);

        pickups.add(pickup1);
        pickups.add(pickup2);
        pickups.add(pickup3);

        deps.add(dep1);
        deps.add(dep2);
        deps.add(dep3);
    }

    /** This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because wof the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on. */
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                /**Raise Slide**/
                wrist.wristUp();
                slider.highChamberLoad();
                follower.followPath(scorePreload, true);
                setPathState(1);
                break;
            case 1:
                /**Go to clipping position IF not moving already (which it shouldn't (but just in case))**/
                if (slider.highChamberLoad() && !follower.isBusy()) {
                    slider.clearAction();
                    setPathState(2);
                }
                break;
            case 2:
                /**Go down to clip the clip**/
                if (!follower.isBusy()) {
                    slider.highChamberScore();
                    setPathState(3);
                }
                break;
            case 3:
                if (slider.highChamberScore() && pathTimer.getElapsedTime() > 500) {
                    slider.clearAction();
                    setPathState(4);
                }
                break;
            case 4:
                if (pathTimer.getElapsedTime() > 250) {
                    follower.followPath(prePickups.get(loopState), false);
                    sweeper.setPosition(MConstants.flipperOut);
                    specClaw.openClaw();
                    slider.reset();
//                    extendo.goTo(MConstants.extendoOut);
//                    wrist.goTo(MConstants.wristSpecStrafe);
                    setPathState(5);
                }
                break;
            case 5:
                if (slider.reset()) {
                    slider.clearAction();
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
//                    arm.armPickup();
//                    intake.intake();
                    setPathState(7);
                }
                break;
            case 7:
                follower.followPath(pickups.get(loopState), false);
                setPathState(8);
                break;
            case 8:
                if (!follower.isBusy()) {
//                    follower.followPath(pickup3Post);
                    follower.followPath(deps.get(loopState));
                    setPathState(9);
                }
                break;
            case 9:
                if (!follower.isBusy()) {
                    if (loopState == 3) {
                        setPathState(-1);
                    } else {
                        setPathState(4);
                    }
                }
                break;
//            case 5:
//                if (!follower.isBusy()) {
//                    follower.followPath(specWait, true);
//                    slider.clearAction();
//                    setPathState(6);
//                }
//                break;
//            case 6:
//                if (!follower.isBusy() && pathTimer.getElapsedTime() > 400) {
//                    follower.followPath(specCollect, true);
//                    setPathState(7);
//                }
//                break;
//            case 7:
//                if (!follower.isBusy() && pathTimer.getElapsedTime() > 600) {
//                    follower.followPath(specSlide);
//                    setPathState(8);
//                }
//                break;
//            case 8:
//                if (!follower.isBusy()) {
//                    specClaw.closeClaw();
//                    slider.highChamberLoad();
//                    follower.followPath(scoreSpec2, true);
//                    setPathState(9);
//                }
//                break;
//            case 9:
//                if (!follower.isBusy()) {
//                    slider.highChamberScore();
//                    setPathState(10);
//                }
//                break;
//            case 10:
//                if (slider.highChamberScore() && pathTimer.getElapsedTime() > 350) {
//                    slider.clearAction();
//                    specClaw.openClaw();
//                    setPathState(11);
//                }
//                break;
        }
    }

    /** These change the states of the paths and actions
     * It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        /** get hardware map... **/
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        slider = new SliderAction(hardwareMap);
        wrist = new WristAction(hardwareMap);
        specClaw = new SpecClawAction(hardwareMap);
        arm = new ArmAction(hardwareMap);
        extendo = new ExtendoAction(hardwareMap);
        intake = new IntakeAction(hardwareMap);
        sweeper = new SweeperAction(hardwareMap);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        /** set the starting pos to starting pos **/
        follower.setStartingPose(startPose);
        buildPaths();
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
    }
}

