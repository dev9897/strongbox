package org.carlspring.strongbox.io;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import org.carlspring.commons.http.range.ByteRange;
import org.springframework.util.ReflectionUtils;

public class StreamUtils
{

    private StreamUtils()
    {

    }

    public static long getLength(ByteRangeInputStream bris)
        throws IOException
    {
        return bris.getLength();
    }

    public static void setCurrentByteRange(ByteRangeInputStream bris,
                                           ByteRange byteRange)
        throws IOException
    {
        bris.setCurrentByteRange(byteRange);
        bris.skip(byteRange.getOffset());
    }

    public static <T extends InputStream> T findSource(Class<T> sourceClass,
                                                       InputStream in)
    {
        if (sourceClass.isAssignableFrom(in.getClass()))
        {
            return (T) in;
        }
        
        Field inField = ReflectionUtils.findField(FilterInputStream.class, "in");
        inField.setAccessible(true);

        InputStream source = in;
        while (source instanceof FilterInputStream)
        {
            try
            {
                source = (InputStream) inField.get(source);
            }
            catch (Exception e)
            {
                return null;
            }
            if (sourceClass.isAssignableFrom(source.getClass()))
            {
                return sourceClass.cast(source);
            }
        }
        return null;
    }

    public static <T extends OutputStream> T findSource(Class<T> sourceClass,
                                                        OutputStream out)
    {
        if (sourceClass.isAssignableFrom(out.getClass()))
        {
            return (T) out;
        }
        
        Field inField = ReflectionUtils.findField(FilterOutputStream.class, "out");
        inField.setAccessible(true);

        OutputStream source = out;
        while (source instanceof FilterOutputStream)
        {
            try
            {
                source = (OutputStream) inField.get(source);
            }
            catch (Exception e)
            {
                return null;
            }
            if (sourceClass.isAssignableFrom(source.getClass()))
            {
                return sourceClass.cast(source);
            }
        }
        
        return null;
    }

}
