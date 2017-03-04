/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algs.bean;

/**
 *
 * @author dell
 */
public class TimeSettings {
    private double[][] T1;// 任务i由维修工j完成的时间是T1[i][j],m*n
    private double[] T2;// 从公司到任务i所在地需要的时间是T2[i],1*m
    private double[][] T3;// 从任务i所在地到任务i'所在地需要的时间是T3[i][i'],m*m,不存在用0表示
    
    public TimeSettings(){}
    
    public TimeSettings(double[][] T1,double[] T2,double[][] T3){
        this.T1 = T1;
        this.T2 = T2;
        this.T3 = T3;
    }

    /**
     * @return the T1
     */
    public double[][] getT1() {
        return T1;
    }

    /**
     * @param T1 the T1 to set
     */
    public void setT1(double[][] T1) {
        this.T1 = T1;
    }

    /**
     * @return the T2
     */
    public double[] getT2() {
        return T2;
    }

    /**
     * @param T2 the T2 to set
     */
    public void setT2(double[] T2) {
        this.T2 = T2;
    }

    /**
     * @return the T3
     */
    public double[][] getT3() {
        return T3;
    }

    /**
     * @param T3 the T3 to set
     */
    public void setT3(double[][] T3) {
        this.T3 = T3;
    }
    
    
}
