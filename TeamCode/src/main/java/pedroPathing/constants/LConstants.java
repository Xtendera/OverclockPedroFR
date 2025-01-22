package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class LConstants {
    static {
        PinpointConstants.forwardY = -6.5;
        PinpointConstants.strafeX = -3.625;
        PinpointConstants.distanceUnit = DistanceUnit.INCH;
        PinpointConstants.hardwareMapName = "pinpoint";
        PinpointConstants.useYawScalar = false;
        PinpointConstants.yawScalar = 1.0;
        PinpointConstants.useCustomEncoderResolution = false;
        PinpointConstants.encoderResolution = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD;
        PinpointConstants.customEncoderResolution = 13.26291192;
        PinpointConstants.forwardEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        PinpointConstants.strafeEncoderDirection = GoBildaPinpointDriver.EncoderDirection.REVERSED;
    }
}




