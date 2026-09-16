import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// 1. Data Model Class: Page
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[%s: %s (%s) @ %s]", pageId, title, url, visitedTime.format(dtf));
    }
}

// 2. Algorithm A: Two-Stack Method
class TwoStackBrowser {
    private Deque<Page> backStack = new ArrayDeque<>();
    private Deque<Page> forwardStack = new ArrayDeque<>();
    private Page currentPage = null;

    // คำสั่งที่ 1: VISIT page
    public void visit(Page page) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }
        currentPage = page;
        forwardStack.clear(); // ล้าง Forward History ทันที
    }

    // คำสั่งที่ 2: BACK
    public void back() {
        if (backStack.isEmpty()) {
            System.out.println("[Action: BACK] -> ไม่สามารถ Back ได้ (ไม่มีประวัติก่อนหน้า)");
            return;
        }
        forwardStack.push(currentPage);
        currentPage = backStack.pop();
    }

    // คำสั่งที่ 3: FORWARD
    public void forward() {
        if (forwardStack.isEmpty()) {
            System.out.println("[Action: FORWARD] -> ไม่สามารถ Forward ได้ (ไม่มีประวัติถัดไป)");
            return;
        }
        backStack.push(currentPage);
        currentPage = forwardStack.pop();
    }

    // คำสั่งที่ 4: CURRENT
    public Page current() {
        return currentPage;
    }

    // คำสั่งที่ 5: DISPLAY HISTORY
    public void displayHistory(String action) {
        System.out.println("==================================================");
        System.out.println("Action: " + action);
        System.out.println("Current Page   : " + (currentPage != null ? currentPage : "None"));
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

    // คำสั่งที่ 1: VISIT page
    public void visit(Page page) {
        if (currentIndex < historyList.size() - 1) {
            historyList.subList(currentIndex + 1, historyList.size()).clear();
        }
        historyList.add(page);
        currentIndex++;
    }

    // คำสั่งที่ 2: BACK
    public void back() {
        if (currentIndex <= 0) {
            System.out.println("[Action: BACK] -> ไม่สามารถ Back ได้ (ไม่มีประวัติก่อนหน้า)");
            return;
        }
        currentIndex--;
    }

    // คำสั่งที่ 3: FORWARD
    public void forward() {
        if (currentIndex >= historyList.size() - 1) {
            System.out.println("[Action: FORWARD] -> ไม่สามารถ Forward ได้ (ไม่มีประวัติถัดไป)");
            return;
        }
        currentIndex++;
    }

    // คำสั่งที่ 4: CURRENT
    public Page current() {
        if (currentIndex >= 0 && currentIndex < historyList.size()) {
            return historyList.get(currentIndex);
        }
        return null;
    }

    // คำสั่งที่ 5: DISPLAY HISTORY
    public void displayHistory(String action) {
        System.out.println("==================================================");
        System.out.println("Action: " + action);
        System.out.println("Current Page   : " + current());
        List<Page> backList = (currentIndex > 0) ? historyList.subList(0, currentIndex) : Collections.emptyList();
        List<Page> forwardList = (currentIndex >= 0 && currentIndex < historyList.size() - 1) 
                                 ? historyList.subList(currentIndex + 1, historyList.size()) 
                                 : Collections.emptyList();
        System.out.println("Back History   : " + backList);
        System.out.println("Forward History: " + forwardList);
    }
}

// 4. Main Class: ทดสอบชุดคำสั่งบังคับ, Test Cases และ Benchmark
public class BrowserNavigationLab {

    // เมธอดทดสอบชุดคำสั่งบังคับ (VISIT A -> B -> C -> BACK -> BACK -> FORWARD -> VISIT D -> BACK -> FORWARD)
    public static void runMandatorySequence(String title, TwoStackBrowser browserA, ArrayListBrowser browserB) {
        boolean isA = (browserA != null);
        System.out.println("\n**************************************************");
        System.out.println("   ทดสอบชุดคำสั่งบังคับ: " + title);
        System.out.println("**************************************************");

        Page pageA = new Page("1", "Page A", "https://a.com");
        Page pageB = new Page("2", "Page B", "https://b.com");
        Page pageC = new Page("3", "Page C", "https://c.com");
        Page pageD = new Page("4", "Page D", "https://d.com");

        Runnable back = () -> { if (isA) browserA.back(); else browserB.back(); };
        Runnable forward = () -> { if (isA) browserA.forward(); else browserB.forward(); };
        java.util.function.Consumer<Page> visit = (p) -> { if (isA) browserA.visit(p); else browserB.visit(p); };
        java.util.function.Consumer<String> display = (msg) -> { if (isA) browserA.displayHistory(msg); else browserB.displayHistory(msg); };

        visit.accept(pageA); display.accept("VISIT A");
        visit.accept(pageB); display.accept("VISIT B");
        visit.accept(pageC); display.accept("VISIT C");
        back.run();           display.accept("BACK (to B)");
        back.run();           display.accept("BACK (to A)");
        forward.run();        display.accept("FORWARD (to B)");
        visit.accept(pageD); display.accept("VISIT D (ล้าง Forward C)");
        back.run();           display.accept("BACK (to B)");
        forward.run();        display.accept("FORWARD (to D)");
    }

