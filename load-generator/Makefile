APPNAME=scm-datagen
CXX=g++
CXXFLAGS=-std=c++17 -g -Wall -Wextra -Wfloat-equal -Wundef -Wcast-align -Wwrite-strings -Wlong-long -Wmissing-declarations -Wredundant-decls -Wshadow -Woverloaded-virtual
LINKFLAGS=-lstdc++fs

.PHONY: all clean

all: dirs $(APPNAME)

$(APPNAME): dirs target/main.o target/am3x.o target/detm.o target/util.o
	$(CXX) $(CXXFLAGS) -o ./target/$(APPNAME) ./target/main.o ./target/am3x.o ./target/detm.o ./target/util.o $(LINKFLAGS)

target/main.o: dirs main.cpp am3x.h detm.h util.h
	$(CXX) $(CXXFLAGS) -c ./main.cpp -o ./target/main.o

target/am3x.o: dirs am3x.cpp am3x.h util.h
	$(CXX) $(CXXFLAGS) -c ./am3x.cpp -o ./target/am3x.o

target/detm.o: dirs detm.cpp detm.h util.h
	$(CXX) $(CXXFLAGS) -c ./detm.cpp -o ./target/detm.o

target/util.o: dirs util.cpp util.h
	$(CXX) $(CXXFLAGS) -c ./util.cpp -o ./target/util.o

dirs:
	mkdir -p target output

clean:
	rm -f ./target/$(APPNAME) ./target/main.o ./target/am3x.* ./target/detm.* ./target/util.* ./*-scm.tar.gz
