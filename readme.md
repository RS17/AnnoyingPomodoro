Annoying Pomodoro

This is a Pomodoro timer, currently only for Android and CLI.  To run/work on: clone the repo and open it in Android Studio.  It should run out of the box.

This app is available on the [Android Play store](https://play.google.com/store/apps/details?id=com.github.rs17.annoyingpomodoro)

The desktop CLI version is a jar file located in this project in: /annoyingpomodoro_desktop/build/libs/.  To run it, move it or keep it in the same directory and run:
''''
java -jar ./AnnoyingPomodoro/annoyingpomodoro_desktop/build/libs/annoyingpomodoro_desktop-fat-1.0.jar
''''
Replace with the correct path to the jar as needed.

This remains a work in progress though I've not done much work on it for a while.  Still, it should work pretty well right now.

See designnotes.txt for more about the design.

To Build: 

- Open one of the projects in Idea or whatever
- Run "build" and "jar" in gradle on **lib** (do this anytime you need to refresh the lib code)
- Run build on project

See designnotes.txt if you plan on forking and/or contributing so you can understand the structure.