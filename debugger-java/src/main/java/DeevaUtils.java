package deeva;

import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;

import java.util.List;
import java.util.Map;

public class DeevaUtils {
    private DeevaUtils() {

    }

    public static Map<String, Connector.Argument> generateConnectorArguments(LaunchingConnector
                                                                                connector,
                                                       DeevaConfig config) {
        Map<String, Connector.Argument> arguments = connector.defaultArguments();

        /* Set the options argument, where -classpath and -ea is passed in */
        StringBuilder optionsSB = new StringBuilder();
        Connector.Argument optionArg = arguments.get("options");

        if (optionArg == null) {
            throw new Error("Bad launching connector");
        }

        if (config.isAssertionsEnabled()) {
            optionsSB.append("-ea ");
        }

        String classPaths = config.getClassPaths();
        if (classPaths != null) {
            optionsSB.append("-cp ").append(classPaths).append(" ");
        }

        optionArg.setValue(optionsSB.toString());

        Connector.Argument mainArg = arguments.get("main");
        if (mainArg == null) {
            throw new Error("Bad launching connector"); // TODO: Fix error, make more uniform.
        }

        String programArgsString = stringListJoin(config.getInitialArgs(), " ");
        String mainString = config.getMainClass() + " " + programArgsString;
        mainArg.setValue(mainString);
        //StringUtils.join(null, null);
        return arguments;
    }

    /**
     * Joins
     *
     * @param list list of strings to join together.
     * @param delimiter string to insert between each string in list.
     * @return a string that is all the strings in list delimited by delimiter.
     */
    public static String stringListJoin(List<String> list, String delimiter) {
        if (list == null) {
            return null;
        }

        Object[] strList = list.toArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strList.length; i++) {
            String elem = (String)strList[i];
            if (elem == null || elem.equals("")) {
                continue;
            }

            sb.append(elem);

            /* Don't append the delimiter if we're on the last item */
            if (i != strList.length - 1) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }
}
