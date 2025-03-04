package moremekasuitmodules.common.config;

public class MoreModulesConfig {

    private static MoreModulesConfig LOCAL = new MoreModulesConfig();
    private static MoreModulesConfig SERVER = null;

    public static MoreModulesConfig current() {
        return SERVER != null ? SERVER : LOCAL;
    }

    public static MoreModulesConfig local() {
        return LOCAL;
    }

    public MekaSuitMoreModulesConfig config = new MekaSuitMoreModulesConfig();
}