    // เมธอดทดสอบ 8 Test Cases บังคับ
    public static void run8ForcedTestCases(String title, TwoStackBrowser browserA, ArrayListBrowser browserB) {
        boolean isA = (browserA != null);
        System.out.println("\n**************************************************");
        System.out.println("   ทดสอบ 8 TEST CASES บังคับ: " + title);
        System.out.println("**************************************************");

        Page p1 = new Page("1", "Home", "https://home.com");
        Page p2 = new Page("2", "Search", "https://search.com");
        Page p3 = new Page("3", "Detail", "https://detail.com");
        Page p4 = new Page("4", "Checkout", "https://checkout.com");

        Runnable back = () -> { if (isA) browserA.back(); else browserB.back(); };
        Runnable forward = () -> { if (isA) browserA.forward(); else browserB.forward(); };
        java.util.function.Consumer<Page> visit = (p) -> { if (isA) browserA.visit(p); else browserB.visit(p); };
        java.util.function.Consumer<String> display = (msg) -> { if (isA) browserA.displayHistory(msg); else browserB.displayHistory(msg); };

        System.out.println("\n--- [TC1] กด Back เมื่อไม่มีหน้าก่อนหน้า ---");
        back.run();

        System.out.println("\n--- [TC2] กด Forward เมื่อไม่มีหน้าถัดไป ---");
        forward.run();

        System.out.println("\n--- [TC3] เปิดหน้าแรก ---");
        visit.accept(p1);
        display.accept("เปิดหน้าแรก p1");

        System.out.println("\n--- [TC4] เปิดหน้าเดิมซ้ำ ---");
        visit.accept(p1);
        display.accept("เปิดหน้าเดิมซ้ำ p1");

        visit.accept(p2);
        visit.accept(p3);
        display.accept("เปิดหน้า p2 -> p3");

        System.out.println("\n--- [TC5] Back หลายครั้ง ---");
        back.run(); back.run(); back.run(); back.run();
        display.accept("BACK 4 ครั้ง");

        System.out.println("\n--- [TC6] Forward หลายครั้ง ---");
        forward.run(); forward.run(); forward.run(); forward.run();
        display.accept("FORWARD 4 ครั้ง");

        System.out.println("\n--- [TC7] Back แล้วเปิดหน้าใหม่ (ต้องล้าง Forward) ---");
        back.run(); back.run();
        display.accept("BACK 2 ครั้ง");
        visit.accept(p4);
        display.accept("VISIT p4 (Forward ถูกล้าง)");

        System.out.println("\n--- [TC8] ประวัติจำนวนมาก (Stress Test) ---");
        long start = System.currentTimeMillis();
        for (int i = 5; i <= 100_000; i++) {
            visit.accept(new Page(String.valueOf(i), "Page " + i, "https://test.com/" + i));
        }
        for (int i = 0; i < 50_000; i++) {
            back.run();
        }
        long end = System.currentTimeMillis();
        System.out.printf("บันทึก 100,000 รายการ และ Back 50,000 ครั้ง สำเร็จใน: %d ms%n", (end - start));
    }

    public static void main(String[] args) {
        // 1. รันชุดคำสั่งบังคับ (Mandatory Sequence) ทั้งสองอัลกอริทึม
        runMandatorySequence("ALGORITHM A: TWO-STACK", new TwoStackBrowser(), null);
        runMandatorySequence("ALGORITHM B: ARRAYLIST", null, new ArrayListBrowser());

        // 2. รัน 8 Test Cases บังคับ ทั้งสองอัลกอริทึม
        run8ForcedTestCases("ALGORITHM A: TWO-STACK", new TwoStackBrowser(), null);
        run8ForcedTestCases("ALGORITHM B: ARRAYLIST", null, new ArrayListBrowser());

        // 3. การทดลองวัดประสิทธิภาพ (Benchmark) เฉลี่ย 5 รอบตามเกณฑ์
        System.out.println("\n\n*************************************************************************");
        System.out.println("   การทดลองวัดเวลาประสิทธิภาพ (BENCHMARK เฉลี่ย 5 รอบตามเกณฑ์ข้อ 3.8)    ");
        System.out.println("*************************************************************************");

        int[] dataSizes = {1000, 10000, 50000, 100000};
        int rounds = 5;
        System.out.printf("%-12s | %-28s | %-28s%n", "จำนวนหน้า (N)", "Two-Stack เฉลี่ย (ns / ms)", "ArrayList เฉลี่ย (ns / ms)");
        System.out.println("-------------------------------------------------------------------------");

        for (int size : dataSizes) {
            long totalDurationA = 0;
            long totalDurationB = 0;

            for (int r = 0; r < rounds; r++) {
                // Two-Stack Benchmark
                TwoStackBrowser testA = new TwoStackBrowser();
                for (int i = 0; i < size; i++) {
                    testA.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
                }
                for (int i = 0; i < size / 2; i++) {
                    testA.back();
                }
                Page newPage = new Page("new", "New Page", "https://new.com");
                long startA = System.nanoTime();
                testA.visit(newPage);
                totalDurationA += (System.nanoTime() - startA);

                // ArrayList Benchmark
                ArrayListBrowser testB = new ArrayListBrowser();
                for (int i = 0; i < size; i++) {
                    testB.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
                }
                for (int i = 0; i < size / 2; i++) {
                    testB.back();
                }
                long startB = System.nanoTime();
                testB.visit(newPage);
                totalDurationB += (System.nanoTime() - startB);
            }

            long avgA = totalDurationA / rounds;
            long avgB = totalDurationB / rounds;

            System.out.printf("%-12d | %10d ns (%6.3f ms) | %10d ns (%6.3f ms)%n", 
                    size, avgA, avgA / 1_000_000.0, avgB, avgB / 1_000_000.0);
        }
    }
}