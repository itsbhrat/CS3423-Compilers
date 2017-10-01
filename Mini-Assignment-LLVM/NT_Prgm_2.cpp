class CmplxNmb
{
	private:
	double real;
	double imaginary;
	
	public:
	CmplxNmb(double, double);
	double sq_modulus();
};

CmplxNmb::CmplxNmb(double r, double i)
{
	real = r;
	imaginary = i;
}

double CmplxNmb::sq_modulus()
{
	double mod;
	mod = real*real + imaginary*imaginary;
return mod;
}
