package org.sirnple.gis.global.constant;

import java.util.Arrays;

/**
 * Created by: sirnple
 * Created in: 2020-06-07
 * Description:
 */
public enum Dir {
    PORE_PRESSURE("pore_pressure"),
    FLOW_RATE("flow_rate"),
    SEABED_SLIDING("seabed_sliding"),
    WAVE("wave"),
    MISC("misc");

    public String getDirName() {
        return dirName;
    }

    private String dirName;

    Dir(String dirName) {
        this.dirName = dirName;
    }

    public static Dir parseDir(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Argument [dir] can't be null");
        }

        for (Dir value : Dir.values()) {
            if (dir.equalsIgnoreCase(value.toString())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Argument [dir] allowableValues: " + Arrays.toString(Dir.values()));
    }
}
