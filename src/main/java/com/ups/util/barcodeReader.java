package com.ups.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.accusoft.barcodexpress.BarcodeReader;
import com.accusoft.barcodexpress.BarcodeXpress;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class barcodeReader {
	public static final String strRootDir = System.getProperty("user.dir");
	public static String result;

	public static void main(String[] args) throws Exception {

		// 1st Scenario- Validating data from Image with the data in the excel
		//String dataFromImage = readImageDataOCR();
		//validateDataFromExcel(dataFromImage);

		// 2nd Scenario- Validating Barcode values in the image files
		BarcodeXpress barcodeXpress = new BarcodeXpress();
		BarcodeReader reader = barcodeXpress.getReader();
		imageBarcodeReader.readerAnalyze(reader, "Barcode-2.png");
		imageBarcodeReader.validateBarcodeDataFromExcel();

		// 3rd Scenario- Comparing two image label files
		File fileA = new File(strRootDir + "\\testData\\Label-1.png");
		File fileB = new File(strRootDir + "\\testData\\Label-2.png");
		compareImage(fileA, fileB);
	
		// 4nd Scenario- Comparing two image label files and highlighting changes changes in the labels
		ImageIO.write(
				getDifferenceImage(ImageIO.read(new File(strRootDir + "\\testData\\CompareA.png")),
						ImageIO.read(new File(strRootDir + "\\testData\\CompareB.png"))),
				"png", new File(strRootDir +"\\testData\\output.png"));
		System.out.println("--------------Successfully compared image files--------------");
		System.out.println("Please find the output in the below location : "+ strRootDir +"\\testData\\output.png");
	}

	public static String readImageDataOCR() {
		try {
			ITesseract instance = new Tesseract();

			// In case you don't have your own tessdata, let it also be extracted for you
			File tessDataFolder = LoadLibs.extractTessResources("tessdata");

			// Set the tessdata path
			instance.setDatapath(tessDataFolder.getAbsolutePath());

			File imageFile = new File(strRootDir + "\\testData\\Data-1.png");

			result = instance.doOCR(imageFile);
			System.out.println("----------------Data in Image file is as below------------------------------");
			System.out.println(result);
			System.out.println("-------------------------------End------------------------------------------");

		} catch (TesseractException e) {
			System.err.println(e.getMessage());
			// return "Error while reading image";
		}
		return result;
	}

	public static void validateDataFromExcel(String dataFromImage) throws Exception {
		// Validating data passed in the excel sheet with the data retrieved from the
		// Image OCR
		// Variable declarations
		boolean setFlagPass = false;
		String valueFail = null;
		int noofcolumns = 0;
		int rownumber = 0;
		boolean isfirstrow = false;

		// Getting the excel complete path
		Workbook excelworkbook = null;
		String path = strRootDir + "\\" + "src\\test\\resources\\dataexcel\\data.xlsx";
		// Declaring the file stream for data read
		FileInputStream f1 = new FileInputStream(path);

		excelworkbook = new XSSFWorkbook(f1);

		// Getting the total number of rows and columns in the excel
		Sheet sh = excelworkbook.getSheet("data");
		int noofrows = sh.getPhysicalNumberOfRows();
		for (int j = 0; j < noofrows; j++) {
			noofcolumns = sh.getRow(j).getPhysicalNumberOfCells();
			// When there are blank cells to handle. Avoid unless absolutely neccessary
			/*
			 * int tempcolumncount=0; Iterator<Cell> c = sh.getRow(j).cellIterator();
			 * while(c.hasNext()) { Cell cc=c.next();
			 * if(cc.getCellType()==Cell.CELL_TYPE_BLANK) { tempcolumncount++; } else {
			 * tempcolumncount++;
			 * 
			 * } if (tempcolumncount>noofcolumns) noofcolumns=tempcolumncount; }
			 */
		}
		// Declaring the size of the array to read data
		noofrows = noofrows - 1;
		Object[][] array = new Object[noofrows][noofcolumns];
		// Traversing through the rows
		Iterator<Row> row = sh.rowIterator();

		while (row.hasNext()) {
			Row currentrow = row.next();
			if (isfirstrow == false) {
				isfirstrow = true;
				continue;
			}
			// Traversing through the columns
			int colnumber = 0;
			Iterator<Cell> cell = currentrow.cellIterator();
			while (cell.hasNext()) {
				Cell currentcell = cell.next();
				array[rownumber][colnumber] = currentcell.getStringCellValue();
				// System.out.println(array[rownumber][colnumber].toString());
				colnumber++;
			}
			rownumber++;
		}
		excelworkbook.close();
		f1.close();
		System.out.println("----------------------------------------------");
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < noofcolumns; i++) {
				if (result.contains(array[j][i].toString())) {
					System.out
							.println("Validated data with extracted data from image file : " + array[j][i].toString());
				} else {
					setFlagPass = true;
					valueFail = array[j][i].toString();
					break;
				}
			}
			System.out.println("----------------------------------------------");
		}
		if (setFlagPass == true) {
			System.out.println("There is a missmatch in the label image data : " + valueFail);
		}
	}

	public static boolean compareImage(File fileA, File fileB) {
		try {
			// take buffer data from botm image files //
			BufferedImage biA = ImageIO.read(fileA);
			DataBuffer dbA = biA.getData().getDataBuffer();
			int sizeA = dbA.getSize();
			BufferedImage biB = ImageIO.read(fileB);
			DataBuffer dbB = biB.getData().getDataBuffer();
			int sizeB = dbB.getSize();
			// compare data-buffer objects //
			if (sizeA == sizeB) {
				for (int i = 0; i < sizeA; i++) {
					if (dbA.getElem(i) != dbB.getElem(i)) {
						return false;
					}
				}
				System.out.println("--------------Successfully compared image files--------------");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("-----------Failed to compare image files-------------");
			return false;
		}
	}

	public static BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
		BufferedImage out = null;
		try {
			// convert images to pixel arrays...
			final int w = img1.getWidth(), h = img1.getHeight(), highlight = Color.RED.getRGB();
			final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
			final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
			// compare img1 to img2, pixel by pixel. If different, highlight img1's pixel...
			for (int i = 0; i < p1.length; i++) {
				if (p1[i] != p2[i]) {
					p1[i] = highlight;
				}
			}
			// save img1's pixels to a new BufferedImage, and return it...
			// (May require TYPE_INT_ARGB)
			out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			out.setRGB(0, 0, w, h, p1, 0, w);
		} catch (Exception e) {
			System.out.println("-----------Failed to compare image files-------------");
		}
		return out;
	}
}
