import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.text.ChoiceFormat.nextDouble;

/**
 * Created by hp on 10.12.2016.
 */
public class Graph extends JFrame {
    final static int COUNT_POINTS = 30;
    final static int LENGTH_X = 1500;
    final static int DEGREE = 4;

    static ArrayList<MyPoints> points = new ArrayList<MyPoints>();
    static ArrayList<MyPoints> pointsForGraph = new ArrayList<MyPoints>();
    static ArrayList<MyPoints> pointsForGraph2 = new ArrayList<MyPoints>();
    static ArrayList<MyPoints> pointsGoodFromSample = new ArrayList<MyPoints>();

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setSize(new Dimension(2000, 1200));

        for(double i = -Math.PI; i < 2 * Math.PI; i += 0.002){
            double xx = 200 + i * 200; //для func1, func 3
            double yy = 300 - graph.func1(i) * 200; //для func1, func 3
            pointsForGraph.add(new MyPoints(xx, yy));


        //int xx = 500 + i * 200 * 0.03; // для func 2
            // int yy = 300 - func2(i) * 200 * 0.03; // для func 2

        }

        for (int i = 0; i < COUNT_POINTS; i++) {
            //MyPoints point = graph.generateNormalSample();
            MyPoints point = graph.generateSamle();
            points.add(point);
        }

        double[] w;
       w = graph.builtAndDecisionPolinom(pointsGoodFromSample);
        for (int i = 0; i < w.length; i++)
            System.out.println("w" + i + "= " + w[i]);

     for(double i = -Math.PI; i < 2 * Math.PI; i += 0.002){
         double xx = 200 + i * 200; //для func1, func 3
         double yy = 300 - graph.funcW(i,w) * 200; //для func1, func 3

         pointsForGraph2.add(new MyPoints(xx,yy));
         //int xx = 500 + (int)(i * 200 * 0.03); // для func 2
         // int yy = 300 - (int)(func2(i) * 200 * 0.03); // для func 2

     }

        graph.setVisible(true);
        graph.repaint();
    }


 public void paint(Graphics graphics){

       //отрисовка графика
       graphics.drawLine(200, 0, 200, 600);
       graphics.drawLine(0, 300, LENGTH_X, 300);
     for(int i = 0; i < pointsForGraph.size(); i++){
         graphics.drawOval((int)(pointsForGraph.get(i).x - 1/2),(int)( pointsForGraph.get(i).y - 1/2), 1,1);
     }
      //отрисовываем выборку
     for (int i = 0; i < COUNT_POINTS; i++) {
            int diameter = 1;
            graphics.drawOval((int)(points.get(i).x - diameter/2) ,(int)( points.get(i).y - diameter/2), 10, 10);
     }
     graphics.setColor(new Color(50, 155, 50));

     //отрисовываем график
     for(int i = 0; i < pointsForGraph2.size(); i++){
         graphics.drawOval((int)(pointsForGraph2.get(i).x - 1/2),(int) (pointsForGraph2.get(i).y - 1/2), 2,2);
     }

       //выводим кроссвалидацию
   }

   public MyPoints graphics(double x, double t){
       MyPoints point = new MyPoints();
       double xx = 200 + x *200;
       double yy = 300 - t*200;
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

   public MyPoints generateSamle(){
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
       pointsGoodFromSample.add(new MyPoints(x,t));
       return graphics(x,t);

   }

   public MyPoints generateNormalSample(){
       Random random = new Random();
       double x;
       double t;
       int rd = random.nextInt(LENGTH_X);
       x = (2 * Math.PI *rd) / LENGTH_X;
       t = func1(x) + random.nextGaussian()*0.2;
       pointsGoodFromSample.add(new MyPoints(x,t));
       return graphics(x,t);
   }


    public double[] builtAndDecisionPolinom(ArrayList<MyPoints> points){ //составляем матрицы A,B
        double [][] A = new double[DEGREE+1][DEGREE+1];
        double [] B = new double[DEGREE+1];
        double [] w;

        for(int i = 0; i < DEGREE + 1; i++){
            for(int j = 0; j < DEGREE + 1; j++) {
                A[i][j] = getSumX(points, i+j);
            }
            for(int k = 0; k < COUNT_POINTS; k++)
                B[i] = B[i] + points.get(k).getY() * Math.pow(points.get(k).getX(), i);
        }
       //решаем методом гаусса

        w = decisionPolinomByGauss(A,B);
        return w;
    }

    private double getSumX(ArrayList<MyPoints> point, int power){
        double result = 0;
        for(int i = 0; i < point.size(); i++){
            result+= Math.pow(point.get(i).getX(), power);
        }
        return result;
    }

    public double[] decisionPolinomByGauss(double [][]A, double []B){
        double[]w = new double[DEGREE+1];
        //решение полинома
        double d = 0, s = 0;

        for (int k = 0; k < DEGREE+1; k++) {// прямой ход
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

//    public double crossValidate(double[]x, double[]y){ //полином от w - значение f
//        double error = 0;
//        for(int i = 0; i < COUNT_POINTS; i++){
//
//
//        }
//        return error /= COUNT_POINTS;
//    }

    public double funcW(double x, double [] w){
        double res = 0;
        for(int i = 0; i < DEGREE+1; i++){
            res+= w[i] * Math.pow(x, i);
        }
        return res;

    }
}
