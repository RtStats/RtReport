import sbt._
import Keys._
import play.Project._
import com.typesafe.config._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
    override def settings = super.settings ++ Seq(
      EclipseKeys.skipParents in ThisBuild := false)

    val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
    val appName = "vngup-rtreports"
    val appVersion = conf.getString("app.version")
    val appDepsCommon = appDependenciesBase ++ Seq(
        "org.springframework.security" % "spring-security-core" % "3.2.4.RELEASE",
        "org.springframework.security" % "spring-security-ldap" % "3.2.4.RELEASE"
    )
    val moduleCommon = play.Project(
        appName + "-common", appVersion, appDepsCommon, path = file("modules/common")
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repository
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
    
    val appDepsZcReport = appDependenciesBase
    val moduleZcReport = play.Project(
        appName + "-zcreport", appVersion, appDepsZcReport, path = file("modules/zcreport")
    ).dependsOn(
        moduleCommon
    ).aggregate(
        moduleCommon
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repository
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
    
    val appDepsPplogin = appDependenciesBase
    val modulePplogin = play.Project(
        appName + "-pplogin", appVersion, appDepsPplogin, path = file("modules/pplogin")
    ).dependsOn(
        moduleCommon
    ).aggregate(
        moduleCommon
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repository
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
    
    val appDepsPayCharging = appDependenciesBase
    val modulePayCharging = play.Project(
        appName + "-paycharging", appVersion, appDepsPplogin, path = file("modules/paycharging")
    ).dependsOn(
        moduleCommon
    ).aggregate(
        moduleCommon
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repository
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )

    val appDepsCp = appDependenciesBase
    val moduleCp = play.Project(
        appName + "-cp", appVersion, appDepsCp, path = file("modules/cp")
    ).dependsOn(
        moduleCommon
    ).aggregate(
        moduleCommon
    ).settings(
          // Disable generating scaladoc
          sources in doc in Compile := List(),

          // Custom Maven repository
          resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",

          // Force compilation in java 1.6
          javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )

    val main = play.Project(appName, appVersion, appDependenciesBase, path = file(".")).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Custom Maven repository
        resolvers += "Sonatype OSS repository" at "https://oss.sonatype.org/content/repositories/releases/",
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    ).dependsOn(
        moduleCommon, modulePplogin, modulePayCharging, moduleZcReport, moduleCp
    ).aggregate(
        moduleCommon, modulePplogin, modulePayCharging, moduleZcReport, moduleCp
    )
}
