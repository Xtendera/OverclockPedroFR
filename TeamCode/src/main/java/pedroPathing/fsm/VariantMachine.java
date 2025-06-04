package pedroPathing.fsm;

import java.util.ArrayList;

public class VariantMachine {
    private ArrayList<VariantState> vmb;

    public VariantState defaultVariant;
    private Enum currentState;
    private VariantState currentVariant;

    public VariantMachine(ArrayList<VariantState> vmb) {
        this.vmb = vmb;
        boolean foundDefault = false;
        for (VariantState state : vmb) {
            if (state.isDefault) {
                currentState = state.state;
                defaultVariant = state;
                currentVariant = state;
                foundDefault = true;
                break;
            }
        }
        
        if (!foundDefault && !vmb.isEmpty()) {
            throw new IllegalStateException("No default state defined for VariantMachine");
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
        // BTW I wrote all this code, but I made GPT write the comments
        if (currentVariant != null) {
            // Check if current state is a TRIGGER and condition is no longer true
            if (currentVariant != defaultVariant && 
                currentVariant.conditionType == VariantState.ConditionTypes.TRIGGER && 
                currentVariant.condition != null && 
                !currentVariant.condition.execute()) {
                
                // Transition back to default
                if (currentVariant.onExit != null) currentVariant.onExit.execute();
                currentState = defaultVariant.state;
                currentVariant = defaultVariant;
                if (currentVariant.onEnter != null) currentVariant.onEnter.execute();
            }
        }
        
        // Now check all other states for transitions
        for (VariantState state : vmb) {
            // Skip the current state
            if (state.state == currentState) continue;
            
            // Check if this state's condition is true
            if (state.condition != null && state.condition.execute()) {
                // For SWITCH type, toggle between this state and default
                if (state.conditionType == VariantState.ConditionTypes.SWITCH) {
                    if (currentVariant.onExit != null) currentVariant.onExit.execute();
                    
                    if (currentState == state.state) {
                        // If we're already in this state, go back to default
                        currentState = defaultVariant.state;
                        currentVariant = defaultVariant;
                    } else {
                        // Otherwise, switch to this state
                        currentState = state.state;
                        currentVariant = state;
                    }
                    
                    if (currentVariant.onEnter != null) currentVariant.onEnter.execute();
                    break; // Only handle one transition per update
                } 
                // For TRIGGER type, just switch to this state
                else {
                    if (currentVariant.onExit != null) currentVariant.onExit.execute();
                    currentState = state.state;
                    currentVariant = state;
                    if (currentVariant.onEnter != null) currentVariant.onEnter.execute();
                    break; // Only handle one transition per update
                }
            }
        }
        
        // Execute the current state's loop action
        if (currentVariant != null && currentVariant.loop != null) {
            currentVariant.loop.execute();
        }
    }
}
