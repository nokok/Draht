# Draft
Implementation of JSR-330(Dependency Injection standard for Java)

## Build

```
git clone git@github.com:nokok/Draft.git
cd Draft
./gradlew build
```

## Example

```java
import javax.inject.Inject;
import net.nokok.inject.Injector;
import static net.nokok.inject.Injector.syntax.*;

interface A { }
class AImpl implements A {}

interface Base { }
class Impl implements Base {
    private final A a;
    
    @Inject
    Impl(A a) { this.a = a; }
    
    public A getA() { return a; }
}

class Main {
    public static void main(String[] args) {
        Injector injector = new Injector(
                bind(Base.class).to(Impl.class),
                bind(A.class).to(AImpl.class)
        );

        Base base = injector.getInstance(Base.class);

        System.out.println(base instanceof Impl); // true
        System.out.println(((Impl) base).getA() != null); // true
    }
}
```
