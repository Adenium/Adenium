package io.adenium.utils;

<<<<<<< HEAD:src/main/java/io/adenium/utils/Assertions.java
import io.adenium.exceptions.WolkenException;
=======
import io.adenium.exceptions.AdeniumException;
>>>>>>> 0.01a:src/main/java/org/wolkenproject/utils/Assertions.java

public class Assertions {
    public static void assertTrue(boolean a, String message) throws AdeniumException {
        if (a) {
            return;
        }

        throw new AdeniumException(message);
    }

    public static void assertEquals(int a, int b, String message) throws AdeniumException {
        if (a == b) {
            return;
        }

        throw new AdeniumException(message);
    }

    public static void assertGreaterThan(int a, int b, String message) throws AdeniumException {
        if (a > b) {
            return;
        }

        throw new AdeniumException(message);
    }

    public static void assertGreaterThanOrEquals(int a, int b, String message) throws AdeniumException {
        if (a >= b) {
            return;
        }

        throw new AdeniumException(message);
    }

    public static void assertLessThan(int a, int b, String message) throws AdeniumException {
        if (a < b) {
            return;
        }

        throw new AdeniumException(message);
    }

    public static void assertLessThanOrEquals(int a, int b, String message) throws AdeniumException {
        if (a <= b) {
            return;
        }

        throw new AdeniumException(message);
    }
}
