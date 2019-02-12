package com.ups.util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.accusoft.barcodexpress.BarcodeReader;
import com.accusoft.barcodexpress.BarcodeType;
import com.accusoft.barcodexpress.Result;

public class imageBarcodeReader {
	public static final String strRootDir = System.getProperty("user.dir");
	public static String result;
	public static Result[] results = null;

	public static void pdfconverter() throws IOException {

		File sourceFile = new File(
				"C:\\Users\\u65557\\Desktop\\BarcodeXpressJava12.2-64\\Accusoft\\BarcodeXpressJava12-64\\samples\\ReadBarcodes\\res\\Rajeev return label.pdf");
		File destinationDir = new File(
				"C:\\Users\\u65557\\Desktop\\BarcodeXpressJava12.2-64\\Accusoft\\BarcodeXpressJava12-64\\samples\\images\\");

		// File file = new File("C:\\Users\\U51582\\Desktop\\Rajeev return label.pdf");
		PDDocument document = PDDocument.load(sourceFile);

		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);

		PDFTextStripper tStripper = new PDFTextStripper();
		String pdfFileInText = tStripper.getText(document);
		// System.out.println("Text:" + st);

		// split by whitespace
		String lines[] = pdfFileInText.split("\\r?\\n");
		for (String line : lines) {
			System.out.println(line);
		}

		List<PDPage> list = document.getDocumentCatalog().getAllPages();
		System.out.println("Total files to be converted -> " + list.size());

		String fileName = sourceFile.getName().replace(".pdf", "");
		int pageNumber = 1;
		for (PDPage page : list) {
			BufferedImage image = page.convertToImage();
			File outputfile = new File(destinationDir + "\\" + "Barcode" + "-" + pageNumber + ".png");
			System.out.println("Image Created -> " + outputfile.getName());
			ImageIO.write(image, "png", outputfile);
			pageNumber++;
		}
		document.close();

	}

	public static void readerAnalyze(BarcodeReader reader, String inputFileName) {
		String inputFilePath = strRootDir + "\\testData\\" + inputFileName;
		System.out.println("Analyzing file " + inputFilePath);
		System.out.println();

		try {
			File inputFile = new File(inputFilePath);
			BufferedImage bufferedImage = ImageIO.read(inputFile);

			results = reader.analyze(bufferedImage);
			System.out.println(results.length + " barcodes found");

			for (int i = 0; i < results.length; i++) {
				System.out.println("#" + (i + 1));
				System.out.println("Value: " + results[i].getValue());
				System.out.println("Type:  " + results[i].getType());
				System.out.println("Area: x = " + results[i].getArea().x + " y = " + results[i].getArea().y
						+ " width = " + results[i].getArea().width + " height = " + results[i].getArea().height);
				Point[] points = results[i].getPoints();
				System.out.format(
						"Corners: Top Left(%d, %d), Top Right(%d, %d),"
								+ " Bottom Right(%d, %d), Bottom Left(%d, %d)\n",
						points[0].x, points[0].y, points[1].x, points[1].y, points[2].x, points[2].y, points[3].x,
						points[3].y);
				System.out.println("Confidence: " + results[i].getConfidence());
				System.out.println("Skew: " + results[i].getSkew());
				System.out.println();
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static String[] parseCommandLineArgs(String[] args, BarcodeReader reader) {
		// Default value
		reader.setBarcodeTypes(new BarcodeType[] { BarcodeType.ALL1D });

		int numOptions;
		for (numOptions = 0; numOptions < args.length; ++numOptions) {
			String arg0 = args[numOptions].toLowerCase();

			// No more options
			if (arg0.charAt(0) != '-')
				break;

			if (arg0.equals("-h") || arg0.equals("--help"))
				printUsage();

			if (arg0.equals("-t") || arg0.equals("--type")) {
				if (numOptions + 1 == args.length)
					printUsage();

				numOptions++;
				String arg1 = args[numOptions].toLowerCase();
				if (arg1.equals("1d")) {
					reader.setBarcodeTypes(new BarcodeType[] { BarcodeType.ALL1D });
				} else if (arg1.equals("2d")) {
					reader.setBarcodeTypes(new BarcodeType[] { BarcodeType.ALL2D });
				} else if (arg1.equals("all")) {
					reader.setBarcodeTypes(new BarcodeType[] { BarcodeType.ALL });
				} else {
					printUsage();
				}
			}
		}

		// No files in command line
		if (numOptions == args.length)
			printUsage();

		String[] fileNames = new String[args.length - numOptions];
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = args[numOptions + i];
		}

		return fileNames;
	}

	public static void printUsage() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("    ReadBarcodes [options] <image1> [<image2> ... <imageN>]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("    -t, --type <type>       (optional = true, default = '1d') Barcodes types to scan");
		System.out.println("                            <type>: '1d' - all 1D | '2d' - all 2D | 'all' - all 1D + 2D");
		System.out.println("    -h, --help              (optional = true) Print this help message");
		System.out.println();
		System.out.println("Examples:");
		System.out.println(
				"    ReadBarcodes ../images/Barcode-All-Supported-Types.bmp ../images/Barcode-Multiple-Common.bmp");
		System.out.println("    ReadBarcodes -t 2d ../images/Barcode-All-Supported-Types.bmp");
		System.out.println("    ReadBarcodes --type all ../images/Barcode-*");
		System.exit(-1);
	}

	public static void validateBarcodeDataFromExcel() throws Exception {
		// Validating data passed in the excel sheet with the data retrieved from the
		// image barcode reader
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
		Sheet sh = excelworkbook.getSheet("barcodeData");
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
				String cellType = currentcell.getCellType().toString();
				if (cellType.equals("NUMERIC")) {

					double val = currentcell.getNumericCellValue();
					String strVal = String.valueOf(val);
					array[rownumber][colnumber] = strVal;
					// System.out.println(array[rownumber][colnumber].toString());
				} else {
					array[rownumber][colnumber] = currentcell.getStringCellValue();
				}
				colnumber++;
			}
			rownumber++;
		}
		excelworkbook.close();
		f1.close();
		System.out.println("----------------------------------------------");
		for (int j = 0; j < rownumber; j++) {
			for (int i = 0; i < noofcolumns; i++) {
				String valBarcode = results[i].getValue();
				if (valBarcode.contains(array[j][i].toString())) {
					System.out.println(
							"Validated BarCode data with extracted from image file : " + results[i].getValue());
				} else {
					setFlagPass = true;
					valueFail = array[j][i].toString();
					break;
				}
			}
			System.out.println("----------------------------------------------");
		}
		if (setFlagPass == true) {
			System.out.println("There is a missmatch in the label barcode data : " + valueFail);
		}
	}

}
