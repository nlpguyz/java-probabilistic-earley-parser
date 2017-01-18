package org.leibnizcenter.cfg.perf;


import org.leibnizcenter.cfg.algebra.matrix.Matrix;
import org.leibnizcenter.cfg.algebra.matrix.QRDecomposition;

/**
 * The {@code PolynomialRegression} class performs a polynomial regression
 * on an set of <em>N</em> data points (<em>y<sub>i</sub></em>, <em>x<sub>i</sub></em>).
 * That is, it fits a polynomial
 * <em>y</em> = &beta;<sub>0</sub> +  &beta;<sub>1</sub> <em>x</em> +
 * &beta;<sub>2</sub> <em>x</em><sup>2</sup> + ... +
 * &beta;<sub><em>d</em></sub> <em>x</em><sup><em>d</em></sup>
 * (where <em>y</em> is the response variable, <em>x</em> is the predictor variable,
 * and the &beta;<sub><em>i</em></sub> are the regression coefficients)
 * that minimizes the sum of squared residuals of the multiple regression model.
 * It also computes associated the coefficient of determination <em>R</em><sup>2</sup>.
 * <p>
 * This implementation performs a QR-decomposition of the underlying
 * Vandermonde matrix, so it is not the fastest or most numerically
 * stable way to perform the polynomial regression.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class PolynomialRegression {
    private final String variableName;  // name of the predictor variable
    private int degree;                 // degree of the polynomial regression
    private Matrix beta;                // the polynomial regression coefficients
    private double sse;                 // sum of squares due to error
    private double sst;                 // total sum of squares


    /**
     * Performs a polynomial reggression on the data points {@code (y[i], x[i])}.
     * Uses n as the name of the predictor variable.
     *
     * @param x      the values of the predictor variable
     * @param y      the corresponding values of the response variable
     * @param degree the degree of the polynomial to fit
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public PolynomialRegression(double[] x, double[] y, int degree) {
        this(x, y, degree, "n");
    }

    /**
     * Performs a polynomial reggression on the data points {@code (y[i], x[i])}.
     *
     * @param x            the values of the predictor variable
     * @param y            the corresponding values of the response variable
     * @param degree       the degree of the polynomial to fit
     * @param variableName the name of the predictor variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public PolynomialRegression(double[] x, double[] y, int degree, String variableName) {
        this.degree = degree;
        this.variableName = variableName;

        int n = x.length;
        QRDecomposition qr = null;
        Matrix matrixX = null;

        // in case Vandermonde matrix does not have full rank, reduce degree until it does
        while (true) {

            // build Vandermonde matrix
            double[][] vandermonde = new double[n][this.degree + 1];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j <= this.degree; j++) {
                    vandermonde[i][j] = Math.pow(x[i], j);
                }
            }
            matrixX = new Matrix(vandermonde);

            // find least squares solution
            qr = new QRDecomposition(matrixX);
            if (qr.isFullRank()) break;

            // decrease degree and try again
            this.degree--;
        }

        // create matrix from vector
        Matrix matrixY = new Matrix(y, n);

        // linear regression coefficients
        beta = qr.solve(matrixY);

        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < n; i++)
            sum += y[i];
        double mean = sum / n;

        // total variation to be accounted for
        for (int i = 0; i < n; i++) {
            double dev = y[i] - mean;
            sst += dev * dev;
        }

        // variation not accounted for
        Matrix residuals = matrixX.times(beta).minus(matrixY);
        sse = residuals.norm2() * residuals.norm2();
    }

    /**
     * Unit tests the {@code PolynomialRegression} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        double[] x = {0,
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                17,
                18,
                19,
                20,
                21,
                22,
                23,
                24,
                25,
                26,
                27,
                28,
                29,
                30,
                31,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                43,
                44,
                45,
                46,
                47,
                48,
                49,
                50,
                51,
                52,
                53,
                54,
                55,
                56,
                57,
                58,
                59,
                60,
                61,
                62,
                63,
                64,
                65,
                66,
                67,
                68,
                69,
                70,
                71,
                72,
                73,
                74,
                75,
                76,
                77,
                78,
                79,
                80,
                81,
                82,
                83,
                84,
                85,
                86,
                87,
                88};
        double[] y = {69,
                81,
                20,
                22,
                19,
                41,
                18,
                38,
                40,
                26,
                27,
                24,
                36,
                20,
                17,
                23,
                21,
                37,
                45,
                43,
                54,
                61,
                66,
                86,
                94,
                84,
                94,
                112,
                76,
                172,
                87,
                155,
                178,
                204,
                131,
                219,
                121,
                155,
                149,
                161,
                129,
                137,
                157,
                160,
                187,
                245,
                204,
                184,
                196,
                222,
                217,
                214,
                228,
                319,
                292,
                261,
                270,
                277,
                306,
                322,
                322,
                323,
                342,
                336,
                317,
                338,
                352,
                388,
                386,
                399,
                411,
                437,
                448,
                466,
                494,
                508,
                549,
                555,
                561,
                609,
                623,
                633,
                671,
                723,
                725,
                736,
                748,
                770,
                806};

        PolynomialRegression regression = new PolynomialRegression(x, y, 3);
        System.out.println(regression);
    }

    /**
     * Returns the {@code j}th regression coefficient.
     *
     * @param j the index
     * @return the {@code j}th regression coefficient
     */
    public double beta(int j) {
        // to make -0.0 print as 0.0
        if (Math.abs(beta.get(j, 0)) < 1E-4) return 0.0;
        return beta.get(j, 0);
    }

    /**
     * Returns the degree of the polynomial to fit.
     *
     * @return the degree of the polynomial to fit
     */
    public int degree() {
        return degree;
    }

    /**
     * Returns the coefficient of determination <em>R</em><sup>2</sup>.
     *
     * @return the coefficient of determination <em>R</em><sup>2</sup>,
     * which is a real number between 0 and 1
     */
    public double R2() {
        if (sst == 0.0) return 1.0;   // constant function
        return 1.0 - sse / sst;
    }

    /**
     * Returns the expected response {@code y} given the value of the predictor
     * variable {@code x}.
     *
     * @param x the value of the predictor variable
     * @return the expected response {@code y} given the value of the predictor
     * variable {@code x}
     */
    public double predict(double x) {
        // horner's method
        double y = 0.0;
        for (int j = degree; j >= 0; j--)
            y = beta(j) + (x * y);
        return y;
    }

    /**
     * Returns a string representation of the polynomial regression model.
     *
     * @return a string representation of the polynomial regression model,
     * including the best-fit polynomial and the coefficient of
     * determination <em>R</em><sup>2</sup>
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int j = degree;

        // ignoring leading zero coefficients
        while (j >= 0 && Math.abs(beta(j)) < 1E-5)
            j--;

        // create remaining terms
        while (j >= 0) {
            if (j == 0) s.append(String.format("%.2f ", beta(j)));
            else if (j == 1) s.append(String.format("%.2f %s + ", beta(j), variableName));
            else s.append(String.format("%.2f %s^%d + ", beta(j), variableName, j));
            j--;
        }
        s = s.append("  (R^2 = ").append(String.format("%.3f", R2())).append(")");
        return s.toString();
    }
}

