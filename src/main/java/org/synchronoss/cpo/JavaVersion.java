/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.synchronoss.cpo;

/**
 * This class is used to do conditional compilation. Conditional compilation is
 * needed to support the new jdbc methods that are in JDK1.6 and JDK1.5.
 *
 * I added the other java versions here so that this class can be re-used.
 *
 * This works because when java sees a final boolean that is false it knows that
 * this code will never be executed, and will remove it.
 * 
 * @author dberry
 */
public class JavaVersion {
  // version strings that we will look for
  public static final String Java3 = "1.3";
  public static final String Java4 = "1.4";
  public static final String Java5 = "1.5";
  public static final String Java6 = "1.6";

  // The actual version of java that is interpreting this class
  public static final String version = System.getProperty("java.version")==null?"UNDEFINED":System.getProperty("java.version");

  // The final booleans that java will use to optimize out the code if the
  // boolean is false.
  // If a newer version is true, then the prior versions should be true as well
  public static final boolean isJava6 = version.startsWith(Java6);
  public static final boolean isJava5 = isJava6 || version.startsWith(Java5);
  public static final boolean isJava4 = isJava5 || version.startsWith(Java4);
  public static final boolean isJava3 = isJava4 || version.startsWith(Java3);
}
