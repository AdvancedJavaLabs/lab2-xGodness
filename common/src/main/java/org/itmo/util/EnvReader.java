package org.itmo.util;

public class EnvReader {
    public static String readEnvVar(String varName) {
        String variable = System.getenv(varName);
        if (variable == null) {
            throw new RuntimeException(varName + " environment variable is missing");
        }
        return variable;
    }
}
