# Sault.
File download library.

### Features
- multi task at same time.
- broken-point download.
- auto retry.
- large file support.
- multi thread support.

## Installation

Include the library in your `build.gradle`

```groovy
dependencies{
    compile 'com.bestxty.lib:sault:1.0.0'
}
```

or in your `pom.xml` if you are using Maven

```xml
<dependency>
  <groupId>com.bestxty.lib</groupId>
  <artifactId>sault</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

## Usage

- Include the following code in you `Application#OnCreate()`

```java
Sault sault = new Sault.Builder(this)
                .saveDir()//file save dir.
                .client()//okhttp client.
                .loggingEnabled(true)//show debug log.
                .autoAdjustThreadEnabled(true)//auto adjust thread count.
                .multiThreadEnabled(true)//enable or disable multi thread.
                .breakPointEnabled(true)// enable or disable break-point support.
                .downloader(null)//file downloader.
                .executor(null)//thread executor.
                .build();
```

- use sault anywhere in you app
```java
Object tag = sault.load("url")
            .tag("tag")
            .listener(new Callback() {
                @Override
                public void onEvent(Object tag, int event) {}
                @Override
                public void onProgress(Object tag, long totalSize, long finishedSize) {}
                @Override
                public void onComplete(Object tag, String path) {}
                @Override
                public void onError(SaultException exception) {}
            })
            .priority(Priority.HIGH)
            .multiThreadEnabled(true)
            .breakPointEnabled(true)
            .go();
```
- event:
 1. Callback#EVENT_START;task start.
 2. Callback#EVENT_PAUSE;task pause.
 3. Callback#EVENT_RESUME;task restart.
 4. Callback#EVENT_CANCEL;task cancel.
 5. Callback#EVENT_COMPLETE;task complete.


- priority:
 1. Priority.HIGH
 2. Priority.LOW
 3. Priority.NORMAL

- pause task:
```java
sault.pause(tag)
```

- resume task:
```java
sault.resume(tag)
```

- cancel task:
```java
sault.cancel(tag)
```

- close sault:
```java
sault.shutdown()
```

See more at the [sample](https://github.com/swjjxyxty/Sault/tree/master/app)

## Libraries and tools used in the project

* [OkHttp3](https://github.com/square/okhttp)
The OkHttp3 package provides APIs to support network access to your apps.

## License

    Copyright 2017 xty

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
