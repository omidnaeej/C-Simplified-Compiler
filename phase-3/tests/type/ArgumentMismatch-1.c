void print1(int a, int b) {
    int c = a + b;
    printf(c);
}

void print2(float a, float b) {
    int c = a + b;
    printf(c);
}

int main(char f) {
    int a = 123;
    float b = 5.5;
    print1(a, b);
    print2(a, b);
    return 0;
}