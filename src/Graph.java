import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static java.text.ChoiceFormat.nextDouble;

/**
 * Created by hp on 10.12.2016.
 */
public class Graph extends JFrame {
    final int COUNT_POINTS = 10;
    final int LENGTH_X = 1500;
    final int LENGTH_Y = 600;
    final int DEGREE = 4;
     Point [] points = new Point[COUNT_POINTS];

    private double []w = new double[COUNT_POINTS];

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setSize(new Dimension(2000, 1200));
        graph.setVisible(true);
        graph.repaint();
    }


 public void paint(Graphics graphics){
int height = getHeight(),
        width = getWidth();
       //отрисовка графика
       graphics.drawLine(200, 0, 200, 600);
       graphics.drawLine(0, 300, LENGTH_X, 300);
       int [] x = new int[LENGTH_X];
       int[] y = new int[LENGTH_X];

     int j = 0;
     for(double i = -Math.PI; i < 2 * Math.PI; i += 0.002){
        int xx = 200 + (int)(i * 200); //для func1, func 3
        int yy = 300 - (int)(func1(i) * 200); //для func1, func 3
        //int xx = 500 + (int)(i * 200 * 0.03); // для func 2
       // int yy = 300 - (int)(func2(i) * 200 * 0.03); // для func 2
         j++;
         int diameter = 1;
         graphics.drawOval(xx - diameter / 2, yy - diameter / 2, diameter, diameter);
     }

       //отрисовываем выборку


     Point point = new Point();
        for (int i = 0; i < COUNT_POINTS; i++) {
            //point = generateNormalSample();
            point = generateSamle();
            int diameter = 1;
            graphics.drawOval(point.x - diameter/2 , point.y - diameter/2, 10, 10);

        }



       for(int i = 0; i < points.length; i++){
           graphics.drawOval(points[i].x -1 /2, points[i].y-1/2, 5, 5);
       }
       graphics.setColor(new Color(255, 0, 0));
/////////////
       w = builtAndDecisionPolinom(x,y);//?считаем W
       //рисуем x и W
     for(int i = 0; i < getWidth(); i++) {
         x[i] = i;
         y[i] = (int)(-funcW(i*2*Math.PI/getWidth(), w)*150)+ LENGTH_Y;//1
     }
     graphics.drawPolyline(x, y, getWidth());

       //выводим кроссвалидацию
   }

   public Point graphics(double x, double t){
       Point point = new Point();
       int xx = 200 + (int)(x *200);
       int yy = 300 - (int)(t*200);
       //int xx = 500 + (int)(i * 200 * 0.03); // для func 2
       // int yy = 300 - (int)(func2(i) * 200 * 0.03); // для func 2
       point.x = xx;
       point.y = yy;
       return point;
   }


   public double func1(double x){
       return Math.cos(x);
   }

   public double func2(double x){
       return 5*Math.pow(x,3) + Math.pow(x,2) + 5;
   }

   public double func3(double x){
       return Math.sin(x);
   }

   public Point generateSamle(){
      double x;
      double t;
       double e = 0.0;
       double s = 0.0;
       Point point = new Point();
       Random random = new Random();

       int rand = random.nextInt(getWidth());

       x = (2 * Math.PI *rand) /getWidth();
       e = random.nextDouble()*0.2;
       if(e >= 0.1){
           s = 1.;
       }else
           s = -1;
       t = func1(x) + s*e ;
       return graphics(x,t);

   }

   public Point generateNormalSample(){
       Random random = new Random();
       double x;
       double t;
       Point point = new Point();
       int rd = random.nextInt(LENGTH_X);
       x = (2 * Math.PI *rd) / LENGTH_X;
       t = func1(x) + random.nextGaussian()*0.2;
       return graphics(x,t);
   }


    public double[] builtAndDecisionPolinom(int [] x, int [] t){
        double [][] A = new double[DEGREE][DEGREE];
        double [] B = new double[DEGREE];

        for(int i = 0; i < DEGREE; i++){
            for(int j = 0; j < DEGREE; j++) {
                A[i][j] = getSumX(x, i+j);
            }
            for(int k = 0; k < COUNT_POINTS; k++)
                B[i] = B[i] + t[k] * Math.pow(x[k], i);
        }
       //решаем методом гаусса
        return decisionPolinomByGauss(A,B);
    }

    private double getSumX(int[]x, int power){
        double result = 0;
        for(int i = 0; i < x.length; i++){
            result+= Math.pow(x[i], power);
        }
        return result;
    }

    public double[] decisionPolinomByGauss(double [][]A, double []B){
        double[]w = new double[DEGREE];
        //решение полинома
        double d = 0, s = 0;

        for (int k = 0; k < DEGREE; k++) {// прямой ход
            for (int j = k + 1; j < DEGREE+1; j++) {
                d = A[j][k] / A[k][k];
                for (int i = k; i < DEGREE +1; i++) {
                    A[j][i] = A[j][i] - d * A[k][i];
                }
                B[j] = B[j] - d * B[k];
            }
        }
        // обратный ход
        w[DEGREE] = B[DEGREE]/A[DEGREE][DEGREE];
        for (int k = DEGREE-1; k >= 0; k--) {
            d = 0;
            for (int j = k + 1; j < DEGREE+1; j++) {
                s = A[k][j] * w[j];
                d = d + s;
            }
            w[k] = (B[k] - d) / A[k][k];
        }
        return w;
    }

    public double crossValidate(double[]x, double[]y){ //полином от w - значение f
        double error = 0;
        for(int i = 0; i < COUNT_POINTS; i++){


        }
        return error /= COUNT_POINTS;
    }

    public double funcW(double x, double [] w){
        double res = 0;
        for(int i = 0; i < DEGREE; i++){
            res+= w[i] * Math.pow(x, i);
        }
        return res;

    }
}
