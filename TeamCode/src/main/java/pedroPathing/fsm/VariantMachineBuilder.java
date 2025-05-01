package pedroPathing.fsm;

import java.util.ArrayList;

public class VariantMachineBuilder {
    ArrayList<VariantState> states;
    public VariantMachineBuilder variant(Enum dEnum) {
        VariantState newState = new VariantState();
        newState.state = dEnum;
        states.add(newState);
        return this;
    }

    public VariantMachineBuilder setTrigger(Condition cond) {
        states.get(states.size() - 1).conditionType = VariantState.ConditionTypes.TRIGGER;
        states.get(states.size() - 1).condition = cond;
        return this;
    }

    public VariantMachineBuilder setSwitch(Condition cond) {
        states.get(states.size() - 1).conditionType = VariantState.ConditionTypes.SWITCH;
        states.get(states.size() - 1).condition = cond;
        return this;
    }

    public VariantMachineBuilder setDefault(boolean isDef) {
        states.get(states.size() - 1).isDefault = true;
        return this;
    }

    public VariantMachineBuilder onEnter(Execute callback) {
        states.get(states.size() - 1).onEnter = callback;
        return this;
    }

    public VariantMachineBuilder onExit(Execute callback) {
        states.get(states.size() - 1).onExit = callback;
        return this;
    }

    public VariantMachine build() {
        return new VariantMachine(states);
    }
}
