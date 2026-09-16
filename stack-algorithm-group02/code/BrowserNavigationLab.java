import java.time.LocalDateTime;
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

    public String getTitle() { return title; }
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return String.format("[%s: %s (%s)]", pageId, title, url);
    }
}

// 2. Algorithm A: Two-Stack Method (พร้อมตัวนับ Operation)
class TwoStackBrowser {
    private Deque<Page> backStack = new ArrayDeque<>();
    private Deque<Page> forwardStack = new ArrayDeque<>();
    private Page currentPage = null;

    // เก็บสถิติสำหรับรายงานบทที่ 7
    public long pushCount = 0;
    public long popCount = 0;
    public long clearOperations = 0;

    public void visit(Page page) {
        if (currentPage != null) {
            backStack.push(currentPage);
            pushCount++;
        }
        currentPage = page;
        clearOperations += forwardStack.size();
        forwardStack.clear(); // O(1) ทางตรรกะพอยน์เตอร์
    }

    public void back() {
        if (backStack.isEmpty()) {
            System.out.println("[Action: BACK] -> ไม่สามารถ Back ได้ (ไม่มีประวัติก่อนหน้า)");
            return;
        }
        forwardStack.push(currentPage);
        pushCount++;
        currentPage = backStack.pop();
        popCount++;
    }

    public void forward() {
        if (forwardStack.isEmpty()) {
            System.out.println("[Action: FORWARD] -> ไม่สามารถ Forward ได้ (ไม่มีประวัติถัดไป)");
            return;
        }
        backStack.push(currentPage);
        pushCount++;
        currentPage = forwardStack.pop();
        popCount++;
    }

    public Page getCurrentPage() { return currentPage; }

    public void displayState(String action) {
        System.out.println("==================================================");
        System.out.println("Action: " + action);
        System.out.println("Current Page   : " + (currentPage != null ? currentPage : "None"));
        System.out.println("Back History   : " + new ArrayList<>(backStack));
        System.out.println("Forward History: " + new ArrayList<>(forwardStack));
    }
}

// 3. Algorithm B: ArrayList Method (พร้อมตัวนับ Operation)
class ArrayListBrowser {
    private List<Page> historyList = new ArrayList<>();
    private int currentIndex = -1;

    public long elementsRemoved = 0;

    public void visit(Page page) {
        if (currentIndex < historyList.size() - 1) {
            int itemsToRemove = historyList.size() - (currentIndex + 1);
            elementsRemoved += itemsToRemove;
            historyList.subList(currentIndex + 1, historyList.size()).clear(); // O(N) ต้องขยับ/เคลียร์ Array
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

// 4. Main Class & Benchmark Suite
public class BrowserNavigationLab {

    private static void runMandatorySequence(String name, TwoStackBrowser browserA, ArrayListBrowser browserB) {
        Page pageA = new Page("1", "Page A", "https://a.com");
        Page pageB = new Page("2", "Page B", "https://b.com");
        Page pageC = new Page("3", "Page C", "https://c.com");
        Page pageD = new Page("4", "Page D", "https://d.com");

        if (browserA != null) {
            browserA.visit(pageA); browserA.displayState("VISIT A");
            browserA.visit(pageB); browserA.displayState("VISIT B");
            browserA.visit(pageC); browserA.displayState("VISIT C");
            browserA.back();       browserA.displayState("BACK (to B)");
            browserA.back();       browserA.displayState("BACK (to A)");
            browserA.forward();    browserA.displayState("FORWARD (to B)");
            browserA.visit(pageD); browserA.displayState("VISIT D (Forward C ล้าง)");
            browserA.back();       browserA.displayState("BACK (to B)");
            browserA.forward();    browserA.displayState("FORWARD (to D)");
        } else {
            browserB.visit(pageA); browserB.displayState("VISIT A");
            browserB.visit(pageB); browserB.displayState("VISIT B");
            browserB.visit(pageC); browserB.displayState("VISIT C");
            browserB.back();       browserB.displayState("BACK (to B)");
            browserB.back();       browserB.displayState("BACK (to A)");
            browserB.forward();    browserB.displayState("FORWARD (to B)");
            browserB.visit(pageD); browserB.displayState("VISIT D (Forward C ล้าง)");
            browserB.back();       browserB.displayState("BACK (to B)");
            browserB.forward();    browserB.displayState("FORWARD (to D)");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. ทดสอบชุดคำสั่งบังคับ (Algorithm A) ===");
        runMandatorySequence("TwoStack", new TwoStackBrowser(), null);

        System.out.println("\n=== 2. ทดสอบชุดคำสั่งบังคับ (Algorithm B) ===");
        runMandatorySequence("ArrayList", null, new ArrayListBrowser());

        System.out.println("\n==========================================================================");
        System.out.println("   การทดลองวัดประสิทธิภาพเฉลี่ย 5 รอบ (BENCHMARK 5-RUN AVERAGE)           ");
        System.out.println("==========================================================================");
        
        int[] dataSizes = {1000, 10000, 50000, 100000};
        int runs = 5;

        System.out.printf("%-10s | %-20s | %-20s | %-15s%n", 
                "N", "Avg Two-Stack (ns)", "Avg ArrayList (ns)", "Items Removed (B)");
        System.out.println("--------------------------------------------------------------------------");

        for (int size : dataSizes) {
            long totalTimeA = 0;
            long totalTimeB = 0;
            long removedCountB = 0;

            for (int r = 0; r < runs; r++) {
                // Benchmarking A
                TwoStackBrowser testA = new TwoStackBrowser();
                for (int i = 0; i < size; i++) testA.visit(new Page(String.valueOf(i), "P", "U"));
                for (int i = 0; i < size / 2; i++) testA.back();
                
                Page newPageA = new Page("new", "New", "https://new.com");
                long startA = System.nanoTime();
                testA.visit(newPageA);
                totalTimeA += (System.nanoTime() - startA);

                // Benchmarking B
                ArrayListBrowser testB = new ArrayListBrowser();
                for (int i = 0; i < size; i++) testB.visit(new Page(String.valueOf(i), "P", "U"));
                for (int i = 0; i < size / 2; i++) testB.back();
                
                Page newPageB = new Page("new", "New", "https://new.com");
                long startB = System.nanoTime();
                testB.visit(newPageB);
                totalTimeB += (System.nanoTime() - startB);
                removedCountB = testB.elementsRemoved;
            }

            long avgA = totalTimeA / runs;
            long avgB = totalTimeB / runs;

            System.out.printf("%-10d | %16d ns | %16d ns | %-15d%n", 
                    size, avgA, avgB, removedCountB);
        }
    }
}