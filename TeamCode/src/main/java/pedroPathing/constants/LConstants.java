package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class LConstants {
    static {
        ThreeWheelIMUConstants.forwardTicksToInches = .0029517817865700974;
        ThreeWheelIMUConstants.strafeTicksToInches = .002956906371274946;
        ThreeWheelIMUConstants.turnTicksToInches = .0020191615827405977;
        ThreeWheelIMUConstants.leftY = 6.5;
        ThreeWheelIMUConstants.rightY = -6.5;
        ThreeWheelIMUConstants.strafeX = -3.625;
        ThreeWheelIMUConstants.leftEncoder_HardwareMapName = "leftFront";
        ThreeWheelIMUConstants.rightEncoder_HardwareMapName = "rightFront";
        ThreeWheelIMUConstants.strafeEncoder_HardwareMapName = "leftBack";
        ThreeWheelIMUConstants.leftEncoderDirection = Encoder.REVERSE;
        ThreeWheelIMUConstants.rightEncoderDirection = Encoder.REVERSE;
        ThreeWheelIMUConstants.strafeEncoderDirection = Encoder.REVERSE;
        ThreeWheelIMUConstants.IMU_HardwareMapName = "imu";
        ThreeWheelIMUConstants.IMU_Orientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.RIGHT);
    }
}




