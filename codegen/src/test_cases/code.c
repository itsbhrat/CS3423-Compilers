#include <string.h>
#include <stdio.h>


int fact(int n, char* str) {
	if(n <= 1)
		return 1;
	return n*fact(n-1, str);
}
int main()
{
	char a[] = "harsh agarwal";
	
	a[1] = 'c';
	a[8] = 'c';	

	fact(5, a);
	return 0;
}
