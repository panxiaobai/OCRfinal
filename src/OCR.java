

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.ml.ANN_MLP;

public class OCR {

	ANN_MLP ann;
	public List<Mat> imgs;
	public List<Mat> lines;
	public List<String> resStr=new ArrayList<String>();
	public Map<Integer,Character> transMap=new HashMap<Integer,Character>();

	
	public int lineCutThreshold=3;
	public int wordCutThreshold=0;
	
	public List<Mat> getLinesFromImg(Mat img){
		List<Mat> resLines=new ArrayList<Mat>();
		int startIndex=-1;
		for(int i=0;i<img.height();i++){
			int sum=0;
			for(int j=0;j<img.width();j++){
				double[] a=img.get(i, j);
				if(a[0]>100){
					sum++;
				}
			}

			if((startIndex==-1)&&(sum>lineCutThreshold)){

				startIndex=i;
			}else if((startIndex!=-1)&&(sum<=lineCutThreshold)){
				int endIndex=i;
				Rect rect = new Rect(0, startIndex, img.width(), endIndex-startIndex);
				Mat res=new Mat(img, rect);
				resLines.add(res);
				startIndex=-1;
			}
		}
		return resLines;
	}
	
	
	public List<Mat> getWordImgFromLine(Mat img){
		List<Mat> resWords=new ArrayList<Mat>();
		int startIndex=-1;
		for(int j=0;j<img.width();j++){
			int sum=0;
			for(int i=0;i<img.height();i++){
				double[] a=img.get(i, j);
				if(a[0]>100){
					sum++;
				}
			}
			if(startIndex==-1&&sum>wordCutThreshold){
				startIndex=j;
			}else if(startIndex!=-1&&sum<=wordCutThreshold){
				int endIndex=j;
				Rect rect = new Rect(startIndex,0,endIndex-startIndex,img.height());
				Mat res=new Mat(img, rect);
				resWords.add(res);
				startIndex=-1;
			}
		}
		return resWords;
	}
	
	public OCR(List<Mat> imgs){
		this.imgs=imgs;
		prepare();
	}
	public void prepare(){
		ann = ANN_MLP.create();  
		ann=ann.load("bp2.xml");//读取模型  
		
		for(int i=0;i<10;i++){
			transMap.put(i,(char)( i+'0'));
		}
		for(int i=10;i<36;i++){
			transMap.put(i,(char)(i-10+'A'));
		}
		for(int i=36;i<52;i++){
			transMap.put(i, (char)(i-36+'a'));
		}
	}
	
	public String recognition(Mat lineFuture){
		String res="";
		Mat responseMat = new Mat();  
        ann.predict(lineFuture, responseMat, 0);  
        System.out.println("Ann responseMat:\n" + responseMat.dump());  
        
        for(int i=0;i<responseMat.height();i++){
        	float max=Float.MIN_VALUE;
            int maxIndex=0;
        	for(int j=0;j<responseMat.width();j++){
        		if(responseMat.get(i, j)[0]>max){
        			max=(float)responseMat.get(i, j)[0];
        			maxIndex=j;
        		}
        	}
        	System.out.println(max+"---"+maxIndex);
        	res+=transMap.get(maxIndex);
        }
        return res;
	}
	public List<String> run(){
		
		for(int i=0;i<imgs.size();i++){
			Mat img=imgs.get(i);
			List<Mat> lines=getLinesFromImg(img);
			for(int j=0;j<lines.size();j++){
				Mat line=lines.get(j);
				ImageViewer imageViewer = new ImageViewer(line, "行"+j);
				imageViewer.imshow(); 
				List<Mat> wordImgs=getWordImgFromLine(line);
				Mat lineFuture=new Mat(wordImgs.size(), 8, CvType.CV_32FC1);  
				for(int v=0;v<wordImgs.size();v++){
					Mat wordImg=wordImgs.get(v);
					ImageViewer imageViewer2 = new ImageViewer(wordImg, "行"+j+"字"+v);
					imageViewer2.imshow(); 
					WordFuture wf=new WordFuture();
					wf.mat=wordImg;
					wf.getFuture();
					lineFuture.put(v, 0, wf.future);
					for(int m=0;m<wf.future.length;m++){
						System.out.print(wf.future[m]+"\t");
					}
					System.out.println();
				}
				resStr.add(recognition(lineFuture));
			}
		}
		return resStr;
	}
	
	
	
}
