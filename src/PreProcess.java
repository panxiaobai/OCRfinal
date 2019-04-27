import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class PreProcess {


	
	public static List<Mat> process(Mat img){
		List<Mat> resWordArea=new ArrayList<Mat>();
		//ת�Ҷ�
		Mat matGray=new Mat();
		Imgproc.cvtColor(img, matGray, Imgproc.COLOR_RGB2GRAY);
		
		//�Ҷ�ֱ��ͼͳ��
		double[] dis=new double[256];
		for(int i=0;i<matGray.height();i++){
			for(int j=0;j<matGray.width();j++){
				double[] a=matGray.get(i, j);
				dis[((int)a[0])]++;
			}

		}
		//��ֱ��ͼ���к��ܶȹ���
		double[] p=kernelDensityEstimation(dis,matGray.height()*matGray.width());
		//�õ���С���ֵ
		List<Integer> maxList=new ArrayList<Integer>();
		List<Integer> minList=new ArrayList<Integer>();
		TreeSet<Integer> minSet=new TreeSet<Integer>();
		for(int i=0;i<dis.length;i++){
			System.out.print(p[i]+",");
		}
		for(int i=2;i<dis.length-2;i++){
			//System.out.println(i+"@@"+dis[i]+"@@"+p[i]);
			if(p[i]<p[i-1]&&p[i]<p[i+1]&&p[i-1]<p[i-2]&&p[i+2]>p[i+1]){
				minList.add(i);
				minSet.add(i);
			}
			if(p[i]>p[i-1]&&p[i]>p[i+1]&&p[i-1]>p[i-2]&&p[i+1]>p[i+2]){
				maxList.add(i);
			}
		}
		System.out.print("\nmaxList:");
		for(int i=0;i<maxList.size();i++){
			System.out.print(maxList.get(i)+"---");
		}
		System.out.print("\nminList:");
		for(int i=0;i<minList.size();i++){
			System.out.print(minList.get(i)+"---");
		}
		maxList.add(0);
		maxList.add(255);
		
		Mat res=new Mat(matGray.size(),CvType.CV_8UC1,new Scalar(0, 0, 255));
		//��ͼ����зֲ�
		for(int i=0;i<maxList.size();i++){

			Mat dd=new Mat();
			int start=0;
			if(minSet.floor(maxList.get(i))!=null){
				start=minSet.floor(maxList.get(i));
			}
			int end=255;
			if(minSet.ceiling(maxList.get(i))!=null){
				end=minSet.ceiling(maxList.get(i));
			}
			//�Ҷȷֲ�
			if(start==0){
				Imgproc.threshold(matGray, dd, end,  255, Imgproc.THRESH_BINARY_INV);
			}else{
				Imgproc.threshold(matGray, dd, end,  0, Imgproc.THRESH_TOZERO_INV);
				Imgproc.threshold(dd, dd, start, 255, Imgproc.THRESH_BINARY);
			}
			
			ImageViewer imageViewer1 = new ImageViewer(dd,start+"-"+end);
			imageViewer1.imshow(); 
			
			Mat out=new Mat();
			dd.copyTo(out);
			/*
			Mat sobel=new Mat();
			Imgproc.Sobel(out, out, -1, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);
			ImageViewer imageViewer7 = new ImageViewer(out,"��Եx");
			imageViewer7.imshow(); 
			*/
			Mat element=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(30,10));
			//���
			Imgproc.morphologyEx(out, out, Imgproc.MORPH_CLOSE, element);
			ImageViewer imageViewer3 = new ImageViewer(out,"����");
			imageViewer3.imshow();   
			
			
			//��ȡ�����ж�
			List<MatOfPoint> lists=new ArrayList<MatOfPoint>();
			Mat hierarchy=new Mat();
			Imgproc.findContours(out, lists, hierarchy,  Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			//Mat lunkuotu=new Mat(out.size(),CvType.CV_8UC1,new Scalar(0, 0, 255));
			for(int j=0;j<lists.size();j++){
				//Imgproc.drawContours(lunkuotu, lists, j, new Scalar(255,255,255));
				//�õ����������������
				double contourArea = Imgproc.contourArea(lists.get(j));
				//�������ƶ��ж�
				Rect rect=Imgproc.boundingRect(lists.get(j));
				double rectArea=rect.area();
				double rectlike=contourArea/rectArea;
				System.out.println("�������ƶ�"+rectlike);
				if(contourArea>550&&rectlike>0.59){
				//if(contourArea>650){
					Mat test=new Mat();					
					//Imgproc.drawContours(dd, lists, j, new Scalar(255,0,0));
					//ImageViewer imageViewer4 = new ImageViewer(dd,i+"����"+j);
					//imageViewer4.imshow();   
					
					Mat hole=new Mat(out.size(),CvType.CV_8UC1,new Scalar(0, 0, 255));
					Imgproc.drawContours(hole, lists, j, new Scalar(255,0,0),-1);
					//ImageViewer imageViewer10 = new ImageViewer(hole,i+"����"+j);
					//imageViewer10.imshow();   
					
					Mat crop=new Mat(out.size(),CvType.CV_8UC1,new Scalar(0, 0, 255));
					dd.copyTo(crop,hole);
					ImageViewer imageViewer10 = new ImageViewer(crop,i+"����"+j);
					imageViewer10.imshow();   
					//����ʴ���ж�
					int preNum=getWhiteNum(crop);
					Mat ele = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 2));
					Mat afterErode=new Mat();
					Imgproc.erode(crop, afterErode, ele);
					int nowNum=getWhiteNum(afterErode);
					
					double fushilv=nowNum/(double)preNum;
					System.out.println(i+"����"+j+"��ʴ��"+preNum+"--"+nowNum+"--"+fushilv);
					if(fushilv>=0.58&&fushilv<=0.92){
						ImageViewer imageViewer12 = new ImageViewer(crop,i+"����"+j+"��ʴ���ж���");
						imageViewer12.imshow();
						Core.addWeighted(res, 0.5, crop, 0.5, 0, res);
						Imgproc.threshold(res, res, 0, 255, Imgproc.THRESH_BINARY);
						
						//��õ�����������
						MatOfPoint2f newPoint = new MatOfPoint2f(lists.get(j).toArray());
						RotatedRect rrect =Imgproc.minAreaRect(newPoint);
						Rect r=new Rect();
						r.height=rrect.boundingRect().width;
						r.width=rrect.boundingRect().height;
						r.x=rrect.boundingRect().y;
						r.y=rrect.boundingRect().x;
						Mat resMat=new Mat(crop,rect);
						ImageViewer imageViewer13 = new ImageViewer(resMat,"����");
						imageViewer13.imshow();
						resWordArea.add(resMat);
					}
				}
				

			}
			//ImageViewer imageViewerres = new ImageViewer(lunkuotu,"����ͼ");
			//imageViewerres.imshow();
			//System.out.println();
		}
		ImageViewer imageViewerres = new ImageViewer(res,"���");
		imageViewerres.imshow();
		return resWordArea;
	}
	
	//���ܶȹ���
	public static double[] kernelDensityEstimation(double[] dis,int n){
		double[] p=new double[dis.length];
		double h=0.1;
		double nh=h*n;
		for(int x=0;x<dis.length;x++){
			double sum=0;
			for(int i=0;i<dis.length;i++){
				sum+=dis[i]*funK((x-i)/h);
			}
			p[x]=sum/nh;
		}
		return p;
		
	}
	
	//sigmod
	public static double funK(double x){
		return Math.pow(Math.E, -(x*x)/2)/Math.sqrt(2*Math.PI);
	}
	
	//��ɫ���ص�ͳ��
	public static int getWhiteNum(Mat mat){
		int sum=0;
		for(int i=0;i<mat.height();i++){
			for(int j=0;j<mat.width();j++){
				double[] a=mat.get(i, j);
				if(a[0]>200){
					sum++;
				}
			}
		}
		return sum;
	}
}
