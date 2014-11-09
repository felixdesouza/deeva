package deeva.io;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;

public interface MethodEntryEventHandler {
    public void handleMethodEntryEvent(ThreadReference thread, ObjectReference objRef);
}
