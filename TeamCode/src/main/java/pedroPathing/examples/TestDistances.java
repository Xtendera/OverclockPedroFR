package pedroPathing.examples;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;



@Autonomous(name = "TestDistances", group = "Examples")
public class TestDistances extends OpMode {
    private Follower follower;

    private final Pose startPose = new Pose(0,0, Math.toRadians(0));
    private final Pose interPose = new Pose(48, 0, Math.toRadians(0));
//    private final Pose endPose = new Pose(24, 24, Math.toRadians(45));

    private PathChain triangle;

    private Telemetry telemetryA;

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the FTC Dashboard.
     */
    @Override
    public void loop() {
        follower.update();

//        if (follower.atParametricEnd()) {
//            follower.followPath(triangle, true);
//        }

        follower.telemetryDebug(telemetryA);
    }

    /**
     * This initializes the Follower and creates the PathChain for the path. Additionally, this
     * initializes the FTC Dashboard telemetry.
     */
    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        triangle = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(interPose)))
                .setConstantHeadingInterpolation(startPose.getHeading())
//                .addPath(new BezierLine(new Point(interPose), new Point(endPose)))
//                .setLinearHeadingInterpolation(interPose.getHeading(), endPose.getHeading())
//                .addPath(new BezierLine(new Point(endPose), new Point(startPose)))
//                .setLinearHeadingInterpolation(endPose.getHeading(), startPose.getHeading())
                .build();

        follower.followPath(triangle);

        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryA.addLine("This will run in a roughly triangular shape,"
                + "starting on the bottom-middle point. So, make sure you have enough "
                + "space to the left, front, and right to run the OpMode.");
        telemetryA.update();
    }

}
