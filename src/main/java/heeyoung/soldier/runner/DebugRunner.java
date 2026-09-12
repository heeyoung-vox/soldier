package heeyoung.soldier.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Score;
import heeyoung.soldier.model.ScoreType;

@Component
@ConditionalOnProperty(name = "debug", havingValue = "true")
public class DebugRunner implements ApplicationRunner {
    private final GameWorld gameWorld;
    private static final Logger log = LoggerFactory.getLogger(DebugRunner.class);

    public DebugRunner(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("DebugRunner started (debug mode enabled).");
        
        Score test = new Score(ScoreType.Blue, 0, 0);
        gameWorld.addScore(test);
    }
}
