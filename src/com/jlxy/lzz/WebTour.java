package com.jlxy.lzz;

import org.testng.annotations.Test;

import bsh.ParseException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class WebTour {
	@Parameters({ "firstpara", "secondpara" })
	@Test
	public void f(@Optional("Window") String firstpara, @Optional("Business") String secondpara) {

//		WebDriver dr = new FirefoxDriver();
		System.out.println(firstpara);
		WebDriver dr = new ChromeDriver();
		dr.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		String url = "http://127.0.0.1:1080/WebTours/";

		dr.get(url);
		dr.switchTo().frame("body");
		dr.switchTo().frame("navbar");
		dr.findElement(By.name("username")).sendKeys("2016020800085");

		dr.findElement(By.name("password")).sendKeys("linzhizhou");
		dr.findElement(By.name("login")).click();
		Assert.assertEquals(dr.getTitle(), "Web Tours");

		// 获取cookies
		Set<Cookie> cookies = dr.manage().getCookies();
		System.out.println(String.format("Domain -> name -> value -> expiry -> path"));
		for (Cookie c : cookies) {
			System.out.println(String.format("%s -> %s -> %s -> %s -> %s", c.getDomain(), c.getName(), c.getValue(),
					c.getExpiry(), c.getPath()));
		}

		// 订票
		dr.switchTo().frame("body");
		dr.switchTo().frame("navbar");
		dr.findElement(By.xpath("/html/body/center/center/a[1]/img")).click();
		dr.switchTo().frame("body");
		dr.switchTo().frame("info");
		// 设置出发点和目的地
		Select depart = new Select(dr.findElement(By.name("depart")));
		depart.selectByIndex(3);
		Select arrive = new Select(dr.findElement(By.name("arrive")));
		arrive.selectByValue("Seattle");
		// 将订票日期设置在一个月后
		WebElement departDate = dr.findElement(By.name("departDate"));
		String departs = departDate.getAttribute("value");
		String returnDate = "";
		WebElement bookticket = dr.findElement(By.name("returnDate"));
		bookticket.clear();

		try {
			returnDate = subMonth(formatDate(departs));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		bookticket.sendKeys(formatDate2(returnDate));

		// 设置往返票
		dr.findElement(By.name("roundtrip")).click();

		// 根据参数替换位置信息
		List<WebElement> seatsPref = dr.findElements(By.name("seatPref"));
		for (WebElement e : seatsPref) {
			if (e.getAttribute("value").equals(firstpara)) {
				e.click();
			}
		}
		List<WebElement> seatsType = dr.findElements(By.name("seatType"));
		for (WebElement e : seatsType) {
			if (e.getAttribute("value").equals(secondpara)) {
				e.click();
			}
		}

		// 提交订票信息
		dr.findElement(By.name("findFlights")).click();
		// 航班选择
		List<WebElement> filghtChosen = dr.findElements(By.name("outboundFlight"));
		int count = 0;
		for (WebElement e : filghtChosen) {
			count++;
			if (count != 1) {
				e.click();
				break;
			}
		}
		List<WebElement> filghtChosen2 = dr.findElements(By.name("returnFlight"));
		int count2 = 0;
		for (WebElement e : filghtChosen2) {
			count2++;
			if (count2 != 1) {
				e.click();
				break;
			}
		}

		dr.findElement(By.name("reserveFlights")).click();
		// 支付信息
		dr.findElement(By.name("creditCard")).sendKeys("622208120300002100");
		dr.findElement(By.name("buyFlights")).click();
		// 截图
		File screenShotFile = ((TakesScreenshot) dr).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenShotFile, new File("D:/invoice.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//进入取消订票界面
		dr.switchTo().defaultContent();
		dr.switchTo().frame("body");
		dr.switchTo().frame("navbar");
		dr.findElement(By.xpath("//center/center/a[2]/img")).click();
		//找到自己订的票
		dr.switchTo().parentFrame();
		dr.switchTo().frame("info");
		List<WebElement> bookedTickets=dr.findElements(By.xpath("//center/table/tbody/tr"));
		bookedTickets.get(bookedTickets.size()-7)
		.findElement(By.xpath("./td/b/label/input")).click();
		//删除自己订的票
		dr.findElement(By.name("removeFlights")).click();
		
		//注销
		dr.switchTo().defaultContent();
		dr.switchTo().frame("body");
		dr.switchTo().frame("navbar");
		dr.findElement(By.xpath("//center/center/a[4]/img")).click();
	}

	@BeforeMethod
	public void beforeMethod() {
		System.out.println("@BeforeClass - setUp");
//		System.setProperty("webdriver.gecko.driver", "C:\\WebTours\\geckodriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\Lin Zhizhou\\Desktop\\chromedriver.exe");
	}

	@AfterMethod
	public void afterMethod() {
		System.out.println("@AfterClass - tearDown");
	}

	@BeforeClass
	public void beforeClass() {
	}

	@AfterClass
	public void afterClass() {
	}

	@BeforeTest
	public void beforeTest() {
	}

	private String subMonth(String date) throws ParseException, java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(date);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);
		rightNow.add(Calendar.MONTH, 1);
		Date dt1 = rightNow.getTime();
		String reStr = sdf.format(dt1);
		return reStr;
	}

	private String formatDate(String inDate) {
		SimpleDateFormat inSDF = new SimpleDateFormat("mm/dd/yyyy");
		SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");
		String outDate = "";
		if (inDate != null) {
			try {
				Date date = inSDF.parse(inDate);
				outDate = outSDF.format(date);
			} catch (Exception ex) {
			}
		}
		return outDate;
	}

	private String formatDate2(String inDate) {
		SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat outSDF = new SimpleDateFormat("mm/dd/yyyy");
		String outDate = "";
		if (inDate != null) {
			try {
				Date date = inSDF.parse(inDate);
				outDate = outSDF.format(date);
			} catch (Exception ex) {
			}
		}
		return outDate;
	}
}
