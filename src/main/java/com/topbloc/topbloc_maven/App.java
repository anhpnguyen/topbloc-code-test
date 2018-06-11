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
import org.json.simple.JSONObject;

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
        Data data1 = new Data(file1);
        Data data2 = new Data(file2);
        
        //Importing data from sheets into memory
        data1.processData();
        data2.processData();
        
        //Do calculations and concatenations
        int[] setOneResult = MultiplyNumberSetOne(data1.getNumberSetOne(), data2.getNumberSetOne());
        int[] setTwoResult = DivideNumberSetTwo(data1.getNumberSetTwo(), data2.getNumberSetTwo());
        String[] wordSetResult = ConcatWordSet(data1.getWordSetOne(), data2.getWordSetOne());
        
        //Make POST request to server
        try {
			StatusLine status = PostToServer(setOneResult, setTwoResult, wordSetResult);
			System.out.println(status);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    }
    private static final String uri = "http://34.239.125.159:5000";
    private static StatusLine PostToServer(int[] numberSet1, int[] numberSet2, String[] wordSet) throws ClientProtocolException, IOException {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpPost httpPost = new HttpPost(uri + "/challenge");
    	JSONObject obj = new JSONObject();
    	obj.put("id", id);
    	obj.put("numberSetOne", Arrays.toString(numberSet1));
    	obj.put("numberSetTwo", Arrays.toString(numberSet2));
    	obj.put("wordSetOne", Arrays.toString(wordSet));
    	//System.out.println(Arrays.toString(numberSet1));
    	//System.out.println(Arrays.toString(numberSet2));
    	//System.out.println(Arrays.toString(wordSet));
    	String message = obj.toJSONString();
    	StringEntity entity = new StringEntity(message, ContentType.APPLICATION_JSON);
    	httpPost.setEntity(entity);
    	CloseableHttpResponse response2 = httpclient.execute(httpPost);

    	try {
    	    HttpEntity entity2 = response2.getEntity(); 	 
    	    EntityUtils.consume(entity2);
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} 
    	return response2.getStatusLine();
    }
    private static String[] ConcatWordSet(ArrayList<String> wordSetOne1, ArrayList<String> wordSetOne2) {
		if(wordSetOne1.size() != wordSetOne2.size()) return null;
    	String[] resultArray = new String[wordSetOne1.size()];
		for(int i = 0; i < resultArray.length; i++) {
    		resultArray[i] = wordSetOne1.get(i) + " " + wordSetOne2.get(i);
    	}
		return resultArray;
	}
	private static int[] DivideNumberSetTwo(ArrayList<Integer> numberSetTwo1, ArrayList<Integer> numberSetTwo2) {
		if(numberSetTwo1.size() != numberSetTwo2.size()) return null;
		int[] resultArray = new int[numberSetTwo1.size()];
    	for(int i = 0; i < resultArray.length; i++) {
    		resultArray[i] = numberSetTwo1.get(i) / numberSetTwo2.get(i);
    	}
    	return resultArray;
	}
	private static int[] MultiplyNumberSetOne(ArrayList<Integer> setOne1, ArrayList<Integer> setTwo2) {
		if(setOne1.size() != setTwo2.size()) return null;
		int[] resultArray = new int[setOne1.size()];
    	for(int i = 0; i < resultArray.length; i++) {
    		resultArray[i] = setOne1.get(i) * setTwo2.get(i);
    	}
    	return resultArray;
    }
    public static class Data{
        private ArrayList<Integer> _numberSetOne;
        private ArrayList<Integer> _numberSetTwo;
        private ArrayList<String> _wordSetOne;
        private Workbook _workbook;
        public Data(File file) {
        	try {
            	_workbook = WorkbookFactory.create(file);

        	}catch(Exception e) {
        		e.printStackTrace();	
        	}
        	_numberSetOne = new ArrayList<Integer>();
        	_numberSetTwo = new ArrayList<Integer>();
        	_wordSetOne = new ArrayList<String>(); 	
        }
        public ArrayList<Integer> getNumberSetOne(){ return _numberSetOne; }
        public ArrayList<Integer> getNumberSetTwo(){ return _numberSetTwo; }
        public ArrayList<String> getWordSetOne(){ return _wordSetOne; }
        public void processData() {
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
                    			_numberSetOne.add(Integer.parseInt(cellValue));
                    			break;
                    		case 1: 
                    			_numberSetTwo.add(Integer.parseInt(cellValue));
                    			break;
                    		case 2:
                    			_wordSetOne.add(cellValue);
                    			break;
                    	}
                    }
                }
            }
            catch(Exception e) {
            	e.printStackTrace();	
            }
        }
    }
}
