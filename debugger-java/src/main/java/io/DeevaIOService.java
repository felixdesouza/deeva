package deeva.io;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DeevaIOService implements MethodEntryEventHandler {

    private StreamRedirectThread outThread;
    private StreamRedirectThread errThread;
    private StdInRedirectThread inThread;
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    private boolean setupComplete = false;

    private Map<DeevaIOListener, BlockingQueue<String>> outputListeners
            = new HashMap<DeevaIOListener, BlockingQueue<String>>();
    private Map<DeevaIOListener, BlockingQueue<String>> errorListeners
            = new HashMap<DeevaIOListener, BlockingQueue<String>>();

    private void handleStandardInMethodEvent(ThreadReference thread, ObjectReference objRef) {

    }

    @Override
    public void handleMethodEntryEvent(ThreadReference thread, ObjectReference objRef) {
        // TODO: Any further error checking

        handleStandardInMethodEvent(thread, objRef);
    }

    public void pushToStandardIn(String s) {

    }

    public void setupIO(InputStream inputStream, InputStream errorStream,
                        OutputStream outputStream) {
        setupComplete = true;
    }

    /* Returns a blocking queue which needs to be pulled from,
    for order to be preserved */

    /**
     * Registers a listener that is listening for associated VMs stdout.
     *
     * @param listener stdout listener to be registered.
     * @return a BlockingQueue, which needs to be pulled from on the receiving end.
     */
    public BlockingQueue<String> registerOutputListener(DeevaIOListener listener) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        outputListeners.put(listener, queue);
        return queue;
    }

    /**
     * Registers a listener that is listening for associated VMs stderr.
     * @param listener stderr listener to be registered.
     * @return a BlockingQueue, which needs to be pulled from on the receiving end.
     */
    public BlockingQueue<String> registerErrorListener(DeevaIOListener listener) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        errorListeners.put(listener, queue);
        return queue;
    }

    /**
     * Unregisters a stdout listener
     * @param listener the listener to be unregistered.
     */
    public void unregisterOutputListener(DeevaIOListener listener) {
        BlockingQueue<String> queue = outputListeners.get(listener);
        outputListeners.remove(listener);
    }

    /**
     * Unregisters a stderr listener
     * @param listener the listener to be unregistered.
     */
    public void unregisterErrorListener(DeevaIOListener listener) {
        BlockingQueue<String> queue = errorListeners.get(listener);
        outputListeners.remove(listener);
    }
}
