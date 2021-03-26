addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")            // scalafmt plugin
addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.7")                // project stats plugin
addSbtPlugin("com.scalapenos" % "sbt-prompt" % "1.0.2")             // cool sbt prompt plugin
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.3.0")             // jacoco test plugin
addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.1") // dependency update check plugin

resolvers += ("Artima Maven Repository" at "http://repo.artima.com/releases")    // artima resolver for supersafe plugin
  .withAllowInsecureProtocol(true)
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.12")       // artima supersafe compiler plugin
