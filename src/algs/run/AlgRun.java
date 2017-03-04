/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algs.run;

import algs.bean.GABean;
import algs.bean.OptimalBean;
import algs.bean.TimeSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JTextArea;

/**
 *
 * @author dell
 */
public class AlgRun {
    
    private int scale;// 种群规模
    private int m;// 共有m个任务，即染色体的长度
    private int n;// 共有n个工人，即基因的值为（1~n）,m>n
    private int MAX_T;// 最大进化代数
    private GABean bestSolution;// 最优解

    private double[][] T1;// 任务i由维修工j完成的时间是T1[i][j],m*n
    private double[] T2;// 从公司到任务i所在地需要的时间是T2[i],1*m
    private double[][] T3;// 从任务i所在地到任务i'所在地需要的时间是T3[i][i'],m*m,不存在用0表示

    private GABean[] oldPopulation;// 初始种群，父代种群
    private GABean[] newPopulation;// 新的种群，子代种群

    // private double[] Pi;// 种群中各个个体的累计概率

    private double Pc;// 交叉概率
    private double Pm;// 变异概率
    private int t;// 当前进化代数
    private int count = 0;// 用于避免陷入局部最优的

    private Random random;
    private List<GABean> histrocialBest;//记录历史的全局最优解
    
    private TimeSettings timeSettings;
    
    public AlgRun(){}
    
    public AlgRun(TimeSettings timeSettings){
        this.timeSettings = timeSettings;
        this.T1 = timeSettings.getT1();
        this.T2 = timeSettings.getT2();
        this.T3 = timeSettings.getT3();
        
        this.scale = OptimalBean.gaSettings.getN();
        this.MAX_T = OptimalBean.gaSettings.getT();
        this.Pc = OptimalBean.gaSettings.getPc()-0.15;
        this.Pm = OptimalBean.gaSettings.getPm()-0.15;
        
        this.m = T1.length;
        this.n = T1[0].length;
    }
    
    /**
    * 初始化参数设置
    */
    private void init() {
        // 全局最优解的初始化
        bestSolution = new GABean(m);
        bestSolution.setFitness(Double.MAX_VALUE);
        
        t = 0;
        newPopulation = new GABean[scale];
        oldPopulation = new GABean[scale];
        histrocialBest = new ArrayList<GABean>();
        random = new Random();
    }

	// 初始化种群
    private void initGroup() {
        int gene, i, j;
        int[] ge;// 用于判别是否有未分配任务的工人
        int[] strategy;

        for (i = 0; i < scale;) {
            ge = new int[n + 1];
            strategy = new int[m];
            for (j = 0; j < m; j++) {
                    gene = random.nextInt(n) + 1;// 1~n
                    strategy[j] = gene;
                    ge[gene]++;
            }
            for (j = 1; j <= n; j++) {
                    if (ge[j] == 0) {
                            break;
                    }
            }
            if (j == (n + 1)) {
                    GABean solution = new GABean(m);
                    solution.setStrategy(strategy);
                    oldPopulation[i] = solution;
                    i++;
            }
        }
    }

    // 适应度计算
    private double evaluate(GABean s) {

        double[] workerTime = new double[n + 1];// 记录每个工人完成任务所需时间
        int[] strategy = s.getStrategy();
        double bestTime = 0.0;
        for (int i = 1; i <= n; i++) {// 对每个工人
            List<Integer> tasks = new ArrayList<Integer>();
            for (int j = 0; j < m; j++) {// 对每个任务
                    if (strategy[j] == i) {
                            tasks.add(j + 1);
                    }
            }
            Collections.shuffle(tasks);// 随机打乱顺序
            tasks.add(0, 0);
            // System.out.println("工人" + i + "的任务数：" + tasks);
            // 计算时间
            int taskNum, taskNum2;
            taskNum = tasks.get(1);
            workerTime[i] += T2[taskNum - 1];
            for (int j = 1; j < tasks.size() - 1; j++) {
                    taskNum = tasks.get(j);
                    taskNum2 = tasks.get(j + 1);
                    workerTime[i] += T1[taskNum - 1][i - 1];
                    workerTime[i] += T3[taskNum - 1][taskNum2 - 1];
            }
            taskNum = tasks.get(tasks.size() - 1);
            workerTime[i] += T1[taskNum - 1][i - 1];
            // System.out.println("工人" + i + "的时间：" + workerTime[i]);
        }

        bestTime = workerTime[1];
        for (int i = 2; i <= n; i++) {
                if (workerTime[i] > bestTime) {
                        bestTime = workerTime[i];
                }
        }
        s.setFitness(bestTime);
        // System.out.println("全局最优时间：" + bestTime);
        return bestTime;
    }

