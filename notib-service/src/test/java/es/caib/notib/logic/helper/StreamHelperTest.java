package es.caib.notib.logic.helper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class StreamHelperTest {

    /**
     * StreamHelperTest class tests withCounter() method of the StreamHelper class.
     * It provides a mechanism to iterate over a stream with a counter.
     */

    // Test for the withCounter method with no initial count value
    @Test
    public void testWithCounter_noInitialValue() {
        
        List<String> data = Arrays.asList("Test1", "Test2", "Test3");
        AtomicInteger actualCount = new AtomicInteger(0);

        BiConsumer<Integer, String> biConsumer = (count, s) -> {
            assertEquals(actualCount.getAndIncrement(), count);
        };
        
        Consumer<String> testConsumer = StreamHelper.withCounter(biConsumer);
        data.forEach(testConsumer);
    }

    // Test for the withCounter method with an initial count value
    @Test
    public void testWithCounter_withInitialValue() {

        List<String> data = Arrays.asList("Test1", "Test2", "Test3");
        int start = 5;
        AtomicInteger actualCount = new AtomicInteger(start);

        BiConsumer<Integer, String> biConsumer = (count, s) -> {
            assertEquals(actualCount.getAndIncrement(), count);
        };

        Consumer<String> testConsumer = StreamHelper.withCounter(biConsumer, start);
        data.forEach(testConsumer);
    }

}