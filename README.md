# intersection
Illegal intersection types for Java.

## How?
```java
I3<Class1, Interface1, Interface2> intersection = new I3<>(object);
intersection.get().class1Method();
intersection.get().interface1Method();
intersection.get().interface2Method();
```

## Why?
Made for usage with [Mixin](https://github.com/SpongePowered/Mixin).
Mixin made it possible to inject arbitrary interfaces to classes at runtime.
To access the injected interface methods, you would need to cast it.

Without intersection:
```java
TargetClass target = /* ... */;
InjectedInterface injected = (InjectedInterface) target;
target.original();   // method from original class
injected.injected(); // method from injected interface
```

With intersection:
```java
I2<TargetClass, InjectedInterface> intersection = new I2<>(target);
intersection.get().original();
intersection.get().injected();
```

Worth it? Probably not. With Java 10+ you can (ab)use type inference to cast object to multiple type.
```java
var intersection = (TargetClass & InjectedInterface) target;
intersection.original();
intersection.injected();
```