    /**
     * 计算种群各个个体的累积概率，前提是已经计算出各个个体的适应度，作为轮盘赌选择策略的一部分
     */
    private void countRate() {
        double sumFitness = 0;// 适应度总和
        double[] tempf = new double[scale];
        for (int k = 0; k < scale; k++) {
                tempf[k] = 10.0 / oldPopulation[k].getFitness();
                sumFitness += tempf[k];
        }

        oldPopulation[0].setPi(tempf[0] / sumFitness);
        for (int k = 1; k < scale; k++) {
                oldPopulation[k].setPi(tempf[k] / sumFitness
                                + oldPopulation[k - 1].getPi());
        }
    }

    /**
     * 简单判断两条染色体是否一致
     * 
     * @param s1
     * @param s2
     * @return
     */
    private boolean isEqualSolution(GABean s1, GABean s2) {

        if (s1.getFitness() != s2.getFitness()) {
                return false;
        }

        int[] strategy1 = s1.getStrategy();
        int[] strategy2 = s2.getStrategy();
        for (int i = 0; i < m; i++) {
                if (strategy1[i] != strategy2[i]) {
                        return false;
                }
        }

        return true;
    }

    /**
     * 挑选某代种群中适应度最该的个体，直接复制到子代 前提是已经计算出各个个体的适应度
     */
    private void selectBest() {

        int maxid;
        double maxevaluation;

        maxid = 0;
        maxevaluation = oldPopulation[0].getFitness();
        for (int k = 1; k < scale; k++) {
            if (maxevaluation > oldPopulation[k].getFitness()) {
                    maxevaluation = oldPopulation[k].getFitness();
                    maxid = k;
            }
        }

        if (isEqualSolution(bestSolution, oldPopulation[maxid])) {
                count++;// 当前代的最优染色体和全局最优相等，则count++;
        }

        if (count < 50) {
            if (bestSolution.getFitness() > maxevaluation) {
                    count = 0;
                    bestSolution.setFitness(maxevaluation);
                    bestSolution.setCur_t(t);// 最好的染色体出现的代数
                    int[] bestStrategy = bestSolution.getStrategy();
                    int[] oldStrategy = oldPopulation[maxid].getStrategy();
                    for (int i = 0; i < m; i++) {
                            bestStrategy[i] = oldStrategy[i];
                    }
                    bestSolution.setStrategy(bestStrategy);
            }
            // 将当代种群中适应度最高的染色体maxid复制到新种群中，排在第一位0
            //copySolution(0, maxid);
        } else {
            histrocialBest.add((GABean)bestSolution.clone());//保存历史全局最优
            count = 0;
            System.out.println("历史全局最优：" + bestSolution);

            // 全局最优分配方案，随机打乱
            int[] strategy = bestSolution.getStrategy();
            List<Integer> strList = new ArrayList<Integer>();
            for (int i = 0; i < m; i++) {
                    strList.add(strategy[i]);
            }
            Collections.shuffle(strList);
            for (int i = 0; i < m; i++) {
                    strategy[i] = strList.get(i);
            }

            System.out.println("新的全局最优：" + bestSolution);      
        }
        // 将当代种群中适应度最高的染色体maxid复制到新种群中，排在第一位0
        copySolution(0, maxid);

    }

    /**
     * 复制染色体
     * 
     * @param k1
     *            表示新染色体在种群中的位置
     * @param k2
     *            表示旧染色体在种群中的位置
     */
    private void copySolution(int k1, int k2) {

        newPopulation[k1] = (GABean) (oldPopulation[k2]).clone();

    }

    /**
     * 轮盘赌选择策略挑选
     */
    private void select() {
        int k, i, selectId;
        double ran1;

        for (k = 1; k < scale; k++) {// 因为最优的直接选择了，所以再选scale-1个就行
            ran1 = (random.nextInt(65535) % 1000 / 1000.0);// 三位小数
            // 产生方式
            for (i = 0; i < scale; i++) {// 判断选择种群中哪一个
                    if (ran1 <= oldPopulation[i].getPi()) {
                            break;
                    }
            }
            selectId = i;
            copySolution(k, selectId);
        }
    }

