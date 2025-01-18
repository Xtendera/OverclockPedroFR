package pedroPathing.constants;

public class MConstants {
    // Slider
    public static double spoolCircumference = 150;
    public static double slidesGearRatio = 15.2;//18.9; //27.2;
    public static int ticksPerRev = 28;
    public static double ticksPerOutputRev = ticksPerRev*slidesGearRatio;
    public static double ticksPerMM = ticksPerOutputRev/spoolCircumference;

    public static double highChamberLoad = 529*ticksPerMM;
    public static double highChamberScore = 429*ticksPerMM;

    // Wrist
    public static double wristUp = 0.5;
}
