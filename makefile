limpiar:
	rm -rf bin
	rm -rf html
	rm -f *.jar
compilar:limpiar
	mkdir bin
	find src -name "*.java" | xargs javac -cp bin -d bin
jar:compilar
	jar cvfm padron.jar manifest -C bin .
javadoc:compilar
	find src -type f -name "*.java" | xargs javadoc -d html -encoding utf-8 -docencoding utf-8 -charset utf-8
