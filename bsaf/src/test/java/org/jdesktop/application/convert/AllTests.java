package org.jdesktop.application.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URL;
import java.net.URLDecoder;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
   {
           TestStringToBoolean.class,
           TestStringToBoolean.TestDataValues.class,
           TestStringToCharacter.class,
           TestStringToColor.class,
           TestStringToColor.TestStrings.class,
           TestStringToDimension.class,
           TestStringToEmptyBorder.class,
           TestStringToFont.class,
           TestStringToImage.class,
           TestStringToImage.TestStringToBufferedImage.class,
           TestStringToImage.TestStringToIcon.class,
           TestStringToInsets.class,
           TestStringToKeyStroke.class,
           TestStringToKeyStroke.TestStrings.class,
           TestStringToNumber.class,
           TestStringToNumber.TestStringToByte.class,
           TestStringToNumber.TestStringToDouble.class,
           TestStringToNumber.TestStringToFloat.class,
           TestStringToNumber.TestStringToInteger.class,
           TestStringToNumber.TestStringToLong.class,
           TestStringToNumber.TestStringToShort.class,
           TestStringToPoint.class,
           TestStringToPoint2D_Double.class,
           TestStringToPoint2D_Float.class,
           TestStringToRect2D_Double.class,
           TestStringToRect2D_Float.class,
           TestStringToRectangle.class,
           TestStringToURI.class,
           TestStringToURL.class,
           TestConverterRegistry.class

   })
public class AllTests
{

}