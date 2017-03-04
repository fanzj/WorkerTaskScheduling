/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algs.bean;

/**
 * 参数设置设置
 * @author Fanzhengjie
 */
public class GASettings {
    
    private int n;       //种群规模，即表示具有n条染色体
    private int t;       //迭代次数
    private double pc;   //交配概率，一般取0.4~0.99
    private double pm;   //变异概率，一般取0.001~0.1
    private double bmin;
   

    public GASettings(int n, int t, double pc, double pm,double bmin) {
        this.n = n;
     
        this.t = t;
        this.pc = pc;
        this.pm = pm;
        this.bmin = bmin;
    }
    
    public int getN() {
        return n;
    }

    public double getBmin() {
        return bmin;
    }

    public void setBmin(double bmin) {
        this.bmin = bmin;
    }
    

    public void setN(int n) {
        this.n = n;
    }

 

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public double getPc() {
        return pc;
    }

    public void setPc(double pc) {
        this.pc = pc;
    }

    public double getPm() {
        return pm;
    }

    public void setPm(double pm) {
        this.pm = pm;
    }

    @Override
    public String toString() {
        return "GASettings{" + "n=" + n  + ", t=" + t + ", pc=" + pc + ", pm=" + pm +  '}';
    }

   
    
}
