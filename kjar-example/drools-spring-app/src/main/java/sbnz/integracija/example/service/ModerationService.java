package sbnz.integracija.example.service;

import demo.facts.moderation.BlockEvent;
import demo.facts.moderation.ModerationFlag;
import demo.facts.moderation.ReportEvent;
import demo.facts.moderation.UserInfo;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.UserRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ModerationService {

    @Autowired
    private UserRepository userRepository;

    private KieContainer kieContainer;

    public ModerationService() {
        KieServices ks = KieServices.Factory.get();
        this.kieContainer = ks.newKieContainer(ks.newReleaseId("sbnz.integracija", "drools-spring-kjar", "0.0.1-SNAPSHOT"));
    }

    public List<ModerationFlag> detectSuspiciousBehavior(Long userId, List<ReportEvent> reportEvents, List<BlockEvent> blockEvents) {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        
        try {
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);

            UserInfo userInfo = new UserInfo(userId);
            kieSession.insert(userInfo);

            for (ReportEvent event : reportEvents) {
                kieSession.insert(event);
            }
            
            for (BlockEvent event : blockEvents) {
                kieSession.insert(event);
            }

            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();

            return flags;
        } finally {
            kieSession.dispose();
        }
    }

    public List<ModerationFlag> detectSuspiciousBehaviorWithClock(Long userId, 
                                                                    List<ReportEvent> reportEvents, 
                                                                    List<BlockEvent> blockEvents,
                                                                    Date currentTime) {
        KieSession kieSession = kieContainer.newKieSession("moderationKSession");
        
        try {
            SessionPseudoClock clock = kieSession.getSessionClock();
            
            List<ModerationFlag> flags = new ArrayList<>();
            kieSession.setGlobal("moderationFlags", flags);

            UserInfo userInfo = new UserInfo(userId);
            kieSession.insert(userInfo);

            List<Object> allEvents = new ArrayList<>();
            allEvents.addAll(reportEvents);
            allEvents.addAll(blockEvents);
            allEvents.sort((e1, e2) -> {
                Date t1 = e1 instanceof ReportEvent ? ((ReportEvent) e1).getTimestamp() : ((BlockEvent) e1).getTimestamp();
                Date t2 = e2 instanceof ReportEvent ? ((ReportEvent) e2).getTimestamp() : ((BlockEvent) e2).getTimestamp();
                return t1.compareTo(t2);
            });

            Date lastEventTime = null;
            for (Object event : allEvents) {
                Date eventTime = event instanceof ReportEvent ? 
                    ((ReportEvent) event).getTimestamp() : 
                    ((BlockEvent) event).getTimestamp();
                
                if (lastEventTime != null) {
                    long diff = eventTime.getTime() - lastEventTime.getTime();
                    clock.advanceTime(diff, TimeUnit.MILLISECONDS);
                }
                
                kieSession.insert(event);
                lastEventTime = eventTime;
            }

            if (lastEventTime != null && currentTime != null && currentTime.after(lastEventTime)) {
                long diff = currentTime.getTime() - lastEventTime.getTime();
                clock.advanceTime(diff, TimeUnit.MILLISECONDS);
            }

            kieSession.getAgenda().getAgendaGroup("user-moderation").setFocus();
            kieSession.fireAllRules();

            return flags;
        } finally {
            kieSession.dispose();
        }
    }

    public void applySuspensions(Long userId, List<ModerationFlag> flags) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return;
        }

        User user = userOpt.get();
        Date maxPostingSuspension = user.getPostingSuspendedUntil();
        Date maxLoginSuspension = user.getLoginSuspendedUntil();

        for (ModerationFlag flag : flags) {
            if ("POSTING".equals(flag.getSuspensionType())) {
                if (maxPostingSuspension == null || flag.getSuspendedUntil().after(maxPostingSuspension)) {
                    maxPostingSuspension = flag.getSuspendedUntil();
                }
            } else if ("LOGIN".equals(flag.getSuspensionType())) {
                if (maxLoginSuspension == null || flag.getSuspendedUntil().after(maxLoginSuspension)) {
                    maxLoginSuspension = flag.getSuspendedUntil();
                }
            }
        }

        user.setPostingSuspendedUntil(maxPostingSuspension);
        user.setLoginSuspendedUntil(maxLoginSuspension);
        userRepository.save(user);
    }

    public boolean isPostingSuspended(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        Date suspendedUntil = user.getPostingSuspendedUntil();
        return suspendedUntil != null && suspendedUntil.after(new Date());
    }

    public boolean isLoginSuspended(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        Date suspendedUntil = user.getLoginSuspendedUntil();
        return suspendedUntil != null && suspendedUntil.after(new Date());
    }
}
