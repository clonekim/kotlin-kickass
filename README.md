# Kotlin
 
  https://kotlinlang.org/
 
## Hello World

> main 함수
```kt
fun main(args: Array<String>) { 
    println("Hello, World!") 
}
 //또는
fun main() { 
    println("Hello, World!") 
}
```

> 컴파일

```
kotlinc HelloWorld.kt -include-runtime -d HelloWorld.jar
```

> 실행
```
java -jar HelloWorld.jar
```

## Guide

코틀린의 문법과 샘플은 [여기](https://kotlinlang.org/docs/reference/interfaces.html) 에서 얻을 수 있다.  
잘쓰거나 잊어버리기 쉬운것만 정리한다   

### [Basic Types](https://kotlinlang.org/docs/reference/basic-types.html)  
### [Control Flow](https://kotlinlang.org/docs/reference/control-flow.html)
### [Returns and Jumps](https://kotlinlang.org/docs/reference/returns.html)  


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


### No Checked Exception

java에서는 무조건 처리해야 하지만 코틀린에서는 기술하지 않아도 된다.  
여전히 자바처럼 try/catch/finally로 처리 할 수 있다

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

### Elvis 연산자

3항식 연산자 대신 Elvis를 사용하라

> java
```java
String[] queries = queryString!=null ? queryString.split("&") : new String[0];
```

> kotlin

```kt
val queries: List<String> = queryString?.split("&") ?: listOf()
```

```kt
val l: Int = if (b != null) b.length else -1 //if/esle를 사용할 수도 있다. 아래처럼 엘비스를 사용하면

val l = b?.length ?: -1 //위와 같은 의미
```

### 변수 선언

```kt
 var|val [변수명]:[변수타입]
```
변수타입을 적지않아도  된다. (타입추론)


> var
```kt
var num:Int = 100
num = 10
num = "hello" //error

var num:Any = 100
num = "hello" //ok
```

> val
val로 선언시 읽기전용이 된다.  java의 final과 같다


### 리터럴 표현

> 숫치표현 시 인간이 인지하기 쉽게 _ 를 지원  
raw string를 지원한다  

```kt
var i = 123_456_789
var j = 0xFF_FF_FF  //16진수
var k = 100
println(k::class) //class kotlin.Int로 출력됨 
var num1 = 100L //kotlin.Long
var num2 = 100F  //kotlin.Float

var msg1 ="""
  kotlin 문자열 표현입니다.
  문장1
  문장2
"""

var msg2 = """
| 1232323
| 문장123232
""".trimMargin()


var msg3 = """
! 1232323
! 문장123232
""".trimMargin("!")

var value =10
var data = arrayOf(1,2,3)
println("value는 ${value}")
println("배열마지막은 ${data[2]}")
```

### Null Safe

> ?를 붙이는 것은 Nullable 타입을 의미한다  


```kt
 val a: String  //a에 null를 대입 할 수 없다
 a = null //error
 
 val b: String? //a에 null를 대입 할 수 있다.
 b = null //ok
 
 b.length //컴파일에러
 print(b?.length) //?를 사용해서 작성하고 a가 null 이면 null을 반환  
 println(b?.length ?: 0) //a가 null이면 0을 반환
 println(b!!.length) //!!를 사용하면 NullPointException를 발생시킴
```




## [Java와 함께 쓰기](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html)

### @JvmName

> 아래 코드는 컴파일에러가 난다  
바이트코드로 변환시 시그니처를 구분못하기 때문이다.  

```kotlin
fun foo(a : List<String>) {
    println("foo(a : List<String>)")
}

fun foo(a : List<Int>) {
    println("foo(a : List<Int>)")
}
```

> 아래와 같이 수정한다.

```kotlin
@JvmName("fooListString")
fun foo(a : List<String>) {
    println("foo(a : List<String>)")
}

@JvmName("fooListInt")
fun foo(a : List<Int>) {
    println("foo(a : List<Int>)")
}
```
> 위 코드는 자바로 변환 시 아래와 같다 
```java
public static final void fooListString(@NotNull List a) {
   String var1 = "foo(a : List<String>)";
   System.out.println(var1);
}

public static final void fooListInt(@NotNull List a) {
   String var1 = "foo(a : List<Int>)";
   System.out.println(var1);
}
```
## @Thows

> kotlin
```kt
@Throws(IOException::class)
fun readFile(name: String): String {...}
```
> java 로 변환
```java
String readFile(String name) throws IOException {...}
```
