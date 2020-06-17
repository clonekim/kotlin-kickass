# Kotlin

## Hello World

```kt
fun main(args: Array<String>) { 
      println("Hello, World!") 
 }
```

컴파일

```
kotlinc HelloWorld.kt -include-runtime -d HelloWorld.jar
```

실행
```
java -jar HelloWorld.jar
```

## 자료형

1. [Basic Types](https://kotlinlang.org/docs/reference/basic-types.html)  
2. [Control Flow](https://kotlinlang.org/docs/reference/control-flow.html)
3. [Returns and Jumps](https://kotlinlang.org/docs/reference/returns.html)  

```kt
 val c = if (조건) {
   a
 } else {
   b
 }
```
```kt
val c = if(조건) a else b
```
```kt
when (x) {
    1 -> print("x == 1")
    2 -> print("x == 2")
    3, 4 -> print("x > 2")
    else -> { // Note the block
        print("...")
    }
}
```
4. Null Safe

```kt
 val a: String  //a에 null를 대입 할 수 없다
 val a: String? //a에 null를 대입 할 수 있다.
 
 a.length //만약 null이라면 
 a?.length // a가 null 이면 null을 반환
 a!!.length //!!를 사용하면 NullPointException를 발생시켜라는 의미
```

5. No Checked Exception

코틀린으로 작성하면 checked exception을 발생 시키는 부분을 알수없다.

```java
public static void main(String[] args) {
    try {
      new Loader().load(); // IOException를 던지다고 치자
    } catch (IOException e) {
      // do nothing
    }
  }
}
```

```kt
class Loader {
  fun load() = Files.readAllLines(Paths.get("file.txt")) // IOException를 던짐
}
```
