package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  7:53:27 PM
 */
abstract public class StringToNumber<S, D> extends ResourceConverter<S, D>
{
    protected StringToNumber(Class<S> sourceClass, Class<D> destClass)
    {
       super(sourceClass, destClass);
    }

    protected abstract D parseString(@NotNull String s, int radix) throws NumberFormatException;

    protected D parseString(@NotNull String s) throws StringConvertException
    {
        try
        {
            String[] nar = s.split("&"); // number ampersand radix
            int radix = (nar.length == 2) ? Integer.parseInt(nar[1]) : -1;
            return parseString(nar[0], radix);
        }
        catch (NumberFormatException e)
        {
            throw new StringConvertException("Invalid " + getDestClass().getSimpleName(), s, e);
        }
    }


    public static class StringToByte extends StringToNumber<String, Byte>
    {
        public StringToByte()
        {
            super(String.class, Byte.class);
        }

        @Override
        protected Byte parseString(String s, int radix) throws NumberFormatException
        {
            return (radix == -1) ? Byte.decode(s) : Byte.parseByte(s, radix);
        }

        public Byte convert(@NotNull String source, Object... args) throws StringConvertException
        {
           return  parseString(source);
        }
    }

    public static class StringToShort extends StringToNumber<String, Short>
    {
        public StringToShort()
        {
            super(String.class, Short.class);
        }

        @Override
        protected Short parseString(String s, int radix) throws NumberFormatException
        {
            return (radix == -1) ? Short.decode(s) : Short.parseShort(s, radix);
        }

        public Short convert(@NotNull String source, Object... args) throws StringConvertException
        {
            return parseString(source);
        }
    }

    public static class StringToInteger extends StringToNumber<String, Integer>
    {
        public StringToInteger()
        {
            super(String.class, Integer.class);
        }

        @Override
        protected Integer parseString(String s, int radix) throws NumberFormatException
        {
            return (radix == -1) ? Integer.decode(s) : Integer.parseInt(s, radix);
        }

        public Integer convert(@NotNull String source, Object... args) throws StringConvertException
        {
            return parseString(source);
        }
    }

    public static class StringToLong extends StringToNumber<String, Long>
    {
        public StringToLong()
        {
            super(String.class, Long.class);
        }

        @Override
        protected Long parseString(String s, int radix) throws NumberFormatException
        {
            return (radix == -1) ? Long.decode(s) : Long.parseLong(s, radix);
        }

        public Long convert(@NotNull String source, Object... args) throws StringConvertException
        {
            return parseString(source);
        }
    }

    public static class StringToFloat extends StringToNumber<String, Float>
    {
        public StringToFloat()
        {
            super(String.class, Float.class);
        }

        @Override
        protected Float parseString(String s) throws NumberFormatException
        {
            return Float.valueOf(s);
        }

        //Not Used
        protected Float parseString(String s, int radix) throws NumberFormatException
        {
            throw new UnsupportedOperationException("This method should not be called by  StringToFloat");
        }

        public Float convert(@NotNull String source, Object... args) throws StringConvertException
        {
            try
            {
                return parseString(source);
            }
            catch (NumberFormatException e)
            {
                throw new StringConvertException("invalid " + getDestClass().getSimpleName(), source, e);
            }
        }
    }

    public static class StringToDouble extends StringToNumber<String, Double>
    {
        public StringToDouble()
        {
            super(String.class, Double.class);
        }

        @Override
        protected Double parseString(String s) throws NumberFormatException
        {
            return Double.valueOf(s);
        }

        //Not Used
        protected Double parseString(String s, int radix) throws NumberFormatException
        {
            throw new UnsupportedOperationException("This method should not be called by the StringToDouble");
        }

        public Double convert(@NotNull String source, Object... args) throws StringConvertException
        {
            try
            {
                return parseString(source);
            }
            catch (NumberFormatException e)
            {
                throw new StringConvertException("invalid " + getDestClass().getSimpleName(), source, e);
            }
        }
    }


}
