package com.topbloc.topbloc_maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App 
{
	public static final String DATA1_PATH = "./Data1.xlsx";
    public static final String DATA2_PATH = "./Data2.xlsx";
    private static final String id = "anhpnguyen.an@gmail.com";
    public static void main( String[] args )
    {
    	//Creating files from paths
        File file1 = new File(DATA1_PATH);
        File file2 = new File(DATA2_PATH);
        
        //Passing files in Data constructor to create workbooks
        DataProcessor data1 = new DataProcessor(file1);
        DataProcessor data2 = new DataProcessor(file2);
        
        //Importing data from sheets into memory
        Payload ds1 = data1.processData();
        Payload ds2 = data2.processData();
        
        //Do calculations and concatenations
        Payload calculatedData = new Payload();
        calculatedData.numberSetOne = MultiplyNumberSetOne(ds1.numberSetOne, ds2.numberSetOne);
        calculatedData.numberSetTwo = DivideNumberSetTwo(ds1.numberSetTwo, ds2.numberSetTwo);
        calculatedData.wordSetOne = ConcatWordSet(ds1.wordSetOne, ds2.wordSetOne);
        
        //Make POST request to server
        try {
			String httpResult = PostToServer(calculatedData);
			System.out.println(httpResult);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   
    }
    private static final String uri = "http://34.239.125.159:5000";
    private static String PostToServer(Payload data) throws ClientProtocolException, IOException {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpPost httpPost = new HttpPost(uri + "/challenge");
    	data.id = id;
    	GsonBuilder builder = new GsonBuilder();
    	Gson gson = builder.create();   	
    	String message = gson.toJson(data);
    	System.out.println(message);
    	StringEntity entity = new StringEntity(message, ContentType.APPLICATION_JSON);
    	httpPost.setEntity(entity);
    	CloseableHttpResponse response2 = httpclient.execute(httpPost);

    	try {
    	    HttpEntity entity2 = response2.getEntity(); 	 
    	    if(entity2 != null) {
    	    	String ret = EntityUtils.toString(entity2);
    	    	return ret;
    	    }
    	} catch (Exception e) {
			e.printStackTrace();			
		} 
    	return null;
    }
    private static ArrayList<String> ConcatWordSet(ArrayList<String> wordSetOne1, ArrayList<String> wordSetOne2) {
		if(wordSetOne1.size() != wordSetOne2.size()) return null;
    	ArrayList<String> resultArray = new ArrayList<String>();
		for(int i = 0; i < wordSetOne1.size(); i++) {
    		resultArray.add(wordSetOne1.get(i) + " " + wordSetOne2.get(i));
    	}
		return resultArray;
	}
	private static ArrayList<Integer> DivideNumberSetTwo(ArrayList<Integer> numberSetTwo1, ArrayList<Integer> numberSetTwo2) {
		if(numberSetTwo1.size() != numberSetTwo2.size()) return null;
		ArrayList<Integer> resultArray = new ArrayList<Integer>();
    	for(int i = 0; i < numberSetTwo1.size(); i++) {
    		resultArray.add(numberSetTwo1.get(i) / numberSetTwo2.get(i));
    	}
    	return resultArray;
	}
	private static ArrayList<Integer> MultiplyNumberSetOne(ArrayList<Integer> setOne1, ArrayList<Integer> setTwo2) {
		if(setOne1.size() != setTwo2.size()) return null;
		ArrayList<Integer> resultArray = new ArrayList<Integer>();
    	for(int i = 0; i < setOne1.size(); i++) {
    		resultArray.add(setOne1.get(i) * setTwo2.get(i));
    	}
    	return resultArray;
    }
	public static class Payload{
		public String id;
        public ArrayList<Integer> numberSetOne = new ArrayList<Integer>();
        public ArrayList<Integer> numberSetTwo = new ArrayList<Integer>();
        public ArrayList<String> wordSetOne = new ArrayList<String>();
		
	}
    public static class DataProcessor{

        private Workbook _workbook;
        public DataProcessor(File file) {
        	try {
            	_workbook = WorkbookFactory.create(file);

        	}catch(Exception e) {
        		e.printStackTrace();	
        	} 	
        }
        public Payload processData() {
        	Payload result = new Payload();
        	Sheet sheet = _workbook.getSheetAt(0);
        	DataFormatter dataFormatter = new DataFormatter();
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            try {
            	for(int row = 1; row < numberOfRows; row++){
                	Row r = sheet.getRow(row);
                    for(Cell cell : r){
                    	String cellValue = dataFormatter.formatCellValue(cell);
                    	switch(cell.getColumnIndex()) {
                    		case 0:
                    			result.numberSetOne.add(Integer.parseInt(cellValue));
                    			break;
                    		case 1: 
                    			result.numberSetTwo.add(Integer.parseInt(cellValue));
                    			break;
                    		case 2:
                    			result.wordSetOne.add(cellValue);
                    			break;
                    	}
                    }
                }
            }
            catch(Exception e) {
            	e.printStackTrace();	
            }
            return result;
        }
    }
}
