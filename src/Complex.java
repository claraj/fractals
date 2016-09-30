
/**
 * Created by clara on 9/26/16.
 */
public class Complex {

    double real;
    double imaginary;

    public Complex(double r, double i) {
        real = r;
        imaginary = i;
    }


    public Complex add(Complex z) {

        return new Complex(this.real+z.real, this.imaginary+z.imaginary);

    }


    public static Complex absSquare(Complex z) {

        double x = Math.abs(z.real);
        double y = Math.abs(z.imaginary);

        //Same square

        return new Complex(x*x - y*y , 2*x*y);

    }

    public static Complex square(Complex z) {

        double x = z.real;
        double y = z.imaginary;
        // complex (x + iy) squared
        // xx - yy +  i2xy

        return new Complex(x*x - y*y, 2*y*x);

    }


    //Is this complex number less than absValue?
    public boolean lessThan(double absValue) {

        //Go look up how to do this properly

        double sqReal = real * real;
        double sqImg = imaginary * imaginary;

        return ( Math.sqrt(sqImg + sqReal) < absValue );

    }

    //Is this complex number greater than absValue?
    public boolean greaterThan(double absValue) {

        if  (Double.isNaN(real) || Double.isNaN(imaginary) ) {
            return true;
        }

        double sqReal = real * real;
        double sqImg = imaginary * imaginary;

        return ( Math.sqrt(sqImg + sqReal) > absValue );

    }


    @Override
    public String toString() {
        return real + " + " + imaginary + " i";
    }

}
