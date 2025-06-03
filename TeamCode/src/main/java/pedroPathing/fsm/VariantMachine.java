package pedroPathing.fsm;

import java.util.ArrayList;

public class VariantMachine {
    private ArrayList<VariantState> vmb;

    public VariantState defaultVariant;
    private Enum currentState;
    private VariantState currentVariant;

    public VariantMachine(ArrayList<VariantState> vmb) {
        this.vmb = vmb;
        for (VariantState state : vmb) {
            if (state.isDefault) {
                currentState = state.state;
                defaultVariant = state;
                currentVariant = state;
            }
        }
    }

    public void start() {
        if (currentVariant != null) {
            if (currentVariant.onEnter != null) {
                currentVariant.onEnter.execute();
            }
            if (currentVariant.loop != null) {
                currentVariant.loop.execute();
            }
        }
    }

    public void update() {
        for (VariantState state : vmb) {
            if (state.condition != null && state.condition.execute()) {
                if (state.conditionType == VariantState.ConditionTypes.SWITCH) {
                    if (currentState == state.state) {
                        if (currentVariant.onExit != null) currentVariant.onExit.execute();
                        currentState = defaultVariant.state;
                        currentVariant = defaultVariant;
                        if (currentVariant.onEnter != null) currentVariant.onEnter.execute();
                    } else {
                        if (currentVariant.onExit != null) currentVariant.onExit.execute();
                        currentState = state.state;
                        currentVariant = state;
                        if (currentVariant.onEnter != null) currentVariant.onEnter.execute();
                    }
                } else {

                }
            }
        }
    }

}
