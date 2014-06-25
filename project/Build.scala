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
    val appName         = "Truyen"
    val appVersion      = conf.getString("app.version")

    val appDependencies = Seq(
        "org.slf4j"             %  "log4j-over-slf4j"       % "1.7.7",
        "mysql"                 %  "mysql-connector-java"   % "5.1.29",
        "com.google.guava"      %  "guava"                  % "16.0.1",
        "org.apache.commons"    %  "commons-pool2"          % "2.2",
        "org.jodd"              %  "jodd-http"              % "3.5.2",
        "org.jodd"              %  "jodd-lagarto"           % "3.5.2",
        "com.ibm.icu"           %  "icu4j"                  % "53.1",
        "com.github.ddth"       %  "ddth-commons"           % "0.2.2.2",
        "com.github.ddth"       %% "play-module-plommon"    % "0.5.1.1",
        javaJdbc,
        cache,
        filters
    )
    
    var _javaVersion = "1.6"
    
    val moduleCommon = play.Project(
        appName + "-common", appVersion, appDependencies, path = file("modules/common")
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
      
    val moduleWorker = play.Project(
        appName + "-worker", appVersion, appDependencies, path = file("modules/worker")
    ).dependsOn(
        moduleCommon
    ).aggregate(
        moduleCommon
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )
    
    val moduleAdmin = play.Project(
        appName + "-admin", appVersion, appDependencies, path = file("modules/admin")
    ).dependsOn(
        moduleCommon, moduleWorker
    ).aggregate(
        moduleCommon, moduleWorker
    ).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    )

    val main = play.Project(appName, appVersion, appDependencies, path = file(".")).settings(
        // Disable generating scaladoc
        sources in doc in Compile := List(),
        
        // Force compilation in java 1.6
        javacOptions in Compile ++= Seq("-source", _javaVersion, "-target", _javaVersion)
    ).dependsOn(
        moduleCommon, moduleWorker, moduleAdmin
    ).aggregate(
        moduleCommon, moduleWorker, moduleAdmin
    )
}
