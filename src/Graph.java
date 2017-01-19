import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hp on 10.12.2016.
 */
public class Graph extends JFrame {
    final static int COUNT_POINTS = 10;
    final static int LENGTH_X = 1500;
    final static int DEGREE = 6;

    final static double delta = 0.00001;
    final static int POINT = 3;


    static ArrayList<MyPoints> points = new ArrayList<>();
    static ArrayList<MyPoints> pointsForGraph = new ArrayList<>();
    static ArrayList<MyPoints> pointsForGraph2 = new ArrayList<>();
    static ArrayList<MyPoints> pointsGoodFromSample = new ArrayList<>();

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setSize(new Dimension(2000, 1200));
        graph.setBackground(Color.WHITE);

        for(double i = -Math.PI; i < 2 * Math.PI; i += 0.002){
            double xx = 200 + i * 200; //для func1, func 3
            double yy = 300 - graph.func1(i) * 200; //для func1, func 3

            //double xx = 500 + i * 200 * 0.03; // для func 2
            //double yy = 300 - graph.func2(i) * 200 * 0.03; // для func 2

            pointsForGraph.add(new MyPoints(xx, yy));
        }

        for (int i = 0; i < COUNT_POINTS; i++) {
            //MyPoints point = graph.generateNormalSample();
            MyPoints point = graph.generateSamle();
            points.add(point);
        }

        double[] w;
       w = graph.builtAndDecisionPolinom(pointsGoodFromSample, DEGREE,COUNT_POINTS);
//        for (int i = 0; i < w.length; i++) {
//          System.out.println("w" + i + "= " + w[i]);
//        }

     for(double i = -Math.PI; i < 2 * Math.PI; i += 0.002){
        double xx = 200 + i * 200; //для func1, func 3
        double yy = 300 - graph.funcW(i,w,DEGREE) * 200; //для func1, func 3
        // double xx = 500 + (int)(i * 200 * 0.03); // для func 2
         //double yy = 300 - (int)graph.funcW(i,w,DEGREE) * 200 * 0.03; // для func 2
         pointsForGraph2.add(new MyPoints(xx,yy));
     }

