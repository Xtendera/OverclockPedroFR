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

import pedroPathing.actions.ArmAction;
import pedroPathing.actions.SliderAction;
import pedroPathing.actions.SpecClawAction;
import pedroPathing.actions.WristAction;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

/**
 * This is an example auto that showcases movement and control of two servos autonomously.
 * It is a 0+4 (Specimen + Sample) bucket auto. It scores a neutral preload and then pickups 3 samples from the ground and scores them before parking.
 * There are examples of different ways to build paths.
 * A path progression method has been created and can advance based on time, position, or other factors.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 11/28/2024
 */

@Autonomous(name = "Clip4_Park", group = "AAA")
public class Clip4_Park extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private SpecClawAction specClaw;

    /** Start Pose of our robot */
    private final Pose startPose = new Pose(8, 64, Math.toRadians(0));

    /** Scoring Pose of our robot. It is facing the submersible at a -45 degree (315 degree) angle. */

    private final Pose scoreSlidePose = new Pose(32, 69, Math.toRadians(0));
    private final Pose scorePose = new Pose(38, 69, Math.toRadians(0));

    private final Pose pushPre1ControlPose = new Pose(2.33, 45.2);
    private final Pose pushPre1Control2Pose = new Pose(60, 36);
    private final Pose pushPre1Pose = new Pose(62, 20, Math.toRadians(90));
    private final Pose push1Pose = new Pose(27, 20, Math.toRadians(90));

    private final Pose pushPre2ControlPose = new Pose(50, 32);
    private final Pose pushPre2Pose = new Pose(62, 12, Math.toRadians(90));
    private final Pose push2Pose = new Pose(18, 12, Math.toRadians(90));

    private final Pose specWaitPose = new Pose(40, 12, Math.toRadians(180));
    private final Pose specCollectPose = new Pose(14, 14, Math.toRadians(180));
    private final Pose specSlidePose = new Pose(14, 28, Math.toRadians(180));

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private Path scoreSlide;
    private PathChain scorePreload, push2Sample, specWait, specCollect, specSlide;
    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {

        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */

        scoreSlide = new Path(new BezierLine(new Point(startPose), new Point(scoreSlidePose)));

        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreSlidePose), new Point(scorePose)))
                .setConstantHeadingInterpolation(scoreSlidePose.getHeading())
                .build();

        push2Sample = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(pushPre1ControlPose), new Point(pushPre1Control2Pose), new Point(pushPre1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pushPre1Pose.getHeading())
                .addPath(new BezierLine(new Point(pushPre1Pose), new Point(push1Pose)))
                .setConstantHeadingInterpolation(pushPre1Pose.getHeading())
                .addPath(new BezierCurve(new Point(push1Pose), new Point(pushPre2ControlPose), new Point(pushPre2Pose)))
                .setConstantHeadingInterpolation(push1Pose.getHeading())
                .addPath(new BezierLine(new Point(pushPre2Pose), new Point(push2Pose)))
                .setConstantHeadingInterpolation(push2Pose.getHeading())
                .build();

        specWait = follower.pathBuilder()
                .addPath(new BezierLine(new Point(push2Pose), new Point(specWaitPose)))
                .setLinearHeadingInterpolation(push2Pose.getHeading(), specWaitPose.getHeading())
                .build();

        specCollect = follower.pathBuilder()
                .addPath(new BezierLine(new Point(specWaitPose), new Point(specCollectPose)))
                .setConstantHeadingInterpolation(specWaitPose.getHeading())
                .build();

        specSlide = follower.pathBuilder()
                .addPath(new BezierLine(new Point(specCollectPose), new Point(specSlidePose)))
                .setConstantHeadingInterpolation(specCollectPose.getHeading())
                .build();
        /* Here is an example for Constant Interpolation
        scorePreload.setConstantInterpolation(startPose.getHeading()); */
    }

    /** This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on. */
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                /**Raise Slide**/
                wrist.wristUp();
                slider.highChamberLoad();
                follower.followPath(scoreSlide);
                setPathState(1);
                break;
            case 1:
                /**Go to clipping position IF not moving already (which it shouldn't (but just in case))**/
                if (slider.highChamberLoad() && !follower.isBusy()) {
                    slider.clearAction();
                    follower.followPath(scorePreload, true);
                    setPathState(2);
                }
            case 2:
                /**Go down to clip the clip**/
                if (!follower.isBusy()) {
                    slider.highChamberScore();
                    setPathState(3);
                }
                //Yaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaay
                // we did a thing
                break;
            case 3:
                if (slider.highChamberScore() && pathTimer.getElapsedTime() > 600) {
                    slider.clearAction();
                    specClaw.openClaw();
                    setPathState(4);
                }
                break;
            case 4:
                if (pathTimer.getElapsedTime() > 300) {
                    follower.followPath(push2Sample);

                    specClaw.closeClaw();
                    slider.reset();
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(specWait, true);
                    slider.clearAction();
                    slider.specLoad();
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy() && slider.specLoad() && pathTimer.getElapsedTime() > 400) {
                    follower.followPath(specCollect, true);
                    slider.clearAction();
                    specClaw.openClaw();
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy() && pathTimer.getElapsedTime() > 600) {
                    follower.followPath(specSlide);
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    specClaw.closeClaw();
                    setPathState(9);
                }
                break;
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

