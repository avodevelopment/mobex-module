# Mobex Module

This module is to request native functions on android

## Getting started

```shell
$ npm install mobex-module --save
```

## Mostly automatic installation

```shell
$ react-native link mobex-module
```

## Add these permissions in AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.ACTION_MANAGE_OVERLAY_PERMISSION" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

## Manual installation

### Android

- Open up android/app/src/main/java/[...]/MainActivity.java

- Add import uk.co.avodev.mobex.module.MobexPackage; to the imports at the top of the file

- Append the following lines to android/settings.gradle:

```groovy
include ':mobex-module'
project(':mobex-module').projectDir = new File(rootProject.projectDir, '../node_modules/mobex-module/android')
```

- Insert the following lines inside the dependencies block in android/app/build.gradle:


```groovy
compile project(':mobex-module')
```

## Usage

### Request Overlay Permission

The requestOverlayPermission function navigates to the permission settings on Xiaomi devices to request overlay permission.

```js
import MobexModule from "mobex-module";

MobexModule.requestOverlayPermission();
```

### Check Overlay Permission

The isRequestOverlayPermissionGranted function checks if the overlay permission is granted and prompts the user to grant it if necessary.

```js
import MobexModule from "mobex-module";

if (Platform.OS === "android") {
  MobexModule.isRequestOverlayPermissionGranted((status) => {
    if (status) {
      Alert.alert(
        "Permissions",
        "Overlay Permission",
        [
          {
            text: "Cancel",
            onPress: () => console.log("Cancel Pressed"),
            style: "cancel",
          },
          {
            text: "OK",
            onPress: () => MobexModule.requestOverlayPermission(),
          },
        ],
        { cancelable: false }
      );
    }
  });
}
```

### Check Screen Lock State

The isLocked function returns whether the screen is locked or unlocked.

```js
import MobexModule from "mobex-module";

const checkLock = async () => {
  const isLocked = await MobexModule.isLocked();
  console.log(isLocked);
  if (isLocked) {
    showLog("🔐 Screen Locked");
  } else {
    showLog("🔓 Screen Unlocked");
  }
};

checkLock();
```

### Kill App

The killApp function terminates the current app (process)

```js
import MobexModule from "mobex-module";

MobexModule.killApp();
```

## License

This project is licensed under the MIT License.
