package pedroPathing.constants;

public class MConstants {
    // Slider
    public static int spoolCircumference = 150;
    public static double slidesGearRatio = 15.2;//18.9; //27.2;
    public static int ticksPerRev = 28;
    public static double ticksPerOutputRev = ticksPerRev*slidesGearRatio;
    public static double ticksPerMM = ticksPerOutputRev/spoolCircumference;

    public static double sliderReset = 0d;
    public static double highChamberLoad = 410*ticksPerMM;
    public static double highChamberScore = 282*ticksPerMM;
    public static double specLoad = 30*ticksPerMM;
//    public static double highBasketScore = 802*ticksPerMM;
    public static double highBasketScore = 870*ticksPerMM;
    // Wrist
    public static double wristUp = 0.68d;
    public static double wristStrafe = 0.95d;

    public static double wristSpecStrafe = -0.95d;
    public static double wristLeft = 1d;

    // Arm
    public static double armStowed = 0.685;
    public static double armPickup = 0.06;
    public static double armScore = 0.45; // 0.6

    // Spec Claw
    public static double specClawClosed = 0.27;
    public static double specClawOpen = 0.7;

    // Extendo

    public static double extendoScore = 0.22d;
    public static double extendoOut = 0.33d;
    public static double extendoIn = 0.8d;

    // Sweeper
    public static double flipperOut = .7;
    public static double flipperIn = .98;
}
