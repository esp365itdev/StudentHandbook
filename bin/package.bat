@echo off
echo.
echo [信息] 打包Web工程，生成war/jar包文件。
echo.

%~d0
cd %~dp0

cd ..
echo [信息] 清理旧的构建文件...
call mvn clean

echo [信息] 安装依赖...
call mvn install -DskipTests

echo [信息] 编译和打包项目...
call mvn package -DskipTests

echo [信息] 构建完成!
pause