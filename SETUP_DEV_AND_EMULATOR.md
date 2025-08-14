# Setup directions

## Kotlin and Gradle

### Use Sdkman

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk selfupdate force
sdk install kotlin
sdk install gradle
# Check
which kotlinc
kotlinc -version
```

We should be able to compile the app:

```bash
cd ${HOME}/Projects/Quantfidential/guitar-bass-practice-app/android-kotlin
./gradlew app:assembleDebug --continue
```

## Android Studio & Android SDK

### Windows

Esiest way around emulator and nested emulator issues is install same version of the Sdk in Wsl and windows 11.

### WSL

### Windows


### WSL

```bash
cd /usr/local/
sudo tar -xvf /mnt/c/Users/stuar/Downloads/android-studio-2025.1.2.11-linux.tar.gz
# Check
./android-studio/jbr/bin/java -version
java -version
ls -l android-studio/bin/studio.sh
echo "alias android-studio=/usr/local/android-studio/bin/studio.sh" >> ${HOME}/.bash_aliases
```

Run Android studio to install Sdk

```bash
android-studio &
# Check
ls ${HOME}$/Android/Sdk/
[[ -d "${HOME}/Android/Sdk" ]] && export ANDROID_HOME="${HOME}/Android/Sdk"
[[ -d "${HOME}/Android/Sdk/platform-tools" && ":$PATH:" != *":${HOME}/Android/Sdk/platform-tools:"* ]] && export PATH=${HOME}/Android/Sdk/platform-tools:$PATH
```

Add our user to the `kvm` group and restart the bash shell:

```bash
sudo gpasswd -a $USER kvm
```

https://gist.github.com/bergmannjg/461958db03c6ae41a66d264ae6504ade#connect-to-android-hardware-device-from-windows

We should be able to startup an emulator and run our app

```powershell
adb kill-server
adb -a nodaemon server start
adb start-server
* daemon not running; starting now at tcp:5037
* daemon started successfully
PS C:\Users\stuar> adb devices
List of devices attached
emulator-5554   offline
```

```bash
cd ${HOME}/Projects/Quantfidential/guitar-bass-practice-app/android-kotlin

$ANDROID_HOME/emulator/emulator -list-avds
$ANDROID_HOME/emulator/emulator -avd Medium_Phone_API_36.0 &
adb devices
./run-app.sh
```

Run on our phone:

1. In Powershell on Windows, run `adb tcpip 5555`.
1. In WSL2 terminal, run `adb devices`. You will not see any device yet, but this will start up adb.
1. Get the IP address of your phone. You can do this from Settings ->  About phone -> Status -> IP address. It will probably be something like 192.168.0.10n.
1. In WSL2 terminal, run `adb connect PHONE_ID:5555`.
1. You will be prompted to confirm the connection on your phone. Do that.
1. adb will probably fail because of the connection confirmation. Run adb kill-server in the WSL2 terminal and then run `adb connect PHONE_ID:5555`.
1. If Android Studio was open in WSL2, close it and then open it again.
```

## Windows

Install both windows 


