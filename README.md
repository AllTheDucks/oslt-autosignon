AutoSignon Authentication Provider for Blackboard
================

This Authentication provider is meant to replace the old (Legacy Authentication) AutoSignon Building Block which was originally written
by Blackboard Global Services, and made available on Github.

The original AutoSignOn Authentication Provider was contributed to the community by Swinburne University, and is now maintained by 
[All the Ducks](https://www.alltheducks.com/) Pty Ltd.

## Version Notes
### Version 3.0.0
This version of the Authentication Provider has been updated for use with Ultra Courses as delivered via Blackboard Learn SaaS. This version allows the AutoSignOn authentication provider to fully support both hosted and SaaS environments.

Specifically:
AutoSignOn has been updated for Ultra support. Ultra uses a different URL syntax than Classic so in addition to supporting the non-Ultra "/webapps/" URL scheme, if the system is running Ultra we need to use the Ultra URL scheme:

* Original Course (Ultra not enabled): "/webapps/blackboard/execute/modulepage/view?course\_id=_3_1"
* Ultra Course (Ultra enabled): "/ultra/courses/_4_1/outline"
* Original Course (Ultra enabled): "/ultra/courses/_3_1/cl/outline"

Additionally if the the course is not available or if the user does not have a course membership the user is sent to the courses page in Ultra.


Building the Autosignon Auth Provider B2
-------

1. Get the source code from github
2. Open a command prompt and change to the directory where you put the project source code
3. execute `./gradlew autosignon-b2:build` on linux/osx, or `gradlew.bat autosignon-b2:build` on windows.
4. You'll find the war file in `build/libs`

Downloading
-------
You can download the latest binary package of this Building Block from the [Github Releases page for this project](https://github.com/AllTheDucks/oslt-autosignon/releases).