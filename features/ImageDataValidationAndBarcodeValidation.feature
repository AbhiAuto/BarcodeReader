Feature: Image Data Validation and Barcode Validation

Scenario: Validating data from Image with the data in the excel
Given I have UPS label image to validate data
When I extract data from the Label image
Then I should see the data is successfully validated as per the input

Scenario: Validating Barcode values in the image files
Given I have UPS label image to validate Barcode data
When I extract Barcode data from the Label image
Then I should see the Barcode data is successfully validated as per the input

Scenario: Comparing two image label files
Given I have UPS label images to compare
When I compare the UPS label images
Then I should see the comparison is successful

@output
Scenario: Comparing two label image files and highlighting discrepancies
Given I have UPS label images to compare
When I compare the UPS label images
Then I should see the comparison is successful with highlighting discrepancies
