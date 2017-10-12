COMPILE = g++
FLAGS = -g -W -Wall -Werror -ansi -pedantic -std=c++0x
MAIN = main.cpp
EXEC1 = problem1
EXEC2 = problem2

all:
	$(COMPILE) $(FLAGS) $(MAIN) -o $(EXEC1)
 
test: all 
	./$(EXEC1)
 
clean:
	rm -f $(EXEC1) $(EXEC2)
