package pedroPathing.paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
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

@Autonomous(name = "KFC4_Park", group = "AAA")
public class KFC4_Park extends OpMode {
    private Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer;

    private SliderAction slider;
    private WristAction wrist;
    private ArmAction arm;
    private IntakeAction intake;
    private SpecClawAction specClaw;

    private final Pose startPose = new Pose(8, 114, Math.toRadians(0));

    private final Pose scoreControlPose = new Pose(36.6, 116.7);
    private final Pose scorePose = new Pose(18, 126, Math.toRadians(135));

    private int pathState;
    private PathChain scorePreload;

    private void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addBezierCurve(new Point(startPose), new Point(scoreControlPose), new Point(scorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
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
                if (pathTimer.getElapsedTime() >= 3000 || intake.outake()) {
                    intake.stoptake();
                    intake.clearAction();
                    arm.stow();
                    setPathState(3);
                }
                break;
            case 3:
                if (pathTimer.getElapsedTime() >= 500) {
                    slider.reset();
                    setPathState(4);
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
