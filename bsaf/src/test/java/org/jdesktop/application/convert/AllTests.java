package org.jdesktop.application.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
           TestConverterRegistry.class,
           TestStringToMnemonicTextValue.class

   })
public class AllTests
{

}