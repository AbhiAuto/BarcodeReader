package com.ups.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import com.accusoft.barcodexpress.BarcodeReader;
import com.accusoft.barcodexpress.BarcodeXpress;
import com.ups.util.barcodeReader;
import com.ups.util.imageBarcodeReader;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class stepDef {

	public static final String strRootDir = System.getProperty("user.dir");
	public static String dataFromImage;
	private Scenario scenario;

	@Before("@output")
	public void setUp(Scenario scenario) {
		this.scenario = scenario;
	}

	@Given("^I have UPS label image to validate data$")
	public void i_have_UPS_label_image_to_validate_data() throws Throwable {
		System.out.println("---------Extracting data from label Image file----------");
	}

	@When("^I extract data from the Label image$")
	public void i_extract_data_from_the_Label_image() throws Throwable {
		dataFromImage = barcodeReader.readImageDataOCR();
	}

	@Then("^I should see the data is successfully validated as per the input$")
	public void i_should_see_the_data_is_successfully_validated_as_per_the_input() throws Throwable {
		barcodeReader.validateDataFromExcel(dataFromImage);
	}

	@Given("^I have UPS label image to validate Barcode data$")
	public void i_have_UPS_label_image_to_validate_Barcode_data() throws Throwable {
		System.out.println("-----------Extracting Barcode details from label image file----------");
	}

	@When("^I extract Barcode data from the Label image$")
	public void i_extract_Barcode_data_from_the_Label_image() throws Throwable {
		BarcodeXpress barcodeXpress = new BarcodeXpress();
		BarcodeReader reader = barcodeXpress.getReader();
		imageBarcodeReader.readerAnalyze(reader, "Barcode-2.png");
	}

	@Then("^I should see the Barcode data is successfully validated as per the input$")
	public void i_should_see_the_Barcode_data_is_successfully_validated_as_per_the_input() throws Throwable {
		imageBarcodeReader.validateBarcodeDataFromExcel();
	}

	@Given("^I have UPS label images to compare$")
	public void i_have_UPS_label_images_to_compare() throws Throwable {
		System.out.println("---------Started comparing label image files----------");
	}

	@When("^I compare the UPS label images$")
	public void i_compare_the_UPS_label_images() throws Throwable {
		System.out.println("");
	}

	@Then("^I should see the comparison is successful$")
	public void i_should_see_the_comparison_is_successful() throws Throwable {
		File fileA = new File(strRootDir + "\\testData\\Label-1.png");
		File fileB = new File(strRootDir + "\\testData\\Label-2.png");
		barcodeReader.compareImage(fileA, fileB);
	}

	@Then("^I should see the comparison is successful with highlighting discrepancies$")
	public void i_should_see_the_comparison_is_successful_highlighting() throws Throwable {
		ImageIO.write(
				barcodeReader.getDifferenceImage(ImageIO.read(new File(strRootDir + "\\testData\\CompareA.png")),
						ImageIO.read(new File(strRootDir + "\\testData\\CompareB.png"))),
				"png", new File(strRootDir + "\\testData\\output.png"));
		System.out.println("--------------Successfully compared image files--------------");
		System.out.println("Please find the output in the below location : " + strRootDir + "\\testData\\output.png");

		Thread.sleep(1000);

		String imgPath = strRootDir + "\\testData\\output.png";
		
		BufferedImage bImage = ImageIO.read(new File(imgPath));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(bImage, "png", bos);
		byte[] data = bos.toByteArray();
		scenario.embed(data, "image/png");

	}
}
