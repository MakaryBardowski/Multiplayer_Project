package dirty.itemSystem;

public class ItemTypeDefinitions {
    public static final String DEFAULT_COLOR_CODE_PREFIX = "\\#FFFFFF#";
    public static final String ARTIFACT_COLOR_CODE_PREFIX = "\\#8b008b#";

    public static String getAsArtifact(String artifact){
        return ARTIFACT_COLOR_CODE_PREFIX+artifact+DEFAULT_COLOR_CODE_PREFIX;
    }
}


