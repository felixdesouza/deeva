package deeva;

import java.util.LinkedList;
import java.util.List;

public class DeevaStateBuilder {

    private DeevaState.State state = DeevaState.State.NO_INFERIOR;
    private int lineNumber = -1;
    private List<StackFrameMeta> stacks = new LinkedList<StackFrameMeta>();
    private String currentClass = "";
    private List<String> arguments = new LinkedList<String>();
    private boolean enableAssertions = false;

    public DeevaState build() {
        return new DeevaState(state, lineNumber, stacks, currentClass,
                              arguments, enableAssertions);
    }

    public DeevaState.State getState() {
        return state;
    }

    public void setState(DeevaState.State state) {
        this.state = state;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int line_number) {
        this.lineNumber = line_number;
    }

    public List<StackFrameMeta> getStacks() {
        return stacks;
    }

    public void setStacks(List<StackFrameMeta> stacks) {
        this.stacks = stacks;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String current_class) {
        this.currentClass = current_class;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isEa() {
        return enableAssertions;
    }

    public void setEa(boolean ea) {
        this.enableAssertions = ea;
    }
}
