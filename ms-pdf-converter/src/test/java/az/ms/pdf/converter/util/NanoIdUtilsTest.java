package az.ms.pdf.converter.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Stream;

@DisplayName("NanoIdUtils Tests")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NanoIdUtilsTest {
    public static Stream<Arguments> invalidArguments() {
        var random = new SecureRandom();
        return Stream.of(
                Arguments.arguments(Named.of("Random is NULL", null), null, 0),
                Arguments.arguments(Named.of("Alphabet is NULL", random), null, 0),
                Arguments.arguments(Named.of("Alphabet is EMPTY", random), new char[]{}, 0),
                Arguments.arguments(Named.of("Alphabet is OVERSIZE", random), "a".repeat(256).toCharArray(), 0),
                Arguments.arguments(Named.of("Size is ZERO", random), new char[]{'a'}, 0)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidArguments")
    void randomNanoId_IllegalArgumentException(Random random, char[] alphabet, int size) {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                NanoIdUtils.randomNanoId(random, alphabet, size));
    }
}