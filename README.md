AutoSignon Authentication Provider for Blackboard
================

This Authentication provider is meant to replace the old (Legacy Authentication) AutoSignon Building Block which was originally written
by Blackboard Global Services, and made available on Github.

This Auth Provider was contributed to the community by Swinburne University, and is now maintained by
[All the Ducks](https://www.alltheducks.com/) Pty Ltd.

Building the Autosignon Auth Provider B2
-------

1. Get the source code from github
2. Open a command prompt and change to the directory where you put the project source code
3. execute `./gradlew autosignon-b2:build` on linux/osx, or `gradlew.bat autosignon-b2:build` on windows.
4. You'll find the war file in `build/libs`

Downloading
-------
You can download the latest binary package of this Building Block from the [AutoSignon oscelot page](http://projects.oscelot.org/gf/project/autosignon/).