## Scala back-end

in SBT:

run-main org.vertx.java.platform.impl.cli.Starter run scala:com.articlio.deployer

Or:

set mainClass in Revolver.reStart := Some("org.vertx.java.platform.impl.cli.Starter")
~re-start run scala:com.articlio.deployer
