package restAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTest {

	Response response;
	RequestSpecification request;
	Map <String, Object > mapObj = new HashMap<String, Object>();
	int empid;
	String name;
	JsonPath jpath;
	String ResponseBody;
	

	@Test
	public void test1() {

		
		RestAssured.baseURI = "http://localhost:3000";
		request = RestAssured.given();
		
		//To get all employee list
		System.out.println("Get all employee list");
		response = getAllEmployee();
		Assert.assertEquals(response.getStatusCode(), 200);
		
		//To create employee named John
		System.out.println("Create employee named John");
		response = createEmployee("John", 8000);
		Assert.assertEquals(response.getStatusCode(), 201);

		
		//To get single employee created above
		System.out.println("get single employee and validate the name as John");
		response = getSingleEmployee(empid);
		Assert.assertEquals(name, "John");
		Assert.assertEquals(response.getStatusCode(), 200);
		
		//To update employee name created above to Smith
		System.out.println("Update John to Smith and validate");
		response = updateEmployee("Smith", empid);
		response = getSingleEmployee(empid);
		Assert.assertEquals(name, "Smith");
		Assert.assertEquals(response.getStatusCode(), 200);
		
		//To delete the employee
		System.out.println("Delete the employee created above");
		response = deleteEmployee(empid);
		Assert.assertEquals(response.getStatusCode(), 200);
		
		//To get single employee created above and validate the 404 error
		System.out.println("get single employee and validate the 404 error");
		response = getSingleEmployee(empid);
		Assert.assertEquals(response.getStatusCode(), 404);
		
		//To validate that the response body does not contain Smith
		System.out.println("Get all employee list");
		response = getAllEmployee();
		Assert.assertFalse(ResponseBody.contains("Smith"));
		
	}

	public Response getAllEmployee() {

		response = request.get("employees");
		ResponseBody = response.getBody().asString();
		System.out.println(ResponseBody);
		System.out.println("Response code is "+response.getStatusCode());
		return response;
	}

	public Response createEmployee(String name, int salary) {

		mapObj.put("name", name);
		mapObj.put("salary", salary);
		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(mapObj).post("employees/create");
		System.out.println(response.getBody().asString());
		jpath = response.jsonPath();
		empid = jpath.get("id");
		System.out.println("Employee id is "+ empid + "\nResponse code is "+response.getStatusCode());
		return response;

	}

	public Response getSingleEmployee(int empid) {

		response = request.get("employees/"+empid);
		System.out.println(response.getBody().asString());
		
		jpath = response.jsonPath();
		name = jpath.get("name");
		System.out.println("Employee id is "+ empid + "\nResponse code is "+response.getStatusCode());
		return response;
	}
	
	public Response updateEmployee(String updatedName, int empid) {

		mapObj.put("name", updatedName);
		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(mapObj).patch("employees/"+empid);
		System.out.println("Response code is "+response.getStatusCode());
		return response;
	}

	public Response deleteEmployee(int empid) {

		response = request.delete("employees/"+empid);
		System.out.println(response.getBody().asString());
		System.out.println(" Response code is "+response.getStatusCode());
		return response;
	}

}
