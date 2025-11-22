@echo off
echo.
echo [信息] 清理Web工程。
echo.

%~d0
cd %~dp0

cd ..
echo [信息] 清理Maven构建目录...
call mvn clean

echo [信息] 清理前端Node模块和构建目录...
cd student-handbook-vue
if exist node_modules (
  echo [信息] 删除node_modules目录...
  rmdir /s /q node_modules
)

if exist dist (
  echo [信息] 删除dist目录...
  rmdir /s /q dist
)

cd ..
echo [信息] 清理完成!
pause