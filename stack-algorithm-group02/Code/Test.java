public class Test {
    public static void runForcedTestCases(String algoName, TwoStackBrowser browserA, ArrayListBrowser browserB) {
        boolean isA = (browserA != null);
        System.out.println("\n**************************************************");
        System.out.println("   เริ่มรัน TEST CASES บังคับ: " + algoName);
        System.out.println("**************************************************");

        Page p1 = new Page("1", "Home", "https://home.com");
        Page p2 = new Page("2", "Search", "https://search.com");
        Page p3 = new Page("3", "Detail", "https://detail.com");
        Page p4 = new Page("4", "Checkout", "https://checkout.com");

        Runnable back = () -> { if (isA) browserA.back(); else browserB.back(); };
        Runnable forward = () -> { if (isA) browserA.forward(); else browserB.forward(); };
        java.util.function.Consumer<Page> visit = (p) -> { if (isA) browserA.visit(p); else browserB.visit(p); };
        java.util.function.Consumer<String> display = (msg) -> { if (isA) browserA.displayState(msg); else browserB.displayState(msg); };

        // Test 1: กด Back เมื่อไม่มีหน้าก่อนหน้า
        System.out.println("\n--- [Test 1] กด Back เมื่อไม่มีประวัติ ---");
        back.run();

        // Test 2: กด Forward เมื่อไม่มีหน้าถัดไป
        System.out.println("\n--- [Test 2] กด Forward เมื่อไม่มีประวัติถัดไป ---");
        forward.run();

        // Test 3: เปิดหน้าแรก
        System.out.println("\n--- [Test 3] เปิดหน้าแรก ---");
        visit.accept(p1);
        display.accept("VISIT Page 1");

        // Test 4: เปิดหน้าเดิมซ้ำ
        System.out.println("\n--- [Test 4] เปิดหน้าเดิมซ้ำ (Page 1 ซ้ำ) ---");
        visit.accept(p1);
        display.accept("VISIT Page 1 (Again)");

        visit.accept(p2);
        visit.accept(p3);
        display.accept("VISIT Page 2 -> Page 3");

        // Test 5: Back หลายครั้ง
        System.out.println("\n--- [Test 5] Back หลายครั้งจนสุด ---");
        back.run();
        back.run();
        back.run();
        back.run();
        display.accept("BACK 4 ครั้ง");

        // Test 6: Forward หลายครั้ง
        System.out.println("\n--- [Test 6] Forward หลายครั้งจนสุด ---");
        forward.run();
        forward.run();
        forward.run();
        forward.run();
        display.accept("FORWARD 4 ครั้ง");

        // Test 7: Back แล้วเปิดหน้าใหม่
        System.out.println("\n--- [Test 7] Back 2 ครั้งแล้วเปิดหน้าใหม่ (Forward ต้องถูกล้าง) ---");
        back.run();
        back.run();
        display.accept("BACK 2 ครั้งก่อนเปิดหน้าใหม่");
        visit.accept(p4);
        display.accept("VISIT Page 4");

        // Test 8: ประวัติจำนวนมาก
        System.out.println("\n--- [Test 8] ทดสอบประวัติจำนวนมาก (100,000 หน้า) ---");
        long start = System.currentTimeMillis();
        for (int i = 5; i <= 100_000; i++) {
            visit.accept(new Page(String.valueOf(i), "Page " + i, "https://test.com/" + i));
        }
        for (int i = 0; i < 50_000; i++) {
            back.run();
        }
        long end = System.currentTimeMillis();
        System.out.printf("บันทึก 100,000 รายการ และ Back 50,000 ครั้ง สำเร็จในเวลา: %d ms%n", (end - start));
    }

    public static void main(String[] args) {
        // 1. รัน 8 Test Cases บังคับสำหรับ Two-Stack
        runForcedTestCases("ALGORITHM A: TWO-STACK", new TwoStackBrowser(), null);

        // 2. รัน 8 Test Cases บังคับสำหรับ ArrayList
        runForcedTestCases("ALGORITHM B: ARRAYLIST", null, new ArrayListBrowser());

        // 3. รันการวัดเวลา Benchmark ต่อท้าย
        System.out.println("\n\n**************************************************");
        System.out.println("        การทดลองวัดเวลาประสิทธิภาพ (BENCHMARK)        ");
        System.out.println("**************************************************");

        int[] dataSizes = {1000, 10000, 50000, 100000};
        System.out.printf("%-12s | %-25s | %-25s%n", "จำนวนหน้า (N)", "Two-Stack (ns / ms)", "ArrayList (ns / ms)");
        System.out.println("------------------------------------------------------------------");

        for (int size : dataSizes) {
            TwoStackBrowser testA = new TwoStackBrowser();
            for (int i = 0; i < size; i++) {
                testA.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
            }
            for (int i = 0; i < size / 2; i++) {
                testA.back();
            }
            Page newPage = new Page("new", "New Page", "https://new.com");
            long startTimeA = System.nanoTime();
            testA.visit(newPage);
            long durationA = System.nanoTime() - startTimeA;

            ArrayListBrowser testB = new ArrayListBrowser();
            for (int i = 0; i < size; i++) {
                testB.visit(new Page(String.valueOf(i), "Title " + i, "url" + i));
            }
            for (int i = 0; i < size / 2; i++) {
                testB.back();
            }
            long startTimeB = System.nanoTime();
            testB.visit(newPage);
            long durationB = System.nanoTime() - startTimeB;

            System.out.printf("%-12d | %10d ns (%6.3f ms) | %10d ns (%6.3f ms)%n",
                    size, durationA, durationA / 1_000_000.0, durationB, durationB / 1_000_000.0);
        }
    }
}