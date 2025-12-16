rem comienza la prueba del control de la Practica 1y2 de SI curso 2024-25
rem ***********************Version 9 de octubre 2024
cd ..
call P1y2_SI2024.bat  -f ./1juan/config-cifra-quijote1.txt
cd ./1juan/finales


rem deben ser iguales
Fc criptograma1.txt criptograma1_juan.txt 
Fc quijote1recuperado.txt quijote1recuperado_juan.txt 


cd ../..




pause 	OJO QUE ES EL FINAL*********************************************************
pause
