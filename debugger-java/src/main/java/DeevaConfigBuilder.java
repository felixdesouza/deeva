package deeva;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DeevaConfigBuilder {
    private String programArgs = "";
    private boolean assertionsEnabled = false;
    private String classPaths = ".";
    private String[] fixedExcludes = {"java.*", "javax.*", "sun.*", "com.sun.*"};
    private List<String> excludes = new LinkedList<String>();
    private String sourcePaths = "";
    private String mainClass = null; // no default exists
    private List<String> initialArgs = new LinkedList<String>();

    public DeevaConfig build() throws DeevaException {
        if (mainClass == null) {
            throw new DeevaException("Main Class not specified");
        }

        List<String> excludesList = Arrays.asList(fixedExcludes);
        excludesList.addAll(excludes);

        return new DeevaConfig(
                programArgs, assertionsEnabled, classPaths,
                excludesList, sourcePaths, mainClass,
                initialArgs
        );
    }

    public void setProgramArgs(String programArgs) {
        this.programArgs = programArgs;
    }

    public void setAssertionsEnabled(boolean assertionsEnabled) {
        this.assertionsEnabled = assertionsEnabled;
    }

    public void setClassPaths(String classPaths) {
        this.classPaths = classPaths;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setSourcePaths(String sourcePaths) {
        this.sourcePaths = sourcePaths;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setInitialArgs(List<String> initialArgs) {
        this.initialArgs = initialArgs;
    }
}
