package pedroPathing.fsm;

public class VariantState {
    public enum ConditionTypes {
        SWITCH,
        TRIGGER
    }
    public Enum state = null;
    public boolean isDefault = false;
    public Condition condition;
    public ConditionTypes conditionType;
    public Execute onEnter;
    public Execute loop;
    public Execute onExit;
    public TimedExecute afterTime;

}