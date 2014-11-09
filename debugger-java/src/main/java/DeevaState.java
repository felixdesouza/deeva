package deeva;

import java.util.List;

public class DeevaState {
    public static enum State {
        NO_INFERIOR,
        STASIS,
        RUNNING,
        AWAITING_IO;
    }

    private final State state;
    private final int lineNumber;
    private final String currentClass;
    private final List<String> arguments; // separate message (init message)
    private final boolean enableAssertions; // separate message (init message)
    private final List<StackFrameMeta> stacks;

    public DeevaState(State state, int lineNumber,
                      List<StackFrameMeta> stacks, String currentClass,
                      List<String> arguments, boolean ea) {

        this.state = state;
        this.lineNumber = lineNumber;
        this.currentClass = currentClass;
        this.arguments = arguments;
        this.enableAssertions = ea;
        this.stacks = stacks;
    }

    public State getState() {
        return state;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public boolean assertionsEnabled() {
        return enableAssertions;
    }

    public List<StackFrameMeta> getStacks() {
        return stacks;
    }
}
