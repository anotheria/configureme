ConfigureMe
===========

## What is ConfigureMe
ConfigureMe is a configuration utility for really simple autoconfiguration of java objects. ConfigureMe focuses on different configuration of the same object in different Environments during staging (dev, test, prod). ConfigureMe configures POJOs based on annotations.
ConfigureMe is free and distributed under the MIT license.

## Features
* Automatic type safe configuration via annotations
* Support for cascading environments
* Automatic re-configuration of the configurables upon configuration (file) change
* One on one relation between a configuration and a configurable object
* Overview of all configuration options for a given configurable object at a glance
* Low load time, low memory footprint
* Support for system properties in config files.
* Includes for configuration files
* Handling of simple and complex properies (objects).

## Getting started
Take your first steps with [getting started](https://confluence.opensource.anotheria.net/display/CONFIGUREME/01+Getting+Started)

## How to get/use
Just add your maven/ivy/gradle dependency:
```
<dependency>
  <groupId>net.anotheria</groupId>
  <artifactId>configureme</artifactId>
  <version>2.3.4</version>
</dependency>
```
