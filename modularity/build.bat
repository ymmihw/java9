dir  /B  /S  hello-module\src\main\*.java > src1.txt
javac -d out/hello.modules @src1.txt

dir  /B  /S  main-app\src\main\*.java > src1.txt
javac --module-path out -d out/main.app @src1.txt

java --module-path out -m main.app/com.ymmihw.core.java9.main.MainApp