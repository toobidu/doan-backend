package org.example.quizizz.service.helper;

import com.corundumstudio.socketio.SocketIOServer;
import org.example.quizizz.service.Interface.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameTimerService {
    
    private final SocketIOServer socketIOServer;
    private final ApplicationEventPublisher eventPublisher;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<Long, GameTimer> activeTimers = new ConcurrentHashMap<>();
    
    public void startGameTimer(Long roomId, int timeLimit) {
        // Cancel any existing timer for this room
        stopGameTimer(roomId);
        
        // Create new timer
        GameTimer timer = new GameTimer(roomId, timeLimit);
        activeTimers.put(roomId, timer);
        
        // Schedule timer ticks
        scheduler.scheduleAtFixedRate(() -> {
            try {
                timer.tick();
                broadcastTimerUpdate(roomId, timer.getRemainingTime());
                
                if (timer.isFinished()) {
                    handleTimerFinished(roomId);
                }
            } catch (Exception e) {
                log.error("Error in game timer for room {}: {}", roomId, e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    public void stopGameTimer(Long roomId) {
        GameTimer timer = activeTimers.remove(roomId);
        if (timer != null) {
            timer.cancel();
        }
    }
    
    private void broadcastTimerUpdate(Long roomId, int remainingTime) {
        try {
            // Gửi sự kiện đếm ngược đến tất cả người chơi trong phòng
            socketIOServer.getRoomOperations("room-" + roomId)
                .sendEvent("countdown-tick", Map.of(
                    "roomId", roomId,
                    "remainingTime", remainingTime,
                    "timestamp", System.currentTimeMillis() // Thêm timestamp để đồng bộ chính xác hơn
                ));
        } catch (Exception e) {
            log.error("Error broadcasting timer update for room {}: {}", roomId, e.getMessage());
        }
    }
    
    /*** Xử lý khi hết thời gian trả lời câu hỏi ***/
    private void handleTimerFinished(Long roomId) {
        stopGameTimer(roomId);
        
        try {
            // Publish event để GameService xử lý next question
            eventPublisher.publishEvent(new GameTimerEvent(this, roomId));
            
            // Broadcast time's up
            socketIOServer.getRoomOperations("room-" + roomId)
                .sendEvent("time-up", Map.of(
                    "roomId", roomId,
                    "timestamp", System.currentTimeMillis()
                ));
        } catch (Exception e) {
            log.error("Error handling timer finished for room {}: {}", roomId, e.getMessage());
        }
    }
    
    private static class GameTimer {
        private final Long roomId;
        private final int totalTime;
        private int remainingTime;
        private boolean cancelled = false;
        
        public GameTimer(Long roomId, int totalTime) {
            this.roomId = roomId;
            this.totalTime = totalTime;
            this.remainingTime = totalTime;
        }
        
        public void tick() {
            if (!cancelled && remainingTime > 0) {
                remainingTime--;
            }
        }
        
        public boolean isFinished() {
            return !cancelled && remainingTime <= 0;
        }
        
        public int getRemainingTime() {
            return remainingTime;
        }
        
        public void cancel() {
            cancelled = true;
        }
    }
}
