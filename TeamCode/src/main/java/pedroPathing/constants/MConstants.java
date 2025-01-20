package pedroPathing.constants;

public class MConstants {
    // Slider
    public static double spoolCircumference = 150;
    public static double slidesGearRatio = 15.2;//18.9; //27.2;
    public static int ticksPerRev = 28;
    public static double ticksPerOutputRev = ticksPerRev*slidesGearRatio;
    public static double ticksPerMM = ticksPerOutputRev/spoolCircumference;

    public static double sliderReset = 0;
    public static double highChamberLoad = 530*ticksPerMM;
    public static double highChamberScore = 410*ticksPerMM;
    public static double specLoad = 145*ticksPerMM;

    // Wrist
    public static double wristUp = 0.5;

    // Arm
    public static double armStowed = 0.685;
    public static double armPickup = 0.06;

    // Spec Claw
    public static double specClawClosed = 0.7;
    public static double specClawOpen = 0.35;
}
