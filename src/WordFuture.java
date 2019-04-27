

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class WordFuture {
	public char word;
	public float[] future=new float[8];
	public Mat mat;
	public int width=64;
	public int height=128;
	
	public void getFuture(){
		
		Imgproc.resize(mat, mat, new Size(width,height));
		for(int i=0;i<height/2;i++){
			for(int j=0;j<width/2;j++){
				double[] a=mat.get(i, j);
				if(a[0]>10){
					future[0]++;
				}
			}
		}
		for(int i=0;i<height/2;i++){
			for(int j=width/2;j<width;j++){
				double[] a=mat.get(i, j);
				if(a[0]>10){
					future[1]++;
				}
			}
		}
		for(int i=height/2;i<height;i++){
			for(int j=0;j<width/2;j++){
				double[] a=mat.get(i, j);
				if(a[0]>10){
					future[2]++;
				}
			}
		}
		for(int i=height/2;i<height;i++){
			for(int j=width/2;j<width;j++){
				double[] a=mat.get(i, j);
				if(a[0]>10){
					future[3]++;
				}
			}
		}
		future[4]=0;//������
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				double[] a=mat.get(i, j);
				if(a[0]<10){
					future[4]++;
				}else{
					break;
				}
			}
		}
		future[5]=0;//���ҵ���
		for(int i=0;i<height;i++){
			for(int j=width-1;j>-1;j--){
				double[] a=mat.get(i, j);
				if(a[0]<10){
					future[5]++;
				}else{
					break;
				}
			}
		}
		future[6]=0;//���ϵ���
		for(int j=0;j<width;j++){
			for(int i=0;i<height;i++){
				double[] a=mat.get(i, j);
				if(a[0]<10){
					future[6]++;
				}else{
					break;
				}
			}
		}
		future[7]=0;//���µ���
		for(int j=0;j<width;j++){
			for(int i=height-1;i>0;i--){
				double[] a=mat.get(i, j);
				if(a[0]<10){
					future[7]++;
				}else{
					break;
				}
			}
		}
	}
}
