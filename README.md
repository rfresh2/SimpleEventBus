# SimpleEventBus

Minimal, High Performance Java EventBus

# Features

* Minimal API, no reflection or annotations processing
* Asynchronous and Synchronous Event Dispatching
* Event Priorities
* Cancellable Events

## Why?

I use GraalVM in projects like [ZenithProxy](https://github.com/rfresh2/ZenithProxy)

Using GraalVM with reflection is painful as each reflective call needs to be registered at build time.

# Usage

## Add Dependency

### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.rfresh2:SimpleEventBus:1.1'
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url> 
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.rfresh2</groupId>
        <artifactId>SimpleEventBus</artifactId>
        <version>1.1</version>
    </dependency>
</dependencies>
```

## EventBus API

See [SimpleEventBus.java](https://github.com/rfresh2/SimpleEventBus/blob/mainline/src/main/java/com/github/rfresh2/SimpleEventBus.java)

Example usage: [SimpleEventBusTest.java](https://github.com/rfresh2/SimpleEventBus/blob/mainline/src/test/java/com/github/rfresh2/SimpleEventBusTest.java)

# Benchmarks

VS [LambdaEvents](https://github.com/lenni0451/LambdaEvents)

```
EventsBenchmark.callASM                avgt    4  418591.445 ± 182333.340  ns/op
EventsBenchmark.callLambdaMetaFactory  avgt    4  426080.459 ±  11000.081  ns/op
EventsBenchmark.callMethodHandles      avgt    4  758218.763 ±  57466.156  ns/op
EventsBenchmark.callReflection         avgt    4  563770.273 ±  33414.060  ns/op
EventsBenchmark.callSimpleEventBus     avgt    4  371913.931 ±  20369.029  ns/op
```
