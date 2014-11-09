
package com.pacoworks.reactiveinputs;

import static com.pacoworks.reactiveinputs.KEY_WRAPPER.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Paco on 09/11/2014. See LICENSE.md
 */
@Slf4j
public class ReactiveInputs {
    private final PublishSubject<Integer> moves;

    @Getter
    @Setter
    @Accessors(prefix = "m")
    private int mFramesPerSecond;

    @Getter
    @Setter
    @Accessors(prefix = "m")
    private int mLeniencyFrames;

    public ReactiveInputs() {
        moves = PublishSubject.<Integer> create();
        mFramesPerSecond = 60;
        mLeniencyFrames = 4;
        int windowDurationMs = 1000 / mFramesPerSecond;
        moves.doOnNext(move -> log.error("{}", move)).throttleFirst(windowDurationMs,
                TimeUnit.MILLISECONDS);
    }

    public void subscribeMove(final IKnownMove move) {
        int windowDurationMs = 1000 / mFramesPerSecond;
        moves.buffer(windowDurationMs * (mLeniencyFrames + move.getInputSequence().size()),
                windowDurationMs, TimeUnit.MILLISECONDS, Schedulers.computation()).map(results -> {
            if (results.size() < move.getInputSequence().size()) {
                return new ArrayList<>();
            }
            ArrayList<Integer> inputs = new ArrayList<>();
            for (int i = results.size() - move.getInputSequence().size(); i < results.size(); i++) {
                inputs.add(results.get(i));
            }
            return inputs;
        }).filter(windowMoves -> {
            List<Integer> inputSequence = move.getInputSequence();
            if (windowMoves.size() != inputSequence.size()) {
                return false;
            }
            for (int i = 0; i < windowMoves.size(); i++) {
                if (windowMoves.get(i) != inputSequence.get(i)) {
                    return false;
                }
            }
            return true;
        }).subscribe(message -> log.debug("{} - {}", move.getMoveName(), message));
    }

    public void sendMove(int input) {
        log.debug("{}", input);
        moves.onNext(input);
    }

    @Data
    @Accessors(prefix = "m")
    public static class SingleInput {
        private final int mKeyCode;

        public SingleInput(int keyCode) {
            this.mKeyCode = keyCode;
        }

        public SingleInput(@NonNull KEY_WRAPPER keyCodeWrap) {
            this.mKeyCode = keyCodeWrap.ordinal();
        }
    }

    @ToString
    public static class Hadouken implements IKnownMove {
        @Getter
        private List<Integer> inputSequence = Arrays.asList(KEY_DOWN.ordinal(),
                KEY_RIGHT.ordinal(), KEY_ONE.ordinal());

        @Getter
        private String moveName = "Hadouken";
    }

    @ToString
    public static class Shoryuken implements IKnownMove {
        @Getter
        private List<Integer> inputSequence = Arrays.asList(KEY_DOWN.ordinal(),
                KEY_RIGHT.ordinal(), KEY_ONE.ordinal());

        @Getter
        private String moveName = "Shoryuken";
    }
}
