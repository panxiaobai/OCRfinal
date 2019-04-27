import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class Main {

	public static void main(String[] args){
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );  
		Mat img=Imgcodecs.imread("2.png");
		List<Mat> wordAreas=PreProcess.process(img);
		for(int i=0;i<wordAreas.size();i++){
			ImageViewer imageViewerres = new ImageViewer(wordAreas.get(i),"ÎÄ×ÖÇøÓò"+i);
			imageViewerres.imshow();
		}
		
		OCR ocr=new OCR(wordAreas);
		List<String> resStrs=ocr.run();
		for(int i=0;i<resStrs.size();i++){
			System.out.println(resStrs.get(i));
		}
		
	}
}