## Logging

 1. Android Studio Logcat (Recommended)

  Most Common Method:
  1. Open Android Studio
  2. Run your app in the emulator
  3. Open the Logcat window:
    - Go to View → Tool Windows → Logcat
    - Or click the Logcat tab at the bottom of Android Studio
    - Or use shortcut: Alt+6 (Windows/Linux) or Cmd+6 (Mac)

  Filter your logs:
  # In the Logcat filter box, use:
  package:com.quantfidential.guitarbasspractice

  # Or filter by your custom tag:
  tag:GuitarBassPractice

  # Or filter by log level:
  level:ERROR

  2. ADB Command Line

  Terminal/Command Prompt:
  # View all logs
  adb logcat

  # Filter by your app package
  adb logcat | grep "com.quantfidential.guitarbasspractice"

  # Filter by your custom tag (from the Logger utility we created)
  adb logcat | grep "GuitarBassPractice"

  # Show only errors and warnings
  adb logcat *:E *:W

  # Clear logs first, then monitor
  adb logcat -c && adb logcat

  # Save logs to file
  adb logcat > app_logs.txt

  3. Using Our Custom Logger

  Since we implemented the Logger utility, you can easily add logging throughout your app:

  class MainActivity {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)

          // Log app startup
          logd("App started successfully")

          try {
              // Your code here
          } catch (e: Exception) {
              // This will appear in Logcat with your tag
              loge("App crashed in onCreate", e)
          }
      }
  }

  4. Crash-Specific Debugging

  For crashes when clicking create/AI buttons:
  // Add this to MainViewModel to see exact crash details
  fun handlePlaybackEvent(event: ExerciseEngineEvent) {
      logEntry("handlePlaybackEvent", mapOf("event" to event::class.simpleName))

      try {
          val currentState = _playbackState.value
          val newState = exerciseEngine.handleEvent(event, currentState)
          _playbackState.value = newState

          if (event is ExerciseEngineEvent.Play && _uiState.value.currentExercise != null) {
              logd("Starting exercise execution")
              viewModelScope.launch {
                  try {
                      exerciseEngine.executeExercise(
                          exercise = _uiState.value.currentExercise!!,
                          initialState = newState
                      ).collect { state ->
                          _playbackState.value = state
                      }
                  } catch (e: Exception) {
                      loge("Exercise execution failed", e)
                      _playbackState.value = newState.copy(isPlaying = false)
                      _uiState.value = _uiState.value.copy(
                          errorMessage = "Playback error: ${e.message}"
                      )
                  }
              }
          }
      } catch (e: Exception) {
          loge("Critical error in handlePlaybackEvent", e)
          throw e
      }
  }

  5. Logcat Filters in Android Studio

  Create custom filters:
  1. In Logcat, click the filter dropdown
  2. Click "Edit Filter Configuration"
  3. Create a new filter:
    - Name: "Guitar App Errors"
    - Package Name: com.quantfidential.guitarbasspractice
    - Log Level: Error
    - Tag: GuitarBassPractice

  6. Real-Time Monitoring Script

  Create a script to monitor your app logs:

  #!/bin/bash
  # save as monitor_app.sh

  echo "Monitoring Guitar Bass Practice App..."
  echo "Press Ctrl+C to stop"

  adb logcat -c  # Clear existing logs
  adb logcat | grep -E "(GuitarBassPractice|com\.quantfidential\.guitarbasspractice|AndroidRuntime|FATAL)"

  7. Debugging Dependency Injection Issues

  Since we fixed DI issues, add this to see injection status:

  @HiltViewModel
  class MainViewModel @Inject constructor(
      private val exerciseRepository: ExerciseRepository,
      private val userProfileRepository: UserProfileRepository,
      private val exerciseEngine: ExerciseEngine,
      private val customizationEngine: ExerciseCustomizationEngine,
      private val aiComposerAgent: AIComposerAgent,
      private val networkConnectivity: NetworkConnectivity
  ) : ViewModel() {

      init {
          logd("MainViewModel dependencies injected successfully")
          logd("ExerciseRepository: ${exerciseRepository::class.simpleName}")
          logd("UserProfileRepository: ${userProfileRepository::class.simpleName}")
          // ... log other dependencies
      }
  }

  8. Quick Debug Commands

  # Check if your app is running
  adb shell ps | grep guitarbasspractice

  # Check app's current activity
  adb shell dumpsys activity activities | grep guitarbasspractice

  # Monitor system crashes
  adb logcat | grep "FATAL EXCEPTION"

  # Monitor ANRs (App Not Responding)
  adb logcat | grep "ANR in"

  🎯 Recommended Workflow

  1. Start with Logcat in Android Studio (easiest)
  2. Add logging to suspected crash points using our Logger utility
  3. Use ADB for continuous monitoring if Android Studio Logcat is slow
  4. Create custom filters for different types of issues

  The logs will show you exactly where the app crashes and why, making debugging much more efficient!