        System.out.println("crossvalidate = " + graph.crossValidate(pointsGoodFromSample));
        graph.setVisible(true);
        graph.repaint();
    }


 public void paint(Graphics graphics){

     double[] y = new double[COUNT_POINTS];
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
     graphics.setColor(Color.ORANGE);

     //отрисовываем график
     for(int i = 0; i < pointsForGraph2.size(); i++){
         graphics.drawOval((int)(pointsForGraph2.get(i).x - 1/2),(int) (pointsForGraph2.get(i).y - 1/2), 2,2);
     }

     y = netWork(points);

     graphics.setColor(Color.GREEN);
     for (int i = 0; i < COUNT_POINTS; i++) {
         int diameter = 1;
         graphics.drawOval((int)(points.get(i).x - diameter/2) ,(int)( y[i] - diameter/2), 10, 10);
     }

       //выводим кроссвалидацию
 }

   public MyPoints graphics(double x, double t){
       MyPoints point = new MyPoints();
       double xx = 200 + x *200;
       double yy = 300 - t*200;
       //double xx = 500 + (int)(x * 200 * 0.03); // для func 2
      // double yy = 300 - (int)(t * 200 * 0.03); // для func 2
       point.x = xx;
       point.y = yy;
       return point;
   }


   public double func1(double x){
       return Math.cos(x);
   }

   public double func2(double x){
       return 5*Math.pow(x,6) + Math.pow(x,2) + 5;
   }

   public double func3(double x){
       return Math.sin(x);
   }

   public MyPoints generateSamle(){
      double x;
      double t;
       double e = 0.0;
       double s = 0.0;
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
       t = func1(x) - nextGaussian1()*0.2;
       pointsGoodFromSample.add(new MyPoints(x,t));
       return graphics(x,t);
   }

   public double nextGaussian1(){
       Random random = new Random();
       return (Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) / 6;
   }


    public double[] builtAndDecisionPolinom(ArrayList<MyPoints> points, int polinomDegree,int count){ //составляем матрицы A,B
        double [][] A = new double[polinomDegree+1][polinomDegree+1];
        double [] B = new double[polinomDegree+1];
        double [] w;

        for(int i = 0; i < polinomDegree + 1; i++){
            for(int j = 0; j < polinomDegree + 1; j++) {
                A[i][j] = getSumX(points, i+j);
            }
            for(int k = 0; k < count; k++)
                B[i] = B[i] + points.get(k).getY() * Math.pow(points.get(k).getX(), i);
        }
       //решаем методом гаусса

        w = decisionPolinomByGauss(A,B,polinomDegree);
        return w;
    }

    private double getSumX(ArrayList<MyPoints> point, int power){ //sum x^power
        double result = 0;
        for(int i = 0; i < point.size(); i++){
            result+= Math.pow(point.get(i).getX(), power);
        }
        return result;
    }

    public double[] decisionPolinomByGauss(double [][]A, double []B, int polinomDegree){ //
        double[]w = new double[polinomDegree+1];
        //решение полинома
        double d = 0, s = 0;

        for (int k = 0; k < polinomDegree+1; k++) {// прямой ход
            for (int j = k + 1; j < polinomDegree+1; j++) {
                d = A[j][k] / A[k][k];
                for (int i = k; i < polinomDegree +1; i++) {
                    A[j][i] = A[j][i] - d * A[k][i];
                }
                B[j] = B[j] - d * B[k];
            }
        }
        // обратный ход
        w[polinomDegree] = B[polinomDegree]/A[polinomDegree][polinomDegree];
        for (int k = polinomDegree-1; k >= 0; k--) {
            d = 0;
            for (int j = k + 1; j < polinomDegree+1; j++) {
                s = A[k][j] * w[j];
                d = d + s;
            }
            w[k] = (B[k] - d) / A[k][k];
        }
        return w;
    }

    public double crossValidate(ArrayList<MyPoints> points){ //полином от w - значение f
        double error = 0;
        double [] t2 = new double[COUNT_POINTS - 1];
        double [] x2 = new double[COUNT_POINTS - 1];
        double curError = 0;
        ArrayList<Double> errors = new ArrayList<>();

        double [] x = new double[COUNT_POINTS];
        double [] t = new double[COUNT_POINTS];
        for(int i = 0; i < points.size();i++){
            x[i] = points.get(i).x;
            t[i] = points.get(i).y;
        }

        for(int degree = 1; degree < DEGREE; degree++) {
            error = 0;
            for (int curPoint = 0; curPoint < COUNT_POINTS; curPoint++) {

                for (int i = 0; i < curPoint; i++) {
                    x2[i] = x[i];
                    t2[i] = t[i];

                }
                for (int i = curPoint + 1; i < COUNT_POINTS; i++) {
                    x2[i - 1] = x[i];
                    t2[i - 1] = t[i];
                }

                //Для нахождения w используются все точки, кроме одной!
                ArrayList<MyPoints> pointses = new ArrayList<>();
                for (int i = 0; i < t2.length; i++) {
                    pointses.add(new MyPoints(x2[i], t2[i]));
                }

                double[] w = builtAndDecisionPolinom(pointses, degree, COUNT_POINTS - 1);
                double y = funcW(x[curPoint], w, degree);
                curError = Math.abs(t[curPoint] - y);
                error += curError;
            }
            errors.add(error);

        }

        int index = 0;
        double minError = Double.MAX_VALUE;
        for(int i = 0; i < errors.size(); i++){
            double min = errors.get(i);
            if(min < minError) {
                minError = min;
                index = i + 1;
            }
        }

        return  index;
      //return error /= COUNT_POINTS;
    }

    public double funcW(double x, double [] w, int polinomDegree){ //считаем значение полинома w при известных x w
        double res = 0;
        for(int i = 0; i < polinomDegree+1; i++){
            res+= w[i] * Math.pow(x, i);
        }
        return res;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double[] netWork(ArrayList<MyPoints> points){
        double[] weight = new double[POINT*2];
        double[] w = new double[POINT+1];
        double[] y = new double[points.size()];
        double errorMin = Double.MAX_VALUE;
        double errorCurrent = 0;
        Random random = new Random();
        double [] gradient = new double[POINT*2];
        double[] yMin = new double[points.size()];
        double n = random.nextDouble();

        for(int k = 0; k < 10000; k++){ //выполняем эти иттерации 10 000 раз.
            //генерим веса
            for (int i = 0; i < POINT; i++) {
                weight[i] = random.nextDouble();
            }

            for (int i = 0; i < POINT + 1; i++) {
                w[i] = random.nextDouble();
            }

            while (errorMin > 0.0001) {

                for (int i = 0; i < points.size(); i++) {
                    y[i] = valueY(weight, w, points.get(i).x);
                }

                errorCurrent = valueError(points, y); //посчитали ошибку

                if(errorMin > errorCurrent) {
                    errorMin = errorCurrent;
                    for (int i = 0; i < points.size(); i++) {
                        yMin[i] = y[i];
                    }
                }

                //пересчет весов
                for (int i = 0; i < POINT * 2; i++) {
                    weight[i] -= gradient(weight, points, w)[i] * n;
                }

            }
        }

       return yMin;
    }

    public double valueY(double[] weight, double[] w, double x) {
        double y = 0;
        double[] z = new double[POINT];
        for(int i = 0 ; i < POINT; i++) {
            for(int j = 0; j < POINT; j++) {
                z[i] = funcActive(x * weight[i]*w[j]);
            }
        }

        for(int i = POINT; i <POINT*2; i++) {
            for(int j = 0; j < POINT; j++) {
                y += weight[i]*z[j];
            }
        }
        y+= w[POINT+1]; //прибавляем W02

      return y;
    }


    public double funcActive(double x ){//функция активации гиперболический тангенс
        return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
    }

    //считаем ошибку
    public double valueError(ArrayList<MyPoints> points, double[]y) {
        double error = 0;
        for(int i = 0; i < points.size(); i++){
            error+= Math.pow(y[i] - points.get(i).getY(),2);
        }
        return 0.5*error;
    }

    //считаем градиент
    public double[] gradient(double[]weight, ArrayList<MyPoints> points,double[]w) {
        double[] gradient = new double[POINT*2];
        double[] weightDelta = new double[POINT*2];
        double[] y = new double[points.size()];
        double[] yy = new double[points.size()];

        for(int i = 0; i <POINT*2; i++) {
            weightDelta[i] = weight[i];
        }

        for(int i = 0; i < POINT*2; i++) {
            weightDelta[i]+=delta;

            for (int j = 0; i < points.size(); i++) {
                y[i] = valueY(weight, w, points.get(i).x);
            }

            for (int j = 0; i < points.size(); i++) {
                yy[i] = valueY(weightDelta, w, points.get(i).x);
            }

            gradient[i] = (valueError(points,yy) - valueError(points,y)) / delta;
        }
        return gradient;
    }


}
