int det_mat_mul(double **a, double **b, int r_a, int r_b, int c_a, int c_b)
{
	if(c_a != r_b)
	{
		return -1;
	}

	double c[r_a][c_b];
	for(int i = 0; i < r_a; i++)
	{
		for(int j = 0;j < c_b; j++)
		{
			c[i][j] = 0;
			for(int k = 0;k < c_a; k++)
			{
				c[i][j] = c[i][j] + a[i][k]*b[k][j];
			}
		}
	}
return 1;
}
