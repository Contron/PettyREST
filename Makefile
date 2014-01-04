PACKAGE = com/connorhaigh/pettyrest
SRC = src/$(PACKAGE)
BIN = bin/$(PACKAGE)

bin/PettyREST.jar: $(BIN)/PettyREST.class $(subst .java,.class, $(subst $(SRC),$(BIN), $(foreach dir,$(wildcard $(SRC)/*),$(wildcard $(dir)/*.java))))
        cd bin && jar cf PettyREST.jar $(subst $(BIN),$(PACKAGE), $^)

$(BIN)/PettyREST.class: $(SRC)/PettyREST.java
        mkdir -p bin
        javac -classpath src -d bin $(SRC)/PettyREST.java

$(BIN)/core/%.class: $(SRC)/core/%.java
        javac -classpath src -d bin $<

$(BIN)/exception/%.class: $(SRC)/exception/%.java
        javac -classpath src -d bin $<

$(BIN)/http/%.class: $(SRC)/http/%.java
        javac -classpath src -d bin $<

$(BIN)/listener/%.class: $(SRC)/listener/%.java
        javac -classpath src -d bin $<

.PHONY: clean
clean:
        rm -rf bin
