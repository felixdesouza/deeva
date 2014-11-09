package deeva;

import java.util.List;

public class DeevaConfig {
    private final String programArgs;
    private final boolean assertionsEnabled;
    private final String classPaths;
    private final List<String> excludes;
    private final String sourcePaths;
    private final String mainClass;
    private final List<String> initialArgs;

    public DeevaConfig(String programArgs, boolean assertionsEnabled, String classPaths,
                       List<String> excludes, String sourcePaths, String mainClass,
                       List<String> initialArgs) {
        this.programArgs = programArgs;
        this.assertionsEnabled = assertionsEnabled;
        this.classPaths = classPaths;
        this.excludes = excludes;
        this.sourcePaths = sourcePaths;
        this.mainClass = mainClass;
        this.initialArgs = initialArgs;
    }

    public String getProgramArgs() {
        return programArgs;
    }

    public boolean isAssertionsEnabled() {
        return assertionsEnabled;
    }

    public String getClassPaths() {
        return classPaths;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public String getSourcePaths() {
        return sourcePaths;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getInitialArgs() {
        return initialArgs;
    }
}
