APPNAME=scm-datagen
CXX=g++
CXXFLAGS=-std=c++17 -g -Wall -Wextra -Wfloat-equal -Wundef -Wcast-align -Wwrite-strings -Wlong-long -Wmissing-declarations -Wredundant-decls -Wshadow -Woverloaded-virtual
LINKFLAGS=-lstdc++fs
OUTPUT_DIR=output
TARGET_DIR=target
DIR=$(TARGET_DIR) $(OUTPUT_DIR)

.PHONY: all clean

all: $(APPNAME)

$(APPNAME): target/main.o target/am3x.o target/detm.o target/util.o
	$(CXX) $(CXXFLAGS) -o ./target/$(APPNAME) ./target/main.o ./target/am3x.o ./target/detm.o ./target/util.o $(LINKFLAGS)

target/main.o: main.cpp am3x.h detm.h util.h
	$(CXX) $(CXXFLAGS) -c ./main.cpp -o ./target/main.o

target/am3x.o: am3x.cpp am3x.h util.h
	$(CXX) $(CXXFLAGS) -c ./am3x.cpp -o ./target/am3x.o

target/detm.o: detm.cpp detm.h util.h
	$(CXX) $(CXXFLAGS) -c ./detm.cpp -o ./target/detm.o

target/util.o: $(DIRS) util.cpp util.h
	$(CXX) $(CXXFLAGS) -c ./util.cpp -o ./target/util.o

$(DIRS):
	mkdir -p $@

clean:
	rm -f ./target/$(APPNAME) ./target/main.o ./target/am3x.* ./target/detm.* ./target/util.* ./*-scm.tar.gz
