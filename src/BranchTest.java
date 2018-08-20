import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BranchTest {
	
	private WebDriver mydriver;
	private String vURL;
	private JavascriptExecutor js;
	
	
	@Before
	public void setup(){
		// TODO: Please update below line with actual path to the Chrome driver executable.
		System.setProperty("webdriver.chrome.driver", "<Path to ChromeDriver>/chromedriver");
		mydriver = new ChromeDriver();
		vURL = "https://google.com";
		mydriver.manage().window().maximize();
		mydriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		js = (JavascriptExecutor) mydriver;
	}
	
	@Test
	public void navigatetoBranch() throws InterruptedException{
		mydriver.get(vURL);
		WebElement searchbox = mydriver.findElement(By.xpath("//input[@id='lst-ib']"));
		searchbox.sendKeys("branch website");
		searchbox.sendKeys(Keys.ENTER);
		mydriver.findElement(By.xpath("//a[@href='https://branch.io/']")).click();
		String parentHandle = mydriver.getWindowHandle();
		WebElement teamlink = mydriver.findElement(By.xpath("//a[@href='https://branch.io/team/#all']"));
		js.executeScript("arguments[0].scrollIntoView(true);", teamlink);
		teamlink.click();
		
		Set<String> handles = mydriver.getWindowHandles();
		for (String handle: handles){
			if(!handle.equals(parentHandle)){
			mydriver.switchTo().window(handle);
			}
		}	
			
	
		List<WebElement> links = findAllDepartNames();
		int total = 0;
		int allCount = 0;
		HashMap<String, WebElement> allEmpMap = new HashMap<>();
		for(WebElement link: links) {
			if(link.getText().equalsIgnoreCase("all")) {
				List<WebElement> allEmps = findImagesCountByDepartment(link);
				for (WebElement emp: allEmps) {
					String empName = emp.findElement(By.tagName("h2")).getText();
					allEmpMap.put(empName, emp);
				}
				 System.out.println("All Emp Map size = " + allEmpMap.size());
				allCount = allEmps.size();
			} else {
				List<WebElement> allDeptEmps = findImagesCountByDepartment(link);
				total += allDeptEmps.size();
				for (WebElement deptEmp: allDeptEmps) {
					String deptEmpName = deptEmp.findElement(By.tagName("h2")).getText();
					WebElement allEmp = allEmpMap.get(deptEmpName);
					if (allEmp == null) {
						System.out.println(" Employee not exists in All Employee List : " + deptEmpName );
					} else {
						String empDeptName = deptEmp.findElement(By.tagName("h4")).getText();
						String empAllName = allEmp.findElement(By.tagName("h4")).getText();
						if(!empDeptName.equals(empAllName))
							System.out.println("Employee department doesn't match between All list and Dept list: " + deptEmpName);
						allEmpMap.remove(deptEmpName);
					}
				}
			}
		}
		for(String remainingEle : allEmpMap.keySet()) {
			System.out.println("This employee not found in any department: " + remainingEle);
		}
		try{
			assertEquals(total,  allCount);
			System.out.println("Test Passed: All count = " + allCount + ", total by dept count = " + total);
	     }catch(AssertionError e){
				System.out.println("Test Failed: All count = " + allCount + ", total by dept count = " + total);
	        throw e;
	     }
	}

	
	private List<WebElement> findAllDepartNames(){
		WebElement allElements = mydriver.findElement(By.xpath("//ul[@class='team-categories']"));
		List<WebElement> linkElements = allElements.findElements(By.tagName("li"));
		
		return linkElements;
	}
	
	private List<WebElement> findImagesCountByDepartment(WebElement link) {
		
		link.click();
		String rel = link.findElement(By.tagName("a")).getAttribute("rel");
		List<WebElement> deptDiv = mydriver.findElements(By.className("category-" +rel));
		System.out.println("Dept = " + rel + ", size = " + deptDiv.size());
		
		return deptDiv;
	}

}
