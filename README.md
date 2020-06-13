# Draft
Implementation of JSR-330(Dependency Injection standard for Java)

[![CircleCI](https://circleci.com/gh/nokok/Draft.svg?style=svg)](https://circleci.com/gh/nokok/Draft)
![GitHub](https://img.shields.io/github/license/nokok/Draft)
[![Maintainability](https://api.codeclimate.com/v1/badges/8ade7f5db96df3a44de5/maintainability)](https://codeclimate.com/github/nokok/Draft/maintainability)

## Build

```
git clone git@github.com:nokok/Draft.git
cd Draft
./gradlew build
```

## Features

- Compile-Time verification(Experimental)
- Only depends on `javax.inject`

## Getting Started

### 1. Configure Gradle/Maven

Gradle
```groovy
dependencies {
    implementation 'javax.inject:javax.inject:1'
    implementation 'net.nokok.draft:draft:$version'
    annotationProcessor ('net.nokok.draft:draft:$version')
}
```

Maven
```xml
<dependency>
    <groupId>javax.inject</groupId>
    <artifactId>javax.inject</artifactId>
    <version>1</version>
</dependency>
<dependency>
    <groupId>net.nokok.draft</groupId>
    <artifactId>draft</artifactId>
    <version>$version</version>
</dependency>
```

### 2. Configure Module

```java
// SampleModule.java
import net.nokok.draft.Module;

@Module
interface SampleModule {
    ServiceImpl bind(Service s);
}
```

### 3. Create injector

```java
// Main.java
import net.nokok.draft.Injector;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.fromModule(SampleModule.class);
        injector.getInstance(Service.class); // new ServiceImpl();
    }
}
```

# Documentation(WIP)

e.g.
```java
interface Service {...}
class ServiceImpl implements Service {...}

interface GenericService<T> {...}
class GenericServiceImpl<T> implements GenericService<T> {...}
```

Configure

```java
@Module
interface SampleModule {
    // bind Service to ServiceImpl
    ServiceImpl bindService(Service s);

    // bind GenericService<SomeClass> to GenericServiceImpl<SomeClass>
    GenericServiceImpl<SomeClass> bindGenericServiceSomeClass(GenericService<SomeClass> s);

    // bind instance
    @Named("ApplicationTitle")
    default String title() {
        return "App";
    }
}
```

## Override bindings

You can safely overwrite binding settings by using inheritance.

```java
@Module
interface Production {
    @Named("DatabaseUrl")
    default String databaseUrl() {
        return "jdbc:mysql://prod-db:3306/db";
    }
}

@Module
interface Local extends Production {
    @Override
    @Named("DatabaseUrl")
    default String databaseUrl() {
        return "jdbc:mysql://localhost:3306/db";
    }
}

// ...

Injector injector;
if(isProd){
    injector = Injector.fromModule(Production.class);
} else {
    injector = Injector.fromModule(Local.class);
}

/// ...

public class Repository {

    @Inject
    public Repository(@Named("DatabaseUrl") String databaseUrl) {
        // You can switch the databaseUrl dependening on the environment
        ...
    }
}

```

## Publish

```
./gradlew build signFiles publishToMavenLocal
```
