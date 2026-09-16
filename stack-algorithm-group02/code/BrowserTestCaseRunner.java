public class BrowserTestCaseRunner {

    public static void runAllTests() {
        System.out.println("==================================================");
        System.out.println("          RUNNING MANDATORY TEST CASES (8/8)      ");
        System.out.println("==================================================");

        // TC01: Back เมื่อไม่มีประวัติ
        TwoStackBrowser b1 = new TwoStackBrowser();
        System.out.print("[TC01] Empty Back: ");
        b1.back();

        // TC02: Forward เมื่อไม่มีประวัติ
        TwoStackBrowser b2 = new TwoStackBrowser();
        System.out.print("[TC02] Empty Forward: ");
        b2.forward();

        // TC03: เปิดหน้าแรก
        TwoStackBrowser b3 = new TwoStackBrowser();
        b3.visit(new Page("1", "Page A", "https://a.com"));
        boolean tc03Pass = b3.getCurrentPage().getTitle().equals("Page A");
        System.out.println("[TC03] Visit First Page: " + (tc03Pass ? "PASSED" : "FAILED"));

        // TC04: เปิดหน้าเดิมซ้ำ
        TwoStackBrowser b4 = new TwoStackBrowser();
        b4.visit(new Page("1", "Page A", "https://a.com"));
        b4.visit(new Page("1", "Page A", "https://a.com"));
        boolean tc04Pass = b4.getCurrentPage().getTitle().equals("Page A") && b4.pushCount == 1;
        System.out.println("[TC04] Visit Duplicate Page: " + (tc04Pass ? "PASSED" : "FAILED"));

        // TC05: Back หลายครั้ง
        TwoStackBrowser b5 = new TwoStackBrowser();
        b5.visit(new Page("1", "A", "a.com"));
        b5.visit(new Page("2", "B", "b.com"));
        b5.visit(new Page("3", "C", "c.com"));
        b5.back();
        b5.back();
        boolean tc05Pass = b5.getCurrentPage().getTitle().equals("A");
        System.out.println("[TC05] Multiple Back: " + (tc05Pass ? "PASSED" : "FAILED"));

        // TC06: Forward หลายครั้ง
        b5.forward();
        b5.forward();
        boolean tc06Pass = b5.getCurrentPage().getTitle().equals("C");
        System.out.println("[TC06] Multiple Forward: " + (tc06Pass ? "PASSED" : "FAILED"));

        // TC07: Back แล้วเปิดหน้าใหม่ (Forward ต้องถูกล้าง)
        b5.back(); // กลับมา B
        b5.visit(new Page("4", "D", "d.com"));
        // ลอง Forward ดูว่า C หายไปจริงไหม
        System.out.print("[TC07] Back then Visit (Clear Forward): ");
        b5.forward(); // ต้องขึ้นเตือนว่าไม่มีประวัติถัดไป

        // TC08: ประวัติจำนวนมาก (100,000 รายการ)
        System.out.print("[TC08] Large History (100k items): ");
        TwoStackBrowser b8 = new TwoStackBrowser();
        for (int i = 0; i < 100000; i++) {
            b8.visit(new Page(String.valueOf(i), "P" + i, "url"));
        }
        for (int i = 0; i < 50000; i++) {
            b8.back();
        }
        b8.visit(new Page("new", "NewPage", "new.com"));
        boolean tc08Pass = b8.getCurrentPage().getTitle().equals("NewPage");
        System.out.println(tc08Pass ? "PASSED (Large history handled correctly)" : "FAILED");
    }

    public static void main(String[] args) {
        runAllTests();
    }
}