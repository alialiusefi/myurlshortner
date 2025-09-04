addSbtPlugin("ch.epfl.scala"                     % "sbt-bloop"                 % "1.4.8")
addSbtPlugin("ch.epfl.scala"                     % "sbt-scalafix"              % "0.14.3")
addSbtPlugin("com.eed3si9n"                      % "sbt-buildinfo"             % "0.10.0")
addSbtPlugin("com.github.cb372"                  % "sbt-explicit-dependencies" % "0.2.15")
addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings"          % "3.0.0")
addSbtPlugin("com.typesafe"                      % "sbt-mima-plugin"           % "0.9.0")
addSbtPlugin("de.heikoseeberger"                 % "sbt-header"                % "5.6.0")
addSbtPlugin("org.scalameta"                     % "sbt-mdoc"                  % "2.2.20")
addSbtPlugin("org.scalameta"                     % "sbt-scalafmt"              % "2.4.2")
addSbtPlugin("pl.project13.scala"                % "sbt-jcstress"              % "0.2.0")
addSbtPlugin("pl.project13.scala"                % "sbt-jmh"                   % "0.4.0")
addSbtPlugin("com.github.sbt"                    % "sbt-avro"                  % "3.5.0")

libraryDependencies += "org.snakeyaml"   % "snakeyaml-engine" % "2.3"
libraryDependencies += "org.apache.avro" % "avro"             % "1.12.0"
libraryDependencies += "org.apache.avro" % "avro-compiler"    % "1.12.0"
