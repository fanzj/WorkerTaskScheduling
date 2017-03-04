package algs.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件类，读取文本文件
 */
public class FileUtils {

	/**
	 * 读文本文件
	 * 
	 * @param m
	 *            行
	 * @param n
	 *            列
	 * @param fileName
	 * @return 返回二维数组
	 * @throws IOException
	 */
	public static double[][] read2D(String fileName,int m, int n)
			throws IOException {
		double[][] T = new double[m][n];
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		for (int i = 0; i < m; i++) {
			strbuff = data.readLine();// 读一行数据
			String[] strcol = strbuff.split(" ");// 字符分割
			for (int j = 0; j < strcol.length; j++) {
				T[i][j] = Double.valueOf(strcol[j]);
			}
		}
		return T;
	}

	/**
	 * 读文本文件
	 * 
	 * @param m
	 *            列
	 * @param fileName
	 * @return 返回一维数组
	 * @throws IOException
	 */
	public static double[] read1D(String fileName,int m) throws IOException {
		double[] T = new double[m];
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		strbuff = data.readLine();// 读一行数据
		String[] strcol = strbuff.split(" ");// 字符分割
		for (int i = 0; i < m; i++) {
			T[i] = Double.valueOf(strcol[i]);
		}
		return T;
	}
        

	public static void main(String[] args) throws IOException {
            double[][] T1 = read2D("data//data_01//T1.txt",5,3);
            for(int i=0;i<5;i++){
                for(int j=0;j<3;j++){
                    System.out.print(T1[i][j]+" ");
                }
                System.out.println();
            }
	}

}