    /**
     * 交叉算子
     * 
     * @param k1
     *            选择后的子代种群中第k1条染色体
     * @param k2
     *            选择后的子代种群中第k2条染色体
     */
    private void cross(int k1, int k2) {

        GABean s1 = newPopulation[k1];// 待交叉的染色体k1
        GABean s2 = newPopulation[k2];// 待交叉的染色体k2
        int[] bStrategy1 = s1.getStrategy();// 待交叉的染色体k1的分配方案
        int[] bStrategy2 = s2.getStrategy();// 待交叉的染色体k2的分配方案

        int[] tempOrder1 = new int[m];// 从k1中取出的序列,索引作为位置标记
        int[] tempOrder2 = new int[m];// 从k2中取出的序列,索引作为位置标记
        boolean[] isWorkerChoosen1 = new boolean[n + 1];
        boolean[] isWorkerChoosen2 = new boolean[n + 1];

        for (int i = 0; i < m; i++) {

            if (isWorkerChoosen1[bStrategy1[i]] == false) {
                    tempOrder1[i] = bStrategy1[i];
                    isWorkerChoosen1[bStrategy1[i]] = true;
            }

            if (isWorkerChoosen2[bStrategy2[i]] == false) {
                    tempOrder2[i] = bStrategy2[i];
                    isWorkerChoosen2[bStrategy2[i]] = true;
            }
        }

        int pos1 = 0, pos2 = 0;
        for (int i = 0; i < m; i++) {
            if (tempOrder1[i] != 0) {// 第i位可放置
                    for (int j = pos1; j < m; j++) {
                            if (tempOrder2[j] != 0) {
                                    bStrategy1[i] = tempOrder2[j];
                                    pos1 = j + 1;
                                    break;
                            }
                    }
            }

            if (tempOrder2[i] != 0) {// 第i位可放置
                    for (int j = pos2; j < m; j++) {
                            if (tempOrder1[j] != 0) {
                                    bStrategy2[i] = tempOrder1[j];
                                    pos2 = j + 1;
                                    break;
                            }
                    }
            }
        }

        // 交叉完毕，放回种群
        newPopulation[k1].setStrategy(bStrategy2);
        newPopulation[k2].setStrategy(bStrategy1);

    }

	/**
	 * 多次对换变异算子
	 * 
	 * @param k
	 *            选择，交叉后，新种群中第k条染色体
	 */
	private void variation(int k) {
		/*
		 * int ran1;
		 * 
		 * ran1 = random.nextInt(m);// 变异位
		 * 
		 * Solution solution = newPopulation[k]; int[] strategy =
		 * solution.getStrategy(); strategy[ran1] = random.nextInt(n) + 1;
		 */
		int ran1, ran2, temp;
		int count;// 对换次数

		count = random.nextInt(m);
		GABean solution = newPopulation[k];
		for (int i = 0; i < count; i++) {
			ran1 = random.nextInt(m);
			ran2 = random.nextInt(m);
			while (ran1 == ran2) {
				ran2 = random.nextInt(m);
			}
			int[] strategy = solution.getStrategy();
			temp = strategy[ran1];
			strategy[ran1] = strategy[ran2];
			strategy[ran2] = temp;
		}
	}

	
	/**
	 * 进化函数，保留最好染色体不进行交叉变异
	 */
	private void evolution() {
		int k;
		// 挑选某代种群中适应度最高的个体
		selectBest();
		// 轮盘赌选择策略挑选scale-1个下一代个体
		select();

		double r;
		int count = scale;
		if (count % 2 != 0) {
			count++;
		}
		for (k = 1; k < count - 1; k += 2) {
			r = random.nextDouble();// 产生概率
			if (r < Pc) {
				// singlePointCross(k, k+1);
				// twoPointCross(k, k+1);
				cross(k, k + 1);
			} else {
				r = random.nextDouble();// 产生概率
				if (r < Pm) {
					variation(k);
				}
				r = random.nextDouble();// 产生概率
				if (r < Pm) {
					variation(k + 1);
				}
			}
		}
		if (scale % 2 == 0) {// 剩最后一个染色体没有交叉
			r = random.nextDouble();// 产生概率
			if (r < Pm) {
				variation(scale - 1);
			}
		}
	}

	public String solve() {
		int k, i;
		init();// 参数初始化设置
		initGroup();// 种群初始化
		// 计算初始化种群适应度
		for (k = 0; k < scale; k++) {
			evaluate(oldPopulation[k]);
		}
		// 计算初始化种群中各个个体的累积概率
		countRate();

		

		// 进化过程
		for (t = 0; t < MAX_T; t++) {
			// evolution();
			evolution();
			// 将新种群newPopulation复制到旧种群中，准备下一代进化
			for (k = 0; k < scale; k++) {
				oldPopulation[k] = (GABean) newPopulation[k].clone();
			}
			// 计算种群适应度
			for (k = 0; k < scale; k++) {
				evaluate(oldPopulation[k]);
			}
			// 计算种群中各个个体的累积概率
			countRate();
			// 为了避免陷入局部最优，当全局最优连续100代没有改变时

		}
		
		for(i=0;i<histrocialBest.size();i++){
			System.out.println("历史全局最优的解有："+histrocialBest.get(i));
		}
		for(i=0;i<histrocialBest.size();i++){
			if(bestSolution.getFitness()>histrocialBest.get(i).getFitness()){
				bestSolution = (GABean) histrocialBest.get(i).clone();
			}
		}
                String result = "";
		result += "最佳长度出现代数：\n";
		result += bestSolution.getCur_t()+"\n";
		result += "最短时间：\n";
		result += bestSolution.getFitness()+"\n";
		result += "最佳分配方案：\n";
		int[] a = bestSolution.getStrategy();
		for (i = 0; i < m; i++) {
                    result += a[i];
                    if(i<m-1)
                        result += " ";
                    else
                        result += "\n";
		}
                System.out.println(result);
                return result;
	}
}
