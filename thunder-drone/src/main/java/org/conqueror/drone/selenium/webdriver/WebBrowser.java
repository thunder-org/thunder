package org.conqueror.drone.selenium.webdriver;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class WebBrowser implements Closeable {

    public enum WebDriverName {CHROME}

    public enum RelLocation {UP, DOWN, LEFT, RIGHT, CENTER, NONE}

    private final WebDriver driver;
    private String requestedUrl;
    private Dimension windowSize;
    private int restrictedWindowHeight;

    private final boolean findInOnlyWindowSize;

    private final Map<WebElement, Integer> numbersOfElements = new HashMap<>();


    private WebBrowser(WebDriver driver, boolean findInOnlyWindowSize) {
        this.driver = driver;
        this.findInOnlyWindowSize = findInOnlyWindowSize;
        this.windowSize = driver.manage().window().getSize();
        this.restrictedWindowHeight = windowSize.getHeight();

//        driver.manage().timeouts().implicitlyWait(10, SECONDS);
//        driver.manage().timeouts().pageLoadTimeout(100, SECONDS);
    }

    public static WebBrowser createWebBrowser(WebDriverName name) {
        return createWebBrowser(name, null, false);
    }

    public static WebBrowser createWebBrowser(WebDriverName name, MutableCapabilities options) {
        return createWebBrowser(name, options, false);
    }

    public static WebBrowser createWebBrowser(WebDriverName name, MutableCapabilities options, boolean findInOnlyWindowSize) {
        switch (name) {
            case CHROME:
                return new WebBrowser(options != null ? new ChromeDriver((ChromeOptions) options) : new ChromeDriver(), findInOnlyWindowSize);
        }

        throw new RuntimeException("does not exist the web driver '" + name + "'");
    }

    public boolean visit(String url) {
        URI uri = toURI(url);
        if (uri != null) {
            url = uri.toString();
        }

        requestedUrl = url;

        try {
            driver.get(url);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void desideNumbersOfNodes(int attempts) {
        AtomicInteger number = new AtomicInteger(0);
        WebElement body = driver.findElement(By.tagName("body"));
        desideNumbersOfNodes(body, number, attempts);
    }

    private void desideNumbersOfNodes(WebElement node, AtomicInteger number, int attempts) {
        if (node != null) {
            numbersOfElements.put(node, number.incrementAndGet());

            for (WebElement child : findChildElements(node, attempts)) {
                desideNumbersOfNodes(child, number, attempts);
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void waitForPageLoad() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        wait.until(driver -> String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")).equals("complete"));
    }

//    public void waitForPageLoad() {
//        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
//        wait.until((ExpectedCondition<Boolean>) driver -> ((JavascriptExecutor) driver).executeScript(
//            "return document.readyState"
//        ).equals("complete"));
//    }

    public WebElement findParentElement(WebElement element, int attempts) {
        return findElementByXPath(element, "./parent::*", attempts);
    }

    public List<WebElement> findChildElements(WebElement element, int attempts) {
        return findElementsByXPath(element, "./*[not(contains(@style,'display: none') or contains(@style, 'display:none'))]", attempts);
    }

    public List<WebElement> findAllChildElements(WebElement element, int attempts) {
        return findElementsByXPath(element, ".//*", attempts);
    }

    public boolean isChildElement(WebElement parent, WebElement child, int attempts) {
        while (true) {
            try {
                RemoteWebElement dest = (RemoteWebElement) findParentElement(child, attempts);
                if (dest == null) return false;
                if (dest.getId().equals(((RemoteWebElement) parent).getId())) return true;
                child = dest;
            } catch (NoSuchElementException e) {
                return false;
            }
        }
    }

    public boolean isParentOfSelfTagName(String[] tagNames, WebElement child, int attempts) {
        while (true) {
            try {
                for (String tagName : tagNames) {
                    if (child.getTagName().toLowerCase(Locale.ENGLISH).equals(tagName)) return true;
                }

                RemoteWebElement parent = (RemoteWebElement) findParentElement(child, attempts);
                if (parent == null) return false;
                for (String tagName : tagNames) {
                    if (parent.getTagName().toLowerCase(Locale.ENGLISH).equals(tagName)) return true;
                }
                child = parent;
            } catch (NoSuchElementException e) {
                return false;
            }
        }
    }

    public boolean isChildOfSelfTagName(String[] tagNames, WebElement parent, int attempts) {
        try {
            for (String tagName : tagNames) {
                if (parent.getTagName().toLowerCase(Locale.ENGLISH).equals(tagName)) return true;
            }

            List<WebElement> children = findAllChildElements(parent, attempts);
            for (WebElement child : children) {
                if (isChildOfSelfTagName(tagNames, child, attempts)) return true;
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        return false;
    }

    public boolean isChildrenSameFont(WebElement parent, int attempts) {
        float fontSize = getFontSize(parent);
        String fontStyle = getFontStyle(parent);
        RGB fontColor = getFontColor(parent);
        try {
            List<WebElement> children = findAllChildElements(parent, attempts);
            for (WebElement child : children) {
                if (fontSize != getFontSize(child) || !fontStyle.equals(getFontStyle(child)) || !fontColor.equals(getFontColor(child)))
                    return false;
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        return true;
    }

    public WebElement findElementByCssSelector(String selector, int attempts) {
        try {
            return driver.findElement(By.cssSelector(selector));
        } catch (Exception e) {
            return null;
        }
    }

    public WebElement findElementByXPath(WebElement element, String xpath, int attempts) {
        return findElementByXPath((SearchContext) element, xpath, attempts);
    }

    public WebElement findElementByXPath(String xpath, int attempts) {
        return findElementByXPath(driver, xpath, attempts);
    }

    public List<WebElement> findElementsByXPath(WebElement element, String xpath, int attempts) {
        return findElementsByXPath((SearchContext) element, xpath, attempts);
    }

    public List<WebElement> findElementsByXPath(String xpath, int attempts) {
        return findElementsByXPath(driver, xpath, attempts);
    }

    public List<WebElement> findElementsByTag(String tag) {
        return driver.findElements(By.tagName(tag));
    }

    public List<WebElement> findElementsHasText(boolean onlySelf, int attempts) {
        return findElementsHasText((String[]) null, onlySelf, attempts);
    }

    public List<WebElement> findElementsHasText(String[] notTags, boolean onlySelf, int attempts) {
        return findElementsHasText(driver, "//*", notTags, onlySelf, attempts);
    }


    public List<WebElement> findElementsHasText(WebElement element, boolean onlySelf, int attempts) {
        return findElementsHasText(element, null, onlySelf, attempts);
    }

    public List<WebElement> findElementsHasText(WebElement element, String[] notTags, boolean onlySelf, int attempts) {
        return findElementsHasText(element, "./*", notTags, onlySelf, attempts);
    }

    private List<WebElement> findElementsHasText(SearchContext element, String root, String[] notTags, boolean onlySelf, int attempts) {
        List<WebElement> results;
        List<WebElement> children = new ArrayList<>();
        Set<WebElement> parents = new HashSet<>();

        String xpath = root +
            getNotTagsString(notTags) +
            "[not(self::*[contains(@style,'display: none') or contains(@style, 'display:none') or contains(@style, 'hidden') or contains(@style, 'block')])]" +
            "[not(ancestor::*[contains(@style,'display: none') or contains(@style, 'display:none') or contains(@style, ' hidden') or contains(@style, 'block')])]" +
            (onlySelf ? "[text()[normalize-space()]]" : "[text() or self::main or self::header or self::footer]");
        List<WebElement> elements = findElementsByXPath(element, xpath, attempts);
        for (WebElement child : elements) {
            if (!child.getText().isEmpty()) {
                children.add(child);
                if (onlySelf && hasNDepthChildren(child, 0, attempts)) {
                    WebElement parent = findParentElement(child, attempts);
                    if (parent != null) parents.add(parent);
                }
            }
        }

        if (onlySelf) {
            results = new ArrayList<>(parents);
            for (WebElement child : children) {
                if (!parents.contains(child)) results.add(child);
            }
        } else {
            results = children;
        }

        return results;
    }

    public List<WebElement> findElementsHasImage(int attempts) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement child : findElementsByXPath("//img", attempts)) {
            if (child.isDisplayed() && getElementSize(child) > 0) {
                result.add(child);
            }
        }

        return result;
    }

    public List<WebElement> findElementsHasImageOrderBySize(int attempts) {
        return findElementsHasImageOrderBySize(findElementsHasImage(attempts));
    }

    private String getNotTagsString(String[] notTags) {
        if (notTags == null) return "";

        StringBuilder xpath = new StringBuilder();
        for (String tag : notTags) {
            xpath.append("[not(self::");
            xpath.append(tag);
            xpath.append(")]");
        }

        return xpath.toString();
    }

    private List<WebElement> findElementsHasImageOrderBySize(List<WebElement> elements) {
        elements.sort(Comparator.comparingInt(o -> (getElementSize(o) * -1)));
        return elements;
    }

    public static int getElementSize(WebElement element) {
        return element.getSize().getWidth() * element.getSize().getHeight();
    }

    public List<WebElement> findElementsHasAnyoneOfChildren(WebElement element, List<WebElement> children, int attempts) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement child : findElementsHasText(element, false, attempts)) {
            for (WebElement dest : children) {
                if (isChildElement(child, dest, attempts)) {
                    result.add(child);
                    break;
                }
            }
        }

        return result;
    }

    public List<WebElement> findElementsHasAnyoneOfChildren(List<WebElement> elements, List<WebElement> children, int attempts) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement element : elements) {
            result.addAll(findElementsHasAnyoneOfChildren(element, children, attempts));
        }

        return result;
    }

    public String getXPathFromElement(WebElement element) {
        return (String) executeJavascript("gPt=function(c){" +
            "   if(c.id!==''){" +
            "       return'id(\"'+c.id+'\")'" +
            "   }" +
            "   if(c===document.body){" +
            "       return c.tagName" +
            "   }" +
            "   var a=0;" +
            "   var e=c.parentNode.childNodes;" +
            "   for(var b=0;b<e.length;b++){" +
            "       var d=e[b];" +
            "       if(d===c){" +
            "           return gPt(c.parentNode)+'/'+c.tagName.toLowerCase()+'['+(a+1)+']'" +
            "       }" +
            "       if(d.nodeType===1&&d.tagName===c.tagName){" +
            "           a++" +
            "       }" +
            "   }" +
            "};" +
            "return gPt(arguments[0]);", element);
    }

    public String getCssSelectorFromElement(WebElement element) {
        try {
            return (String) executeJavascript("for(var e=arguments[0],n=[],i=function(e,n){" +
                "if(!e||!n)return 0;for(var i=0,a=e.length;a>i;i++)if(-1==n.indexOf(e[i]))return 0;re" +
                "turn 1};e&&1==e.nodeType&&'HTML'!=e.nodeName;e=e.parentNode){if(" +
                "e.id){n.unshift('#'+e.id);break}for(var a=1,r=1,o=e.localName,l=" +
                "e.className&&e.className.trim().split(/[\\s,]+/g),t=e.previousSi" +
                "bling;t;t=t.previousSibling)10!=t.nodeType&&t.nodeName==e.nodeNa" +
                "me&&(i(l,t.className)&&(l=null),r=0,++a);for(var t=e.nextSibling" +
                ";t;t=t.nextSibling)t.nodeName==e.nodeName&&(i(l,t.className)&&(l" +
                "=null),r=0);n.unshift(r?o:o+(l?'.'+l.join('.'):':nth-child('+a+'" +
                ")'))}return n.join(' > ');", element);
        } catch (Exception e) {
            return "";
        }
    }

    public List<WebElement> getTextElement(boolean onlySelf, int attempts) {
        return getTextElement(null, null, onlySelf, attempts);
    }

    public List<WebElement> getTextElement(List<WebElement> includedElements, boolean onlySelf, int attempts) {
        return getTextElement(null, includedElements, onlySelf, attempts);
    }

    public List<WebElement> getTextElement(String[] notTags, List<WebElement> includedElements, boolean onlySelf, int attempts) {
        List<WebElement> elements = findElementsHasText(notTags, onlySelf, attempts);
        List<WebElement> selectedElements = new ArrayList<>();

        for (WebElement element : elements) {
            if (!element.isDisplayed() || (includedElements != null && includedElements.contains(element))) continue;

            String text = element.getText().trim().toLowerCase(Locale.ENGLISH).replaceAll("\n\r", " ");
            if (text == null || text.length() == 0) continue;

            boolean selected = true;
            try {
                if (hasNDepthChildren(element, 0, attempts)) {
                    selected = true;
                } else if (hasNDepthChildren(element, 1, attempts)) {
                    for (WebElement child : findChildElements(element, attempts)) {
                        String childText = child.getText().trim().toLowerCase(Locale.ENGLISH).replaceAll("\n\r", " ");
                        if (text.length() == childText.length()) {
                            selected = false;
                            break;
                        } else if (includedElements != null && text.contains(childText)) {
                            includedElements.add(child);
                        }
                    }
                } else {
                    selected = false;
                }
            } catch (NoSuchElementException e) {
                selected = false;
            }
            if (selected) {
                selectedElements.add(element);
            }

        }

        return selectedElements;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public boolean isRedirected() {
        return !equalsUrl(requestedUrl, driver.getCurrentUrl());
    }

    public Object executeJavascript(String script, Object... args) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return jsExecutor.executeScript(script, args);
    }

    public String getHtmlTagAttributes(WebElement element) {
        return (String.valueOf(executeJavascript("var items = {};" +
                " for (index = 0; index < arguments[0].attributes.length; ++index) {" +
                " items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value" +
                " };" +
                " return items;"
            , element))).trim().replaceAll("\n\r", " ");
    }

    public void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
        windowSize = new Dimension(width, height);
        restrictedWindowHeight = windowSize.getHeight();
    }

    public int getWindowWidth() {
        return windowSize.getWidth();
    }

    public int getWindowHeight() {
        return windowSize.getHeight();
    }

    public static float getFontSize(WebElement element) {
        return toFontSize(element.getCssValue("font-size"));
    }

    public static String getFontStyle(WebElement element) {
        return element.getCssValue("font-style").toLowerCase(Locale.ENGLISH);
    }

    public static String getFontFamily(WebElement element) {
        return element.getCssValue("font-family").toLowerCase(Locale.ENGLISH);
    }

    public static int getFontWeight(WebElement element) {
        return toFontWeight(element.getCssValue("font-weight").toLowerCase(Locale.ENGLISH));
    }

    public static int getWordCount(WebElement element) {
        return new StringTokenizer(element.getText().trim()).countTokens();
    }

    public static float getNumberRatio(String text) {
        int tot = 0;
        int num = 0;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) continue;
            if (Character.isDigit(ch)) num++;

            tot++;
        }

        return (float) num / tot;
    }

    public static float getSpecialCharRatio(String text) {
        int tot = 0;
        int num = 0;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) continue;
            if (!Character.isLetterOrDigit(ch)) num++;

            tot++;
        }

        return (float) num / tot;
    }

    public static float getTextPercent(WebElement element) {
        String text = element.getText().replaceAll("\\s", "");
        float textCount = 0f;
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                textCount++;
            }
        }
        return textCount / text.length();
    }

    public static RGB getFontColor(WebElement element) {
        return new RGB(element.getCssValue("color").toLowerCase(Locale.ENGLISH));
    }

    public static RelLocation getLocation(WebElement center, WebElement dest) {
        try {
            int centerXStart = center.getLocation().getX();
            int centerYStart = center.getLocation().getY();
            int centerXEnd = centerXStart + center.getSize().getWidth();
            int centerYEnd = centerYStart + center.getSize().getHeight();

            int destX = dest.getLocation().getX() + (dest.getSize().getWidth() / 2);
            int destY = dest.getLocation().getY() + (dest.getSize().getHeight() / 2);

            RelLocation location;
            if (centerYStart < destY && centerYEnd > destY) {
                if (centerXStart > destX) {
                    location = RelLocation.LEFT;
                } else if (centerXEnd < destX) {
                    location = RelLocation.RIGHT;
                } else {
                    location = RelLocation.CENTER;
                }
            } else if (centerYStart > destY) {
                location = RelLocation.UP;
            } else {
                location = RelLocation.DOWN;
            }

            return location;
        } catch (Exception e) {
            return RelLocation.NONE;
        }
    }

    public static double getDistance(WebElement src, WebElement dest) {
        Point srcPoint = src.getLocation();
        Point destPoint = dest.getLocation();

        return Math.sqrt(Math.pow(srcPoint.getX() - destPoint.getX(), 2.d) + Math.pow(srcPoint.getY() - destPoint.getY(), 2.d));
    }

    public boolean isBetweenPoints(Point begin, Point end, WebElement element) {
        Point dest = element.getLocation();
        return begin.getX() <= dest.getX()
            && begin.getY() <= dest.getY()
            && end.getX() >= dest.getX()
            && end.getY() >= dest.getY();
    }

    public int getFullPageWidth() {
        return Integer.valueOf(
            String.valueOf(
                executeJavascript("return Math.max(document.body.scrollWidth" +
                    ", document.body.offsetWidth, document.documentElement.clientWidth" +
                    ", document.documentElement.scrollWidth" +
                    ", document.documentElement.offsetWidth);")
            )
        );
    }

    public int getFullPageHeight() {
        return Integer.valueOf(
            String.valueOf(
                executeJavascript("return Math.max(document.body.scrollHeight" +
                    ", document.body.offsetHeight, document.documentElement.clientHeight" +
                    ", document.documentElement.scrollHeight" +
                    ", document.documentElement.offsetHeight);")
            )
        );
    }

    public byte[] captureImage() {
        RemoteWebDriver screenshot = (RemoteWebDriver) driver;
        return screenshot.getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void close() {
        driver.quit();
    }

    public void closeWindow() {
        driver.close();
    }

    private WebElement findElementByXPath(SearchContext dest, String xpath, int attempts) {
        while (attempts > 0) {
            try {
                return dest.findElement(By.xpath(xpath));
            } catch (StaleElementReferenceException | TimeoutException e) {
                attempts--;
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        return null;
    }

    private List<WebElement> findElementsByXPath(SearchContext dest, String xpath, int attempts) {
        List<WebElement> results = new ArrayList<>();

        if (!findInOnlyWindowSize) {
            while (attempts > 0) {
                try {
                    return dest.findElements(By.xpath(xpath));
                } catch (StaleElementReferenceException | TimeoutException e) {
                    attempts--;
                } catch (NoSuchElementException e) {
                    break;
                }
            }
        } else {
            List<WebElement> elements = Collections.emptyList();
            while (attempts > 0) {
                try {
                    elements = dest.findElements(By.xpath(xpath));
                    break;
                } catch (StaleElementReferenceException | TimeoutException e) {
                    attempts--;
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            for (WebElement element : elements) {
                try {
                    if (isInOnlyWindowSize(element) && element.isDisplayed()) {
                        results.add(element);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return results;
    }

    private int depthOfChildren(WebElement element, int attempts) {
        List<WebElement> children = findChildElements(element, attempts);
        int depth = 0;
        for (WebElement child : children) {
            depth = Math.max(depthOfChildren(child, attempts), depth);
        }

        return depth + (children.size() == 0 ? 0 : 1);
    }

    private int depthOfChildren(WebElement element, int max, int attempts) {
        List<WebElement> children = findChildElements(element, attempts);
        int depth = 0;
        for (WebElement child : children) {
            depth = Math.max(depthOfChildren(child, attempts), depth);
            if (depth >= max) break;
        }

        return depth + (children.size() == 0 ? 0 : 1);
    }

    public boolean hasNDepthChildren(WebElement element, int depth, int attempts) {
        if (depth == 0) {
            try {
                return findElementByXPath(element, "./*", attempts) == null;
            } catch (NoSuchElementException e) {
                return true;
            }
        }

        StringBuilder nDepthSB = new StringBuilder(".");
        for (int n = 0; n < depth; n++) {
            nDepthSB.append("/*");
        }
        String nDepth = nDepthSB.toString();
        String mDepth = nDepthSB.append("/*").toString();
        try {
            WebElement webElement = findElementByXPath(element, nDepth, attempts);
            if (webElement != null) {
                try {
                    webElement = findElementByXPath(element, mDepth, attempts);
                    return webElement == null;
                } catch (NoSuchElementException e) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        return false;
    }

    public boolean isInOnlyWindowSize(WebElement element) {
        return element.getLocation().getY() < restrictedWindowHeight;
    }

    private static boolean equalsUrl(String url1, String url2) {
        URI uri1 = toURI(url1);
        URI uri2 = toURI(url2);

        try {
            return uri1.getHost().equals(uri2.getHost()) && uri1.getPath().equals(uri2.getPath());
        } catch (Exception e) {
            return false;
        }
    }

    private static URI toURI(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null) {
                uri = new URI("http://" + url);
            }
            return uri;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static float toFontSize(String fontSize) {
        float fontSizeValue;

        if (fontSize.endsWith("px")) {
            fontSizeValue = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
        } else {
            try {
                fontSizeValue = Float.parseFloat(fontSize);
            } catch (NumberFormatException e) {
                fontSizeValue = 0;
            }
        }

        return fontSizeValue;
    }

    private static int toFontWeight(String fontWeight) {
        int fontWeightValue = 0;

        try {
            fontWeightValue = Integer.parseInt(fontWeight);
        } catch (NumberFormatException e) {
            if (fontWeight.compareTo("normal") == 0) {
                fontWeightValue = 400;
            } else if (fontWeight.compareTo("bold") == 0) {
                fontWeightValue = 700;
            }
        }

        return fontWeightValue;
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hyundai\\Downloads\\chromedriver_win32\\chromedriver.exe");

        WebBrowser browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME);
        browser.visit("https://www.geogigani.com/destination-detail/2406364");
//        browser.visit("https://www.enjoydrone.com/bbs/board.php?bo_table=product&sca=type1");
//        WebElement element = browser.findElementByXPath("//a[@class=\"portfolio-link\"]", 3);
//        browser.executeJavascript("location.href='https://www.geogigani.com/destination-detail/2405703?'");
//        browser.executeJavascript(String.format("location.href='%s';", "https://www.geogigani.com/destination-detail/2405703?"));
//        browser.executeJavascript("function getElementByXpath(path) { return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; }" +
//            "var elmt = getElementByXpath('id(\"popular-ac-tab-1-0\")/div[1]/div[1]/div[1]/a[1]');");
//        browser.executeJavascript("var value = document.evaluate('"+ browser.getXPathFromElement(element) +"', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
//            "value.setAttribute('target', '_blank');");
//        System.out.println(element.getAttribute("target"));
//        browser.executeJavascript("var value = document.evaluate('"+ browser.getXPathFromElement(element) +"', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; value.click(); alert(location.href);");
//        element.click();
        browser.executeJavascript("arguments[0].click();", browser.findElementByXPath("//*[@id=\"services\"]/div[1]/div/div/div/div/div[1]/div[2]/blockquote[1]/div/p[1]/a", 3));


//        WebElement element = browser.findElementByXPath("//*[@id=\"ajaxlist_tbody\"]/div[2]/ul/li[1]/div[2]/a", 3);
//        element.click();
    }

}