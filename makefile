JC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib

# Includes all JARs in lib/ (ObjectDB + JPA + Transaction)
CP = "$(LIB_DIR)/*:$(BIN_DIR)"
AGENT = $(LIB_DIR)/objectdb.jar

SRCS = $(wildcard $(SRC_DIR)/*.java)

all: compile

compile: $(SRCS)
	@mkdir -p $(BIN_DIR)
	$(JC) -cp "$(LIB_DIR)/*" -d $(BIN_DIR) $(SRCS)

r: compile
	$(JAVA) -javaagent:$(AGENT) -cp $(CP) Main

clean:
	rm -rf $(BIN_DIR)/*

.PHONY: all compile r clean