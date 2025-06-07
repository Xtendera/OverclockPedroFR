package pedroPathing.fsm;

import java.util.ArrayList;

public class VariantMachineBuilder {
    ArrayList<VariantState> states = new ArrayList<>();
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

    public VariantMachineBuilder afterTime(long time, Execute callback) {
        states.get(states.size() - 1).afterTime = new TimedExecute(time, callback);
        return this;
    }

    public VariantMachine build() {
        int defaults = 0;
        for (VariantState state : states) {
            if (state.isDefault) defaults++;
        }
        if (defaults > 1) {
            throw new IllegalStateException("No more than one default state may be initialized!");
        } else if (defaults == 0) {
            throw new IllegalStateException("One default state is required!");
        }
        return new VariantMachine(states);
    }
}
