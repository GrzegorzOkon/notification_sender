package okon.ns;

public class Version {
    private static String name = "Notification Sender";
    private static int major = 1;
    private static int minor = 1;
    private static int realase = 2;
    private static String revision = "08 October 2021";
    private static String author = "Grzegorz Okon";
    private static String license = "GPLv2. This is free software.";

    public static String getName() {
        return name;
    }

    public static String getVersionInfo() {
        return name + " " + major + "." + minor + "." + realase + " (revision " + revision + ")";
    }

    public static void printVersionInfo() {
        StringBuilder result = new StringBuilder();
        result.append(name + " " + major + "." + minor + "." + realase);
        result.append("\n");
        result.append("Revision " + revision);
        result.append("\n");
        result.append("\n");
        result.append("Copyright (C) 2021 " + author);
        result.append("\n");
        result.append("License " + license);
        System.out.println(result.toString());
    }
}
