package session;


import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private final Map<Long, UserSession> sessions = new HashMap<>();

    public UserSession getSession(long userId) {
        return sessions.computeIfAbsent(userId, UserSession::new);
    }

    public void removeSession(long userId) {
        sessions.remove(userId);
    }

    public boolean hasActiveQuiz(long userId) {
        UserSession session = sessions.get(userId);
        return session != null && session.isInQuiz();
    }

    public Map<Long, UserSession> getAllSessions() {
        return sessions;
    }
}
