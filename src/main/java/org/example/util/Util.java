package org.example.util;

public class Util
{
    public static int countParameters(String...parameters)
    {
        int count = 0;

        for (String parameter : parameters)
        {
            if (parameter != null)
            {
                ++count;
            }
        }

        return count;
    }
}
