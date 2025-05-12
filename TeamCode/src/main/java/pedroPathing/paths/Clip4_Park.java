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
    private int clipState = 0;

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

    private final Pose pickup1Pose = new Pose(58, 32, Math.toRadians(270));
    private final Pose pickup1Control = new Pose(14.2, 31.7);
    private final Pose pickup1Dep = new Pose(8.5, 31, Math.toRadians(270));

    private final Pose pickup2Pose = new Pose(58, 26, Math.toRadians(270));
    private final Pose pickup2Control = new Pose(18.3, 43.2);
    private final Pose pickup2Dep = new Pose(8.5, 25,  Math.toRadians(270));

    private final Pose pickup3Pose = new Pose(58, 18, Math.toRadians(270));
    private final Pose pickup3Dep = new Pose(8.5, 18, Math.toRadians(270));

//    private final Pose depBack = new Pose(30, 16, Math.toRadians(225));
    private final Pose depBack = new Pose(20, 29, Math.toRadians(225));

    private final Pose collectPose = new Pose(8.7, 27, Math.toRadians(180));
    private final Pose slidePose = new Pose(8.7, 35, Math.toRadians(180));
    private final Pose clip1Pose = new Pose(38, 70.5, Math.toRadians(0));
    private final Pose clip2Pose = new Pose(38, 72, Math.toRadians(0));
    private final Pose clip3Pose = new Pose(38, 73.5, Math.toRadians(0));

    

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private PathChain scorePreload, pickup1, dep1, pickup2, dep2, pickup3, dep3, depSpecBack, slide, collect1, clip1, collect2, clip2, collect3, clip3;


    private List<Pose> pushPoses = new ArrayList<Pose>();
    private List<Pose> depPoses = new ArrayList<Pose>();
    private List<PathChain> pickups = new ArrayList<PathChain>();

    private List<PathChain> deps = new ArrayList<PathChain>();

    private List<PathChain> collects = new ArrayList<PathChain>();
    private List<PathChain> clips = new ArrayList<PathChain>();
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
        pickup1 = follower.pathBuilder()
                .addBezierCurve(new Point(scorePose), new Point(pickup1Control), new Point(pickup1Pose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();
        dep1 = follower.pathBuilder()
                .addBezierLine(new Point(pickup1Pose), new Point(pickup1Dep))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), pickup1Dep.getHeading())
                .build();


        pickup2 = follower.pathBuilder()
                .addBezierCurve(new Point(pickup1Dep), new Point(pickup2Control),new Point(pickup2Pose))
                .setLinearHeadingInterpolation(pickup1Dep.getHeading(), pickup2Pose.getHeading())
                .build();

        dep2 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2Pose), new Point(pickup2Dep))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), pickup2Dep.getHeading())
                .build();

        pickup3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup2Dep), new Point(pickup3Pose))
                .setLinearHeadingInterpolation(pickup2Dep.getHeading(), pickup3Pose.getHeading())
                .build();

        dep3 = follower.pathBuilder()
                .addBezierLine(new Point(pickup3Pose), new Point(pickup3Dep))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), pickup3Dep.getHeading())
                .build();

        depSpecBack = follower.pathBuilder()
                .addBezierLine(new Point(pickup3Dep), new Point(depBack))
                .setLinearHeadingInterpolation(pickup3Dep.getHeading(), depBack.getHeading())
                .build();

        slide = follower.pathBuilder()
                .addBezierLine(new Point(collectPose), new Point(slidePose))
                .setConstantHeadingInterpolation(slidePose.getHeading())
                .build();

        collect1 = follower.pathBuilder()
                .addBezierLine(new Point(depBack), new Point(collectPose))
                .setLinearHeadingInterpolation(depBack.getHeading(), collectPose.getHeading())
                .build();


        collect2 = follower.pathBuilder()
                .addBezierLine(new Point(clip1Pose), new Point(collectPose))
                .setLinearHeadingInterpolation(clip1Pose.getHeading(), collectPose.getHeading())
                .build();
        collect3 = follower.pathBuilder()
                .addBezierLine(new Point(clip2Pose), new Point(collectPose))
                .setLinearHeadingInterpolation(clip2Pose.getHeading(), collectPose.getHeading())
                .build();

        clip1 = follower.pathBuilder()
                .addBezierLine(new Point(slidePose), new Point(clip1Pose))
                .setLinearHeadingInterpolation(slidePose.getHeading(), clip1Pose.getHeading())
                .build();
        clip2 = follower.pathBuilder()
                .addBezierLine(new Point(slidePose), new Point(clip2Pose))
                .setLinearHeadingInterpolation(slidePose.getHeading(), clip2Pose.getHeading())
                .build();
        clip3 = follower.pathBuilder()
                .addBezierLine(new Point(slidePose), new Point(clip3Pose))
                .setLinearHeadingInterpolation(slidePose.getHeading(), clip3Pose.getHeading())
                .build();

        pushPoses.add(pickup1Pose);
        pushPoses.add(pickup2Pose);
        pushPoses.add(pickup3Pose);

        depPoses.add(pickup1Dep);
        depPoses.add(pickup2Dep);
        depPoses.add(pickup3Dep);

        pickups.add(pickup1);
        pickups.add(pickup2);
        pickups.add(pickup3);

        deps.add(dep1);
        deps.add(dep2);
        deps.add(dep3);

        collects.add(collect1);
        collects.add(collect2);
        collects.add(collect3);

        clips.add(clip1);
        clips.add(clip2);
        clips.add(clip3);
    }

    /** This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because wof the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on. */
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                /**Raise Slide**/
                extendo.goTo(MConstants.extendoIn);
                arm.stow();
                wrist.wristUp();
                slider.highChamberLoad();
                follower.followPath(scorePreload, true);
                setPathState(1);
                break;
            case 1:
                /**Go to clipping position IF not moving already (which it shouldn't (but just in case))**/
                if (slider.highChamberLoad() && (Math.abs(scorePose.getX() - follower.getPose().getX()) <= 2.5f && Math.abs(scorePose.getY() - follower.getPose().getY()) <= 2.5f)) {
                    slider.clearAction();
                    setPathState(2);
                }
                break;
            case 2:
                /**Go down to clip the clip**/
                if (true) {
                    slider.highChamberScore();
                    setPathState(3);
                }
                break;
            case 3:
                if (slider.highChamberScore() && pathTimer.getElapsedTime() > 500) {
                    slider.clearAction();
                    specClaw.openClaw();
                    slider.reset();
                    setPathState(4);
                }
                break;
            case 4:
                if (slider.reset()) {
                    slider.clearAction();
                    follower.followPath(pickups.get(loopState), false);
                    setPathState(5);
                }
                break;
            case 5:
                if (pathTimer.getElapsedTime() >= 400) {
                    sweeper.setPosition(MConstants.flipperIn);
                    setPathState(6);
                }
                break;
            case 6:
                if (Math.abs(pushPoses.get(loopState).getX() - follower.getPose().getX()) <= 3.0f && Math.abs(pushPoses.get(loopState).getY() - follower.getPose().getY()) <= 1.0f) {
//                    follower.followPath(pickup3Post);depPoses
                    sweeper.setPosition(MConstants.flipperOut);
                    follower.followPath(deps.get(loopState));
                    setPathState(7);
                }
                break;
            case 7:
                if (Math.abs(depPoses.get(loopState).getX() - follower.getPose().getX()) <= 3.5f && Math.abs(depPoses.get(loopState).getY() - follower.getPose().getY()) <= 2.0f) {
                    loopState++;
                    if (loopState == 2) {
                        setPathState(8);
                    } else {
                        setPathState(4);
                    }
                }
                break;
            case 8:
                follower.followPath(depSpecBack, false);
                setPathState(9);
            case 9:
                if (!follower.isBusy()) {
                    sweeper.setPosition(MConstants.flipperIn);
                    specClaw.openClaw();
                    follower.followPath(collects.get(clipState));
                    setPathState(10);
                }
                break;
            case 10:
                if (Math.abs(collectPose.getX() - follower.getPose().getX()) <= 2.0f && Math.abs(collectPose.getY() - follower.getPose().getY()) <= 2.0f) {
                    follower.followPath(slide, 0.75, true);
                    setPathState(11);
                }
                break;
            case 11:
                if (!follower.isBusy()) {
                    specClaw.closeClaw();
                    setPathState(12);
                }
                break;
            case 12:
                if (pathTimer.getElapsedTime() >= 200) {
                    slider.highChamberLoad();
                    follower.followPath(clips.get(clipState), true);
                    setPathState(13);
                }
                break;
            case 13:
                if (!follower.isBusy() && slider.highChamberLoad()) {
                    slider.clearAction();
                    slider.highChamberScore();
                    setPathState(14);
                }
                break;
            case 14:
                if (slider.highChamberScore() && pathTimer.getElapsedTime() > 450) {
                    slider.clearAction();
                    specClaw.openClaw();
                    slider.reset();
                    setPathState(15);
                }
                break;
            case 15:
                if (slider.reset()) {
                    slider.clearAction();
//                    follower.followPath(pickups.get(loopState), false);
                    if (clipState == 2) {
                        setPathState(16);
                    } else {
                        clipState++;
                        setPathState(9);
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

