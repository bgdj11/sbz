package sbnz.integracija.example;

import demo.facts.moderation.BlockEvent;
import demo.facts.moderation.ModerationFlag;
import demo.facts.moderation.ReportEvent;
import demo.facts.moderation.UserInfo;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ModerationRulesTest {

    private KieContainer kieContainer;

    public ModerationRulesTest() {
        KieServices ks = KieServices.Factory.get();
        this.kieContainer = ks.newKieContainer(
            ks.newReleaseId("sbnz.integracija", "drools-spring-kjar", "0.0.1-SNAPSHOT")
        );
    }

    @Test
    public void testRule1_MoreThan5ReportsIn24Hours_ShouldSuspendPosting24h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 1L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 6; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Spam");
                kieSession.insert(event);
                clock.advanceTime(3, TimeUnit.HOURS); // 3h između svake prijave
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            System.out.println("=== DEBUG: Broj aktiviranih pravila: " + rulesFired);
            System.out.println("=== DEBUG: Broj flagova: " + flags.size());
            for (ModerationFlag f : flags) {
                System.out.println("=== DEBUG: Flag reason: '" + f.getReason() + "'");
            }
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            assertFalse("Treba da postoji bar jedna moderation flag", flags.isEmpty());
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("24h") && f.getReason().contains("prijava"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za vise od 5 prijava u 24h", flag);
            assertEquals("Tip suspenzije treba da bude POSTING", "POSTING", flag.getSuspensionType());
            assertEquals("User ID treba da se poklapa", userId, flag.getUserId());
            
            System.out.println("Test 1 prošao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule1_Exactly5ReportsIn24Hours_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 1L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 5; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Spam");
                kieSession.insert(event);
                clock.advanceTime(3, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("24h") && f.getReason().contains("prijava"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa 5 prijava pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 1 (negative) prosao: Pravilo se nije aktiviralo sa 5 prijava");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule2_MoreThan8ReportsIn48Hours_ShouldSuspendPosting48h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 2L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 9; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Inappropriate content");
                kieSession.insert(event);
                clock.advanceTime(5, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("48h") && f.getReason().contains("prijava"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za vise od 8 prijava u 48h", flag);
            assertEquals("Tip suspenzije treba da bude POSTING", "POSTING", flag.getSuspensionType());
            
            System.out.println("Test 2 prosao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule2_Exactly8ReportsIn48Hours_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 2L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 8; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Content");
                kieSession.insert(event);
                clock.advanceTime(5, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("48h") && f.getReason().contains("prijava"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa 8 prijava pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 2 (negative) prosao: Pravilo se nije aktiviralo sa 8 prijava");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule3_MoreThan4BlocksIn24Hours_ShouldSuspendPosting24h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 3L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 5; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(4, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("24h") && f.getReason().contains("blokiranja"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za vise od 4 blokiranja u 24h", flag);
            assertEquals("Tip suspenzije treba da bude POSTING", "POSTING", flag.getSuspensionType());
            
            System.out.println("Test 3 prosao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule3_Exactly4BlocksIn24Hours_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 3L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 4; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(4, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("24h") && f.getReason().contains("blokiranja"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa 4 blokiranja pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 3 (negative) prosao: Pravilo se nije aktiviralo sa 4 blokiranja");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule4_CombinedBlocksAndReports_ShouldSuspendLogin48h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 4L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 3; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(15, TimeUnit.HOURS);
            }

            for (int i = 0; i < 5; i++) {
                ReportEvent event = new ReportEvent(200L + i, userId, 300L + i, "Harassment");
                kieSession.insert(event);
                clock.advanceTime(4, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("logovanja"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za kombinovano pravilo", flag);
            assertEquals("Tip suspenzije treba da bude LOGIN", "LOGIN", flag.getSuspensionType());
            assertTrue("Razlog treba da sadrzi oba uslova", 
                flag.getReason().contains("blokiranja") && flag.getReason().contains("prijave"));
            
            System.out.println("Test 4 prosao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule4_OnlyBlocksNoReports_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 4L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 3; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(15, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("logovanja") && f.getReason().contains("48h"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa samo blokiranjima pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 4 (negative) prošao: Pravilo se nije aktiviralo samo sa blokiranjima");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule5_MoreThan3BlocksIn6Hours_ShouldSuspendPosting12h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 5L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 4; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(1, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("6h") && f.getReason().contains("eskalacija"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za brzu eskalaciju", flag);
            assertEquals("Tip suspenzije treba da bude POSTING", "POSTING", flag.getSuspensionType());
            assertTrue("Razlog treba da sadrzi 'brza eskalacija'", 
                flag.getReason().contains("brza eskalacija") || flag.getReason().contains("eskalacija"));
            
            System.out.println("Test 5 prosao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule5_Exactly3BlocksIn6Hours_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 5L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 3; i++) {
                BlockEvent event = new BlockEvent(100L + i, userId);
                kieSession.insert(event);
                clock.advanceTime(1, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("6h") && f.getReason().contains("eskalacija"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa 3 blokiranja pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 5 (negative) prosao: Pravilo se nije aktiviralo sa 3 blokiranja");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule6_MoreThan12ReportsIn7Days_ShouldSuspendLogin72h() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 6L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);
            
            for (int i = 0; i < 13; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Chronic violation");
                kieSession.insert(event);
                clock.advanceTime(12, TimeUnit.HOURS); // 12h između prijava
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira", rulesFired > 0);
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("7 dana") || f.getReason().contains("7dana"))
                .findFirst()
                .orElse(null);
            
            assertNotNull("Treba da postoji flag za hronicno problematicno ponasanje", flag);
            assertEquals("Tip suspenzije treba da bude LOGIN", "LOGIN", flag.getSuspensionType());
            
            System.out.println("Test 6 prosao: " + flag.getReason());
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testRule6_Exactly12ReportsIn7Days_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 6L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 12; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Violation");
                kieSession.insert(event);
                clock.advanceTime(12, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("7 dana") || f.getReason().contains("7dana"))
                .findFirst()
                .orElse(null);
            
            assertNull("Sa 12 prijava pravilo ne bi trebalo da se aktivira", flag);
            
            System.out.println("Test 6 (negative) prosao: Pravilo se nije aktiviralo sa 12 prijava");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testWindowExpiry_OldEventsOutsideWindow_ShouldNotTrigger() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 7L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 3; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Spam");
                kieSession.insert(event);
                clock.advanceTime(4, TimeUnit.HOURS);
            }

            clock.advanceTime(25, TimeUnit.HOURS);

            for (int i = 3; i < 5; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Spam");
                kieSession.insert(event);
                clock.advanceTime(1, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();
            
            ModerationFlag flag = flags.stream()
                .filter(f -> f.getReason().contains("24h") && f.getReason().contains("prijava"))
                .findFirst()
                .orElse(null);
            
            assertNull("Prijave van vremenskog prozora ne bi trebalo da aktiviraju pravilo", flag);
            
            System.out.println("Test Window Expiry prosao: Stari eventi ne aktiviraju pravilo");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testMultipleRules_SeveralConditionsMet_ShouldTriggerMultipleRules() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 8L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 10; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Multiple violations");
                kieSession.insert(event);
                clock.advanceTime(4, TimeUnit.HOURS); // Ukupno 40h
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Vise pravila bi trebalo da se aktivira", rulesFired >= 2);
            assertTrue("Treba da postoje vise flagova", flags.size() >= 2);
            
            boolean hasRule1 = flags.stream().anyMatch(f -> f.getReason().contains("24h") && f.getReason().contains("prijava"));
            boolean hasRule2 = flags.stream().anyMatch(f -> f.getReason().contains("48h") && f.getReason().contains("prijava"));
            
            assertTrue("Treba da se aktivira pravilo za 5+ prijava u 24h", hasRule1);
            assertTrue("Treba da se aktivira pravilo za 8+ prijava u 48h", hasRule2);
            
            System.out.println("Test Multiple Rules prosao: Vise pravila se aktiviralo");
            for (ModerationFlag flag : flags) {
                System.out.println("  - " + flag.getReason());
            }
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testEdgeCase_ExactlyThresholdPlusOne() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 9L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);

            for (int i = 0; i < 6; i++) {
                ReportEvent event = new ReportEvent(100L + i, userId, 200L + i, "Edge case test");
                kieSession.insert(event);
                clock.advanceTime(3, TimeUnit.HOURS);
            }
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertTrue("Pravilo bi trebalo da se aktivira sa threshold + 1", rulesFired > 0);
            assertFalse("Treba da postoji flag", flags.isEmpty());
            
            System.out.println("Test Edge Case prosao: Pravilo radi sa tacno threshold + 1");
            
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testNoEvents_ShouldNotTriggerAnyRules() {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);
            
            Long userId = 10L;
            UserInfo user = new UserInfo(userId);
            kieSession.insert(user);
            
            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            int rulesFired = kieSession.fireAllRules();
            
            assertEquals("Nijedan rule ne bi trebalo da se aktivira", 0, rulesFired);
            assertTrue("Ne treba da postoje flagovi", flags.isEmpty());
            
            System.out.println("Test No Events prosao: Bez dogadjaja nema aktiviranja pravila");
            
        } finally {
            kieSession.dispose();
        }
    }
}
