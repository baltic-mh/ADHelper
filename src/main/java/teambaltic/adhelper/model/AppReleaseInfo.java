/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Thu May 19 19:03:54 CEST 2016 */
package teambaltic.adhelper.model;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class AppReleaseInfo {


   /** buildDate (set during build process to 1463677434014L). */
   private static Date buildDate = new Date(1463677434014L);

   /**
    * Get buildDate (set during build process to Thu May 19 19:03:54 CEST 2016).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** copyright (set during build process to "(C) 2016 TeamBaltic"). */
   private static String copyright = "(C) 2016 TeamBaltic";

   /**
    * Get copyright (set during build process to "(C) 2016 TeamBaltic").
    * @return String copyright
    */
   public static final String getCopyright() { return copyright; }


   /** project (set during build process to "KVK Arbeitsdienst-Helferlein"). */
   private static String project = "KVK Arbeitsdienst-Helferlein";

   /**
    * Get project (set during build process to "KVK Arbeitsdienst-Helferlein").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** organization (set during build process to "TeamBaltic"). */
   private static String organization = "TeamBaltic";

   /**
    * Get organization (set during build process to "TeamBaltic").
    * @return String organization
    */
   public static final String getOrganization() { return organization; }


   /** version (set during build process to "1.0.2.22"). */
   private static String version = "1.0.2.22";

   /**
    * Get version (set during build process to "1.0.2.22").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /** buildType (set during build process to "Release"). */
   private static String buildType = "Release";

   /**
    * Get buildType (set during build process to "Release").
    * @return String buildType
    */
   public static final String getBuildType() { return buildType; }


   /** buildTime (set during build process to "2016-05-19 19:03"). */
   private static String buildTime = "2016-05-19 19:03";

   /**
    * Get buildTime (set during build process to "2016-05-19 19:03").
    * @return String buildTime
    */
   public static final String getBuildTime() { return buildTime; }


   /** home (set during build process to "http://www.teambaltic.de"). */
   private static String home = "http://www.teambaltic.de";

   /**
    * Get home (set during build process to "http://www.teambaltic.de").
    * @return String home
    */
   public static final String getHome() { return home; }


   /** release (set during build process to "2016010516"). */
   private static String release = "2016010516";

   /**
    * Get release (set during build process to "2016010516").
    * @return String release
    */
   public static final String getRelease() { return release; }

}
