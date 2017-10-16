#include <string.h>
void func(char* a, int index, int len) {
  char* e = "\0";
  char* s = strncpy(a, a+index, len);
  char* d = strcat(s, e);

}