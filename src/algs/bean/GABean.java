package algs.bean;

import java.util.Arrays;


public class GABean implements Cloneable{

    private double fitness;//适应度值，即问题解决所需的时间
    private int[] strategy;//分配策略，strategy[i]=j,表示任务i+1分配给工人j
    private int d;//染色体长度
    private int cur_t;//当前进化代数
    private double Pi;//累计进化概率

    public GABean() {

    }

    /**
     * 构造函数，解的长度
     * @param d
     */
    public GABean(int d) {
            this.d = d;
            this.strategy = new int[d];
    }

    public double getFitness() {
            return fitness;
    }

    public void setFitness(double fitness) {
            this.fitness = fitness;
    }



    public int[] getStrategy() {
            return strategy;
    }

    public void setStrategy(int[] strategy) {
            this.strategy = strategy;
    }

    public int getD() {
            return d;
    }

    public void setD(int d) {
            this.d = d;
    }





    public int getCur_t() {
            return cur_t;
    }

    public void setCur_t(int cur_t) {
            this.cur_t = cur_t;
    }


    public double getPi() {
            return Pi;
    }

    public void setPi(double pi) {
            Pi = pi;
    }



    @Override
    public String toString() {
        return "Solution [fitness=" + fitness + ", strategy="
                + Arrays.toString(strategy) + ", d=" + d + ", cur_t=" + cur_t
                + ", Pi=" + Pi + "]";
    }

    @Override
    public Object clone() {
        GABean solution = null;
        try{
            solution = (GABean) super.clone();//浅复制
            solution.strategy = strategy.clone();
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return solution;
    }
	
	
}
