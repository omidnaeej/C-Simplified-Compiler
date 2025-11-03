double p(int a, int b)
{
    int c = a + b;
    printf(c);
    return c+6;
}

int f(){
    short c = 5;
    printf(c);
    return c;
}


double fd(double d)
{
    int c = 5;
    return c;
}

int main(){
    float x=1.5;
    char c = "l";
    short i=25;
    short j=215;
    p(c, i);

    int fr = f();

    int a=3;
    double res = fd(fr);
}