import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// 1. Page Model
class Page {
    private String pageId;
    private String title;
    private String url;
    private LocalDateTime visitedTime;

    public Page(String pageId, String title, String url) {
        this.pageId = pageId;
        this.title = title;
        this.url = url;
        this.visitedTime = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s (%s)]", pageId, title, url);
    }
}

// 2. Algorithm A: Two-Stack Method
class TwoStackBrowser {
    private Deque<Page> backStack = new ArrayDeque<>();
    private Deque<Page> forwardStack = new ArrayDeque<>();
    private Page currentPage = null;

    public void visit(Page page) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }
        currentPage = page;
        forwardStack.clear(); // ล้าง Forward History ทันที
    }

    public void back() {
        if (backStack.isEmpty()) {
            System.out.println("[Action: BACK] -> ไม่สามารถ Back ได้ (ไม่มีประวัติก่อนหน้า)");
            return;
        }
        forwardStack.push(currentPage);
        currentPage = backStack.pop();
    }

    public void forward() {
        if (forwardStack.isEmpty()) {
            System.out.println("[Action: FORWARD] -> ไม่สามารถ Forward ได้ (ไม่มีประวัติถัดไป)");
            return;
        }
        backStack.push(currentPage);
        currentPage = forwardStack.pop();
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public void displayState(String action) {
        System.out.println("==================================================");
        System.out.println("Action: " + action);
        System.out.println("Current Page   : " + (currentPage != null ? currentPage : "None"));
        
        // แปลง Stack เป็น List เพื่อแสดงผลให้ดูง่าย
        List<Page> backList = new ArrayList<>(backStack);
        List<Page> forwardList = new ArrayList<>(forwardStack);
        
        System.out.println("Back History   : " + backList);
        System.out.println("Forward History: " + forwardList);
    }
}

// 3. Algorithm B: ArrayList and Current Index Method
class ArrayListBrowser {
    private List<Page> historyList = new ArrayList<>();
    private int currentIndex = -1;

    public void visit(Page page) {
        if (currentIndex < historyList.size() - 1) {
            // ลบข้อมูล Forward History ที่อยู่ถัดจาก currentIndex ทั้งหมด
            historyList.subList(currentIndex + 1, historyList.size()).clear();
        }
        historyList.add(page);
        currentIndex++;
    }

    public void back() {
        if (currentIndex <= 0) {
            System.out.println("[Action: BACK] -> ไม่สามารถ Back ได้ (ไม่มีประวัติก่อนหน้า)");
            return;
        }
        currentIndex--;
    }

    public void forward() {
        if (currentIndex >= historyList.size() - 1) {
            System.out.println("[Action: FORWARD] -> ไม่สามารถ Forward ได้ (ไม่มีประวัติถัดไป)");
            return;
        }
        currentIndex++;
    }

    public Page getCurrentPage() {
        if (currentIndex >= 0 && currentIndex < historyList.size()) {
            return historyList.get(currentIndex);
        }
        return null;
    }

    public void displayState(String action) {
        System.out.println("==================================================");
        System.out.println("Action: " + action);
        System.out.println("Current Page   : " + getCurrentPage());
        
        List<Page> backList = (currentIndex > 0) ? historyList.subList(0, currentIndex) : Collections.emptyList();
        List<Page> forwardList = (currentIndex >= 0 && currentIndex < historyList.size() - 1) 
                                 ? historyList.subList(currentIndex + 1, historyList.size()) 
                                 : Collections.emptyList();
        
        System.out.println("Back History   : " + backList);
        System.out.println("Forward History: " + forwardList);
    }
}

public class BrowserNavigationLab {
    public static void main(String[] args) {
        System.out.println("**************************************************");
        System.out.println("   ทดสอบชุดคำสั่งบังคับ (ALGORITHM A: TWO-STACK)   ");
        System.out.println("**************************************************");
        
        TwoStackBrowser browserA = new TwoStackBrowser();
        Page pageA = new Page("1", "Page A", "https://a.com");
        Page pageB = new Page("2", "Page B", "https://b.com");
        Page pageC = new Page("3", "Page C", "https://c.com");
        Page pageD = new Page("4", "Page D", "https://d.com");

        browserA.visit(pageA);
        browserA.displayState("VISIT A");

        browserA.visit(pageB);
        browserA.displayState("VISIT B");

        browserA.visit(pageC);
        browserA.displayState("VISIT C");

        browserA.back();
        browserA.displayState("BACK (to B)");

        browserA.back();
        browserA.displayState("BACK (to A)");

        browserA.forward();
        browserA.displayState("FORWARD (to B)");

        browserA.visit(pageD);
        browserA.displayState("VISIT D (Forward ประวัติหน้า C ต้องถูกล้าง)");

        browserA.back();
        browserA.displayState("BACK (to B)");

        browserA.forward();
        browserA.displayState("FORWARD (to D)");

        System.out.println("\n\n**************************************************");
        System.out.println("        การทดลองวัดเวลาประสิทธิภาพ (BENCHMARK)        ");
        System.out.println("**************************************************");
        
        int[] dataSizes = {1000, 10000, 50000, 100000};
        System.out.printf("%-12s | %-25s | %-25s%n", "จำนวนหน้า (N)", "Two-Stack (ns / ms)", "ArrayList (ns / ms)");
        System.out.println("------------------------------------------------------------------");

        for (int size : dataSizes) {
            // 1. เตรียม Benchmark Two-Stack
            TwoStackBrowser testA = new TwoStackBrowser();
            for (int i = 0; i < size; i++) {
                testA.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
            }
            for (int i = 0; i < size / 2; i++) {
                testA.back(); // ถอยกลับมาที่กึ่งกลาง
            }
            
            // จับเวลาเฉพาะจังหวะ Visit หน้าใหม่ ที่ต้องเคลียร์ Forward
            Page newPage = new Page("new", "New Page", "https://new.com");
            long startTimeA = System.nanoTime();
            testA.visit(newPage);
            long durationA = System.nanoTime() - startTimeA;

            // 2. เตรียม Benchmark ArrayList
            ArrayListBrowser testB = new ArrayListBrowser();
            for (int i = 0; i < size; i++) {
                testB.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
            }
            for (int i = 0; i < size / 2; i++) {
                testB.back(); // ถอยกลับมาที่กึ่งกลาง
            }
            
            // จับเวลาเฉพาะจังหวะ Visit หน้าใหม่ ที่ต้องเคลียร์ Forward
            long startTimeB = System.nanoTime();
            testB.visit(newPage);
            long durationB = System.nanoTime() - startTimeB;

            System.out.printf("%-12d | %10d ns (%6.3f ms) | %10d ns (%6.3f ms)%n", 
                    size, durationA, durationA / 1_000_000.0, durationB, durationB / 1_000_000.0);
        }
    }
}