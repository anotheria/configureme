[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.anotheria/configureme/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.anotheria/configureme)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)


ConfigureMe
===========

![ConfigureMe](https://github.com/anotheria/configureme/assets/2864497/3e014e61-f1d7-41c7-9eb8-31bb26bce545)



## What is ConfigureMe
ConfigureMe is a configuration utility for really simple autoconfiguration of java objects. ConfigureMe focuses on different configuration of the same object in different Environments during staging (dev, test, prod). ConfigureMe configures POJOs based on annotations.
ConfigureMe is free and distributed under the [MIT license](http://www.opensource.org/licenses/mit-license.php).

## Features
* Automatic type safe configuration via annotations.
* Support for cascading environments.
* Automatic re-configuration of the configurables upon configuration (file) change.
* One on one relation between a configuration and a configurable object.
* Overview of all configuration options for a given configurable object at a glance.
* Low load time, low memory footprint.
* Support for system properties in config files.
* Includes for configuration files.
* Handling of simple and complex properies (objects).

## Getting started
Take your first steps with [getting started](https://github.com/anotheria/configureme/wiki#getting-started-with-configureme)

## How to get/use
Just add your maven/ivy/gradle dependency:
```
<dependency>
  <groupId>net.anotheria</groupId>
  <artifactId>configureme</artifactId>
  <version>4.0.0</version>
</dependency>
```
## Further documentation
* [Supported annotations](https://github.com/anotheria/configureme/wiki/Annotations)
* [Examples](https://github.com/anotheria/configureme/wiki/Examples)
* [What are environments](https://github.com/anotheria/configureme/wiki/Environments)


## Java Version
* 4.x versions require and are built for Java11 and above.
* 2.x versions require and are built for Java8. 
