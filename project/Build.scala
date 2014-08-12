import sbt._
import Keys._
import play.Project._
import com.typesafe.config._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
    override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
    )

    val conf            = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
    val appName         = "rtreport"
    val appVersion      = conf.getString("app.version")
    
    val _javaVersion = "1.6"
    val _versionSpringFramework = "3.2.10.RELEASE"
    val _versionSpringSecurity = "3.2.4.RELEASE"
    val _versionTsc = "0.5.1"
      
    val appDependenciesBase = Seq(
        javaJdbc,
        cache,
        filters,
        "org.slf4j"             %  "log4j-over-slf4j"       % "1.7.7",
        "mysql"                 %  "mysql-connector-java"   % "5.1.29",
        "com.google.guava"      %  "guava"                  % "16.0.1",
        "org.apache.commons"    %  "commons-pool2"          % "2.2",
        "org.jodd"              %  "jodd-http"              % "3.5.2",
        "org.jodd"              %  "jodd-lagarto"           % "3.5.2",
        "com.ibm.icu"           %  "icu4j"                  % "53.1",
        "com.github.ddth"       %  "ddth-commons"           % "0.2.2.2",
        "com.github.ddth"       %  "spring-social-helper"   % "0.2.1",
        "com.github.ddth"       %  "ddth-tsc"               % _versionTsc,
        "com.github.ddth"       %  "ddth-tsc-cassandra"     % _versionTsc,
        "com.github.ddth"       %  "ddth-tsc-redis"         % _versionTsc,
        "com.github.ddth"       %% "play-module-plommon"    % "0.5.1.5"
    )
    
    val appDepsCommon = appDependenciesBase ++ Seq(
        "com.datastax.cassandra"       % "cassandra-driver-core" % "2.0.1",
        "org.springframework.security" % "spring-security-core"  % _versionSpringSecurity,
        "org.springframework.security" % "spring-security-ldap"  % _versionSpringSecurity,
        "org.springframework"          % "spring-core"           % _versionSpringFramework,
        "org.springframework"          % "spring-expression"     % _versionSpringFramework
        
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
    
    val appDepsReport = appDependenciesBase
    val moduleReport = play.Project(
        appName + "-report", appVersion, appDepsReport, path = file("modules/report")
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
        moduleCommon, moduleReport
    ).aggregate(
        moduleCommon, moduleReport
    )
}
