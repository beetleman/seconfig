.PHONY: test-ci test test-watch clean
seconfig.jar: pom.xml test-ci
	clojure -A:uberjar
pom.xml:
	clojure -Spom
clean:
	rm -f seconfig.jar
	rm -rf test-results
test:
	clojure -A:test
test-ci:
	clojure -A:test --plugin kaocha.plugin/junit-xml --junit-xml-file test-results/junit.xml
test-watch:
	clojure -A:test --watch
