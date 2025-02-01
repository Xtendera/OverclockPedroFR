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
    public static double specLoad = 125*ticksPerMM;
    public static double highBasketScore = 720*ticksPerMM;

    // Wrist
    public static double wristUp = 0.5;
    public static double wristLeft = 0.8;

    // Arm
    public static double armStowed = 0.685;
    public static double armPickup = 0.06;
    public static double armScore = 0.58; // 0.5 0.55

    // Spec Claw
    public static double specClawClosed = 0.7;
    public static double specClawOpen = 0.35;
}
