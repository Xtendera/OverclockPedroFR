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

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "KFC4_Park", group = "AAA")
public class KFC4_Park extends OpMode {
    private Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
    private PathChain chain;

    private void buildPaths() {
              chain = follower.pathBuilder().addPath(
                // Line 1
                new BezierCurve(
                        new Point(8.000, 112.000, Point.CARTESIAN),
                        new Point(22.172, 116.694, Point.CARTESIAN),
                        new Point(18.000, 126.000, Point.CARTESIAN)
                )
        )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
                .addPath(
                        // Line 2
                        new BezierLine(
                                new Point(18.000, 126.000, Point.CARTESIAN),
                                new Point(30.574, 122.528, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(0)).build();
    }
    private boolean isRunning = false;
    public void autonomousPathUpdate() {
        if (!isRunning) {
            follower.followPath(chain);
            isRunning = true;
        }
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

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
