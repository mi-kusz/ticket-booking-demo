package util;

import org.example.util.Util;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest
{

    @Test
    public void testNoParameters()
    {
        int result = Util.countParameters();

        assertEquals(0, result);
    }

    @Test
    public void testOnlyNulls()
    {
        List<String> data = Collections.nCopies(100, null);

        int result = Util.countParameters(data.toArray(new String[0]));

        assertEquals(0, result);
    }

    @Test
    public void testCountSingleParameter()
    {
        int result = Util.countParameters("Test");

        assertEquals(1, result);
    }

    @Test
    public void testCountSingleParameterAmongNulls()
    {
        List<String> data = new ArrayList<>(Collections.nCopies(100, null));
        data.add(5, "Test");

        int result = Util.countParameters(data.toArray(new String[0]));

        assertEquals(1, result);
    }

    @Test
    public void testManyParameters()
    {
        List<String> data = Collections.nCopies(100, "Test");

        int result = Util.countParameters(data.toArray(new String[0]));

        assertEquals(100, result);
    }
}
