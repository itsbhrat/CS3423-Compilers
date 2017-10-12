#include <stdio.h>
class harsh_class
{
public:

	int a;
	int b;

	int fu_________nc() {
		int c, d, e = 10;
		if (a > 12345) {
			e++;
			if (98754 <= b) {
				c = b + a;
				return c;
			}
			else {
				d = b * a;
				return d;
			}
		}
		else {
			if (a <= 741852) {
				c = b - a;
				return c;
			}
			else {
				d = b / a;
				return d;
			}
		}
	}
};

int main()
{
	harsh_class obj;
	int c, d;
	obj.fu_________nc();
	if (obj.a > obj.b) {
		c = obj.b + obj.a;
	}
	else {
		d = obj.b * obj.a;
	}
	return 0;
}



int loop___________loop()
{
  harsh_class obj;
  int c = 0;
  for(int i = 0; i < obj.a; i++) {
      c += i;
  }
  return 0;
}