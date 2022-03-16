# YeoManDa_Kotlin
Kotlin 공부를 위해 Java 버전으로 짜놓은 프로젝트를 Kotlin언어로 변경한 버전이다.
# Java Version
https://github.com/kenny8062/yeomanda?organization=kenny8062&organization=kenny8062
# 바뀐점
언어만 바꾼게 아니라 Retrofit2를 이용할 때 이전에는 execute()를 사용했던걸 enqueue()로 바꾸었다.
먼저 execute는 동기적 행동을 한다. 하지만 안드로이드 개발 제약상 서버와의 통신을 MainThread에서 할 수 없었다. 그래서 새로운 thread를 추가하여 서버와 통신을 했다.
하지만 이렇게 하면 mainThread에서 새로운 thread를 생성해 리퀘스트를 보내면 리스폰스를 받기 전에 다음 행동으로 넘어가서 NullException 일어났다.
처음으로 생각한 해결 방법은 execute()를 사용할 때는 다음 while()문을 이용하여 리스폰스를 받을때까지 강제로 while문을 돌게 하였다.
하지만 이는 사실상 멈춘게 아니라 while문 안에서 계속 돌고 있는것이기 때문에 자원소비가 너무 비효율적이라고 생각했다. 고정적으로 1초등을 sleep을 줄 수도 있지만 이 또한 비효율적이라고 생각했다.
그래서 나는 enqueue()를 이용해서 비동기로 하는 방법이 있어서 이번에 Kotlin으로 언어를 변환하면서 사용하였다.
enqueue는 함수 실행시 바로 백그라운드로 넘겨서 작업을 하기 때문에 새로운 thread를 생성 할 필요가 없다.
하지만 enqueue를 사용할 경우 성공적으로 리스폰스를 받았을때 실행되는 OnResponse 함수 안에서 작업을 안하면 execute때처럼 NullException이 일어나기 때문에 OnResponse 함수 안에서 모든 작업을 해야 했다.

# 느낀점
enqueue로 코드를 바꿈으로서 Activity가 코드가 더 길어져서 더 무거워진것이 보였다. 이는 나중에 유지보수와 수정을 할 때 불편함을 겪을 수 있다.
그래서 다음번에는 MVVM 구조를 공부 한 뒤 MVVM 구조로 다시 개발을 해 볼 예정이다.
