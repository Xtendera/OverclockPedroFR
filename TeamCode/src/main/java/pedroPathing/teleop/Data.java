package pedroPathing.teleop;

import com.pedropathing.localization.Pose;

public class Data {
    private static Data instance;

    public Pose currPose;
    private Data() {}

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }
}
