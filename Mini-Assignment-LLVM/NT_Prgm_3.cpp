int prime_cnt(int n)
{
	if(n == 2)
	{
		return 1;
	}
	else if(n == 3)
	{
		return 2;
	}
	else if(n < 2)
	{
		return 0;
	}
	
	int n_primes = 2;
	bool flag = false;
	
	for(int i = 4;i <= n;i++)
	{
		for(int j = 2; j*j <= i; j++)
		{
			if(i % j == 0)
			{
				flag = true;
				break;
			}
		}
		if(flag == false)
		{
			n_primes = n_primes + 1;
		}
	}
return n_primes;
}
